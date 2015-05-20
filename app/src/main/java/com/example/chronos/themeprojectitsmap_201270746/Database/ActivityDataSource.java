package com.example.chronos.themeprojectitsmap_201270746.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.GPSModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.OffIntervalsModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Breuer on 20-05-2015.
 */
public class ActivityDataSource {
    private ReminderDbHelper dbHelper;
    private SQLiteDatabase db;

    public ActivityDataSource(Context context) throws SQLException
    {
        dbHelper = new ReminderDbHelper(context);
    }

    public void open()
    {
        db = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    //
    //------------------Create methods------------------
    //

    /**
     * Write and Activity to the database. Also inserts GPS and OffInterval data.     *
     * @param activity The Activity to be written to the database.
     * @return
     */
    public boolean insertActivity(ActivityModel activity) {
        if(!db.isOpen())
        {
            open();
        }

        //Create ContentValue collection and add columns and data for insertion
        ContentValues values = new ContentValues();
        values.put(ReminderContract.ActivityTable.COLUMN_NAME_ACTIVITYNAME, activity.getName());
        values.put(ReminderContract.ActivityTable.COLUMN_NAME_ISSNOOZE, activity.getIsSnooze() ? 1 : 0);
        values.put(ReminderContract.ActivityTable.COLUMN_NAME_ISOFF, activity.getIsOff() ? 1 : 0);
        values.put(ReminderContract.ActivityTable.COLUMN_NAME_MINTIMEINTERVAL, activity.getMinTimeInterval());
        values.put(ReminderContract.ActivityTable.COLUMN_NAME_MAXREMINDERS, activity.getMaxReminders());
        values.put(ReminderContract.ActivityTable.COLUMN_NAME_REMINDERCOUNTER, activity.getReminderCounter());
        values.put(ReminderContract.ActivityTable.COLUMN_NAME_DONE, activity.getDone() ? 1 : 0);

        long activityId;

        try {
            activityId = db.insertOrThrow(ReminderContract.ActivityTable.TABLE_NAME, null, values);
        }
        catch(android.database.SQLException ex)
        {
            //TODO: Some sort of error message or logging?
            return false;
        }

        //Clear ContentValue collection
        values.clear();

        ArrayList<String> gpsErrors = insertGPSData(activity, activityId);
        ArrayList<String> offIntervalErrors = insertOffIntervals(activity, activityId);

        if(!gpsErrors.isEmpty() || !offIntervalErrors.isEmpty())
        {
            //TODO: Some sort of error message or logging?
        }

        return true;
    }

    private ArrayList<String> insertGPSData(ActivityModel activity, long activityId) {
        if(!db.isOpen())
        {
            open();
        }
        ContentValues values = new ContentValues();
        ArrayList<String> errors = new ArrayList<String>();
        Collection<GPSModel> gpsData = activity.getGpsData();

        if(gpsData.isEmpty())
        {
            return errors;
        }

        //Add data for GPS table
        for(GPSModel item : gpsData) {
            values.clear();
            values.put(ReminderContract.GPSTable.COLUMN_NAME_FRIENDLYNAME, item.getName());
            values.put(ReminderContract.GPSTable.COLUMN_NAME_GPSCOORDINATES, item.getCoordinates());
            values.put(ReminderContract.GPSTable.COLUMN_NAME_ACTIVITY_FK, activityId);

            try {
                db.insertOrThrow(ReminderContract.GPSTable.TABLE_NAME, null, values);
            }
            catch(android.database.SQLException ex)
            {
                errors.add("Exception during GPSData insert:\n" + ex.getMessage());
            }
        }

        return errors;
    }

    private ArrayList<String> insertOffIntervals(ActivityModel activity, long activityId) {
        if(!db.isOpen())
        {
            open();
        }
        ContentValues values = new ContentValues();
        ArrayList<String> errors = new ArrayList<String>();
        Collection<OffIntervalsModel> offIntervalsData = activity.getOffIntervals();

        if(offIntervalsData.isEmpty())
        {
            return errors;
        }

        //Add data for OffIntervals table
        for(OffIntervalsModel item : offIntervalsData) {
            values.clear();
            values.put(ReminderContract.OffIntervalsTable.COLUMN_NAME_OFFINTERVAL, item.getOffInterval());
            values.put(ReminderContract.OffIntervalsTable.COLUMN_NAME_ACTIVITY_FK, activityId);

            try {
                db.insertOrThrow(ReminderContract.OffIntervalsTable.TABLE_NAME, null, values);
            }
            catch(android.database.SQLException ex)
            {
                errors.add("Exception during OffInterval insert:\n" + ex.getMessage());
            }
        }

        return errors;
    }

    //
    //------------------Read methods------------------
    //

    public ArrayList<ActivityModel> getAllActivities()
    {
        if(!db.isOpen())
        {
            open();
        }

        ArrayList<ActivityModel> activityList = new ArrayList<>();

        String[] projection = {
            ReminderContract.ActivityTable._ID,
            ReminderContract.ActivityTable.COLUMN_NAME_ACTIVITYNAME,
            ReminderContract.ActivityTable.COLUMN_NAME_ISSNOOZE,
            ReminderContract.ActivityTable.COLUMN_NAME_ISOFF,
            ReminderContract.ActivityTable.COLUMN_NAME_MINTIMEINTERVAL,
            ReminderContract.ActivityTable.COLUMN_NAME_MAXREMINDERS,
            ReminderContract.ActivityTable.COLUMN_NAME_REMINDERCOUNTER,
            ReminderContract.ActivityTable.COLUMN_NAME_DONE
        };

        String sortOrder = ReminderContract.ActivityTable._ID + " DESC";

        Cursor cursor = db.query(
                ReminderContract.ActivityTable.TABLE_NAME,
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
                );

        activityList = readActivityCursor(cursor);

        //TODO: Get other values

        cursor.close();
        return activityList;
    }

    private ArrayList<ActivityModel> readActivityCursor(Cursor cursor) {
        ArrayList<ActivityModel> activityList = new ArrayList<ActivityModel>();
        if(cursor.getCount() == 0)
        {
            return activityList;
        }

        while(cursor.moveToNext())
        {
            ActivityModel activity = new ActivityModel();

            activity.setId(cursor.getLong(cursor.getColumnIndex(ReminderContract.ActivityTable._ID)));
            activity.setName(cursor.getString(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_ACTIVITYNAME)));
            activity.setIsSnooze(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_ISSNOOZE)) == 1 ? true : false);
            activity.setIsOff(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_ISOFF)) == 1 ? true : false);
            activity.setMinTimeInterval(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_MINTIMEINTERVAL)));
            activity.setMaxReminders(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_MAXREMINDERS)));
            activity.setReminderCounter(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_REMINDERCOUNTER)));
            activity.setDone(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_DONE)) == 1 ? true : false);

            activityList.add(activity);
        }

        return activityList;
    }

    private ArrayList<GPSModel> readGPSDataCursor(Cursor cursor)
    {
        ArrayList<GPSModel> gpsList = new ArrayList<GPSModel>();
        if(cursor.getCount() == 0)
        {
            return gpsList;
        }

        return gpsList;
    }
}
