package com.example.chronos.themeprojectitsmap_201270746.Wizard;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.chronos.themeprojectitsmap_201270746.R;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class WizardNightModeFragment extends Fragment implements View.OnClickListener {

    private WizardActivity parentActivity;

    EditText editTextNightModeFromTime;
    EditText editTextNightModeToTime;

    InputMethodManager imm;

    public WizardNightModeFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_wizard_night_mode, container, false);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        editTextNightModeFromTime = (EditText) rootView.findViewById(R.id.editText_nightMode_from);
        editTextNightModeFromTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNightModeFromTime);

        editTextNightModeToTime = (EditText) rootView.findViewById(R.id.editText_nightMode_to);
        editTextNightModeToTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNightModeToTime);

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

    @Override
    public void onClick(View v) {
        int hour = 0;
        int minute = 0;

        switch (v.getId()){
            case R.id.editText_nightMode_from:

                if (parentActivity.activityObject.nightModeTimeSlot.startTime != null) {
                    hour = parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.HOUR_OF_DAY);
                    minute = parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.MINUTE);
                }

                // Launch Time Picker Dialog
                new TimePickerDialog(this.getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                parentActivity.activityObject.nightModeTimeSlot.startTime = new GregorianCalendar(0,0,0,hourOfDay,minute);

                                if (parentActivity.activityObject.nightModeTimeSlot.endTime != null){
                                    if ((parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.HOUR_OF_DAY) != parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.HOUR_OF_DAY)) | (parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.HOUR_OF_DAY) == parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.HOUR_OF_DAY) && parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.MINUTE) != parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.MINUTE))){
                                        parentActivity.activityObject.nightModeTimeSlot.isSet = true;
                                    } else {
                                        parentActivity.activityObject.nightModeTimeSlot.isSet = false;
                                    }
                                } else {
                                    parentActivity.activityObject.nightModeTimeSlot.isSet = false;
                                }

                                // Display Selected time in textbox
                                editTextNightModeFromTime.setText(parentActivity.getTimeFormat(hourOfDay, minute));
                            }
                        }, hour, minute, true).show();
                break;
            case R.id.editText_nightMode_to:

                if (parentActivity.activityObject.nightModeTimeSlot.endTime != null) {
                    hour = parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.HOUR_OF_DAY);
                    minute = parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.MINUTE);
                }

                // Launch Time Picker Dialog
                new TimePickerDialog(this.getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                parentActivity.activityObject.nightModeTimeSlot.endTime = new GregorianCalendar(0,0,0,hourOfDay,minute);

                                if (parentActivity.activityObject.nightModeTimeSlot.startTime != null){
                                    if ((parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.HOUR_OF_DAY) != parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.HOUR_OF_DAY)) | (parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.HOUR_OF_DAY) == parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.HOUR_OF_DAY) && parentActivity.activityObject.nightModeTimeSlot.startTime.get(Calendar.MINUTE) != parentActivity.activityObject.nightModeTimeSlot.endTime.get(Calendar.MINUTE))){
                                        parentActivity.activityObject.nightModeTimeSlot.isSet = true;
                                    } else {
                                        parentActivity.activityObject.nightModeTimeSlot.isSet = false;
                                    }
                                } else {
                                    parentActivity.activityObject.nightModeTimeSlot.isSet = false;
                                }

                                // Display Selected time in textbox
                                editTextNightModeToTime.setText(parentActivity.getTimeFormat(hourOfDay, minute));
                            }
                        }, hour, minute, true).show();
                break;
        }
    }

    private void disableKeyboardPopup(EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);   // handle the event first
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  // hide the soft keyboard
                }
                return true;
            }
        });
    }

}
