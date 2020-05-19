package com.machinestalk.android.entities;


import com.google.gson.JsonObject;
import com.machinestalk.android.interfaces.WebServiceResponse;
import com.machinestalk.android.utilities.DateAndTimeUtility;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.android.utilities.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * An abstract class that is to be used as base for all the entities(POJO) in the project
 * , specially the entities that are used for web service response
 */

@SuppressWarnings({"ClassMayBeInterface", "EmptyClass"})
public abstract class BaseEntity implements WebServiceResponse {

    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected Date getDate(JsonObject rootObj, String key) {
       return getDate(rootObj, key, DATE_FORMAT);
    }

    protected Date getDate(JsonObject rootObj, String key, String format) {
        return DateAndTimeUtility.parse(JsonUtility.getString(rootObj, key), format);
    }

    protected long timeSince(Date date) {
        if(date == null) {
            return -1;
        }
        return System.currentTimeMillis() - date.getTime();
    }

    protected long timeLeft(Date date) {
        if(date == null) {
            return -1;
        }
        return date.getTime() - System.currentTimeMillis();
    }


    protected String getTimeString(Date date) {

        long diff = timeSince(date);
//        if(diff < 0) {
//            return "Not Available";
//        }
        long seconds = diff / 1000;
        if(seconds < 60) {
            return "Just now";
        }
        long minutes = seconds / 60;
        if(minutes < 60) {
            return String.valueOf(minutes) + " " + StringUtility.pluralizeStringIfRequired("minute", (int) minutes) + " ago";
        }
        long hours = minutes / 60;
        if(hours < 24) {
            return String.valueOf(hours) + " " + StringUtility.pluralizeStringIfRequired("hour", (int) hours) + " ago";
        }
        long days = hours / 24;
        return String.valueOf(days) + " " + StringUtility.pluralizeStringIfRequired("day", (int) days) + " ago";
    }
}
