package com.machinestalk.android.service;


import com.machinestalk.android.R;
import com.machinestalk.android.utilities.DeviceUtility;
import com.machinestalk.android.utilities.StringUtility;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 27/01/2016.
 */
@SuppressWarnings({"FeatureEnvy", "MethodOnlyUsedFromInnerClass"})
final class ServiceCallAdapter<T> implements ServiceCall<T> {

    private final Call<T> call;
    private boolean shouldShowRetry;
    private int maxRetries;
    private int retryCount;

    ServiceCallAdapter(Call<T> call) {
        shouldShowRetry = false;
        this.call = call;
        maxRetries = -1;
        retryCount = 0;
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone", "unchecked", "AccessingNonPublicFieldOfAnotherObject"})
    @Override
    public ServiceCall<T> clone() {
        ServiceCallAdapter callAdapter = new ServiceCallAdapter<>(call.clone());
        callAdapter.maxRetries = maxRetries;
        callAdapter.retryCount = retryCount;
        return callAdapter;
    }

    @Override
    public ServiceCall<T> enableRetry(boolean enable) {
        shouldShowRetry = enable;
        return this;
    }

    @Override
    public ServiceCall<T> maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    @Override
    public boolean isExecuted() {
        return (call != null) && call.isExecuted();
    }

    @Override
    public void enqueue(final ServiceCallback callback) {
        callback.refresh();
        if(!callback.isOnline()) {
            String response = callback.getOfflineResponse(call.request().method());
            callback.success(response, 200);
            return;
        }
        if (callback.getController().getBaseActivity() == null)
            return;
        if(!DeviceUtility.isInternetConnectionAvailable(callback.getController().getBaseActivity())) {
            callback.onInternetConnectionError(this);
            return;
        }
        callback.willStartCall();
        call.enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                ServiceCallAdapter.this.onResponse(callback, call, response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                ServiceCallAdapter.this.onFailure(callback, call, t);
            }
        });
    }

    @SuppressWarnings("UnusedParameters")
    private void onResponse(ServiceCallback callback, Call<T> call, Response<T> response) {
        if(call.isCanceled()) {
            return;
        }
        int code = callback.getStatusCode(response);
        if ( !callback.isAuthorized( code ) ) {
            callback.onUnAuthorized();
            callback.onUnAuthorized( this, callback );
        }

        try {
            if(!callback.areCallbacksEnabled()) {
                return;
            }

            if(!callback.isSuccessful(response)) {
                if (callback.isAuthorized(code)) {
                    failure(callback, callback.getErrorMessage(response, code), code);
                }
                return;
            }
            callback.success(response.body(), code);

        } catch (Exception ex) {
            failure(callback, callback.getDefaultErrorMessage(), callback.getDefaultErrorCode());
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void onFailure(ServiceCallback callback, Call<T> call, Throwable t) {
        if(call.isCanceled()) {
            return;
        }
        String message = t.getMessage();
        if(t instanceof SocketTimeoutException) {
//            message = callback.getController().getBaseActivity().getString(R.string.error_connection_timeout);
            message = callback.getController().getBaseActivity().getString(R.string.error_connection_timeout);
        }

        if(StringUtility.isEmptyOrNull(message)) {
            message = callback.getDefaultErrorMessage();
        }

        failure(callback, message, callback.getDefaultErrorCode());
    }


    private void failure(ServiceCallback callback, String errorMessage, int code) {
        callback.failure(errorMessage, code);
        if(!shouldShowRetry) {
            return;
        }

        retryCount++;
        if((maxRetries > 0) && (retryCount > maxRetries)) {
            return;
        }

        callback.initiateRetry(clone());
    }

}
