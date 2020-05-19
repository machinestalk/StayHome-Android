package com.machinestalk.stayhome.database.converters;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Maher on 04/12/2017.
 */

public class LocationTypeConverter {

    @TypeConverter
    public static LatLng toLatLang(String StringLatLng) {
        if (TextUtils.isEmpty(StringLatLng)){
            return null;
        }
        String [] location = StringLatLng.split(",");
        LatLng latLng = new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
        return latLng;
    }

    @TypeConverter
    public static String toString(LatLng latLng) {
        StringBuilder locationBuilder = new StringBuilder();
        if (latLng != null) {
            String latitude = String.valueOf(latLng.latitude);
            String longitude = String.valueOf(latLng.longitude);

            locationBuilder.append(String.valueOf(latitude)).append(",").append(String.valueOf(longitude));
            String latLngString = locationBuilder.toString();
            return latLngString;
        }
        return null ;
    }
}
