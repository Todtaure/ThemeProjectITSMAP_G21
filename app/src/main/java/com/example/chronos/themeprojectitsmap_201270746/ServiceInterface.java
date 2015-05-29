package com.example.chronos.themeprojectitsmap_201270746;

/**
 * Created by Christian on 29-05-2015.
 */
public interface ServiceInterface {
    public void sendToService(long activityId, int messageType);
    public void sendToService(long activityId, int messageType, int snoozeInterval);
}
