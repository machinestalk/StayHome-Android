package com.machinestalk.stayhome.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.responses.BraceletListResponse;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.views.BraceletFragmentView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.machinestalk.stayhome.config.AppConfig.braceletDialog;
import static com.machinestalk.stayhome.constants.KeyConstants.KEY_MAC_ADRESS;

public class BraceletFragment extends BaseFragment {
    private Controller mController;

    @Override
    protected BaseView getViewForController(Controller controller) {
        mController = controller;
        return new BraceletFragmentView(controller);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.bracelet_fragment_title);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.ADD_BRACELET_REQUEST_CODE){

                String macAddress = data.getStringExtra(AppConstants.BRACELET_MAC_ADDRESS);
                ((BraceletFragmentView) view).onMacAddressReceived(macAddress, true);
                ((BraceletFragmentView) view).hideAddBraceletDialog();
                ((BraceletFragmentView) view).setPlusBtnClicked(true);
            }
        }else if (resultCode == RESULT_CANCELED){
//            ((BraceletFragmentView) view).showAddScanBraceletDialog();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
//        AppConfig.getInstance().StartBackgroundTasks(getBaseActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
//        AppConfig.getInstance().cancelAlarm();
        String macStored = PreferenceUtility.getInstance(getBaseActivity()).getString(KeyConstants.KEY_MAC_ADRESS, "");


    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getBaseActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        Fragment fragment = getVisibleFragment();

        if (fragment instanceof BraceletFragment) {
            switch (event) {
                case AppConstants.EVENT_BRACELET_NOT_DETECTED: {

                    if  (!((DashboardActivity) getBaseActivity()).isDrawerOpened()){
                        AppConfig.getInstance().showBraceletNotDetectedDialog(getBaseActivity());
                        PreferenceUtility.getInstance(getBaseActivity()).putBoolean(PreferenceUtility.BRACELET_DETECTED, false);
                    }

                }
                break;
                case AppConstants.EVENT_BRACELET_DETECTED: {
                    if (!getBaseActivity().isFinishing()) {
                        hideBraceletDialog();
                    }
                    PreferenceUtility.getInstance(getBaseActivity()).putBoolean(PreferenceUtility.BRACELET_DETECTED, true);
                }
                break;
            }
        }

    }

    private void hideBraceletDialog() {
        if (braceletDialog != null && braceletDialog.isShowing()) {
            braceletDialog.dismiss();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    public void checkMacAddressBracelet(String macAddress) {

        User user = AppConfig.getInstance().getUser();
        ((ServiceFactory) serviceFactory).getUserService()
                .checkBracelet(macAddress, user.getDeviceId(), user.getTenantId(), user.getCustomerId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(getBaseActivity(), new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {
                        ((BraceletFragmentView) view).showProgress();
                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                        ((BraceletFragmentView) view).hideProgress();
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        BraceletListResponse braceletListResponse = (BraceletListResponse)response;
                        String connectedDateTime = "";
                        ((BraceletFragmentView) view).hideAddBraceletDialog();
                        if (braceletListResponse != null){
                            long connectedTime = braceletListResponse.getConnectedTime();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm a", Locale.US);
                            Date netDate = (new Date(connectedTime * 1000));
                            connectedDateTime = simpleDateFormat.format(netDate);
                            PreferenceUtility.getInstance(getBaseActivity()).putString(PreferenceUtility.BRACELET_Connected_DATE_TIME, connectedDateTime);
                        }

                        ((BraceletFragmentView) view).onSuccessMacAddressSent(connectedDateTime);
                        PreferenceUtility.getInstance(getBaseActivity()).putString(KEY_MAC_ADRESS, macAddress);
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        ((BraceletFragmentView) view).onFailedMacAddressSent();
                        //showToast(errorMessage);
                    }
                });
    }
}
