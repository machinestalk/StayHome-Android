package com.machinestalk.android.service;

import com.machinestalk.android.R;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.StringUtility;

import retrofit2.Response;

/**
 * This class will be used as a Callback for all
 * the Webservice calls
 */
public abstract class ServiceCallback {

    private final ServiceProtocol serviceProtocol;
    private final Controller controller;
    private final ServiceSecondaryEventHandler serviceSecondaryEventHandler;
    private boolean callbacksEnabled;

    public ServiceCallback(Controller controller) {
        this.controller = controller;
        serviceSecondaryEventHandler = null;
        serviceProtocol = ServiceManager.getInstance().getServiceProtocol();
        callbacksEnabled = true;
    }

    public ServiceCallback(Controller controller, ServiceSecondaryEventHandler serviceSecondaryEventHandler) {

        this.controller = controller;
        this.serviceSecondaryEventHandler = serviceSecondaryEventHandler;
        serviceProtocol = ServiceManager.getInstance().getServiceProtocol();
        callbacksEnabled = true;
    }

    public final void refresh() {
        callbacksEnabled = true;
    }

    /**
     * This method will be called when a successful response
     * is received
     * @param response Cast this response object to the the
     *                 desired {@link com.machinestalk.android.entities.BaseEntity}
     *                 or {@link BaseResponse}
     *                 subclass
     * @param code HTTP Status code
     *
     * @see ServiceCallback#onFailure(String, int)
     */
    protected abstract void onSuccess(Object response, int code);


    /**
     * This method will be called when the call
     * fails
     * @param errorMessage Error Message returned by {@link ServiceProtocol#parseError(Response, int)} )}
     * @param code HTTP Status code
     *
     * @see ServiceCallback#onSuccess(Object, int)
     * @see ServiceProtocol#parseError(Response, int)
     */
    protected abstract void onFailure(String errorMessage, int code);


    final void success(final Object response,final int code) {

        if(!callbacksEnabled) {
            return;
        }

        executeCallOnUIThread(new Runnable() {
            @Override
            public void run() {
                onSuccess(response, code);
                if(serviceSecondaryEventHandler == null) {
                    return;
                }

                serviceSecondaryEventHandler.didFinishCall(true);
            }
        });
    }

    final void failure(final String errorMessage,final int code) {

        if(!callbacksEnabled) {
            return;
        }

        executeCallOnUIThread(new Runnable() {
            @Override
            public void run() {
                onFailure(errorMessage, code);
                if (serviceSecondaryEventHandler == null) {
                    return;
                }

                serviceSecondaryEventHandler.didFinishCall(false);
            }
        });
    }

    final void willStartCall() {

        if(serviceSecondaryEventHandler == null) {
            return;
        }

        executeCallOnUIThread(new Runnable() {
            @Override
            public void run() {
                serviceSecondaryEventHandler.willStartCall();
            }
        });
    }

    @SuppressWarnings("WeakerAccess")
    protected boolean isSuccessful(Response response) {

        if(serviceProtocol != null) {
            return serviceProtocol.isSuccessful(response);
        }

        return response.errorBody() == null;
    }


    @SuppressWarnings("WeakerAccess")
    protected String getErrorMessage(Response response, int code) {
        try {
            if(serviceProtocol != null) {
                String errorMessage = serviceProtocol.parseError(response, code);
                if(!StringUtility.isEmptyOrNull(errorMessage)) {
                    return errorMessage;
                }
            }

        } catch (Exception ignored) {

        }

        return getDefaultErrorMessage();
    }

    @SuppressWarnings("WeakerAccess")
    protected int getStatusCode(Response response) {

        int code = response.code();
        if(serviceProtocol != null) {
            code = serviceProtocol.getStatusCode(response);
        }
        return code;
    }

    final String getDefaultErrorMessage() {
        if(controller == null
                || controller.getBaseActivity() == null) {
            return "";
        }
        return controller.getBaseActivity().getResources().getString(R.string.error_message_default);
    }

    final int getDefaultErrorCode() {
        return -1;
    }

    @SuppressWarnings("WeakerAccess")
    protected boolean isAuthorized(int code) {

        return (serviceProtocol == null) || serviceProtocol.isAuthorized(code);
    }

    @SuppressWarnings("WeakerAccess")
    protected void onUnAuthorized() {

        if(serviceProtocol == null) {
            callbacksEnabled = true;
            return;
        }

        executeCallOnUIThread(new Runnable() {
            @Override
            public void run() {
                callbacksEnabled =  !serviceProtocol.onUnAuthorized(controller);

            }
        });
    }

    protected void onUnAuthorized( final ServiceCall serviceCall, final ServiceCallback serviceCallback){
        if(serviceProtocol == null) {
            callbacksEnabled = true;
            return;
        }

        executeCallOnUIThread(new Runnable() {
            @Override
            public void run() {
                callbacksEnabled =  !serviceProtocol.onUnAuthorized(serviceCall, serviceCallback, controller);
            }
        });
    }

    final void initiateRetry(final ServiceCall serviceCall) {

        executeCallOnUIThread(new Runnable() {
            @Override
            public void run() {
                onRetry(serviceCall);
            }
        });
    }

    @SuppressWarnings("WeakerAccess")
    protected void onRetry(ServiceCall serviceCall) {

        if(serviceProtocol == null) {
            return;
        }

        serviceProtocol.onRetry(serviceCall, this, controller);
    }

    protected final void retry(ServiceCall serviceCall, ServiceCallback serviceCallback) {

        if(serviceProtocol == null) {
            return;
        }

        serviceProtocol.retry(serviceCall, serviceCallback);
    }

    @SuppressWarnings("WeakerAccess")
    protected void onInternetConnectionError(ServiceCall serviceCall) {

        if (serviceProtocol == null) {
            return;
        }

        serviceProtocol.onInternetConnectionError(serviceCall, this, controller);
    }

    private void executeCallOnUIThread(Runnable runnable) {

        if(!callbacksEnabled) {
            return;
        }

        if(controller == null
                || controller.getBaseActivity() == null) {
            return;
        }

        controller.getBaseActivity().runOnUiThread(runnable);
    }

    final boolean areCallbacksEnabled() {
        return callbacksEnabled;
    }

    final Controller getController() {
        return controller;
    }

    boolean isOnline() {
        if (serviceProtocol == null) {
            return true;
        }

        return serviceProtocol.isOnline();
    }

    String getOfflineResponse(String path) {
        if (serviceProtocol == null) {
            return "";
        }

        return serviceProtocol.getOfflineResponse(path);
    }
}
