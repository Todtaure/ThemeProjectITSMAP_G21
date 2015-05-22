package com.example.chronos.themeprojectitsmap_201270746.Wizard;

import java.util.List;

/**
 * Created by Alex on 22-05-2015.
 */
public class WizardData {
    private String activityName;
    private int activityDuration;
    TimeSlot nightModeTimeSlot;
    GeoLocation GeoLock;

    List<TimeSlot> notDisturbTimeSlots;

    public void setActivityName( String name ){
        if (name != null){
            activityName = name;
        }
        else {
            activityName = "unspecified";
        }
    }

    public String getActivityName(){
        return activityName;
    }

    public void setActivityDuration( int duration ){
        if (duration != 0){
            activityDuration = duration;
        }
        else {
            activityDuration = 61;
        }
    }

    public int getActivityDuration(){
        return activityDuration;
    }
}
