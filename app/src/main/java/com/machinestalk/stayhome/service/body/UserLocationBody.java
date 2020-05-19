package com.machinestalk.stayhome.service.body;

public class UserLocationBody {

    private String latitude;
    private String longitude;
    private String radius;


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }
}
