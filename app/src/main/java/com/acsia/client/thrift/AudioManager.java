package com.acsia.client.thrift;

import com.acsia.client.support.Constants;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by Acsia on 12/9/2016.
 */
public class AudioManager {
    private static AudioManager ourInstance = new AudioManager();


    public enum Device {
        LOCAL,
        REMOTE
    }

    public static AudioManager getInstance() {
        return ourInstance;
    }

    private static TTransport localTransport;
    private static TProtocol localProtocol;
    private static AudioService.Client localClient;

    private static TTransport remoteTransport;
    private static TProtocol remoteProtocol;
    private static AudioService.Client remoteClient;


    public void startClient(Device local) {
        if (local == Device.LOCAL) {
            startLocalClient();
        } else {
            startRemoteClient();
        }
    }

    private void startLocalClient() {
        try {
            if (localTransport == null) {
                localTransport = new TSocket(Constants.LOCAL_HOST, 9090);
                localTransport.open();
                //   if (localProtocol == null) {
                localProtocol = new TBinaryProtocol(localTransport);
                localClient = new AudioService.Client(localProtocol);
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void startRemoteClient() {
        try {
            if (remoteTransport == null) {
                remoteTransport = new TSocket(Constants.REMOTE_HOST, 9090);
                remoteTransport.open();
                //  if (remoteProtocol == null) {
                remoteProtocol = new TBinaryProtocol(remoteTransport);
                remoteClient = new AudioService.Client(remoteProtocol);
                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isClientOpen(Device device) {
        if (device == Device.LOCAL) {
            return isLocalClientOpen();
        } else {
            return isRemoteClientOpen();
        }
    }

    private boolean isRemoteClientOpen() {
        boolean isOpen = false;
        if (remoteTransport != null) {
            if (remoteTransport.isOpen()) {
                isOpen = true;
            } else {
                remoteTransport = null;
            }
        }
        return isOpen;
    }

    private boolean isLocalClientOpen() {
        boolean isOpen = false;
        if (localTransport != null) {
            if (localTransport.isOpen()) {
                isOpen = true;
            } else {
                localTransport = null;
            }
        }
        return isOpen;
    }

    public static int getMaximumVolume(Device device) {
        int maxVolume = -1;
        try {
            if (!getInstance().isClientOpen(device)) {
                getInstance().startClient(device);
            }
            maxVolume = getInstance().getClient(device).getMaxAudioLevel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxVolume;
    }


    public static int getCurrentVolume(Device device) {
        int volume = -1;
        try {
            if (!getInstance().isClientOpen(device)) {
                getInstance().startClient(device);
            }
            volume = getInstance().getClient(device).getAudioLevel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volume;
    }

    public static boolean setVolume(int volume, Device device) {
        boolean success = false;
        try {
            if (!getInstance().isClientOpen(device)) {
                getInstance().startClient(device);
            }

            success = getInstance().getClient(device).setAudioLevel(volume, isHeadUnit(device));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }


    public static int isMute(Device device) {
        int status = -1;
        try {
            if (!getInstance().isClientOpen(device)) {
                getInstance().startClient(device);
            }
            if (getInstance().getClient(device).isMute()) {
                status = 0;
            } else {
                status = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }


    public static boolean muteAudio(boolean mute, Device device) {
        boolean success = false;
        try {
            if (!getInstance().isClientOpen(device)) {
                getInstance().startClient(device);
            }
            success = getInstance().getClient(device).muteAudio(mute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    private static boolean isHeadUnit(Device device) {
        if (device == Device.LOCAL) {
            return false;
        } else {
            return true;
        }
    }

    private AudioService.Client getClient(Device device) {
        if (device == Device.LOCAL) {
            return getInstance().localClient;
        } else {
            return getInstance().remoteClient;
        }
    }

}
