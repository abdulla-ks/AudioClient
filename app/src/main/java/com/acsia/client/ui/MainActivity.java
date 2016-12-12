package com.acsia.client.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;

import com.acsia.client.aidl.AudioManager;
import com.acsia.client.support.Constants;
import com.acsia.client.R;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity {

    static boolean aidlConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudioManager.initConnection(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (o instanceof Constants.Status) {
                    Constants.Status status = (Constants.Status) o;
                    if (status == Constants.Status.SUCCESS) {
                        setAidlConnected(true);
                    } else {
                        setAidlConnected(false);
                    }/* else {
                                Toast.makeText(MainActivity.this, "Failed to connect aidl", Toast.LENGTH_SHORT).show();
                            }*/
                }


            }
        });

        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        Button conferenceBtn = (Button) findViewById(R.id.conferenceBtn);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAidlConnected()) {
                    Intent mainIntent = new Intent(MainActivity.this, SettingActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                }


            }
        });
        conferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSkype();
            }
        });
    }

    public static boolean isAidlConnected() {
        return aidlConnected;
    }

    public static void setAidlConnected(boolean aidlConnected) {
        MainActivity.aidlConnected = aidlConnected;
    }

    public void openSkype() {
        // Make sure the Skype for Android client is installed.
        if (!isSkypeClientInstalled()) {
            goToMarket();
            return;
        }

        // Create the Intent from our Skype URI.
        Intent myIntent = new Intent("android.intent.action.VIEW");

        // Restrict the Intent to being handled by the Skype for Android client only.
        myIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Initiate the Intent. It should never fail because you've already established the
        // presence of its handler (although there is an extremely minute window where that
        // handler can go away).
        startActivity(myIntent);

        return;
    }

    /**
     * Determine whether the Skype for Android client is installed on this device.
     */
    public boolean isSkypeClientInstalled() {
        PackageManager myPackageMgr = getPackageManager();
        try {
            myPackageMgr.getPackageInfo("com.skype.raider", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }

    /**
     * Install the Skype client through the market: URI scheme.
     */
    public void goToMarket() {
        Uri marketUri = Uri.parse("market://details?id=com.skype.raider");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);

        return;
    }

}
