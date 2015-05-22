package com.example.chronos.themeprojectitsmap_201270746.Wizard;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chronos.themeprojectitsmap_201270746.R;


public class WizardActivityFragment extends Fragment {

    private WizardActivity parentActivity;

    private TextView mActivityNameView;
    private TextView mActivityDurationView;

    private boolean hasRequiredFields = true;
    private boolean isActivityNameSet = false;
    private boolean isActivityDurationSet = false;


    public WizardActivityFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_wizard_activity, container, false);

        mActivityNameView = ((TextView) rootView.findViewById(R.id.activity_name));
        mActivityDurationView = ((TextView) rootView.findViewById(R.id.activity_duration));

        if (parentActivity.activityObject.getActivityName() != null) {
            mActivityNameView.setText(parentActivity.activityObject.getActivityName());
            mActivityDurationView.setText(Integer.toString(parentActivity.activityObject.getActivityDuration()));
            isActivityNameSet = true;
            isActivityDurationSet = true;
        }

        if (hasRequiredFields && !isActivityNameSet && !isActivityDurationSet) {
            parentActivity.notifyFragmentHasRequiredFields();
        }

        mActivityNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.toString().length() != 0){
                    isActivityNameSet = true;
                    parentActivity.activityObject.setActivityName(editable.toString());
                } else {
                    isActivityNameSet = false;
                    parentActivity.notifyFragmentHasRequiredFields();
                }

                if (isActivityDurationSet && isActivityNameSet)
                {
                    parentActivity.notifyRequiredFieldsFilled();
                }


            }
        });

        mActivityDurationView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.toString().length() != 0) {
                    isActivityDurationSet = true;
                    parentActivity.activityObject.setActivityDuration(Integer.parseInt(editable.toString()));
                } else {
                    isActivityDurationSet = false;
                    parentActivity.notifyFragmentHasRequiredFields();
                }

                if (isActivityDurationSet && isActivityNameSet)
                {
                    parentActivity.notifyRequiredFieldsFilled();
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
