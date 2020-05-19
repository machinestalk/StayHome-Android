package com.machinestalk.stayhome.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.service.ServiceCall;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.responses.LocationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Created on 12/28/2016.
 */
public class LocationUtils {

    public static void openGoogleMapIntent( Controller controller, LatLng start, LatLng end ) {

        String uri    = "http://maps.google.com/maps?f=d&hl=en&saddr=" + start.latitude + "," + start.longitude + "&daddr=" + end.latitude + "," + end.longitude;
        Intent intent = new Intent( android.content.Intent.ACTION_VIEW, Uri.parse( uri ) );
        controller.getBaseActivity().startActivity( Intent.createChooser( intent, "Select an application" ) );
    }

    public static void fetchAddress( final Controller controller, final LatLng location, final IGeoCodingListener listener ) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                ConnectedCar.getInstance().getServiceFactory().getUtilService()
                        .getAddress( location.latitude + "," + location.longitude, AppConfig.getInstance().getLanguage()
                                , AppConstants.GOOGLE_MAP_API_KEY)
                        .enableRetry( false )
                        .enqueue( new ServiceCallback( controller, null ) {
                            @Override
                            protected void onSuccess( Object response, int code ) {
                                if ( !TextUtils.isEmpty( ( (LocationResponse) response ).getAddress() ) ) {
                                    listener.onAddressReceived( ( ( LocationResponse ) response ).getAddress() );
                                } else {
                                    String address = getAddressFromLocation(location.latitude, location.longitude, controller.getBaseActivity());
                                    if (!TextUtils.isEmpty(address)) {
                                        listener.onAddressReceived(address);
                                    }else {
                                        listener.onAddressFailed();
                                    }
                                }
                            }

                            @Override
                            protected void onFailure( String errorMessage, int code ) {
                                String address = getAddressFromLocation(location.latitude, location.longitude, controller.getBaseActivity());
                                if (!TextUtils.isEmpty(address)) {
                                    listener.onAddressReceived(address);
                                }else {
                                    listener.onAddressFailed();
                                }
                            }

                            @Override
                            protected void onInternetConnectionError(ServiceCall serviceCall) {
                                super.onInternetConnectionError(serviceCall);
                                listener.onInternetConnectionFailed();
                            }
                        } );
            }
        }).start();



    }

    /**
     * get address from location using geocoder.
     * @param latitude
     * @param longitude
     * @param context
     * @return
     */
    public static String getAddressFromLocation(final double latitude, final double longitude, final Context context) {
        Geocoder geocoder = new Geocoder(context);
        String addressText = "";
        try {
            List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);

            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                List<String> listElementsAddress = new ArrayList<String>();

                String addressLine = address.getMaxAddressLineIndex() >= 0 ? address.getAddressLine(0) : "";
                if (!StringUtility.isEmptyOrNull(addressLine)) {
                    listElementsAddress.add(addressLine);
                }
                String locality = address.getLocality();
                if (!StringUtility.isEmptyOrNull(locality)) {
                    listElementsAddress.add(locality);
                }

                String countryName = address.getCountryName();
                if (!StringUtility.isEmptyOrNull(countryName)) {
                    listElementsAddress.add(countryName);
                }

                addressText = TextUtils.join("," + " ", listElementsAddress);

            }
        } catch (IOException e) {
            Log.e(LocationUtils.class.getName(), "Impossible to connect to Geocoder", e);
        }
        return addressText;
    }

    public static String getFormattedAddress( Address address ) {

        StringBuilder formattedAddress = new StringBuilder();
        for ( int i = 0 ; i < address.getMaxAddressLineIndex() ; i++ ) {
            formattedAddress.append( address.getAddressLine( i ) );
        }

        return formattedAddress.toString();
    }

    public static LatLngBounds getBoundsFromRadius( LatLng center, double radius ) {

        LatLng southwest = SphericalUtil.computeOffset( center, radius * Math.sqrt( 2.0 ), 225 );
        LatLng northeast = SphericalUtil.computeOffset( center, radius * Math.sqrt( 2.0 ), 45 );
        return new LatLngBounds( southwest, northeast );
    }

    public static float getDistanceBetween( LatLng from, LatLng to ) {

        float[] distanceResult = new float[1];
        Location.distanceBetween( from.latitude, from.longitude, to.latitude, to.longitude, distanceResult );
        return distanceResult[0];
    }

    public static boolean isLocationValid( LatLng latLng ) {

        return latLng.latitude != 0 && latLng.longitude != 0;
    }


    public static LatLng pointLatLng( double latitude, double longitude, double distanceInMetres, double bearing ) {
        double brngRad             = toRadians( bearing );
        double latRad              = toRadians( latitude );
        double lonRad              = toRadians( longitude );
        int    earthRadiusInMetres = 6371000;
        double distFrac            = distanceInMetres / earthRadiusInMetres;

        double latitudeResult  = asin( sin( latRad ) * cos( distFrac ) + cos( latRad ) * sin( distFrac ) * cos( brngRad ) );
        double a               = atan2( sin( brngRad ) * sin( distFrac ) * cos( latRad ), cos( distFrac ) - sin( latRad ) * sin( latitudeResult ) );
        double longitudeResult = ( lonRad + a + 3 * PI ) % ( 2 * PI ) - PI;

        return new LatLng( toDegrees( latitudeResult ), toDegrees( longitudeResult ) );

    }

    public static void shareLocation( Controller controller, LatLng location ) {

        LatLng currentCoordinates = location;
        //TODO: include if sharing current location!
       /* LocationManager.make(controller, new LocationManager.ICurrentLocation() {
            @Override
            public void onLocationReceived(LatLng address) {
                currentCoordinates = address;
            }
        }).fetch();*/

        double latitude      = currentCoordinates.latitude;
        double longitude     = currentCoordinates.longitude;
        String uri           = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;
        Intent sharingIntent = new Intent( android.content.Intent.ACTION_SEND );
        sharingIntent.setType( "text/plain" );
        sharingIntent.putExtra( android.content.Intent.EXTRA_TEXT, uri );
        controller.getBaseActivity().startActivity( Intent.createChooser( sharingIntent, "Share via" ) );
    }

    public interface IGeoCodingListener {
        void onAddressReceived( String address );

        void onAddressFailed();

        void onInternetConnectionFailed();
    }
}
