package com.example.chronos.themeprojectitsmap_201270746.Wizard;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.chronos.themeprojectitsmap_201270746.R;


public class WizardGpsFragment extends Fragment {

    private WizardActivity parentActivity;

    private Switch geoLockSwitch;

    public WizardGpsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_wizard_gps, container, false);

        geoLockSwitch = (Switch) rootView.findViewById(R.id.wizard_gps_switch);

        //attach a listener to check for changes in state
        geoLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    parentActivity.activityObject.GeoLock.isSet = true;
                }else{
                    parentActivity.activityObject.GeoLock.isSet = false;
                }
            }
        });

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

}
