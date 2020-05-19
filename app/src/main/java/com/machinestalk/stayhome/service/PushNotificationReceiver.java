package com.machinestalk.stayhome.service;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.entities.NotificationDataEntity;
import com.machinestalk.stayhome.utils.NotificationUtil;

import org.json.JSONObject;

import java.util.Map;

import static com.machinestalk.stayhome.constants.KeyConstants.KEY_NOTIFICATION_MISC;


/**
 * Created on 3/17/2017.
 */

public class PushNotificationReceiver extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        ServiceApi.getInstance().sendFirebseToken(token);
        PreferenceUtility.getInstance(this).putString(KeyConstants.KEY_NOTIFICATION_REFRESH_TOKEN, token);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> map = remoteMessage.getData();
            JsonObject payload = JsonUtility.parseToJsonObject(new JSONObject(map).toString());
            NotificationDataEntity notificationDataEntity = new NotificationDataEntity();
            notificationDataEntity.loadJson(payload);
            if (notificationDataEntity.getType().equals("check_in")) {
//                PreferenceUtility.getInstance(getApplicationContext()).putBoolean(KeyConstants.KEY_CHECK_IN_NOTIF, true);
//                if (NotificationUtil.isAppIsInBackground(this)) {
////                    handleCheckInInBackground(notificationDataEntity);
//                    Log.i(getClass().getName(),"notif Background") ;
//                } else {
//                    handleMiscellaneousInForeground(notificationDataEntity);
//                    Log.i(getClass().getName(),"notif foreground") ;
//                }
            }else if (notificationDataEntity.getBody().equals("resetFingers")) {
                if (NotificationUtil.isAppIsInBackground(this)) {
                    handleFingerResetInBackground(notificationDataEntity);
                    Log.i(getClass().getName(),"notif Background") ;
                } else {
                    handleFingerResetInForeground(notificationDataEntity);
                    Log.i(getClass().getName(),"notif foreground") ;
                }
            }else if (notificationDataEntity.getTitle().equals("Alert")){
                AppConfig.getInstance().startSecondServiceTracking(getBaseContext());
            }
        }else if (remoteMessage.getNotification() != null){
            Map<String, String> map = remoteMessage.getData();
            JsonObject payload = JsonUtility.parseToJsonObject(new JSONObject(map).toString());
            NotificationDataEntity notificationDataEntity = new NotificationDataEntity();
            notificationDataEntity.loadJson(payload);
        }else {
            AppConfig.getInstance().StartBackgroundTasks(getBaseContext());
        }

    }

    private void handleFingerResetInForeground(NotificationDataEntity notificationDataEntity) {
        Intent intent = new Intent(KEY_NOTIFICATION_MISC);
        intent.putExtra(KeyConstants.KEY_RESET_DATA, notificationDataEntity);
        intent.putExtra(KeyConstants.KEY_COMING_FROM_NOTIFICATION, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleFingerResetInBackground(NotificationDataEntity notificationDataEntity) {
        Intent intentCheckIn = new Intent(this, DashboardActivity.class);
        intentCheckIn.putExtra(KeyConstants.KEY_EXTRA_DATA, notificationDataEntity);
        intentCheckIn.putExtra(KeyConstants.KEY_COMING_FROM_NOTIFICATION, true);
        intentCheckIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        NotificationUtil.sendNotification(getApplicationContext(),intentCheckIn, "Check In ","Please Reset your finger print");
    }

}
