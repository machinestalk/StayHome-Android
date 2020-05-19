package com.machinestalk.stayhome.constants;

/**
 * Created on 12/21/2016.
 */

public class KeyConstants {

	public static final String KEY_EXTRA_DEVICE							   = "extra_device";
	public static final String KEY_CHANGE_LANGUAGE						   = "ChangeLanguage";
	public static final String KEY_FACE_RECOGNIZER						   = "FACE_RECOGNIZER";
	public static final String KEY_AUTH_TOKEN							   = "X-Authorization";
	public static final String KEY_NOTIFICATION_MISC					   = "MiscellaneousNotification";
	public static final String KEY_BROADCAST_WIFI					       = "broadcastwifi";
	public static final String KEY_WIFI_ENABLED          			       = "wifienabled";
	public static final String KEY_BROADCAST_GPS					       = "broadcastgps";

	public static final String KEY_LAST_UPDATE					           = "lastUpdateTs";
	public static final String KEY_HOME_VALUE_KEY				           = "value";
	public static final String KEY_KEY_DAY			                       = "key";
	public static final String KEY_WRONG_FINGERPRINT					   = "wrongfingerprint";
	public static final String KEY_ID_FINGERPRINT					       = "fingerPrintID";
	public static final String KEY_SIGNUP_DONE					           = "signupDone";
	public static final String KEY_BROADCAST_BLUETOOTH					   = "broadcastblutooth";
	public static final String KEY_BLUETOOTH_ENABLED					   = "bluetoothEnabled";


	public static final String KEY_APP_SERIAL_NUMBER					   = "serialNumber";
	public static final String KEY_APP_ACTIVATION_CODE                     = "activationCode";
	public static final String KEY_ID									   = "Id";
	public static final String KEY_COMING_FROM_NOTIFICATION				   = "isComingFromNotification";

	public static final String KEY_NAME									   = "Name";
	public static final String KEY_TITLE								   = "Title";
	public static final String KEY_VALUE								   = "Value";
	public static final String KEY_PLACE_NAME							   = "PlaceName";


	public static final String KEY_TYPE									   = "Type";
	public static final String KEY_USER_ID								   = "userId";
	public static final String KEY_DESCRIPTION							   = "Description";
	public static final String KEY_CENTER								   = "Center";
	public static final String KEY_LATITUDE								   = "Latitude";
	public static final String KEY_LATITUDE_LOWER_CASE					   = "latitude";
	public static final String KEY_LAST_DAY					               = "lastDay";
	public static final String KEY_LONGITUDE_LOWER_CASE					   = "longitude";
	public static final String KEY_MAC_ADRESS					           = "macadress";
	public static final String KEY_CELL_ID					               = "CellID";
	public static final String KEY_SERVICE_ACTIVATED				       = "serviceActivated";
	public static final String KEY_CHECK_IN_NOTIF   					   = "checkin";
	public static final String KEY_FINGER_NOTIF_SEND   					   = "fingerSend";
	public static final String KEY_FINGER_NOTIF_SHOWN			           = "fingerShown";
	public static final String KEY_LONGITUDE							   = "Longitude";
	public static final String KEY_RADIUS								   = "Radius";
	public static final String KEY_RADIUS_LOWER_CASE					   = "radius";
	public static final String KEY_COLOR								   = "Color";
	public static final String KEY_ACTIVE_FROM_UTC						   = "ActiveFromUtc";
	public static final String KEY_ACTIVE_TO_UTC						   = "ActiveToUtc";
	public static final String KEY_DRIVERS								   = "Drivers";
	public static final String KEY_VEHICLES								   = "Vehicles";
	public static final String KEY_DRIVER								   = "Driver";
	public static final String ZONE_IS_FROM_USER_PROFILE                   = "fromUserProfile";
	public static final String KEY_MODE									   = "mode";

	public static final String KEY_EXTRA_DATA							   = "extra";
	public static final String KEY_RESET_DATA							   = "reset";

	public static final String KEY_ADDRESS								   = "Address";
	public static final String KEY_LOCATION								   = "Location";
	public static final String KEY_FAVORITE_ITEM						   = "FavoriteItem";
	public static final String KEY_ZONE									   = "Zone";
	public static final String KEY_ZONE_DETAILS							   = "ZoneDetails";
	public static final String KEY_ZONE_DETAILS_FROM_MAP				   = "ZoneDetailsFromMap";
	public static final String KEY_ZONE_RETURN							   = "ZoneReturn";

	public static final String KEY_DEVICE								   = "Device";
	public static final String KEY_MESSAGE								   = "message";
	public static final String KEY_ERROR_DESC							   = "error_description";
	public static final String KEY_EXCEPTION_MSG						   = "ExceptionMessage";


	public static final String KEY_SELECTED_ZONE						   = "SelectedZone";
	public static final String KEY_MAX									   = "Max";
	public static final String KEY_MIN									   = "Min";

	public static final String KEY_TEXT									   = "Key";
	public static final String KEY_HTML_EN								   = "en";
	public static final String KEY_HTML_AR								   = "ar";

	public static final String CAN_DELETE_ZONES							   = "canDeleteZones";
	public static final String KEY_TERM_CONDITION						   = "termsOfUse";
	public static final String KEY_ABOUT_US								   = "aboutUs";
	public static final String KEY_STORE								   = "store";
	public static final String KEY_HOW_TO								   = "howTo";
	public static final String KEY_FIND_DEVICE							   = "findDevice";
	public static final String KEY_FIND_IT							       = "findIT";
	public static final String KEY_TERMS_CONDITIONS						   = "termsAndConditions";
	public static final String KEY_WARRANTY					               = "warranty";
	public static final String KEY_PRIVACY_POLICY						   = "privacyPolicy";
	public static final String KEY_MOTORNA_FORM_MISSING_DEVICE			   = "missingDeviceForm";

	public static final String KEY_IS_ONE_DAY							   = "IsOneDay";


	public static final String KEY_FRAGMENT								   = "KeyFragment";
	public static final String KEY_SELECT_LOCATION						   = "Select_Location";

	public static final String KEY_NOTIFICATION_RIDE_TRIP_REMINDER		   = "rideTripReminder";
	public static final String KEY_NOTIFICATION_REFRESH_TOKEN			   = "refreshToken";
	public static final String KEY_IS_DEVICE_EXPIRED			           = "isDeviceExpired";
	public static final String KEY_NUM_DAY                                 = "NumDay";

	public static final String KEY_BUTTON_SATELLITE						   = "btnSatellite";
	public static final String KEY_BUTTON_ZONES							   = "btnZones";
	public static final String KEY_BUTTON_TRAFFIC						   = "btnTraffic";
	public static final String KEY_VEHICLE_ID_NOTIFICATION				   = "VehicleId";
	public static final String KEY_SECURITY_TOKEN						   = "SecurityToken";
	public static final String KEY_OTP_EXPIRED							   = "OTPExpired";

	public static final String KEY_GUIDANCE_GOOGLE_MAP_API				   = "key";

	public interface ContactUsMessageKeys{

		String KEY_MESSAGE_ID = "Id";
		String KEY_MESSAGE_NAME = "Name";
		String KEY_MESSAGE_DESCRIPTION = "Description";
		String KEY_MESSAGE_CONTACT_TYPE = "ContactType";
		String KEY_MESSAGE_ATTACHMENT = "Attachments";
		String KEY_MESSAGE_FILE_NAME = "FileName";
		String KEY_MESSAGE_CREATION_DATE = "CreatedOn";
		String KEY_MESSAGE_COMPLETED_DATE = "CompletedOn";
		String KEY_MESSAGE_STATUS = "Status";
		String KEY_MESSAGE_TICKET_NO = "TicketNo";

	}
}
