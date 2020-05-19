package com.machinestalk.stayhome.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.database.LoggerDataSaver;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.responses.LastDayResponse;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.utils.DateUtil;
import com.machinestalk.stayhome.utils.LocationManager;
import com.machinestalk.stayhome.utils.RxPermissionUtil;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.HomeFragmentView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.WIFI_SERVICE;
import static com.machinestalk.android.utilities.PreferenceUtility.IS_MESSAGE_Z_OUT_STATUS;
import static com.machinestalk.stayhome.config.AppConfig.batteryDialog;
import static com.machinestalk.stayhome.config.AppConfig.bluetoothDialog;
import static com.machinestalk.stayhome.config.AppConfig.braceletDialog;
import static com.machinestalk.stayhome.config.AppConfig.locationDialog;
import static com.machinestalk.stayhome.config.AppConfig.wifiDialog;
import static com.machinestalk.stayhome.config.AppConfig.zoneOutDialog;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_BROADCAST_BLUETOOTH;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_BROADCAST_GPS;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_BROADCAST_WIFI;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_MAC_ADRESS;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_WIFI_ENABLED;

public class HomeFragment extends BaseFragment implements Controller {

    private BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_ENABLE_BT = 1250;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected BaseView getViewForController(Controller controller) {
        return new HomeFragmentView(controller);
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getBaseActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {

        try {

            Fragment fragment = getVisibleFragment();

            if (fragment instanceof HomeFragment && !((DashboardActivity) getBaseActivity()).isDrawerOpened()) {
                switch (event) {
                    case AppConstants.EVENT_BRACELET_NOT_DETECTED: {

                        AppConfig.getInstance().showBraceletNotDetectedDialog(getBaseActivity());

                        PreferenceUtility.getInstance(getBaseActivity()).putBoolean(PreferenceUtility.BRACELET_DETECTED, false);
                    }
                    break;
                    case AppConstants.EVENT_BRACELET_DETECTED: {
                        if (!getBaseActivity().isFinishing()) {
                            hideBraceletDialog();
                        }
                        PreferenceUtility.getInstance(getBaseActivity()).putBoolean(PreferenceUtility.BRACELET_DETECTED, true);
                    }
                    break;

                    case AppConstants.EVENT_ZONE_OUT: {
                        if (braceletDialog != null && !braceletDialog.isShowing()) {
//                        hideBatteryDialog();
//                        hideBluetoothDialog();
//                        hideGpsDialog();
//                        hideWifiDialog();
                            AppConfig.getInstance().showZoneOutDialog(getBaseActivity());
                        }
                    }
                    break;
                    case AppConstants.EVENT_ZONE_IN: {
                        if (!getBaseActivity().isFinishing()) {
                            hideZoneOutDialog();
                        }
                        PreferenceUtility.getInstance(getBaseActivity()).getBoolean(IS_MESSAGE_Z_OUT_STATUS, false);
                    }
                    break;
                    case AppConstants.EVENT_LOCATION_DISABLED: {
                        if (braceletDialog != null && !braceletDialog.isShowing() && zoneOutDialog != null && !zoneOutDialog.isShowing()) {
//                        hideBatteryDialog();
//                        hideBluetoothDialog();
//                        hideWifiDialog();
                            AppConfig.getInstance().showGpsDisabledDialog(getBaseActivity());
                        }

                        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_BROADCAST_GPS, true);
                    }
                    break;
                    case AppConstants.EVENT_LOCATION_ENABLED: {
                        if (!getBaseActivity().isFinishing()) {
                            hideGpsDialog();
                        }
                    }
                    break;
                    case AppConstants.EVENT_BLUETOOTH: {
                        if (braceletDialog != null && !braceletDialog.isShowing() && zoneOutDialog != null && !zoneOutDialog.isShowing() &&
                                locationDialog != null && !locationDialog.isShowing()) {
//                        hideBatteryDialog();
//                        hideWifiDialog();
                            AppConfig.getInstance().showBluetoothDisabledDialog(getBaseActivity());
                        }
                        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_BROADCAST_BLUETOOTH, true);
                    }
                    break;
                    case AppConstants.EVENT_BLUETOOTH_ENABLED: {
                        if (!getBaseActivity().isFinishing()) {
                            hideBluetoothDialog();
                        }
                    }
                    break;
                    case AppConstants.EVENT_BATTERY_LOW: {
                        if (batteryDialog != null && !braceletDialog.isShowing()) {
                            //hideWifiDialog();
                            AppConfig.getInstance().showBatteryLowDialog(getBaseActivity());
                        }
                    }
                    break;
                    case AppConstants.EVENT_BATTERY_FINE: {
                        if (!getBaseActivity().isFinishing()) {
                            hideBatteryDialog();
                        }
                    }
                    break;
                    case AppConstants.EVENT_WIFI_DISABLED: {
                        if (braceletDialog != null && !braceletDialog.isShowing() && zoneOutDialog != null && !zoneOutDialog.isShowing() &&
                                locationDialog != null && !locationDialog.isShowing() && bluetoothDialog != null && !bluetoothDialog.isShowing() &&
                                batteryDialog != null && !batteryDialog.isShowing()) {
                            AppConfig.getInstance().showWifiDisabledDialog(getBaseActivity());
                        }
                        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_BROADCAST_WIFI, true);
                    }
                    break;
                    case AppConstants.EVENT_WIFI_ENABLED: {
                        if (!getBaseActivity().isFinishing()) {
                            hideWifiDialog();
                        }
                    }
                    break;

                }
            }



        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }

    }
    private void hideBraceletDialog() {
        if (braceletDialog != null && braceletDialog.isShowing()) {
            braceletDialog.dismiss();
        }
    }



        @Override
    public void onResume() {
        super.onResume();
        //PreferenceUtility.getInstance(ConnectedCar.getInstance()).putLong(PreferenceUtility.EVENT_LAST_DAY, 0);
        long lastDay = PreferenceUtility.getInstance(ConnectedCar.getInstance()).getLong(PreferenceUtility.EVENT_LAST_DAY, 0);
        //long lastDay = 0l;
        if (lastDay > 0L) {
            String lastDate = DateUtil.getDateFromTimeStamp(lastDay, DateUtil.STANDARD_FORMAT_PATTERN);
            String currentDate = Util.getCurrentDate();
            int diff = Integer.parseInt(Util.getDifferenceBtwDates(currentDate, lastDate));
            ((HomeFragmentView) view).checkRemainingDays(diff);
        } else {
            getLastDay();
        }
        requestPermissionForBluetooth();

        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);


        if (!PreferenceUtility.getInstance(ConnectedCar.getInstance()).getBoolean(KEY_WIFI_ENABLED, false)
                && !wifiManager.isWifiEnabled() && !((DashboardActivity) getBaseActivity()).isDrawerOpened()) {
            AppConfig.getInstance().showWifiDisabledDialog(getBaseActivity());
        }
        if (PreferenceUtility.getInstance(getBaseActivity()).getBoolean(AppConstants.EVENT_BATTERY_DOWN, false)
                && getBatteryPercentage(getBaseActivity()) < 10 && !((DashboardActivity) getBaseActivity()).isDrawerOpened()) {

            AppConfig.getInstance().showBatteryLowDialog(getBaseActivity());

        }

        if (!PreferenceUtility.getInstance(getBaseActivity()).getBoolean(AppConstants.EVENT_LOCATION_ENABLED, false)
                && !LocationManager.isLocationEnabled(getBaseActivity())&& !((DashboardActivity) getBaseActivity()).isDrawerOpened() ) {

            AppConfig.getInstance().showGpsDisabledDialog(getBaseActivity());

        }

        if (PreferenceUtility.getInstance(getBaseActivity()).getBoolean(IS_MESSAGE_Z_OUT_STATUS, false)
                && !((DashboardActivity) getBaseActivity()).isDrawerOpened()) {

            AppConfig.getInstance().showZoneOutDialog(getBaseActivity());

        }

         if (!PreferenceUtility.getInstance(getBaseActivity()).getBoolean(IS_MESSAGE_Z_OUT_STATUS, false)
                 && !((DashboardActivity) getBaseActivity()).isDrawerOpened()) {
            if (getBaseActivity() != null && !getBaseActivity().isFinishing() && zoneOutDialog != null && zoneOutDialog.isShowing()) {
                zoneOutDialog.dismiss();
            }
        }

         String macAdress =   PreferenceUtility.getInstance(getBaseActivity()).getString(KEY_MAC_ADRESS, "");

            if (!PreferenceUtility.getInstance(getBaseActivity()).getBoolean(PreferenceUtility.BRACELET_DETECTED, false)
                    && !StringUtility.isEmptyOrNull(macAdress) && !((DashboardActivity) getBaseActivity()).isDrawerOpened()){
                AppConfig.getInstance().showBraceletNotDetectedDialog(getBaseActivity());
            }

        }

    @Override
    public void onStop() {
        super.onStop();
        Util.hideSoftKeyboard(getBaseActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
    private void getLastDay() {

        User user = AppConfig.getInstance().getUser();
        ((ServiceFactory) serviceFactory).getUserService()
                .getLastDay(user.getCustomerId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(getBaseActivity(), new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {
//                        ((HomeFragmentView) view).showProgress();
                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
//                        ((HomeFragmentView) view).hideProgress();
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {

                        LastDayResponse lastDayResponse = (LastDayResponse) response;
                        if (lastDayResponse != null && lastDayResponse.getLastDay() != null) {
                            long lastD = lastDayResponse.getLastDay() * 1000;
                            PreferenceUtility.getInstance(getBaseActivity()).putLong(PreferenceUtility.EVENT_LAST_DAY, lastD);
                            String lastDate = DateUtil.getDateFromTimeStamp(lastD, DateUtil.STANDARD_FORMAT_PATTERN);
                            String currentDate = Util.getCurrentDate();
                            int diff = Integer.parseInt(Util.getDifferenceBtwDates(currentDate, lastDate));

                            ((HomeFragmentView) view).checkRemainingDays(diff);
                        }

                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        showToast(errorMessage);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT: {
                    mBluetoothAdapter.startDiscovery();
                }
            }
        }
//        else if (requestCode == RESULT_CANCELED){
//            mBluetoothAdapter.startDiscovery();
//        }

    }

    /**
     *
     */
    private void requestPermissionForBluetooth() {
        RxPermissions rxPermissions = new RxPermissions(this);
        RxPermissionUtil.getInstance().checkRxPermission(rxPermissions, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                new RxPermissionUtil.onPermissionListener() {
                    @Override
                    public void onPermissionAllowed() {
                        scanBluetooth();
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });
    }

    /**
     *
     */
    private void scanBluetooth() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean bleStatusPref = PreferenceUtility.getInstance(getBaseActivity()).getBoolean(PreferenceUtility.BLUETOOTH_STATUS, false);
            boolean bleStatus = mBluetoothAdapter.isEnabled();
            if (bleStatusPref != bleStatus) {
                PreferenceUtility.getInstance(getBaseActivity()).putBoolean(PreferenceUtility.BLUETOOTH_STATUS, bleStatus);
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

    private void hideBluetoothDialog() {
        if (bluetoothDialog != null && bluetoothDialog.isShowing()) {
            bluetoothDialog.dismiss();
        }
    }

    private void hideWifiDialog() {
        if (wifiDialog != null && wifiDialog.isShowing()) {
            wifiDialog.dismiss();
        }
    }

    private void hideGpsDialog() {
        if (locationDialog != null && locationDialog.isShowing()) {
            locationDialog.dismiss();
        }
    }

    private void hideZoneOutDialog() {
        if (zoneOutDialog != null && zoneOutDialog.isShowing()) {
            zoneOutDialog.dismiss();
        }
    }

    private void hideBatteryDialog() {
        if (batteryDialog != null && batteryDialog.isShowing()) {
            batteryDialog.dismiss();
        }
    }


    private boolean isBraceletDialogShowing() {
        return (braceletDialog != null && braceletDialog.isShowing());
    }
}
