package com.appdvl.silenttimer.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appdvl.silenttimer.Fragments.ClockFragment;
import com.appdvl.silenttimer.Fragments.SettingsFragment;
import com.appdvl.silenttimer.Fragments.TimeDetailsFragment;
import com.appdvl.silenttimer.Fragments.TimeFragment;
import com.appdvl.silenttimer.Models.Silencer;
import com.appdvl.silenttimer.R;


public class MainActivity extends AppCompatActivity implements TimeDetailsFragment.refreshRecyclerView
{
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static final int REQUEST_CODE_TIME = 1;
    boolean mTwoPane = false;

    //UI
    TextView toolbarTitle;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;


    //Fragments
    ClockFragment clockFragment;
    TimeFragment timeFragment;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //Set title
        toolbarTitle = (TextView) findViewById(R.id.app_title);
        toolbarTitle.setText(R.string.time);


        // Create our fragments
        timeFragment = new TimeFragment();
        clockFragment = new ClockFragment();

        // Determine whether we are in single-pane or dual-pane mode by xml bool
        mTwoPane = getResources().getBoolean(R.bool.has_two_panes);
        if (mTwoPane)
        {

            getFragmentManager().beginTransaction().replace(R.id.main_container, timeFragment, "timeFragment").commit();
            getFragmentManager().beginTransaction().replace(R.id.detail_container, clockFragment, "clockFragment").commit();
        }
        else
        {
            getFragmentManager().beginTransaction().replace(R.id.main_container, timeFragment, "timeFragment").commit();
        }

        // Set the activity orientation, portrait mode only for smaller devices
        if(isLargeScreen())
        {
            // width > height, better to use Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        initNavigationDrawer();

    }

    private void initNavigationDrawer()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navitem_time: {
                        timeFragment = new TimeFragment();
                        fragmentTransaction(R.id.main_container, timeFragment, "timeFragment", View.GONE);
                        toolbarTitle.setText(R.string.time);
                        break;
                    }
                    case R.id.navitem_settings: {
                        settingsFragment = new SettingsFragment();
                        fragmentTransaction(R.id.main_container, settingsFragment, "settingsFragment", View.GONE);
                        toolbarTitle.setText(R.string.settings);
                        break;
                    }
                    case R.id.navitem_rate: {
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                }
                return false;
            }
        });

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure.
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
            return;
        }
        if(mTwoPane)
        {
            if(getFragmentManager().getBackStackEntryCount() > 0)
            {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            else
            {
                super.onBackPressed();
            }
        }
        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public void getSilencer(Silencer silencer, int position)
    {
        timeFragment.refreshSilencer(silencer, position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && requestCode == REQUEST_CODE_TIME)
        {
            String name = data.getStringExtra("name");
            String startTime = data.getStringExtra("startTime");
            String endTime = data.getStringExtra("endTime");
            boolean sunday = data.getBooleanExtra("sunday", false);
            boolean monday = data.getBooleanExtra("monday", false);
            boolean tuesday = data.getBooleanExtra("tuesday", false);
            boolean wednesday = data.getBooleanExtra("wednesday", false);
            boolean thursday = data.getBooleanExtra("thursday", false);
            boolean friday = data.getBooleanExtra("friday", false);
            boolean saturday = data.getBooleanExtra("saturday", false);
            boolean isChecked = data.getBooleanExtra("isChecked", false);
            int position = data.getIntExtra("position", -1);

            Silencer silencer = new Silencer(name, startTime, endTime, sunday, monday,
                    tuesday, wednesday, thursday, friday, saturday, isChecked);

            timeFragment.refreshSilencer(silencer, position);
        }
    }

    public void fragmentTransaction(final int container, final Fragment fragment, final String tag, final int switchVisibility)
    {
        mDrawerLayout.closeDrawers();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                getFragmentManager()
                        .beginTransaction()
                        .replace(container, fragment, tag)
                        .commit();

            }
        }, 500);
    }
    public boolean isLargeScreen()
    {
        final int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

}
