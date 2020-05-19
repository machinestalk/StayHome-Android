package com.machinestalk.stayhome.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.database.LoggerDataSaver;
import com.machinestalk.stayhome.helpers.NotificationHelper;
import com.machinestalk.stayhome.service.ServiceApi;
import com.machinestalk.stayhome.service.body.CompliantBody;

import org.greenrobot.eventbus.EventBus;

import static com.machinestalk.stayhome.constants.KeyConstants.KEY_BROADCAST_WIFI;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_WIFI_ENABLED;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                //do stuff
                PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_BROADCAST_WIFI, false);
                PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_WIFI_ENABLED, true);
                EventBus.getDefault().post(AppConstants.EVENT_WIFI_ENABLED);
            } else {
                if (!PreferenceUtility.getInstance(ConnectedCar.getInstance()).getBoolean(KEY_BROADCAST_WIFI, false)) {
                    // wifi connection was lost
                    PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_WIFI_ENABLED, false);
                    if (ConnectedCar.isAppInForeground()) {
                        EventBus.getDefault().post(AppConstants.EVENT_WIFI_DISABLED);
                    } else {
                        NotificationHelper.createNotificationWithIntent(NotificationHelper.WIFI_DISABLED_NOTIFICATION_ID,
                                "Your wifi is disabled, please you should be connected", context);
                    }
                    CompliantBody compliantBody = new CompliantBody();
                    compliantBody.setCompliant(0);
                    compliantBody.setWifiEnabled(0);
                    compliantBody.setReason("WifiDisabled");
                    ServiceApi.getInstance().sendCompliantStatus(compliantBody);
                }
            }
        }

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int wifiStatus = PreferenceUtility.getInstance(ConnectedCar.getInstance()).getInteger(PreferenceUtility.WIFI_STATUS, -1);
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (wifiStatus != state) {
                PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.WIFI_STATUS, state);
                LoggerDataSaver.putNewWifiStatusToLogFile(state);
            }

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            } else {
                // activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed! We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
            //fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }

    }

}
