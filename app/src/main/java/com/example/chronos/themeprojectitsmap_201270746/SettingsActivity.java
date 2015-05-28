package com.example.chronos.themeprojectitsmap_201270746;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.chronos.themeprojectitsmap_201270746.Database.ActivityDataSource;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

import java.sql.SQLException;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private ActivityDataSource dataSource = null;
    long activityId;
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        try {
            dataSource = new ActivityDataSource(getBaseContext());
        } catch (SQLException ex) {
            Toast.makeText(getBaseContext(), Constants.Messages.ERR_DB_CONNECTION, Toast.LENGTH_LONG).show();
            return;
        }
        // In the simplified UI, fragments are not used at all and we instead

        addPreferencesFromResource(R.xml.pref_name);

        // Add 'general' preferences.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_general);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'dnd' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_dnd);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_dnd);

        // Add 'nightmode' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_night_mode);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_nightmode);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_buttons);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_buttons);

        Intent intent = getIntent();
        activityId = intent.getLongExtra(Constants.ACTIVITY_ID, -1);

        if (activityId != -1) {

            dataSource.open();
            final ActivityModel activity = dataSource.getActivityById(activityId);
            dataSource.close();

            if (activity == null) {
                Toast.makeText(getBaseContext(), Constants.Messages.ERR_DB_READ, Toast.LENGTH_LONG).show();
                activityId = -1;
                finish();
                return;
            }
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = settings.edit();

            editor.putString("activity_name", activity.getName());
            editor.putString("activity_duration", String.valueOf(activity.getMinTimeInterval()));
            editor.putBoolean("pref_key_gps_status", !activity.getGpsData().isEmpty());

            String nightMode = activity.getNightMode();

            if (nightMode != null) {
                if(!nightMode.equals("")) {
                    editor.putString("nightPrefA_Key", nightMode.split(",")[0]);
                    editor.putString("nightPrefB_Key", nightMode.split(",")[1]);
                }
            }
            editor.apply();
        }
        else
        {
            Toast.makeText(getBaseContext(), "No Activity attached.", Toast.LENGTH_LONG).show();
        }

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("activity_name"));
        bindPreferenceSummaryToValue(findPreference("activity_duration"));
        bindPreferenceSummaryToValue(findPreference("activity_locations"));
        bindPreferenceSummaryToValue(findPreference("nightPrefA_Key"));
        bindPreferenceSummaryToValue(findPreference("nightPrefB_Key"));

        Preference buttonSave = (Preference)findPreference(getString(R.string.pref_key_delete));
        buttonSave.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if(preference instanceof Preference)
                {
                    ActivityDataSource dataSource;
                    try {
                        dataSource = new ActivityDataSource(preference.getContext());
                    } catch (SQLException ex) {
                        Toast.makeText(preference.getContext(), Constants.Messages.ERR_DB_CONNECTION, Toast.LENGTH_LONG).show();
                        return false;
                    }
                    if(preference.getKey() == getString(R.string.pref_key_delete))
                    {
                        new AlertDialog.Builder(SettingsActivity.this)
                                .setIcon(R.mipmap.ic_alert)
                                .setTitle("Delete Activity")
                                .setMessage("Are you sure you want to delete the activity?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityDataSource deleteSource;
                                        try {
                                            deleteSource = new ActivityDataSource(getBaseContext());
                                        } catch (SQLException ex) {
                                            Toast.makeText(getBaseContext(), Constants.Messages.ERR_DB_CONNECTION, Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        deleteSource.open();
                                        deleteSource.deleteActivityById(activityId);
                                        deleteSource.close();
                                        sendBroadcastToService(Constants.BroadcastMethods.ACTIVITY_UPDATED);
                                        activityId = -1;
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                }
                return false;
            };
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(Constants.Debug.LOG_TAG, "SettingsActivity.onStop");

        if(activityId == -1)
        {
            return;
        }

        dataSource.open();
        ActivityModel activity = dataSource.getActivityById(activityId);

        if(activity == null)
        {
            return;
        }

        Preference preference = (Preference)findPreference(getString(R.string.pref_key_activity_name));
        activity.setName(preference.getSummary().toString());

        preference = (Preference)findPreference(getString(R.string.pref_key_activity_duration));

        try {
            activity.setMinTimeInterval(Integer.parseInt(preference.getSummary().toString()));
        }
        catch(NumberFormatException ex)
        {
            activity.setMinTimeInterval(60);
        }
        preference = (Preference)findPreference(getString(R.string.pref_key_night_from));
        if(preference.getSummary() != null || !preference.getSummary().equals(""))
        {
            String nightMode = preference.getSummary().toString();
            preference = (Preference)findPreference(getString(R.string.pref_key_night_to));
            nightMode += "," + preference.getSummary().toString();
            activity.setNightMode(nightMode);
        }

        activity.setNightMode("");

        dataSource.updateActivity(activity);
        dataSource.close();

        sendBroadcastToService(Constants.BroadcastMethods.ACTIVITY_UPDATED);
    }

    private void sendBroadcastToService(Constants.BroadcastMethods type)
    {
        Intent intent = new Intent(Constants.Service.SERVICE_BROADCAST);
        intent.putExtra(Constants.BroadcastParams.BROADCAST_METHOD, type.ordinal());
        intent.putExtra(Constants.ACTIVITY_ID, activityId);
        sendBroadcast(intent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

     /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            else if(preference instanceof com.example.chronos.themeprojectitsmap_201270746.Utilities.TimePreference)
            {
                switch(preference.getKey()) {
                    case "nightPrefA_Key":
                    {
                        String nightA = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("nightPrefA_Key", "");
                        preference.setSummary(nightA);
                        break;
                    }
                    case "nightPrefB_Key":
                    {
                        String nightB = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("nightPrefB_Key", "");
                        preference.setSummary(nightB);
                        break;
                    }
                }
            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("activity_name"));
            bindPreferenceSummaryToValue(findPreference("activity_duration"));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_dnd);
        }
    }
}
