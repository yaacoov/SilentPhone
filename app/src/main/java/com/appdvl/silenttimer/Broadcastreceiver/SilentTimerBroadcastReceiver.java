package com.appdvl.silenttimer.Broadcastreceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;

import com.appdvl.silenttimer.Activities.DialogActivity;
import com.appdvl.silenttimer.Services.SilentTimeService;


public class SilentTimerBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION"))
            {
                int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
                int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);

                volumeDifference(newVolume, oldVolume);

                if(newVolume < oldVolume && volumeDifference(newVolume, oldVolume) && am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
                {
                    startActivity(context);
                }
                else if (newVolume <= oldVolume && volumeDifference(newVolume, oldVolume) && am.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
                {
                    startActivity(context);
                }
                else if (newVolume >= 1)
                {
                    final NotificationManager silentNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    silentNotificationManager.cancel(SilentTimeService.NOTIFICATION_ID);

                }
            }
        }
    }

    private void startActivity(Context context)
    {
        Context appContext = context.getApplicationContext();
        Intent dialogActivity = new Intent(appContext, DialogActivity.class);
        dialogActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogActivity);
    }

    private boolean volumeDifference(int newVolume, int oldVolume)
    {
        return !((oldVolume - newVolume) > 1);
    }
}