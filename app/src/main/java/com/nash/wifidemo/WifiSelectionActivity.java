package com.nash.wifidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class WifiSelectionActivity extends AppCompatActivity {

    private static final String TAG = "WifiSelectionActivity";

    private WifiManager mWM;
    private ArrayList<String> mDeviceList = new ArrayList<>();

    private ListView mListView;
    ArrayAdapter<String> mWifiDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_selection);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        mWM = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mListView = findViewById(R.id.device_list);

        mWifiDevicesAdapter = new ArrayAdapter<>(this, R.layout.single_list_view,
                R.id.deviceID, mDeviceList);
        mListView.setAdapter(mWifiDevicesAdapter);

        if(mWM.isWifiEnabled()){
            enumerateWifiDevices();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceName = mDeviceList.get(position);
                Log.i(TAG, deviceName);
                Intent intent = new Intent(getApplicationContext(), CommandActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void enableWifiSearch(){
        try{
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(wifiScanReceiver, intentFilter);

            Runnable work = new Runnable() {
                @Override
                public void run() {
                    mWM.startScan();
                }
            };
            Thread thread = new Thread(work);
            thread.start();

        } catch (Exception e){

        } finally {
            unregisterReceiver(wifiScanReceiver);
        }
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if(success){
                scanSuccess();
            }
            else {
                //Scan failure handling
                scanFailure();
            }
        }
    };

    private void scanSuccess() {
        List<ScanResult> results = mWM.getScanResults();
        enumerateWifiDevices();
    }

    private void scanFailure() {
        Log.i(TAG, "Scan Failed!");
        // Display old results. If, scan fails.
        enumerateWifiDevices();
    }

    private void enumerateWifiDevices(){
        List<ScanResult> results = mWM.getScanResults();
        ArrayList<String> deviceList = new ArrayList<>();

        for(ScanResult s: results){
            Log.i("WIFI Device SSID: ", s.SSID);
            deviceList.add(s.SSID);
        }
        deviceList = removeDuplicates(deviceList);
        mDeviceList.addAll(deviceList);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    mWifiDevicesAdapter.notifyDataSetChanged();
                    //mListView.notifyAll();
                    //mWifiDevicesAdapter.notifyDataSetInvalidated();
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    // Remove Duplicates from the List
    private ArrayList<String> removeDuplicates(ArrayList<String> list)
    {

        // Create a new ArrayList
        ArrayList<String> newList = new ArrayList<String>();

        // Traverse through the first list
        for (String element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        // return the new list
        return newList;
    }
}
