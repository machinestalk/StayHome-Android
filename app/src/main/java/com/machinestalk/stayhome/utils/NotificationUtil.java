package com.machinestalk.stayhome.utils;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.machinestalk.stayhome.R;

import java.util.List;
import java.util.Random;
/**
 * Created on 5/10/2017.
 */

public class NotificationUtil {
	public static final String ANDROID_CHANNEL_ID = "com.machinestalk.connectedcar.ANDROID";
	public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";

	public static void sendNotification(Context context, Intent intent, String title, String content) {
		// RemoteViews remoteViews = getComplexNotificationView(context, title, content);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ANDROID_CHANNEL_ID);

		PendingIntent resultIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationBuilder.setSmallIcon(R.drawable.ic_launch)
				.setContentTitle(title)
				.setContentText(content)
				.setContentIntent(resultIntent)
				.setVibrate(new long[]{100, 250, 100, 500})
				.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
				.setAutoCancel(true);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (mNotificationManager != null) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
				NotificationChannel notificationChannel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
				mNotificationManager.createNotificationChannel(notificationChannel);
			}
			mNotificationManager.notify(new Random().nextInt(500), notificationBuilder.build());
		}
	}


	public static boolean isAppIsInBackground (Context context) {

		boolean isInBackground = true;
		try {
			ActivityManager am = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
				List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses ();
				for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
					if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						for (String activeProcess : processInfo.pkgList) {
							if (activeProcess.equals (context.getPackageName ())) {
								isInBackground = false;
							}
						}
					}
				}
			}
			else {
				List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks (1);
				ComponentName componentInfo = taskInfo.get (0).topActivity;
				if (componentInfo.getPackageName ().equals (context.getPackageName ())) {
					isInBackground = false;
				}
			}
		}catch (Exception e){
			return isInBackground;
		}

		return isInBackground;
	}

}
