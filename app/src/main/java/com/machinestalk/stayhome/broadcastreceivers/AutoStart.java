package com.machinestalk.stayhome.broadcastreceivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.orhanobut.logger.Logger;

public class AutoStart extends BroadcastReceiver {

    // Method is called after device bootup is complete
    public void onReceive(final Context context, Intent arg1) {

        if (arg1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Logger.i("AutoStart service is detected","");
            boolean serviceActivated = PreferenceUtility.getInstance(context).getBoolean(KeyConstants.KEY_SERVICE_ACTIVATED, false);
            if (serviceActivated) {
                // Start service here
                BluetoothAdapter.getDefaultAdapter().setName(AppConfig.getInstance().getUser().getDeviceId().substring(28));
                AppConfig.getInstance().StartBackgroundTasks(context);
                Logger.i("AutoStart service is starting","");
                PreferenceUtility.getInstance(context).putBoolean(KeyConstants.KEY_SERVICE_ACTIVATED, true);
            }else {
                Logger.i("AutoStart service is Desactivated","");
            }

        }
    }




}
