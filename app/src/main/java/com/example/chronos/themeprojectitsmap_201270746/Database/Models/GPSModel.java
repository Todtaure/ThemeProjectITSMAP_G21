package com.example.chronos.themeprojectitsmap_201270746.Database.Models;

/**
 * Created by Chronos on 20/05/2015.
 */
public class GPSModel {
    private long id;
    private String name;
    private String coordinates;

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

    public void setCoordinates(String coordinates)
    {
        this.coordinates = coordinates;
    }
    public String getCoordinates()
    {
        return coordinates;
    }

    /**
     * Create GPS coordinates from database.
     */
    public GPSModel()
    {
    }

    /**
     * Create new GPS coordinates.
     * @param coordinates
     */
    public GPSModel(String name, String coordinates)
    {
        this.id = -1;
        this.name = name;
        this.coordinates = coordinates;
    }
}

