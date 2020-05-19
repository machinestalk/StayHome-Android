package com.machinestalk.stayhome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.service.OnClearFromRecentService;
import com.machinestalk.stayhome.views.SplashScreenView;

/**
 * Created by sara.batool on 10/27/2017.
 */

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        if (AppConfig.getInstance().isUserLoggedIn()) {
            new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    ((SplashScreenView) view).hideLoading();
                    navigateToMainActivity();
                }
            }.start();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getBaseActivity(), LandingActivity.class));
                    finish();
                }
            }, 1500);
        }

    }


    @Override
    public void willStartCall() {

    }

    @Override
    public void didFinishCall(boolean isSuccess) {

    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
    }

    private boolean isNetworkConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    protected BaseView getViewForController(Controller controller) {
        return new SplashScreenView(controller);
    }


}
