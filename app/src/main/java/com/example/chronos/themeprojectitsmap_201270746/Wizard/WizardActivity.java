package com.example.chronos.themeprojectitsmap_201270746.Wizard;
// Design inspiration fra https://plus.google.com/+RomanNurik/posts/6cVymZvn3f4
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Database.ActivityDataSource;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.OffIntervalsModel;
import com.example.chronos.themeprojectitsmap_201270746.MainMenuActivity;
import com.example.chronos.themeprojectitsmap_201270746.R;
import com.example.chronos.themeprojectitsmap_201270746.Service.CalendarInfo;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WizardActivity extends ActionBarActivity {



    // The pager widget, which handles animation and allows swiping horizontally to access previous
    // and next wizard steps.
    private ViewPager mPager;

    // The pager adapter, which provides the pages to the view pager widget.
    private PagerAdapter mPagerAdapter;

    private List<Fragment> mPageSequence;

    private ProgressBar mprogressBar;
    private Button mNextButton;
    private Button mPrevButton;
    int nextButtonDefaultTextColor;

    private int currentPage;
    private int currentMaxPage;
    private List<TimeSlot> notDisturbTimeSlotList;

    private boolean isLastPage = false;

    public WizardData activityObject;
    private ActivityDataSource activityDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        getSupportActionBar().hide();

        // Create object for wizard user information (singleton) used by wizard fragments
        activityObject = new WizardData();

        try{
            activityDataSource = new ActivityDataSource(this);
        }
        catch ( SQLException e){
            Toast.makeText(getBaseContext(), Constants.Messages.ERR_DB_CONNECTION, Toast.LENGTH_LONG).show();
        }



        // Create List structure for not disturb time slots in activityObject
        notDisturbTimeSlotList = new ArrayList<>();
        for(int i=1; i<4; i++){
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.isSet = false;
            notDisturbTimeSlotList.add(timeSlot);
        }
        activityObject.notDisturbTimeSlots = notDisturbTimeSlotList;

        // Create night mode object for activityObject
        TimeSlot nightModeTimeSlot = new TimeSlot();
        nightModeTimeSlot.isSet = false;
        activityObject.nightModeTimeSlot = nightModeTimeSlot;

        // Create geoLock object for activityObject
        GeoLocation geoLock = new GeoLocation();
        geoLock.isSet = false;
        activityObject.GeoLock = geoLock;

        // Adds fragments to a List used by WizardPagerAdapter(ViewPager)
        mPageSequence = new ArrayList<>();
        mPageSequence.add(new WizardActivityFragment());
        mPageSequence.add(new WizardNightModeFragment());
        mPageSequence.add(new WizardDontDisturbFragment());
        mPageSequence.add(new WizardGpsFragment());
        mPageSequence.add(new WizardReviewFragment());
        currentMaxPage = mPageSequence.size();

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new WizardPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        mprogressBar.setMax(mPageSequence.size());

        // Sets progressbar to start value
        currentPage = 1;
        updateProgressbar(currentPage);
        // Set buttons to start value
        updateButtons(currentPage);

        // Next page(next fragment)
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLastPage != true) {
                    mPager.setCurrentItem(++currentPage - 1);
                } else {
                    // Save activity in DB
                    ActivityModel activityModel = new ActivityModel();
                    activityModel.setName(activityObject.getActivityName());
                    activityModel.setMinTimeInterval(activityObject.getActivityDuration());
                    List<OffIntervalsModel> offIntervalsModels = new ArrayList<>();
                    if (activityObject.nightModeTimeSlot.isSet){
                        String startTimeoffInterval = getTimeFormat(activityObject.nightModeTimeSlot.startTime.get(Calendar.HOUR_OF_DAY),activityObject.nightModeTimeSlot.startTime.get(Calendar.MINUTE));
                        String endTimeoffInterval = getTimeFormat(activityObject.nightModeTimeSlot.endTime.get(Calendar.HOUR_OF_DAY),activityObject.nightModeTimeSlot.endTime.get(Calendar.MINUTE));
                        activityModel.setNightMode(startTimeoffInterval + "," + endTimeoffInterval);                    }
                    for (TimeSlot timeSlot : activityObject.notDisturbTimeSlots) {
                        if (timeSlot.isSet){
                            OffIntervalsModel offIntervalsModel = new OffIntervalsModel();
                            String startTimeoffInterval = getTimeFormat(timeSlot.startTime.get(Calendar.HOUR_OF_DAY),timeSlot.startTime.get(Calendar.MINUTE));
                            String endTimeoffInterval = getTimeFormat(timeSlot.endTime.get(Calendar.HOUR_OF_DAY),timeSlot.endTime.get(Calendar.MINUTE));
                            offIntervalsModel.setOffInterval(startTimeoffInterval + "," + endTimeoffInterval);
                            offIntervalsModels.add(offIntervalsModel);
                        }
                    }
                    if (offIntervalsModels.size() != 0) {
                        activityModel.setOffIntervals(offIntervalsModels);
                    }
                    activityDataSource.open();

                    if (activityDataSource.insertActivity(activityModel)) {
                        Toast.makeText(getBaseContext(), "The activity has been saved",
                                Toast.LENGTH_SHORT).show();
                    }
                    activityDataSource.close();

                    finish();
                }

            }
        });

        // Prev page(previous fragment)
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(--currentPage -1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wizard, menu);
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

    // Called by fragment which contains required fields. ViewPagers max page is set to current page to prevent user from swiping to next page
    public void notifyFragmentHasRequiredFields() {
        showNextButton(false);
        currentMaxPage = currentPage;
        mPagerAdapter.notifyDataSetChanged();
    }

    // Called by fragment when required fields has been filled out
    public void notifyRequiredFieldsFilled(){
        if (currentPage < mPageSequence.size()){
            showNextButton(true);
            currentMaxPage = mPageSequence.size();
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    private void showNextButton(boolean show){
        mNextButton.setEnabled(show);
    }

    private void showPrevButton(boolean show){
        mPrevButton.setEnabled(show);
    }

    private void changeNextButtonToSaveButton(boolean toSaveButton){


        if (toSaveButton) {
            nextButtonDefaultTextColor = mNextButton.getCurrentTextColor();
            mNextButton.setText("Save");
            mNextButton.setBackgroundColor(Color.parseColor("#ff669900"));
            mNextButton.setTextColor(Color.WHITE);
            showNextButton(true);
        } else {
            mNextButton.setTextColor(nextButtonDefaultTextColor);
            mNextButton.setBackgroundResource(R.drawable.next_button_background);
            mNextButton.setText("Next");

        }
    }

    // Update progressbar when page change
    private void updateProgressbar(int pageNumber){
        mprogressBar.setProgress(pageNumber);
    }

    private void updateButtons(int pageNumber){
        if (pageNumber == currentMaxPage) {
            showNextButton(false);
        } else {
            showNextButton(true);
        }
        if (pageNumber == 1){
            showPrevButton(false);
        } else {
            showPrevButton(true);
        }
        if (isLastPage){
            showNextButton(true);
        }
    }

    // Used by fragments to get the right format for time used for EditText label
    public String getTimeFormat(int aHour, int aMinute){
        String hour;
        String minute;
        if (aHour < 10) {
            hour = "0" + aHour;
        } else {
            hour = Integer.toString(aHour);
        }
        if (aMinute < 10) {
            minute = "0" + aMinute;
        } else {
            minute = Integer.toString(aMinute);
        }
        return hour + ":" + minute;
    }

    private class WizardPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        public WizardPagerAdapter(FragmentManager fm) {
            super(fm);
            mPager.setOnPageChangeListener(this);
        }

        @Override
        public Fragment getItem(int position) {
            return mPageSequence.get(position);
        }

        @Override
        public int getCount() {
            return currentMaxPage;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position + 1;
            updateProgressbar(currentPage);
            if (currentPage == mPageSequence.size()){
                isLastPage = true;
                changeNextButtonToSaveButton(isLastPage);
            } else if(currentPage != mPageSequence.size() && isLastPage) {
                isLastPage = false;
                changeNextButtonToSaveButton(isLastPage);
            }
            updateButtons(currentPage);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
