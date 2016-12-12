package com.acsia.client.thrift;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.acsia.client.support.Constants;
import com.acsia.client.ui.MainActivity;


/**
 * Created by Acsia on 12/9/2016.
 */

public class InitHandler extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    public static boolean started = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("InitHandler.onReceive");
        if (!started) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!AudioManager.getInstance().isClientOpen(AudioManager.Device.LOCAL)) {
                            AudioManager.getInstance().startClient(AudioManager.Device.LOCAL);
                        }
                        if (!AudioManager.getInstance().isClientOpen(AudioManager.Device.REMOTE)) {
                            AudioManager.getInstance().startClient(AudioManager.Device.REMOTE);
                        }

                        if (AudioManager.getInstance().isClientOpen(AudioManager.Device.LOCAL)
                                && AudioManager.getInstance().isClientOpen(AudioManager.Device.REMOTE)) {//) {//
                            Intent mainIntent = new Intent(context, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(mainIntent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            stopClientService(context);
        }

    }

    public static void startClientService(Context context) {
        started = false;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, InitHandler.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        intent.setAction("start");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 30 seconds

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.INTERVAL_MILLIS, pi);
    }

    public static void stopClientService(Context context) {
        started = true;
        Intent intent = new Intent(context, InitHandler.class);
        intent.setAction("stop");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }



      /*PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
            msgStr.append("One time Timer : ");
        }
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));

        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        //Release the lock
        wl.release();*/
}