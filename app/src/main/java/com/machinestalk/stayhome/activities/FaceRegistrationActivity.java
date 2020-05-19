package com.machinestalk.stayhome.activities;

import android.Manifest;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.utils.GpsUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.callback.PasswordCallback;
import me.aflak.libraries.dialog.FingerprintDialog;
import me.aflak.libraries.utils.FingerprintToken;
import me.aflak.libraries.view.Fingerprint;

public class FaceRegistrationActivity extends AppCompatActivity implements OnClickAlertListener, FingerprintSecureCallback, PasswordCallback, FingerprintDialogCallback {

    public static final int REQUEST_ENABLE_BT = 1250;
    private final static int REQUEST_ENABLE_GPS = 3000;
    private Toolbar toolbar;
    private AlertBottomDialog alertVerifyFaceDialog;
    private AlertBottomDialog alertVeryFingerNumberDialog;
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private int state = 0;
    private boolean comeFromSettings = false;
    private boolean sucessAuthWithFinger = false;
    private AlertBottomDialog alertSuccessFaceDialog;
    private Button btAgreed;
    private LinearLayout layoutPermission;
    private RelativeLayout layoutSucess;
    private RelativeLayout layoutRegisterFace;
    private View layoutWelcoming;
    private BluetoothAdapter mBluetoothAdapter;
    FingerprintDialog fingerprintDialog;
    Fingerprint fingerprint;
    private CountDownTimer downLocationSuccessfullyTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_auth);
        initViews();
        setupToolbar();
        displayDialogVerifyIdentity();
        setListener();
    }


    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        btAgreed = findViewById(R.id.bt_agreed);
        layoutPermission = findViewById(R.id.layout_permission);
        layoutRegisterFace = findViewById(R.id.layout_register_face);
        layoutSucess = findViewById(R.id.layout_success);
    }

    private void setListener() {

        btAgreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySuccessView();
            }
        });


    }



    private void displayDialogVerifyIdentity() {
        alertVerifyFaceDialog = new AlertBottomDialog(this);
        alertVerifyFaceDialog.setListener(this);
        alertVerifyFaceDialog.setCancelable(false);
        alertVerifyFaceDialog.create();
        alertVerifyFaceDialog.setTextButton(this.getResources().getString(R.string.Gen_Gen_lbl_next));
        alertVerifyFaceDialog.setTvTitle(this.getResources().getString(R.string.verify_identity));
        alertVerifyFaceDialog.setTvSubTitle(this.getResources().getString(R.string.note_face_id));
        alertVerifyFaceDialog.setTextButtonVisibility(true);
        alertVerifyFaceDialog.show();
    }
    private void displayDialogOneFingerError() {


        alertVeryFingerNumberDialog = new AlertBottomDialog(this);
        alertVeryFingerNumberDialog.setListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                comeFromSettings = true ;
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
                alertVeryFingerNumberDialog.dismiss();
            }
        });
        alertVeryFingerNumberDialog.setCancelable(false);
        alertVeryFingerNumberDialog.create();
        alertVeryFingerNumberDialog.setIvAvatar(R.drawable.red_fail);
        alertVeryFingerNumberDialog.setTextButton(this.getResources().getString(R.string.alert_ok));
        alertVeryFingerNumberDialog.setTvTitle(this.getResources().getString(R.string.attention));
        alertVeryFingerNumberDialog.setTvSubTitle("Please Try to keep one finger print");
        alertVeryFingerNumberDialog.show();
    }

    public void checkBiometricStatus() {
        KeyguardManager keyguardManager =
                (KeyguardManager) this.getSystemService(KEYGUARD_SERVICE);
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(this);

        try {
            if (fingerprintManagerCompat.isHardwareDetected()
                    && fingerprintManagerCompat.hasEnrolledFingerprints() && getFingerprintInfo(this) == 1) {
                startAuthBiometricSDK();
                comeFromSettings = false ;
            } else if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.USE_BIOMETRIC) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();
            }else if (getFingerprintInfo(getApplicationContext()) > 1){
                displayDialogOneFingerError();
            } else {
                comeFromSettings = true ;
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.i(getClass().getName(), e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppConfig.getInstance().getAlarmManagerScheduler() != null
                && !PreferenceUtility.getInstance(getApplicationContext()).getBoolean(KeyConstants.KEY_SIGNUP_DONE, false)){
            AppConfig.getInstance().getAlarmManagerScheduler().cancel(AppConfig.getInstance().getScheduledIntent());
        }

        if ((state != 0 || comeFromSettings || (fingerprintDialog != null && state == 0))
                && !PreferenceUtility.getInstance(getApplicationContext()).getBoolean(KeyConstants.KEY_FINGER_NOTIF_SHOWN, false)){
            checkBiometricStatus();
            PreferenceUtility.getInstance(getApplicationContext()).putBoolean(KeyConstants.KEY_FINGER_NOTIF_SHOWN, true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public int getFingerprintInfo(Context context) {

        int numberOfFingerPrints = 0;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P ){
            try {
                FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
                Method method = FingerprintManager.class.getDeclaredMethod("getEnrolledFingerprints");
                Object obj = method.invoke(fingerprintManager);

                if (obj != null) {
                    Class<?> clazz = Class.forName("android.hardware.fingerprint.Fingerprint");
                    Method getFingerId = clazz.getDeclaredMethod("getFingerId");
                    numberOfFingerPrints = ((List) obj).size();
                    for (int i = 0; i < ((List) obj).size(); i++) {
                        Object item = ((List) obj).get(i);
                        if (item != null) {
                            System.out.println("fkie4. fingerId: " + getFingerId.invoke(item));
                            Log.i("fingerid is :"+ getFingerId.invoke(item),"");
                            if (sucessAuthWithFinger)
                                PreferenceUtility.getInstance(getApplicationContext()).putInteger(KeyConstants.KEY_ID_FINGERPRINT, (Integer) getFingerId.invoke(item));

                        }
                    }
                }
                return numberOfFingerPrints;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return numberOfFingerPrints;

        }else {
            return 1 ;
        }

    }


    private void requestPermission() {
        scanBluetooth();
        enableWifi();
    }

    private void enableWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }


    private void scanBluetooth() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return;
            } else {
                mBluetoothAdapter.startDiscovery();
                checkGPSPermissions();
            }


        } catch (Exception e) {
            Log.i(getClass().getName(), e.getMessage().toString());
        }
    }

    private void checkGPSPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(FaceRegistrationActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ENABLE_GPS);
            }
        } else {
            turnGPSOn();

        }
    }


    private void turnGPSOn() {
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                if (isGPSEnable)
                    displayWelcomingView();
            }
        });
    }


    private void displayWelcomingView() {
        layoutPermission.setVisibility(View.GONE);
        layoutRegisterFace.setVisibility(View.GONE);
        layoutSucess.setVisibility(View.GONE);
        layoutWelcoming.setVisibility(View.VISIBLE);
    }

    private void displaySuccessView() {
        layoutPermission.setVisibility(View.GONE);
        layoutRegisterFace.setVisibility(View.GONE);
        layoutSucess.setVisibility(View.VISIBLE);
        startTimer();
        BluetoothAdapter.getDefaultAdapter().setName(AppConfig.getInstance().getUser().getDeviceId().substring(28));
        AppConfig.getInstance().StartBackgroundTasks(getBaseContext());
    }

    private void startTimer() {
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                navigateToMainActivity();
            }
        }.start();

    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
    }




    private void setupToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
//        toolbar.setTitle(this.getResources().getString(R.string.sign_up));
        getSupportActionBar().setTitle(this.getResources().getString(R.string.sign_up));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onAlertClick() {
        if (state == 0) {
            alertVerifyFaceDialog.dismiss();
            checkBiometricStatus();
        } else {
//            alertSuccessFaceDialog.dismiss();
//            PreferenceUtility.getInstance(this).putBoolean(KeyConstants.KEY_FACE_RECOGNIZER, true);
//            displayPermissionView();
        }
    }

    private void displayPermissionView() {
        layoutRegisterFace.setVisibility(View.GONE);
        layoutSucess.setVisibility(View.GONE);
        layoutPermission.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            mBluetoothAdapter.startDiscovery();
            if (mBluetoothAdapter.isEnabled()) checkGPSPermissions();
        }
        if (requestCode == REQUEST_ENABLE_GPS) {
            if (isGPSEnabled(this)) displayPermissionView();
        }
        if (requestCode == GpsUtils.GPS_REQUEST) {
            if (isGPSEnabled(this)) displayPermissionView();
        }

        int PICK_IMAGE_CAMERA = 1;
        if (requestCode == PICK_IMAGE_CAMERA) {
            if (data != null) {
                try {
                    displayDialogVerifyIdentity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
        }

    }


    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                int PICK_IMAGE_CAMERA = 1;
                startActivityForResult(intent, PICK_IMAGE_CAMERA);
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void startAuthBiometricSDK() {

        if (FingerprintDialog.isAvailable(getApplicationContext())) {
            fingerprintDialog = FingerprintDialog.initialize(this)
                    .title("")
                    .message("Check my finger print")
                    .cancelOnPressBack(false)
                    .callback(this);

            fingerprintDialog.show();
        }

        fingerprint = findViewById(R.id.fingerprint);
        if (fingerprint != null) {
            try {
                fingerprint.callback(this, "KeyName2")
                        .circleScanningColor(android.R.color.black)
                        .fingerprintScanningColor(R.color.colorAccent)
                        .authenticate();

            } catch (Exception e) {
                Log.i(getClass().getName(), e.getMessage());
            }
        }
    }


    @Override
    public void onAuthenticationSucceeded() {
        Log.i(getClass().getName(), "onAuthenticationSucceeded");
        fingerprintDialog.dismiss();
        //                //afficher screen your face id successuful
        alertSuccessFaceDialog = new AlertBottomDialog(this);
        alertSuccessFaceDialog.setListener(this);
        alertSuccessFaceDialog.setCancelable(false);
        alertSuccessFaceDialog.create();
        alertSuccessFaceDialog.setIvAvatar(R.drawable.big_green_check);
        alertSuccessFaceDialog.setTextButtonVisibility(false);
//        alertSuccessFaceDialog.setTextButton(this.getResources().getString(R.string.Gen_Gen_lbl_next));
        alertSuccessFaceDialog.setTvSubTitle(this.getResources().getString(R.string.register_successfully));
        alertSuccessFaceDialog.show();
        state = 1;
        User user = AppConfig.getInstance().getUser();
        user.setANewDevice(false);
        PreferenceUtility.getInstance(getBaseContext()).putBoolean(KeyConstants.KEY_SIGNUP_DONE, true);
        sucessAuthWithFinger = true ;
        getFingerprintInfo(getApplicationContext());
        AppConfig.getInstance().setUser(user);
        startLocationSuccefullyTimer();
    }

    private void startLocationSuccefullyTimer() {

        downLocationSuccessfullyTimer = new CountDownTimer(3000, 2000) {


            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                alertSuccessFaceDialog.dismiss();
                PreferenceUtility.getInstance(getApplicationContext()).putBoolean(KeyConstants.KEY_FACE_RECOGNIZER, true);
                displayPermissionView();
            }
        }.start();
    }


    @Override
    public void onAuthenticationCancel() {
        Log.i(getClass().getName(), "onAuthenticationCancel");
        checkBiometricStatus();
    }

    @Override
    public void onAuthenticationFailed() {
        Log.i(getClass().getName(), "onAuthenticationFailed");
        fingerprintDialog.dismiss();
    }

    @Override
    public void onNewFingerprintEnrolled(FingerprintToken token) {
    }

    @Override
    public void onAuthenticationError(int errorCode, String error) {
        Log.i(getClass().getName(), "onAuthenticationError");
    }

    @Override
    public void onPasswordSucceeded() {
        Log.i(getClass().getName(), "onPasswordSucceeded");
    }

    @Override
    public boolean onPasswordCheck(String password) {
        return password.equals("password");

    }

    @Override
    public void onPasswordCancel() {
        Log.i(getClass().getName(), "onPasswordCancel");
    }
}
