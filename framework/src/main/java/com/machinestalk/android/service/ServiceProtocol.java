package com.machinestalk.android.service;

import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import com.machinestalk.android.R;
import com.machinestalk.android.interfaces.Controller;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * This class is used to define the basic protocol
 * for the Web service that you will use
 * and should be overridden
 */
public abstract class ServiceProtocol implements Interceptor {

    @SuppressWarnings( "WeakerAccess" )
    protected static final int DEFAULT_UNAUTHORIZED_CODE = 401;

    @Override
    public final Response intercept( Interceptor.Chain chain ) throws IOException {

        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder()
                .method( request.method(), request.body() );

        AbstractMap< String, String > headers = getHeaders();

        if ( headers != null ) {
            for ( Map.Entry< String, String > entry : headers.entrySet() ) {
                requestBuilder = requestBuilder.addHeader( entry.getKey(), entry.getValue() );
            }
        }

        /**
         * if request url is for forgot password
         * remove unsupported characters
         */


            request = requestBuilder.build();

        Log.i(getClass().getName(),"request method :"+request.method());
        Log.i(getClass().getName(), "request url :"+request.url().toString());
        Log.i(getClass().getName(), "request headers :"+request.headers().toString());
        Log.i(getClass().getName(), "request body :"+getBodyAsString(request.body()));

        Response response;

        try {
            response = chain.proceed( request );

        } catch ( ProtocolException e ) {
            response = new Response.Builder()
                    .request( request )
                    .code( 204 )
                    .protocol( Protocol.HTTP_1_1 )
                    .build();

        }
        return response;

    }


    private String getBodyAsString(RequestBody body) {
        Buffer buffer = new Buffer();
        try {
            if(body != null){
                body.writeTo(buffer);
            }
        } catch (IOException e) {
        }
        String bodyString = buffer.readUtf8();
        return bodyString ;
    }



    /**
     * Return a {@link HashMap} of all the headers that you need to send
     * with all calls
     *
     * @return A {@link HashMap} of headers
     */

    protected abstract AbstractMap< String, String > getHeaders();

    /**
     * Return a the base API URL
     *
     * @return A {@link HashMap} of headers
     */

    public abstract String getAPIUrl();

    //Parsing information


    /**
     * Override this method to parse errors from response returned by
     * server on call failure
     *
     * @param response
     * @see ServiceCallback#onFailure(String, int)
     */
    @SuppressWarnings( "UnusedParameters" )
    protected String parseError( retrofit2.Response response, int code ) {
        try {
            return response.errorBody().string();
        } catch ( IOException e ) {
            return "";
        }
    }


    /**
     * Override this method to parse status code from response
     *
     * @param response
     * @see ServiceCallback#onFailure(String, int)
     * @see ServiceCallback#onSuccess(Object, int)
     * @see retrofit2.Response
     */
    protected int getStatusCode( retrofit2.Response response ) {
        return response.code();
    }

    /**
     * Override this method to define custom logic for successful response
     *
     * @param response
     * @see ServiceCallback#onFailure(String, int)
     * @see ServiceCallback#onSuccess(Object, int)
     * @see retrofit2.Response
     */
    @SuppressWarnings( "WeakerAccess" )
    protected boolean isSuccessful( retrofit2.Response response ) {
        return response.errorBody() == null;
    }


    /**
     * Override this method to have custom rules for authorization <br/>
     * Default unauthorized code is 401
     *
     * @see ServiceCallback#onFailure(String, int)
     * @see ServiceProtocol#onUnAuthorized(Controller)
     */

    protected boolean isAuthorized( int code ) {
        return code != DEFAULT_UNAUTHORIZED_CODE;
    }


    /**
     * Override this method to perform tasks when client is unauthorized to use service
     *
     * @return true if you have consumed the event and
     * don't want service callbacks to be executed. Default is false
     * @see ServiceProtocol#isAuthorized(int)
     */
    @SuppressWarnings( { "BooleanMethodNameMustStartWithQuestion", "SameReturnValue", "UnusedParameters" } )
    protected boolean onUnAuthorized( Controller controller ) {
        return false;
    }

    /**
     * Override this method to perform tasks when client is unauthorized to use service
     *
     * @return true if you have consumed the event and
     * don't want service callbacks to be executed. Default is false
     * @see ServiceProtocol#isAuthorized(int)
     */
    @SuppressWarnings( { "BooleanMethodNameMustStartWithQuestion", "SameReturnValue", "UnusedParameters" } )
    protected boolean onUnAuthorized( ServiceCall serviceCall, ServiceCallback serviceCallback, Controller controller ) {
        return false;
    }

    /**
     * Override this method to implement custom retry functionality<br/>
     * By default is shows a snackbar
     */

    protected void onRetry( ServiceCall serviceCall, ServiceCallback serviceCallback, Controller controller ) {
        showRetrySnackbar( R.string.error_retry_request, serviceCall, serviceCallback, controller );
    }

    /**
     * Override this method to implement custom functionality on internet connection error<br/>
     */

    protected void onInternetConnectionError( ServiceCall serviceCall, ServiceCallback serviceCallback, Controller controller ) {

    }

    /**
     * Override this method to provide a custom Connection time out in seconds
     *
     * @return time out value in seconds
     */

    @SuppressWarnings( "SameReturnValue" )
    protected int getConnectionTimeoutInSeconds() {
        return 10;
    }

    /**
     * Override this method to provide a custom read time out in seconds
     *
     * @return time out value in seconds
     */

    @SuppressWarnings( "SameReturnValue" )
    protected int getReadTimeoutInSeconds() {
        return 10;
    }

    /**
     * Override this method to provide a custom write time out in seconds
     *
     * @return time out value in seconds
     */

    @SuppressWarnings( "SameReturnValue" )
    protected int getWriteTimeoutInSeconds() {
        return 10;
    }

    @SuppressWarnings( { "FeatureEnvy", "WeakerAccess" } )
    protected final void retry( ServiceCall serviceCall, ServiceCallback callback ) {
        callback.refresh();
        if ( serviceCall.isExecuted() ) {
            serviceCall = serviceCall.clone();
        }
        serviceCall.enableRetry( true ).enqueue( callback );
    }

    protected void showRetrySnackbar( int textResource, final ServiceCall serviceCall, final ServiceCallback callback, Controller controller ) {

        final View view = controller.getView();
        if ( view == null ) {
            return;
        }

        try {
            final Snackbar snackbar = Snackbar.make( view, textResource, Snackbar.LENGTH_INDEFINITE );
            snackbar.setAction( R.string.btn_title_retry, new View.OnClickListener() {
                @Override
                public void onClick( View v ) {

                    retry( serviceCall, callback );
                    snackbar.dismiss();
                }
            } );

            snackbar.show();
            controller.setSnackbar( snackbar );

            view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    snackbar.dismiss();
                    view.setOnClickListener( null );
                }
            } );
        } catch ( Exception ex ) {
        }

    }

    protected boolean isSafeClient() {
        return true;
    }

    protected int getCertResId() {
        return 0;
    }

    protected boolean isOnline() {
        return true;
    }

    protected String getOfflineResponse( String path ) {
        return "";
    }
}
