package com.example.chronos.themeprojectitsmap_201270746;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
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


public class MainMenuActivity extends Activity {

    Point p;
    private TimePicker tp;
    private Switch offSwitch;
    private Integer snoozeHour;
    private Integer snoozeMinute;
    private ActivityDataSource dataSource;
    private ArrayList<ActivityModel> activities;
    private ActivityListAdapter activityAdapter;
    private ListView activityList;
    private long listItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button btn_show = (Button) findViewById(R.id.snoozeButton);
        activityList = (ListView)findViewById(R.id.activityList);

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //Open popup window
                if (p != null)
                    showPopup(MainMenuActivity.this, p);
            }
        });

        offSwitch = (Switch) findViewById(R.id.offSwitch);

        offSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isOn) {

                if(isOn && listItemId > 0) {
                    offSwitch.setText("On");

                    Intent intent = new Intent(getApplicationContext(), ReminderService.class);
                    intent.putExtra(Constants.ACTIVITY_ID,listItemId);
                    startService(intent);
                }
                else {
                    offSwitch.setText("Off");
                    onDestroy();
                }
            }
        });

        try {
            dataSource = new ActivityDataSource(getBaseContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemId = id;
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();


        //ActivityModel mainActivityModel = new ActivityModel();
        dataSource.open();
//        ActivityModel testActivity = new ActivityModel();
//
//        testActivity.setName("Test Aktivitet");
//        testActivity.setMinTimeInterval(30);
//        testActivity.setMaxReminders(3);
//
//        boolean success = dataSource.insertActivity(testActivity);
//
//        if (!success)
//        {
//            Log.d(Constants.Messages.ERR_DB_INSERT, "No Success!");
//            dataSource.close();
//            return;
//        }

        activities = new ArrayList<>();
        activities = dataSource.getAllActivities();

        dataSource.close();

        setActivityList();
    }

    public void setActivityList()
    {
        activityAdapter = new ActivityListAdapter(getApplicationContext(), R.layout.activity_list, activities);

        // Here, you set the data in your ListView
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

    public void addActivityBtn(View view)
    {
        Intent wizardIntent = new Intent(this, WizardActivity.class);
        startActivity(wizardIntent);
    }

    public void editBtn(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(Constants.ACTIVITY_ID, activities.get(0).getId());
        startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    // The method that displays the popup.
    private void showPopup(final Activity context, Point p) {
        // Inflate the popup_layout.xml
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popup_layout_main_snooze, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(layout, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT,true);
        popup.setContentView(layout);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.BLUE));

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, 0, 10, 0);

        tp = (TimePicker)layout.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

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

                Intent snoozeIntent = new Intent(Constants.Service.SERVICE_BROADCAST);
                snoozeIntent.putExtra(Constants.BroadcastParams.BROADCAST_METHOD,Constants.BroadcastMethods.SNOOZE.ordinal());
                snoozeIntent.putExtra(Constants.BroadcastParams.SNOOZE_INTERVAL, snoozeInterval);
                sendBroadcast(snoozeIntent);

                TextView textViewMinute = (TextView)layout.findViewById(R.id.textViewMinute);
                textViewMinute.setText(snoozeMinute.toString());

                TextView textViewHour = (TextView)layout.findViewById(R.id.textViewHour);
                textViewHour.setText(snoozeHour.toString());
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(this,ReminderService.class));
    }
}


