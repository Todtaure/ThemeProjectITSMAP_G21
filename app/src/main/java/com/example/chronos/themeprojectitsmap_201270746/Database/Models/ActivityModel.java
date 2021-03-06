package com.example.chronos.themeprojectitsmap_201270746.Database.Models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Chronos on 20/05/2015.
 */
public class ActivityModel
{
    //Private properties
    private long id;
    private String name;
    private Boolean isSnooze;
    private Boolean isOff;
    private int minTimeInterval;
    private int maxReminders;
    private int reminderCounter;
    private Boolean done;
    private String nightMode;

    private List<GPSModel> gpsData;
    private List<OffIntervalsModel> offIntervals;

    //Getters and Setters
    public void setId(long id)
    {
        this.id = id;
    }
    public long getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }

    public void setIsSnooze(Boolean isSnooze)
    {
        this.isSnooze = isSnooze;
    }
    public Boolean getIsSnooze()
    {
        return isSnooze;
    }

    public void setIsOff(Boolean isOff)
    {
        this.isOff = isOff;
    }
    public Boolean getIsOff()
    {
        return isOff;
    }

    public void setMinTimeInterval(int minTimeInterval)
    {
        this.minTimeInterval = minTimeInterval;
    }
    public int getMinTimeInterval()
    {
        return minTimeInterval;
    }

    public void setMaxReminders(int maxReminders)
    {
        this.maxReminders = maxReminders;
    }
    public int getMaxReminders()
    {
        return maxReminders;
    }

    public void setReminderCounter(int reminderCounter)
    {
        this.reminderCounter = reminderCounter;
    }
    public int getReminderCounter()
    {
        return reminderCounter;
    }

    public void setDone(Boolean done)
    {
        this.done = done;
    }
    public Boolean getDone()
    {
        return done;
    }

    public void setNightMode(String interval)
    {
        this.nightMode = interval;
    }
    public String getNightMode()
    {
        return nightMode;
    }

    public void setGpsData(List<GPSModel> gpsData)
    {
        this.gpsData = gpsData;
    }
    public List<GPSModel> getGpsData()
    {
        if(gpsData == null)
        {
            gpsData = new ArrayList<GPSModel>();
        }
        return gpsData;
    }

    public void setOffIntervals(List<OffIntervalsModel> offIntervals){
        this.offIntervals = offIntervals;
    }
    public List<OffIntervalsModel> getOffIntervals(){
        if(offIntervals == null)
        {
            offIntervals = new ArrayList<OffIntervalsModel>();
        }
        return offIntervals;
    }

    public ActivityModel()
    {
        setDone(false);
        setIsOff(true);
        setIsSnooze(false);
        setReminderCounter(0);
        setMaxReminders(3);
    }

    //Other public methods.

    public void addGpsData(GPSModel data)
    {
        if(gpsData == null)
        {
            gpsData = new ArrayList<GPSModel>();
        }
        gpsData.add(data);
    }

    public void addGpsData(Collection<GPSModel> data)
    {
        if(gpsData == null)
        {
            gpsData = new ArrayList<GPSModel>();
        }
        gpsData.addAll(data);
    }

    public void removeGpsData(GPSModel data)
    {
        if(gpsData != null)
        {
            gpsData.remove(data);
        }
    }

    public void addOffInterval(OffIntervalsModel data)
    {
        if(offIntervals == null)
        {
            offIntervals = new ArrayList<OffIntervalsModel>();
        }
        offIntervals.add(data);
    }

    public void addOffInterval(Collection<OffIntervalsModel> data)
    {
        if(offIntervals == null)
        {
            offIntervals = new ArrayList<OffIntervalsModel>();
        }
        offIntervals.addAll(data);
    }

    public void removeOffInterval(OffIntervalsModel data) {
        if(offIntervals != null)
        {
            offIntervals.remove(data);
        }
    }

    /**
     * Post increments the Reminder Counter for the Activity.
     */
    public void incrementReminderCounter()
    {
        reminderCounter++;
    }

}