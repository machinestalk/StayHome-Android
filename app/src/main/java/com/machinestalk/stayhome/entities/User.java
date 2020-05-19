package com.machinestalk.stayhome.entities;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.entities.BaseEntity;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.stayhome.helpers.JwtHelper;
import com.machinestalk.stayhome.utils.DateUtil;

public class User extends BaseEntity {

    private static final String KEY_ACCESS_TOKEN = "token";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_DEVICE_TOKEN = "deviceToken";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_SUB = "sub";
    private static final String KEY_CUSTOMER_ID = "customerId";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IS_A_NEW_DEVICE = "isANewDevice";
    private static final String KEY_TENANT_ID = "tenantId";
    private static final String KEY_SIGNUP_DATE = "iat";
    private String id;
    private String accessToken;
    private String refreshToken;
    private String deviceToken;
    private String deviceId;
    private String tenantId;
    private String dateSignup;
    private String phoneNumber;
    private String sub;
    private JsonObject decodedToken;
    private String customerId;
    private boolean isANewDevice;

    @Override
    public void loadJson(JsonElement jsonElement) {

        if (JsonUtility.isJsonElementNull(jsonElement)) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has(KEY_ACCESS_TOKEN)) {
            accessToken = JsonUtility.getString(jsonObject, KEY_ACCESS_TOKEN);
            refreshToken = JsonUtility.getString(jsonObject, KEY_REFRESH_TOKEN);
            deviceToken = JsonUtility.getString(jsonObject, KEY_DEVICE_TOKEN);
            deviceId = JsonUtility.getString(jsonObject, KEY_DEVICE_ID);
            isANewDevice = JsonUtility.getBoolean(jsonObject, KEY_IS_A_NEW_DEVICE);
            try {
                JwtHelper tokenHelper = new JwtHelper(accessToken);
                decodedToken = tokenHelper.getJson().getAsJsonObject();
            }catch (Exception e) {
                Log.e(User.class.getSimpleName(), "error when decode access token");
            }
        }
        id = JsonUtility.getString(decodedToken, KEY_USER_ID);
        sub = JsonUtility.getString(decodedToken, KEY_SUB);
        customerId = JsonUtility.getString(decodedToken, KEY_CUSTOMER_ID);
        tenantId = JsonUtility.getString(decodedToken, KEY_TENANT_ID);
        dateSignup = DateUtil.getCurrentDateFromTimeStamp( JsonUtility.getLong(decodedToken, KEY_SIGNUP_DATE)).replace("-","/");

    }


    /**
     * load json saved in preferences.
     *
     * @param jsonElement
     */
    public void loadLocalJson(JsonElement jsonElement) {

        if (JsonUtility.isJsonElementNull(jsonElement)) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        id = JsonUtility.getString(jsonObject, KEY_USER_ID);
        accessToken = JsonUtility.getString(jsonObject, KEY_ACCESS_TOKEN);
        refreshToken = JsonUtility.getString(jsonObject, KEY_REFRESH_TOKEN);
        deviceId = JsonUtility.getString(jsonObject, KEY_DEVICE_ID);
        deviceToken = JsonUtility.getString(jsonObject, KEY_DEVICE_TOKEN);
        isANewDevice = JsonUtility.getBoolean(jsonObject, KEY_IS_A_NEW_DEVICE);
        sub = JsonUtility.getString(jsonObject, KEY_SUB);
        customerId = JsonUtility.getString(jsonObject, KEY_CUSTOMER_ID);
        dateSignup = JsonUtility.getString(jsonObject, KEY_SIGNUP_DATE);
        tenantId = JsonUtility.getString(jsonObject, KEY_TENANT_ID);

    }

    /**
     * save data as json type in preferences
     *
     * @return
     */
    public String getJson() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(KEY_USER_ID, id);
        jsonObject.addProperty(KEY_ACCESS_TOKEN, accessToken);
        jsonObject.addProperty(KEY_REFRESH_TOKEN, refreshToken);
        jsonObject.addProperty(KEY_DEVICE_TOKEN, deviceToken);
        jsonObject.addProperty(KEY_DEVICE_ID, deviceId);
        jsonObject.addProperty(KEY_SIGNUP_DATE, dateSignup);
        jsonObject.addProperty(KEY_TENANT_ID, tenantId);
        jsonObject.addProperty(KEY_IS_A_NEW_DEVICE, isANewDevice);
        jsonObject.addProperty(KEY_SUB, sub);
        jsonObject.addProperty(KEY_CUSTOMER_ID, customerId);

        return jsonObject.toString();
    }

    public String getAccessToken() {

        return accessToken;
    }

    public String getId() {

        return id;
    }


    public void setANewDevice(boolean ANewDevice) {
        isANewDevice = ANewDevice;
    }

    public boolean isANewDevice() {
        return isANewDevice;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDecodedToken(JsonObject decodedToken) {
        this.decodedToken = decodedToken;
    }

    public JsonObject getDecodedToken() {
        return decodedToken;
    }

    public String getRefreshToken() {

        return refreshToken;
    }

    public String getPhoneNumber() {
        return sub;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setAccessToken(String accessTokenValue) {
        accessToken = accessTokenValue;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDateSignup() {
        return dateSignup;
    }

    public void setDateSignup(String dateSignup) {
        this.dateSignup = dateSignup;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", sub=" + sub +
                '}';
    }
}
