package com.machinestalk.stayhome.views;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.components.Button;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.service.ServiceApi;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.machinestalk.stayhome.views.base.BaseView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

import javax.crypto.KeyGenerator;

import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.callback.PasswordCallback;
import me.aflak.libraries.dialog.FingerprintDialog;
import me.aflak.libraries.utils.FingerprintToken;
import me.aflak.libraries.view.Fingerprint;

import static android.content.Context.KEYGUARD_SERVICE;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_NAME;

public class CheckInFragmentView extends BaseView implements FingerprintSecureCallback, PasswordCallback, FingerprintDialogCallback {

    private final String TAG = "CheckInFragmentView";
    private Button btAmHere;
    private Button btTryAgain;
    private Toolbar toolbar;
    private RelativeLayout layoutIntro;
    private ConstraintLayout layoutFailed;
    private RelativeLayout layoutSuccess;
    private Context mContext;
    private TextView txtTimer;
    private int fingerID;


    /*********** Face Biometric Prompt ***********/
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String FORWARD_SLASH = "/";
    FingerprintDialog fingerprintDialog ;
    Fingerprint fingerprint ;
    private CountDownTimer mNavigationTimer;


    public CheckInFragmentView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_check_in;
    }

    @Override
    protected void onCreate() {

        try {
            ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_check_in), false);
        } catch (Exception e) {
        }

        initContext();
        initView();
//        setupToolbar();
    }

    private void initContext() {
        mContext = getBaseActivity();
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected void onToolBarSetup(Toolbar toolBar) {
        super.onToolBarSetup(toolBar);
        this.toolbar = toolBar;
    }


    private void initView() {
        btAmHere = findViewById(R.id.bt_am_here);
        btTryAgain = findViewById(R.id.bt_try_again);
        toolbar = findViewById(R.id.toolbar);
        layoutIntro = findViewById(R.id.layout_intro);
        layoutFailed = findViewById(R.id.layout_failed);
        layoutSuccess = findViewById(R.id.layout_success);
        txtTimer = findViewById(R.id.tv_time);
    }

    private void setupToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        (getBaseActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(mContext.getResources().getString(R.string.sign_in));
    }

    public boolean isSameFinger (){
        boolean isSameFinger = true ;

        if (fingerID != PreferenceUtility.getInstance(getBaseActivity()).getInteger(KeyConstants.KEY_ID_FINGERPRINT,0)) {
            isSameFinger = false ;
        }
        return isSameFinger ;
    }

    public void checkBiometricStatus (){
        KeyguardManager keyguardManager =
                (KeyguardManager) getBaseActivity().getSystemService(KEYGUARD_SERVICE);
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(getBaseActivity());
        try {
            if (fingerprintManagerCompat.isHardwareDetected()
                    && fingerprintManagerCompat.hasEnrolledFingerprints() && getFingerprintInfo(getBaseActivity()) == 1) {
                startAuthBiometricSDK();
            }else if (!keyguardManager.isKeyguardSecure()) {
                showToast("Fingerprint authentication permission not enabled");
            } else if (ActivityCompat.checkSelfPermission(getBaseActivity(),
                    Manifest.permission.USE_BIOMETRIC) !=
                    PackageManager.PERMISSION_GRANTED) {
                showToast("Fingerprint authentication permission not enabled");
            }else if (getFingerprintInfo(getBaseActivity()) > 1){
                displayErrorOneFinger();
            }else {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                getBaseActivity().startActivity(intent);
            }
        }catch (Exception e){
            Log.i(getClass().getName(), e.getMessage());
        }
    }

    private void displayErrorOneFinger (){
        final AlertBottomDialog wifiDialog = new AlertBottomDialog(getBaseActivity());
        wifiDialog.setOnClickAlertListener(new OnClickAlertListener() {
            @Override
            public void onAlertClick() {
                wifiDialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                getBaseActivity().startActivity(intent);
            }
        });
        wifiDialog.setCancelable(false);
        wifiDialog.create();
        wifiDialog.setIvAvatar(R.drawable.red_fail);
        wifiDialog.setTextButton(getBaseActivity().getString(R.string.alert_ok));
        wifiDialog.setTvTitle(getBaseActivity().getResources().getString(R.string.attention));
        wifiDialog.setTvSubTitle("Please Try to keep one finger print");
        wifiDialog.show();

    }

    @Override
    protected void setActionListeners() {
        btAmHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    startAuthBiometricSDK();
                checkBiometricStatus();
            }
        });
        btTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkBiometricStatus();

                    if (getFingerprintInfo(getBaseActivity()) == 1)
                        show_dialog();


                }


        });


    }

    public void setBtnAgreedisabled(){
        btTryAgain.setEnabled(false);
    }

    public void show_dialog(){
        fingerprintDialog = FingerprintDialog.initialize(getBaseActivity())
                .title("")
                .message("Check my finger print")
                .callback(this) ;
        fingerprintDialog.show();
    }

    private void displayErrorLayout() {
        layoutFailed.setVisibility(View.VISIBLE);
        layoutIntro.setVisibility(View.GONE);
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
                cancel();
            }
        }.start();
    }

    private void startTimer() {
       mNavigationTimer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (controller != null && controller.getBaseActivity() != null){
                    ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_home), true);
                }
            }
        }.start();

    }


    public void startAuthBiometricSDK(){

            if (FingerprintDialog.isAvailable(getBaseActivity().getApplicationContext())) {
                fingerprintDialog = FingerprintDialog.initialize(getBaseActivity())
                        .title("")
                        .message("Check my finger print")
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
    public void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();

    }

    public void onPause() {
        Log.d(TAG, "onPause called");
        if (mNavigationTimer != null){
            mNavigationTimer.cancel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
        if (mNavigationTimer != null){
            mNavigationTimer.cancel();
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
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


    @Override
    public void onAuthenticationSucceeded() {
        Log.i(getClass().getName(),"onAuthenticationSucceeded");
        fingerprintDialog.dismiss();
        displayLayoutSucess();
        CompliantBody compliantBody = new CompliantBody();
        compliantBody.setCompliant(1);
        compliantBody.setFingerReseted(0);
        compliantBody.setReason("Success check in");
        ServiceApi.getInstance().sendCompliantStatus(compliantBody);

    }


    @Override
    public void onAuthenticationCancel() {
        Log.i(getClass().getName(),"onAuthenticationCancel");
        fingerprintDialog.dismiss();
        displayErrorLayout();
    }

    @Override
    public void onAuthenticationFailed() {
        Log.i(getClass().getName(),"onAuthenticationFailed");
        fingerprintDialog.dismiss();
        displayErrorLayout();
    }

    @Override
    public void onNewFingerprintEnrolled(FingerprintToken token) {
        if (fingerprintDialog != null)
        fingerprintDialog.dismiss();

        CompliantBody compliantBody = new CompliantBody();
        compliantBody.setCompliant(0);
        compliantBody.setFingerReseted(0);
        compliantBody.setReason("FingerReseted");

        ServiceApi.getInstance().sendCompliantStatus(compliantBody);
            final AlertBottomDialog contactSupportDialog = new AlertBottomDialog(getBaseActivity());
            contactSupportDialog.setOnClickAlertListener(new OnClickAlertListener() {
                @Override
                public void onAlertClick() {
                    contactSupportDialog.dismiss();
                    ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_support), true);

                }
            });
            contactSupportDialog.setCancelable(false);
            contactSupportDialog.create();
            contactSupportDialog.setIvAvatar(R.drawable.red_faceid);
            contactSupportDialog.setTextButton(getBaseActivity().getResources().getString(R.string.bt_contact_support));
            contactSupportDialog.setTvSubTitle(getBaseActivity().getResources().getString(R.string.alert_finger_changed_msg));
            contactSupportDialog.show();
            btAmHere.setEnabled(false);
            PreferenceUtility.getInstance(getBaseActivity()).putBoolean(KeyConstants.KEY_WRONG_FINGERPRINT, true);
    }

    @Override
    public void onAuthenticationError(int errorCode, String error) {
        Log.i(getClass().getName(),"onAuthenticationError");
    }

    @Override
    public void onPasswordSucceeded() {
        Log.i(getClass().getName(),"onPasswordSucceeded") ;
    }

    @Override
    public boolean onPasswordCheck(String password) {
        return password.equals("password");

    }

    @Override
    public void onPasswordCancel() {
        Log.i(getClass().getName(),"onPasswordCancel");
    }
}
