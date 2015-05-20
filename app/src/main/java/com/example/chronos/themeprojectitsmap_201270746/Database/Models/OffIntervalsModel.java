package com.example.chronos.themeprojectitsmap_201270746.Database.Models;

public class OffIntervalsModel
{
    private long id;
    private String offInterval;

    public void setId(long id)
    {
        this.id = id;
    }
    public long getId()
    {
        return id;
    }

    public void setOffInterval(String offInterval)
    {
        this.offInterval = offInterval;
    }
    public String getOffInterval()
    {
        return offInterval;
    }

    /**
     * Create new offInterval.
     * @param offInterval
     */
    public OffIntervalsModel(String offInterval)
    {
        this.id = -1;
        this.offInterval = offInterval;
    }

    /**
     * Create offInterval from database.
     * @param id from database.
     * @param offInterval from database.
     */
    public OffIntervalsModel(long id, String offInterval)
    {
        this.id = id;
        this.offInterval = offInterval;
    }
}
