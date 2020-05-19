package com.machinestalk.stayhome.service.body;

public class CompliantBody {

    private int compliant;
    private int FingerReseted;
    private int systemRebooted;
    private int gpsEnabled;
    private int wifiEnabled;
    private int internetCnxEnabled;
    private int notificationEnabled;
    private int appKilledStatus;
    private int zoneStatus;
    private String Reason;
    private String info;
    private String iBeacon;
    private String UID;
    private String URL;
    private String Acc;
    private String TLM;




    public void setCompliant(int compliant) {
        this.compliant = compliant;
    }

    public void setFingerReseted(int fingerReseted) {
        FingerReseted = fingerReseted;
    }

    public void setSystemRebooted(int systemRebooted) {
        this.systemRebooted = systemRebooted;
    }

    public void setGpsEnabled(int gpsEnabled) {
        this.gpsEnabled = gpsEnabled;
    }

    public void setWifiEnabled(int wifiEnabled) {
        this.wifiEnabled = wifiEnabled;
    }

    public void setInternetCnxEnabled(int internetCnxEnabled) {
        this.internetCnxEnabled = internetCnxEnabled;
    }

    public void setNotificationEnabled(int notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public void setAppKilledStatus(int appKilledStatus) {
        this.appKilledStatus = appKilledStatus;
    }

    public void setReason(String reason) {
        this.Reason = reason;
    }

    public int getCompliant() {
        return compliant;
    }

    public int getFingerReseted() {
        return FingerReseted;
    }

    public int getSystemRebooted() {
        return systemRebooted;
    }

    public int getGpsEnabled() {
        return gpsEnabled;
    }

    public int getWifiEnabled() {
        return wifiEnabled;
    }

    public int getInternetCnxEnabled() {
        return internetCnxEnabled;
    }

    public int getNotificationEnabled() {
        return notificationEnabled;
    }

    public int getAppKilledStatus() {
        return appKilledStatus;
    }

    public String getReason() {
        return Reason;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(String iBeacon) {
        this.iBeacon = iBeacon;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getAcc() {
        return Acc;
    }

    public void setAcc(String acc) {
        Acc = acc;
    }

    public String getTLM() {
        return TLM;
    }

    public void setTLM(String TLM) {
        this.TLM = TLM;
    }

    public int getZoneStatus() {
        return zoneStatus;
    }

    public void setZoneStatus(int zoneStatus) {
        this.zoneStatus = zoneStatus;
    }
}
