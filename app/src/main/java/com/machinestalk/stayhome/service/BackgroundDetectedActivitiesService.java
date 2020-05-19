package com.machinestalk.stayhome.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.broadcastreceivers.BluetoothBroadcastReceiver;
import com.machinestalk.stayhome.broadcastreceivers.BootReceiver;
import com.machinestalk.stayhome.broadcastreceivers.WiFiDirectBroadcastReceiver;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.database.LoggerDataSaver;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.orhanobut.logger.Logger;

import java.nio.charset.Charset;

import static android.content.Intent.ACTION_SHUTDOWN;

public class BackgroundDetectedActivitiesService extends Service {

    private static final String TAG = BackgroundDetectedActivitiesService.class.getSimpleName();
    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    IBinder mBinder = new BackgroundDetectedActivitiesService.LocalBinder();
    private BluetoothAdapter mBluetoothAdapter;

    private final IntentFilter intentFilter = new IntentFilter();
    private final IntentFilter bleIntentFilter = new IntentFilter();
    private final IntentFilter bootIntentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private WiFiDirectBroadcastReceiver mWiFiDirectBroadcastReceiver;
    private BluetoothBroadcastReceiver mBluetoothBroadcastReceiver;
    private BootReceiver mBootReceiver;
    private ConnectivityManager mConnectivityManager;

    BluetoothLeAdvertiser advertiser;
    AdvertiseCallback advertisingCallback;
    private BluetoothManager mBluetoothManager;


    private final AdvertiseCallback advCallback = new AdvertiseCallback() {

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(getClass().getName(),"Advertisement onStartSuccess ");
        }
        @Override
        public void onStartFailure(int errorCode) {
            if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                /* This is probably OK -- we accidentally called startAdvertising while we were
                   already doing so.
                 */
                Log.i(getClass().getName(),"Advertisement failed with error code " + errorCode);
            }
            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                /* This is probably OK -- we accidentally called startAdvertising while we were
                   already doing so.
                 */
                Log.i(getClass().getName(),"ADVERTISE_FAILED_DATA_TOO_LARGE " + errorCode);
            }
        }
    };





    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);

            int connectionMode = PreferenceUtility.getInstance(BackgroundDetectedActivitiesService.this).getInteger(PreferenceUtility.NETWORK_CONNECTION_MODE, 1);

            if (connectionMode != networkInfo.getType()){
                PreferenceUtility.getInstance(BackgroundDetectedActivitiesService.this).putInteger(PreferenceUtility.NETWORK_CONNECTION_MODE, networkInfo.getType());
                PreferenceUtility.getInstance(BackgroundDetectedActivitiesService.this).putBoolean(PreferenceUtility.NETWORK_CONNECTION_MODE_CHANGED, true);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    LoggerDataSaver.putNewConnectionModeToLogFile(networkInfo.getType());
                }
                else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    LoggerDataSaver.putNewConnectionModeToLogFile(networkInfo.getType());
                }
            }
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.i("network lost");
//            if (ConnectedCar.isAppInForeground()) {
//                EventBus.getDefault().post(AppConstants.EVENT_WIFI_DISABLED);
//            } else {
//                NotificationHelper.createNotificationWithIntent(1, 13,"There is no internet connection please check ", getApplicationContext());
//            }
            CompliantBody compliantBody = new CompliantBody();
            compliantBody.setCompliant(0);
            compliantBody.setInternetCnxEnabled(0);
            compliantBody.setReason("InternetCnxDisabled");
            ServiceApi.getInstance().sendCompliantStatus(compliantBody);
        }
    };


    public class LocalBinder extends Binder {
        public BackgroundDetectedActivitiesService getServerInstance() {
            return BackgroundDetectedActivitiesService.this;
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Intent intent = new Intent(this, DashboardActivity.class);

            PendingIntent resultIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launch)
                    .setContentIntent(resultIntent)
                    .setContentTitle("Stay home please, be safe")
                    //.setContentText("Stay home please, be safe")
                    .build();

            startForeground(1, notification);
        }

        mActivityRecognitionClient = new ActivityRecognitionClient(this);

        mIntentService = new Intent(this, DetectedActivitiesIntentService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mWiFiDirectBroadcastReceiver = new WiFiDirectBroadcastReceiver();
        prepareWifiBroadCast();
        registerReceiver(mWiFiDirectBroadcastReceiver, intentFilter);

        bleIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
        registerReceiver(mBluetoothBroadcastReceiver, bleIntentFilter);

        mBootReceiver = new BootReceiver();
        bootIntentFilter.addAction(ACTION_SHUTDOWN);
        bootIntentFilter.addAction("android.intent.action.QUICKBOOT_POWEROFF");
        bootIntentFilter.addAction("com.htc.intent.action.QUICKBOOT_POWEROFF");

        registerReceiver(mBootReceiver, bootIntentFilter);
        runSetupSteps();
        startAdvertising();


        return START_STICKY;
    }



    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(AppConstants.DETECTION_INTERVAL_IN_MILLISECONDS, mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                //  Toast.makeText(getApplicationContext(), "Successfully requested activity updates", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //   Toast.makeText(getApplicationContext(), "Requesting activity updates failed to start", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                //  Toast.makeText(getApplicationContext(), "Removed activity updates successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(getApplicationContext(), "Failed to remove activity updates!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdatesButtonHandler();
        if ( mWiFiDirectBroadcastReceiver != null && mBootReceiver != null){
            unregisterReceiver(mWiFiDirectBroadcastReceiver);
            unregisterReceiver(mBootReceiver);
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        }
        if (mBluetoothBroadcastReceiver != null){
            unregisterReceiver(mBluetoothBroadcastReceiver);
        }
    }

    /**
     *
     */
    private void prepareWifiBroadCast(){

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);


        //manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //channel = manager.initialize(this, getMainLooper(), null);
    }


    public void runSetupSteps() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            mBluetoothAdapter.setName(AppConfig.getInstance().getUser().getDeviceId().substring(28));

            if (mBluetoothAdapter == null) {
                Log.i("Bluetooth is not supported!","");
            }
        }



        advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
    }


    private void startAdvertising() {
        AdvertiseSettings advSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();

        AdvertiseData advData = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(true)
                .addServiceUuid(AppConstants.SERVICE_pUUID)
//                .addServiceData(AppConstants.SERVICE_pUUID, "Data".getBytes())
                .build();

        AdvertiseData advScanResponse = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();

        if (advertiser != null) {
            advertiser.startAdvertising(advSettings, advData, advScanResponse, advCallback);
            Log.d(getClass().getName(), "Started advertisement of mDL service with data " + "Data".getBytes(Charset.forName("UTF-8")));
        } else {
            Log.e(getClass().getName(), "mAdvertiser not available!");
        }
    }




}