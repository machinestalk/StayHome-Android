package com.machinestalk.stayhome.constants;

import android.os.ParcelUuid;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

/**
 * Created on 12/29/2016.
 */
public class AppConstants {

    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 30 * 1000;

    public static final int CONFIDENCE = 70;

    public static final UUID SERVICE_UUID = UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0");
    public static final ParcelUuid SERVICE_pUUID = new ParcelUuid(SERVICE_UUID);

    public static final UUID APDU_UUID = UUID.fromString("00002315-0000-1000-8000-00805f9b34fb");
    public static final ParcelUuid APDU_pUUID = new ParcelUuid(APDU_UUID);


    public static final LatLng RIYADH_LAT_LNG = new LatLng(23.8859, 45.0792);
    public static final String MOTORNA_URL_PROD = "https://japi2.motorna.sa";
    public static final String MOTORNA_URL_DEV = " http://uat.motorna.sa";
    public static final String MOTORNA_URL_IDENTITY_DEV = "http://37.224.62.114:19403";
    public static final String MOTORNA_URL_IDENTITY_PROD = "https://auth.motorna.sa";

    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 12;


    public static final int MODE_UPDATE = 0;
    public static final int MODE_ADD = 1;
    public static final int MODE_VIEW = 2;
    public static final int MODE_PICK = 3;
    public static final int MODE_ZONE = 6;
    public static final int MODE_DRIVER = 7;
    public static final int PICK_ZONE = 3;
    public static final int PICK_VEHICLE = 4;
    public static final int MY_PERMISSIONS_LOCATION = 4;
    public static final String EMAIL_ACCEPTED_CHARACTER = "[a-zA-Z0-9@.-_ ]+";
    public static final int MY_PERMISSIONS_CAMERA = 5;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;
    public static final int FAVORITE_ZONE_REQUEST_CODE = 6;

    public static final String GOOGLE_MAP_API_KEY = "AIzaSyBJKPD5U_7gmzXSBRmDnk-3SUTIEiR3W5E";
    public static final String IMAGES_FOLDER = "MachineTalkImg";

    public static final int ROLE_HOUSE_HOLDER = 3;
    public static final int ROLE_DRIVER = 4;
    public static final int ROLE_RIDER = 5;
    public static final int FRAGMENT_SUBSCRIPTION = 77;
    public static final String LANGUAGE_LITERAL_ENGLISH = "en";
    public static final String LANGUAGE_LITERAL_ARABIC = "ar";

    public static final int FRAGMENT_DRIVER_RIDE_STATUS = 0;
    public static final int FRAGMENT_ZONE_LIST = 1;
    public static final int FRAGMENT_DEVICE_LIST = 2;
    public static final int FRAGMENT_GUIDANCE = 4;

    public static final String KEY_REFRESH_TOKEN = "refreshToken";

    public static final int FRAGMENT_CALENDAR = 3;
    public static final int FRAGMENT_LIVE_MAP_TRACKING = 5;
    public static final String PATH_DRAWABLE = "drawable";


    public static final int RESULT_CODE_FROM_MAP = 2;
    public static final int RESULT_CODE_FROM_SIDE_MENU_PROFILE = 3;
    public static final String PERMISSIONS_ACTIVITY_NAME = "com.android.packageinstaller.permission.ui.GrantPermissionsActivity";


    public static final String SIGN_UP_SCREEN = "signup";

    public static final String EVENT_UPDATE_DRIVER = "updateDriverUserProfileScreen";
    public static final String EVENT_CHANGE_IS_DEVICE_EXPIRY = "isDeviceExpiry";
    public static final String EVENT_CHANGE_IS_DEVICE_NOT_EXPIRY = "isDeviceNotExpired";
    public static final String EVENT_BLUETOOTH = "EVENT_BLUETOOTH";
    public static final String EVENT_BLUETOOTH_ENABLED = "bluetooth_enabled";
    public static final String EVENT_WIFI_DISABLED = "EVENT_WIFI";
    public static final String EVENT_WIFI_ENABLED = "EVENT_WIFI_enabled";
    public static final String EVENT_LOCATION_DISABLED = "EVENT_LOCATION";
    public static final String EVENT_LOCATION_ENABLED = "EVENT_LOCATION_ENABLED";
    public static final String EVENT_BATTERY_DOWN = "BatteryDown";
    public static final String EVENT_ZONE_OUT = "ZoneOut";
    public static final String EVENT_BRACELET_NOT_DETECTED = "braceletNotDetected";
    public static final String EVENT_BRACELET_DETECTED = "braceletDetected";
    public static final String EVENT_ZONE_IN = "ZoneIn";
    public static final String EVENT_BATTERY_LOW = "Battery";
    public static final String EVENT_BATTERY_FINE = "BatteryNotlow";


    public static final String KEY_COMING_FROM_NOTIFICATION = "isComingFromNotification";
    public static final String KEY_NOTIFICATION = "Notification";
    public static final String KEY_EXTRA_NOTIFICATION_DATA = "NotificationData";
    public static final int  ADD_BRACELET_REQUEST_CODE = 1455;
    public static String BRACELET_MAC_ADDRESS = "braceletMacAddress";

    public interface DefaultConfigurationValues{

        String DEFAULT_MAX_DOCUMENT_SIZE = "4";
    }
}