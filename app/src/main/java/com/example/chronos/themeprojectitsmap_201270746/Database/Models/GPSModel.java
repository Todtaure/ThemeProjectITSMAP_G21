package com.example.chronos.themeprojectitsmap_201270746.Database.Models;

/**
 * Created by Chronos on 20/05/2015.
 */
public class GPSModel {
    private long id;
    private String coordinates;

    public void setId(long id)
    {
        this.id = id;
    }
    public long getId()
    {
        return id;
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
     * Create new GPS coordinates.
     * @param coordinates
     */
    public GPSModel(String coordinates)
    {
        this.id = -1;
        this.coordinates = coordinates;
    }

    /**
     * Create GPS coordinates from database.
     * @param id from database.
     * @param coordinates from database.
     */
    public GPSModel(long id, String coordinates)
    {
        this.id = id;
        this.coordinates = coordinates;
    }

}

