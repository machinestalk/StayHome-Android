package com.machinestalk.stayhome.service;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.service.ServiceCall;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

/**
 * Created on 08/02/2016.
 */
public class ServiceProtocol extends com.machinestalk.android.service.ServiceProtocol {

    public static final int DURATION_IN_SECONDS = 60;
    private final String KEY_MESSAGE = "message";
    private int count = 0;

    private static ServiceProtocol mInstance;
    private Boolean refreshInProgress = false;

    public static ServiceProtocol getInstance() {
        synchronized (ServiceProtocol.class) {
            if (mInstance == null) {
                mInstance = new ServiceProtocol();
            }
        }

        return mInstance;
    }

    @Override
    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        String token = AppConfig.getInstance().getAccessToken();

        if (!StringUtils.isEmpty(token)) {
            headers.put(KeyConstants.KEY_AUTH_TOKEN, token);
        }
        return headers;
    }

    public void setRefreshInProgress(Boolean refreshInProgress) {
        this.refreshInProgress = refreshInProgress;
    }

    @Override
    public String getAPIUrl() {

        return AppConfig.getInstance().getUrl();
    }

    @Override
    protected boolean onUnAuthorized(ServiceCall serviceCall, ServiceCallback serviceCallback, Controller controller) {
        synchronized (ServiceProtocol.class) {
            if (!refreshInProgress) {
                if (controller.getBaseActivity() != null) {
                    refreshInProgress = true;
                    ((BaseActivity) controller.getBaseActivity()).refreshToken(serviceCall, serviceCallback);
                    return true;
                }
            } else {
                //retry requires new auth token
                ((BaseActivity) controller.getBaseActivity()).addServiceCallBackToMap(serviceCall, serviceCallback);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onInternetConnectionError(ServiceCall serviceCall, ServiceCallback serviceCallback, Controller controller) {

        final View view = controller.getView();
        if (view == null) {
            return;
        }

        try {
            final Snackbar snackbar = Snackbar.make(view, R.string.error_internet_connection, Snackbar.LENGTH_SHORT);

            snackbar.show();
            controller.setSnackbar(snackbar);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    snackbar.dismiss();
                    view.setOnClickListener(null);
                }
            });
        } catch (Exception ex) {
            Logger.i(getClass().getName(), ex.getMessage().toString());
            ;
        }
    }

    @Override
    protected int getConnectionTimeoutInSeconds() {

        return DURATION_IN_SECONDS;
    }

    @Override
    protected int getReadTimeoutInSeconds() {

        return DURATION_IN_SECONDS;
    }

    @Override
    protected int getWriteTimeoutInSeconds() {

        return DURATION_IN_SECONDS;
    }

    @Override
    protected String parseError(Response response, int code) {

        String errorKey = getErrorKey(response);

        if (errorKey.equalsIgnoreCase(KeyConstants.KEY_OTP_EXPIRED)) {

            return KeyConstants.KEY_OTP_EXPIRED;
        }

        if (errorKey.contains(KeyConstants.KEY_SECURITY_TOKEN)) {

            return errorKey;
        }

        return errorKey;
    }

    private String getErrorKey(Response response) {

        String errorKey = "";
        try {
            JsonObject rootObject = JsonUtility.parseToJsonObject(response.errorBody().string());
            errorKey = JsonUtility.getString(rootObject, KeyConstants.KEY_MESSAGE);
            if (StringUtility.isEmptyOrNull(errorKey)) {
                errorKey = JsonUtility.getString(rootObject, KeyConstants.KEY_ERROR_DESC);
            }
            if (StringUtility.isEmptyOrNull(errorKey)) {
                errorKey = JsonUtility.getString(rootObject, KeyConstants.KEY_EXCEPTION_MSG);
            }
        } catch (IOException e) {
            Logger.i(getClass().getName(), e.getMessage().toString());
            ;
        }

        if (!StringUtility.isEmptyOrNull(errorKey) && errorKey.contains(",")) {

            List<String> keysList = Arrays.asList(errorKey.split(","));

            if (errorKey.contains(KeyConstants.KEY_SECURITY_TOKEN)) {

                errorKey = keysList.get(1);
            } else {

                errorKey = keysList.get(0);
            }
        }

        return errorKey;
    }
}
