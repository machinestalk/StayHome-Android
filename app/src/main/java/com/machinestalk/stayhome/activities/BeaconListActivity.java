package com.machinestalk.stayhome.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.adapterdelegates.BaseRecyclerAdapter;
import com.machinestalk.stayhome.adapterdelegates.BraceletListDelegate;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.entities.BluetoothEntity;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.machinestalk.stayhome.utils.Util;
import com.minew.beaconplus.sdk.MTCentralManager;
import com.minew.beaconplus.sdk.MTPeripheral;
import com.minew.beaconplus.sdk.enums.BluetoothState;
import com.minew.beaconplus.sdk.enums.ConnectionStatus;
import com.minew.beaconplus.sdk.enums.FrameType;
import com.minew.beaconplus.sdk.exception.MTException;
import com.minew.beaconplus.sdk.frames.AccFrame;
import com.minew.beaconplus.sdk.frames.IBeaconFrame;
import com.minew.beaconplus.sdk.frames.MinewFrame;
import com.minew.beaconplus.sdk.frames.TlmFrame;
import com.minew.beaconplus.sdk.frames.UrlFrame;
import com.minew.beaconplus.sdk.interfaces.ConnectionStatueListener;
import com.minew.beaconplus.sdk.interfaces.GetPasswordListener;
import com.minew.beaconplus.sdk.interfaces.MTCentralManagerListener;
import com.minew.beaconplus.sdk.interfaces.OnBluetoothStateChangedListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameAccSensor;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameTLM;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameURL;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameiBeacon;

public class BeaconListActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 3;
    private static final int PERMISSION_COARSE_LOCATION = 2;
    @BindView(R.id.recycle)
    RecyclerView mRecycle;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;

    Activity activity = this;

    private MTCentralManager mMtCentralManager;
    //    private RecycleAdapter mAdapter;
    private Toolbar toolbar;
    private View mProgress;
    private Handler scanHandler = new Handler();
    ScheduledExecutorService scheduler;
    private boolean isOneTimeDetected = false;
    private List<MTPeripheral> mtBracelets = new ArrayList<>();
    private List<MTPeripheral> mEmptytBracelets = new ArrayList<>();
    private BaseRecyclerAdapter baseAdapter;
    private boolean firstTime = true;
    private Chronometer chronometer;
    long totalSeconds = 5;
    long totalReachedSeconds = 0;
    long intervalSeconds = 1;
    private static int countBracelet = 1;


    private Runnable stopProgress = new Runnable() {
        @Override
        public void run() {
//            if (firstTime)
            hideProgress();

            dataReceived(mtBracelets);
            Log.i("BLE_Scanner", "Stop Scan");
            firstTime = false;
        }
    };

    private Runnable startCheckToDeleteList = new Runnable() {
        @Override
        public void run() {
            totalReachedSeconds = 11;
//            mtBracelets.clear();
//            mtBracelets.addAll(mEmptytBracelets);
//            dataReceived(mtBracelets);
            Log.i("finish startCheckToDeleteList ", "finish startCheckToDeleteList: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_activity);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        mProgress = findViewById(R.id.view_beacon_activity_progress);
        chronometer = findViewById(R.id.chronometer);


        if (!ensureBleExists())
            finish();
        if (!isBLEEnabled()) {
            showBLEDialog();
        }
        initUI();
//        initView();
        initManager();
        getRequiredPermissions();
        initListener();
        setupToolbar();
    }

    public void dataReceived(List<MTPeripheral> mtPeripherals) {
//            this.mtPeripherals = mtPeripherals;
        baseAdapter.setDataList(this.mtBracelets);
        txtEmpty.setVisibility(this.mtBracelets.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        // call service
//                        mMtCentralManager.stopService();
//                        mMtCentralManager.startService();
//                        mMtCentralManager.startScan();
                        Log.i("start scan bracelets", "start scan bracelets");

                        runOnUiThread(new Thread(new Runnable() {
                            public void run() {
                                if (firstTime)
                                    showProgress();
                                txtEmpty.setVisibility(GONE);
                                scanHandler.postDelayed(stopProgress, 1500);
                            }
                        }));
                    }
                }, 0, 15, TimeUnit.SECONDS);

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
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                initData();
            } else {
                finish();
            }
        }
    }


    private void initUI() {
//        progress = findViewById(R.id.progress);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        baseAdapter = new BaseRecyclerAdapter(mtBracelets);
        baseAdapter.addAdapterDelegates(new BraceletListDelegate(this));
        mRecycle.setAdapter(baseAdapter);
        baseAdapter.notifyDataSetChanged();
    }

    private void initView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
//        mAdapter = new RecycleAdapter();
//        mRecycle.setAdapter(mAdapter);
        //mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
    }

    private void setupToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Scan bracelet");
        getSupportActionBar().setTitle("Scan bracelet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void deletOldBracelets() {

        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameiBeacon");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameAccSensor");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameTLM");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("FrameURL");
        AppDatabase.getInstance(getApplicationContext()).getBluetoothDao().deleteByFrameName("Other Frame");
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
                    && !StringUtility.isEmptyOrNull(bluetoothEntity.getTemperature())
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


    private void initListener() {
        mMtCentralManager.setMTCentralManagerListener(new MTCentralManagerListener() {

            @Override
            public void onScanedPeripheral(final List<MTPeripheral> peripherals) {
                Log.i("start scan bracelet size ", "start scan bracelet size is: " + peripherals.size());


                if (totalReachedSeconds == 11) {
                    totalReachedSeconds = 0;
                }
                if (peripherals.size() > 0) {
                    mtBracelets.clear();
                    mtBracelets.addAll(peripherals);
                    Log.i("startCheckToDeleteList started", "startCheckToDeleteList started : ");
//                    scanHandler.removeCallbacksAndMessages(startCheckToDeleteList);
//                    scanHandler.postDelayed(startCheckToDeleteList, 11000);
                }

                storeBluetoothBracelets(peripherals);
                getAllBracelets(peripherals);


            }
        });

//        baseAdapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                MTPeripheral mtPeripheral = baseAdapter.getData(position);
//                mMtCentralManager.connect(mtPeripheral, connectionStatueListener);
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onAddItemClick(View view, int position) {
//                MTPeripheral mtPeripheral = baseAdapter.getData(position);
//                if (mtPeripheral != null) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra(AppConstants.BRACELET_MAC_ADDRESS, mtPeripheral.mMTFrameHandler.getMac());
//                    setResult(Activity.RESULT_OK, returnIntent);
//                    finish();
//                    // addMacAddress(mtPeripheral.mMTFrameHandler.getMac());
//                }
//            }
//        });

    }


    @Override
    public void onRequestPermissionsResult(int code, String permissions[], int[] grantResults) {
        if (code == PERMISSION_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
            } else {
                finish();
            }
        }
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

    private void getRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COARSE_LOCATION);
        } else {
            initData();
        }
    }

    private void initData() {
        //三星手机系统可能会限制息屏下扫描，导致息屏后无法获取到广播数据
        mMtCentralManager.startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private ConnectionStatueListener connectionStatueListener = new ConnectionStatueListener() {
        @Override
        public void onUpdateConnectionStatus(final ConnectionStatus connectionStatus, final GetPasswordListener getPasswordListener) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (connectionStatus) {
                        case CONNECTING:
                            Log.e("tag", "CONNECTING");
                            Toast.makeText(BeaconListActivity.this, "CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case CONNECTED:
                            Log.e("tag", "CONNECTED");
                            Toast.makeText(BeaconListActivity.this, "CONNECTED", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGINFO:
                            Log.e("tag", "READINGINFO");
                            Toast.makeText(BeaconListActivity.this, "READINGINFO", Toast.LENGTH_SHORT).show();
                            break;
                        case DEVICEVALIDATING:
                            Log.e("tag", "DEVICEVALIDATING");
                            Toast.makeText(BeaconListActivity.this, "DEVICEVALIDATING", Toast.LENGTH_SHORT).show();
                            break;
                        case PASSWORDVALIDATING:
                            Log.e("tag", "PASSWORDVALIDATING");
                            Toast.makeText(BeaconListActivity.this, "PASSWORDVALIDATING", Toast.LENGTH_SHORT).show();
                            String password = "minew123";
                            getPasswordListener.getPassword(password);
                            break;
                        case SYNCHRONIZINGTIME:
                            Log.e("tag", "SYNCHRONIZINGTIME");
                            Toast.makeText(BeaconListActivity.this, "SYNCHRONIZINGTIME", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGCONNECTABLE:
                            Log.e("tag", "READINGCONNECTABLE");
                            Toast.makeText(BeaconListActivity.this, "READINGCONNECTABLE", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGFEATURE:
                            Log.e("tag", "READINGFEATURE");
                            Toast.makeText(BeaconListActivity.this, "READINGFEATURE", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGFRAMES:
                            Log.e("tag", "READINGFRAMES");
                            Toast.makeText(BeaconListActivity.this, "READINGFRAMES", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGTRIGGERS:
                            Log.e("tag", "READINGTRIGGERS");
                            Toast.makeText(BeaconListActivity.this, "READINGTRIGGERS", Toast.LENGTH_SHORT).show();
                            break;
                        case COMPLETED:
                            Log.e("tag", "COMPLETED");
                            Toast.makeText(BeaconListActivity.this, "COMPLETED", Toast.LENGTH_SHORT).show();
                            break;
                        case CONNECTFAILED:
                        case DISCONNECTED:
                            Log.e("tag", "DISCONNECTED");
                            Toast.makeText(BeaconListActivity.this, "DISCONNECTED", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });
        }

        @Override
        public void onError(MTException e) {
            Log.e("tag", e.getMessage());
        }
    };


    boolean checkMacExistance(List<MTPeripheral> peripherals) {
        boolean isExist = false;
        for (int i = 0; i < peripherals.size(); i++) {
            String macStored = PreferenceUtility.getInstance(getApplicationContext()).getString(KeyConstants.KEY_MAC_ADRESS, "");
            if (StringUtility.isEmptyOrNull(macStored)) {
                if (macStored.equals(peripherals.get(i).mMTFrameHandler.getMac())) {
                    isExist = true;
                }
            }

        }
        return isExist;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMtCentralManager.stopScan();
        mMtCentralManager.stopService();
        if (scheduler != null)
            scheduler.shutdown();
    }


    public void showProgress() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(VISIBLE);
    }

    public void hideProgress() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(GONE);
    }

    /**
     *
     */
    private void showErrorDialog() {
        final AlertBottomDialog alertBottomDialog = new AlertBottomDialog(this);
        alertBottomDialog.setTvSubTitle(getString(R.string.bracelet_fragment_dialog_error_mrssage_label));
        alertBottomDialog.setIvAvatar(R.drawable.red_bluetooth);
        alertBottomDialog.setTopBarInvisible();
        alertBottomDialog.setCancelable(false);
        alertBottomDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertBottomDialog.dismiss();
            }
        }, 3000);
    }


}
