package com.example.chronos.themeprojectitsmap_201270746.Wizard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chronos.themeprojectitsmap_201270746.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WizardReviewFragment extends Fragment {

    private WizardActivity parentActivity;

    ListView listView;

    public WizardReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wizard_review, container, false);

        listView = (ListView) rootView.findViewById(R.id.review_list);

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parentActivity = (WizardActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.parentActivity = null;
    }

    // We need to know when the review fragment i visible to update it with the latest changes, since "onCreate" and "onCreateView" is called before the previous fragment is filled out
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            listView.setAdapter(new ReviewAdapter(getReviewItems()));
        }
    }

    private List<ReviewItem> getReviewItems(){
        List<ReviewItem> reviewItems = new ArrayList<>();
        WizardData wizardObject = parentActivity.activityObject;

        // Activity name review item
        ReviewItem activityNameItem = new ReviewItem();
        activityNameItem.name = getResources().getString(R.string.wizard_review_item_name);
        activityNameItem.value = wizardObject.getActivityName();

        // Duration review item
        ReviewItem activityDurationItem = new ReviewItem();
        activityDurationItem.name = getResources().getString(R.string.wizard_review_item_duration);
        activityDurationItem.value = Integer.toString(wizardObject.getActivityDuration()) + getResources().getString(R.string.wizard_review_item_mins);;

        // Night mode review item
        ReviewItem nightModeItem = new ReviewItem();
        nightModeItem.name = getResources().getString(R.string.wizard_review_item_night_mode);
        if (wizardObject.nightModeTimeSlot.isSet) {
            String fromTime = parentActivity.getTimeFormat(wizardObject.nightModeTimeSlot.startTime.get(Calendar.HOUR_OF_DAY), wizardObject.nightModeTimeSlot.startTime.get(Calendar.MINUTE));
            String endTime = parentActivity.getTimeFormat(wizardObject.nightModeTimeSlot.endTime.get(Calendar.HOUR_OF_DAY), wizardObject.nightModeTimeSlot.endTime.get(Calendar.MINUTE));
            nightModeItem.value = fromTime + " - " + endTime;
        } else {
            nightModeItem.value = getResources().getString(R.string.wizard_review_item_none);
        }

        // Geo lock review item
        ReviewItem geoLockItem = new ReviewItem();
        geoLockItem.name = getResources().getString(R.string.wizard_review_item_geo_locked);
        if(wizardObject.GeoLock.isSet){
            geoLockItem.value = getResources().getString(R.string.wizard_review_item_yes);
        } else {
            geoLockItem.value = getResources().getString(R.string.wizard_review_item_no);
        }

        reviewItems.add(activityNameItem);
        reviewItems.add(activityDurationItem);
        reviewItems.add(nightModeItem);
        reviewItems.add(geoLockItem);

        // Not disturb review items
        if (wizardObject.notDisturbTimeSlots != null){
            for (TimeSlot timeSlot : wizardObject.notDisturbTimeSlots) {
                if (timeSlot.isSet) {
                    ReviewItem reviewItem = new ReviewItem();
                    String fromTime = parentActivity.getTimeFormat(timeSlot.startTime.get(Calendar.HOUR_OF_DAY), timeSlot.startTime.get(Calendar.MINUTE));
                    String endTime = parentActivity.getTimeFormat(timeSlot.endTime.get(Calendar.HOUR_OF_DAY), timeSlot.endTime.get(Calendar.MINUTE));
                    reviewItem.name = getResources().getString(R.string.wizard_review_not_disturb);
                    reviewItem.value = fromTime + " - " + endTime;
                    reviewItems.add(reviewItem);
                }
            }
        }

        return reviewItems;
    }

    private class ReviewAdapter extends BaseAdapter {

        private List<ReviewItem> data;

        public ReviewAdapter(List<ReviewItem> data) {

            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return data.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.wizard_review_list_item, parent, false);

            ReviewItem reviewItem = data.get(position);

            ((TextView) rootView.findViewById(R.id.review_item_name)).setText(reviewItem.name);
            ((TextView) rootView.findViewById(R.id.review_item_value)).setText(reviewItem.value);
            return rootView;
        }
    }

}
