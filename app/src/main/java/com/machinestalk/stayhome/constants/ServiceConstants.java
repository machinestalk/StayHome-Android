package com.machinestalk.stayhome.constants;

/**
 * Created on 12/19/2016.
 */

public class ServiceConstants {

    public static final String GET_ADDRESS_BY_LOCATION = "https://maps.googleapis.com/maps/api/geocode/json";
    public static final String GET_NEARBY_PLACES = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?rankby=distance";
    public static final String GET_GOOGLE_DIRECTION = "https://maps.googleapis.com/maps/api/directions/json?sensor=false&alternative=false&key=AIzaSyDp2OBzMsdJGj1HBqR5j03DpwpAiQM-Tkk";

    private static final String PREFIX = "/api/v1/";

    public static final String POST_URL_LOGIN = "/api/noauth/signInCustomer";
    public static final String POST_REFRESH_TOKEN = "/api/auth/token";
    public static final String POST_URL_LOGOUT = "/api/auth/logout";

    public static final String POST_CONTACT_US = "/api/noauth/contact-us";
    public static final String POST_SIGN_IN_VERIFY_OTP = "/api/noauth/loginMobile";

    public static final String POST_UPDATE_DEVICE_TOKEN = "/api/plugins/telemetry/DEVICE/{deviceToken}/attributes/SERVER_SCOPE";
    public static final String POST_GET_CONFIGURATION = "/api/plugins/telemetry/TENANT/{deviceToken}/values/attributes/SERVER_SCOPE";
    public static final String POST_GET_USER_INF = "/api/plugins/telemetry/CUSTOMER/{customerID}/values/timeseries?limit=1";
    public static final String POST_SEND_USER_LOCATION = "/api/plugins/telemetry/CUSTOMER/{customerId}/timeseries/LATEST_TELEMETRY";
    public static final String POST_SEND_USER_STATUS = "/api/plugins/telemetry/CUSTOMER/{customerId}/timeseries/LATEST_TELEMETRY";
    public static final String POST_SEND_COMPLIANT_STATUS = "/api/v1/{deviceToken}/telemetry";
    public static final String GET_CHECK_BRACELET = "/api/customer-bracelet/device";
    public static final String GET_LAST_VERSION = "/api/plugins/telemetry/TENANT/{tenantId}/values/attributes/SERVER_SCOPE";
    public static final String POST_SEND_CURRENT_VERSION = "/api/plugins/telemetry/CUSTOMER/{customerId}/timeseries/LATEST_TELEMETRY";
    public static final String GET_LAST_DAY = "api/plugins/telemetry/CUSTOMER/{customerId}/values/timeseries?limit=1";
    public static final String GET_BRACELET_LIST = "/api/customer/{customerId}/devices?limit=100&type=Bracelet";
    public static final String PATH_LATLNG = "latlng";
    public static final String PATH_LANGUAGE = "language";

}
