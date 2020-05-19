package com.machinestalk.stayhome.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.machinestalk.android.entities.BaseEntity;

/**
 * Created on 1/3/2017.
 */

@Entity(tableName = "TrackingInfoEntity")
public class TrackingInfoEntity extends BaseEntity implements Parcelable {

    @PrimaryKey (autoGenerate = false)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "centerLatitude")
    double centerLatitude;
    @ColumnInfo(name = "centerLongitude")
    double centerLongitude;
    @ColumnInfo(name = "currentLatitude")
    double currentLatitude;
    @ColumnInfo(name = "currentLongitude")
    double currentLongitude;

    @ColumnInfo(name = "speed")
    float speed;
    @ColumnInfo(name = "accuracy")
    float accuracy;
    @ColumnInfo(name = "bearing")
    float bearing;
    @ColumnInfo(name = "provider")
    String provider;

    @ColumnInfo(name = "time")
    long time;

    @Override
    public void loadJson(JsonElement jsonElement) {
//        if (JsonUtility.isJsonElementNull(jsonElement)) {
//            return;
//        }
//
//        JsonObject rootObject = jsonElement.getAsJsonObject();


    }


    public TrackingInfoEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.centerLatitude);
        dest.writeDouble(this.centerLongitude);
        dest.writeDouble(this.currentLatitude);
        dest.writeDouble(this.currentLongitude);
    }

    protected TrackingInfoEntity(Parcel in) {
        this.centerLatitude = in.readDouble();
        this.centerLongitude = in.readDouble();
        this.centerLatitude = in.readDouble();
        this.centerLongitude = in.readDouble();
    }

    public static final Creator<TrackingInfoEntity> CREATOR = new Creator<TrackingInfoEntity>() {
        @Override
        public TrackingInfoEntity createFromParcel(Parcel source) {
            return new TrackingInfoEntity(source);
        }

        @Override
        public TrackingInfoEntity[] newArray(int size) {
            return new TrackingInfoEntity[size];
        }
    };

    public double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public static Creator<TrackingInfoEntity> getCREATOR() {
        return CREATOR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getcenterPosition (){

        return new LatLng(centerLatitude, centerLongitude);
    }

    public LatLng getCurrentPosition (){
        return  new LatLng(currentLatitude, currentLongitude);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TrackingInfoEntity{" +
                "id=" + id +
                ", centerLatitude=" + centerLatitude +
                ", centerLongitude=" + centerLongitude +
                ", currentLatitude=" + currentLatitude +
                ", currentLongitude=" + currentLongitude +
                ", speed=" + speed +
                ", accuracy=" + accuracy +
                ", bearing=" + bearing +
                ", provider='" + provider + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
