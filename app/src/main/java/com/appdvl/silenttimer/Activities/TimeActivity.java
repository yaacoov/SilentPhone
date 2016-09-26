package com.appdvl.silenttimer.Activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.appdvl.silenttimer.Fragments.TimeDetailsFragment;
import com.appdvl.silenttimer.R;


public class TimeActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_silencer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // savedInstanceState is not null when there is fragment state saved from previous
        // configurations of this activity (e.g. when rotating the screen from portrait to
        // landscape). In this case, the fragment will automatically be re-added to its container
        // so we don't need to add it.
        if (savedInstanceState == null)
        {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt("intentID", getIntent().getIntExtra("intentID", 0));
            TimeDetailsFragment fragment = new TimeDetailsFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.detail_container, fragment).commit();
        }
    }
}