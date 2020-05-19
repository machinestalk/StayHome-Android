package com.machinestalk.stayhome.utils;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.orhanobut.logger.Logger;

public class AppLifecycleObserver implements LifecycleObserver {

    public static final String TAG = AppLifecycleObserver.class.getName();
    private boolean isAppInForeground = false;

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        isAppInForeground = true;
        //run the code we need
        Logger.i(" App Entered Foreground :  ");

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        isAppInForeground = false;
        //run the code we need
        Logger.i(" App Entered Background :  ");
    }

    public boolean isAppInForeground() {
        return isAppInForeground;
    }
}
