package com.example.chronos.themeprojectitsmap_201270746.Service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Database.ReminderDbHelper;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

/**
 * Created by Breuer on 19-05-2015.
 */

//Created with code snippets from http://developer.android.com/guide/components/services.html#ExtendingIntentService

public class ReminderService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            Log.d(Constants.Debug.LOG_TAG, "ServiceHandler.handleMessage");

            //stopSelf(msg.arg1);
        }

    }

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
        HandlerThread thread = new HandlerThread(Constants.Service.SERVICE_HANDLER, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(Constants.Debug.LOG_TAG, "ReminderService.onStartCommand");

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Service.UPDATE_INTERVAL_KEY, Constants.Service.UPDATE_INTERVAL_VAL);

        msg.setData(bundle);
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(Constants.Debug.LOG_TAG, "ReminderService.onDestroy");
        super.onDestroy();
        //Quit thread with looper.quit() ?
    }

    public void handleReminderTimeout()
    {
        //Handle alarm timeout and notification
    }
}
