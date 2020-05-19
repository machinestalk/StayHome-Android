package com.machinestalk.stayhome.activities;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.components.Button;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.service.ServiceApi;
import com.machinestalk.stayhome.service.body.CompliantBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Objects;

import javax.crypto.KeyGenerator;

import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.callback.PasswordCallback;
import me.aflak.libraries.dialog.FingerprintDialog;
import me.aflak.libraries.utils.FingerprintToken;
import me.aflak.libraries.view.Fingerprint;

import static com.machinestalk.stayhome.constants.KeyConstants.KEY_NAME;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_WRONG_FINGERPRINT;

public class CheckInActivity extends AppCompatActivity implements FingerprintSecureCallback, PasswordCallback, FingerprintDialogCallback {

    private Toolbar toolbar;
    private Button btAmHere;
    private Button btTryAgain;
    private TextView txtTimer;
    private RelativeLayout layoutIntro;
    private ConstraintLayout layoutFailed;
    private RelativeLayout layoutSuccess;
    private int fingerID;



    /*********** Face Biometric Prompt ***********/
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String FORWARD_SLASH = "/";
    FingerprintDialog fingerprintDialog;
    Fingerprint fingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        initViews();
        setupToolbar();
        setListener();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        btAmHere = findViewById(R.id.bt_am_here);
        btTryAgain = findViewById(R.id.bt_try_again);
        layoutIntro = findViewById(R.id.layout_intro);
        layoutFailed = findViewById(R.id.layout_failed);
        layoutSuccess = findViewById(R.id.layout_success);
        txtTimer = findViewById(R.id.tv_time);
    }

    private void setListener() {
        btAmHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBiometricStatus();
            }
        });
        btTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    show_dialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFingerprintInfo(getApplicationContext()) ;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P ) {

            if (!PreferenceUtility.getInstance(getApplicationContext()).getBoolean(KEY_WRONG_FINGERPRINT, false)
                    || PreferenceUtility.getInstance(getApplicationContext()).getBoolean(KeyConstants.KEY_CHECK_IN_NOTIF, true)){
                checkBiometricStatus();
                PreferenceUtility.getInstance(getApplicationContext()).getBoolean(KeyConstants.KEY_CHECK_IN_NOTIF, false) ;
            } else {
                final AlertBottomDialog wifiDialog = new AlertBottomDialog(getApplicationContext());
                wifiDialog.setOnClickAlertListener(new OnClickAlertListener() {
                    @Override
                    public void onAlertClick() {
                        wifiDialog.dismiss();
                    }
                });
                wifiDialog.setCancelable(false);
                wifiDialog.create();
                wifiDialog.setIvAvatar(R.drawable.red_faceid);
                wifiDialog.setTextButton(getResources().getString(R.string.bt_contact_support));
                wifiDialog.setTvSubTitle(getResources().getString(R.string.alert_finger_changed_msg));
                wifiDialog.show();
            }
        }else {
            if (getFingerprintInfo(getApplicationContext()) == 1)
                show_dialog();
            else {
                CompliantBody compliantBody = new CompliantBody();
                compliantBody.setCompliant(0);
                compliantBody.setFingerReseted(1);
                compliantBody.setReason("FingerReseted");
                ServiceApi.getInstance().sendCompliantStatus(compliantBody);
                final AlertBottomDialog wifiDialog = new AlertBottomDialog(getApplicationContext());
                wifiDialog.setOnClickAlertListener(new OnClickAlertListener() {
                    @Override
                    public void onAlertClick() {
                        wifiDialog.dismiss();
                        ((DashboardActivity) getApplicationContext()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_support), true);
                    }
                });
                wifiDialog.setCancelable(false);
                wifiDialog.create();
                wifiDialog.setIvAvatar(R.drawable.red_faceid);
                wifiDialog.setTextButton(getApplicationContext().getResources().getString(R.string.bt_contact_support));
                wifiDialog.setTvSubTitle(getApplicationContext().getResources().getString(R.string.alert_finger_changed_msg));
                wifiDialog.show();
                setBtnAgreedisabled();
                PreferenceUtility.getInstance(getApplicationContext()).putBoolean(KeyConstants.KEY_WRONG_FINGERPRINT, true);
            }
        }


    }

    public void setBtnAgreedisabled(){
        btTryAgain.setEnabled(false);
    }


    public boolean isSameFinger (){
        boolean isSameFinger = true ;

        if (fingerID != PreferenceUtility.getInstance(getApplicationContext()).getInteger(KeyConstants.KEY_ID_FINGERPRINT,0)) {
            isSameFinger = false ;
        }

        return isSameFinger ;
    }


    private void setupToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Check In");
        getSupportActionBar().setTitle("Check In");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void checkBiometricStatus() {
        KeyguardManager keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(this);

        try {
            if (fingerprintManagerCompat.isHardwareDetected()
                    && fingerprintManagerCompat.hasEnrolledFingerprints() && getFingerprintInfo(getApplicationContext()) == 1) {
                startAuthBiometricSDK();
            } else if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.USE_BIOMETRIC) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();
            } else if (getFingerprintInfo(getApplicationContext()) > 1) {
                Toast.makeText(this, "Please Try to keep one finger print", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.i(getClass().getName(), e.getMessage());
        }
    }

    private void show_dialog() {
        fingerprintDialog = FingerprintDialog.initialize(this)
                .title("")
                .message("Check my finger print")
                .callback(this);
        fingerprintDialog.show();
    }


    private void displayErrorLayout() {
        layoutIntro.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.VISIBLE);
        long maxTimeInMilliseconds = 5 * 60 * 1000;// in your case
        startTimer(maxTimeInMilliseconds, 1000);
    }

    private void displayLayoutSucess() {
        layoutSuccess.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.GONE);
        layoutIntro.setVisibility(View.GONE);
        startTimer();
    }

    public void startTimer(final long finish, long tick) {
        CountDownTimer t;
        t = new CountDownTimer(finish, tick) {

            public void onTick(long millisUntilFinished) {
                long remainedSecs = millisUntilFinished / 1000;
                txtTimer.setText("" + (remainedSecs / 60) + ":" + (remainedSecs % 60)+ " min") ;// manage it accordign to you
            }

            public void onFinish() {
                txtTimer.setText("00:00:00");
                Toast.makeText(getBaseContext(), "Finish", Toast.LENGTH_SHORT).show();

                cancel();
            }
        }.start();
    }


    private void startTimer() {
        new CountDownTimer(1500, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                navigateToMainActivity();
            }
        }.start();
    }

    public void navigateToMainActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
    }

    private void generateSecretKey() {
        KeyGenerator keyGenerator = null;
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(false)
                .build();
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        if (keyGenerator != null) {
            try {
                keyGenerator.init(keyGenParameterSpec);
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            keyGenerator.generateKey();
        }
    }


    public void startAuthBiometricSDK() {

        if (FingerprintDialog.isAvailable(this)) {
            fingerprintDialog = FingerprintDialog.initialize(this)
                    .title("")
                    .message("Check my finger print")
                    .callback(this);

            if(getBaseContext() != null && !isFinishing()) {
                fingerprintDialog.show(); // if fragment use getActivity().isFinishing() or isAdded() method
            }
        }

        fingerprint = findViewById(R.id.fingerprint);
        if (fingerprint != null) {
            fingerprint.callback(this, "KeyName2")
                    .circleScanningColor(android.R.color.black)
                    .fingerprintScanningColor(R.color.colorAccent)
                    .authenticate();
        }
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
                            fingerID = (int) getFingerId.invoke(item);
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

    @Override
    public void onAuthenticationSucceeded() {
        Log.i(getClass().getName(), "onAuthenticationSucceeded");
        fingerprintDialog.dismiss();
        displayLayoutSucess();
        CompliantBody compliantBody = new CompliantBody();
        compliantBody.setCompliant(1);
        compliantBody.setFingerReseted(0);
        compliantBody.setReason("Success Check in");
        ServiceApi.getInstance().sendCompliantStatus(compliantBody);

    }

    @Override
    public void onAuthenticationCancel() {
        Log.i(getClass().getName(), "onAuthenticationCancel");
        fingerprintDialog.dismiss();
        displayErrorLayout();
    }

    @Override
    public void onAuthenticationFailed() {
        Log.i(getClass().getName(), "onAuthenticationFailed");
        fingerprintDialog.dismiss();
        displayErrorLayout();
    }

    @Override
    public void onNewFingerprintEnrolled(FingerprintToken token) {
        if (fingerprintDialog != null)
            fingerprintDialog.dismiss();

        CompliantBody compliantBody = new CompliantBody();
        compliantBody.setCompliant(0);
        compliantBody.setFingerReseted(1);

        ServiceApi.getInstance().sendCompliantStatus(compliantBody);
            final AlertBottomDialog contactSupportDialog = new AlertBottomDialog(getApplicationContext());
            contactSupportDialog.setOnClickAlertListener(new OnClickAlertListener() {
                @Override
                public void onAlertClick() {
                    contactSupportDialog.dismiss();
                }
            });
            contactSupportDialog.setCancelable(false);
            contactSupportDialog.create();
            contactSupportDialog.setIvAvatar(R.drawable.red_faceid);
            contactSupportDialog.setTextButton(getResources().getString(R.string.bt_contact_support));
            contactSupportDialog.setTvSubTitle(getResources().getString(R.string.alert_finger_changed_msg));
            contactSupportDialog.show();
            btAmHere.setEnabled(false);
            PreferenceUtility.getInstance(getApplicationContext()).putBoolean(KeyConstants.KEY_WRONG_FINGERPRINT, true);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
