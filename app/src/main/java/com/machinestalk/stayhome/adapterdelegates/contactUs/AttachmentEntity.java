package com.machinestalk.stayhome.adapterdelegates.contactUs;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentEntity implements Parcelable {

    private int Id;
    private String Name;
    private Bitmap ImageBitmap;
    private String AttachmentStream;
    private String AttachmentExtension;

    public AttachmentEntity() {
    }


    protected AttachmentEntity(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        ImageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        AttachmentStream = in.readString();
        AttachmentExtension = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeParcelable(ImageBitmap, flags);
        dest.writeString(AttachmentStream);
        dest.writeString(AttachmentExtension);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AttachmentEntity> CREATOR = new Creator<AttachmentEntity>() {
        @Override
        public AttachmentEntity createFromParcel(Parcel in) {
            return new AttachmentEntity(in);
        }

        @Override
        public AttachmentEntity[] newArray(int size) {
            return new AttachmentEntity[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Bitmap getImageBitmap() {
        return ImageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        ImageBitmap = imageBitmap;
    }

    public String getAttachmentStream() {
        return AttachmentStream;
    }

    public void setAttachmentStream(String attachmentStream) {
        AttachmentStream = attachmentStream;
    }

    public String getAttachmentExtension() {
        return AttachmentExtension;
    }

    public void setAttachmentExtension(String attachmentExtension) {
        AttachmentExtension = attachmentExtension;
    }

    @Override
    public String toString() {
        return "AttachmentEntity{" +
                "Name='" + Name + '\'' +
                ", ImageBitmap=" + ImageBitmap +
                ", AttachmentStream='" + AttachmentStream + '\'' +
                ", AttachmentExtension='" + AttachmentExtension + '\'' +
                '}';
    }
}
