package com.machinestalk.stayhome.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.entities.BaseEntity;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.stayhome.adapterdelegates.contactUs.AttachmentEntity;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.orhanobut.logger.Logger;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageContactEntity extends BaseEntity implements Parcelable {

    private int Id;
    private String Name;
    private String Description;
    private int ContactType;
    private String CreationDate;
    private String CreationTime;
    private String CompletedDate;
    private String CompletedTime;
    private int Status;
    private String TicketNo;
    private List<AttachmentEntity> mAttachmentEntityList;

    public MessageContactEntity() {
    }


    @Override
    public void loadJson(JsonElement jsonElement) {

        if (JsonUtility.isJsonElementNull (jsonElement)) {
            return;
        }

        JsonObject rootObject = jsonElement.getAsJsonObject ();

        Id = JsonUtility.getInt (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_ID);
        Name = JsonUtility.getString (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_NAME);
        Description = JsonUtility.getString (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_DESCRIPTION);
        ContactType = JsonUtility.getInt (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_CONTACT_TYPE);
        String creationDate = JsonUtility.getString (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_CREATION_DATE);
        String completedDate = JsonUtility.getString (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_COMPLETED_DATE);
        TicketNo = JsonUtility.getString (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_TICKET_NO);
        Status = JsonUtility.getInt (rootObject, KeyConstants.ContactUsMessageKeys.KEY_MESSAGE_STATUS);

        try {
            SimpleDateFormat creationDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            creationDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            SimpleDateFormat completedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat timeFormatDisplay = new SimpleDateFormat("HH:mm a", Locale.getDefault());
            SimpleDateFormat dateFormatDisplay = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

            if (!creationDate.isEmpty()){
                Date dateCreated = creationDateFormat.parse(creationDate);
                DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
                symbols.setAmPmStrings(new String[] { "am", "pm" });
                timeFormatDisplay.setDateFormatSymbols(symbols);
                CreationDate = dateFormatDisplay.format(dateCreated);
                CreationTime = timeFormatDisplay.format(dateCreated);

            }
            if (!completedDate.isEmpty()){
                Date dateCompleted = completedDateFormat.parse(completedDate);
                CompletedDate = dateFormatDisplay.format(dateCompleted);
                CompletedTime = timeFormatDisplay.format(dateCompleted);
            }





        } catch (ParseException e) {
            Logger.i("error parse date "+e);
        }

    }


    protected MessageContactEntity(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        Description = in.readString();
        ContactType = in.readInt();
        CreationDate = in.readString();
        CreationTime = in.readString();
        CompletedDate = in.readString();
        CompletedTime = in.readString();
        Status = in.readInt();
        TicketNo = in.readString();
        mAttachmentEntityList = in.createTypedArrayList(AttachmentEntity.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeInt(ContactType);
        dest.writeString(CreationDate);
        dest.writeString(CreationTime);
        dest.writeString(CompletedDate);
        dest.writeString(CompletedTime);
        dest.writeInt(Status);
        dest.writeString(TicketNo);
        dest.writeTypedList(mAttachmentEntityList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageContactEntity> CREATOR = new Creator<MessageContactEntity>() {
        @Override
        public MessageContactEntity createFromParcel(Parcel in) {
            return new MessageContactEntity(in);
        }

        @Override
        public MessageContactEntity[] newArray(int size) {
            return new MessageContactEntity[size];
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getContactType() {
        return ContactType;
    }

    public void setContactType(int contactType) {
        ContactType = contactType;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        this.CreationDate = creationDate;
    }

    public String getCompletedDate() {
        return CompletedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.CompletedDate = completedDate;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public void setCompletedTime(String completedTime) {
        CompletedTime = completedTime;
    }

    public String getCompletedTime() {
        return CompletedTime;
    }

    public void setCreationTime(String creationTime) {
        CreationTime = creationTime;
    }

    public String getCreationTime() {
        return CreationTime;
    }

    public String getTicketNo() {
        return TicketNo;
    }

    public void setTicketNo(String ticketNo) {
        TicketNo = ticketNo;
    }

    @Override
    public String toString() {
        return "MessageContactEntity{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Description='" + Description + '\'' +
                ", ContactType=" + ContactType +
                ", CreationDate='" + CreationDate + '\'' +
                ", CreationTime='" + CreationTime + '\'' +
                ", CompletedDate='" + CompletedDate + '\'' +
                ", CompletedTime='" + CompletedTime + '\'' +
                ", Status=" + Status +
                ", TicketNo='" + TicketNo + '\'' +
                ", mAttachmentEntityList=" + mAttachmentEntityList +
                '}';
    }
}
