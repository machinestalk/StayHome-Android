package com.machinestalk.stayhome.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonElement;
import com.machinestalk.android.entities.BaseEntity;
import com.machinestalk.android.utilities.StringUtility;


@Entity(tableName = "bluetoothEntity")
public class BluetoothEntity extends BaseEntity implements Parcelable {

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "frameName")
    private String frameName;
    @ColumnInfo(name = "adresse")
    private String adresse;
    @ColumnInfo(name = "rssi")
    private String rssi;
    @ColumnInfo(name = "txPower")
    private String txPower;
    @ColumnInfo(name = "Namespace")
    private String namespace;
    @ColumnInfo(name = "instanceID")
    private String instanceID;
    @ColumnInfo(name = "minor")
    private String minor;
    @ColumnInfo(name = "major")
    private String major;
    @ColumnInfo(name = "udid")
    private String udid;
    @ColumnInfo(name = "URL")
    private String url;
    @ColumnInfo(name = "X")
    private String x;
    @ColumnInfo(name = "y")
    private String y;
    @ColumnInfo(name = "z")
    private String z;
    @ColumnInfo(name = "rx")
    private String rx;
    @ColumnInfo(name = "tx")
    private String tx;
    @ColumnInfo(name = "battery")
    private String battery;
    @ColumnInfo(name = "temperature")
    private String temperature;

    protected BluetoothEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        frameName = in.readString();
        adresse = in.readString();
        rssi = in.readString();
        txPower = in.readString();
        namespace = in.readString();
        instanceID = in.readString();
        minor = in.readString();
        major = in.readString();
        udid = in.readString();
        url = in.readString();
        x = in.readString();
        y = in.readString();
        z = in.readString();
        rx = in.readString();
        tx = in.readString();
        battery = in.readString();
        temperature = in.readString();

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.frameName);
        dest.writeString(this.adresse);
        dest.writeString(this.rssi);
        dest.writeString(this.txPower);
        dest.writeString(this.namespace);
        dest.writeString(this.instanceID);
        dest.writeString(this.minor);
        dest.writeString(this.major);
        dest.writeString(this.udid);
        dest.writeString(this.url);
        dest.writeString(this.x);
        dest.writeString(this.y);
        dest.writeString(this.z);
        dest.writeString(this.rx);
        dest.writeString(this.tx);
        dest.writeString(this.battery);
        dest.writeString(this.temperature);
    }


    public BluetoothEntity() {
    }

    public static final Creator<BluetoothEntity> CREATOR = new Creator<BluetoothEntity>() {
        @Override
        public BluetoothEntity createFromParcel(Parcel in) {
            return new BluetoothEntity(in);
        }

        @Override
        public BluetoothEntity[] newArray(int size) {
            return new BluetoothEntity[size];
        }
    };

    @Override
    public void loadJson(JsonElement jsonElement) {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.equals("Unnamed") || StringUtility.isEmptyOrNull(name))
            this.name = "Stayhome App" ;
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getFrameName() {
        return frameName;
    }

    public void setFrameName(String frameName) {
        this.frameName = frameName;
    }

    public String getTxPower() {
        return txPower;
    }

    public void setTxPower(String txPower) {
        this.txPower = txPower;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public static Creator<BluetoothEntity> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "BluetoothEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", frameName='" + frameName + '\'' +
                ", adresse='" + adresse + '\'' +
                ", rssi='" + rssi + '\'' +
                ", txPower='" + txPower + '\'' +
                ", namespace='" + namespace + '\'' +
                ", instanceID='" + instanceID + '\'' +
                ", minor='" + minor + '\'' +
                ", major='" + major + '\'' +
                ", udid='" + udid + '\'' +
                ", url='" + url + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", z='" + z + '\'' +
                ", rx='" + rx + '\'' +
                ", tx='" + tx + '\'' +
                ", battery='" + battery + '\'' +
                ", temperature='" + temperature + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
