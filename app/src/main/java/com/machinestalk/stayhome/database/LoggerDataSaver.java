package com.machinestalk.stayhome.database;

import android.bluetooth.BluetoothAdapter;
import android.net.ConnectivityManager;
import android.net.wifi.p2p.WifiP2pManager;

import com.google.android.gms.location.DetectedActivity;
import com.orhanobut.logger.Logger;

public class LoggerDataSaver {


    /**
     *
     * @param mode
     */
    public static void putNewConnectionModeToLogFile(int mode) {
        if (mode == ConnectivityManager.TYPE_WIFI) {
            Logger.i("network mobile -> wifi");
        } else if (mode == ConnectivityManager.TYPE_MOBILE){
            Logger.i("network wifi-> mobile");
        }
    }

    /**
     *
     * @param status
     */
    public static void putNewBleStatusChangedToLogFile(int status) {
        if (status == BluetoothAdapter.STATE_ON) {
            Logger.i("ble turn on");
        } else if (status == BluetoothAdapter.STATE_OFF){
            Logger.i("ble turn off");
        }
    }


    /**
     *
     * @param status
     */
    public static void putNewBleStatusToLogFile(boolean status) {
        if (status) {
            Logger.i("ble is on");
        } else {
            Logger.i("ble is off");
        }
    }


    /**
     *
     * @param type
     */
    public static void putNewUserActivityToLogFile(int type) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {

                Logger.i("act in veh");
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                Logger.i(" act on bic");
                break;
            }
            case DetectedActivity.ON_FOOT: {
                Logger.i("act on foot");
                break;
            }
            case DetectedActivity.RUNNING: {
                Logger.i("act run");
                break;
            }
            case DetectedActivity.STILL: {
                Logger.i("act still");
                break;
            }
            case DetectedActivity.TILTING: {
                Logger.i("act tilt");
                break;
            }
            case DetectedActivity.WALKING: {
                Logger.i("act wlk");
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Logger.i(" act unknown");
                break;
            }
        }
    }

    /**
     *
     * @param status
     */
    public static void putNewAccelerometerStatusToLogFile(int status) {
        if (status == 1) {
            Logger.i("mvt acc 1");
        } else {
            Logger.i("mvt acc 0");
        }
    }


    /**
     *
     * @param status
     */
    public static void putNewWifiStatusToLogFile(int status) {
        if (status == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            Logger.i("wifi turn on");
        } else {
            Logger.i("wifi turn off");
        }
    }


    /**
     *
     * @param status
     */
    public static void putNewInternetStatusToLogFile(boolean status) {
        if (status) {
            Logger.i("int cnx is 1");
        } else {
            Logger.i("int cnx is 0");
        }
    }

}
