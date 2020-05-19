package com.machinestalk.stayhome.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.helpers.ImagePickerManager;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.SupportFragmentView;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;

/**
 * Created on 12/26/2016.
 */

public class SupportFragment extends BaseFragment implements Controller, ServiceSecondaryEventHandler {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected BaseView getViewForController(Controller controller) {
        return new SupportFragmentView(controller);
    }

    @Override
    public String getActionBarTitle() {

        return getString(R.string.ApDr_ApDr_SBtn_support);
    }
    @Override
    public boolean hasToolbar() {
        return true;
    }

    /**
     *
     * @return
     */
    public boolean isAttachmentUpdateMode(){
       return  ((SupportFragmentView) view).getAttachmentMode().getMode() == AppConstants.MODE_UPDATE;
    }

    public void submitContactForm(String body) {

        ((ServiceFactory) serviceFactory).getUserService()
                .contactUs(body)
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, this) {
                    @Override
                    protected void onSuccess(Object response, int code) {

                        showToast(getString(R.string.ApI_ContUs_lbl_contact_us_sent));
                        ((SupportFragmentView) view).clearFields();
                        ((SupportFragmentView) view).resetAttachmentList();

                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {

                        showToast(errorMessage);
                    }
                });
    }

    @Override
    public void willStartCall() {
        ((SupportFragmentView) view).showProgress();
    }

    @Override
    public void didFinishCall(boolean isSuccess) {
        ((SupportFragmentView) view).hideProgress();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            case ImagePickerManager.CAMERA_REQUEST_CODE:
            case ImagePickerManager.GALLERY_REQUEST_CODE:
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                ((SupportFragmentView) view).handleIntent(data, requestCode);
            }

            break;

        }
    }

    @Override
    public void onBackPressed() {
        if (((SupportFragmentView) view).resetAttachmentModeOnBackPressed()){
            return;
        }
        super.onBackPressed();

    }

    @Override
    public void onStop() {
        super.onStop();
        Util.hideSoftKeyboard(getBaseActivity());

    }

}
