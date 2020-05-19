package com.machinestalk.stayhome.entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.entities.BaseEntity;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.utils.DateUtil;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created on 12/21/2016.
 */
@Entity(tableName = "zone")
public class ZoneEntity extends BaseEntity implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "placeName")
    private String placeName;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "center")
    private LatLng center;
    @ColumnInfo(name = "radius")
    private int radius;
    @ColumnInfo(name = "color")
    private int color;
    @ColumnInfo(name = "type")
    private int type = Type.NONE;
    @ColumnInfo(name = "activeFrom")
    private Date activeFrom;
    @ColumnInfo(name = "activeTo")
    private Date activeTo;
    @ColumnInfo(name = "typeKey")
    private String typeKey;
    @ColumnInfo(name = "IsOneDay")
    private boolean IsOneDay;
    @ColumnInfo(name = "driverIds")
    private String driverIds;
    @ColumnInfo(name = "isNotFromDriverProfile")
    private boolean isNotFromDriverProfile = true;
    @ColumnInfo(name = "vehicleIds")
    private String vehicleIds;
    @Ignore
    private boolean isSelected = false;

    public ZoneEntity() {
    }

    @Override
    public void loadJson(JsonElement jsonElement) {

        if (JsonUtility.isJsonElementNull(jsonElement)) {
            return;
        }

        JsonObject rootObject = jsonElement.getAsJsonObject();

        if (rootObject.getAsJsonArray(KeyConstants.KEY_LATITUDE_LOWER_CASE).size() > 0  ){
            double longitude;
            double lattitude ;
            longitude = rootObject.getAsJsonArray(KeyConstants.KEY_LONGITUDE_LOWER_CASE).get(0).getAsJsonObject().get("value").getAsDouble() ;
            lattitude = rootObject.getAsJsonArray(KeyConstants.KEY_LATITUDE_LOWER_CASE).get(0).getAsJsonObject().get("value").getAsDouble() ;

            center = new LatLng(lattitude , longitude);
        }
        if (rootObject.getAsJsonArray(KeyConstants.KEY_RADIUS_LOWER_CASE).size() > 0  ){
            radius = rootObject.getAsJsonArray(KeyConstants.KEY_RADIUS_LOWER_CASE).get(0).getAsJsonObject().get("value").getAsInt() ;
        }

    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ZoneEntity entity = (ZoneEntity) o;

        return new EqualsBuilder().append(getId(), entity.getId()).isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
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

        this.name = name;
    }

    public String getPlaceName() {

        return placeName;
    }

    public void setPlaceName(String placeName) {

        this.placeName = placeName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public LatLng getCenter() {

        return center;
    }

    public void setCenter(LatLng center) {

        this.center = center;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public int getRadius() {

        return radius;
    }

    public void setRadius(int radius) {

        this.radius = radius;
    }

    public int getColor() {

        return color;
    }

    public String getColorHexCode() {

        return String.format("#%06X", 0xFFFFFF & color);
    }

    public void setColor(int color) {

        this.color = color;
    }

    public int getType() {

        return type;
    }

    public void setType(int type) {

        this.type = type;
    }


    public boolean getIsOneDay() {

        return IsOneDay;
    }

    public void setIsOneDay(boolean IsOneDay) {

        this.IsOneDay = IsOneDay;
    }


    public Date getActiveFrom() {

        return activeFrom;
    }

    public String getActiveFromUTC() {

        DateFormat timeFormat = new SimpleDateFormat("H:mm:ss", Locale.US);
        timeFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.getUTC()));

        return timeFormat.format(activeFrom);
    }

    public void setActiveFrom(Date activeFrom) {

        this.activeFrom = activeFrom;
    }

    public Date getActiveTo() {

        return activeTo;
    }

    public String getActiveToUTC() {

        DateFormat timeFormat = new SimpleDateFormat("H:mm:ss", Locale.US);
        timeFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.getUTC()));

        return timeFormat.format(activeTo);
    }

    public void setActiveTo(Date activeTo) {

        this.activeTo = activeTo;
    }

    public String getActiveFromString(Context context) {

        if (activeFrom == null) {
            return "";
        } else {
           return DateUtil.timeTypePattern(context, activeFrom, "h:mm ");
        }
    }

    public String getActiveToString(Context context) {

        if (activeTo == null) {
            return context.getString(R.string.Zon_ZonDt_lbl_24_hours);
        } else {
           return DateUtil.timeTypePattern(context, activeTo, "h:mm ");
        }
    }


    public boolean isNew() {

        return getId() == 0;
    }

    public interface Type {
        int NONE = -1;
        int IN = 0;
        int OUT = 1;
        int INOUT = 2;
    }

    public boolean isOneDay() {

        return IsOneDay;
    }

    public void setOneDay(boolean oneDay) {

        IsOneDay = oneDay;
    }

    public List<Integer> getListDriverIds() {
        List<Integer> integerList = new ArrayList<>();
        if (TextUtils.isEmpty(driverIds)) {
            return integerList;
        }
        List<String> listDriverId = new ArrayList<String>(Arrays.asList(driverIds.split(",")));
        for (int i = 0; i < listDriverId.size(); i++) {
            integerList.add(Integer.parseInt(listDriverId.get(i)));
        }
        return integerList;
    }

    public void setListDriverIds(List<Integer> listDriverId) {
        String driversIds = null;
        StringBuilder driversIdsBuilder = new StringBuilder();
        if (listDriverId != null && listDriverId.size() > 0) {
            for (int i = 0; i < listDriverId.size(); i++) {
                driversIdsBuilder.append(String.valueOf(listDriverId.get(i))).append(",");
            }
            driversIds = driversIdsBuilder.toString();
        }
        this.driverIds = driversIds;
    }

    public List<Integer> getListVehicleIds() {
        List<Integer> integerList = new ArrayList<>();
        if (TextUtils.isEmpty(vehicleIds)) {
            return integerList;
        }
        List<String> listVehicleId = new ArrayList<String>(Arrays.asList(vehicleIds.split(",")));
        for (int i = 0; i < listVehicleId.size(); i++) {
            integerList.add(Integer.parseInt(listVehicleId.get(i)));
        }
        return integerList;
    }

    public void setListVehicleIds(List<Integer> listVehicleId) {
        String vehicleIds = null;
        StringBuilder driversIdsBuilder = new StringBuilder();
        if (listVehicleId != null && listVehicleId.size() > 0) {
            for (int i = 0; i < listVehicleId.size(); i++) {
                driversIdsBuilder.append(String.valueOf(listVehicleId.get(i))).append(",");
            }
            vehicleIds = driversIdsBuilder.toString();
        }
        this.vehicleIds = vehicleIds;
    }

    public boolean isNotFromDriverProfile() {

        return isNotFromDriverProfile;
    }

    public void setNotFromDriverProfile(boolean notFromDriverProfile) {

        isNotFromDriverProfile = notFromDriverProfile;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.placeName);
        dest.writeString(this.description);
        dest.writeParcelable(this.center, flags);
        dest.writeInt(this.radius);
        dest.writeInt(this.color);
        dest.writeInt(this.type);
        dest.writeLong(this.activeFrom != null ? this.activeFrom.getTime() : -1);
        dest.writeLong(this.activeTo != null ? this.activeTo.getTime() : -1);
        dest.writeString(this.typeKey);
        dest.writeByte(this.IsOneDay ? (byte) 1 : (byte) 0);
        dest.writeString(this.driverIds);
        dest.writeByte(this.isNotFromDriverProfile ? (byte) 1 : (byte) 0);
        dest.writeString(this.vehicleIds);
    }

    protected ZoneEntity(Parcel in) {

        this.id = in.readInt();
        this.name = in.readString();
        this.placeName = in.readString();
        this.description = in.readString();
        this.center = in.readParcelable(LatLng.class.getClassLoader());
        this.radius = in.readInt();
        this.color = in.readInt();
        this.type = in.readInt();
        long tmpActiveFrom = in.readLong();
        this.activeFrom = tmpActiveFrom == -1 ? null : new Date(tmpActiveFrom);
        long tmpActiveTo = in.readLong();
        this.activeTo = tmpActiveTo == -1 ? null : new Date(tmpActiveTo);
        this.typeKey = in.readString();
        this.IsOneDay = in.readByte() != 0;
        this.driverIds = in.readString();
        this.isNotFromDriverProfile = in.readByte() != 0;
        this.vehicleIds = in.readString();
    }

    public static final Creator<ZoneEntity> CREATOR = new Creator<ZoneEntity>() {
        @Override
        public ZoneEntity createFromParcel(Parcel source) {

            return new ZoneEntity(source);
        }

        @Override
        public ZoneEntity[] newArray(int size) {

            return new ZoneEntity[size];
        }
    };

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }



    public String getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(String vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "ZoneEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", placeName='" + placeName + '\'' +
                ", description='" + description + '\'' +
                ", center=" + center +
                ", radius=" + radius +
                ", color=" + color +
                ", type=" + type +
                ", activeFrom=" + activeFrom +
                ", activeTo=" + activeTo +
                ", typeKey='" + typeKey + '\'' +
                ", IsOneDay=" + IsOneDay +
                ", driverIds='" + driverIds + '\'' +
                ", isNotFromDriverProfile=" + isNotFromDriverProfile +
                ", vehicleIds='" + vehicleIds + '\'' +
                ", isSelected='" + isSelected + '\'' +
                '}';
    }
}
