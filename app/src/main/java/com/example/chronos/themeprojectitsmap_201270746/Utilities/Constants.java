package com.example.chronos.themeprojectitsmap_201270746.Utilities;

import java.util.Dictionary;
import java.util.Enumeration;

/**
 * Created by Chronos on 20/05/2015.
 */
public final class Constants {

    public static final String ACTIVITY_ID = "ACTIVITY_ID";

    public static abstract class Service
    {
        public static final String UPDATE_INTERVAL_KEY = "UPDATE_INTERVAL";
        public static final int UPDATE_INTERVAL_VAL = 3600;

        public static final String SERVICE_HANDLER = "ReminderServiceHandler";

        public static final String SERVICE_BROADCAST = "reminder-service-event";
    }

    public static abstract class Messages
    {
        public static final String ERR_DB_UPDATE = "Database update failed.";
        public static final String ERR_DB_INSERT = "Database insert failed.";
        public static final String ERR_DB_DELETE = "Database delete failed.";
        public static final String ERR_DB_READ = "Database read failed.";
        public static final String ERR_DB_CONNECTION = "Database connection failed.";
    }

    public static abstract class Debug
    {
        public static final String LOG_TAG = "WHERE AM I?";
        public static final String IS_DEBUG = "IS_DEBUG";
    }

    public static abstract class BroadcastParams
    {
        public static final String BROADCAST_METHOD = "BROADCAST_METHOD";

        // Manuel snooze interval from mainMenuActivity
        public static final String SNOOZE_INTERVAL = "snooze_interval";
    }

    public static enum BroadcastMethods
    {
        SNOOZE,
        ALARM_WAKEUP,
        ALARM_SERVICE_CHECK,
        ALARM_NOTIFICATION,
        ACTIVITY_UPDATED,
        ACTIVITY_STATE_CHANGE
    }


}
