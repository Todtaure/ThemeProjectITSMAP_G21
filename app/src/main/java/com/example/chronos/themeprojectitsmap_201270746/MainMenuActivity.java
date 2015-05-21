package com.example.chronos.themeprojectitsmap_201270746;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Service.ReminderService;

import java.util.Date;


public class MainMenuActivity extends Activity {

    Point p;
    private TimePicker tp;
    private Switch offSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button btn_show = (Button) findViewById(R.id.snoozeButton);

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //Open popup window
                if (p != null)
                    showPopup(MainMenuActivity.this, p);
            }
        });

        offSwitch = (Switch) findViewById(R.id.offSwitch);
        offSwitch.setChecked(false);

        offSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isOff) {

                if(isOff) {
                    offSwitch.setText("On");
                    onDestroy();
                }
                else {
                    offSwitch.setText("Off");
                }
            }
        });
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
        startService(new Intent(getApplicationContext(), ReminderService.class));
    }

    public void editBtn(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
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
                Integer tmpHour = tp.getCurrentHour();
                Integer tmpMinute = tp.getCurrentMinute();

                TextView textViewMinute = (TextView)layout.findViewById(R.id.textViewMinute);
                textViewMinute.setText(tmpMinute.toString());

                TextView textViewHour = (TextView)layout.findViewById(R.id.textViewHour);
                textViewHour.setText(tmpHour.toString());
            }
        }) ;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(this,ReminderService.class));
    }
}


