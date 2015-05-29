package com.example.chronos.themeprojectitsmap_201270746;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chronos.themeprojectitsmap_201270746.Database.ActivityDataSource;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Service.ReminderService;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

import java.util.List;

public class ActivityListAdapter extends ArrayAdapter
{
    private Context mContext;
    private int id;
    private List<ActivityModel> items;
    private int selected_position = -1;

    public ActivityListAdapter(Context context, int textViewResourceId , List<ActivityModel> list )
    {
        super(context, textViewResourceId, list);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        selected_position = sharedPreferences.getInt("checkedId", -1);

        mContext = context;
        id = textViewResourceId;
        items = list;
    }


    @Override
    public View getView(final int position, View v, ViewGroup parent)
    {
        View mView = v;

        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.textView);
        CheckBox listItemCheckbox = (CheckBox)mView.findViewById(R.id.listItemCheckbox);


        if(items.get(position) != null )
        {
            text.setTextColor(Color.BLACK);
            text.setText(items.get(position).getName());
            text.setTextSize(22);

            int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
            listItemCheckbox.setButtonDrawable(id);
        }

        if(selected_position == position)
        {
            listItemCheckbox.setChecked(true);
        }
        else
        {
            listItemCheckbox.setChecked(false);
        }

        listItemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActivityModel checked = new ActivityModel();

                if (isChecked) {
                    selected_position = position;

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

//                            ((MainMenuActivity) mContext).sendToService(items.get(position).getId(), Constants.Service.ACTIVITY_STATE_CHANGE);



                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("checkedId", position);
                    editor.commit();

                } else {
                       // ((MainMenuActivity) mContext).sendToService(items.get(position).getId(), Constants.Service.SERVICE_STOP);

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("checkedId");
                    editor.commit();
                    selected_position = -1;
                }
                notifyDataSetChanged();
            }
        });


        return mView;
    }
}
