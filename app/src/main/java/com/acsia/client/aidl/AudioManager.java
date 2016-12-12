package com.acsia.client.aidl;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.acsia.client.support.ClientApplication;
import com.acsia.client.support.Constants;
import com.acsia.server.IAudioService;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Acsia on 12/9/2016.
 */

public class AudioManager extends Observable {

    private static ServiceConnection serviceConnection;
    private static IAudioService audioService;
    private static AudioManager instance;

    public static void initConnection(final Observer observer) {
        if (observer != null) {
            getInstance().deleteObservers();
            getInstance().addObserver(observer);
        }
        System.out.println("initConnection ");
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // TODO Auto-generated method stub
                    audioService = null;
                    Log.d("IAudioService", "Binding - AudioService disconnected");
                    getInstance().setChanged();
                    getInstance().notifyObservers(Constants.Status.FAIL);
                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    // TODO Auto-generated method stub
                    audioService = IAudioService.Stub.asInterface((IBinder) service);
                    Log.d("IAudioService", "Binding is done - AudioService connected");
                    getInstance().setChanged();
                    getInstance().notifyObservers(Constants.Status.SUCCESS);
                }
            };
        }
       /* if (audioService == null) {
            Intent bindIntent = new Intent("server.intent.action.BIND_AUDIO_SERVICE");
            bindIntent.setClassName("com.acsia.server", "com.acsia.server.aidl.AudioService");
            ClientApplication.getInstance().bindService(bindIntent, serviceConnection, Service.BIND_AUTO_CREATE);
        }*/
        if (audioService == null) {
            Intent it = new Intent("server.AudioService");
            it.setPackage("com.acsia.server");
            // binding to remote service
            ClientApplication.getInstance().bindService(it, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }

    public static int getMaximumVolume() {
        int volume = -1;
        if (audioService == null) {
            initConnection(null);
        }
        try {
            if (audioService != null) {
                volume = audioService.getMaxAudioLevel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volume;
    }

    public static int getCurrentVolume() {
        int volume = -1;
        if (audioService == null) {
            initConnection(null);
        }
        try {
            if (audioService != null) {
                volume = audioService.getAudioLevel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volume;
    }

    public static boolean setVolume(int progress) {
        boolean success = false;
        if (audioService == null) {
            initConnection(null);
        }
        try {
            if (audioService != null) {
                success = audioService.setAudioLevel(progress, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static int isMute() {
        int volume = -1;
        if (audioService == null) {
            initConnection(null);
        }
        try {
            if (audioService != null) {
                if (audioService.isMute()) {
                    volume = 0;
                } else {
                    volume = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volume;
    }

    public static boolean muteAudio(boolean checked) {
        boolean success = false;
        if (audioService == null) {
            initConnection(null);
        }
        try {
            if (audioService != null) {
                success = audioService.muteAudio(checked);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }


    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
}
