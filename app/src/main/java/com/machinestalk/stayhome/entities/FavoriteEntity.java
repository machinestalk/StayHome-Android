package com.machinestalk.stayhome.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.entities.BaseEntity;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.utils.LocationUtils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created on 12/21/2016.
 */

public class FavoriteEntity extends BaseEntity implements Parcelable {

    public static final Creator< FavoriteEntity > CREATOR = new Creator< FavoriteEntity >() {
        @Override
        public FavoriteEntity createFromParcel( Parcel source ) {

            return new FavoriteEntity( source );
        }

        @Override
        public FavoriteEntity[] newArray( int size ) {

            return new FavoriteEntity[size];
        }
    };
    private int    id;
    private String name;
    private String address;
    private LatLng location;
    private int    userId;
    private double latitude;
    private double longitude;
    private boolean isFavourited = false;
    private boolean isSelected   = false;
    private String placeId;
    private boolean enabled = true;

    public FavoriteEntity() {
    }

    protected FavoriteEntity(Parcel in ) {
        this.id = in.readInt();
        this.name = in.readString();
        this.address = in.readString();
        this.location = in.readParcelable( LatLng.class.getClassLoader() );
        this.userId = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.isFavourited = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
    }

    @Override
    public void loadJson( JsonElement jsonElement ) {

        if ( JsonUtility.isJsonElementNull( jsonElement ) ) {
            return;
        }

        JsonObject rootObject     = jsonElement.getAsJsonObject();
        JsonObject locationObject = JsonUtility.getJsonObject( rootObject, KeyConstants.KEY_LOCATION );

        id = JsonUtility.getInt( rootObject, KeyConstants.KEY_ID );
        name = JsonUtility.getString( rootObject, KeyConstants.KEY_NAME );
        address = JsonUtility.getString( rootObject, KeyConstants.KEY_ADDRESS );
        location = new LatLng( JsonUtility.getDouble( locationObject, KeyConstants.KEY_LATITUDE ), JsonUtility.getDouble( locationObject, KeyConstants.KEY_LONGITUDE ) );
        userId = JsonUtility.getInt( rootObject, KeyConstants.KEY_USER_ID );
        latitude = JsonUtility.getDouble( locationObject, KeyConstants.KEY_LATITUDE );
        longitude = JsonUtility.getDouble( locationObject, KeyConstants.KEY_LONGITUDE );

    }




    public int getId() {

        return id;
    }

    public void setId( int id ) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName( String name ) {

        if ( TextUtils.isEmpty( name ) )
            this.name = "N/A";
        this.name = name;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress( String address ) {

        this.address = address;
    }

    public LatLng getLocation() {

        if ( location == null )
            return new LatLng( latitude, longitude );

        return location;
    }

    public void setLocation( LatLng location ) {

        if ( location != null ) {
            this.latitude = location.latitude;
            this.longitude = location.longitude;
        }
        this.location = location;
    }

    public int getUserId() {

        return userId;
    }

    public void setUserId( int userId ) {

        this.userId = userId;
    }

    public LatLng getCalculatedLocation() {

        return new LatLng( this.latitude, this.longitude );
    }

    public boolean isInRange( LatLng location ) {

        return LocationUtils.getDistanceBetween( this.location, location ) <= 10;
    }

    public boolean isFavourited() {

        return isFavourited;
    }

    public void setFavourited( boolean favourited ) {

        isFavourited = favourited;
    }

    public boolean isSelected() {

        return isSelected;
    }

    public void setSelected( boolean selected ) {

        isSelected = selected;
    }

    public String getPlaceId() {

        return placeId;
    }

    @Override
    public boolean equals( Object o ) {

        if ( this == o )
            return true;

        if ( o == null || getClass() != o.getClass() )
            return false;

        FavoriteEntity entity = ( FavoriteEntity ) o;

        return new EqualsBuilder().append( latitude, entity.latitude ).append( longitude, entity.longitude ).append( getLocation(), entity.getLocation() ).isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder( 17, 37 ).append( getLocation() ).append( latitude ).append( longitude ).toHashCode();
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel( Parcel dest, int flags ) {

        dest.writeInt( this.id );
        dest.writeString( this.name );
        dest.writeString( this.address );
        dest.writeParcelable( this.location, flags );
        dest.writeInt( this.userId );
        dest.writeDouble( this.latitude );
        dest.writeDouble( this.longitude );
        dest.writeByte( this.isFavourited ? ( byte ) 1 : ( byte ) 0 );
        dest.writeByte( this.isSelected ? ( byte ) 1 : ( byte ) 0 );
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }
}