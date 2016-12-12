package com.acsia.client.support;

/**
 * Created by Acsia on 12/6/2016.
 */

public class Constants {


    public static final String LOCAL_HOST = "localhost";
    public static final String REMOTE_HOST = "192.168.1.150";
    public static final long INTERVAL_MILLIS = 5000;
    public static final long INTERVAL_POLLING_MILLIS = 1000;
    public static final String THRIFT_RECEIVER = "com.acsia.client.THRIFT_RECEIVER";
    public static final String THRIFT_DATA_SUCCESS = "thrift_success";

    public static final String THRIFT_DATA_LOCAL_MAX_VOLUME = "thrift_local_max_volume";
    public static final String THRIFT_DATA_LOCAL_VOLUME = "thrift_local_volume";
    public static final String THRIFT_DATA_LOCAL_MUTE_STATUS = "thrift_local_mute_status";

    public static final String THRIFT_DATA_REMOTE_MAX_VOLUME = "thrift_remote_max_volume";
    public static final String THRIFT_DATA_REMOTE_VOLUME = "thrift_remote_volume";
    public static final String THRIFT_DATA_REMOTE_MUTE_STATUS = "thrift_remote_mute_status";

    public enum Status {
        SUCCESS, FAIL
    }

    public enum ACTION {
        SET_VOLUME, MUTE_VOLUME
    }
}
