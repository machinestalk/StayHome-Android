package com.machinestalk.stayhome.views;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.views.base.BaseView;

/**
 * Created by sara.batool on 10/27/2017.
 */

public class SplashScreenView extends BaseView {


    public SplashScreenView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.splash_screen;
    }

    @Override
    protected void onCreate() {

    }
    public void showLoading() {


    }

    public void hideLoading() {

    }

    @Override
    protected void setActionListeners() {

    }
}
