package com.machinestalk.stayhome.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.database.LoggerDataSaver;
import com.machinestalk.stayhome.entities.BluetoothEntity;
import com.machinestalk.stayhome.entities.NotificationDataEntity;
import com.machinestalk.stayhome.entities.ZoneEntity;
import com.machinestalk.stayhome.fragments.BaseFragment;
import com.machinestalk.stayhome.fragments.HomeFragment;
import com.machinestalk.stayhome.responses.HomeConfigurationResponse;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.views.DashboardActivityView;
import com.orhanobut.logger.Logger;

import org.altbeacon.beacon.BeaconManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.machinestalk.stayhome.constants.KeyConstants.KEY_EXTRA_DATA;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_RESET_DATA;

/**
 * Created on 12/21/2016.
 */
public class DashboardActivity extends BaseActivity {

    public static final int REQUEST_ENABLE_BT = 1250;
    private DashboardActivityView dashboardView;
    private onMainCallbackListener mOnMainCallbackListener;
    private InsertZoneTask mInsertZonesTask;
    private boolean isDeviceExpired = false;
    private boolean isCreated = false;
    private String lastBleDetected;
    private ConnectivityManager mConnectivityManager;
    private List<BluetoothEntity> bluetoothEntities = new ArrayList<>();
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 112;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_FILE_WRITE = 1337;


    private BroadcastReceiver notificationPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((intent == null)) {
                return;
            }
            NotificationDataEntity notificationEntity = intent.getParcelableExtra(KEY_EXTRA_DATA);
            NotificationDataEntity notificationEntityReset = intent.getParcelableExtra(KEY_RESET_DATA);
            if (notificationEntity != null) {
                //showNotificationDialog(notificationEntity);
                Intent intentCheckIn = new Intent(DashboardActivity.this, CheckInActivity.class);
                intentCheckIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCheckIn);
            }
            if (notificationEntityReset != null) {
                //showNotificationDialog(notificationEntity);
                Intent intentCheckIn = new Intent(DashboardActivity.this, FaceRegistrationActivity.class);
                intentCheckIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCheckIn);

            }
        }
    };


    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
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
    };

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mComingFromNotification = false;


    @Override
    protected BaseView getViewForController(Controller controller) {

        return dashboardView = new DashboardActivityView(controller);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        isCreated = true;
        getUserInfo();
        fetchLastAppVersion();
        takePermissions();
        getConfiguration();
        getBraceletList();

        if (ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getBaseActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getBaseActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            }
        }


        EventBus.getDefault().register(this); // this == your class instance
        if (getSupportActionBar() != null) {
            //  getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_bar));
        }

        implementNotificationReceiver(getIntent());

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        for (Network network : mConnectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
            PreferenceUtility.getInstance(this).putInteger(PreferenceUtility.NETWORK_CONNECTION_MODE, networkInfo.getType());
        }


        if (!AppConfig.getInstance().getUser().isANewDevice()) {
            BluetoothAdapter.getDefaultAdapter().setName(AppConfig.getInstance().getUser().getDeviceId().substring(28));
            AppConfig.getInstance().StartBackgroundTasks(getBaseActivity());
        } else {
            //if (AppDatabase.getInstance(getBaseActivity()).getConfigurationDao().getAll().size() != 0 ){
            // getConfiguration();
            //}
        }

    }


    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        try {
            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);

            Object btManagerService = mServiceField.get(bluetoothAdapter);

            if (btManagerService != null) {
                bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
            }
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {

        }
        return bluetoothMacAddress;
    }


    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception exp) {

            exp.printStackTrace();
        }
        return "";
    }


    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //finish();
                        //System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    //finish();
                    //System.exit(0);
                }

            });
            builder.show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FILE_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
//                    Toast.makeText(this, "File access permission granted", Toast.LENGTH_LONG).show();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "File access permission denied", Toast.LENGTH_LONG).show();
                }
            }

        }
    }


    /**
     * @param intent
     */
    private void implementNotificationReceiver(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            boolean comeFromNotification = bundle.getBoolean(KeyConstants.KEY_COMING_FROM_NOTIFICATION);
            NotificationDataEntity notificationEntity = intent.getParcelableExtra(KEY_EXTRA_DATA);
            NotificationDataEntity notificationEntityReset = intent.getParcelableExtra(KEY_RESET_DATA);
            if (notificationEntity != null) {
                Intent intentCheckIn = new Intent(this, CheckInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentCheckIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentCheckIn);
            }

            if (comeFromNotification && notificationEntityReset != null) {
                //showNotificationDialog(notificationEntity);
                Intent intentCheckIn = new Intent(DashboardActivity.this, FaceRegistrationActivity.class);
                intentCheckIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCheckIn);
            }

            if (comeFromNotification) {
                ((DashboardActivity) getBaseActivity()).navigateToFragment(new HomeFragment(), R.string.ApDr_ApDr_SBtn_home);
            }
        }
    }

    public boolean isDrawerOpened() {
        return ((DashboardActivityView) view).isDrawerOpened();

    }


    @Subscribe
    public void getMessage(String event) {
        if (event.equals("1")) {
            ((DashboardActivityView) view).openDrawer();
        }
    }

    /**
     *
     */
    public void openDrawer() {
        ((DashboardActivityView) view).openDrawer();
    }

    @Override
    public boolean hasToolbar() {

        return true;
    }

    @Override
    public String getActionBarTitle() {

        return "";
    }

    @Override
    public void willStartCall() {

    }

    public Toolbar getToolbar() {

        return dashboardView.getToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = dashboardView.getDlDashboard();

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!AppConfig.getInstance().getUser().isANewDevice()) {
                    drawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void didFinishCall(boolean isSuccess) {

    }

    @Override
    public void onBackPressed() {
        tellFragments();
        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (!dashboardView.closeDrawer()) {
            if (index > 0) {
                super.onBackPressed();
            } else {
                finish();
            }
        }
    }

    /**
     *
     */
    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f != null && f instanceof BaseFragment)
                ((BaseFragment) f).onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        WiFiDirectBroadcastReceiver receiver = new WiFiDirectBroadcastReceiver();
//        registerReceiver(receiver, intentFilter);

//        startTracking();
//        ConnectedCar.getInstance().enableMonitoring();


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBluetoothReceiver, filter);
        LocalBroadcastManager.getInstance(getBaseActivity()).registerReceiver(notificationPushReceiver, new IntentFilter(KeyConstants.KEY_NOTIFICATION_MISC));
    }

    @Override
    protected void onPause() {
        ConnectedCar.getInstance().disableMonitoring();
        ((ConnectedCar) this.getApplicationContext()).setMonitoringActivity(null);
        super.onPause();
    }

    private void takePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("File Access Permission required")
                        .setMessage("Please allow the app to access your external storage for the log files to be saved. " +
                                "If you're not using the log-type as file, you can ignore this")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                takePermissions();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getBaseActivity(), "External storage access permission denied", Toast.LENGTH_LONG).show();
                            }
                        })
                        .create()
                        .show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_FILE_WRITE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    /**
     *
     */
    private void scanBluetooth() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean bleStatusPref = PreferenceUtility.getInstance(this).getBoolean(PreferenceUtility.BLUETOOTH_STATUS, false);
            boolean bleStatus = mBluetoothAdapter.isEnabled();
            if (bleStatusPref != bleStatus) {
                PreferenceUtility.getInstance(this).putBoolean(PreferenceUtility.BLUETOOTH_STATUS, bleStatus);
                LoggerDataSaver.putNewBleStatusToLogFile(bleStatus);
            }
            if (!bleStatus) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return;
            }
            mBluetoothAdapter.startDiscovery();

        } catch (Exception e) {
            Log.i(getClass().getName(), e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        Bundle res = null;


        switch (requestCode) {
            case AppConstants.RESULT_CODE_FROM_MAP:
                res = data.getExtras();
                showHouseholderDashboardFragment(res);
                break;
            case AppConstants.RESULT_CODE_FROM_SIDE_MENU_PROFILE:
                //	if (data.hasExtra(KEY_IS_IMAGE_CHANGED))
                //		((DashboardActivityView) view).onImageChanged();


            case REQUEST_ENABLE_BT: {
                //mBluetoothAdapter.startDiscovery();
            }
        }


    }

    public void showHouseholderDashboardFragment(Bundle args) {

        ((DashboardActivityView) view).switchToRoleDashboard(args);
    }


    public void showHomeDashboard(Bundle args) {

        ((DashboardActivityView) view).showHome();
    }

    public void navigateToFragment(BaseFragment targetClass, int tag) {
        ((DashboardActivityView) view).setFragmentOnLayout(targetClass, tag, null);
        dashboardView.closeDrawer();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                implementNotificationReceiver(intent);

            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBluetoothReceiver);
        LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(notificationPushReceiver);

    }


    /**
     * if zone exist in list
     * return position
     * else
     * return -1.
     *
     * @param zoneEntities
     * @param zoneEntity
     * @return
     */
    private int isZoneExistInList(List<ZoneEntity> zoneEntities, ZoneEntity zoneEntity) {
        for (int i = 0; i < zoneEntities.size(); i++) {
            if (zoneEntities.get(i).getId() == zoneEntity.getId()) {
                return i;
            }
        }
        return -1;
    }


    private void insertZones(final List<ZoneEntity> zoneEntities) {
        if (mInsertZonesTask != null && mInsertZonesTask.isCancelled() == false) {
            mInsertZonesTask.cancel(true);
        } else {
            mInsertZonesTask = new InsertZoneTask();
            mInsertZonesTask.execute(zoneEntities);
        }

    }

    class InsertZoneTask extends AsyncTask<Object, Void, Void> {
        List<ZoneEntity> zoneEntities;

        @Override
        protected Void doInBackground(Object... params) {
            if (isCancelled()) {
                return null;
            }
            zoneEntities = (List<ZoneEntity>) params[0];
            if (zoneEntities != null && zoneEntities.size() > 0) {
                AppDatabase.getInstance(getBaseActivity()).getZonesDao().deleteAllZone();
                AppDatabase.getInstance(getBaseActivity()).getZonesDao().insertAll(zoneEntities);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mInsertZonesTask = null;
        }
    }


    public void navigateSlideMenu(String fragmentTitle, boolean openFragment) {
        ((DashboardActivityView) view).onItemClick(fragmentTitle, openFragment);
    }


    public void setonCallbackListener(onMainCallbackListener listener) {
        mOnMainCallbackListener = listener;
    }

    public interface onMainCallbackListener {
        void onVehiclesReceived(boolean isFromResume);
    }


    public void getUserInfo() {
        ((ServiceFactory) serviceFactory).getUserService()
                .getUserInfo(AppConfig.getInstance().getUser().getCustomerId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(getBaseActivity(), new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {
                        //((Dash) view).showProgress();
                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                        // ((BraceletFragmentView) view).hideProgress();
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {

                        ZoneEntity zoneEntity = (ZoneEntity) response;
                        Log.i(getClass().getName(), "getUserInfo success");
                        if (zoneEntity != null && zoneEntity.getCenter() != null) {
                            AppDatabase.getInstance(getBaseActivity().getApplicationContext()).getZonesDao().insertZone(zoneEntity);
                        }
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        showToast(errorMessage);
                    }
                });
    }

    public void getConfiguration() {

        ConnectedCar.getInstance().getServiceFactory().getUserService()
                .getConfiguration(AppConfig.getInstance().getUser().getTenantId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {

                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                        ((DashboardActivityView) view).hideProgress();
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        Log.i(getClass().getName(), "get configuration list success");
                        List<com.machinestalk.stayhome.responses.Configuration> configurations = ((HomeConfigurationResponse) response).getList();
                        if (configurations != null && configurations.size() > 0) {
                            AppDatabase.getInstance(getBaseActivity()).getConfigurationDao().insertAll(configurations);
                        }
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        showToast(errorMessage);
                        //    Logger.i("user status request onFailure:");
                    }
                });
    }



}
