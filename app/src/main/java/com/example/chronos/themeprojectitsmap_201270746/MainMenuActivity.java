package com.example.chronos.themeprojectitsmap_201270746;

/* When developing the part with the popup following references has been used:
https://androidresearch.wordpress.com/2012/05/06/how-to-create-popups-in-android/
http://stackoverflow.com/questions/7498605/how-to-create-a-popup-window-in-android
http://mrbool.com/how-to-implement-popup-window-in-android/28285
http://developer.android.com/reference/android/widget/PopupWindow.html
*/

/* Icons throughout the app have been found on:
http://materialdesignicons.com/
*/

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Database.ActivityDataSource;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.GPSModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.OffIntervalsModel;
import com.example.chronos.themeprojectitsmap_201270746.Service.ReminderService;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;
import com.example.chronos.themeprojectitsmap_201270746.Wizard.WizardActivity;

import org.apache.http.conn.ConnectionKeepAliveStrategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainMenuActivity extends Activity implements ServiceInterface {
    private TimePicker tp;
    private Switch offSwitch;
    private Integer snoozeHour;
    private Integer snoozeMinute;
    private ActivityDataSource dataSource;
    private ArrayList<ActivityModel> activities;
    private ActivityListAdapter activityAdapter;
    private ListView activityList;
    private long listItemId = -1;
    private boolean serviceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button btn_show = (Button) findViewById(R.id.snoozeButton);
        activityList = (ListView) findViewById(R.id.activityList);

        bindService(new Intent(this, ReminderService.class), mConn, Context.BIND_AUTO_CREATE);

        try {
            dataSource = new ActivityDataSource(getBaseContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Opens popup window
                showPopup(MainMenuActivity.this);
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serviceRunning = sharedPreferences.getBoolean(Constants.Service.SERVICE_RUNNING, false);

        offSwitch = (Switch) findViewById(R.id.offSwitch);

        offSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isOn) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isOn && listItemId >= 0) {
                    offSwitch.setText("On");
                    if (serviceRunning) {
                        return;
                    }
                    sendToService(listItemId, Constants.Service.ACTIVITY_STATE_CHANGE);

                    editor.putBoolean(Constants.Service.SERVICE_RUNNING, true);
                    editor.apply();
                } else {
                    CheckBox checkBox = (CheckBox) findViewById(R.id.listItemCheckbox);
                    checkBox.setChecked(false);
                    offSwitch.setText("Off");

                    editor.putBoolean(Constants.Service.SERVICE_RUNNING, false);
                    editor.apply();

                    onAppOffBtn();
                }
            }

        });

        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemId = activities.get(position).getId();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onPostResume();
        if(!mServiceConnected) {
            bindService(new Intent(this, ReminderService.class), mConn, Context.BIND_AUTO_CREATE);
        }
        // Opens dataSource and gets all activities, then closes dataSource
        dataSource.open();
        activities = new ArrayList<>();
        activities = dataSource.getAllActivities();
        dataSource.close();

        listItemId = -1;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.getBoolean(Constants.Service.SERVICE_RUNNING, false);

        setActivityList();

//        CheckBox listItemCheckbox = (CheckBox) findViewById(R.id.listItemCheckbox);
//
//        listItemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    sendToService(listItemId, Constants.Service.ACTIVITY_STATE_CHANGE);
//
//                } else {
//                    sendToService(listItemId, Constants.Service.SERVICE_STOP);
//                }
//            }
//        });
    }


    public void setActivityList()
    {

        activityAdapter = new ActivityListAdapter(getApplicationContext(), R.layout.activity_list, activities, this );

        // Set adapter with activityAdapter
        activityList.setAdapter(activityAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Starts wizardActivity
    public void addActivityBtn(View view)
    {
        Intent wizardIntent = new Intent(this, WizardActivity.class);
        startActivity(wizardIntent);
    }

    // Starts settingsActivity if a list item is selected
    public void editBtn(View view)
    {
        if(listItemId == -1)
        {
            return;
        }
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(Constants.ACTIVITY_ID, listItemId);
        startActivity(intent);
    }

    private void showPopup(final Activity context) {
        // Inflate popup_layout_main_snooze
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popup_layout_main_snooze, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(layout, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT,true);
        popup.setContentView(layout);
        popup.setFocusable(true);

        // Display popup
        popup.showAtLocation(layout, 0, 10, 0);

        tp = (TimePicker)layout.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        // Gets a reference to the close button and closes view when clicked
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        // Gets a reference to the save button, saves time interval and closes view when clicked
        Button saveBtn = (Button) layout.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                snoozeHour = tp.getCurrentHour();
                snoozeMinute = tp.getCurrentMinute();

                Calendar currentTime = Calendar.getInstance();
                long offset = currentTime.get(Calendar.ZONE_OFFSET) +  currentTime.get(Calendar.DST_OFFSET);
                String sinceMidnight = Long.toString((currentTime.getTimeInMillis() + offset) %  (24 * 60 * 60 * 1000));
                int timeSinceMidnight = Integer.parseInt(sinceMidnight);

                int snoozeInterval = (snoozeHour*60 + snoozeMinute) - (timeSinceMidnight/1000)/60;

                sendToService(listItemId, Constants.Service.SNOOZE_APP, snoozeInterval);

                popup.dismiss();
            }
        });
    }

    public void onAppOffBtn() {
        stopService(new Intent(this, ReminderService.class));
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (mServiceConnected) {

            unbindService(mConn);
            mServiceConnected = false;
        }
    }

    /**
     * Sends message with text stored in bundle extra data ("data" key).
    */

    public void sendToService(long activityId, int messageType)
    {
        sendToService(activityId, messageType, 0);
    }

    public void sendToService(long activityId, int messageType, int snoozeInterval ) {
        if (mServiceConnected) {
            Message msg = Message.obtain(null, messageType);

            Bundle b = new Bundle();
            b.putLong(Constants.ACTIVITY_ID, activityId);
            b.putInt(Constants.BroadcastParams.SNOOZE_INTERVAL, snoozeInterval);
            msg.setData(b);

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                // We always have to trap RemoteException
                // (DeadObjectException
                // is thrown if the target Handler no longer exists)

                e.printStackTrace();
            }
        } else {
            Log.d(Constants.Debug.LOG_TAG, "Cannot send - not connected to service.");
        }

    }


    Messenger mService = null;
    boolean mServiceConnected = false;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(Constants.Debug.LOG_TAG, "Connected to service.");

            mService = new Messenger(service);
            mServiceConnected = true;
        }

        /**
         * Connection dropped.
         */
        @Override
        public void onServiceDisconnected(ComponentName className) {

            Log.d(Constants.Debug.LOG_TAG, "Disconnected from service.");
            mService = null;
            mServiceConnected = false;
        }
    };
}
