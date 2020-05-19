package com.machinestalk.stayhome.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.dialogs.ConfirmationDialog;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.listeners.OnChooseListener;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.service.BackgroundDetectedActivitiesService;
import com.machinestalk.stayhome.service.MyLocationService;
import com.machinestalk.stayhome.utils.Util;

import static com.machinestalk.stayhome.ConnectedCar.getApiUrl;
import static com.machinestalk.stayhome.constants.AppConstants.LANGUAGE_LITERAL_ARABIC;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_CHANGE_LANGUAGE;

/**
 * Created on 7/19/2016.
 */
public class AppConfig {

    public static final String DEFAULT_LANGUAGE = LANGUAGE_LITERAL_ARABIC;

    private User mUser;
    private final String KEY_USER = "user";
    private final String KEY_LANGUAGE = "language";
    private final String KEY_NOTIFICATION_COUNT = "notificationCount";
    private static final String KEY_URL = "url";
    private static final String KEY_SIGNAL_URL = "signalRUrl";
    private static final String KEY_IDENTITY_URL = "identityUrl";
    private final static boolean IS_NOT_SL = true;
    private static final String DEFAULT_PAGE = "defaultPage";

    public static final long INTERVAL_ONE_MINUTES = 60 * 1000;
    /**********Alarm manager *************/
    static AlarmManager alarmManagerScheduler;
    static PendingIntent scheduledIntent;
    public static AlertBottomDialog wifiDialog ;
    public static AlertBottomDialog locationDialog ;
    public static AlertBottomDialog batteryDialog ;
    public static AlertBottomDialog braceletDialog ;
    public static ConfirmationDialog zoneOutDialog ;
    public static AlertBottomDialog bluetoothDialog;


    private static AppConfig instance;

    public static AppConfig getInstance() {

        if (instance == null)
            instance = new AppConfig();

        return instance;
    }

    public void setUser(User user) {
        mUser = user;
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putString(KEY_USER, user.getJson());
    }

    /**
     *
     * @param token
     * @param refreshToken
     */
    public void setUserUpdateAfterRefreshToken(String token, String refreshToken) {
        if (mUser != null){
            mUser.setAccessToken(token);
            mUser.setRefreshToken(refreshToken);

            PreferenceUtility.getInstance(ConnectedCar.getInstance()).putString(KEY_USER, mUser.getJson());
        }else {
            String jsonString = PreferenceUtility.getInstance(ConnectedCar.getInstance()).getString(KEY_USER, "");
            if (!StringUtility.isEmptyOrNull(jsonString)) {
                mUser.loadLocalJson(JsonUtility.parseToJsonObject(jsonString));
                mUser.setAccessToken(token);
                mUser.setRefreshToken(refreshToken);

                PreferenceUtility.getInstance(ConnectedCar.getInstance()).putString(KEY_USER, mUser.getJson());
            }
        }
    }

    /**
     *
     * @return
     */
    public User getUser() {
        if (mUser == null) {
            mUser = new User();
            try {
                String jsonString = PreferenceUtility.getInstance(ConnectedCar.getInstance()).getString(KEY_USER, "");
                if (!StringUtility.isEmptyOrNull(jsonString)) {
                    mUser.loadLocalJson(JsonUtility.parseToJsonObject(jsonString));
                }
            } catch (Exception e) {

            }
        }
        return mUser;
    }


    public String getAccessToken() {
        if (getUser().getAccessToken() == null)
            return null;

        return "Bearer " + mUser.getAccessToken();
    }


    public void clearPreferenceData() {
        mUser = null;
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(PreferenceUtility.IS_LOGGED, PreferenceUtility.DEFAULT_IS_LOGGED);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putString(KEY_USER, "");
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(KeyConstants.KEY_NUM_DAY, 0);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KeyConstants.KEY_IS_DEVICE_EXPIRED, false);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.WIFI_STATUS, -1);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(PreferenceUtility.INTERNET_STATUS, false);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.MOVEMENT_ACCLRMTR_STATUS, 0);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.USER_ACTIVITY, -1);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(PreferenceUtility.BLUETOOTH_STATUS, false);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.BLUETOOTH_STATUS_CHANGED, -1);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(PreferenceUtility.NETWORK_CONNECTION_MODE_CHANGED, false);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(PreferenceUtility.NETWORK_CONNECTION_MODE, 1);
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putLong(PreferenceUtility.EVENT_LAST_DAY, 0);
        clearNotificationCount();
    }

    public String getLanguage() {
        String appLanguage = PreferenceUtility.getInstance(ConnectedCar.getInstance()).getString(KEY_LANGUAGE, "");
        appLanguage = Util.checkDeviceLanguage(appLanguage);
        setLanguage(appLanguage);
        return appLanguage;
    }

    public void setLanguage(String language) {

        if (TextUtils.isEmpty(language))
            return;

        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putString(KEY_LANGUAGE, language);
    }

    public void setChangeLanguageStatus(boolean language) {

        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putBoolean(KEY_CHANGE_LANGUAGE, language);
    }

    public boolean getChangeLanguageStatus() {

        return PreferenceUtility.getInstance(ConnectedCar.getInstance()).getBoolean(KEY_CHANGE_LANGUAGE, false);

    }


    public boolean isArabic() {

        return getLanguage().equals(DEFAULT_LANGUAGE);
    }

    public void clearNotificationCount() {

        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(KEY_NOTIFICATION_COUNT, 0);
    }

    public void setNotificationCount() {

        int previousCount = PreferenceUtility.getInstance(ConnectedCar.getInstance()).getInteger(KEY_NOTIFICATION_COUNT, 0);
        previousCount++;
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putInteger(KEY_NOTIFICATION_COUNT, previousCount);
    }

    public int getNotificationCount() {

        return PreferenceUtility.getInstance(ConnectedCar.getInstance()).getInteger(KEY_NOTIFICATION_COUNT, 0);
    }


    public void setUrl(String url) {
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putString(KEY_URL, url);
    }

    /**
     * @return
     */
    public String getUrl() {

        return PreferenceUtility.getInstance(ConnectedCar.getInstance()).getString(KEY_URL, getApiUrl());
    }


    /**
     * get base url for identity APIs.
     *
     * @return
     */
    public static String getIdentityUrl() {

        return PreferenceUtility.getInstance(ConnectedCar.getInstance()).getString(KEY_IDENTITY_URL, ConnectedCar.getIdentityUrl());
    }

    /**
     * set base url for identity API
     *
     * @param identityUrl
     */
    public void setIdentityUrl(String identityUrl) {
        PreferenceUtility.getInstance(ConnectedCar.getInstance()).putString(KEY_IDENTITY_URL, identityUrl);
    }


    public boolean isUserLoggedIn() {
        return PreferenceUtility.getInstance(ConnectedCar.getInstance()).getBoolean(PreferenceUtility.IS_LOGGED, PreferenceUtility.DEFAULT_IS_LOGGED);
    }

    public static boolean isNotSl() {

        return IS_NOT_SL;
    }


    public static boolean canDrive() {

//		return (!TextUtils.isEmpty (getInstance ().getUser ().getDriverId ()));
        return true;
    }

    /**
     * get base image url from data base.
     *
     * @return
     */
    public String getBaseImageUrl() {
        String basImageUrl = "";
        return basImageUrl;
    }

    public static AlarmManager getAlarmManagerScheduler() {
        return alarmManagerScheduler;
    }

    public static PendingIntent getScheduledIntent() {
        return scheduledIntent;
    }

    public void cancelAlarm()
    {
//        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
//        Intent cancelServiceIntent = new Intent(ctx, MyLocationService.class);
//        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(ctx,
//                MyLocationService.SERVICE_ID, // integer constant used to identify the service
//                cancelServiceIntent,
//                0 //no FLAG needed for a service cancel
//        );
//        am.cancel(cancelServicePendingIntent);
        if (alarmManagerScheduler != null && scheduledIntent != null)
        alarmManagerScheduler.cancel(scheduledIntent);
    }


    @SuppressLint("NewApi")
    public void StartBackgroundTasks(Context context) {
        startSecondServiceTracking(context);
        cancelAlarm();
        alarmManagerScheduler = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyLocationService.class);
        scheduledIntent = PendingIntent.getService(context, MyLocationService.SERVICE_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManagerScheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL_ONE_MINUTES, scheduledIntent);
//        alarmManagerScheduler.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//                INTERVAL_ONE_MINUTES, scheduledIntent);

    }

    public void startSecondServiceTracking(Context context) {
        stopSecondServiceTracking(context);
        Intent intent = new Intent(context, BackgroundDetectedActivitiesService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
    private void stopSecondServiceTracking(Context context) {
        Intent intent = new Intent(context, BackgroundDetectedActivitiesService.class);
        context.stopService(intent);
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void showBatteryLowDialog(Activity context) {
        if (batteryDialog == null )
            batteryDialog = new AlertBottomDialog(context);
        if (!context.isFinishing() && batteryDialog != null && !batteryDialog.isShowing()) {
            batteryDialog.show();
        }
    }

    public void showGpsDisabledDialog(Activity activity) {
        if (locationDialog == null )
            locationDialog = new AlertBottomDialog(activity.getApplicationContext());
        if (!activity.isFinishing() && locationDialog != null && !locationDialog.isShowing()) {
            locationDialog.show();
        }
    }

    public void showWifiDisabledDialog(Activity activity) {
        if (wifiDialog == null )
            wifiDialog = new AlertBottomDialog(activity.getApplicationContext());
        if (!activity.isFinishing() && wifiDialog != null && !wifiDialog.isShowing()) {
            wifiDialog.show();
        }
    }


    public void showZoneOutDialog(Activity activity) {
                //Write whatever to want to do after delay specified (0.5 sec)
                if (zoneOutDialog == null )
                    zoneOutDialog = new ConfirmationDialog(activity.getApplicationContext());
                if (!activity.isFinishing() && zoneOutDialog != null && !zoneOutDialog.isShowing()) {
                    zoneOutDialog.show();
                }
    }

    public void showBluetoothDisabledDialog(Activity activity) {
        if (bluetoothDialog == null )
            bluetoothDialog = new AlertBottomDialog(activity.getApplicationContext());
        if (!activity.isFinishing() && bluetoothDialog != null && !bluetoothDialog.isShowing()) {
            bluetoothDialog.show();
        }
    }

    /**
     *
     * @param activity
     */
    public void showBraceletNotDetectedDialog(Activity activity){
        if (braceletDialog == null )
            braceletDialog = new AlertBottomDialog(activity.getApplicationContext());
        if (!activity.isFinishing() && braceletDialog != null && !braceletDialog.isShowing()) {
            braceletDialog.show();
        }
    }

    /**
     *
     * @param activity
     */
    private void createBraceletDialog(Activity activity){
        braceletDialog = new AlertBottomDialog(activity);
        braceletDialog.setOnClickAlertListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                braceletDialog.dismiss();
            }
        });

        braceletDialog.setCancelable(false);
        braceletDialog.create();
        braceletDialog.setIvAvatar(R.drawable.bracelet_red_pop);
        braceletDialog.setTextButton(activity.getResources().getString(R.string.alert_ok));
        braceletDialog.setTvTitle(activity.getResources().getString(R.string.attention));
        braceletDialog.setTvSubTitle(activity.getResources().getString(R.string.alert_bracelet_msg));
    }

    private void createZoneOutDialog(Activity activity){
        zoneOutDialog = new ConfirmationDialog(activity);
        zoneOutDialog.setCancelable(false);
        zoneOutDialog.create();
        zoneOutDialog.setDialogImageView(R.drawable.ic_speaker);
        zoneOutDialog.setTvTitle(activity.getResources().getString(R.string.attention));
        zoneOutDialog.setTvSubTitle(activity.getResources().getString(R.string.alert_location_msg));
        zoneOutDialog.setTextBtnNoVisibility(false);
        zoneOutDialog.setTextBtnYesVisibility(false);
        zoneOutDialog.setCanceledOnTouchOutside(true);
        zoneOutDialog.setTextBtnNo(activity.getResources().getString(R.string.emergency));
        zoneOutDialog.setTextBtnYes(activity.getResources().getString(R.string.alert_ok));
        zoneOutDialog.setListener(new OnChooseListener() {
            @Override
            public void onAccept() {
                zoneOutDialog.dismiss();
            }

            @Override
            public void onRefuse() {
                Intent sendIntent = new Intent(Intent.ACTION_DIAL);
                sendIntent.setData(Uri.parse("tel:" + "937"));
                activity.startActivity(sendIntent);
            }
        });
    }

    private void createWifiDialog(Activity activity){
        wifiDialog = new AlertBottomDialog(activity);
        wifiDialog.setOnClickAlertListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                wifiDialog.dismiss();
            }
        });

        wifiDialog.setCancelable(false);
        wifiDialog.create();
        wifiDialog.setIvAvatar(R.drawable.red_wifi);
        wifiDialog.setTextButton(activity.getResources().getString(R.string.alert_ok));
        wifiDialog.setTvTitle(activity.getResources().getString(R.string.attention));
        wifiDialog.setTvSubTitle(activity.getResources().getString(R.string.alert_wifi_msg));
    }

    private void createGpsDialog(Activity activity){
        locationDialog = new AlertBottomDialog(activity);
        locationDialog.setOnClickAlertListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                locationDialog.dismiss();
            }
        });
        locationDialog.setCancelable(false);
        locationDialog.create();
        locationDialog.setIvAvatar(R.drawable.red_gps);
        locationDialog.setTextButton(activity.getResources().getString(R.string.alert_ok));
        locationDialog.setTvTitle(activity.getResources().getString(R.string.attention));
        locationDialog.setTvSubTitle(activity.getResources().getString(R.string.alert_gps_msg));
    }

    private void createBatteryDialog(Activity activity){
        batteryDialog = new AlertBottomDialog(activity);
        batteryDialog.setOnClickAlertListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                batteryDialog.dismiss();
            }
        });
        batteryDialog.setCancelable(false);
        batteryDialog.create();
        batteryDialog.setIvAvatar(R.drawable.red_battery);
        batteryDialog.setTextButton(activity.getResources().getString(R.string.alert_ok));
        batteryDialog.setTvTitle(activity.getString(R.string.attention));
        batteryDialog.setTvSubTitle(activity.getString(R.string.alert_battery_msg));
    }

    private void createBluetoothDialog(Activity activity){
        bluetoothDialog = new AlertBottomDialog(activity);
        bluetoothDialog.setOnClickAlertListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                bluetoothDialog.dismiss();
            }
        });
        bluetoothDialog.setCancelable(false);
        bluetoothDialog.create();
        bluetoothDialog.setIvAvatar(R.drawable.red_bluetooth);
        bluetoothDialog.setTextButton(activity.getResources().getString(R.string.alert_ok));
        bluetoothDialog.setTvTitle(activity.getResources().getString(R.string.attention));
        bluetoothDialog.setTvSubTitle(activity.getResources().getString(R.string.alert_blue_msg));
    }

    public void createAllAlertDialogs(Activity activity){
        createBatteryDialog(activity);
        createBluetoothDialog(activity);
        createBraceletDialog(activity);
        createGpsDialog(activity);
        createWifiDialog(activity);
        createZoneOutDialog(activity);
    }
}
