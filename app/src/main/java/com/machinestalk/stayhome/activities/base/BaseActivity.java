package com.machinestalk.stayhome.activities.base;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.machinestalk.android.components.Loader;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.ServiceCall;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.activities.LandingActivity;
import com.machinestalk.stayhome.broadcastreceivers.GpsStateReceiver;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.dialogs.base.BaseDialog;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.helpers.NotificationHelper;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.listeners.PermissionGrantedListener;
import com.machinestalk.stayhome.responses.BraceletListResponse;
import com.machinestalk.stayhome.responses.Configuration;
import com.machinestalk.stayhome.responses.HomeConfigurationResponse;
import com.machinestalk.stayhome.responses.RefreshTokenResponse;
import com.machinestalk.stayhome.service.ServiceApi;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.service.ServiceProtocol;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.machinestalk.stayhome.service.body.DeviceTokenBody;
import com.machinestalk.stayhome.service.body.RefreshTokenForm;
import com.machinestalk.stayhome.utils.LocationManager;
import com.machinestalk.stayhome.utils.MTContextWrapper;
import com.machinestalk.stayhome.utils.Util;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.machinestalk.stayhome.constants.KeyConstants.KEY_MAC_ADRESS;

public abstract class BaseActivity extends com.machinestalk.android.activities.BaseActivity
        implements ServiceSecondaryEventHandler, GpsStateReceiver.GpsStateChangeListener {

    PermissionGrantedListener permissionGrantedListener;
    private Dialog permissionDialog;
    private HashMap<ServiceCall, ServiceCallback> mListCallback = new HashMap<>();
    private static boolean isAppWentToBg = false;
    private static boolean isWindowFocused = false;
    private static boolean isBackPressed = false;
    protected static final String TAG = BaseActivity.class.getName();
    private EventBus myEventBus = EventBus.getDefault();
    private static boolean isLocationEnabledOneTime = false;
    private static boolean isLocationDisabledOneTime = false;

    private GpsStateReceiver mgGpsStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mgGpsStateReceiver = new GpsStateReceiver(this);
        getBaseActivity().registerReceiver(mgGpsStateReceiver, new IntentFilter(android.location.LocationManager.PROVIDERS_CHANGED_ACTION));
        AppConfig.getInstance().createAllAlertDialogs(this);
    }


    @Override
    public void onGpsStateChanged() {
        if (LocationManager.hasLocationPermission(getBaseActivity())
                && LocationManager.isLocationEnabled(getBaseActivity())) {
            //moveToCurrentLocation();
            if (!isLocationEnabledOneTime) {
                isLocationDisabledOneTime = false;
                Logger.i("GPS Status is Enabled");
                EventBus.getDefault().post(AppConstants.EVENT_LOCATION_ENABLED);
                PreferenceUtility.getInstance(getBaseContext()).putBoolean(AppConstants.EVENT_LOCATION_ENABLED, true);
                isLocationEnabledOneTime = true;
            }
        } else {
            if (!isLocationDisabledOneTime) {
                isLocationEnabledOneTime = false;
                Logger.i("GPS Status is Disabled");
                CompliantBody compliantBody = new CompliantBody();
                compliantBody.setCompliant(0);
                compliantBody.setGpsEnabled(0);
                ServiceApi.getInstance().sendCompliantStatus(compliantBody);
                PreferenceUtility.getInstance(getBaseContext()).putBoolean(AppConstants.EVENT_LOCATION_ENABLED, false);
                if (ConnectedCar.isAppInForeground()) {
                    EventBus.getDefault().post(AppConstants.EVENT_LOCATION_DISABLED);
                } else {
                    NotificationHelper.createNotificationWithIntent(NotificationHelper.GPS_DISABLED_NOTIFICATION_ID,
                            "Your GPS is disabled, please enable your GPS ", getBaseContext());
                }
                isLocationDisabledOneTime = true;
            }

        }

    }


    public boolean isIsAppWentToBg() {
        return isAppWentToBg;
    }

    public static void setIsAppWentToBg(boolean isAppWentToBg) {
        BaseActivity.isAppWentToBg = isAppWentToBg;
    }

    public boolean isIsWindowFocused() {
        return isWindowFocused;
    }

    public static void setIsWindowFocused(boolean isWindowFocused) {
        BaseActivity.isWindowFocused = isWindowFocused;
    }

    public boolean isIsBackPressed() {
        return isBackPressed;
    }

    public static void setIsBackPressed(boolean isBackPressed) {
        BaseActivity.isBackPressed = isBackPressed;
    }


    @Override
    protected ServiceFactory getServiceFactory() {

        return ConnectedCar.getInstance().getServiceFactory();
    }

    public void switchActivity(Class<? extends BaseActivity> activity) {

        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart isAppWentToBg " + isAppWentToBg);
        applicationWillEnterForeground();
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop ");
        applicationdidenterbackground();
    }

    public void applicationdidenterbackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
        }
    }

    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
        }
    }

    private void navigateToDashboard(User user) {
        AppConfig.getInstance().setUser(user);
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        /* Do something */

        /*
         * Priority of displaying alert dialogs
         * 1 Bracelet Not Detected
         * 2 Out of Zone
         * 3 GPS deactivated
         * 4 Bluetooth Deactivated
         * 5 Battery Low
         * 6 Wifi Deactivated
         */


        try {


        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }


    }

    /**
     * To Logout user from the app, clears all cached profile information
     */
    public void onLogout() {
        displayLogOutDialog();

    }

    public void clearNotificationFromBar() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    public void clearAppData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                todoBeforeLogout();
                switchActivity(LandingActivity.class);
                finish();
            }
        }, 100);

    }

    protected void todoBeforeLogout() {

        ConnectedCar.getInstance().deleteDataBase();
        ConnectedCar.getInstance().getFabStates().clear();
        AppConfig.getInstance().clearPreferenceData();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(MTContextWrapper.wrap(newBase, AppConfig.getInstance().getLanguage()));
    }

    public void showDialog(BaseDialog dialog) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(dialog, dialog.getTagText());
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void addServiceCallBackToMap(final ServiceCall serviceCall, final ServiceCallback serviceCallback) {
        mListCallback.put(serviceCall, serviceCallback);
    }

    public HashMap<ServiceCall, ServiceCallback> getServiceCallBackMap() {
        return mListCallback;
    }

    public void refreshToken(final ServiceCall serviceCall, final ServiceCallback serviceCallback) {
        RefreshTokenForm refreshTokenForm = new RefreshTokenForm();
        refreshTokenForm.setRefreshToken(AppConfig.getInstance().getUser().getRefreshToken());

        addServiceCallBackToMap(serviceCall, serviceCallback);
        ((ServiceFactory) serviceFactory).getUserService()
                .refreshToken(refreshTokenForm)
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, this) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        ServiceProtocol.getInstance().setRefreshInProgress(false);
                        RefreshTokenResponse refreshTokenResponse = (RefreshTokenResponse) response;
                        AppConfig.getInstance().setUserUpdateAfterRefreshToken(refreshTokenResponse.getToken(), refreshTokenResponse.getRefreshToken());

                        for (Map.Entry<ServiceCall, ServiceCallback> entry : getServiceCallBackMap().entrySet()) {
                            entry.getValue().refresh();
                            entry.getKey().clone().enqueue(entry.getValue());
                        }

                        mListCallback.clear();
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        ServiceProtocol.getInstance().setRefreshInProgress(false);
                        clearNotificationFromBar();
                        clearAppData();
                    }
                });
    }

    /**
     *
     */
    public void updateDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();


                DeviceTokenBody deviceTokenBody = new DeviceTokenBody();
                deviceTokenBody.setFirebaseToken(newToken);

                ((ServiceFactory) serviceFactory).getUserService().updateDeviceToken(deviceTokenBody, AppConfig.getInstance().getUser().getDeviceId())
                        .enableRetry(false).enqueue(new ServiceCallback(BaseActivity.this, BaseActivity.this) {
                    @Override
                    protected void onSuccess(Object response, int code) {

                        Log.i(BaseActivity.TAG, "update device token success");
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        Log.i(BaseActivity.TAG, "update device token failed ");
                    }
                });
            }
        });
    }


    public void setPermissionGrantedListener(PermissionGrantedListener permissionGrantedListener) {
        this.permissionGrantedListener = permissionGrantedListener;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissionGrantedListener != null)
            permissionGrantedListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void logoutUserCall() {
        ((ServiceFactory) serviceFactory).getUserService()
                .logoutUser().
                enableRetry(false)
                .enqueue(new ServiceCallback(this, new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {

                        Loader.showLoader(BaseActivity.this, getString(R.string.Gen_Gen_lbl_please_wait), "");
                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                        Loader.hideLoader();
                    }
                }) {

                    @Override
                    protected void onSuccess(Object response, int code) {
                        Log.i(BaseActivity.TAG, "user logout successfully");
                        clearAppData();
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        Log.i(BaseActivity.TAG, "user logout failed");
                    }
                });
    }


    public void showLocationDialog() {

        if (permissionDialog == null) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.Gen_Gen_lbl_location_request_permission));
            dialog.setPositiveButton(getResources().getString(R.string.Gen_Gen_lbl_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                    permissionDialog.dismiss();

                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.Gen_Gen_lbl_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                    Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivity(myIntent);
                    permissionDialog.dismiss();
                }
            });

            permissionDialog = dialog.create();
        }
        if (permissionDialog != null && !permissionDialog.isShowing())
            permissionDialog.show();
    }

    public void showLocationToggleDialog(final ToggleButton toggleLocation) {

        try {
            if (!isFinishing()) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getBaseActivity());
                dialog.setCancelable(false);
                dialog.setMessage(getBaseActivity().getResources().getString(R.string.Gen_Gen_lbl_gps_network_not_enabled));
//                dialog.setPositiveButton(getBaseActivity().getResources().getString(R.string.Gen_Gen_lbl_ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//
//                        paramDialogInterface.dismiss();
//                        toggleLocation.setChecked(false);
//                    }
//                });
                dialog.setNegativeButton(getBaseActivity().getResources().getString(R.string.Gen_Gen_lbl_settings), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getBaseActivity().startActivity(myIntent);
                        toggleLocation.setChecked(true);
                    }
                });


                dialog.show();
            }

        } catch (Exception e) {
        }

    }


    public void showURLHack() {

        final Dialog dialog = new Dialog(BaseActivity.this);
        dialog.setContentView(R.layout.view_change_url);
        dialog.setTitle(getString(R.string.SgIn_SgIn_lbl_server_url_dialog_title));

        final AppCompatEditText serverUrlEditText = (AppCompatEditText) dialog.findViewById(R.id.view_change_base_url_edit_text);
        final AppCompatEditText identityUrlEditText = (AppCompatEditText) dialog.findViewById(R.id.view_change_identity_url_edit_text);
        AppCompatButton confirmChangeUrl = (AppCompatButton) dialog.findViewById(R.id.view_change_url_confirm_button);

        serverUrlEditText.setText(AppConfig.getInstance().getUrl());
        identityUrlEditText.setText(AppConfig.getInstance().getIdentityUrl());
        if (serverUrlEditText.getText().length() > 0) {
            serverUrlEditText.setSelection(serverUrlEditText.getText().length());
        }
        confirmChangeUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtility.isEmptyOrNull(serverUrlEditText.getText().toString()) && StringUtility.isEmptyOrNull(identityUrlEditText.getText().toString())) {
                    dialog.dismiss();
                }
                if (!URLUtil.isValidUrl(serverUrlEditText.getText().toString()) || !URLUtil.isValidUrl(identityUrlEditText.getText().toString())) {
                    Toast.makeText(BaseActivity.this, "invalid url", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtility.isEmptyOrNull(serverUrlEditText.getText().toString())) {
                    AppConfig.getInstance().setUrl(serverUrlEditText.getText().toString());
                }
                if (!StringUtility.isEmptyOrNull(identityUrlEditText.getText().toString())) {
                    AppConfig.getInstance().setIdentityUrl(identityUrlEditText.getText().toString());
                }
                ConnectedCar.getInstance().initServiceFactory(true);
                getBaseActivity().initializeServiceFactory();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     *
     */
    public void fetchLastAppVersion() {
        ((ServiceFactory) serviceFactory).getUserService()
                .getLastVersion(AppConfig.getInstance().getUser().getTenantId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, this) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        try {
                            PackageInfo pInfo = BaseActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                            List<com.machinestalk.stayhome.responses.Configuration> configurationList = ((HomeConfigurationResponse) response).getList();
                            String currentVersion = pInfo.versionName;
                            Log.i(TAG, "current version :" + currentVersion);
                            String urlApp = "";
                            String lastVersion = "";
                            if (configurationList != null && configurationList.size() > 0) {
                                for (Configuration configuration : configurationList) {
                                    if (configuration.getKey().equals(Configuration.KEY_LAST_VERSION_ANDROID)) {
                                        lastVersion = configuration.getLastVersionAndroid();
                                    }

                                    if (configuration.getKey().equals(Configuration.KEY_ANDROID_URL_APP)) {
                                        urlApp = configuration.getAndroidUrlApp();
                                    }
                                }
                            }

                            if (StringUtility.isEmptyOrNull(urlApp)){
                                urlApp = "https://play.google.com/store/apps";
                            }

                            if (StringUtility.isEmptyOrNull(urlApp)){
                                lastVersion = currentVersion;
                            }

                            if (!lastVersion.equals(currentVersion)) {
                                showForceUpdateDialog(urlApp);
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            Log.i(TAG, "error when get app version :" + e.getMessage());

                        }
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {

                        // showToast(errorMessage);
                    }
                });
    }


    /**
     *
     */
    public void getBraceletList() {

        ConnectedCar.getInstance().getServiceFactory().getUserService()
                .getBraceletList(AppConfig.getInstance().getUser().getCustomerId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {

                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        Log.i(getClass().getName(), "get connected date :" + response);
                        BraceletListResponse braceletListResponse = (BraceletListResponse) response;
                        if (braceletListResponse != null) {

                            long connectedTime = braceletListResponse.getConnectedTime();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm a", Locale.US);
                            Date netDate = (new Date(connectedTime * 1000));
                            String connectedDateTime = simpleDateFormat.format(netDate);

                            PreferenceUtility.getInstance(getBaseActivity()).putString(PreferenceUtility.BRACELET_Connected_DATE_TIME, connectedDateTime);

                            String macAddress = braceletListResponse.getMacAddress();
                            if (StringUtility.isEmptyOrNull(macAddress))
                                PreferenceUtility.getInstance(getBaseActivity()).putString(KEY_MAC_ADRESS, "");
                            else
                                PreferenceUtility.getInstance(getBaseActivity()).putString(KEY_MAC_ADRESS, macAddress);

                        }

                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        // showToast(errorMessage);
                        //    Logger.i("user status request onFailure:");
                    }
                });
    }

    /**
     * show dialog when the current version not available in backend
     * should update the app.
     */
    public void showForceUpdateDialog(String url) {
        new AlertDialog.Builder(this).setMessage(getString(R.string.App_Version_UsrPr_txt))
                .setPositiveButton(this.getString(R.string.Gen_Gen_lbl_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        finish();


                    }
                })
                .setCancelable(false)
                .show();
    }

    private void displayLogOutDialog() {
        final AlertBottomDialog alertLogoutDialog = new AlertBottomDialog(this);
        alertLogoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertLogoutDialog.setCancelable(false);
        alertLogoutDialog.create();
        alertLogoutDialog.setTextButton(this.getResources().getString(R.string.Gen_Gen_lbl_sure));
        alertLogoutDialog.setTvSubTitle(this.getResources().getString(R.string.HHD_HHDh_sur_quarantine_message));
        alertLogoutDialog.setTextButtonVisibility(true);
        alertLogoutDialog.show();

        alertLogoutDialog.setOnClickAlertListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                alertLogoutDialog.dismiss();
                String mCurrentDate = Util.getCurrentDate();
                int remainingDays = 14 - Integer.parseInt(Util.getDifferenceBtwDates(AppConfig.getInstance().getUser().getDateSignup(), mCurrentDate));

                if (remainingDays == 0) {
                    logoutUserCall();
                } else {
                    displayErrorRemainingDaysLogoutDialog();
                }
            }
        });

    }


    private void displayErrorRemainingDaysLogoutDialog() {

        final AlertBottomDialog alertFailedDialog = new AlertBottomDialog(this);
        alertFailedDialog.setCancelable(false);
        alertFailedDialog.create();
        alertFailedDialog.setTvSubTitle(this.getResources().getString(R.string.HHD_HHDh_continue_quarantine_message));
        alertFailedDialog.setIvAvatar(R.drawable.red_fail);
        alertFailedDialog.show();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                //put your code here
                alertFailedDialog.dismiss();
            }

        }, 5000);
    }

    protected void displayPoupAnotherDevice(String errorMessage) {
        final AlertBottomDialog alertAnotherDeviceDialog = new AlertBottomDialog(this);
        alertAnotherDeviceDialog.setCancelable(false);
        alertAnotherDeviceDialog.create();
        alertAnotherDeviceDialog.setTvSubTitle(errorMessage);
        alertAnotherDeviceDialog.setIvAvatar(R.drawable.red_fail);
        alertAnotherDeviceDialog.show();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                //put your code here
                alertAnotherDeviceDialog.dismiss();
            }

        }, 5000);
    }

}
