package com.machinestalk.stayhome.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.entities.TrackingInfoEntity;

import static android.location.GpsStatus.GPS_EVENT_SATELLITE_STATUS;

/**
 * Created on 1/31/2017.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener, GpsStatus.Listener {

    GoogleApiClient mGoogleApiClient;
    static Context context;
    LatLng latLng;
    ICurrentLocation currentLocation;
    LocationRequest mLocationRequest;
    static LatLng lastKnownLocation;
    PendingIntent mGeofencePendingIntent;
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 100;
    int priority;


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public LocationManager(Context context, ICurrentLocation currentLocation) {
        this.context = context;
        this.currentLocation = currentLocation;
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    }

    public static LocationManager make(Context context, ICurrentLocation currentLocation) {
        return new LocationManager(context, currentLocation);
    }

    public LatLng getLastKnownLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LocationManager fetch() {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        return this;

    }

    public LocationManager setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public static boolean hasLocationPermission(Context context) {
        try {
            return !(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            ;

            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            lastKnownLocation = latLng;
            currentLocation.onLocationReceived(mLastLocation);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(priority);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);



    }



    public static LatLng getLastKnownLocation() {
        return lastKnownLocation;
    }

    public static void setLastKnownLocation(LatLng lastKnownLocation) {
        LocationManager.lastKnownLocation = lastKnownLocation;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(context, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        lastKnownLocation = latLng;
        currentLocation.onLocationReceived(location);
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (event == GPS_EVENT_SATELLITE_STATUS) {
        } else if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
        } else if (event == GpsStatus.GPS_EVENT_STARTED) {
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
        }


    }


    public interface ICurrentLocation {

        void onLocationReceived(Location address);
    }

    public void stopLocationManager() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Exception e) {
                ;
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }




    public static void saveTrackingInfoEntity( double centerLatitude, double centerLongitude,  double currentLatitude, double currentLongitude) {

        TrackingInfoEntity trackingInfoEntity = new TrackingInfoEntity();
        trackingInfoEntity.setId(1);
        trackingInfoEntity.setCenterLatitude(centerLatitude);
        trackingInfoEntity.setCenterLongitude(centerLongitude);
        trackingInfoEntity.setCurrentLatitude(currentLatitude);
        trackingInfoEntity.setCurrentLongitude(currentLongitude);
        AppDatabase.getInstance(context).getTrackingInfoDao().insertTrackingInfoEntity(trackingInfoEntity);
    }


}
