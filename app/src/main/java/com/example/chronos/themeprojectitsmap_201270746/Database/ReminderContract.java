package com.example.chronos.themeprojectitsmap_201270746.Database;
import android.provider.BaseColumns;

/**
 * Created by Chronos on 19/05/2015.
 */
public final class ReminderContract {
    public ReminderContract()
    {}

    public static abstract class ActivityTable implements BaseColumns
    {
        public static final String TABLE_NAME = "activity";
        //public static final String COLUMN_NAME_ACTIVITY_ID = "activityId";
        public static final String COLUMN_NAME_ACTIVITYNAME = "name";
        public static final String COLUMN_NAME_ISSNOOZE = "isSnooze";
        public static final String COLUMN_NAME_ISOFF = "isOff";
        public static final String COLUMN_NAME_MINTIMEINTERVAL = "minTimeInterval";
        public static final String COLUMN_NAME_MAXREMINDERS = "maxReminders";
        public static final String COLUMN_NAME_REMINDERCOUNTER = "reminderCounter";
        public static final String COLUMN_NAME_DONE = "done";
    }

    public static abstract class GPSTable implements BaseColumns
    {
        public static final String TABLE_NAME = "gpsCoordinates";
        //public static final String COLUMN_NAME_GPSCOORDINATES_ID = "gpsCoordinatesId";
        public static final String COLUMN_NAME_FRIENDLYNAME = "friendlyName";
        public static final String COLUMN_NAME_ACTIVITY_FK = "activityFK";
        public static final String COLUMN_NAME_GPSCOORDINATES= "gpsCoordinates";
    }

    public static abstract class OffIntervalsTable implements BaseColumns
    {
        public static final String TABLE_NAME = "offInterval";
        //public static final String COLUMN_NAME_OFFINTERVAL_ID = "offIntervalId";
        public static final String COLUMN_NAME_ACTIVITY_FK = "activityFK";
        public static final String COLUMN_NAME_OFFINTERVAL = "offInterval";
    }

}

