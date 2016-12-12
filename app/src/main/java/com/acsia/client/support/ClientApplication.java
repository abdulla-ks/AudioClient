package com.acsia.client.support;

import android.app.Application;

/**
 * Created by Acsia on 12/11/2016.
 */

public class ClientApplication extends Application {

    private static ClientApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ClientApplication getInstance() {
        return instance;
    }

}
