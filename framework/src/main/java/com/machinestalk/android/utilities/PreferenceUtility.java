package com.machinestalk.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class serves as an intermediate layer between {@link SharedPreferences}
 * and your code. Contains various methods to set/get persistent values in
 * Shared Preferences.
 *
 * 
 */
@SuppressWarnings({"BooleanMethodNameMustStartWithQuestion", "WeakerAccess"})
public final class PreferenceUtility {


	/*
	 */
	public static final String IS_LOGGED = "is_logged";

	public final static int DEFAULT_GUIDANCE_TAB_INDEX = 0;
	public static final String SELECTED_GUIDANCE_TAB_INDEX = "selectedGuidanceTabIndex";
	public static final boolean DEFAULT_IS_LOGGED = false;

	public static final String WIFI_STATUS = "wifiStatus";
	public static final String INTERNET_STATUS = "internetStatus";
	public static final String MOVEMENT_ACCLRMTR_STATUS = "movementAcclrmtrStatus";
	public static final String IS_MESSAGE_Z_OUT_STATUS = "isMessageZoneOutShown";
	public static final String IS_MESSAGE_Z_IN_STATUS = "isMessageZoneInShown";

	public static final String USER_ACTIVITY = "userActivity";
	public static final String BLUETOOTH_STATUS = "bluetoothStatus";
	public static final String BLUETOOTH_STATUS_CHANGED = "bluetoothStatusChanged";
	public static final String NETWORK_CONNECTION_MODE_CHANGED = "networkConnectionModeChanged";
	public static final String NETWORK_CONNECTION_MODE = "networkConnectionMode";
	public static final String EVENT_LAST_DAY = "DiffDay";
	public static final String BRACELET_DETECTED = "braceletDetected";
	public static final String BRACELET_Connected_DATE_TIME = "braceletConnectedDateTime";

    /**
	 * Singleton instance
	 */
	private static PreferenceUtility mInstance;
	/**
	 * System preference class
	 */
	private SharedPreferences mPreferences;


	/**
	 * Constructor
	 */
	private PreferenceUtility(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Singleton
	 */
	public static PreferenceUtility getInstance(Context context) {
		if (mInstance == null) {
			synchronized (PreferenceUtility.class) {
				mInstance = new PreferenceUtility(context);
			}
		}
		return mInstance;
	}


	/**
	 * Set an integer value for a key in {@link SharedPreferences}.
	 *s
	 * @param key Shared preferences key
	 * @param value Integer value to be saved
	 */
	public void putInteger (String key, int value) {

		mPreferences.edit()
				.putInt(key, value)
				.apply();
	}

	/**
	 * Set a boolean value for a key in {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key
	 * @param value Boolean value to be saved
	 */
	public void putBoolean (String key, boolean value) {

		mPreferences
				.edit()
				.putBoolean(key, value)
				.apply();
	}

	/**
	 * Set a string value for a key in {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key
	 * @param value String value to be saved
	 */
	public void putString(String key, String value) {

		mPreferences
				.edit()
				.putString (key, value)
				.apply();
	}

	/**
	 * Set a float value for a key in {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key
	 * @param value Float value to be saved
	 */
	public void putFloat (String key, float value) {

		mPreferences
				.edit()
				.putFloat(key, value)
				.apply();
	}

	/**
	 * Set a long value for a key in {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key
	 * @param value Long value to be saved
	 */
	public void putLong (String key, long value) {

		mPreferences
				.edit()
				.putLong(key, value)
				.apply();
	}

	/**
	 * Get an integer value for a key from {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key, whose value is to be retrieved
	 * @param defaultValue Default value
	 * @return The integer value against the key. If key is not set,
	 *         defaultValue will be returned
	 */
	public int getInteger (String key, int defaultValue) {

		return mPreferences.getInt(key, defaultValue);
	}

	/**
	 * Get a string value for a key from {@link SharedPreferences}.
	 *
	 *
	 * @param key Shared preferences key, whose value is to be retrieved
	 * @param defaultValue Default value
	 * @return The string value against the key. If key is not set, defaultValue
	 *         will be returned
	 */
	public String getString(String key, String defaultValue) {

		return mPreferences.getString(key, defaultValue);
	}

	/**
	 * Get a boolean value for a key from {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key, whose value is to be retrieved
	 * @param defaultValue Default value
	 * @return The boolean value against the key. If key is not set,
	 *         defaultValue will be returned
	 */
	public boolean getBoolean (String key, boolean defaultValue) {

		return mPreferences.getBoolean(key, defaultValue);
	}

	/**
	 * Get a float value for a key from {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key, whose value is to be retrieved
	 * @param defaultValue Default value
	 * @return The float value against the key. If key is not set, defaultValue
	 *         will be returned
	 */
	public float getFloat (String key, float defaultValue) {

		return mPreferences.getFloat(key, defaultValue);
	}

	/**
	 * Get a long value for a key from {@link SharedPreferences}.
	 *
	 * @param key Shared preferences key, whose value is to be retrieved
	 * @param defaultValue Default value
	 * @return The longu value against the key. If key is not set, defaultValue
	 *         will be returned
	 */
	public long getLong (String key, long defaultValue) {

		return mPreferences.getLong(key, defaultValue);
	}

}
