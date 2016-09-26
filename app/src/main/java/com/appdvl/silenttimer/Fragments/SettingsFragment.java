package com.appdvl.silenttimer.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.appdvl.silenttimer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}