package com.machinestalk.stayhome.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.helpers.NotificationHelper;
import com.machinestalk.stayhome.service.ServiceApi;
import com.machinestalk.stayhome.service.body.CompliantBody;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == "android.intent.action.ACTION_SHUTDOWN") {
            // Your tasks for shut down
            Log.i("BootReceiver", "shut down");
            PreferenceUtility.getInstance(context).putBoolean(KeyConstants.KEY_SERVICE_ACTIVATED, true);
            NotificationHelper.createNotificationWithIntent(NotificationHelper.SHUT_DOWN_SESSION_NOTIFICATION_ID,
                    "Your session is shut down we will conact you ", context);
            CompliantBody compliantBody = new CompliantBody();
            compliantBody.setCompliant(0);
            compliantBody.setSystemRebooted(1);
            compliantBody.setReason("SystemRebooted");
            ServiceApi.getInstance().sendCompliantStatus(compliantBody);
        }
    }
}