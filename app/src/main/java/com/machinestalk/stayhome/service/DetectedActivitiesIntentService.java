/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.machinestalk.stayhome.service;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.database.LoggerDataSaver;
import com.machinestalk.stayhome.entities.BluetoothEntity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * IntentService for handling incoming intents that are generated as a result of requesting
 * activity updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();
    private BleReceiver mBleReceiver = new BleReceiver();
    private WifiManager mWifiManager;
    private String lastWifiDetected;
    private String lastBleDetected;



    // Create a BroadcastReceiver for ACTION_FOUND.
    private static class BleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                BluetoothEntity bluetoothEntity = new BluetoothEntity();
                bluetoothEntity.setAdresse(deviceHardwareAddress);
                bluetoothEntity.setName(deviceName);
                bluetoothEntity.setFrameName("Other Bluetooth");
                bluetoothEntity.setRssi(String.valueOf(rssi));
//                FL.i(", Bluetooth detected : " + bluetoothEntity);
                Logger.i(", Bluetooth detected : " + bluetoothEntity);

            }

            if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Logger.i("Bluetooth STATE_OFF : ");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Logger.i("Bluetooth STATE_TURNING_OFF : ");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Logger.i("Bluetooth STATE_ON : ");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Logger.i("Bluetooth STATE_TURNING_ON : ");
                        break;
                }

            }
        }


    }


    boolean isEquals(List<String> firstList, List<String> secondList) {
        ArrayList<String> commons = new ArrayList<>();

        for (String s2 : secondList) {
            for (String s1 : firstList) {
                if (s2.contains(s1)) {
                    commons.add(s2);
                }
            }
        }

        firstList.removeAll(commons);
        secondList.removeAll(commons);
        return !(firstList.size() > 0 || secondList.size() > 0);
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
//    private class WifiReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
//            if (success) {
//                scanWifiSuccess();
//            } else {
//                // scan failure handling
//                scanFailure();
//            }
//        }
//    }

    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBleReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
            Log.i(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
            broadcastActivity(activity);
            handleUserActivity(activity.getType(), activity.getConfidence());
        }

        scanBluetooth();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBleReceiver);
    }

    private void broadcastActivity(DetectedActivity activity) {
        Intent intent = new Intent(AppConstants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     *
     */
    private void scanBluetooth() {
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean bleStatus = mBluetoothAdapter.isEnabled();

            if (bleStatus) {
                mBluetoothAdapter.startDiscovery();
            }

        } catch (Exception e) {
            Log.e(TAG, " error when scan ble");
        }
    }


    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<android.net.wifi.ScanResult> results = mWifiManager.getScanResults();
    }

    private void handleUserActivity(int type, int confidence) {
        int activity = PreferenceUtility.getInstance(this).getInteger(PreferenceUtility.USER_ACTIVITY, -1);

        if (confidence > AppConstants.CONFIDENCE) {
            if (activity != type) {
                PreferenceUtility.getInstance(this).putInteger(PreferenceUtility.USER_ACTIVITY, type);
                LoggerDataSaver.putNewUserActivityToLogFile(type);
            }
        }
    }
}
