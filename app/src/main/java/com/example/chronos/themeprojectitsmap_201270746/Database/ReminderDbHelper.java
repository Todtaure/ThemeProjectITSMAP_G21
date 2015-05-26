package com.example.chronos.themeprojectitsmap_201270746.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;
//Taken from: http://developer.android.com/training/basics/data-storage/databases.html

public class ReminderDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NOTNULL = " NOT NULL";

    private static final String SQL_CREATE_ENTRIES_ACTIVITY =
            "CREATE TABLE " + ReminderContract.ActivityTable.TABLE_NAME + " (" +
                    ReminderContract.ActivityTable._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ReminderContract.ActivityTable.COLUMN_NAME_ACTIVITYNAME + TEXT_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.ActivityTable.COLUMN_NAME_ISSNOOZE + INTEGER_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.ActivityTable.COLUMN_NAME_ISOFF + INTEGER_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.ActivityTable.COLUMN_NAME_MINTIMEINTERVAL + INTEGER_TYPE + NOTNULL  + COMMA_SEP +
                    ReminderContract.ActivityTable.COLUMN_NAME_MAXREMINDERS + INTEGER_TYPE + COMMA_SEP +
                    ReminderContract.ActivityTable.COLUMN_NAME_REMINDERCOUNTER + INTEGER_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.ActivityTable.COLUMN_NAME_DONE + INTEGER_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.ActivityTable.COLUMN_NAME_NIGHTMODE + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_ENTRIES_GPS =
            "CREATE TABLE " + ReminderContract.GPSTable.TABLE_NAME + " (" +
                    ReminderContract.GPSTable._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ReminderContract.GPSTable.COLUMN_NAME_FRIENDLYNAME + TEXT_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.GPSTable.COLUMN_NAME_GEOLOC + TEXT_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.GPSTable.COLUMN_NAME_ACTIVITY_FK + INTEGER_TYPE +
                    " REFERENCES " + ReminderContract.ActivityTable.TABLE_NAME + "("+ReminderContract.ActivityTable._ID + ")" + " ON DELETE CASCADE" +
                    " )";
    private static final String SQL_CREATE_ENTRIES_OFFINTERVALS =
            "CREATE TABLE " + ReminderContract.OffIntervalsTable.TABLE_NAME + " (" +
                    ReminderContract.OffIntervalsTable._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ReminderContract.OffIntervalsTable.COLUMN_NAME_OFFINTERVAL + TEXT_TYPE + NOTNULL + COMMA_SEP +
                    ReminderContract.OffIntervalsTable.COLUMN_NAME_ACTIVITY_FK + INTEGER_TYPE +
                    " REFERENCES " + ReminderContract.ActivityTable.TABLE_NAME + "(" + ReminderContract.ActivityTable._ID + ")" + " ON DELETE CASCADE" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ReminderContract.GPSTable.TABLE_NAME + ";" +
            "DROP TABLE IF EXISTS " + ReminderContract.OffIntervalsTable.TABLE_NAME + ";" +
            "DROP TABLE IF EXISTS " + ReminderContract.ActivityTable.TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 15;
    public static final String DATABASE_NAME = "ReminderApp.db";

    public ReminderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(Constants.Debug.LOG_TAG, "ReminderDbHelper.Constructor");
    }
    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.Debug.LOG_TAG, "ReminderDbHelper.onCreate");
        db.execSQL(SQL_CREATE_ENTRIES_ACTIVITY);

        db.execSQL(SQL_CREATE_ENTRIES_GPS);
        db.execSQL(SQL_CREATE_ENTRIES_OFFINTERVALS);
        Log.d(Constants.Debug.LOG_TAG, "DB created.");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.d(Constants.Debug.LOG_TAG, "ReminderDbHelper.onUpgrade" + db.getVersion());
        db.execSQL(SQL_DELETE_ENTRIES);
        db.setVersion(newVersion);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}