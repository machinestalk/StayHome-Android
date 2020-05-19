package com.machinestalk.android.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.service.ServiceFactory;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.views.BaseView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.Util;


/**
 * Abstract class which extends {@link AppCompatActivity}.
 * All activities must inherit from this class.
 *
 * @see BaseView
 */
public abstract class BaseActivity extends AppCompatActivity implements Controller {

	protected BaseView view;
	protected ServiceFactory serviceFactory;
	private Snackbar snackbar;

	private  boolean isAppWentToBg;
	private  boolean isWindowFocused;
	private  boolean isBackPressed;
	private static boolean isMovingToAnotherActivity;

	/**
	 * This method must return an object of a subclass of
	 * {@link BaseView}. This view will bind to this Activity through
	 * {@link Controller} interface
	 *
	 * @param controller an implementation of {@link Controller} interface
	 * @return an object of a subclass of {@link BaseView}
	 */
	protected abstract BaseView getViewForController(Controller controller);


	/**
	 * A call to {@code super.onCreate()} is necessary if
	 * you override this method. It sets up the activity and has
	 * other boiler plate code
	 *
	 * @see AppCompatActivity#onCreate(Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		initializeServiceFactory();

		initUI();
	}

	public void initializeServiceFactory()
	{
		serviceFactory = getServiceFactory();
	}

	/**
	 * This method must be overridden if this class will be
	 * used to make Webservice calls.
	 * Method must return an
	 * object of class that inherits
	 * {@link ServiceFactory}
	 *
	 * @return An object of class that inherits {@link ServiceFactory}
	 */

	protected abstract ServiceFactory getServiceFactory();

	private void initUI()
	{
		view = getViewForController(this);
		setContentView(view.getView());
		view.initialize();
	}



	/**
	 * This method is used to set default {@link androidx.appcompat.app.ActionBar}
	 * title.
	 *
	 * @return Return title {@link String}
	 * @see BaseActivity#hasToolbar()
	 * @see BaseActivity#invalidateToolBar()
	 * @see BaseView#onToolBarSetup(Toolbar)
	 */

	public String getActionBarTitle()
	{
		return "";
	}

	/**
	 * Call this method when you want to refresh
	 * {@link Toolbar}
	 *
	 * @see BaseActivity#hasToolbar()
	 * @see BaseView#onToolBarSetup(Toolbar)
	 * @see BaseActivity#getActionBarTitle()
	 */

	protected final void invalidateToolBar()
	{
		view.invalidateToolBar();
	}

	/**
	 * This method is used to know if activity has its own
	 * {@link Toolbar}
	 *
	 * @return Return {@code true} if this Activity implements its on {@link Toolbar}
	 * @see BaseActivity#invalidateToolBar()
	 * @see BaseView#onToolBarSetup(Toolbar)
	 * @see BaseActivity#getActionBarTitle()
	 */
	public boolean hasToolbar()
	{
		return false;
	}

	/**
	 * Call this method to show a loader
	 *
	 * @see BaseActivity#hideLoader()
	 */
	protected void showLoader()
	{
		view.showLoader();
	}

	/**
	 * Call this method to hide a loader
	 *
	 * @see BaseActivity#showLoader() ()
	 */

	protected void hideLoader()
	{
		view.hideLoader();
	}

	/**
	 * Call this method to show a short {@link Toast}
	 *
	 * @param text Text to show in the {@link Toast}
	 *
	 * @see BaseActivity#showLongToast(CharSequence)
	 */

	@SuppressWarnings("WeakerAccess")
	public final void showToast(CharSequence text)
	{
		view.showToast(text);
	}


	/**
	 * Call this method to show a long {@link Toast}
	 *
	 * @param text Text to show in the {@link Toast}
	 *
	 * @see BaseActivity#showToast(CharSequence)
	 */
	public final void showLongToast(CharSequence text)
	{
		view.showLongToast(text);
	}

	/**
	 * A call to {@code super.onResume()} is necessary if
	 * you override this method
	 *
	 * @see AppCompatActivity#onResume()
	 */

	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(snackbar == null) {
			return;
		}

		snackbar.dismiss();
	}

	/**
	 * Call this method to show a Not Implemented {@link Toast}
	 *
	 * Note : It is a good practice to show Not Implemented on
	 * features that are not yet ready for testing
	 *
	 */

	protected final void notImplemented()
	{
		view.notImplemented();
	}


	@Override
	public final BaseActivity getBaseActivity() {
		return this;
	}


	@Override
	protected void onStart () {
		super.onStart();
		if (isIsAppWentToBg()) {
			isAppWentToBg = false;
			onEnterForeground();
		}
	}

	@Override
	protected void onStop () {
		super.onStop();
		if (!isIsWindowFocused()) {
			isAppWentToBg = true;
			onEnterBackground ();
		}
	}

	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date date = new Date();
		return dateFormat.format(date);
	}


	@Override
	public void onWindowFocusChanged (boolean hasFocus) {

		isWindowFocused = hasFocus;

		if ((isIsBackPressed() || isIsMovingToAnotherActivity()) && !hasFocus) {
			isBackPressed = false;
			isMovingToAnotherActivity = false;
			isWindowFocused = true;
		}

		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void startActivity (Intent intent) {

		isMovingToAnotherActivity = true;
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult (Intent intent, int requestCode) {

		isMovingToAnotherActivity = true;
		super.startActivityForResult (intent, requestCode);
	}

	/**
	 * Override this method if you want to provide a custom implementation when
	 * application goes into background (Not very reliable).
	 */
	@SuppressWarnings({"NoopMethodInAbstractClass", "WeakerAccess"})
	protected void onEnterBackground () {


	}


	/**
	 * Override this method if you want to provide a custom implementation when
	 * application goes into foreground (Not very reliable).
	 */
	@SuppressWarnings({"NoopMethodInAbstractClass", "WeakerAccess"})
	protected void onEnterForeground () {


	}

	@Override
	public final View getView() {
		return view.getView();
	}

	@Override
	public void setSnackbar(Snackbar snackBar) {
		snackbar = snackBar;
	}

	public  boolean isIsAppWentToBg() {
		return isAppWentToBg;
	}

	public  boolean isIsWindowFocused() {
		return isWindowFocused;
	}

	public  boolean isIsBackPressed() {
		return isBackPressed;
	}

	public static boolean isIsMovingToAnotherActivity() {
		return isMovingToAnotherActivity;
	}

	public void setAppWentToBg(boolean appWentToBg) {
		isAppWentToBg = appWentToBg;
	}

	public void setWindowFocused(boolean windowFocused) {
		isWindowFocused = windowFocused;
	}

	public void setBackPressed(boolean backPressed) {
		isBackPressed = backPressed;
	}

	public static void setIsMovingToAnotherActivity(boolean isMovingToAnotherActivity) {
		BaseActivity.isMovingToAnotherActivity = isMovingToAnotherActivity;
	}
}
