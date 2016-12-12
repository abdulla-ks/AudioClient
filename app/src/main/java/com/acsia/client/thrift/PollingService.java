package com.acsia.client.thrift;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.acsia.client.support.ClientApplication;
import com.acsia.client.support.Constants;
import com.acsia.client.ui.MainActivity;

/**
 * Created by Acsia on 12/12/2016.
 */

public class PollingService extends BroadcastReceiver {
    private static final String TAG = PollingService.class.getSimpleName();
    private static boolean started = false;


    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.i(TAG, "onReceive Starting service @ " + SystemClock.elapsedRealtime());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (started) {
                    try {
                        Thread.sleep(Constants.INTERVAL_POLLING_MILLIS);
                        new PollingTask().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    class PollingTask extends AsyncTask<Void, Void, Boolean> {
        int localMaxVolume = -1;
        int localVolume = -1;
        int localMuteStatus = -1;
        int remoteMaxVolume = -1;
        int remoteVolume = -1;
        int remoteMuteStatus = -1;

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success = false;
            try {
                if (AudioManager.getInstance().isClientOpen(AudioManager.Device.LOCAL)
                        && AudioManager.getInstance().isClientOpen(AudioManager.Device.REMOTE)) {
                    try {
                        localMaxVolume = AudioManager.getMaximumVolume(AudioManager.Device.LOCAL);
                        localVolume = AudioManager.getCurrentVolume(AudioManager.Device.LOCAL);
                        localMuteStatus = AudioManager.isMute(AudioManager.Device.LOCAL);
                        remoteMaxVolume = AudioManager.getMaximumVolume(AudioManager.Device.REMOTE);
                        remoteVolume = AudioManager.getCurrentVolume(AudioManager.Device.REMOTE);
                        remoteMuteStatus = AudioManager.isMute(AudioManager.Device.REMOTE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (localMaxVolume != -1 && remoteMaxVolume != -1
                            && localVolume != -1 && remoteVolume != -1
                            && localMuteStatus != -1 && remoteMuteStatus != -1) {
                        success = true;
                    }
                } else {
                    success = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            Intent intent = new Intent(Constants.THRIFT_RECEIVER);
            // Put the random number to intent to broadcast it
            intent.putExtra(Constants.THRIFT_DATA_SUCCESS, success);
            if (success) {
                intent.putExtra(Constants.THRIFT_DATA_LOCAL_MAX_VOLUME, localMaxVolume);
                intent.putExtra(Constants.THRIFT_DATA_LOCAL_VOLUME, localVolume);
                intent.putExtra(Constants.THRIFT_DATA_LOCAL_MUTE_STATUS, localMuteStatus);
                intent.putExtra(Constants.THRIFT_DATA_REMOTE_MAX_VOLUME, remoteMaxVolume);
                intent.putExtra(Constants.THRIFT_DATA_REMOTE_VOLUME, remoteVolume);
                intent.putExtra(Constants.THRIFT_DATA_REMOTE_MUTE_STATUS, remoteMuteStatus);
            }
            // Send the broadcast
            ClientApplication.getLocalBroadcastManager().sendBroadcast(intent);
        }
    }

    public static void startPollingService(Context context) {
        started = true;
        Intent intent = new Intent(context, PollingService.class);
        context.sendBroadcast(intent);
       /* AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PollingService.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        intent.setAction("start");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 30 seconds

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.INTERVAL_MILLIS, pi);*/
    }

    public static void stopPollingService(Context context) {
        started = false;
       /* Intent intent = new Intent(context, PollingService.class);
        intent.setAction("stop");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);*/
    }
}