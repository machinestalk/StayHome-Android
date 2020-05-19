package com.machinestalk.stayhome.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.responses.ChangePhoneNumberResponse;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.service.body.OTPBody;
import com.machinestalk.stayhome.service.body.SignInBodyForm;
import com.machinestalk.stayhome.utils.PermissionUtils;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.LoginActivityView;
import com.orhanobut.logger.Logger;

/**
 * Created on 12/19/2016.
 */

public class LoginActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Controller, ServiceSecondaryEventHandler, ResultCallback {

    private String screenMode = "";
    private static String phoneNumber = "";
    private static String securitytoken = "";
    private static String otp = "";

    @Override
    protected BaseView getViewForController(Controller controller) {

        return new LoginActivityView(controller);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppConfig.getInstance().isUserLoggedIn()) {
            navigateToMainActivity();
        }

        PermissionUtils.checkAndRequestPermissionLocation((Activity) getBaseActivity());

    }

    @Override
    public void willStartCall() {
        ((LoginActivityView) view).showProgress();
    }

    @Override
    public void didFinishCall(boolean isSuccess) {

        ((LoginActivityView) view).hideProgress();
    }

    @Override
    public boolean hasToolbar() {

        return false;
    }


    public void signIn(final String countryCode, final String username) {
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        SignInBodyForm form = new SignInBodyForm();
        form.setUsername(countryCode + username);
        ((ServiceFactory) serviceFactory).getUserService()
                .signIn(form)
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, this) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        ((LoginActivityView) view).showOtpScreen(((ChangePhoneNumberResponse) response).getSecurityToken());
                        phoneNumber = countryCode + username;
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        if (code == 400 && errorMessage.contains(KeyConstants.KEY_SECURITY_TOKEN)) {
                            onSecurityTokenReceived(errorMessage);
                        } else {
                            Logger.i("error is " + errorMessage + "code is : " + code);
                            showToast(errorMessage);
                        }
                    }
                });
    }

    public void onLoginSuccess() {
        PreferenceUtility.getInstance(this).putBoolean(PreferenceUtility.IS_LOGGED, true);
        Intent intent = new Intent(this, TermsConditionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
    }


    private void showSessionErrorDialog() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((LoginActivityView) view).phoneNumberRequestfocus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ConnectedCar.getInstance().isSessionExpired()) {
            showSessionErrorDialog();
            ConnectedCar.getInstance().setSessionExpired(false);
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);
    }

    private void onSecurityTokenReceived(String securityToken) {

        String[] token = securityToken.split("\"");

        if (token.length >= 3 && !TextUtils.isEmpty(token[3])) {

            ((LoginActivityView) view).showOtpScreen(token[3]);

        }
    }

    @SuppressLint("HardwareIds")
    public void onSignOTP(final String otpCode) {
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        OTPBody body = new OTPBody();
        body.setPhoneNumber(phoneNumber);
        body.setPhoneOtp(otpCode);
        body.setPhoneUdid(androidId);

        getServiceFactory().getUserService()
                .VerifySignOTP(body)
                .enqueue(new ServiceCallback(this, this) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        Util.hideSoftKeyboard(LoginActivity.this);
                        User user = ((User) response);
                        if (user == null) {
                            return;
                        }
                        AppConfig.getInstance().setUser(user);
                        updateDeviceToken();
                        onLoginSuccess();
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {

                        if (code == 400 && errorMessage.equalsIgnoreCase(KeyConstants.KEY_OTP_EXPIRED)) {
                            clearNotificationFromBar();
                            clearAppData();
                        } else {
                            displayPoupAnotherDevice(errorMessage);
                        }
                    }
                });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {

        if (((LoginActivityView) view).isPinContainerVisible()) {
            ((LoginActivityView) view).returnToUsernameContainer();
        }  else {
            super.onBackPressed();
        }
    }


}
