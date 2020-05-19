package com.machinestalk.stayhome.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.service.ServiceApi;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.CheckInFragmentView;

import static com.machinestalk.stayhome.constants.KeyConstants.KEY_WRONG_FINGERPRINT;

public class CheckInFragment extends BaseFragment implements Controller, ServiceSecondaryEventHandler {

    private Controller mController;

    @Override
    protected BaseView getViewForController(Controller controller) {
        mController = controller;
        return new CheckInFragmentView(controller);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return super.onCreateView(inflater, container, savedInstanceState);

    }


    @Override
    public String getActionBarTitle() {
        return getString(R.string.ApDr_ApDr_SBtn_check_in);
    }


    @Override
    public void onPause() {
        super.onPause();
        getViewForController(mController).onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        getViewForController(mController).onResume();
        ((CheckInFragmentView) view).getFingerprintInfo(getBaseActivity());
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P ) {
            if (!PreferenceUtility.getInstance(getBaseActivity()).getBoolean(KEY_WRONG_FINGERPRINT, false))
            ((CheckInFragmentView) view).checkBiometricStatus();
            else {
                final AlertBottomDialog wifiDialog = new AlertBottomDialog(getBaseActivity());
                wifiDialog.setOnClickAlertListener(new OnClickAlertListener() {
                    @Override
                    public void onAlertClick() {
                        ((DashboardActivity) getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_support), true);
                        wifiDialog.dismiss();
                    }
                });
                wifiDialog.setCancelable(false);
                wifiDialog.create();
                wifiDialog.setIvAvatar(R.drawable.red_faceid);
                wifiDialog.setTextButton(getBaseActivity().getResources().getString(R.string.bt_contact_support));
                wifiDialog.setTvSubTitle(getBaseActivity().getResources().getString(R.string.alert_finger_changed_msg));
                wifiDialog.show();

            }
        }else {
            if (((CheckInFragmentView) view).getFingerprintInfo(getBaseActivity()) == 1)
                ((CheckInFragmentView) view).show_dialog();
            else {
                CompliantBody compliantBody = new CompliantBody();
                compliantBody.setCompliant(0);
                compliantBody.setFingerReseted(1);
                ServiceApi.getInstance().sendCompliantStatus(compliantBody);
                final AlertBottomDialog wifiDialog = new AlertBottomDialog(getBaseActivity());
                wifiDialog.setOnClickAlertListener(new OnClickAlertListener() {
                    @Override
                    public void onAlertClick() {
                        wifiDialog.dismiss();
                        ((DashboardActivity) getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_support), true);
                    }
                });
                wifiDialog.setCancelable(false);
                wifiDialog.create();
                wifiDialog.setIvAvatar(R.drawable.red_faceid);
                wifiDialog.setTextButton(getBaseActivity().getResources().getString(R.string.bt_contact_support));
                wifiDialog.setTvSubTitle(getBaseActivity().getResources().getString(R.string.alert_finger_changed_msg));
                wifiDialog.show();
                ((CheckInFragmentView) view).setBtnAgreedisabled();
                PreferenceUtility.getInstance(getBaseActivity()).putBoolean(KeyConstants.KEY_WRONG_FINGERPRINT, true);
            }
        }

    }



    @Override
    public void onStop() {
        super.onStop();
        Util.hideSoftKeyboard(getBaseActivity());
        getViewForController(mController).onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getViewForController(mController).onDestroy();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    @Override
    public void willStartCall() {

    }

    @Override
    public void didFinishCall(boolean isSuccess) {

    }

    public void navigateToHomeScreen() {
        try {
            ((DashboardActivity) getBaseActivity()).showHomeDashboard(null);
        } catch (Exception e) {
        }
    }

}
