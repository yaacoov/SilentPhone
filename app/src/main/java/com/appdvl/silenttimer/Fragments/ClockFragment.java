package com.appdvl.silenttimer.Fragments;


import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdvl.silenttimer.R;
import com.appdvl.silenttimer.Services.SilentTimeService;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockFragment extends Fragment
{

    int interval = 1000;
    private Handler TimeHandler;
    TextView fancyClock;
    TextView fancyDate;
    boolean isVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        TimeHandler = new Handler();

        View rootView = inflater.inflate(R.layout.fragment_clock, container, false);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "GeosansLight.ttf");

        fancyClock = (TextView)rootView.findViewById(R.id.txt_fancyclock);
        fancyClock.setIncludeFontPadding(false);
        fancyClock.setTypeface(font);

        fancyDate = (TextView)rootView.findViewById(R.id.txt_fancydate);

        isVisible = true;

        startRepeatingTask();

        return rootView;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        stopRepeatingTask();
    }

    Runnable UpdateFancyClock = new Runnable()
    {
        @Override
        public void run()
        {
            UpdateTimeTextView(fancyClock);
            UpdateDateTextView(fancyDate);
            TimeHandler.postDelayed(UpdateFancyClock, interval);
        }
    };

    void startRepeatingTask()
    {
        UpdateFancyClock.run();
    }

    void stopRepeatingTask()
    {
        TimeHandler.removeCallbacks(UpdateFancyClock);
    }

    private TextView UpdateTimeTextView(TextView fancyClock)
    {
        SilentTimeService service = new SilentTimeService();

        final Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minutes = time.get(Calendar.MINUTE);
        int seconds = time.get(Calendar.SECOND);

        String combinedString = service.pad(hour) + ":" + service.pad(minutes) + " " + service.pad(seconds) ;

        SpannableString finalTime = new SpannableString(combinedString);
        finalTime.setSpan(new RelativeSizeSpan(4f), 0, 5, 0);

        fancyClock.setText(finalTime);
        return fancyClock;
    }

    private TextView UpdateDateTextView(TextView fancyDate)
    {
        String date = java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        fancyDate.setText(date);
        return fancyDate;
    }
}