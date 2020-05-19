package com.machinestalk.stayhome.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;

public class NotificationHelper {

    private static NotificationManager mNotificationManager;
    private static NotificationChannel mChannel;
    private static NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static RemoteViews contentView;

    public static final int ZONE_OUT_NOTIFICATION_ID = 10;
    public static final int GPS_DISABLED_NOTIFICATION_ID = 11;
    public static final int BLUETOOTH_DISABLED_NOTIFICATION_ID = 12;
    public static final int BATTERY_LOW_DISABLED_NOTIFICATION_ID = 13;
    public static final int WIFI_DISABLED_NOTIFICATION_ID = 14;
    public static final int SHUT_DOWN_SESSION_NOTIFICATION_ID = 15;
    public static final int ZONE_IN_NOTIFICATION_ID = 16;

//    public NotificationHelper(Context context) {
//        mContext = context;
//    }

    /**
     * Create and push the notification
     */

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";


    public static void createNotificationWithIntent(int notificationId, String message, Context context) {
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(context, DashboardActivity.class);
//        resultIntent.putExtra(KeyConstants.KEY_MODE, AppConstants.MODE_ALL);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (StringUtility.isEmptyOrNull(message)) {
            message = "";
        }

        int resource = R.drawable.ic_launch;
        if (notificationId == ZONE_OUT_NOTIFICATION_ID || notificationId == BLUETOOTH_DISABLED_NOTIFICATION_ID || notificationId == WIFI_DISABLED_NOTIFICATION_ID
                || notificationId == BATTERY_LOW_DISABLED_NOTIFICATION_ID || notificationId == GPS_DISABLED_NOTIFICATION_ID || notificationId == SHUT_DOWN_SESSION_NOTIFICATION_ID) {

            resource = R.drawable.ic_warning;
        }


        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        //mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(resource);
        mBuilder.setContentTitle(message)
                //.setContentText(message)
                //.setNumber(notifNumber)
//                .setCustomBigContentView(contentView)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                //.setCustomContentView(contentView)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(notificationChannel);
                //mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            }
            mNotificationManager.notify(notificationId, mBuilder.build());
        }

    }


    public static boolean areNotificationsEnabled(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                if (channel != null)
                    return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }


}
