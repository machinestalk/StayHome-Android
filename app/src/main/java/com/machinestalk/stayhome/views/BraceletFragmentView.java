package com.machinestalk.stayhome.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.BeaconListActivity;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.components.EditText;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.dialogs.ConfirmationDialog;
import com.machinestalk.stayhome.fragments.BraceletFragment;
import com.machinestalk.stayhome.listeners.OnChooseListener;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.base.BaseView;
import com.orhanobut.logger.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.machinestalk.stayhome.config.AppConfig.braceletDialog;

public class BraceletFragmentView extends BaseView {

    private Context mContext;
    private EditText mCodeNumberOne;
    private EditText mCodeNumberTwo;
    private EditText mCodeNumberThree;
    private EditText mCodeNumberFour;
    private EditText mCodeNumberFive;
    private EditText mCodeNumberSix;
    private EditText mCodeNumberSeven;
    private EditText mCodeNumberEight;
    private EditText mCodeNumberNine;
    private EditText mCodeNumberTen;
    private EditText mCodeNumberEleven;
    private EditText mCodeNumberTwelve;
    private LinearLayout mFieldsContainerLinearLayout;
    private TextView mDescriptionTextView;
    private TextWatcher mTextWatcher;
    private View.OnKeyListener mOnKeyListener;
    private View mProgress;
    private boolean isDismissedByClickButton = false;
    private TextView mTitleTextView;
    private AppCompatImageView mMenuImageView;
    private boolean isComingFromBraceletList;
    private boolean isPlusBtnClicked = false;
    private boolean isBraceletNotDetectedShowing = false ;
    ScheduledExecutorService scheduler;
    ScheduledExecutorService schedulerSuccessDialog;
    private ConfirmationDialog confirmationDialog;
    private AlertBottomDialog alertBottomDialog;
    private AlertBottomDialog  successResponseBottomDialog;
    private String mMacStored;

    public BraceletFragmentView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.view_fragment_bracelet;
    }

    @Override
    protected void onCreate() {

        try {
            ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_bracelet), false);
        } catch (Exception e) {
        }

        initContext();
        initView();
        mMacStored = PreferenceUtility.getInstance(getBaseActivity()).getString(KeyConstants.KEY_MAC_ADRESS, "");


    }

    private void initContext() {
        mContext = getBaseActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(successResponseBottomDialog != null ){
            successResponseBottomDialog.dismiss();
            successResponseBottomDialog.hide();
        }
        if(confirmationDialog != null ){
            confirmationDialog.dismiss();
            confirmationDialog.hide();
        }
        if (scheduler != null)
            scheduler.shutdown();
        if (schedulerSuccessDialog != null)
            schedulerSuccessDialog.shutdown();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(successResponseBottomDialog != null ){
            successResponseBottomDialog.dismiss();
            successResponseBottomDialog.hide();
        }
        if(confirmationDialog != null ){
            confirmationDialog.dismiss();
            confirmationDialog.hide();
        }
        if (scheduler != null)
            scheduler.shutdown();
        if (schedulerSuccessDialog != null)
            schedulerSuccessDialog.shutdown();

    }

    private void hideBraceletDialog() {
        if (braceletDialog != null && braceletDialog.isShowing()) {
            braceletDialog.dismiss();
        }
    }

    public void startSuccessDialogSchedule (){
        schedulerSuccessDialog = Executors.newSingleThreadScheduledExecutor();

        schedulerSuccessDialog.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        getBaseActivity().runOnUiThread(new Thread(new Runnable() {
                            public void run() {

                                if (!((DashboardActivity) controller.getBaseActivity()).isDrawerOpened()
                                        && !successResponseBottomDialog.isShowing() && !isBraceletNotDetectedShowing){
                                    String connectedDateTime = PreferenceUtility.getInstance(getBaseActivity()).getString(PreferenceUtility.BRACELET_Connected_DATE_TIME, "");

                                    showSuccessDialog(connectedDateTime);
                                }

                            }
                        }));
                    }
                }, 0, 5, TimeUnit.SECONDS);

    }
    public void startAddBraceletDialogSchedule (){
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        getBaseActivity().runOnUiThread(new Thread(new Runnable() {
                            public void run() {

                                if (!((DashboardActivity) controller.getBaseActivity()).isDrawerOpened() && confirmationDialog != null && !confirmationDialog.isShowing() ){
                                    showAddScanBraceletDialog();
                                }

                            }
                        }));
                    }
                }, 0, 6, TimeUnit.SECONDS);

    }


    @Override
    public void onResume() {
        super.onResume();
        mMacStored = PreferenceUtility.getInstance(getBaseActivity()).getString(KeyConstants.KEY_MAC_ADRESS, "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Write whatever to want to do after delay specified (1 sec)
                String connectedDateTime = PreferenceUtility.getInstance(getBaseActivity()).getString(PreferenceUtility.BRACELET_Connected_DATE_TIME, "");

                if (!StringUtility.isEmptyOrNull(mMacStored)){
                    onMacAddressReceived(mMacStored, false);
                    onSuccessMacAddressSent(connectedDateTime);
                    hideAddBraceletDialog();
                }
                if (!PreferenceUtility.getInstance(getBaseActivity()).getBoolean(PreferenceUtility.BRACELET_DETECTED, false)
                        && !StringUtility.isEmptyOrNull(mMacStored)){
                    AppConfig.getInstance().showBraceletNotDetectedDialog(getBaseActivity());
                    isBraceletNotDetectedShowing = true ;
                    if (successResponseBottomDialog != null) {
                        successResponseBottomDialog.dismiss();
                        successResponseBottomDialog.hide();
                    }
                    schedulerSuccessDialog.shutdown();
                }else {
                    if (!getBaseActivity().isFinishing()) {
                        hideBraceletDialog();
                    }
                    isBraceletNotDetectedShowing = false ;
                }


                if (StringUtility.isEmptyOrNull(mMacStored) && !isPlusBtnClicked){
                    showAddScanBraceletDialog();
                    startAddBraceletDialogSchedule();


                }

            }
        }, 500);




    }

    private void initView() {

        mCodeNumberOne = findViewById(R.id.view_fragment_bracelet_code_one);
        mCodeNumberTwo = findViewById(R.id.view_fragment_bracelet_code_two);
        mCodeNumberThree = findViewById(R.id.view_fragment_bracelet_code_three);
        mCodeNumberFour = findViewById(R.id.view_fragment_bracelet_code_four);
        mCodeNumberFive = findViewById(R.id.view_fragment_bracelet_code_five);
        mCodeNumberSix = findViewById(R.id.view_fragment_bracelet_code_six);
        mCodeNumberSeven = findViewById(R.id.view_fragment_bracelet_code_seven);
        mCodeNumberEight = findViewById(R.id.view_fragment_bracelet_code_eight);
        mCodeNumberNine = findViewById(R.id.view_fragment_bracelet_code_nine);
        mCodeNumberTen = findViewById(R.id.view_fragment_bracelet_code_ten);
        mCodeNumberEleven = findViewById(R.id.view_fragment_bracelet_code_eleven);
        mCodeNumberTwelve = findViewById(R.id.view_fragment_bracelet_code_twelve);
        mFieldsContainerLinearLayout = findViewById(R.id.view_fragment_codes_container);
        mProgress = findViewById(R.id.view_fragment_bracelet_progress);
        mDescriptionTextView = findViewById(R.id.view_fragment_bracelet_text_title);
        mTitleTextView = findViewById(R.id.view_fragment_bracelet_title);
        mMenuImageView = findViewById(R.id.view_fragment_bracelet_menu_image);


        initEvent();
        onTextChangesFields();
    }

    @Override
    protected void setActionListeners() {

        mMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) controller.getBaseActivity()).openDrawer();
            }
        });
    }


    private void initEvent() {

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mCodeNumberOne.isFocused() && !s.toString().equals("")) {
                    mCodeNumberTwo.requestFocus();
                    return;
                }
                if (mCodeNumberTwo.isFocused() && !s.toString().equals("")) {
                    mCodeNumberThree.requestFocus();
                    return;
                }

                if (mCodeNumberThree.isFocused() && !s.toString().equals("")) {
                    mCodeNumberFour.requestFocus();
                    return;
                }

                if (mCodeNumberFour.isFocused() && !s.toString().equals("")) {
                    mCodeNumberFive.requestFocus();
                    return;
                }

                if (mCodeNumberFive.isFocused() && !s.toString().equals("")) {
                    mCodeNumberSix.requestFocus();
                    return;
                }

                if (mCodeNumberSix.isFocused() && !s.toString().equals("")) {
                    mCodeNumberSeven.requestFocus();
                    return;
                }

                if (mCodeNumberSeven.isFocused() && !s.toString().equals("")) {
                    mCodeNumberEight.requestFocus();
                    return;
                }
                if (mCodeNumberEight.isFocused() && !s.toString().equals("")) {
                    mCodeNumberNine.requestFocus();
                    return;
                }

                if (mCodeNumberNine.isFocused() && !s.toString().equals("")) {
                    mCodeNumberTen.requestFocus();
                    return;
                }

                if (mCodeNumberTen.isFocused() && !s.toString().equals("")) {
                    mCodeNumberEleven.requestFocus();
                    return;
                }

                if (mCodeNumberEleven.isFocused() && !s.toString().equals("")) {
                    mCodeNumberTwelve.requestFocus();
                    return;
                }
                if (mCodeNumberTwelve.isFocused() && !s.toString().equals("")) {
                    if(!isComingFromBraceletList){
                        showConfirmationDialog();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        };


        mCodeNumberTwo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberTwo.getText().toString().isEmpty()) {
                    mCodeNumberOne.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberThree.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberThree.getText().toString().isEmpty()) {
                    mCodeNumberTwo.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberFour.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberFour.getText().toString().isEmpty()) {
                    mCodeNumberThree.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberFive.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberFive.getText().toString().isEmpty()) {
                    mCodeNumberFour.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberSix.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberSix.getText().toString().isEmpty()) {
                    mCodeNumberFive.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberSeven.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberSeven.getText().toString().isEmpty()) {
                    mCodeNumberSix.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberEight.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberEight.getText().toString().isEmpty()) {
                    mCodeNumberSeven.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberNine.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberNine.getText().toString().isEmpty()) {
                    mCodeNumberEight.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberTen.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberTen.getText().toString().isEmpty()) {
                    mCodeNumberNine.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberEleven.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberEleven.getText().toString().isEmpty()) {
                    mCodeNumberTen.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCodeNumberTwelve.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mCodeNumberTwelve.getText().toString().isEmpty()) {
                    mCodeNumberEleven.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     *
     */
    public void showAddScanBraceletDialog() {


         confirmationDialog = new ConfirmationDialog(getBaseActivity());

        confirmationDialog.setTitleInvisible();
        confirmationDialog.setTvSubTitle(getString(R.string.bracelet_fragment_add_bracelet_dialog_title));
        confirmationDialog.setTextBtnNo(getString(R.string.bracelet_fragment_add_manually_label));
        confirmationDialog.setTextBtnYes(getString(R.string.bracelet_fragment_scan_label));
        confirmationDialog.setCanceledOnTouchOutside(true);
        // confirmationDialog.setCancelable(false);
        confirmationDialog.sePositiveButtonBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn));
        confirmationDialog.setNegativeButtonBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn));
        confirmationDialog.setDialogImageView(R.drawable.big_add);
        confirmationDialog.setTOpBarInvisible();

        confirmationDialog.setListener(new OnChooseListener() {
            @Override
            public void onAccept() {
                confirmationDialog.dismiss();
                navigateToBeaconActivity();
            }

            @Override
            public void onRefuse() {
                confirmationDialog.dismiss();
                onAddManuallyClick();
            }
        });

        confirmationDialog.show();
    }

    /**
     *
     */
    private void showConfirmationDialog() {
        setDescriptionText(getString(R.string.bracelet_fragment_add_bracelet_third_description));
        Util.hideSoftKeyboard(getBaseActivity());
         alertBottomDialog = new AlertBottomDialog(mContext);
        alertBottomDialog.setTvSubTitle(getString(R.string.bracelet_fragment_dialog_confirmation_title_label));
        alertBottomDialog.setTextButton(getString(R.string.bracelet_fragment_dialog_confirmation_button_label));
        alertBottomDialog.setTextButtonVisibility(true);
        alertBottomDialog.setSpecificCanceledOnTouchOutside(false);
        alertBottomDialog.setTopBarInvisible();
        alertBottomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isComingFromBraceletList = false;
                if (isDismissedByClickButton) {
                    return;
                }
                Logger.i("dialog dismissed");
//                initMacAddressFields();
//                if (isComingFromBraceletList)
//                showAddScanBraceletDialog();
            }
        });

        alertBottomDialog.setOnClickAlertListener(new OnClickAlertListener() {

            @Override
            public void onAlertClick() {
                isDismissedByClickButton = true;
                alertBottomDialog.dismiss();

                if (!isInputsValidated()) {
                    showToast("empty fields");
                    return;
                }
                String macAddress = mCodeNumberOne.getText().toString() + mCodeNumberTwo.getText().toString() +":"+ mCodeNumberThree.getText().toString() + mCodeNumberFour.getText().toString()
                        +":"+ mCodeNumberFive.getText().toString() + mCodeNumberSix.getText().toString() +":"+ mCodeNumberSeven.getText().toString() + mCodeNumberEight.getText().toString()
                        +":"+ mCodeNumberNine.getText().toString() + mCodeNumberTen.getText().toString() +":"+ mCodeNumberEleven.getText().toString() + mCodeNumberTwelve.getText().toString();

                ((BraceletFragment) controller).checkMacAddressBracelet(macAddress);
            }
        });

        alertBottomDialog.show();
    }

    /**
     *
     */
    private void showSuccessDialog(String connectedTime){
         successResponseBottomDialog = new AlertBottomDialog(mContext);
        successResponseBottomDialog.setTvTitle(getString(R.string.bracelet_fragment_dialog_success_mrssage_label));
        successResponseBottomDialog.setTitleColor(R.color.green_light_color);
        successResponseBottomDialog.setTvSubTitle(connectedTime);
//        successResponseBottomDialog.setSecondIconVisible(R.drawable.bluetooth_blue);
        successResponseBottomDialog.setIvAvatar(R.drawable.big_green_check);
        successResponseBottomDialog.setTopBarInvisible();
        successResponseBottomDialog.setTextButtonVisibility(false);
        successResponseBottomDialog.setCancelable(false);
        successResponseBottomDialog.setCanceledOnTouchOutside(false);
        successResponseBottomDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                successResponseBottomDialog.dismiss();
//                displayMacFieldsInNormalColor();
//                initMacAddressFields();
//                showAddScanBraceletDialog();
            }
        }, 3000);
    }

    private void showErrorDialog() {

        final AlertBottomDialog errorResponseBottomDialog = new AlertBottomDialog(mContext);
        errorResponseBottomDialog.setTvSubTitle(getString(R.string.bracelet_fragment_dialog_error_mrssage_label));
        errorResponseBottomDialog.setIvAvatar(R.drawable.red_fail);
        errorResponseBottomDialog.setTopBarInvisible();
        errorResponseBottomDialog.setTextButtonVisibility(false);
        errorResponseBottomDialog.setCancelable(false);
        errorResponseBottomDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                errorResponseBottomDialog.dismiss();
                displayMacFieldsInNormalColor();
                initMacAddressFields();
                showAddScanBraceletDialog();
                startAddBraceletDialogSchedule();

            }
        }, 3000);
    }

    /**
     *
     */
    private void onAddManuallyClick() {
//        if (confirmationDialog != null && !confirmationDialog.isShowing())
        confirmationDialog.dismiss();
        confirmationDialog.hide();
        if (schedulerSuccessDialog != null )
        schedulerSuccessDialog.shutdown();
        if (scheduler != null )
            scheduler.shutdown();

        initMacAddressFields();
        setTitleText(getString(R.string.bracelet_fragment_second_title));
        setDescriptionText(getString(R.string.bracelet_fragment_add_bracelet_second_description));
        setFieldsVisible(true);
        Util.showKeyboard(mContext);
    }


    private void setTitleText(String text){
        mTitleTextView.setText(text);
    }

    private void setDescriptionText(String text){

        mDescriptionTextView.setText(text);
    }


    private void setFieldsVisible(boolean isVisible){
        mFieldsContainerLinearLayout.setVisibility(isVisible? VISIBLE : GONE);
    }

    public void hideAddBraceletDialog(){
        if (confirmationDialog != null ){
            confirmationDialog.dismiss();
            confirmationDialog.hide();
            scheduler.shutdown();
        }
    }


    /**
     *
     */
    public void onMacAddressReceived(String macAddress, boolean showConfirmationDialog){
        if (!StringUtility.isEmptyOrNull(macAddress)){
            Log.i("Bracelet fragment", " mac addressssss :"+macAddress);
            hideAddBraceletDialog();
            setFieldsVisible(true);
            setDescriptionText(getString(R.string.bracelet_fragment_add_bracelet_third_description));
            setTitleText(getString(R.string.bracelet_fragment_third_title));
            String [] mac = macAddress.split(":");
            Log.i("Bracelet fragment", " mac address :");
            if (!StringUtility.isEmptyOrNull(mac[0])){
                String firstChar = String.valueOf(mac[0].charAt(0));
                String secondChar = String.valueOf(mac[0].charAt(1));
                mCodeNumberOne.setText(firstChar);
                mCodeNumberTwo.setText(secondChar);
            }
            if (!StringUtility.isEmptyOrNull(mac[1])){
                String firstChar = String.valueOf(mac[1].charAt(0));
                String secondChar = String.valueOf(mac[1].charAt(1));
                mCodeNumberThree.setText(firstChar);
                mCodeNumberFour.setText(secondChar);
            }
            if (!StringUtility.isEmptyOrNull(mac[2])){
                String firstChar = String.valueOf(mac[2].charAt(0));
                String secondChar = String.valueOf(mac[2].charAt(1));
                mCodeNumberFive.setText(firstChar);
                mCodeNumberSix.setText(secondChar);
            }
            if (!StringUtility.isEmptyOrNull(mac[3])){
                String firstChar = String.valueOf(mac[3].charAt(0));
                String secondChar = String.valueOf(mac[3].charAt(1));
                mCodeNumberSeven.setText(firstChar);
                mCodeNumberEight.setText(secondChar);
            }
            if (!StringUtility.isEmptyOrNull(mac[4])){
                String firstChar = String.valueOf(mac[4].charAt(0));
                String secondChar = String.valueOf(mac[4].charAt(1));
                mCodeNumberNine.setText(firstChar);
                mCodeNumberTen.setText(secondChar);
            }
            if (!StringUtility.isEmptyOrNull(mac[5])){
                String firstChar = String.valueOf(mac[5].charAt(0));
                String secondChar = String.valueOf(mac[5].charAt(1));
                isComingFromBraceletList = true;
                mCodeNumberEleven.setText(firstChar);
                mCodeNumberTwelve.setText(secondChar);
                if (showConfirmationDialog) {
                    showConfirmationDialog();
                }
            }
        }
    }


    /**
     *
     */
    public void onSuccessMacAddressSent(String connectedTime){
        if (confirmationDialog != null){
            confirmationDialog.dismiss();
            confirmationDialog.hide();
        }
        if (alertBottomDialog != null){
            alertBottomDialog.dismiss();
            alertBottomDialog.hide();
        }

        String macStored = PreferenceUtility.getInstance(getBaseActivity()).getString(KeyConstants.KEY_MAC_ADRESS, "");

        if (!StringUtility.isEmptyOrNull(mMacStored)){
            startSuccessDialogSchedule();
        }


        showSuccessDialog(connectedTime);
        isDismissedByClickButton = false;
    }

    /**
     *
     */
    public void onFailedMacAddressSent() {
        displayMacFieldsInFailedColor();
        showErrorDialog();
        isDismissedByClickButton = false;
    }


    /**
     *
     */
    private void initMacAddressFields() {
        mCodeNumberOne.requestFocus();
        mCodeNumberOne.setText("");
        mCodeNumberTwo.setText("");
        mCodeNumberThree.setText("");
        mCodeNumberFour.setText("");
        mCodeNumberFive.setText("");
        mCodeNumberSix.setText("");
        mCodeNumberSeven.setText("");
        mCodeNumberEight.setText("");
        mCodeNumberNine.setText("");
        mCodeNumberTen.setText("");
        mCodeNumberEleven.setText("");
        mCodeNumberTwelve.setText("");

        mCodeNumberOne.setHint("-");
        mCodeNumberTwo.setHint("-");
        mCodeNumberThree.setHint("-");
        mCodeNumberFour.setHint("-");
        mCodeNumberFive.setHint("-");
        mCodeNumberSix.setHint("-");
        mCodeNumberSeven.setHint("-");
        mCodeNumberEight.setHint("-");
        mCodeNumberNine.setHint("-");
        mCodeNumberTen.setHint("-");
        mCodeNumberEleven.setHint("-");
        mCodeNumberTwelve.setHint("-");
    }

    /**
     *
     */
    private void onTextChangesFields() {
        mCodeNumberOne.addTextChangedListener(mTextWatcher);
        mCodeNumberTwo.addTextChangedListener(mTextWatcher);
        mCodeNumberThree.addTextChangedListener(mTextWatcher);
        mCodeNumberFour.addTextChangedListener(mTextWatcher);
        mCodeNumberFive.addTextChangedListener(mTextWatcher);
        mCodeNumberSix.addTextChangedListener(mTextWatcher);
        mCodeNumberSeven.addTextChangedListener(mTextWatcher);
        mCodeNumberEight.addTextChangedListener(mTextWatcher);
        mCodeNumberNine.addTextChangedListener(mTextWatcher);
        mCodeNumberTen.addTextChangedListener(mTextWatcher);
        mCodeNumberEleven.addTextChangedListener(mTextWatcher);
        mCodeNumberTwelve.addTextChangedListener(mTextWatcher);
    }


    /**
     * @return
     */
    private boolean isInputsValidated() {
        if (StringUtility.isEmptyOrNull(mCodeNumberOne.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberTwo.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberThree.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberFour.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberFive.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberSix.getText().toString())) {
            return false;
        }
        if (StringUtility.isEmptyOrNull(mCodeNumberSeven.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberEight.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberNine.getText().toString())) {
            return false;
        }

        if (StringUtility.isEmptyOrNull(mCodeNumberTen.getText().toString())) {
            return false;
        }
        if (StringUtility.isEmptyOrNull(mCodeNumberEleven.getText().toString())) {
            return false;
        }
        if (StringUtility.isEmptyOrNull(mCodeNumberTwelve.getText().toString())) {
            return false;
        }

        return true;
    }
    /**
     *
     */
    private void displayMacFieldsInFailedColor() {
        mCodeNumberOne.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberTwo.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberThree.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberFour.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberFive.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberSix.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberSeven.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberEight.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberNine.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberTen.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberEleven.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
        mCodeNumberTwelve.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressFailedColor));
    }

    /**
     *
     */
    private void displayMacFieldsInNormalColor() {
        mCodeNumberOne.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberTwo.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberThree.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberFour.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberFive.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberSix.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberSeven.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberEight.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberNine.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberTen.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberEleven.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
        mCodeNumberTwelve.setTextColor(ContextCompat.getColor(mContext, R.color.macAddressNormalColor));
    }

    public void showProgress() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(VISIBLE);
    }

    public void hideProgress() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(GONE);
    }

    private void navigateToBeaconActivity() {
        Intent intent = new Intent(getBaseActivity(), BeaconListActivity.class);
        getBaseActivity().startActivityForResult(intent, AppConstants.ADD_BRACELET_REQUEST_CODE);
    }

    public boolean isPlusBtnClicked() {
        return isPlusBtnClicked;
    }

    public void setPlusBtnClicked(boolean plusBtnClicked) {
        isPlusBtnClicked = plusBtnClicked;
    }
}
