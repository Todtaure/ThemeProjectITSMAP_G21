package com.example.chronos.themeprojectitsmap_201270746.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.GPSModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.OffIntervalsModel;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Breuer on 20-05-2015.
 */
public class ActivityDataSource {
    private ReminderDbHelper dbHelper;
    private SQLiteDatabase db;
    private Context currentContext;

    /**
     * Used for handling communication with the database.
     * @param context
     * @throws SQLException
     */
    public ActivityDataSource(Context context) throws SQLException {
        currentContext = context;
        dbHelper = new ReminderDbHelper(currentContext);
    }

    /**
     * Open DB connection
     */
    public void open()
    {
        try {
            db = dbHelper.getWritableDatabase();
        }
        catch(android.database.SQLException ex) {
            Toast.makeText(currentContext, Constants.Messages.ERR_DB_CONNECTION, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Close DB connection.
     */
    public void close()
    {
        dbHelper.close();
    }

    //
    //------------------Create methods------------------
    //

    /**
     * Write an Activity to the database. Also inserts GPS and OffInterval data.     *
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

        activityId = db.insert(ReminderContract.ActivityTable.TABLE_NAME, null, values);

        //Clear ContentValue collection
        values.clear();

        if(activityId == -1)
        {
            Toast.makeText(currentContext, "Activity - " + Constants.Messages.ERR_DB_INSERT, Toast.LENGTH_LONG).show();
            return false;
        }

        insertGPSData(activity, activityId);
        insertOffIntervals(activity, activityId);

        return true;
    }

    /**
     * Insert single GPS data set.
     * @param gpsData
     * @param activityId
     * @return
     */
    public boolean insertGPSData(GPSModel gpsData, long activityId) {
        if(!db.isOpen())
        {
            open();
        }
        ContentValues values = new ContentValues();

        //Add data for GPS table
        values.put(ReminderContract.GPSTable.COLUMN_NAME_FRIENDLYNAME, gpsData.getName());
        values.put(ReminderContract.GPSTable.COLUMN_NAME_GEOLOC, gpsData.getCoordinates());
        values.put(ReminderContract.GPSTable.COLUMN_NAME_ACTIVITY_FK, activityId);
        long returnId = db.insert(ReminderContract.GPSTable.TABLE_NAME, null, values);

        if(returnId == -1)
        {
            Toast.makeText(currentContext, "GPS Data - " + Constants.Messages.ERR_DB_INSERT, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Insert single OffInterval.
     * @param offInterval
     * @param activityId
     * @return
     */
    public boolean insertOffInterval(OffIntervalsModel offInterval, long activityId) {
        if(!db.isOpen())
        {
            open();
        }
        ContentValues values = new ContentValues();

        //Add data for OffIntervals table
        values.put(ReminderContract.OffIntervalsTable.COLUMN_NAME_OFFINTERVAL, offInterval.getOffInterval());
        values.put(ReminderContract.OffIntervalsTable.COLUMN_NAME_ACTIVITY_FK, activityId);

        long returnId = db.insert(ReminderContract.OffIntervalsTable.TABLE_NAME, null, values);

        if(returnId == -1)
        {
            Toast.makeText(currentContext, "OffInterval - " + Constants.Messages.ERR_DB_INSERT, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void insertGPSData(ActivityModel activity, long activityId) {
        if(!db.isOpen())
        {
            open();
        }
        Collection<GPSModel> gpsData = activity.getGpsData();

        if(gpsData.isEmpty())
        {
            return;
        }

        //Add data for GPS table
        for(GPSModel item : gpsData) {
            insertGPSData(item, activityId);
        }
    }

    private void insertOffIntervals(ActivityModel activity, long activityId) {
        if(!db.isOpen())
        {
            open();
        }
        Collection<OffIntervalsModel> offIntervalsData = activity.getOffIntervals();

        //Add data for OffIntervals table
        for(OffIntervalsModel item : offIntervalsData) {
           insertOffInterval(item, activityId);
        }
    }

    //
    //------------------Read methods------------------
    //

    /**
     * Retrieve all Activities.
     * @return
     */
    public ArrayList<ActivityModel> getAllActivities() {
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

    /**
     * Retrieve an Activity by its ID.
     * @param activityId
     * @return
     */
    public ActivityModel getActivityById(long activityId) {
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
        String  selection = ReminderContract.ActivityTable._ID + " = ?";
        String[] selectionArgs = { String.valueOf(activityId) };

        Cursor cursor = db.query(
                ReminderContract.ActivityTable.TABLE_NAME,
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        activityList = readActivityCursor(cursor);

        if(activityList.isEmpty())
        {
            return null;
        }

        ActivityModel activity = activityList.get(0);

        activity.addGpsData(getGpsDataByActivity(activityId));
        activity.addOffInterval(getOffIntervalsByActivity(activityId));

        return activity;
    }

    private ArrayList<GPSModel> getGpsDataByActivity(long activityId) {
        if(!db.isOpen())
        {
            open();
        }

        ArrayList<GPSModel> gpsDataList;

        String[] projection = {
                ReminderContract.GPSTable._ID,
                ReminderContract.GPSTable.COLUMN_NAME_FRIENDLYNAME,
                ReminderContract.GPSTable.COLUMN_NAME_GEOLOC,
                ReminderContract.GPSTable.COLUMN_NAME_ACTIVITY_FK
        };
        String sortOrder = ReminderContract.GPSTable.COLUMN_NAME_ACTIVITY_FK + " DESC";
        String  selection = ReminderContract.GPSTable.COLUMN_NAME_ACTIVITY_FK + " = ?";
        String[] selectionArgs = { String.valueOf(activityId) };

        Cursor cursor = db.query(
                ReminderContract.GPSTable.TABLE_NAME,
                projection,                               // The columns to return
                selection,                                     // The columns for the WHERE clause
                selectionArgs,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        gpsDataList = readGPSDataCursor(cursor);
        cursor.close();

        return gpsDataList;
    }

    private ArrayList<OffIntervalsModel> getOffIntervalsByActivity(long activityId) {
        if(!db.isOpen())
        {
            open();
        }

        ArrayList<OffIntervalsModel> offIntervalsList;

        String[] projection = {
                ReminderContract.OffIntervalsTable._ID,
                ReminderContract.OffIntervalsTable.COLUMN_NAME_OFFINTERVAL,
                ReminderContract.OffIntervalsTable.COLUMN_NAME_ACTIVITY_FK
        };
        String sortOrder = ReminderContract.OffIntervalsTable.COLUMN_NAME_ACTIVITY_FK + " DESC";
        String  selection = ReminderContract.OffIntervalsTable.COLUMN_NAME_ACTIVITY_FK + " = ?";
        String[] selectionArgs = { String.valueOf(activityId) };

        Cursor cursor = db.query(
                ReminderContract.OffIntervalsTable.TABLE_NAME,
                projection,                               // The columns to return
                selection,                                     // The columns for the WHERE clause
                selectionArgs,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        offIntervalsList = readOffIntervalsCursor(cursor);
        cursor.close();

        return offIntervalsList;
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

            activity.setId(cursor.getLong(
                    cursor.getColumnIndex(ReminderContract.ActivityTable._ID)));
            activity.setName(cursor.getString(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_ACTIVITYNAME)));
            activity.setIsSnooze(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_ISSNOOZE)) == 1);
            activity.setIsOff(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_ISOFF)) == 1);
            activity.setMinTimeInterval(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_MINTIMEINTERVAL)));
            activity.setMaxReminders(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_MAXREMINDERS)));
            activity.setReminderCounter(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_REMINDERCOUNTER)));
            activity.setDone(cursor.getInt(
                    cursor.getColumnIndex(ReminderContract.ActivityTable.COLUMN_NAME_DONE)) == 1);

            activity.setGpsData(getGpsDataByActivity(activity.getId()));
            activity.setOffIntervals(getOffIntervalsByActivity(activity.getId()));

            activityList.add(activity);
        }

        return activityList;
    }

    private ArrayList<GPSModel> readGPSDataCursor(Cursor cursor) {
        ArrayList<GPSModel> gpsList = new ArrayList<GPSModel>();
        if(cursor.getCount() == 0)
        {
            return gpsList;
        }

        while(cursor.moveToNext())
        {
            GPSModel gpsData = new GPSModel();

            gpsData.setId(cursor.getLong(
                    cursor.getColumnIndex(ReminderContract.GPSTable._ID)));
            gpsData.setName(cursor.getString(
                    cursor.getColumnIndex(ReminderContract.GPSTable.COLUMN_NAME_FRIENDLYNAME)));
            gpsData.setCoordinates(cursor.getString(
                    cursor.getColumnIndex(ReminderContract.GPSTable.COLUMN_NAME_GEOLOC)));

            gpsList.add(gpsData);
        }

        return gpsList;
    }

    private ArrayList<OffIntervalsModel> readOffIntervalsCursor(Cursor cursor) {
        ArrayList<OffIntervalsModel> offIntervalsList = new ArrayList<OffIntervalsModel>();
        if(cursor.getCount() == 0)
        {
            return offIntervalsList;
        }

        while(cursor.moveToNext())
        {
            OffIntervalsModel offIntervals = new OffIntervalsModel();

            offIntervals.setId(cursor.getLong(
                    cursor.getColumnIndex(ReminderContract.OffIntervalsTable._ID)));
            offIntervals.setOffInterval(cursor.getString(
                    cursor.getColumnIndex(ReminderContract.OffIntervalsTable.COLUMN_NAME_OFFINTERVAL)));

            offIntervalsList.add(offIntervals);
        }

        return offIntervalsList;
    }

    //
    //------------------Update methods------------------
    //

    /**
     * Update an existing Activity. Does not update GPS or OffIntervals!
     * @param activity
     * @return
     */
    public boolean updateActivity(ActivityModel activity) {
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

        String  selection = ReminderContract.ActivityTable._ID + " = ?";
        String[] selectionArgs = { String.valueOf(activity.getId()) };

        int count;
        count = db.update(ReminderContract.ActivityTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        if(!(count > 0))
        {
            Toast.makeText(currentContext, "Activity - " + Constants.Messages.ERR_DB_UPDATE, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * Update an existing GPS data set.
     * @param gpsData
     * @return
     */
    public boolean updateGPSData(GPSModel gpsData) {
        if(!db.isOpen())
        {
            open();
        }
        ContentValues values = new ContentValues();

        values.put(ReminderContract.GPSTable.COLUMN_NAME_FRIENDLYNAME, gpsData.getName());
        values.put(ReminderContract.GPSTable.COLUMN_NAME_GEOLOC, gpsData.getCoordinates());

        String  selection = ReminderContract.GPSTable._ID + " = ?";
        String[] selectionArgs = { String.valueOf(gpsData.getId()) };

        int count = db.update(
                ReminderContract.GPSTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        if(!(count > 0))
        {
            Toast.makeText(currentContext, "GPS Data - " + Constants.Messages.ERR_DB_UPDATE, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Update an existing OffInterval.
     * @param offInterval
     * @return
     */
    public boolean updateOffInterval(OffIntervalsModel offInterval) {
        if(!db.isOpen())
        {
            open();
        }
        ContentValues values = new ContentValues();

        values.put(ReminderContract.OffIntervalsTable.COLUMN_NAME_OFFINTERVAL, offInterval.getOffInterval());

        String  selection = ReminderContract.OffIntervalsTable._ID + " = ?";
        String[] selectionArgs = { String.valueOf(offInterval.getId()) };

        int count = db.update(
                ReminderContract.OffIntervalsTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        if(!(count > 0))
        {
            Toast.makeText(currentContext, "OffInterval Data - " + Constants.Messages.ERR_DB_UPDATE, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //
    //------------------Delete methods------------------
    //

    /**
     * Delete an Activity.
     * @param activityId
     * @return
     */
    public boolean deleteActivityById(long activityId) {
        if(!db.isOpen())
        {
            open();
        }

        String selection = ReminderContract.ActivityTable._ID + " = ?";
        String[] selectionArgs = { String.valueOf(activityId) };

        int count = db.delete(ReminderContract.ActivityTable.TABLE_NAME, selection, selectionArgs);

        if(!(count > 0))
        {
            Toast.makeText(currentContext, "Activity - " + Constants.Messages.ERR_DB_DELETE, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * Delete GPS Data set.
     * @param gpsDataId
     * @return
     */
    public boolean deleteGPSDataById(long gpsDataId) {
        if(!db.isOpen())
        {
            open();
        }

        String selection = ReminderContract.GPSTable._ID + " = ?";
        String[] selectionArgs = { String.valueOf(gpsDataId) };

        int count = db.delete(ReminderContract.GPSTable.TABLE_NAME, selection, selectionArgs);

        if(!(count > 0))
        {
            Toast.makeText(currentContext, "GPS Data - " + Constants.Messages.ERR_DB_DELETE, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Delete OffInterval.
     * @param offIntervalId
     * @return
     */
    public boolean deleteOffIntervalById(long offIntervalId) {
        if(!db.isOpen())
        {
            open();
        }

        String selection = ReminderContract.OffIntervalsTable._ID + " = ?";
        String[] selectionArgs = { String.valueOf(offIntervalId) };

        int count = db.delete(ReminderContract.OffIntervalsTable.TABLE_NAME, selection, selectionArgs);

        if(!(count > 0))
        {
            Toast.makeText(currentContext, "OffInterval - " + Constants.Messages.ERR_DB_DELETE, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
