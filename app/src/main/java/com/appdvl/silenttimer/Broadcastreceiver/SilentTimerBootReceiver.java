package com.appdvl.silenttimer.Broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.appdvl.silenttimer.Services.SilentTimeService;
import com.appdvl.silenttimer.Utils.SilentContentProvider;

import java.util.Calendar;


public class SilentTimerBootReceiver extends BroadcastReceiver
{
    String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    String TIME_SET = "android.intent.action.TIME_SET";
    String TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
    String LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED";

    SilentTimeService alarm = new SilentTimeService();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ContentResolver cr = context.getContentResolver();

        String[] columns = new String[]
                {
                        SilentContentProvider.SILENCER_ID,
                        SilentContentProvider.SILENCER_NAME,
                        SilentContentProvider.SILENCER_START_TIME,
                        SilentContentProvider.SILENCER_END_TIME,
                        SilentContentProvider.SILENCER_SUNDAY,
                        SilentContentProvider.SILENCER_MONDAY,
                        SilentContentProvider.SILENCER_TUESDAY,
                        SilentContentProvider.SILENCER_WEDNESDAY,
                        SilentContentProvider.SILENCER_THURSDAY,
                        SilentContentProvider.SILENCER_FRIDAY,
                        SilentContentProvider.SILENCER_SATURDAY,
                        SilentContentProvider.SILENCER_IS_CHECKED
                };

        if (intent.getAction().equals(BOOT_COMPLETED)
                || intent.getAction().equals(TIME_SET)
                || intent.getAction().equals(TIMEZONE_CHANGED)
                || intent.getAction().equals(LOCALE_CHANGED))
        {
            Cursor result = cr.query(SilentContentProvider.SILENCER_CONTENT_URI, columns, null, null, null);

            while (result.moveToNext())
            {
                int id = result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_ID));

                String startTime = result.getString(result.getColumnIndex(SilentContentProvider.SILENCER_START_TIME));
                String endTime = result.getString(result.getColumnIndex(SilentContentProvider.SILENCER_END_TIME));

                int startHour = hour(startTime);
                int startMinute = minute(startTime);
                int endHour = hour(endTime);
                int endMinute = minute(endTime);

                if(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_IS_CHECKED )) == 1)
                {
                    setAlarmForDays(context,
                            toBoolean(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_SUNDAY ))),
                            toBoolean(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_MONDAY ))),
                            toBoolean(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_TUESDAY ))),
                            toBoolean(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_WEDNESDAY ))),
                            toBoolean(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_THURSDAY ))),
                            toBoolean(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_FRIDAY ))),
                            toBoolean(result.getInt(result.getColumnIndex(SilentContentProvider.SILENCER_SATURDAY ))),
                            id,
                            startHour,
                            startMinute,
                            endHour,
                            endMinute,
                            endTime);
                }
            }
        }
    }

    private void setAlarmForDays(Context context, boolean sunday, boolean monday,
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

    public boolean toBoolean(int intValue)
    {
        return intValue == 1;
    }

    public int hour(String time)
    {
        String c = time.substring(0, 2);
        return Integer.parseInt(c);
    }

    public int minute (String time)
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