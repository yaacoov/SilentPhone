package com.appdvl.silenttimer.Activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.appdvl.silenttimer.R;


public class DialogActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        {
            //Change the color of the Title Divider
            int dividerId = getApplicationContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = findViewById(dividerId);
            divider.setBackgroundColor(getResources().getColor(R.color.color_primary));
            setContentView(R.layout.activity_dialog);
        }


        if(isLargeScreen())
        {
            // if width > height use Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public boolean isLargeScreen()
    {
        final int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}