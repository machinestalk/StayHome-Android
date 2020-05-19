package com.machinestalk.stayhome.responses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.entities.base.BaseEntity;
import com.machinestalk.stayhome.utils.DateUtil;

@Entity(tableName = "Configuration")
public class Configuration extends BaseEntity implements Parcelable {

    private static final String KEY_RADIUS = "radius";
    public static final String KEY_LAST_VERSION_ANDROID = "lastVersionAndroid";
    public static final String KEY_ANDROID_URL_APP = "url-app-android";
    private static final String KEY_TITLE_HOME = "title";
    private static final String KEY_BODY_HOME = "body";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "lastUpdateTs")
    private String lastUpdateTs;
    @ColumnInfo(name = "key")
    private String key;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "body")
    private String body;
    @ColumnInfo(name = "radius")
    private String radius;
    @ColumnInfo(name = "timeOfservice")
    private int timeOfservice;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "lastVersionAndroid")
    private String lastVersionAndroid;
    @ColumnInfo(name = "androidUrlApp")
    private String androidUrlApp;

    protected Configuration(Parcel in) {
        id = in.readInt();
        lastUpdateTs = in.readString();
        key = in.readString();
        title = in.readString();
        body = in.readString();
        radius = in.readString();
        timeOfservice = in.readInt();
        email = in.readString();
        lastVersionAndroid = in.readString();
        androidUrlApp = in.readString();
    }

    public Configuration() {
    }

    public static final Creator<Configuration> CREATOR = new Creator<Configuration>() {
        @Override
        public Configuration createFromParcel(Parcel in) {
            return new Configuration(in);
        }

        @Override
        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };

    @Override
    public void loadJson(JsonElement jsonElement) {

        if (jsonElement == null) {
            return;
        }
        JsonObject rootObj = jsonElement.getAsJsonObject();
        lastUpdateTs = DateUtil.getCurrentDateFromTimeStamp(JsonUtility.getLong(rootObj, KeyConstants.KEY_LAST_UPDATE));

        key = JsonUtility.getString(rootObj, KeyConstants.KEY_KEY_DAY);

        String homeJString = JsonUtility.getString(rootObj, KeyConstants.KEY_HOME_VALUE_KEY);
        JsonObject homeJsonObject;
        if (JsonUtility.isJSONValid(homeJString)) {
            homeJsonObject = new Gson().fromJson(homeJString, JsonObject.class);
            title = JsonUtility.getString(homeJsonObject, KEY_TITLE_HOME);
            body = JsonUtility.getString(homeJsonObject, KEY_BODY_HOME);
        } else if (key.equals(KEY_RADIUS)) {
            radius = String.valueOf(JsonUtility.getInt(rootObj, KeyConstants.KEY_HOME_VALUE_KEY));
        } else if (key.equals(KEY_LAST_VERSION_ANDROID)) {
            lastVersionAndroid = JsonUtility.getString(rootObj, KeyConstants.KEY_HOME_VALUE_KEY);
        } else if (key.equals(KEY_ANDROID_URL_APP)) {
            androidUrlApp = JsonUtility.getString(rootObj, KeyConstants.KEY_HOME_VALUE_KEY);
        }
    }

    public String getLastUpdateTs() {
        return lastUpdateTs;
    }

    public void setLastUpdateTs(String lastUpdateTs) {
        this.lastUpdateTs = lastUpdateTs;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "lastUpdateTs='" + lastUpdateTs + '\'' +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public int getTimeOfservice() {
        return timeOfservice;
    }

    public void setTimeOfservice(int timeOfservice) {
        this.timeOfservice = timeOfservice;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastVersionAndroid() {
        return lastVersionAndroid;
    }

    public void setLastVersionAndroid(String lastVersionAndroid) {
        this.lastVersionAndroid = lastVersionAndroid;
    }

    public String getAndroidUrlApp() {
        return androidUrlApp;
    }

    public void setAndroidUrlApp(String androidUrlApp) {
        this.androidUrlApp = androidUrlApp;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lastUpdateTs);
        dest.writeString(this.key);
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeInt(this.timeOfservice);
        dest.writeString(this.radius);
        dest.writeString(this.lastVersionAndroid);
        dest.writeString(this.androidUrlApp);
    }
}
