package com.example.chronos.themeprojectitsmap_201270746.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Database.ActivityDataSource;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.R;
import com.example.chronos.themeprojectitsmap_201270746.SettingsActivity;

import java.sql.SQLException;

/**
 * Created by Breuer on 26-05-2015.
 */
public class IntervalListPreference extends DialogPreference {
    ListView listView = null;
    ActivityDataSource dataSource;
    long activityId;

    public IntervalListPreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        Intent intent = ((SettingsActivity) ctxt).getIntent();
        activityId = intent.getLongExtra(Constants.ACTIVITY_ID, -1);
        try
        {
            dataSource = new ActivityDataSource(getContext());
        }
        catch(SQLException ex)
        {
            Toast.makeText(getContext(), "Shit went down", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected View onCreateDialogView() {
        listView = new ListView(getContext());

        if (activityId == -1)
        {
            return(listView);
        }
        dataSource.open();
        ActivityModel activity = dataSource.getActivityById(activityId);
        IntervalListAdapter intervalAdapter = new IntervalListAdapter(getContext(), R.layout.interval_list, activity.getOffIntervals());
        listView.setAdapter(intervalAdapter);
        dataSource.close();
        return(listView);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
    }
}
