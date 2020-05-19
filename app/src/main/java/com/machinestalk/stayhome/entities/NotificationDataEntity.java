package com.machinestalk.stayhome.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.stayhome.entities.base.BaseEntity;

/**
 * Created on 5/11/2017.
 */

public class NotificationDataEntity extends BaseEntity implements Parcelable {

    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_TYPE = "type";
    private String title;
    private String body;
    private String type;


    public NotificationDataEntity() {

    }

    @Override
    public void loadJson(JsonElement jsonElement) {

        if (jsonElement == null) {
            return;
        }

        JsonObject root = jsonElement.getAsJsonObject();

        title = JsonUtility.getString(root, KEY_TITLE);
        body = JsonUtility.getString(root, KEY_BODY);
        type = JsonUtility.getString(root, KEY_TYPE);

    }

    public String getType() {
        return type;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.type);
    }

    protected NotificationDataEntity(Parcel in) {

        this.title = in.readString();
        this.body = in.readString();
        this.type = in.readString();

    }


    public static final Creator<NotificationDataEntity> CREATOR = new Creator<NotificationDataEntity>() {
        @Override
        public NotificationDataEntity createFromParcel(Parcel source) {
            return new NotificationDataEntity(source);
        }

        @Override
        public NotificationDataEntity[] newArray(int size) {
            return new NotificationDataEntity[size];
        }
    };
}
