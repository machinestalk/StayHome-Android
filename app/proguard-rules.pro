#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


-keep public class com.machinestalk.connectedcar.entities.** { *; }
-keep public class com.machinestalk.connectedcar.responses.** { *; }
-keep public class com.machinestalk.connectedcar.service.** { *; }
-keep public class com.machinestalk.connectedcar.utils.** { *; }
-keep public class com.machinestalk.connectedcar.service.body.** { *; }
-keep public class microsoft.aspnet.signalr.client.hubs.** { *; }
-keep public class com.machinestalk.connectedcar.signalR.models.** { *; }
-keep public class com.google.android.gms.maps.model.** { *; }
-keep class androidx.core.app.CoreComponentFactory { *; }
-keep public class com.google.android.gms.maps.model.** { *; }
-keep public class com.payfort.fort.android.sdk.base.** { *; }
-keep public class com.payfort.fort.android.sdk.base.callbacks.** { *; }
-keep public class com.payfort.fort.android.sdk.activities.services.** { *; }
-keep public class com.payfort.fort.android.sdk.activities.** { *; }
-keep public class com.payfort.sdk.android.dependancies.models.** { *; }
-keep public class com.payfort.sdk.android.dependancies.models.SdkResponse.** { *; }
-keep public class com.payfort.** { *; }




-keepclassmembers class * implements android.os.Parcelable {
}

-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#### -- Picasso --
 -dontwarn com.squareup.picasso.**

 #### -- Apache Commons --

 -dontwarn org.apache.commons.**

#### -- Okio --
-keepattributes Signature
-keepattributes Annotation
-dontwarn okio.**

#### --retrofit2

-dontwarn retrofit2.**

-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource
-dontwarn androidx.annotation.**


#-dontwarn android.arch.**
#-dontwarn com.google.firebase.messaging.**




