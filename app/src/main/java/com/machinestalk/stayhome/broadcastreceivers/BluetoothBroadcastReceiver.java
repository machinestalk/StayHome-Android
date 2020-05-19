package com.machinestalk.stayhome.broadcastreceivers;
// w w  w  . ja v a2  s.  c o m

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.database.LoggerDataSaver;
import com.machinestalk.stayhome.helpers.NotificationHelper;

import org.greenrobot.eventbus.EventBus;

import static com.machinestalk.stayhome.constants.AppConstants.EVENT_BLUETOOTH_ENABLED;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_BROADCAST_BLUETOOTH;

/**
 * IMPORTANT NOTE: this receiver needs to know that the disable request was actually sent by the device service
 * rather than being initiated by the user, otherwise it will enable bluetooth and kick off a device scan if the
 * user disables bluetooth
 * This is part of a workaround where we have to disable and re-enable bluetooth to be able scan devices
 * see http://stackoverflow.com/questions/17870189/android-4-3-bluetooth-low-energy-unstable
 * I hope that in the future that this is unnecessary
 *
 * @author matt2
 */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = BluetoothBroadcastReceiver.class.getName();
    private static BluetoothManager mBluetoothManager = null;
    private static BluetoothAdapter mBluetoothAdapter = null;
    private static boolean mfAdapterReset = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // For API level 18 and above, get a reference to BluetoothAdapter through bluetoothManager.

        int bleStatusChangedPref = PreferenceUtility.getInstance(ConnectedCar.getInstance()).getInteger(PreferenceUtility.BLUETOOTH_STATUS_CHANGED, -1);

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return;
            }
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                return;
            }
        }

        String action = intent.getAction();

            // re-enable the bluetooth adapter if it was disabled by our service, and only by our service
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int adapterState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (adapterState == BluetoothAdapter.STATE_OFF) {

                    if (!PreferenceUtility.getInstance(ConnectedCar.getInstance()).getBoolean(KEY_BROADCAST_BLUETOOTH, false)) {
                        if (ConnectedCar.isAppInForeground()) {
                            EventBus.getDefault().post(AppConstants.EVENT_BLUETOOTH);
                            PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(EVENT_BLUETOOTH_ENABLED, false);
                        } else {
                            NotificationHelper.createNotificationWithIntent(NotificationHelper.BLUETOOTH_DISABLED_NOTIFICATION_ID,
                                    "Your bluetooth is disabled, please enable it", context);
                        }
                    }

                    if (bleStatusChangedPref != adapterState){
                        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.BLUETOOTH_STATUS_CHANGED, adapterState);
                        LoggerDataSaver.putNewBleStatusChangedToLogFile(adapterState);
                    }
                    mBluetoothAdapter.enable();
                } else if (adapterState == BluetoothAdapter.STATE_ON) {
                    EventBus.getDefault().post(EVENT_BLUETOOTH_ENABLED);
                    PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(EVENT_BLUETOOTH_ENABLED, true);
                    PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_BROADCAST_BLUETOOTH, false);
                    if (bleStatusChangedPref != adapterState){
                        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.BLUETOOTH_STATUS_CHANGED, adapterState);
                        LoggerDataSaver.putNewBleStatusChangedToLogFile(adapterState);
                    }
                }
            }
        }
}
