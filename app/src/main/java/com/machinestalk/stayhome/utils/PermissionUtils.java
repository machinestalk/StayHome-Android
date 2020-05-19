package com.machinestalk.stayhome.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.constants.AppConstants;

import java.util.List;

/**
 * Created on 3/1/2017.
 */

public class PermissionUtils {

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkAndRequestPermissionQRCode (Controller context) {

		if (ActivityCompat.checkSelfPermission (context.getBaseActivity (), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

			if (context.getBaseActivity ().shouldShowRequestPermissionRationale (Manifest.permission.CAMERA)) {
				showDialogForPermission (context.getBaseActivity (), context.getBaseActivity ().getString (R.string.Gen_Gen_lbl_camera_request_permission));
				return false;
			}
			ActivityCompat.requestPermissions (context.getBaseActivity (), new String[] { Manifest.permission.CAMERA }, AppConstants.MY_PERMISSIONS_CAMERA);
			return false;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkAndRequestPermissionContact (Controller context) {

		if (ActivityCompat.checkSelfPermission (context.getBaseActivity (), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions (context.getBaseActivity (), new String[] { Manifest.permission.READ_CONTACTS }, AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);

			if (!context.getBaseActivity ().shouldShowRequestPermissionRationale (Manifest.permission.READ_CONTACTS)) {
				showCustomDialogIfNecessary (context.getBaseActivity (), context.getBaseActivity ().getString (R.string.Gen_Gen_lbl_contact_request_permission));
				return false;
			}


			return false;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkAndRequestPermissionLocation (Controller controller) {

		if (controller.getBaseActivity () == null)
			return false;

		if ((ActivityCompat.checkSelfPermission (controller.getBaseActivity (), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission (controller.getBaseActivity (), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

			ActivityCompat.requestPermissions (controller.getBaseActivity (), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE },
					AppConstants.MY_PERMISSIONS_LOCATION);


			if (!controller.getBaseActivity ().shouldShowRequestPermissionRationale (Manifest.permission.ACCESS_FINE_LOCATION)) {
				showCustomLocationDialogIfNecessary(controller);
				//showDialogForPermission (context.getBaseActivity (), context.getBaseActivity ().getString (R.string.location_request_permission));
				return false;
			}


			return false;
		}
		return true;
	}
	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkAndRequestPermissionLocation (Activity controller) {

		if (controller == null)
			return false;

		if ((ActivityCompat.checkSelfPermission (controller, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission (controller, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

			ActivityCompat.requestPermissions (controller, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, AppConstants.MY_PERMISSIONS_LOCATION);


			if (!controller.shouldShowRequestPermissionRationale (Manifest.permission.ACCESS_FINE_LOCATION)) {
				showCustomLocationDialogIfNecessary(controller);
				//showDialogForPermission (context.getBaseActivity (), context.getBaseActivity ().getString (R.string.location_request_permission));
				return false;
			}


			return false;
		}
		return true;
	}

	public static void showCustomLocationDialogIfNecessary(final Controller controller)
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (controller != null && controller.getBaseActivity() != null) {
					ActivityManager am = (ActivityManager) controller.getBaseActivity().getSystemService(Context.ACTIVITY_SERVICE);
					List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
					if (!taskInfo.get(0).topActivity.getClassName().equals(AppConstants.PERMISSIONS_ACTIVITY_NAME)) {
						((BaseActivity) controller.getBaseActivity()).showLocationDialog();
					}
				}
			}
		},200);

	}
	public static void showCustomLocationDialogIfNecessary(final Activity controller)
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (controller != null && controller != null) {
					ActivityManager am = (ActivityManager) controller.getSystemService(Context.ACTIVITY_SERVICE);
					List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
					if (!taskInfo.get(0).topActivity.getClassName().equals(AppConstants.PERMISSIONS_ACTIVITY_NAME)) {
						((BaseActivity) controller).showLocationDialog();
					}
				}
			}
		},200);

	}


	public static boolean checkAndRequestPermissionLocationGuidance (Controller context) {

		if (ActivityCompat.checkSelfPermission (context.getBaseActivity (), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (context.getBaseActivity (), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions (context.getBaseActivity (), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, AppConstants.MY_PERMISSIONS_LOCATION);
			return false;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static void checkAndRequestPermissionCamera (Controller context) {

		ActivityCompat.requestPermissions (context.getBaseActivity (), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA }, AppConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

		if (!context.getBaseActivity ().shouldShowRequestPermissionRationale (Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			showCustomDialogIfNecessary (context.getBaseActivity (), context.getBaseActivity ().getString (R.string.Gen_Gen_lbl_camera_request_permission));
			return;
		}

	}

	public static void showDialogForPermission (final Context context, String text) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (context);

		alertDialogBuilder.setMessage (text).setCancelable (false).setPositiveButton (context.getString (R.string.Gen_Gen_lbl_ok), new DialogInterface.OnClickListener () {
			public void onClick (DialogInterface dialog, int id) {

				dialog.cancel ();
			}
		}).setNegativeButton (context.getString (R.string.Gen_Gen_lbl_settings), new DialogInterface.OnClickListener () {
			public void onClick (DialogInterface dialog, int id) {

				dialog.cancel ();

				context.startActivity (new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse ("package:" + context.getPackageName ())));
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create ();

		// show it
		alertDialog.show ();

	}

	public static void showCustomDialogIfNecessary(final Controller controller, final String text)
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				ActivityManager am = (ActivityManager) controller.getBaseActivity().getSystemService (Context.ACTIVITY_SERVICE);
				List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks (1);
				if (!taskInfo.get (0).topActivity.getClassName ().equals (AppConstants.PERMISSIONS_ACTIVITY_NAME)) {
					showDialogForPermission (controller.getBaseActivity (), text);
				}
			}
		},200);

	}


	@TargetApi(Build.VERSION_CODES.M)
	public static boolean isLocationPermissionAvailable (Controller controller) {

		if (controller.getBaseActivity () == null)
			return false;

		if (ActivityCompat.checkSelfPermission (controller.getBaseActivity (), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (controller.getBaseActivity (), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			return false;
		}
		return true;
	}
}
