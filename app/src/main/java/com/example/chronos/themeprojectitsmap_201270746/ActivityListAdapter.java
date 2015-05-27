package com.example.chronos.themeprojectitsmap_201270746;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
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

import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Service.ReminderService;
import com.example.chronos.themeprojectitsmap_201270746.Utilities.Constants;

import java.util.List;

public class ActivityListAdapter extends ArrayAdapter
{
    private Context mContext;
    private int id;
    private List<ActivityModel> items;

    public ActivityListAdapter(Context context, int textViewResourceId , List<ActivityModel> list )
    {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list;
    }


    @Override
    public View getView(final int position, View v, ViewGroup parent)
    {
        View mView = v ;
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

        listItemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), ReminderService.class);
                    intent.putExtra(Constants.ACTIVITY_ID, items.get(position).getId());
                    getContext().startService(intent);

                } else {
                    Intent intent = new Intent(getContext(),ReminderService.class);
                    getContext().stopService(intent);
                }
            }
        });


        return mView;

    }
}
