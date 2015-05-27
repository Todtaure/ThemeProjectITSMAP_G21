package com.example.chronos.themeprojectitsmap_201270746.Service;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import java.util.Calendar;

/**
 * Created by Alex on 26-05-2015.
 */
public class CalendarInfo {
    private Cursor mCursor = null;
    private static final String[] COLS = new String[]
            { CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};

    public int getTimeInMinToNextFreeTimeSlot(Context context, int requiredTimeSlotInMin){
        Calendar startTime = Calendar.getInstance();
        Calendar endTime= Calendar.getInstance();
        endTime.add(Calendar.HOUR,48);

        String selection = "(( " + CalendarContract.Events.DTEND + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.ACCOUNT_NAME + " == ? ))";
        String[] selectionArgs = new String[] {"breuer.rz@gmail.com"};

        mCursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, COLS, selection, selectionArgs, null);
        mCursor.moveToFirst();

        long eventStartTime = mCursor.getLong(1);
        long eventEndTime = mCursor.getLong(2);

        //long timeDifferenceInMillis = eventStartTime - startTime.getTimeInMillis();
        int timeDifferenceInMin = (int)((eventStartTime - startTime.getTimeInMillis()) / (1000 * 60));

        // if there is enough time until next event return 0
        if (timeDifferenceInMin >= requiredTimeSlotInMin) {
            return 0;
        } else {
            while (timeDifferenceInMin < requiredTimeSlotInMin && !mCursor.isLast())
            {
                mCursor.moveToNext();
                eventStartTime = mCursor.getLong(1);
                //timeDifferenceInMillis = eventStartTime - eventEndTime;
                timeDifferenceInMin = (int)((eventStartTime - eventEndTime) / (1000 * 60));
                // if there is enough time between previous and next event return time until free interval
                if (timeDifferenceInMin >= requiredTimeSlotInMin) {
                    int timeUntilFreeTimeSlot = (int)(eventEndTime - startTime.getTimeInMillis())/ (1000*60);
                    return timeUntilFreeTimeSlot;
                } // if there is not enough time before after the last event return number of minutes till the end of the event
                else if (mCursor.isLast()){
                    return (int)((mCursor.getLong(2) - startTime.getTimeInMillis())/(1000 * 60));
                }
                eventEndTime = mCursor.getLong(2);
            }
            // if only one event in calendar return minutes until end of event
            if (mCursor.isLast()){
                return (int)((mCursor.getLong(2) - startTime.getTimeInMillis())/(1000 * 60));
            }
            return -1;

        }
    }

}

