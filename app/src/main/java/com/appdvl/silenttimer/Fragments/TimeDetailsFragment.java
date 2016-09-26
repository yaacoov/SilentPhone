package com.appdvl.silenttimer.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appdvl.silenttimer.Activities.MainActivity;
import com.appdvl.silenttimer.Models.Silencer;
import com.appdvl.silenttimer.R;
import com.appdvl.silenttimer.Services.SilentTimeService;
import com.appdvl.silenttimer.Utils.SilentContentProvider;

import java.util.Calendar;


public class TimeDetailsFragment extends Fragment implements View.OnClickListener
{
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane = false;
    private int updatePosition = -1;

    CheckedTextView sundayTextView;
    CheckedTextView mondayTextView;
    CheckedTextView tuesdayTextView;
    CheckedTextView wednesdayTextView;
    CheckedTextView thursdayTextView;
    CheckedTextView fridayTextView;
    CheckedTextView saturdayTextView;
    SwitchCompat silencerSwitch;
    EditText silencerNameEditText;
    TextView startTimeTextView;
    TextView endTimeTextView;
    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f);
    PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f);
    ObjectAnimator scaleAnim;
    ObjectAnimator fadeInAnim;
    ObjectAnimator fadeOutAnim;
    TextView toLabelTextView;
    TextView startEndLabel;
    TextView repeatLabel;
    TextView setButton;

    private int intentID = 0;
    private int where_id = 0;

    private static final int INTENT_UPDATE = 2;

    float alphaOpaque = 1f;
    float alphaTransparent = 0.3f;

    Intent intent;
    Bundle arguments;

    SilentTimeService alarm = new SilentTimeService();

    refreshRecyclerView mCallback;

    public interface refreshRecyclerView
    {
        void getSilencer(Silencer silencer, int position);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimeDetailsFragment()
    {

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        intent = getActivity().getIntent();
        mTwoPane = intent.getBooleanExtra("isTwoPane", mTwoPane);

        arguments = this.getArguments();
        mTwoPane = arguments.getBoolean("isTwoPane", mTwoPane);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if(mTwoPane)
        {
            updatePosition = arguments.getInt("update_position", updatePosition);

            try
            {
                mCallback = (refreshRecyclerView) activity;
            }
            catch (ClassCastException e)
            {
                throw new ClassCastException(activity.toString() + " must implement refreshRecyclerView");
            }
        }
        else
        {
            updatePosition = intent.getIntExtra("update_position", updatePosition);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int argsUpdateID;
        String argsUpdateName;
        String argsUpdateStartTime;
        String argsUpdateEndTime;
        int argsUpdateSun;
        int argsUpdateMon;
        int argsUpdateTue;
        int argsUpdateWed;
        int argsUpdateThu;
        int argsUpdateFri;
        int argsUpdateSat;
        int argsUpdateIsChecked;

        View rootView = inflater.inflate(R.layout.fragment_new_silencer, container, false);

        if (mTwoPane)
        {
            intentID = getArguments().getInt("intentID", 0);
            argsUpdateID = getArguments().getInt("update_id", 0);
            argsUpdateName = getArguments().getString("update_name");
            argsUpdateStartTime = getArguments().getString("update_startTime");
            argsUpdateEndTime = getArguments().getString("update_endTime");
            argsUpdateSun = getArguments().getInt("update_sunday", 0);
            argsUpdateMon = getArguments().getInt("update_monday", 0);
            argsUpdateTue = getArguments().getInt("update_tuesday", 0);
            argsUpdateWed = getArguments().getInt("update_wednesday", 0);
            argsUpdateThu = getArguments().getInt("update_thursday", 0);
            argsUpdateFri = getArguments().getInt("update_friday", 0);
            argsUpdateSat = getArguments().getInt("update_saturday", 0);
            argsUpdateIsChecked = getArguments().getInt("update_isChecked", 0);

            silencerNameEditText = (EditText) rootView.findViewById(R.id.details_name_edittext);
            silencerSwitch = (SwitchCompat) rootView.findViewById(R.id.details_silencer_switch);
        }
        else
        {
            intentID = intent.getIntExtra("intentID", 0);
            argsUpdateID = intent.getIntExtra("update_id", 0);
            argsUpdateName = intent.getStringExtra("update_name");
            argsUpdateStartTime = intent.getStringExtra("update_startTime");
            argsUpdateEndTime = intent.getStringExtra("update_endTime");
            argsUpdateSun = intent.getIntExtra("update_sunday", 0);
            argsUpdateMon = intent.getIntExtra("update_monday", 0);
            argsUpdateTue = intent.getIntExtra("update_tuesday", 0);
            argsUpdateWed = intent.getIntExtra("update_wednesday", 0);
            argsUpdateThu = intent.getIntExtra("update_thursday", 0);
            argsUpdateFri = intent.getIntExtra("update_friday", 0);
            argsUpdateSat = intent.getIntExtra("update_saturday", 0);
            argsUpdateIsChecked = intent.getIntExtra("update_isChecked", 0);

            silencerNameEditText = (EditText) getActivity().findViewById(R.id.details_name_edittext);
            silencerSwitch = (SwitchCompat) getActivity().findViewById(R.id.details_silencer_switch);
        }

        Typeface robotoMediumFont = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        Typeface robotoLightFont = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        Typeface geoSansLightFont = Typeface.createFromAsset(getActivity().getAssets(), "GeosansLight.ttf");

        silencerNameEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        silencerNameEditText.setTypeface(robotoMediumFont);
        silencerNameEditText.setSingleLine(true);
        silencerNameEditText.setHintTextColor(getResources().getColor(R.color.color_background));
        silencerNameEditText.setTextColor(getResources().getColor(R.color.color_primary_accent));

        startEndLabel = (TextView)rootView.findViewById(R.id.start_end_textview);
        startEndLabel.setTypeface(robotoMediumFont);

        startTimeTextView = (TextView)rootView.findViewById(R.id.details_start_time_text_view);
        startTimeTextView.setOnClickListener(this);
        startTimeTextView.setTypeface(geoSansLightFont);
        updateTimeDisplay(startTimeTextView);

        toLabelTextView = (TextView)rootView.findViewById(R.id.label_to);
        toLabelTextView.setTypeface(geoSansLightFont);

        endTimeTextView = (TextView)rootView.findViewById((R.id.details_end_time_text_view));
        endTimeTextView.setOnClickListener(this);
        endTimeTextView.setTypeface(geoSansLightFont);
        updateTimeDisplay(endTimeTextView);

        repeatLabel = (TextView)rootView.findViewById(R.id.repeat_textview);
        repeatLabel.setTypeface(robotoMediumFont);

        sundayTextView = (CheckedTextView)rootView.findViewById(R.id.txt_sunday);
        sundayTextView.setOnClickListener(this);
        sundayTextView.setTypeface(robotoLightFont);
        sundayTextView.setAlpha(alphaTransparent);

        mondayTextView = (CheckedTextView)rootView.findViewById(R.id.txt_monday);
        mondayTextView.setOnClickListener(this);
        mondayTextView.setTypeface(robotoLightFont);
        mondayTextView.setAlpha(alphaTransparent);

        tuesdayTextView = (CheckedTextView)rootView.findViewById(R.id.txt_tuesday);
        tuesdayTextView.setOnClickListener(this);
        tuesdayTextView.setTypeface(robotoLightFont);
        tuesdayTextView.setAlpha(alphaTransparent);

        wednesdayTextView = (CheckedTextView)rootView.findViewById(R.id.txt_wednesday);
        wednesdayTextView.setOnClickListener(this);
        wednesdayTextView.setTypeface(robotoLightFont);
        wednesdayTextView.setAlpha(alphaTransparent);

        thursdayTextView = (CheckedTextView)rootView.findViewById(R.id.txt_thursday);
        thursdayTextView.setOnClickListener(this);
        thursdayTextView.setTypeface(robotoLightFont);
        thursdayTextView.setAlpha(alphaTransparent);

        fridayTextView = (CheckedTextView)rootView.findViewById(R.id.txt_friday);
        fridayTextView.setOnClickListener(this);
        fridayTextView.setTypeface(robotoLightFont);
        fridayTextView.setAlpha(alphaTransparent);

        saturdayTextView = (CheckedTextView)rootView.findViewById(R.id.txt_saturday);
        saturdayTextView.setOnClickListener(this);
        saturdayTextView.setTypeface(robotoLightFont);
        saturdayTextView.setAlpha(alphaTransparent);

        setButton = (TextView)rootView.findViewById(R.id.details_set_button);
        setButton.setOnClickListener(this);

        if (intentID == INTENT_UPDATE)
        {
            where_id = argsUpdateID;

            silencerNameEditText.setText(argsUpdateName);

            startTimeTextView.setText(argsUpdateStartTime);

            endTimeTextView.setText(argsUpdateEndTime);

            sundayTextView.setChecked(booleanValue(argsUpdateSun));
            sundayTextView.setAlpha(setAlphaValue(sundayTextView));

            mondayTextView.setChecked(booleanValue(argsUpdateMon));
            mondayTextView.setAlpha(setAlphaValue(mondayTextView));

            tuesdayTextView.setChecked(booleanValue(argsUpdateTue));
            tuesdayTextView.setAlpha(setAlphaValue(tuesdayTextView));

            wednesdayTextView.setChecked(booleanValue(argsUpdateWed));
            wednesdayTextView.setAlpha(setAlphaValue(wednesdayTextView));

            thursdayTextView.setChecked(booleanValue(argsUpdateThu));
            thursdayTextView.setAlpha(setAlphaValue(thursdayTextView));

            fridayTextView.setChecked(booleanValue(argsUpdateFri));
            fridayTextView.setAlpha(setAlphaValue(fridayTextView));

            saturdayTextView.setChecked(booleanValue(argsUpdateSat));
            saturdayTextView.setAlpha(setAlphaValue(saturdayTextView));

            silencerSwitch.setChecked(booleanValue(argsUpdateIsChecked));

            setButton.setText(R.string.btn_update_silencer);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateTimeDisplay(TextView textView)
    {
        SilentTimeService service = new SilentTimeService();

        final Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        textView.setText(new StringBuilder()
                .append(service.pad(hour)).append(":")
                .append(service.pad(minute)));
    }

    public void showStartTimePicker()
    {
        FragmentManager fm = getActivity().getFragmentManager();
        DialogFragment startTimeDialogFragment = new TimePickerFragment();
        startTimeDialogFragment.show(fm, "startTimePicker");
    }

    public void showEndTimePicker()
    {
        FragmentManager fm = getActivity().getFragmentManager();
        DialogFragment endTimeDialogFragment = new TimePickerFragment();
        endTimeDialogFragment.show(fm, "endTimePicker");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.details_start_time_text_view:
            {
                showStartTimePicker();
                break;
            }
            case R.id.details_end_time_text_view:
            {
                showEndTimePicker();
                break;
            }
            case R.id.txt_sunday:
            {
                setUpAnimations(sundayTextView);
                break;
            }
            case R.id.txt_monday:
            {
                setUpAnimations(mondayTextView);
                break;
            }
            case R.id.txt_tuesday:
            {
                setUpAnimations(tuesdayTextView);
                break;
            }
            case R.id.txt_wednesday:
            {
                setUpAnimations(wednesdayTextView);
                break;
            }
            case R.id.txt_thursday:
            {
                setUpAnimations(thursdayTextView);
                break;
            }
            case R.id.txt_friday:
            {
                setUpAnimations(fridayTextView);
                break;
            }
            case R.id.txt_saturday:
            {
                setUpAnimations(saturdayTextView);
                break;
            }
            case R.id.details_set_button:
            {
                String silencerName = silencerNameEditText.getText().toString();
                String startTime = startTimeTextView.getText().toString();
                String endTime = endTimeTextView.getText().toString();
                boolean sunday = sundayTextView.isChecked();
                boolean monday = mondayTextView.isChecked();
                boolean tuesday = tuesdayTextView.isChecked();
                boolean wednesday = wednesdayTextView.isChecked();
                boolean thursday = thursdayTextView.isChecked();
                boolean friday = fridayTextView.isChecked();
                boolean saturday = saturdayTextView.isChecked();
                boolean isChecked = silencerSwitch.isChecked();

                Silencer silencer = new Silencer( silencerName, startTime, endTime, sunday,
                        monday, tuesday, wednesday, thursday, friday, saturday, isChecked);

                if (!silencerName.equals(""))
                {
                    if (mTwoPane)
                    {
                        if (intentID == 1)
                        {
                            addNewSilencer(silencer);
                            Toast.makeText(getActivity().getApplicationContext(), "New silencer set.", Toast.LENGTH_SHORT).show();
                        }
                        else if (intentID == 2)
                        {
                            updateSilencer(silencer, where_id);
                            Toast.makeText(getActivity().getApplicationContext(), "Silencer updated.", Toast.LENGTH_SHORT).show();
                        }

                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        mCallback.getSilencer(silencer, updatePosition);
                    }
                    else
                    {
                        if (intentID == 1)
                        {
                            addNewSilencer(silencer);
                            Toast.makeText(getActivity().getApplicationContext(), "New silencer set.", Toast.LENGTH_SHORT).show();
                        }
                        else if (intentID == 2)
                        {
                            updateSilencer(silencer, where_id);
                            Toast.makeText(getActivity().getApplicationContext(), "Silencer updated.", Toast.LENGTH_SHORT).show();
                        }

                        Intent data = new Intent();
                        data.putExtra("name", silencerName);
                        data.putExtra("startTime", startTime);
                        data.putExtra("endTime", endTime);
                        data.putExtra("sunday", sunday);
                        data.putExtra("monday", monday);
                        data.putExtra("tuesday", thursday);
                        data.putExtra("wednesday", wednesday);
                        data.putExtra("thursday", thursday);
                        data.putExtra("friday", friday);
                        data.putExtra("saturday", saturday);
                        data.putExtra("isChecked", isChecked);
                        data.putExtra("position", updatePosition);
                        getActivity().setResult(MainActivity.REQUEST_CODE_TIME, data);
                        getActivity().finish();
                    }
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter description.", Toast.LENGTH_SHORT).show();
                    silencerNameEditText.setFocusableInTouchMode(true);
                    silencerNameEditText.requestFocus();
                }
            }
        }
    }

    private void addNewSilencer(Silencer silencer)
    {
        ContentResolver cr = getActivity().getContentResolver();

        Cursor query = cr.query(SilentContentProvider.SILENCER_CONTENT_URI, null, null, null, null);

        ContentValues values = new ContentValues();

        values.put(SilentContentProvider.SILENCER_NAME, silencer.getSilencerName());
        values.put(SilentContentProvider.SILENCER_START_TIME, silencer.getStartTime());
        values.put(SilentContentProvider.SILENCER_END_TIME, silencer.getEndTime());
        values.put(SilentContentProvider.SILENCER_SUNDAY, silencer.getSunday());
        values.put(SilentContentProvider.SILENCER_MONDAY, silencer.getMonday());
        values.put(SilentContentProvider.SILENCER_TUESDAY, silencer.getTuesday());
        values.put(SilentContentProvider.SILENCER_WEDNESDAY, silencer.getWednesday());
        values.put(SilentContentProvider.SILENCER_THURSDAY, silencer.getThursday());
        values.put(SilentContentProvider.SILENCER_FRIDAY, silencer.getFriday());
        values.put(SilentContentProvider.SILENCER_SATURDAY, silencer.getSaturday());
        values.put(SilentContentProvider.SILENCER_IS_CHECKED, silencer.getIsChecked());

        int rowID = (int) ContentUris.parseId(cr.insert(SilentContentProvider.SILENCER_CONTENT_URI, values));

        int startHour = hour(silencer.getStartTime());
        int startMinute =  minutes(silencer.getStartTime());
        int endHour = hour(silencer.getEndTime());
        int endMinute = minutes(silencer.getEndTime());

        if (silencer.getIsChecked())
        {
            setAlarmForDays(getActivity().getApplicationContext(),
                    silencer.getSunday(),
                    silencer.getMonday(),
                    silencer.getTuesday(),
                    silencer.getWednesday(),
                    silencer.getThursday(),
                    silencer.getFriday(),
                    silencer.getSaturday(),
                    rowID,
                    startHour,
                    startMinute,
                    endHour,
                    endMinute,
                    silencer.getEndTime());
        }

        query.close();
    }

    private void updateSilencer(Silencer silencer, int id)
    {
        ContentResolver cr = getActivity().getContentResolver();

        Cursor query = cr.query(SilentContentProvider.SILENCER_CONTENT_URI, null, null, null, null);

        ContentValues values = new ContentValues();

        values.put(SilentContentProvider.SILENCER_NAME, silencer.getSilencerName());
        values.put(SilentContentProvider.SILENCER_START_TIME, silencer.getStartTime());
        values.put(SilentContentProvider.SILENCER_END_TIME, silencer.getEndTime());
        values.put(SilentContentProvider.SILENCER_SUNDAY, silencer.getSunday());
        values.put(SilentContentProvider.SILENCER_MONDAY, silencer.getMonday());
        values.put(SilentContentProvider.SILENCER_TUESDAY, silencer.getTuesday());
        values.put(SilentContentProvider.SILENCER_WEDNESDAY, silencer.getWednesday());
        values.put(SilentContentProvider.SILENCER_THURSDAY, silencer.getThursday());
        values.put(SilentContentProvider.SILENCER_FRIDAY, silencer.getFriday());
        values.put(SilentContentProvider.SILENCER_SATURDAY, silencer.getSaturday());
        values.put(SilentContentProvider.SILENCER_IS_CHECKED, silencer.getIsChecked());

        cr.update(SilentContentProvider.SILENCER_CONTENT_URI, values, " _id" + " = " + id, null);

        int startHour = hour(silencer.getStartTime());
        int startMinute =  minutes(silencer.getStartTime());
        int endHour = hour(silencer.getEndTime());
        int endMinute = minutes(silencer.getEndTime());

        if (silencer.getIsChecked())
        {
            setAlarmForDays(getActivity().getApplicationContext(),
                    silencer.getSunday(),
                    silencer.getMonday(),
                    silencer.getTuesday(),
                    silencer.getWednesday(),
                    silencer.getThursday(),
                    silencer.getFriday(),
                    silencer.getSaturday(),
                    id,
                    startHour,
                    startMinute,
                    endHour,
                    endMinute,
                    silencer.getEndTime());
        }
        else if (!silencer.getIsChecked())
        {
            cancelAlarmForDays(getActivity().getApplicationContext(),
                    silencer.getSunday(),
                    silencer.getMonday(),
                    silencer.getTuesday(),
                    silencer.getWednesday(),
                    silencer.getThursday(),
                    silencer.getFriday(),
                    silencer.getSaturday(), id);
        }

        query.close();
    }

    public void setAlarmForDays(Context context, boolean sunday, boolean monday,
                                boolean tuesday, boolean wednesday, boolean thursday,
                                boolean friday, boolean saturday, int id, int startHour,
                                int startMinute, int endHour, int endMinute, String notifTime)
    {
        int startAlarmID;
        int endAlarmID;
        int multiplicand = 1000;

        if (sunday)
        {
            startAlarmID = concatenate(id, Calendar.SUNDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.setAlarmClock(context, startAlarmID, Calendar.SUNDAY, startHour, startMinute, notifTime);
            alarm.setAlarmClock(context, endAlarmID, Calendar.SUNDAY, endHour, endMinute, notifTime);
        }

        if (monday)
        {
            startAlarmID = concatenate(id, Calendar.MONDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.setAlarmClock(context, startAlarmID, Calendar.MONDAY, startHour, startMinute, notifTime);
            alarm.setAlarmClock(context, endAlarmID, Calendar.MONDAY, endHour, endMinute, notifTime);
        }

        if (tuesday)
        {
            startAlarmID = concatenate(id, Calendar.TUESDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.setAlarmClock(context, startAlarmID, Calendar.TUESDAY, startHour, startMinute, notifTime);
            alarm.setAlarmClock(context, endAlarmID, Calendar.TUESDAY, endHour, endMinute, notifTime);
        }

        if (wednesday)
        {
            startAlarmID = concatenate(id, Calendar.WEDNESDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.setAlarmClock(context, startAlarmID, Calendar.WEDNESDAY, startHour, startMinute, notifTime);
            alarm.setAlarmClock(context, endAlarmID, Calendar.WEDNESDAY, endHour, endMinute, notifTime);
        }

        if (thursday)
        {
            startAlarmID = concatenate(id, Calendar.THURSDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.setAlarmClock(context, startAlarmID, Calendar.THURSDAY, startHour, startMinute, notifTime);
            alarm.setAlarmClock(context, endAlarmID, Calendar.THURSDAY, endHour, endMinute, notifTime);
        }

        if (friday)
        {
            startAlarmID = concatenate(id, Calendar.FRIDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.setAlarmClock(context, startAlarmID, Calendar.FRIDAY, startHour, startMinute, notifTime);
            alarm.setAlarmClock(context, endAlarmID, Calendar.FRIDAY, endHour, endMinute, notifTime);
        }

        if (saturday)
        {
            startAlarmID = concatenate(id, Calendar.SATURDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.setAlarmClock(context, startAlarmID, Calendar.SATURDAY, startHour, startMinute, notifTime);
            alarm.setAlarmClock(context, endAlarmID, Calendar.SATURDAY, endHour, endMinute, notifTime);
        }
    }

    public void cancelAlarmForDays(Context context, boolean sunday, boolean monday, boolean tuesday,
                                   boolean wednesday, boolean thursday, boolean friday, boolean saturday, int id)
    {
        int startAlarmID;
        int endAlarmID;
        int multiplicand = 1000;

        if (sunday)
        {
            startAlarmID = concatenate(id, Calendar.SUNDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.cancelAlarm(context, startAlarmID);
            alarm.cancelAlarm(context, endAlarmID);
        }

        if (monday)
        {
            startAlarmID = concatenate(id, Calendar.MONDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.cancelAlarm(context, startAlarmID);
            alarm.cancelAlarm(context, endAlarmID);
        }

        if (tuesday)
        {
            startAlarmID = concatenate(id, Calendar.TUESDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.cancelAlarm(context, startAlarmID);
            alarm.cancelAlarm(context, endAlarmID);
        }

        if (wednesday)
        {
            startAlarmID = concatenate(id, Calendar.WEDNESDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.cancelAlarm(context, startAlarmID);
            alarm.cancelAlarm(context, endAlarmID);
        }

        if (thursday)
        {
            startAlarmID = concatenate(id, Calendar.THURSDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.cancelAlarm(context, startAlarmID);
            alarm.cancelAlarm(context, endAlarmID);
        }

        if (friday)
        {
            startAlarmID = concatenate(id, Calendar.FRIDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.cancelAlarm(context, startAlarmID);
            alarm.cancelAlarm(context, endAlarmID);
        }

        if (saturday)
        {
            startAlarmID = concatenate(id, Calendar.SATURDAY);
            endAlarmID = startAlarmID * multiplicand;
            alarm.cancelAlarm(context, startAlarmID);
            alarm.cancelAlarm(context, endAlarmID);
        }
    }

    private void setUpAnimations(CheckedTextView checkedTextView)
    {
        if(!checkedTextView.isChecked())
        {
            checkedTextView.setChecked(true);
        }
        else
        {
            checkedTextView.setChecked(false);
        }

        scaleAnim = ObjectAnimator.ofPropertyValuesHolder(checkedTextView, pvhX, pvhY);
        scaleAnim.setRepeatCount(1);
        scaleAnim.setRepeatMode(ValueAnimator.REVERSE);
        fadeInAnim = ObjectAnimator.ofFloat(checkedTextView, "Alpha", alphaTransparent, alphaOpaque);
        fadeOutAnim = ObjectAnimator.ofFloat(checkedTextView, "Alpha", alphaOpaque, alphaTransparent);

        if(checkedTextView.isChecked())
        {
            fadeInAnim.start();
        }
        else
        {
            fadeOutAnim.start();
        }
        scaleAnim.start();
    }

    public float setAlphaValue(CheckedTextView checkedTextView)
    {
        if(!checkedTextView.isChecked())
        {
            return alphaTransparent;
        }
        else
        {
            return alphaOpaque;
        }
    }

    private boolean booleanValue(int intValue)
    {
        return intValue == 1;
    }

    public int hour(String time)
    {
        String c = time.substring(0, 2);
        return Integer.parseInt(c);
    }

    public int minutes(String time)
    {
        String c = time.substring(3);
        return Integer.parseInt(c);
    }

    public int concatenate(int first_value, int second_value)
    {
        int concatenated_value;

        concatenated_value = Integer.parseInt(Integer.toString(first_value) + Integer.toString(second_value));

        return concatenated_value;
    }
}