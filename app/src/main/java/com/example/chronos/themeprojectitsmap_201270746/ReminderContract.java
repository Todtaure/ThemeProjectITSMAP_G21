package com.example.chronos.themeprojectitsmap_201270746;

import android.provider.BaseColumns;

/**
 * Created by Chronos on 19/05/2015.
 */
public class ReminderContract {
    public ReminderContract()
    {}

    public static abstract class ActivityTable implements BaseColumns
    {
        public static final String TABLE_NAME = "activity";
        public static final String COLUMN_NAME_SETTING_ID = "activityId";
        public static final String COLUMN_NAME_ACTIVITYNAME = "name";
        public static final String COLUMN_NAME_ISSNOOZE = "isSnooze";
        public static final String COLUMN_NAME_ISOFF = "isOff";
        public static final String COLUMN_NAME_OFFINTERVALS = "offIntervals";
        public static final String COLUMN_NAME_MINTIMEINTERVALS = "minTimeIntervals";
        public static final String COLUMN_NAME_MAXREMINDERS = "maxReminders";
        public static final String COLUMN_NAME_REMINDERCOUNTER = "reminderCounter";
        public static final String COLUMN_NAME_ISFIRSTTIMESETUP = "isFirstTimeSetup";
        public static final String COLUMN_NAME_GPSCOORDINATES= "gpsCoordinates";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ActivityTable.TABLE_NAME + " (" +
                    ActivityTable._ID + " INTEGER PRIMARY KEY," +
                    ActivityTable.COLUMN_NAME_ACTIVITYNAME + TEXT_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_ISSNOOZE + INTEGER_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_ISOFF + INTEGER_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_OFFINTERVALS + TEXT_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_MINTIMEINTERVALS + INTEGER_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_MAXREMINDERS + INTEGER_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_REMINDERCOUNTER + INTEGER_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_ISFIRSTTIMESETUP + INTEGER_TYPE + COMMA_SEP +
                    ActivityTable.COLUMN_NAME_GPSCOORDINATES + TEXT_TYPE + COMMA_SEP +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ActivityTable.TABLE_NAME;
}
