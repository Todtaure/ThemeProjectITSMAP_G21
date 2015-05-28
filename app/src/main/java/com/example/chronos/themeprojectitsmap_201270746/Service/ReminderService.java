package com.example.chronos.themeprojectitsmap_201270746.Service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Database.ActivityDataSource;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.GPSModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.OffIntervalsModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.ReminderDbHelper;
import com.example.chronos.themeprojectitsmap_201270746.R;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by Breuer on 19-05-2015.
 */

//Created with code snippets from http://developer.android.com/guide/components/services.html#ExtendingIntentService

public class ReminderService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private ActivityDataSource dataSource;
    private ActivityModel currentActivity;
    private AlarmManager alarmManager;
    private CalendarInfo calendarInfo;
    private boolean activitiesResetted = false;
    private int serviceId;


    @Override
    public IBinder onBind(Intent intent)
    {
        //Handle bind when app has new information (settings changed etc.)
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d(Constants.Debug.LOG_TAG, "ReminderService.onCreate");

        try {
            dataSource = new ActivityDataSource(getBaseContext());
        }
        catch(SQLException ex)
        {
            Toast.makeText(getBaseContext(), Constants.Messages.ERR_DB_CONNECTION, Toast.LENGTH_LONG).show();
        }
        alarmManager = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        calendarInfo = new CalendarInfo();

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(Constants.Debug.LOG_TAG, "ReminderService.onStartCommand");

        registerReceiver(ServiceReceiver, new IntentFilter(Constants.Service.SERVICE_BROADCAST));
        long activityId = intent.getLongExtra(Constants.ACTIVITY_ID, -1);
        if(activityId == -1)
        {
            Toast.makeText(getBaseContext(), "No active Activity.", Toast.LENGTH_LONG).show();
            stopSelf();
        }

        dataSource.open();
        currentActivity = dataSource.getActivityById(activityId);
        if(currentActivity == null)
        {
            Toast.makeText(getBaseContext(), "Activity could not be retrieved.", Toast.LENGTH_LONG).show();
            dataSource.close();
            stopSelf();
        }
        currentActivity.setIsOff(false);
        dataSource.updateActivity(currentActivity);
        dataSource.close();

        //Set timer for reset of activity counters
        setResetTimer();

        //TODO remove after debug!
        boolean success = isDNDOrNightMode();
        resetAllActivityCounters();
        notifyUser();

        Message msg = mServiceHandler.obtainMessage();
        serviceId = startId;

        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.Debug.IS_DEBUG, false);

        msg.arg1 = startId;
        msg.setData(bundle);
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(Constants.Debug.LOG_TAG, "ReminderService.onDestroy");

        Intent updateServiceIntent = new Intent(Constants.Service.SERVICE_BROADCAST);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(this, 0, updateServiceIntent, 0);
        alarmManager.cancel(pendingUpdateIntent);

        unregisterReceiver(ServiceReceiver);
    }

    private void setAlarm(int minutes, Constants.BroadcastMethods type)
    {
        Intent intent =  new Intent(Constants.Service.SERVICE_BROADCAST);
        intent.putExtra(Constants.BroadcastParams.BROADCAST_METHOD, type.ordinal());

        PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, minutes * 60 * 1000, pintent);
    }

    private BroadcastReceiver ServiceReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            int snoozeInterval = intent.getIntExtra(Constants.BroadcastParams.SNOOZE_INTERVAL, 0);
            long activityId = intent.getLongExtra(Constants.ACTIVITY_ID, -1);
            Log.d(Constants.Debug.LOG_TAG, "ServiceBroadcastReceiver");
            //TODO: change to different method, since expensive
            Constants.BroadcastMethods method = Constants.BroadcastMethods.values()[intent.getIntExtra(Constants.BroadcastParams.BROADCAST_METHOD, 0)];

            switch(method)
            {
                case DEFAULT_NONE:
                {
                    break;
                }
                case SNOOZE : {
                    setAlarm(snoozeInterval, Constants.BroadcastMethods.ALARM_WAKEUP);
                    currentActivity.setIsSnooze(true);
                    break;
                }
                case ALARM_WAKEUP: {
                    currentActivity.setIsSnooze(false);
                    break;
                }
                case ALARM_SERVICE_CHECK:
                {
                    if(currentActivity.getIsSnooze())
                    {
                        break;
                    }

                    if(!isDNDOrNightMode() || !currentActivity.getDone()) {
                        if(checkCalendar())
                        {
                            activitiesResetted = false;
                            break;
                        }
                    }
                    setAlarm(Constants.Service.UPDATE_INTERVAL_VAL, Constants.BroadcastMethods.ALARM_SERVICE_CHECK);
                    setResetTimer();
                    break;
                }
                case ALARM_NOTIFICATION:
                {
                    if(currentActivity.getIsSnooze() || currentActivity.getDone())
                    {
                        break;
                    }
                    notifyUser();
                    setAlarm(Constants.Service.UPDATE_INTERVAL_VAL, Constants.BroadcastMethods.ALARM_SERVICE_CHECK);
                    break;
                }
                case ACTIVITY_UPDATED:
                {
                    isThisActivityChanged(activityId);
                }
                case ACTIVITY_STATE_CHANGE:
                {
                    setNewActivity(activityId);
                    break;
                }
                case ACTIVITY_SNOOZED:
                {
                    incrementActivityReminder();
                    setAlarm(Constants.Service.UPDATE_INTERVAL_VAL, Constants.BroadcastMethods.ALARM_SERVICE_CHECK);
                    break;
                }
                case ACTIVITY_DONE:
                {
                    if(!currentActivity.getIsSnooze())
                    {
                        setActivityDone();
                    }
                    break;
                }
                case SERVICE_STOP:
                {
                    stopSelf();
                    break;
                }
                case SERVICE_RESET_ACTIVITIES:
                {
                    resetAllActivityCounters();
                    break;
                }
            }
        }
    };

    private void setNewActivity(long activityId) {
        dataSource.open();
        ActivityModel updatedActivity = dataSource.getActivityById(activityId);
        dataSource.close();

        if(updatedActivity == null)
        {
            return;
        }

        currentActivity = updatedActivity;
        resetService();
    }

    private void isThisActivityChanged(long activityId) {
        if(activityId != currentActivity.getId())
        {
            return;
        }

        dataSource.open();
        ActivityModel updatedActivity = dataSource.getActivityById(activityId);
        dataSource.close();

        if(updatedActivity == null)
        {
            stopSelf();
        }

        currentActivity = updatedActivity;
        resetService();
    }

    private void resetService() {
        Intent updateServiceIntent = new Intent(Constants.Service.SERVICE_BROADCAST);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(this, 0, updateServiceIntent, 0);
        alarmManager.cancel(pendingUpdateIntent);

        if(!checkCalendar())
        {
            setAlarm(Constants.Service.UPDATE_INTERVAL_VAL, Constants.BroadcastMethods.ALARM_SERVICE_CHECK);
        }
    }

    private void setActivityDone() {
        dataSource.open();
        currentActivity.setDone(true);
        dataSource.updateActivity(currentActivity);
        dataSource.close();
    }

    private void incrementActivityReminder() {
        dataSource.open();
        currentActivity.incrementReminderCounter();
        if(currentActivity.getMaxReminders() <= currentActivity.getReminderCounter())
        {
            currentActivity.setIsSnooze(true);
        }

        dataSource.updateActivity(currentActivity);

        dataSource.close();
    }

    private void notifyUser() {
        if(currentActivity.getIsSnooze() || currentActivity.getIsOff())
        {
            return;
        }

        Log.d(Constants.Debug.LOG_TAG, "ReminderService.notifyUser");

        Intent snoozeIntent = new Intent(Constants.Service.SERVICE_BROADCAST);
        snoozeIntent.putExtra(Constants.BroadcastParams.BROADCAST_METHOD, Constants.BroadcastMethods.ACTIVITY_SNOOZED.ordinal());
        PendingIntent snoozePending = PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(Constants.Service.SERVICE_BROADCAST);
        deleteIntent.putExtra(Constants.BroadcastParams.BROADCAST_METHOD, Constants.BroadcastMethods.ACTIVITY_DONE.ordinal());
        PendingIntent deletePending = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n  = new Notification.Builder(this)
                .setContentTitle("Activity Reminder")
                .setContentText("There is time for your Activity!")
                .setSmallIcon(R.drawable.ic_stat_smiley)
                .setAutoCancel(false)
                .addAction(R.drawable.ic_stat_bell, "Snooze", snoozePending)
                .setDeleteIntent(deletePending)
                .setPriority(BIND_IMPORTANT)
                .build();

        n.defaults |= Notification.DEFAULT_SOUND;
        n.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    private void setResetTimer() {
        String[] ids = TimeZone.getAvailableIDs(1 * 60 * 60 * 1000);
        // create a Pacific Standard Time time zone
        SimpleTimeZone pdt = new SimpleTimeZone(1 * 60 * 60 * 1000, ids[0]);

        // set up rules for daylight savings time
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

        Calendar calendar = new GregorianCalendar(pdt);
        int currentHour = calendar.get(Calendar.HOUR);
        int currentMinute = calendar.get(Calendar.MINUTE);

        int timeTillMidnight = (24-currentHour)*60 - currentMinute;

        if(timeTillMidnight <=  Constants.Service.UPDATE_INTERVAL_VAL) {
            setAlarm(timeTillMidnight, Constants.BroadcastMethods.SERVICE_RESET_ACTIVITIES);
        }
    }

    private boolean isDNDOrNightMode() {
        String[] ids = TimeZone.getAvailableIDs(1 * 60 * 60 * 1000);

        SimpleTimeZone pdt = new SimpleTimeZone(1 * 60 * 60 * 1000, ids[0]);

        // set up rules for daylight savings time
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

        Calendar startDate = new GregorianCalendar(pdt);
        Calendar endDate = new GregorianCalendar(pdt);
        Date now;

        try {
            now = new SimpleDateFormat("HH:mm").parse(String.valueOf(startDate.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(startDate.get(Calendar.MINUTE)));
        }
        catch(ParseException ex)
        {
            return false;
        }

        String nightMode = currentActivity.getNightMode();
        String startTime, endTime;

        if(nightMode != null || nightMode.equals(""))
        {
            startTime = nightMode.split(",")[0];
            endTime  = nightMode.split(",")[1];

            Date startSimpleTime, endSimpleTime;
            try {

                startSimpleTime = new SimpleDateFormat("HH:mm").parse(startTime);
                startDate.setTime(startSimpleTime);

                endSimpleTime = new SimpleDateFormat("HH:mm").parse(endTime);
                endDate.setTime(endSimpleTime);

                if(Integer.parseInt(startTime.split(":")[0]) > Integer.parseInt(endTime.split(":")[0]))
                {
                    endDate.add(Calendar.DATE, 1);
                }
            }
            catch(ParseException ex)
            {
                return false;
            }

            if(now.after(startDate.getTime()) && now.before(endDate.getTime()))
            {
                if(!activitiesResetted)
                {
                    resetAllActivityCounters();
                }

                return true;
            }
        }

        List<OffIntervalsModel> items = currentActivity.getOffIntervals();
        for(OffIntervalsModel item : items)
        {
            startTime = item.getOffInterval().split(",")[0];
            endTime = item.getOffInterval().split(",")[1];

            Date startSimpleTime, endSimpleTime;
            try {
                startSimpleTime = new SimpleDateFormat("HH:mm").parse(startTime);
                startDate.setTime(startSimpleTime);

                endSimpleTime = new SimpleDateFormat("HH:mm").parse(endTime);
                endDate.setTime(endSimpleTime);

                if(Integer.parseInt(startTime.split(":")[0]) > Integer.parseInt(endTime.split(":")[0]))
                {
                    endDate.add(Calendar.DATE, 1);
                }
            }
            catch(ParseException ex)
            {
                return false;
            }

            if(now.after(startDate.getTime()) && now.before(endDate.getTime()))
            {
                return true;
            }
        }

        return false;
    }

    private boolean checkCalendar() {
        int timeTillNextInterval = -1;

        timeTillNextInterval = calendarInfo.getTimeInMinToNextFreeTimeSlot(this, currentActivity.getMinTimeInterval());

        if(timeTillNextInterval > Constants.Service.UPDATE_INTERVAL_VAL)
        {
            return false;
        }

        setAlarm(timeTillNextInterval, Constants.BroadcastMethods.ALARM_NOTIFICATION);
        return true;
    }

    private void resetAllActivityCounters() {
        dataSource.open();

        List<ActivityModel> activityList = dataSource.getAllActivities();

        for(ActivityModel activity : activityList)
        {
            activity.setIsSnooze(false);
            activity.setReminderCounter(0);
            activity.setIsOff(true);
            if(activity.getId() == currentActivity.getId())
            {
                activity.setIsOff(false);
                currentActivity = activity;
            }
            dataSource.updateActivity(activity);
        }

        dataSource.close();
        activitiesResetted = true;
    }

    private final class ServiceHandler extends Handler {
        private ActivityDataSource dataSource;
        private int serviceId;
        private AlarmManager alarmManager;

        public ServiceHandler(Looper looper) {
            super(looper);
            try {
                dataSource = new ActivityDataSource(getBaseContext());
            }
            catch(SQLException ex)
            {
                Toast.makeText(getBaseContext(), Constants.Messages.ERR_DB_CONNECTION, Toast.LENGTH_LONG).show();
            }
            alarmManager = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        }

        @Override
        public void handleMessage(Message msg)
        {
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            serviceId = msg.arg1;

            Bundle bundle = msg.getData();

            if(bundle.getBoolean(Constants.Debug.IS_DEBUG, false))
            {
                runTestDatabase();
            }
        }

        private void runTestDatabase() {

            Log.d(Constants.Debug.LOG_TAG, "Starting Database test.");
            getBaseContext().deleteDatabase(ReminderDbHelper.DATABASE_NAME);

            boolean success = false;
            dataSource.open();

            ActivityModel testActivity = new ActivityModel();
            GPSModel testGPSData = new GPSModel();
            OffIntervalsModel testOffInterval = new OffIntervalsModel();

            testActivity.setName("Test Aktivitet");
            testActivity.setMinTimeInterval(30);
            testActivity.setMaxReminders(3);

            testGPSData.setCoordinates("NUMBERS");
            testGPSData.setName("Test GPS");

            testOffInterval.setOffInterval("Sometime");

            testActivity.addGpsData(testGPSData);
            testActivity.addOffInterval(testOffInterval);

            success = dataSource.insertActivity(testActivity);

            if (!success)
            {
                Log.d(Constants.Messages.ERR_DB_INSERT, "No Success!");
                dataSource.close();
                return;
            }

            Log.d(Constants.Debug.LOG_TAG, "Inserted Test Activity.");

            ArrayList<ActivityModel> allValues = dataSource.getAllActivities();

            if(allValues.isEmpty())
            {
                Log.d(Constants.Messages.ERR_DB_READ, "No Success!");
                dataSource.close();
                return;
            }

            boolean nameSuccess = allValues.get(0).getName().equals("Test Aktivitet");
            boolean gpsSuccess = !allValues.get(0).getGpsData().isEmpty();
            boolean offIntervalSuccess = !allValues.get(0).getOffIntervals().isEmpty();

            if(!nameSuccess || !gpsSuccess || !offIntervalSuccess)
            {
                Log.d(Constants.Messages.ERR_DB_READ, "Data incorrect! Name: " + allValues.get(0).getName() + " - GPS: " + gpsSuccess + " - Interval: " + offIntervalSuccess);
                dataSource.close();
                return;
            }

            Log.d(Constants.Debug.LOG_TAG, "Retrieved all Activities.");

            success = dataSource.deleteActivityById(allValues.get(0).getId());

            if(!success)
            {
                Log.d(Constants.Messages.ERR_DB_DELETE, "No Success!");
                dataSource.close();
                return;
            }

            Log.d(Constants.Debug.LOG_TAG, "Deleted Test Activity.");

            dataSource.close();
            getBaseContext().deleteDatabase(ReminderDbHelper.DATABASE_NAME);
        }
    }
}
