package com.nash.wifidemo;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity" ;
    private Button mEnableButton, mDisableButton, mConnectButton;

    private WifiManager mWM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar_1);
        setSupportActionBar(myToolbar);

        mEnableButton = findViewById(R.id.enableBtn);
        mDisableButton = findViewById(R.id.disableBtn);
        mConnectButton = findViewById(R.id.connectBtn);

        mWM = (WifiManager) getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);

        mEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWM.setWifiEnabled(true);
                Log.i(TAG,"WIFI Enabled");
            }
        });

        mDisableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWM.setWifiEnabled(false);
                Log.i(TAG,"WIFI Disabled");
            }
        });

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWM.isWifiEnabled()) {
                    startActivity(new Intent(getApplicationContext(), CommandActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Turn wifi on!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
