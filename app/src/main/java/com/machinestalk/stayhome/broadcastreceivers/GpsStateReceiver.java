package com.machinestalk.stayhome.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.machinestalk.stayhome.service.body.CompliantBody;

import java.lang.ref.WeakReference;

/**
 * Created by asher.ali on 10/17/2017.
 */

public class GpsStateReceiver extends BroadcastReceiver {

    WeakReference<GpsStateChangeListener> gpsStateChangeListener;


    public GpsStateReceiver(GpsStateChangeListener listener) {
        gpsStateChangeListener = new WeakReference<>(listener);
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            // Make an action or refresh an already managed state.
            if (gpsStateChangeListener != null) {
                try {
                    gpsStateChangeListener.get().onGpsStateChanged();
                    if (isGpsEnabled(context)){
                        CompliantBody compliantBody = new CompliantBody();

                    }else {
                    }
                } catch (Exception e) {
                    Log.i(getClass().getName(), "error when state is changed " + e.getMessage());
                }
            }

        }
    }

    public interface GpsStateChangeListener {
        void onGpsStateChanged();
    }


    private boolean isGpsEnabled(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        // Find out what the settings say about which providers are enabled
        //  String locationMode = "Settings.Secure.LOCATION_MODE_OFF";
        int mode = Settings.Secure.getInt(
                contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        if (mode != Settings.Secure.LOCATION_MODE_OFF) {
            return true;
               /* if (mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY) {
                    locationMode = "Device only. Uses GPS to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING) {
                    locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                }*/
        } else {
            return false;
        }
    }



}
