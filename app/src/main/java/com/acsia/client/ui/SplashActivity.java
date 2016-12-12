package com.acsia.client.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.acsia.client.R;
import com.acsia.client.thrift.InitHandler;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, InitHandler.class);
                intent.setAction("start.splash");
                sendBroadcast(intent);
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitHandler.startClientService(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitHandler.stopClientService(getApplicationContext());
    }

    @Override
    public void onBackPressed() {

    }
}
