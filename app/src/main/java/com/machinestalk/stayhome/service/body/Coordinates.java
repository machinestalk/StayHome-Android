package com.machinestalk.stayhome.service.body;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AhmedElyakoubiMachin on 19/12/2017.
 */

public class Coordinates {
    private double Latitude;
    private double Longitude;

    public Coordinates(LatLng latLng) {
        Latitude = latLng.latitude;
        Longitude = latLng.longitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                '}';
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
