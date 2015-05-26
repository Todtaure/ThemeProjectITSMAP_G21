package com.example.chronos.themeprojectitsmap_201270746.Service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
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
import com.example.chronos.themeprojectitsmap_201270746.Database.ReminderContract;
import com.example.chronos.themeprojectitsmap_201270746.Database.ReminderDbHelper;
import com.example.chronos.themeprojectitsmap_201270746.MainMenuActivity;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Breuer on 19-05-2015.
 */

//Created with code snippets from http://developer.android.com/guide/components/services.html#ExtendingIntentService

public class ReminderService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private int snoozeInterval;
    private ActivityDataSource dataSource;
    private AlarmManager alarmManager;
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

            //TODO: change to different method, since expensive
            Constants.BroadcastMethods method = Constants.BroadcastMethods.values()[intent.getIntExtra(Constants.BroadcastParams.BROADCAST_METHOD, 0)];

            switch(method)
            {
                case SNOOZE : {
                    setAlarm(snoozeInterval, Constants.BroadcastMethods.ALARM_WAKEUP);
                    //Toast.makeText(getBaseContext(), String.valueOf(snoozeInterval), Toast.LENGTH_LONG).show();
                    break;
                }

                case ALARM_WAKEUP: {
                    Toast.makeText(getBaseContext(), String.valueOf(snoozeInterval), Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    };

    private final class ServiceHandler extends Handler
    {
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
            Log.d(Constants.Debug.LOG_TAG, "ServiceHandler.handleMessage");
            serviceId = msg.arg1;

            Bundle bundle = msg.getData();

            if(bundle.getBoolean(Constants.Debug.IS_DEBUG, false))
            {
                runTestDatabase();
                stopSelf(serviceId);
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
