package com.example.chronos.themeprojectitsmap_201270746;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;

import java.util.List;

public class ActivityListAdapter extends ArrayAdapter
{
    private Context mContext;
    private int id;
    private List<ActivityModel> items ;

    public ActivityListAdapter(Context context, int textViewResourceId , List<ActivityModel> list )
    {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list ;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.textView);

        if(items.get(position) != null )
        {
            text.setTextColor(Color.BLACK);
            text.setText(items.get(position).getName());
            text.setBackgroundColor(Color.WHITE);
            text.setTextSize(22);


            //v.setBackgroundColor(Color.BLUE);

        }
        return mView;
    }
}
