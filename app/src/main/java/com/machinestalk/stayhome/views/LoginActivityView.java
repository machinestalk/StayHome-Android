package com.machinestalk.stayhome.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.hbb20.CountryCodePicker;
import com.machinestalk.android.components.Button;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.ValidationUtility;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.LoginActivity;
import com.machinestalk.stayhome.components.EditText;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.utils.RxPermissionUtil;
import com.machinestalk.stayhome.utils.Util;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created on 12/19/2016.
 */

public class LoginActivityView extends BaseView {


    private Button mLoginButton;
    private EditText txtUsername;
    private View progress;
    private LinearLayout mLoginContainer, user_name_container;
    private LinearLayout mSendPinContainer;

    private EditText mSendOtpEditText;
    private EditText codeNumberOne, codeNumberTwo, codeNumberThree, codeNumberFour, codeNumberFive, codeNumberSix;
    private Button mSendOtpButton;
    private String mSecurityToken = "";
    private TextView mResendOtpButton;
    private CountryCodePicker mCountryCode;
    private LinearLayout mLanguageLinearLayout;

    public LoginActivityView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.view_login_activity;
    }

    @Override
    protected void onCreate() {
        init();
    }


    private void init() {
        mLoginButton = findViewById(R.id.btnLogin);
        txtUsername = findViewById(R.id.txtUsername);
        progress = findViewById(R.id.progress);

        mLoginContainer = findViewById(R.id.view_sign_in_container);
        user_name_container = findViewById(R.id.user_name_container);
        mSendPinContainer = findViewById(R.id.view_send_pin_container);

        mSendOtpButton = findViewById(R.id.view_login_send_otp);
        mSendOtpEditText = findViewById(R.id.view_login_verify_pin_edit_text);
        mResendOtpButton = findViewById(R.id.view_login_resend_pin);
        mCountryCode = findViewById(R.id.view_sign_in_ccp);
        mLanguageLinearLayout = findViewById(R.id.view_login_language_selector_container);

        /******************************************OTP Edit Text ****************************************************/

        codeNumberOne = findViewById(R.id.view_otp_code_one);
        codeNumberTwo = findViewById(R.id.view_otp_code_two);
        codeNumberThree = findViewById(R.id.view_otp_code_three);
        codeNumberFour = findViewById(R.id.view_otp_code_four);
        codeNumberFive = findViewById(R.id.view_otp_code_five);
        codeNumberSix = findViewById(R.id.view_otp_code_six);

        mResendOtpButton.setPaintFlags(mResendOtpButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtUsername.setText("");


    }

    public void ShowKeybordFocusedWhenOTPScreenLoaded() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                codeNumberOne.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getBaseActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(codeNumberOne, InputMethodManager.SHOW_IMPLICIT);
                codeNumberOne.setSelection(codeNumberOne.getText().length());
            }
        }, 0);
    }


    /**
     *
     */
    @Override
    protected void setActionListeners() {

        mLanguageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.getInstance().getLanguage().equals(AppConstants.LANGUAGE_LITERAL_ENGLISH)) {
                    Util.showConfirmationLanguageDialog(getBaseActivity(), AppConstants.LANGUAGE_LITERAL_ARABIC);
                } else {
                    Util.showConfirmationLanguageDialog(getBaseActivity(), AppConstants.LANGUAGE_LITERAL_ENGLISH);
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestPermission();
            }
        });


        mSendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateSendOtpInputs();
            }
        });

        mResendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateInputs();

            }
        });


        codeNumberOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    codeNumberTwo.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        codeNumberTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    codeNumberThree.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    codeNumberOne.requestFocus();
                }
            }
        });
        codeNumberThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals(""))
                    codeNumberFour.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    codeNumberTwo.requestFocus();
                }
            }
        });

        codeNumberFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals(""))
                    codeNumberFive.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    codeNumberThree.requestFocus();
                }
            }
        });

        codeNumberFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                codeNumberSix.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    codeNumberFour.requestFocus();
                }
            }
        });
        codeNumberSix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    validateSendOtpInputs();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    codeNumberFive.requestFocus();
                }
            }
        });

        codeNumberTwo.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                int i = 0;

                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && codeNumberTwo.getText().toString().isEmpty()) {
                    codeNumberOne.requestFocus();
                    return true;
                }
                return false;
            }
        });
        codeNumberThree.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                int i = 0;

                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && codeNumberThree.getText().toString().isEmpty()) {
                    codeNumberTwo.requestFocus();
                    return true;
                }
                return false;
            }
        });
        codeNumberFour.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                int i = 0;

                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && codeNumberFour.getText().toString().isEmpty()) {
                    codeNumberThree.requestFocus();
                    return true;
                }
                return false;
            }
        });
        codeNumberFive.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && codeNumberFive.getText().toString().isEmpty()) {
                    codeNumberFour.requestFocus();
                    return true;
                }
                return false;
            }
        });
        codeNumberSix.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && codeNumberSix.getText().toString().isEmpty()) {
                    codeNumberFive.requestFocus();
                    return true;
                }
                return false;
            }
        });


    }

    /**
     * @return
     */
    public boolean isPinContainerVisible() {
        return mSendPinContainer.isShown();
    }



    public void returnToUsernameContainer() {
        mLoginContainer.setVisibility(View.VISIBLE);
        mSendPinContainer.setVisibility(View.GONE);
        user_name_container.setVisibility(View.VISIBLE);
    }


    private void validateInputs() {
        String username = txtUsername.getText().toString();
        if (TextUtils.isEmpty(username)) {
            showToast(getString(R.string.SgIn_SgIn_lbl_empty_phone_number));
        } else if (ValidationUtility.isLessThanPhoneNumber(username)) {
            showToast(getString(R.string.SgIn_SgIn_lbl_invalid_lenght_phone_number));
        } else if (ValidationUtility.isValidPhoneNumber(username)) {
            if (mCountryCode.getSelectedCountryCode().contains("966")) {
                username = ValidationUtility.replaceArabicNumbers(username);
                ((LoginActivity) controller).signIn(mCountryCode.getDefaultCountryCodeWithPlus().replace("+","00"), username);
            } else {
                showToast(getString(R.string.Gen_Gen_lbl_error_country_code));
            }
        } else {
            showToast("Phonenumber not valid");
        }

    }


    /**
     *
     */
    private void validateSendOtpInputs() {
        String otp;
        String codeOne = ValidationUtility.replaceArabicNumbers(codeNumberOne.getText().toString());
        String codeTwo = ValidationUtility.replaceArabicNumbers(codeNumberTwo.getText().toString());
        String codeThree = ValidationUtility.replaceArabicNumbers(codeNumberThree.getText().toString());
        String codeFour = ValidationUtility.replaceArabicNumbers(codeNumberFour.getText().toString());
        String codeFive = ValidationUtility.replaceArabicNumbers(codeNumberFive.getText().toString());
        String codeSix = ValidationUtility.replaceArabicNumbers(codeNumberSix.getText().toString());

        otp =  codeOne + codeTwo + codeThree + codeFour + codeFive + codeSix;

        if (TextUtils.isEmpty(otp)) {
            showToast("empty OTP");
            return;
        }

        if (mCountryCode.getDefaultCountryCode().toString().contains("966")) {

            ((LoginActivity) controller).onSignOTP(otp);

        } else {
            showToast("Wrong country code");
        }

    }

    public void phoneNumberRequestfocus() {
        txtUsername.requestFocus();
    }

    public void showOtpScreen(final String securityToken) {
        ShowKeybordFocusedWhenOTPScreenLoaded();
        codeNumberOne.setText("");
        codeNumberTwo.setText("");
        codeNumberThree.setText("");
        codeNumberFour.setText("");
        codeNumberFive.setText("");
        codeNumberSix.setText("");

        codeNumberOne.setHint("-");
        codeNumberTwo.setHint("-");
        codeNumberThree.setHint("-");
        codeNumberFour.setHint("-");
        codeNumberFive.setHint("-");
        codeNumberSix.setHint("-");


        mLoginContainer.setVisibility(View.GONE);
        mSendPinContainer.setVisibility(View.VISIBLE);
        mSendOtpEditText.requestFocus();
        Util.showKeyboard(getBaseActivity());
        mSecurityToken = securityToken;

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void showProgress() {
        hideKeyboard(getBaseActivity());

        if (progress == null)
            return;

        progress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        if (progress == null)
            return;

        progress.setVisibility(View.GONE);
    }

    @NonNull
    private String getUserNumberWithoutPrefix() {

        try {
            return txtUsername.getText().toString();
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     *
     */
    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(getBaseActivity());
        RxPermissionUtil.getInstance().checkRxPermission(rxPermissions, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                new RxPermissionUtil.onPermissionListener() {
                    @Override
                    public void onPermissionAllowed() {
                        validateInputs();
                    }

                    @Override
                    public void onPermissionDenied() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!getBaseActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                            }
                        }
                    }
                });
    }

}
