package com.machinestalk.stayhome.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class GpsUtils {

    private Context context;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;
    public GpsUtils(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mSettingsClient = LocationServices.getSettingsClient(context);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************
    }

    // method for turn on GPS
    public void turnGPSOn(final onGpsListener onGpsListener) {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (onGpsListener != null) {
                onGpsListener.gpsStatus(true);
            }
        } else {
            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                            //  GPS is already enable, callback GPS status through listener
                            if (onGpsListener != null) {
                                onGpsListener.gpsStatus(true);
                            }
                        }
                    })
                    .addOnFailureListener((Activity) context, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) context, GPS_REQUEST);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);

                                    Toast.makeText((Activity) context, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public interface onGpsListener {
        void gpsStatus(boolean isGPSEnable);
    }

    private void askLocationPermission (Context context){
        android.location.LocationManager lm = (android.location.LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {

            final AlertBottomDialog locationDialog = new AlertBottomDialog(context);
            locationDialog.setOnClickAlertListener(new OnClickAlertListener() {
                @Override
                public void onAlertClick() {
                    locationDialog.dismiss();
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);

                }
            });

            locationDialog.setCancelable(false);
            locationDialog.create();
            locationDialog.setIvAvatar(R.drawable.red_wifi);
            locationDialog.setTextButton("OK");
            locationDialog.setTvTitle("Location Services Not Active");
            locationDialog.setTvSubTitle("Please enable Location Services and GPS");
            locationDialog.setTextButtonVisibility(true);
            locationDialog.setCanceledOnTouchOutside(true);
            if (context != null  && !locationDialog.isShowing())
                locationDialog.show();

        }

    }

}
