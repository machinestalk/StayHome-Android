package com.machinestalk.stayhome.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.database.LoggerDataSaver;
import com.machinestalk.stayhome.entities.BluetoothEntity;
import com.machinestalk.stayhome.entities.TrackingInfoEntity;
import com.machinestalk.stayhome.entities.ZoneEntity;
import com.machinestalk.stayhome.helpers.NotificationHelper;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.machinestalk.stayhome.utils.LocationManager;
import com.machinestalk.stayhome.utils.LocationUtils;
import com.machinestalk.stayhome.utils.Util;
import com.minew.beaconplus.sdk.MTCentralManager;
import com.minew.beaconplus.sdk.MTPeripheral;
import com.minew.beaconplus.sdk.enums.BluetoothState;
import com.minew.beaconplus.sdk.enums.FrameType;
import com.minew.beaconplus.sdk.frames.AccFrame;
import com.minew.beaconplus.sdk.frames.IBeaconFrame;
import com.minew.beaconplus.sdk.frames.MinewFrame;
import com.minew.beaconplus.sdk.frames.TlmFrame;
import com.minew.beaconplus.sdk.frames.UrlFrame;
import com.minew.beaconplus.sdk.interfaces.MTCentralManagerListener;
import com.minew.beaconplus.sdk.interfaces.OnBluetoothStateChangedListener;
import com.orhanobut.logger.Logger;
import com.uriio.beacons.model.Beacon;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.machinestalk.stayhome.helpers.NotificationHelper.NOTIFICATION_CHANNEL_ID;
import static com.machinestalk.stayhome.views.HouseHolderDashboardView.MAX_ACCURACY;
import static com.machinestalk.stayhome.views.HouseHolderDashboardView.MAX_RADIUS;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameAccSensor;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameTLM;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameURL;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameiBeacon;

public class MyLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status>, SensorEventListener {

    public static final int SERVICE_ID = 0;
    private static final int TODO = 0;
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    /**
     * The Job scheduler.
     */
    JobScheduler jobScheduler;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private static Location locationFromInternet;
    private TrackingInfoEntity lastLocationInfo;
    private float mDistanceZoneIn = 0;
    private static int countpaquets = 0;
    private static int countBracelet = 1;
    private static String lastPaquet = "inZone";
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private int hitCount = 0;
    private double hitSum = 0;
    private double hitResult = 0;
    private double paquetMovingReceived = 0;
    private double paquetStableReceived = 0;

    private final int SAMPLE_SIZE = 50; // change this sample size as you want, higher is more precise but slow measure.
    private final double THRESHOLD = 0.2; // change this threshold as you want, higher is more spike movement

    private float[] mGravity;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    private WifiManager mWifiManager;
    private String lastWifiDetected;
    private WifiReceiver mWifiReceiver = new WifiReceiver();


    /********* Sattelite attributes *********/

    android.location.LocationManager locationManager;
    int new_sat_count = 0;
    int last_sattelite_count = 0;
    int satNumberRepeted = 0;


    /********* BlueTooth attributes *********/
    Handler handler = new Handler();
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    BluetoothLeScanner btScanner;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    boolean successCreateIBeacon;

    boolean isIbeaconFrameDetected = false;
    boolean isOtherbeaconDetected = false;
    boolean isSensorBeaconDetected = false;
    boolean isTLMBeaconDetected = false;
    boolean isURLBeaconDetected = false;
    private boolean isOneTimeDetected = false;
    private boolean isBraceletListDetected = false;

    Beacon beacon;

    protected static final String TAG = "RangingActivity";
    //    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int PERMISSION_COARSE_LOCATION = 2;

    private String lastBleNameDetected;
    private String lastBleMacDetected;
    private String lastBRaceltDetected;
    private boolean IbeaconDetected = false;
    private boolean AccSensorDetected = false;
    private boolean URLDetected = false;
    private boolean OtherDetected = false;
    private boolean TLMDetected = false;

    private MTCentralManager mMtCentralManager;
    private Handler scanHandler = new Handler();


    private Runnable stopScan = new Runnable() {
        @Override
        public void run() {
            if (btScanner != null) {
                btScanner.stopScan(leScanCallback);
            }
            if (mMtCentralManager != null){
                mMtCentralManager.stopScan();
                mMtCentralManager.stopService();
            }
            String macStored = PreferenceUtility.getInstance(getApplicationContext()).getString(KeyConstants.KEY_MAC_ADRESS, "");

            if (!isBraceletListDetected && !StringUtility.isEmptyOrNull(macStored)) {
                Log.i("bracelet  not detected", "bracelet  not detected");
                        if (ConnectedCar.isAppInForeground())
                            EventBus.getDefault().post(AppConstants.EVENT_BRACELET_NOT_DETECTED);
//                        else
//                            NotificationHelper.createNotificationWithIntent(1, 16
//                                    , "Your bracelet is not detected, please make sure that your bracelet still close to your phone", getApplicationContext());
            }

            Log.i("BLE_Scanner", "Stop Scan");
        }
    };


    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("start scan BLE", "start scan BLE");
            if (result.getDevice().getName() != null && !result.getDevice().getName().equals(lastBleNameDetected)) {

                BluetoothEntity bluetoothEntity = new BluetoothEntity();
                bluetoothEntity.setAdresse(result.getDevice().getAddress());
                if (result.getDevice().getName() != null || !result.getDevice().getName().equals("Unnamed")) {
                    bluetoothEntity.setName(result.getDevice().getName());
                } else {
                    bluetoothEntity.setName("Stayhome App");
                }
//                if (result.getScanRecord().getServiceUuids().size() == 1)
//                    bluetoothEntity.setUdid(result.getScanRecord().getServiceUuids().get(0).getUuid().toString());

                bluetoothEntity.setRssi(String.valueOf(result.getRssi()));
                bluetoothEntity.setFrameName("BLE frame");

                Util.showBluetoothList(getBaseContext(), bluetoothEntity);
                Logger.i(", Bluetooth detected : " + bluetoothEntity);

                CompliantBody compliantBody = new CompliantBody();
                compliantBody.setInfo(bluetoothEntity.getName() + "," + bluetoothEntity.getAdresse());
                compliantBody.setiBeacon(bluetoothEntity.getUdid() + "," + bluetoothEntity.getMinor()
                        + "," + bluetoothEntity.getMajor());
                compliantBody.setUID(bluetoothEntity.getNamespace() + "," + bluetoothEntity.getInstanceID() + ","
                        + bluetoothEntity.getRssi());
                compliantBody.setURL(bluetoothEntity.getUrl());
                compliantBody.setAcc(bluetoothEntity.getX() + "," + bluetoothEntity.getY()
                        + "," + bluetoothEntity.getZ());
                compliantBody.setTLM(bluetoothEntity.getBattery() + "," + bluetoothEntity.getTxPower()
                        + "," + bluetoothEntity.getTemperature());
//                ServiceApi.getInstance().sendCompliantStatus(compliantBody);

            }
            lastBleNameDetected = result.getDevice().getName();
        }
    };

//    @Override
//    public void onBeaconServiceConnect() {
//        beaconManager.addRangeNotifier(new RangeNotifier() {
//            @Override
//            public void didRangeBeaconsInRegion(Collection<org.altbeacon.beacon.Beacon> beacons, Region region) {
//                if (beacons.size() > 0) {
////                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
////                    Log.i(TAG, "Reading…"+"\n"+"proximityUuid:"+" "+ beacons.iterator().next().getId1()+"\n"+
////                            "major:"+" "+beacons.iterator().next().getId2()+"\n"+
////                            "minor:"+" "+beacons.iterator().next().getId3());
////                    String rangedUUID = beacons.iterator().next().getId1().toString();
////                    String rangedMajor = beacons.iterator().next().getId2().toString();
////                    String rangedMinor = beacons.iterator().next().getId3().toString();
////
////                    BluetoothEntity bluetoothEntity = new BluetoothEntity();
////                    bluetoothEntity.setAdresse(beacons.iterator().next().getBluetoothAddress() );
////                    if (beacons.iterator().next().getBluetoothName() != null || !beacons.iterator().next().getBluetoothName() .equals("Unnamed")) {
////                        bluetoothEntity.setName(beacons.iterator().next().getBluetoothName() );
////                    } else {
////                        bluetoothEntity.setName("Stayhome App");
////                    }
////
////                    bluetoothEntity.setRssi(String.valueOf(beacons.iterator().next().getBluetoothName() ));
////
////                    if (!beacons.iterator().next().getBluetoothAddress() .equals(lastBleDetected)) {
////                        Util.showBluetoothList(getBaseContext(), bluetoothEntity);
////                        Logger.d("Bluetooth detected : " + bluetoothEntity);
////                    }
////                    lastBleDetected = beacons.iterator().next().getBluetoothAddress();
//                }
//            }
//        });
//
//
//        try {
//            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId"
//                    , Identifier.parse("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), Identifier.parse("1"), null));
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

    private void storeBluetoothBracelets(List<MTPeripheral> mtPeripherals) {

        for (MTPeripheral mtPeripheral : mtPeripherals) {
            String name = mtPeripheral.mMTFrameHandler.getName();
            String mac = mtPeripheral.mMTFrameHandler.getMac();
            int rssi = mtPeripheral.mMTFrameHandler.getRssi();
            int battery = mtPeripheral.mMTFrameHandler.getBattery();

            BluetoothEntity bluetoothEntity = new BluetoothEntity();
            bluetoothEntity.setAdresse(mac);
            bluetoothEntity.setRssi(String.valueOf(rssi));
            bluetoothEntity.setBattery(String.valueOf(battery));

            StringBuilder beaconData = new StringBuilder();
//            if (name != null && !name.equals("Unnamed")) {
//                beaconData.append(String.format("Name: %s\n", name));
//                bluetoothEntity.setName(name);
//            } else {
//                beaconData.append(String.format("Name: %s\n", "Stayhome App"));
//                bluetoothEntity.setName("Stayhome App");
//            }
            if (name != null) {
                beaconData.append(String.format("Name: %s\n", name));
                bluetoothEntity.setName(name);
            }
            beaconData.append(String.format(Locale.ENGLISH, "MAC: %s\nRSSI: %d\n", mac, rssi));
            List<MinewFrame> advFrames = mtPeripheral.mMTFrameHandler.getAdvFrames();
            for (int i = 0; i < advFrames.size(); i++) {
                MinewFrame advFrame = advFrames.get(i);
                FrameType frameType = advFrame.getFrameType();
//                || FrameType.FrameDeviceInfo.getValue() == frameType
                if (i == 0) {
                    int txPower = advFrame.getAdvtxPower();
                    int radioTxPower = advFrame.getRadiotxPower();
                    bluetoothEntity.setRx(String.valueOf(radioTxPower));
                    bluetoothEntity.setTx(String.valueOf(radioTxPower));
                    bluetoothEntity.setTxPower(String.valueOf(txPower));
                    beaconData.append(String.format(Locale.ENGLISH, "txPower: %d\n", txPower));
                    beaconData.append(String.format(Locale.ENGLISH, "radioTxPower: %d\n", radioTxPower));
                    beaconData.append(String.format(Locale.ENGLISH, "radioTxPower: %d\n", radioTxPower));
                    beaconData.append(String.format(Locale.ENGLISH, "battery: %d\n", battery));
                }
                if (frameType == FrameiBeacon) {
                    beaconData.append("---- iBeacon ---- \n");
                    IBeaconFrame frame = (IBeaconFrame) advFrame;
                    beaconData.append("RSSI @ 1m: ").append(frame.getTxPower()).append("dBm\n");
                    beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
                    beaconData.append("Major:").append(frame.getMajor()).append("\n");
                    beaconData.append("Minor:").append(frame.getMinor()).append("\n");
                    bluetoothEntity.setTx("" + frame.getRadiotxPower());
                    bluetoothEntity.setRx("" + frame.getRadiotxPower());
                    bluetoothEntity.setMinor("" + frame.getMinor());
                    bluetoothEntity.setMajor("" + frame.getMajor());
                    bluetoothEntity.setFrameName("FrameiBeacon");
                    isIbeaconFrameDetected = true;

//                    if (!bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
//                        Util.showBluetoothList(getApplicationContext(), bluetoothEntity);
//                    }
//                    lastBRaceltDetected = bluetoothEntity.getAdresse();
                    Util.showBluetoothList(getApplicationContext(), bluetoothEntity);


                } else if (frameType == FrameAccSensor) {
                    beaconData.append("---- AccSensor ----\n");
                    AccFrame frame = (AccFrame) advFrame;
                    beaconData.append("X: ").append(frame.getXAxis()).append("\n");
                    beaconData.append("Y: ").append(frame.getYAxis()).append("\n");
                    beaconData.append("Z: ").append(frame.getZAxis()).append("\n");
                    beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
                    bluetoothEntity.setX("" + frame.getXAxis());
                    bluetoothEntity.setY("" + frame.getYAxis());
                    bluetoothEntity.setZ("" + frame.getZAxis());
                    bluetoothEntity.setFrameName("FrameAccSensor");
                    isSensorBeaconDetected = true;

//                    if (!bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
//                        Util.showBluetoothList(getApplicationContext(), bluetoothEntity);
//                    }
//                    lastBRaceltDetected = bluetoothEntity.getAdresse();
                    Util.showBluetoothList(getApplicationContext(), bluetoothEntity);


                } else if (frameType == FrameTLM) {
                    beaconData.append("---- TLM ----\n");
                    TlmFrame frame = (TlmFrame) advFrame;
                    beaconData.append("Temperature: ").append(frame.getTemperature()).append("\n");
                    beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
                    bluetoothEntity.setRx("" + frame.getRadiotxPower());
                    bluetoothEntity.setTemperature("" + frame.getTemperature());
                    bluetoothEntity.setFrameName("FrameTLM");
                    isTLMBeaconDetected = true;

//                    if (!bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
//                        Util.showBluetoothList(getApplicationContext(), bluetoothEntity);
//                    }
//                    lastBRaceltDetected = bluetoothEntity.getAdresse();
                    Util.showBluetoothList(getApplicationContext(), bluetoothEntity);

                } else if (frameType == FrameURL) {
                    beaconData.append("---- URL ----\n");
                    UrlFrame frame = (UrlFrame) advFrame;
                    beaconData.append("RSSI @ 0m: ").append(frame.getTxPower()).append("dBm\n");
                    beaconData.append("URL: ").append(frame.getUrlString()).append("\n");
                    bluetoothEntity.setUrl("" + frame.getUrlString());
                    bluetoothEntity.setRssi("" + frame.getTxPower());
                    bluetoothEntity.setFrameName("FrameURL");
                    isURLBeaconDetected = true;
//                    if (!bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
//                        Util.showBluetoothList(getApplicationContext(), bluetoothEntity);
//                    }
//                    lastBRaceltDetected = bluetoothEntity.getAdresse();
                    Util.showBluetoothList(getApplicationContext(), bluetoothEntity);


                } else {
                    for (Map.Entry<String, String> entry : advFrame.getMap().entrySet()) {
                        if (entry.getKey().equals("NameSpace ID")) {
                            bluetoothEntity.setNamespace(String.valueOf(entry.getValue()));
                        } else if (entry.getKey().equals("Instance ID")) {
                            bluetoothEntity.setInstanceID(String.valueOf(entry.getValue()));
                        }
                        beaconData.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
                    }
                    bluetoothEntity.setFrameName("Bracelet Frame");
                    isOtherbeaconDetected = true;

//                    if (!bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
//                        Util.showBluetoothList(getApplicationContext(), bluetoothEntity);
//                    }
//                    lastBRaceltDetected = bluetoothEntity.getAdresse();
                    Util.showBluetoothList(getApplicationContext(), bluetoothEntity);

                }
                beaconData.append("---------\n");

            }


        }
    }


    private void deletOldBracelets() {
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameiBeacon");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameAccSensor");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameTLM");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameURL");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("Bracelet Frame");
    }

    private void getAllBracelets(List<MTPeripheral> peripherals) {

        for (MTPeripheral ble : peripherals) {
            BluetoothEntity bluetoothEntity = new BluetoothEntity();

            if (AppDatabase.getInstance(getApplicationContext())
                    .getBluetoothDao().getBluetoothEntityByAdress(ble.mMTFrameHandler.getMac()) != null)
                bluetoothEntity = AppDatabase.getInstance(getApplicationContext())
                        .getBluetoothDao().getBluetoothEntityByAdress(ble.mMTFrameHandler.getMac());

            if (!StringUtility.isEmptyOrNull(bluetoothEntity.getAdresse())
                    && !StringUtility.isEmptyOrNull(bluetoothEntity.getFrameName())
                    && !StringUtility.isEmptyOrNull(bluetoothEntity.getUrl())
                    && !StringUtility.isEmptyOrNull(bluetoothEntity.getMajor())
                    && !StringUtility.isEmptyOrNull(bluetoothEntity.getMinor())) {
                CompliantBody compliantBody = new CompliantBody();
                compliantBody.setInfo(bluetoothEntity.getName() + "," + bluetoothEntity.getAdresse());
                compliantBody.setiBeacon(bluetoothEntity.getUdid() + "," + bluetoothEntity.getMinor()
                        + "," + bluetoothEntity.getMajor());
                compliantBody.setUID(bluetoothEntity.getNamespace() + "," + bluetoothEntity.getInstanceID() + ","
                        + bluetoothEntity.getRssi());
                compliantBody.setURL(bluetoothEntity.getUrl());
                compliantBody.setAcc(bluetoothEntity.getX() + "," + bluetoothEntity.getY()
                        + "," + bluetoothEntity.getZ());
                compliantBody.setTLM(bluetoothEntity.getBattery() + "," + bluetoothEntity.getTxPower()
                        + "," + bluetoothEntity.getTemperature());

//                FL.i(", Bluetooth detected : " + bluetoothEntity);
                Logger.i(", Bluetooth detected : " + bluetoothEntity);

            }

        }


    }

    public void clearLog(){
        try {
            Process process = new ProcessBuilder()
                    .command("logcat", "-c")
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            Logger.i("Logger file Clear status"+ e.getMessage());
        }
    }



    private double calculateDistance(float txPower, double rssi) {

        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }


    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        createGoogleApi();

        if (!NotificationHelper.areNotificationsEnabled(getBaseContext(), NOTIFICATION_CHANNEL_ID)) {
            CompliantBody compliantBody = new CompliantBody();
            compliantBody.setCompliant(0);
            compliantBody.setNotificationEnabled(0);
            ServiceApi.getInstance().sendCompliantStatus(compliantBody);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        if (cellLocation != null) {
            int cellid = cellLocation.getCid();
            int celllac = cellLocation.getLac();
            if (PreferenceUtility.getInstance(getBaseContext()).getInteger(KeyConstants.KEY_CELL_ID, 0) != cellid) {
                String networkOperator = telephonyManager.getNetworkOperator();
                if (networkOperator.length() > 4){
                    String mnc = networkOperator.substring(3);
                    Logger.i("CellLct :" + cellLocation.toString());
                    Logger.i("Cell mnc :" + mnc);
                    Logger.i("Cell ID: " + String.valueOf(cellid));
                    Logger.i("Cell LctCode :" + String.valueOf(celllac));
                    PreferenceUtility.getInstance(getBaseContext()).putInteger(KeyConstants.KEY_CELL_ID, cellid);

                }
            }
        }
//        FL.i("Cell LctCode :" + String.valueOf(5));

        if (getBatteryPercentage(getBaseContext()) < 10) {
            PreferenceUtility.getInstance(getBaseContext()).putBoolean(AppConstants.EVENT_BATTERY_DOWN, true);
            if (ConnectedCar.isAppInForeground()) {
                EventBus.getDefault().post(AppConstants.EVENT_BATTERY_LOW);
            } else {
                NotificationHelper.createNotificationWithIntent(NotificationHelper.BATTERY_LOW_DISABLED_NOTIFICATION_ID,
                        "Your battery is running low, please recharge your kindle", getBaseContext());
            }
        } else {
            EventBus.getDefault().post(AppConstants.EVENT_BATTERY_FINE);
//                EventBus.getDefault().post(AppConstants.EVENT_BATTERY);
            PreferenceUtility.getInstance(getBaseContext()).putBoolean(AppConstants.EVENT_BATTERY_DOWN, false);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }


        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        locationManager = (android.location.LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);

        if (LocationManager.hasLocationPermission(getBaseContext())
                && LocationManager.isLocationEnabled(getBaseContext())) {
            //moveToCurrentLocation();
            Log.i(getClass().getName(), "GPS Status is Enabled");
        } else {
            NotificationHelper.createNotificationWithIntent(NotificationHelper.GPS_DISABLED_NOTIFICATION_ID,
                    "Your GPS is disabled, please enable your GPS ", getBaseContext());
            CompliantBody compliantBody = new CompliantBody();
            compliantBody.setCompliant(0);
            compliantBody.setGpsEnabled(0);
            compliantBody.setReason("GpsDisabled");
            ServiceApi.getInstance().sendCompliantStatus(compliantBody);
            //EventBus.getDefault().post(AppConstants.EVENT_LOCATION_DISABLED);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(mWifiReceiver, intentFilter);

        lastBleNameDetected = "";
        lastBRaceltDetected = "";
        isOneTimeDetected = false;
        isBraceletListDetected = false;
        locationFromInternet = null ;
        countBracelet = 1 ;

        runSetupSteps();
        startScanning();
        scanWifiSuccess();
        clearLog();
//        clearLog();
//        beaconManager.bind(this);


//        if (beacon != null && beacon.getActiveState() == 0)
//            beacon.stop();
//
//        byte[] uuid = getIdAsByte(UUID.fromString(getString(R.string.ble_uuid)));
////        byte[] uuid2 = fromUuidToBytes(UUID.fromString(getString(R.string.ble_uuid)));
//
////      beacon = new EddystoneUID(uuid, AdvertiseSettings.ADVERTISE_MODE_BALANCED, AdvertiseSettings.ADVERTISE_TX_POWER_LOW) ;
//        beacon = new iBeacon(newUUID(UUID.fromString(getString(R.string.ble_uuid))), 333, 222);
//        successCreateIBeacon = beacon.start();
//
//
//
//        if (successCreateIBeacon) {
//            Log.i(getClass().getName(), "successCreateIBeacon ");
//        }

        if (!ensureBleExists()) {
        }
        if (!isBLEEnabled()) {
            showBLEDialog();
        }
        initManager();
        getRequiredPermissions();
        initListener();
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        Logger.i("Executed apps " + recentTasks.size());
        Logger.i(String.format("Now device is %s.", isDeviceLocked(getApplicationContext()) ? "locked" : "unlocked"));
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
        return START_STICKY;
    }

    private void getRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("", "");
        } else {
            initData();
        }
    }

    private void initData() {
        //三星手机系统可能会限制息屏下扫描，导致息屏后无法获取到广播数据
        mMtCentralManager.startScan();
    }


    private void initListener() {

        mMtCentralManager.setMTCentralManagerListener(new MTCentralManagerListener() {
            @Override
            public void onScanedPeripheral(final List<MTPeripheral> peripherals) {
                isBraceletListDetected = true;
                storeBluetoothBracelets(peripherals);
//                if (isOtherbeaconDetected && isURLBeaconDetected
//                        && isTLMBeaconDetected && isIbeaconFrameDetected && isSensorBeaconDetected) {
//            if (isOtherbeaconDetected ) {
                    getAllBracelets(peripherals);
//                }

                if (!isOneTimeDetected) {
                    String macStored = PreferenceUtility.getInstance(getApplicationContext()).getString(KeyConstants.KEY_MAC_ADRESS, "");
                    if (!StringUtility.isEmptyOrNull(macStored) && peripherals.size() > 0 ) {
                        if (!checkMacExistance(peripherals, macStored)) {
                            Log.i("bracelet detected false", "bracelet detected false");
                            if (ConnectedCar.isAppInForeground())
                                EventBus.getDefault().post(AppConstants.EVENT_BRACELET_NOT_DETECTED);
//                            else
//                                NotificationHelper.createNotificationWithIntent(1, 16
//                                        , "Your bracelet is not detected, please make sure that your bracelet still close to your phone", getApplicationContext());
                        } else {
                            Log.i("bracelet detected true", "bracelet detected true");
                            EventBus.getDefault().post(AppConstants.EVENT_BRACELET_DETECTED);
//                            NotificationHelper.createNotificationWithIntent(1, 16
//                                    , "Your bracelet is detected, please make sure that your bracelet still close to your phone", getApplicationContext());
                        }
                    }

                }
                isOneTimeDetected = true;

            }
        });

    }


    public void onTaskRemoved(Intent rootIntent) {
        CompliantBody compliantBody = new CompliantBody();
        compliantBody.setCompliant(0);
        compliantBody.setAppKilledStatus(1);
        ServiceApi.getInstance().sendCompliantStatus(compliantBody);
        Logger.i("All service down ----Due-----> Force Closed ");
//        beaconManager.bind(this);
    }

    private void initManager() {
        mMtCentralManager = MTCentralManager.getInstance(this);
        //startservice
        mMtCentralManager.startService();
        BluetoothState bluetoothState = mMtCentralManager.getBluetoothState(this);
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Log.e("tag", "BluetoothStateNotSupported");
                break;
            case BluetoothStatePowerOff:
                Log.e("tag", "BluetoothStatePowerOff");
                break;
            case BluetoothStatePowerOn:
                Log.e("tag", "BluetoothStatePowerOn");
                break;
        }

        mMtCentralManager.setBluetoothChangedListener(new OnBluetoothStateChangedListener() {
            @Override
            public void onStateChanged(BluetoothState state) {
                switch (state) {
                    case BluetoothStateNotSupported:
                        Log.e("tag", "BluetoothStateNotSupported");
                        break;
                    case BluetoothStatePowerOff:
                        Log.e("tag", "BluetoothStatePowerOff");
                        break;
                    case BluetoothStatePowerOn:
                        Log.e("tag", "BluetoothStatePowerOn");
                        break;
                }
            }
        });
    }


    public byte[] getIdAsByte(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static byte[] newUUID(UUID uuid) {
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
    }

    public static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }


    public void startScanning() {
        System.out.println("start scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                startScan();
            }
        });
    }


    private void startScan() {
        //Scan for devices advertising the thermometer service
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        Log.d(getClass().getName(), "scan BLE started");

        UUID BLP_SERVICE_UUID = UUID.fromString(getString(R.string.ble_uuid));
        UUID[] serviceUUIDs = new UUID[]{BLP_SERVICE_UUID};
        List<ScanFilter> filters = null;
        if (serviceUUIDs != null) {
            filters = new ArrayList<>();
            for (UUID serviceUUID : serviceUUIDs) {
                ScanFilter filter = new ScanFilter.Builder()
                        .build();
                filters.add(filter);
            }
        }
        if (btScanner != null) {
            btScanner.startScan(leScanCallback);
        }

        scanHandler.postDelayed(stopScan, 10000);

    }

    public void runSetupSteps() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        if (!mBluetoothAdapter.isEnabled()) {
        }

        btScanner = mBluetoothAdapter.getBluetoothLeScanner();


    }


    private boolean isNetworkConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * extract last location if location is not available
     */
    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        //Log.d(TAG, "getLastKnownLocation()");
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            TrackingInfoEntity trackingInfoEntity = new TrackingInfoEntity();
            trackingInfoEntity.setAccuracy(lastLocation.getAccuracy());
            trackingInfoEntity.setProvider(lastLocation.getProvider());
            trackingInfoEntity.setSpeed(lastLocation.getSpeed());
            trackingInfoEntity.setCurrentLongitude(lastLocation.getLongitude());
            trackingInfoEntity.setCurrentLatitude(lastLocation.getLatitude());
            trackingInfoEntity.setBearing(lastLocation.getBearing());
            lastLocationInfo = trackingInfoEntity;

            Log.i(getClass().getName(), "LasKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            writeLastLocation();
            startLocationUpdates();
            List<ZoneEntity> zoneEntityList = AppDatabase.getInstance(getApplicationContext()).getZonesDao().getAll();
            if (zoneEntityList != null && zoneEntityList.size() > 0) {
                ZoneEntity zoneEntity = zoneEntityList.get(0);
                if (zoneEntity != null) {
                    LatLng centerPosition = zoneEntity.getCenter();
                    if (centerPosition != null && !AppConfig.getInstance().getUser().isANewDevice())
                        checkYourPrsenceAtHome(centerPosition, lastLocation);
                }
            }

        } else {
            startLocationUpdates();
            //here we can show Alert to start location
        }
    }

    /**
     * this method writes location to text view or shared preferences
     *
     * @param trackingInfoEntity - location from fused location provider
     */
    @SuppressLint("SetTextI18n")
    private void writeActualLocation(TrackingInfoEntity trackingInfoEntity) {
        //here in this method you can use web service or any other thing
        lastLocationInfo = trackingInfoEntity;
        AppDatabase.getInstance(getBaseContext()).getTrackingInfoDao().insertTrackingInfoEntity(trackingInfoEntity);
    }

    /**
     * this method only provokes writeActualLocation().
     */
    private void writeLastLocation() {
        writeActualLocation(lastLocationInfo);
    }

    private void checkCompliantStatus() {
        List<TrackingInfoEntity> trackingInfoEntityList = AppDatabase.getInstance(getBaseContext()).getTrackingInfoDao().getAll();
        List<ZoneEntity> zoneEntityList = AppDatabase.getInstance(getBaseContext()).getZonesDao().getAll();

        if (trackingInfoEntityList != null && trackingInfoEntityList.size() > 0 && zoneEntityList != null && zoneEntityList.size() > 0) {

            TrackingInfoEntity trackingInfoEntity = trackingInfoEntityList.get(0);
            ZoneEntity zoneEntity = zoneEntityList.get(0);

            if (trackingInfoEntity != null && zoneEntity != null) {
                LatLng currentLocation = trackingInfoEntity.getCurrentPosition();
                LatLng centerPosition = zoneEntity.getCenter();
                if (currentLocation != null && centerPosition != null) {
                    float mDistanceZoneIn = LocationUtils.getDistanceBetween(currentLocation, centerPosition);
                    if (mDistanceZoneIn > MAX_RADIUS) {
//                        ServiceApi.getInstance().sendCompliantStatus(0);
                    } else if (mDistanceZoneIn < MAX_RADIUS) {
//                        ServiceApi.getInstance().sendCompliantStatus(1);
                    }
                }
            }

        }
    }


    /**
     * this method fetches location from fused location provider and passes to writeLastLocation
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        //Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Create google api instance
     */
    private void createGoogleApi() {
        //Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //connect google api
        googleApiClient.connect();

    }

    /**
     * this method tells whether google api client connected.
     *
     * @param bundle - to get api instance
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    /**
     * this method returns whether connection is suspended
     *
     * @param i - 0/1
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(getClass().getName(), "connection suspended");
    }

    /**
     * this method checks connection status
     *
     * @param connectionResult - connected or failed
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /**
     * this method tells the result of status of google api client
     *
     * @param status - success or failure
     */
    @Override
    public void onResult(@NonNull Status status) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            lastLocation = location;
            if (location.getAccuracy() < MAX_ACCURACY) {
                TrackingInfoEntity trackingInfoEntity = new TrackingInfoEntity();
                trackingInfoEntity.setAccuracy(location.getAccuracy());
                trackingInfoEntity.setProvider(location.getProvider());
                trackingInfoEntity.setSpeed(location.getSpeed());
                trackingInfoEntity.setCurrentLongitude(location.getLongitude());
                trackingInfoEntity.setCurrentLatitude(location.getLatitude());
                trackingInfoEntity.setBearing(location.getBearing());
                writeActualLocation(trackingInfoEntity);
                List<ZoneEntity> zoneEntityList = AppDatabase.getInstance(getApplicationContext()).getZonesDao().getAll();
                if (zoneEntityList != null && zoneEntityList.size() > 0) {
                    ZoneEntity zoneEntity = zoneEntityList.get(0);
                    if (zoneEntity != null) {
                        LatLng centerPosition = zoneEntity.getCenter();
                        if (centerPosition != null && !AppConfig.getInstance().getUser().isANewDevice())
                            checkYourPrsenceAtHome(centerPosition, location);
                    }
                }
            }
        }
    }

    private void checkYourPrsenceAtHome(LatLng centerPosition, Location location) {
        LatLng currentLatLng;
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mDistanceZoneIn = LocationUtils.getDistanceBetween(currentLatLng, centerPosition);
//        Log.i("paquests zone  "+countpaquets, "paquests zone  "+countpaquets);
        if (mDistanceZoneIn < MAX_RADIUS
                && !PreferenceUtility.getInstance(this).getBoolean(PreferenceUtility.IS_MESSAGE_Z_IN_STATUS, false)) {
//            Log.i("paquests zone in "+countpaquets, "paquests zone in "+countpaquets);

            if (countpaquets < 10 && !lastPaquet.equals("zoneOut")) {
                countpaquets++;
                lastPaquet = "inZone";
            } else if (countpaquets == 10) {
                if (ConnectedCar.isAppInForeground())
                    EventBus.getDefault().post(AppConstants.EVENT_ZONE_IN);
                else
                    NotificationHelper.createNotificationWithIntent(NotificationHelper.ZONE_IN_NOTIFICATION_ID,
                            "You are in your zone, please stay at Home", getBaseContext());
                PreferenceUtility.getInstance(getBaseContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_IN_STATUS, true);
                PreferenceUtility.getInstance(getBaseContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_OUT_STATUS, false);
                Logger.i("User new location is " + location);
                Logger.i("in Zone ----> Distance is :" + lastLocationInfo);
                countpaquets = 0;
                lastPaquet = "inZone";
                CompliantBody compliantBody = new CompliantBody();
                compliantBody.setCompliant(1);
                compliantBody.setZoneStatus(1);
                compliantBody.setReason("Zone In");
                ServiceApi.getInstance().sendCompliantStatus(compliantBody);

            } else {
                countpaquets = 0;
                lastPaquet = "inZone";
            }
        } else if (mDistanceZoneIn > MAX_RADIUS
                && !PreferenceUtility.getInstance(this).getBoolean(PreferenceUtility.IS_MESSAGE_Z_OUT_STATUS, false)) {
//            Log.i("paquests zone out "+countpaquets, "paquests zone out "+countpaquets);

            if (countpaquets < 10 && !lastPaquet.equals("inZone")) {
                countpaquets++;
                lastPaquet = "zoneOut";
            } else if (countpaquets == 10) {
                if (ConnectedCar.isAppInForeground()) {
                    EventBus.getDefault().post(AppConstants.EVENT_ZONE_OUT);
                } else {
                    NotificationHelper.createNotificationWithIntent(NotificationHelper.ZONE_OUT_NOTIFICATION_ID,
                            "You are out of your zone, please back to your Home", getBaseContext());
                }
                PreferenceUtility.getInstance(getBaseContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_IN_STATUS, false);
                PreferenceUtility.getInstance(getBaseContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_OUT_STATUS, true);
                CompliantBody compliantBody = new CompliantBody();
                compliantBody.setCompliant(0);
                compliantBody.setReason("out of Zone");
                compliantBody.setZoneStatus(0);
                ServiceApi.getInstance().sendCompliantStatus(compliantBody);
                Logger.i("User new location is " + lastLocationInfo);
                Logger.i("Out of Zone ----> Distance is :" + mDistanceZoneIn);
                countpaquets = 0;
                lastPaquet = "zoneOut";
            } else {
                countpaquets = 0;
                lastPaquet = "zoneOut";
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    boolean checkMacExistance(List<MTPeripheral> peripherals, String macStored) {
        boolean isExist = false;
        for (int i = 0; i < peripherals.size(); i++) {
            if (macStored.equals(peripherals.get(i).mMTFrameHandler.getMac())) {
                isExist = true;
            }
        }
        return isExist;

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            double x = mGravity[0];
            double y = mGravity[1];
            double z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(x * x + y * y + z * z);
            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (hitCount <= SAMPLE_SIZE) {
                hitCount++;
                hitSum += Math.abs(mAccel);
            } else {
                hitResult = hitSum / SAMPLE_SIZE;

                Log.d(getClass().getName(), String.valueOf(hitResult));
                if (hitResult > THRESHOLD) {
                    paquetMovingReceived++;
                } else if (hitResult < THRESHOLD) {
                    paquetStableReceived++;
                }

                int movStatus = PreferenceUtility.getInstance(this).getInteger(PreferenceUtility.MOVEMENT_ACCLRMTR_STATUS, 0);
                if (paquetMovingReceived > 5) {
                    if (movStatus == 0) {
                        PreferenceUtility.getInstance(this).putInteger(PreferenceUtility.MOVEMENT_ACCLRMTR_STATUS, 1);
                        LoggerDataSaver.putNewAccelerometerStatusToLogFile(1);
                    }

                    sensorManager.unregisterListener(this);
                    paquetMovingReceived = 0;

                } else if (paquetStableReceived > 5) {
                    if (movStatus == 1) {
                        PreferenceUtility.getInstance(this).putInteger(PreferenceUtility.MOVEMENT_ACCLRMTR_STATUS, 0);
                        LoggerDataSaver.putNewAccelerometerStatusToLogFile(0);
                    }

                    sensorManager.unregisterListener(this);
                    paquetStableReceived = 0;

                }
                hitCount = 0;
                hitSum = 0;
                hitResult = 0;
            }
        }

    }

    public static boolean isDeviceLocked(Context context) {
        boolean isLocked = false;

        // First we check the locked state
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean inKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode();

        if (inKeyguardRestrictedInputMode) {
            isLocked = true;

        } else {
            // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
            // so we need to check if screen on for this case

            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                isLocked = !powerManager.isInteractive();
            } else {
                //noinspection deprecation
                isLocked = !powerManager.isScreenOn();
            }
        }
        return isLocked;
    }

    public static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }


    /**
     *
     */
    private class AsyncTaskRunner extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean internetStatus = isNetworkConnected();

            return internetStatus;
        }


        @Override
        protected void onPostExecute(Boolean internetStatus) {
            boolean internetStatusPref = PreferenceUtility.getInstance(MyLocationService.this).getBoolean(PreferenceUtility.INTERNET_STATUS, false);
            if (internetStatusPref != internetStatus) {
                PreferenceUtility.getInstance(MyLocationService.this).putBoolean(PreferenceUtility.INTERNET_STATUS, internetStatus);
                LoggerDataSaver.putNewInternetStatusToLogFile(internetStatus);
            }
        }
    }

    private void scanWifiSuccess() {
        List<android.net.wifi.ScanResult> results = mWifiManager.getScanResults();
//        if (results.size() > 0) {
//            Logger.i("Wifi list : " + results.size());
//        }
        for (android.net.wifi.ScanResult scanResult : results) {
            if (!scanResult.SSID.equals(lastWifiDetected)) {
                Logger.i("Wifi detected : " + scanResult.SSID);
            }
            lastWifiDetected = scanResult.SSID;
            //writeListOfWifi(scanResult.SSID);
        }
    }

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanWifiSuccess();
            } else {
                // scan failure handling
                scanFailure();
            }
        }
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<android.net.wifi.ScanResult> results = mWifiManager.getScanResults();
    }

    private boolean ensureBleExists() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Phone does not support BLE", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    protected boolean isBLEEnabled() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    private void showBLEDialog() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    }




}