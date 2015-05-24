package com.example.chronos.themeprojectitsmap_201270746.Wizard;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
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


public class WizardDontDisturbFragment extends Fragment implements View.OnClickListener {

    private WizardActivity parentActivity;

    InputMethodManager imm;

    EditText editTextNotDisturbMode1FromTime;
    EditText editTextNotDisturbMode1ToTime;
    EditText editTextNotDisturbMode2FromTime;
    EditText editTextNotDisturbMode2ToTime;
    EditText editTextNotDisturbMode3FromTime;
    EditText editTextNotDisturbMode3ToTime;


    public WizardDontDisturbFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_wizard_dont_disturb, container, false);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        editTextNotDisturbMode1FromTime = (EditText) rootView.findViewById(R.id.editText_notDisturbMode1_from);
        editTextNotDisturbMode1FromTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNotDisturbMode1FromTime);

        editTextNotDisturbMode1ToTime = (EditText) rootView.findViewById(R.id.editText_notDisturbMode1_to);
        editTextNotDisturbMode1ToTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNotDisturbMode1ToTime);

        editTextNotDisturbMode2FromTime = (EditText) rootView.findViewById(R.id.editText_notDisturbMode2_from);
        editTextNotDisturbMode2FromTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNotDisturbMode2FromTime);

        editTextNotDisturbMode2ToTime = (EditText) rootView.findViewById(R.id.editText_notDisturbMode2_to);
        editTextNotDisturbMode2ToTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNotDisturbMode2ToTime);

        editTextNotDisturbMode3FromTime = (EditText) rootView.findViewById(R.id.editText_notDisturbMode3_from);
        editTextNotDisturbMode3FromTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNotDisturbMode3FromTime);

        editTextNotDisturbMode3ToTime = (EditText) rootView.findViewById(R.id.editText_notDisturbMode3_to);
        editTextNotDisturbMode3ToTime.setOnClickListener(this);
        disableKeyboardPopup(editTextNotDisturbMode3ToTime);

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
            case R.id.editText_notDisturbMode1_from:
                // Time slot 1 - start time
                showStartTimeTimePickerDialog(0, hour, minute, editTextNotDisturbMode1FromTime);
                break;
            case R.id.editText_notDisturbMode1_to:
                // Time slot 1 - end time
                showEndTimeTimePickerDialog(0, hour, minute, editTextNotDisturbMode1ToTime);
                break;
            case R.id.editText_notDisturbMode2_from:
                // Time slot 2 - start time
                showStartTimeTimePickerDialog(1, hour, minute, editTextNotDisturbMode2FromTime);
                break;
            case R.id.editText_notDisturbMode2_to:
                // Time slot 2 - end time
                showEndTimeTimePickerDialog(1, hour, minute, editTextNotDisturbMode2ToTime);
                break;
            case R.id.editText_notDisturbMode3_from:
                // Time slot 3 - start time
                showStartTimeTimePickerDialog(2, hour, minute, editTextNotDisturbMode3FromTime);
                break;
            case R.id.editText_notDisturbMode3_to:
                // Time slot 3 - start time
                showEndTimeTimePickerDialog(2, hour, minute, editTextNotDisturbMode3ToTime);
                break;
        }
    }

    private void showStartTimeTimePickerDialog(int atimeSlotNumber, int hour, int minute, EditText aEditText) {
        final int  timeSlotNumber = atimeSlotNumber;
        final EditText editText = aEditText;

        if (parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime != null ) {
            hour = parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.HOUR_OF_DAY);
            minute = parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.MINUTE);
        }

        // Launch Time Picker Dialog
        new TimePickerDialog(this.getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime = new GregorianCalendar(0,0,0,hourOfDay,minute);

                        if (parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime != null) {
                            if ((parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.HOUR_OF_DAY) != parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime.get(Calendar.HOUR_OF_DAY)) | (parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.HOUR_OF_DAY) == parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime.get(Calendar.HOUR_OF_DAY) && parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.MINUTE) != parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime.get(Calendar.MINUTE))) {
                                parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).isSet = true;
                            } else {
                                parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).isSet = false;
                            }
                        } else {
                            parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).isSet = false;
                        }

                        // Display Selected time in textbox
                        editText.setText(parentActivity.getTimeFormat(hourOfDay, minute));
                    }
                }, hour, minute, true).show();
    }

    private void showEndTimeTimePickerDialog(int atimeSlotNumber, int hour, int minute, EditText aEditText) {
        final int  timeSlotNumber = atimeSlotNumber;
        final EditText editText = aEditText;

        if (parentActivity.activityObject.notDisturbTimeSlots.get(atimeSlotNumber).endTime != null ) {
            hour = parentActivity.activityObject.notDisturbTimeSlots.get(atimeSlotNumber).endTime.get(Calendar.HOUR_OF_DAY);
            minute = parentActivity.activityObject.notDisturbTimeSlots.get(atimeSlotNumber).endTime.get(Calendar.MINUTE);
        }

        // Launch Time Picker Dialog
        new TimePickerDialog(this.getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime = new GregorianCalendar(0,0,0,hourOfDay,minute);

                        if (parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime != null) {
                            if ((parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.HOUR_OF_DAY) != parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime.get(Calendar.HOUR_OF_DAY)) | (parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.HOUR_OF_DAY) == parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime.get(Calendar.HOUR_OF_DAY) && parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).startTime.get(Calendar.MINUTE) != parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).endTime.get(Calendar.MINUTE))) {
                                parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).isSet = true;
                            }
                            else {
                                parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).isSet = false;
                            }
                        } else {
                            parentActivity.activityObject.notDisturbTimeSlots.get(timeSlotNumber).isSet = false;
                        }

                        // Display Selected time in textbox
                        editText.setText(parentActivity.getTimeFormat(hourOfDay, minute));
                    }
                }, hour, minute, true).show();
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
