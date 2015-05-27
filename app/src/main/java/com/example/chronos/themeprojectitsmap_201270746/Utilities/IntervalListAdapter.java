package com.example.chronos.themeprojectitsmap_201270746.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chronos.themeprojectitsmap_201270746.Database.Models.ActivityModel;
import com.example.chronos.themeprojectitsmap_201270746.Database.Models.OffIntervalsModel;
import com.example.chronos.themeprojectitsmap_201270746.R;

import java.util.List;

/**
 * Created by Breuer on 26-05-2015.
 */
public class IntervalListAdapter extends ArrayAdapter {
    private Context mContext;
    private int id;
    private List<OffIntervalsModel> items;

    public IntervalListAdapter(Context context, int textViewResourceId , List<OffIntervalsModel> list )
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

        TextView interval = (TextView) mView.findViewById(R.id.intervalView);

        if(items.get(position) != null )
        {
            String text = items.get(position).getOffInterval();

            interval.setText(text.replace(",", " - "));
        }

        return mView;
    }
}
