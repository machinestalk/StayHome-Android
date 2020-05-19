package com.machinestalk.stayhome.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.views.LandingActivityView;


/**
 * Created on 4/4/2017.
 */

public class LandingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (AppConfig.getInstance().isUserLoggedIn()) {
            navigateToMainActivity();
        }

    }


    public void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected BaseView getViewForController(Controller controller) {
        return new LandingActivityView(controller);
    }

    public void navigateToLogin() {
        Intent intent = new Intent(getBaseActivity(), LoginActivity.class);
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


    @Override
    public void willStartCall() {
        ((LandingActivityView) view).showProgress();

    }


    /**
     *
     */


    @Override
    public void didFinishCall(boolean isSuccess) {

        ((LandingActivityView) view).hideProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }

    private boolean isNetworkConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }

    }
}
