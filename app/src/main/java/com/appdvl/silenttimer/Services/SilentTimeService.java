package com.appdvl.silenttimer.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;

import com.appdvl.silenttimer.Activities.MainActivity;
import com.appdvl.silenttimer.Broadcastreceiver.SilentTimerAlarmReceiver;
import com.appdvl.silenttimer.Broadcastreceiver.SilentTimerBootReceiver;
import com.appdvl.silenttimer.R;

import java.util.Calendar;


public class SilentTimeService extends IntentService
{
    public static final String ALARM_ACTION = SilentTimerAlarmReceiver.ACTION_START_SILENCER_SERVICE_ALARM;

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmManager;

    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    public static final int NOTIFICATION_ID = 1;

    public SilentTimeService()
    {
        super("SilentTimeService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String notifTime = intent.getStringExtra("notif_time");
        int alarmID = intent.getIntExtra("alarm_id", 0);
        int restoreVolume = 2;

        setRingerMode(alarmID, restoreVolume, notifTime);
    }

    public void setAlarmClock(Context context, int id, int dayOfWeek, int hour, int minute, String notifTime)
    {
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //Convert one week time in milliseconds
        long WEEK_IN_MILLISECONDS = 86400 * 7 * 1000;

        Intent intent = new Intent(ALARM_ACTION);
        intent.putExtra("alarm_id", id);
        intent.putExtra("notif_time", notifTime);
        alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the alarm's trigger time
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // Set the alarm to fire, according to the device's clock, and to repeat once a week.
        if (calendar.getTimeInMillis() < System.currentTimeMillis())
        {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + WEEK_IN_MILLISECONDS,
                    AlarmManager.INTERVAL_DAY * 7, alarmIntent);
        }
        else
        {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, alarmIntent);
        }

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, SilentTimerBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void setAlarmOnDemand(Context context, int id, int milliseconds, String notifTime)
    {
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(ALARM_ACTION);
        intent.putExtra("alarm_id", id);
        intent.putExtra("notif_time", notifTime);
        alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm's trigger time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MILLISECOND, milliseconds);

        //Set the alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, SilentTimerBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int id)
    {
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // If the alarm has been set, cancel it.
        if (alarmManager != null)
        {
            Intent intent = new Intent(ALARM_ACTION);
            alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, SilentTimerBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    private void setRingerMode(int alarmID, int restoreVolume, String notifTime)
    {
        final AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        if (alarmID < 10000)
        {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            broadcastNotification(notifTime);
        }
        else if (alarmID >= 10000)
        {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, restoreVolume, 0);
            removeNotification();
        }
    }

    public void broadcastNotification(String time)
    {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Ringer silenced until around " + time + ".")
                .setContentIntent(notificationPendingIntent);
        //.setColor(Color.RED)

        // Make the notification not dismissable.
        builder.setOngoing(true);

        // Get an instance of the Notification manager
        NotificationManager silentNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        silentNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void removeNotification()
    {
        // Get an instance of the Notification manager
        NotificationManager silentNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Remove the notification with NOTIFICATION_ID = 1.
        silentNotificationManager.cancel(NOTIFICATION_ID);
    }

    public String pad(int c)
    {
        if (c >= 10)
        {
            return String.valueOf(c);
        }
        else
        {
            return "0" + String.valueOf(c);
        }
    }
}