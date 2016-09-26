package com.appdvl.silenttimer.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appdvl.silenttimer.R;
import com.appdvl.silenttimer.Services.SilentTimeService;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        SilentTimeService service = new SilentTimeService();

        // Do something with the time chosen by the user
        StringBuilder time = new StringBuilder()
                .append(service.pad(hourOfDay))
                .append(":")
                .append(service.pad(minute));

        if(getTag().equals("startTimePicker"))
        {
            TextView startTimeTextView = (TextView)getActivity().findViewById(R.id.details_start_time_text_view);
            startTimeTextView.setText(time);
        }
        else if(getTag().equals("endTimePicker"))
        {
            TextView endTimeTextView = (TextView)getActivity().findViewById(R.id.details_end_time_text_view);
            endTimeTextView.setText(time);
        }
    }
}