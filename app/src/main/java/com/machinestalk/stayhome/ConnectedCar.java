package com.machinestalk.stayhome;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.entities.NotificationDataEntity;
import com.machinestalk.stayhome.fragments.BaseFragment;
import com.machinestalk.stayhome.service.MyLocationService;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.utils.AppLifecycleObserver;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

import static com.machinestalk.stayhome.constants.AppConstants.MOTORNA_URL_DEV;
import static com.machinestalk.stayhome.constants.AppConstants.MOTORNA_URL_IDENTITY_DEV;
import static com.machinestalk.stayhome.constants.AppConstants.MOTORNA_URL_IDENTITY_PROD;
import static com.machinestalk.stayhome.constants.AppConstants.MOTORNA_URL_PROD;

/**
 * Created on 12/19/2016.
 */

public class ConnectedCar extends Application {

    private static ConnectedCar instance;
    private final HashMap<String, Boolean> fabStates = new HashMap<>(3);
    boolean isSessionExpired = false;
    private ServiceFactory serviceFactory;
    private static FirebaseAnalytics firebaseAnalyticsInstance;
    private BaseFragment lastVisibleFragment;
    protected static ArrayList<NotificationDataEntity> notificationsList = new ArrayList<>();
    private static boolean isAppWentToBg = false;
    private static boolean isWindowFocused = false;
    private static boolean isBackPressed = false;

    private int currentLocationPosition = -1;
    private AppDatabase mDatabase;
    private DeleteAllTablesTask mDeleteAllTablesTask;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private static AppLifecycleObserver appLifecycleObserver;
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private MyLocationService monitoringActivity = null;

    public static ConnectedCar getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        instance = this;
        firebaseAnalyticsInstance = FirebaseAnalytics.getInstance(this);
        initServiceFactory(false);
        initLogger();
        initLogger2();
        startWakeLock();

        if (AppConfig.getInstance().getUser().getAccessToken() == null)
            clearLog();
//        initBeaconManager();
        appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
        // initialize
        // clear the previous logcat and then write the new one to the file
    }

//    private void initBeaconManager(){
//        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
//
//        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
//        // find a different type of beacon, you must specify the byte layout for that beacon's
//        // advertisement with a line like below.  The example shows how to find a beacon with the
//        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
//        // layout expression for other beacon types, do a web search for "setBeaconLayout"
//        // including the quotes.
//        //
//        beaconManager.getBeaconParsers().clear();
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
//
//        beaconManager.setDebug(true);
//
//        // Uncomment the code below to use a foreground service to scan for beacons. This unlocks
//        // the ability to continually scan for long periods of time in the background on Andorid 8+
//        // in exchange for showing an icon at the top of the screen and a always-on notification to
//        // communicate to users that your app is using resources in the background.
//        //
////        Notification.Builder builder = new Notification.Builder(this);
////        builder.setSmallIcon(R.drawable.ic_stay_home);
////        builder.setContentTitle("Scanning for Beacons");
////        Intent intent = new Intent(this, DashboardActivity.class);
////        PendingIntent pendingIntent = PendingIntent.getActivity(
////                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
////        );
////        builder.setContentIntent(pendingIntent);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
////                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
////            channel.setDescription("My Notification Channel Description");
////            NotificationManager notificationManager = (NotificationManager) getSystemService(
////                    Context.NOTIFICATION_SERVICE);
////            notificationManager.createNotificationChannel(channel);
////            builder.setChannelId(channel.getId());
////        }
////        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
//
//        // For the above foreground scanning service to be useful, you need to disable
//        // JobScheduler-based scans (used on Android 8+) and set a fast background scan
//        // cycle that would otherwise be disallowed by the operating system.
//        //
////        beaconManager.setEnableScheduledScanJobs(false);
////        beaconManager.setBackgroundBetweenScanPeriod(0);
////        beaconManager.setBackgroundScanPeriod(1100);
////        Log.d(getClass().getName(), "setting up background monitoring for beacons and power saving");
////        // wake up the app when a beacon is seen
////        Region region = new Region("backgroundRegion",
////                null, null, null);
////        regionBootstrap = new RegionBootstrap(this, region);
////
////        // simply constructing this class and holding a reference to it in your custom Application
////        // class will automatically cause the BeaconLibrary to save battery whenever the application
////        // is not visible.  This reduces bluetooth power usage by about 60%
////        backgroundPowerSaver = new BackgroundPowerSaver(this);
//
//    }

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

    public void disableMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }

    public void enableMonitoring() {
//        Region region = new Region("backgroundRegion",
//                null, null, null);
//        regionBootstrap = new RegionBootstrap(this, region);
    }


    @SuppressLint("InvalidWakeLockTag")
    public void startWakeLock() {
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DashboardTag");
        mWakeLock.acquire();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (getResources().getConfiguration().locale.toString().contains("ar")) {
//            AppConfig.getInstance().setLanguage(AppConstants.LANGUAGE_LITERAL_ARABIC);
//        } else {
//            AppConfig.getInstance().setLanguage(AppConstants.LANGUAGE_LITERAL_ENGLISH);
//        }

        AppConfig.getInstance().setChangeLanguageStatus(true);

    }

    public static Boolean isAppInForeground() {
        return appLifecycleObserver.isAppInForeground();
    }

    @Override
    public void onTerminate() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        super.onTerminate();
    }


    public static FirebaseAnalytics getFirebaseAnalyticsInstance() {
        return firebaseAnalyticsInstance;
    }


    /**
     * @return
     */
    public static String getApiUrl() {
        String url_signalR;
        if (BuildConfig.FLAVOR.equals("prodProduction")) {
            url_signalR = MOTORNA_URL_PROD;
        } else {
            url_signalR = MOTORNA_URL_DEV;
        }
        return url_signalR;
    }


    /**
     * base url for login, logout and refresh token APIs.
     * MOTORNA_URL_PROD : if prod version.
     * MOTORNA_URL_IDENTITY_DEV : if dev version.
     *
     * @return
     */
    public static String getIdentityUrl() {
        String url_signalR;
        if (BuildConfig.FLAVOR.equals("prodProduction")) {
            url_signalR = MOTORNA_URL_IDENTITY_PROD;
        } else {
            url_signalR = MOTORNA_URL_IDENTITY_DEV;
        }
        return url_signalR;
    }

    public void initLogger() {
        String diskPath = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            diskPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Logger";
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "Logger");

        } else {
            diskPath = getExternalFilesDir(null)
                    + File.separator + "Logger";
            File myDirectory = new File(getExternalFilesDir(null), "Logger");
        }
        File myDirectory = new File(getExternalFilesDir(null), "Logger");

        Logger.addLogAdapter(new AndroidLogAdapter());
        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .tag("")
                .dateFormat(new SimpleDateFormat("MM.dd HH:mm", Locale.UK))
                .build();

        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
    }

    public void initLogger2() {
        String folder = "", diskPath = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            diskPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Logger";
        } else {
            diskPath = getExternalFilesDir(null)
                    + File.separator + "Logger";
        }
        folder = diskPath;

//        FL.init(new FLConfig.Builder(this)
//                .minLevel(FLConst.Level.V)
//                .logToFile(true)
//                .dir(new File(folder, "Stayhome_Bluetooth_Log"))
//                .retentionPolicy(FLConst.RetentionPolicy.FILE_COUNT)
//                .maxFileCount(FLConst.DEFAULT_MAX_FILE_COUNT)    // customise how many log files to keep if retention by file count
//                .maxTotalSize(FLConst.DEFAULT_MAX_TOTAL_SIZE)    // customise how much space log files can occupy if retention by total size
//
//                .build());
//        FL.setEnabled(true);


    }


    public void initServiceFactory(boolean forceInit) {
        serviceFactory = new ServiceFactory();
        serviceFactory.initialize(getApplicationContext(), forceInit);
    }


    public String getStringLang(int id) {
        Context context = getLanguageContext();
        Resources resources = context.getResources();
        return resources.getString(id);
    }

    public Context getLanguageContext() {
        Configuration overrideConfiguration = getBaseContext().getResources().getConfiguration();
        overrideConfiguration.setLocale(new Locale(AppConfig.getInstance().getLanguage()));
        return createConfigurationContext(overrideConfiguration);
    }

    public boolean isSessionExpired() {
        return isSessionExpired;
    }

    public void setSessionExpired(boolean sessionExpired) {
        isSessionExpired = sessionExpired;
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void updateLocale(String language) {
        Locale locale = new Locale(language.substring(0, 2));
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        Context context = createConfigurationContext(configuration);
    }

    public void setLastVisibleFragment(BaseFragment lastVisibleFragment) {
        this.lastVisibleFragment = lastVisibleFragment;
    }

    public BaseFragment getLastVisibleFragment() {
        return lastVisibleFragment;
    }

    public void addToState(String name, boolean state) {
        fabStates.put(name, state);
    }

    public boolean getFabStates(String name) {
        if (fabStates.containsKey(name)) {
            return fabStates.get(name);
        }

        return false;
    }

    public HashMap<String, Boolean> getFabStates() {
        return fabStates;
    }

    public void showCurrentLocationOption(int adapterPosition) {
        currentLocationPosition = adapterPosition;
    }

    public void deleteDataBase() {
        deleteAllTable();
    }

    public int getCurrentLocationPosition() {
        return currentLocationPosition;
    }

    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    /**
     * delete all tables from data base.
     */
    private void deleteAllTable() {
        if (mDeleteAllTablesTask != null && mDeleteAllTablesTask.isCancelled() == false) {
            mDeleteAllTablesTask.cancel(true);
        } else {
            mDeleteAllTablesTask = new DeleteAllTablesTask();
            mDeleteAllTablesTask.execute();
        }
    }


    public DeleteAllTablesTask getmDeleteAllTablesTask() {
        return mDeleteAllTablesTask;
    }

//    @Override
//    public void didEnterRegion(Region arg0) {
//        // In this example, this class sends a notification to the user whenever a Beacon
//        // matching a Region (defined above) are first seen.
//        Log.d(getClass().getName(), "did enter region.");
//        if (!haveDetectedBeaconsSinceBoot) {
//            Log.d(getClass().getName(), "auto launching MainActivity");
//            // The very first time since boot that we detect an beacon, we launch the
//            // MainActivity
////            Intent intent = new Intent(this, DashboardActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
////            // to keep multiple copies of this activity from getting created if the user has
////            // already manually launched the app.
////            this.startActivity(intent);
////            haveDetectedBeaconsSinceBoot = true;
//        } else {
////            if (monitoringActivity != null) {
////                // If the Monitoring Activity is visible, we log info about the beacons we have
////                // seen on its display
////                Log.i(getClass().getName(),"I see a beacon again" );
////            } else {
////                // If we have already seen beacons before, but the monitoring activity is not in
////                // the foreground, we send a notification to the user on subsequent detections.
////                Log.d(getClass().getName(), "Sending notification.");
//////                sendNotification();
////            }
//        }
//
//    }

//    @Override
//    public void didExitRegion(Region region) {
//        Log.i(getClass().getName(),"I no longer see a beacon.");
//    }

//    @Override
//    public void didDetermineStateForRegion(int state, Region region) {
//        Log.i(getClass().getName(),"Current region state is: " + (state == 1 ? "INSIDE" : "OUTSIDE ("+state+")"));
//    }

    /**
     * delete users asyncTask
     */
    private class DeleteAllTablesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            AppDatabase.getInstance(ConnectedCar.this).deleteAllTable();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDeleteAllTablesTask = null;
        }
    }

    public static ArrayList<NotificationDataEntity> getNotificationsList() {
        return notificationsList;
    }

    public static void setNotificationsList(ArrayList<NotificationDataEntity> notificationsList) {
        ConnectedCar.notificationsList = notificationsList;
    }

    public void setMonitoringActivity(MyLocationService activity) {
        this.monitoringActivity = activity;
    }


}
