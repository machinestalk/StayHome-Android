package com.machinestalk.stayhome.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.service.ServiceFactory;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.listeners.OnBackPressed;

import org.greenrobot.eventbus.EventBus;


/**
 * Created on 1/19/2017.
 */

public class BaseFragment extends com.machinestalk.android.fragments.BaseFragment implements OnBackPressed {

    OnFragmentAttachedListener onFragmentAttachedListener;
    private Activity mActivity;
    private EventBus myEventBus = EventBus.getDefault();
    private static boolean isDeviceExpired = false;


    @Override
    public void onAttach(Activity activity) {

        mActivity = activity;
        super.onAttach(activity);
        if (onFragmentAttachedListener == null) {
            return;
        }

        onFragmentAttachedListener.onAttached(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ServiceFactory getServiceFactory() {
        return ConnectedCar.getInstance().getServiceFactory();
    }

    @Override
    protected BaseView getViewForController(Controller controller) {
        return null;
    }

    public void setOnFragmentAttachedListener(OnFragmentAttachedListener onFragmentAttachedListener) {

        this.onFragmentAttachedListener = onFragmentAttachedListener;
    }

    public interface OnFragmentAttachedListener {
        public void onAttached(BaseFragment baseFragment);

        public void onCreateView(BaseFragment baseFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (onFragmentAttachedListener != null) {

            onFragmentAttachedListener.onCreateView(this);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void onBackPressed() {

    }

}
