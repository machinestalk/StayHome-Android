package com.machinestalk.android.views;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.machinestalk.android.R;
import com.machinestalk.android.activities.BaseActivity;
import com.machinestalk.android.interfaces.Controller;


/**
 * All {@code Views} for {@code Controllers} must inherit this abstract class
 *
 * @see BaseActivity
 */

public abstract class BaseView
{
    @SuppressWarnings("WeakerAccess")
    protected View view;
    protected final Controller controller;
    private Toolbar toolbar;
    private ProgressBar layoutLoading;

	/*
        Life Cycle Methods
	 */

    protected BaseView(Controller controller){
        this.controller = controller;
    }

    public final View getView()
    {
        if(view == null) {
            view = getBaseActivity().getLayoutInflater().inflate(getViewLayout(), null);
        }
        return view;
    }

    /**
     * Override this method if you want to
     * do something on {@code onResume} event
     *
     * Note : This method is tied to Controller's {@code onResume} event
     *
     * @see Fragment#onResume()
     */

    @SuppressWarnings("NoopMethodInAbstractClass")
    public void onResume()
    {

    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    public void onPause()
    {

    }


    @SuppressWarnings("NoopMethodInAbstractClass")
    public void onStop()
    {

    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    public void onDestroy()
    {

    }


    /**
     * This method must return the resource ID
     * of this view's layout
     *
     * @return Resource ID of layout
     */

    @LayoutRes
    protected abstract int getViewLayout();

    /**
     * Do all the basic initialization code in this method
     *
     * Note : This method is tied to Controller's {@code onCreate} event
     *
     * @see Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    protected abstract void onCreate();

    /**
     * Set all the Action Listeners in this method
     *
     * Note : This method is called right after {@code onCreate}
     *
     * @see BaseView#onCreate()
     */

    protected abstract void setActionListeners();

    public final void initialize() {

        onCreate();
        setActionListeners();
        setupProgressBar();
        invalidateToolBar();
        onPostInitialize();
    }

    protected void onPostInitialize() {

    }

    /**
     * Call this method when you want to refresh
     * {@link Toolbar}
     *
     * @see BaseActivity#hasToolbar()
     * @see BaseView#onToolBarSetup(Toolbar)
     * @see BaseActivity#getActionBarTitle()
     */

    public final void invalidateToolBar()
    {
        if(getBaseActivity() != null) controller.getBaseActivity().supportInvalidateOptionsMenu();
        if(getToolbarId() == 0) {
            return;
        }

        boolean isAlreadyLoaded = true;

        if(toolbar == null) {
            isAlreadyLoaded = false;
            toolbar = findViewById(getToolbarId());
        }

        if (toolbar == null) {
            return;
        }

        if(!controller.hasToolbar()) {
            toolbar.setVisibility(View.GONE);
            return;
        }

        toolbar.setVisibility(View.VISIBLE);

        if(!isAlreadyLoaded) {
            setupToolBar();
        }

        onToolBarRefresh(toolbar);
    }

    private void setupToolBar()
    {
        setTitle(controller.getActionBarTitle());

        onToolBarSetup(toolbar);
    }

    protected void setTitle(CharSequence title) {
        BaseActivity baseActivity = controller.getBaseActivity();

        if(baseActivity == null) {
            return;
        }

        baseActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = baseActivity.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle( !TextUtils.isEmpty( title ) ? title : "" );
            actionBar.setDisplayHomeAsUpEnabled( true );
        }
    }

    @SuppressWarnings({"NoopMethodInAbstractClass", "WeakerAccess", "UnusedParameters"})
    protected void onToolBarRefresh(Toolbar toolbar) {

    }

    private void setupProgressBar() {
        if(getProgressBarId() == 0) {
            return;
        }

        layoutLoading = findViewById(getProgressBarId());
        if(layoutLoading == null) {
            return;
        }

        onProgressBarSetup(layoutLoading);

    }

    @SuppressWarnings({"NoopMethodInAbstractClass", "WeakerAccess", "UnusedParameters"})
    protected void onProgressBarSetup(ProgressBar layoutLoading) {

    }

	/*
		Toast
	 */

    /**
     * Call this method to show a short {@link Toast}
     *
     * @param text Text to show in the {@link Toast}
     *
     * @see BaseView#showLongToast(CharSequence)
     */

    public final void showToast(CharSequence text)
    {
        if(TextUtils.isEmpty(text)
                || getBaseActivity() == null) {
            return;
        }
        Toast.makeText(getBaseActivity(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * Call this method to show a long {@link Toast}
     *
     * @param text Text to show in the {@link Toast}
     *
     * @see BaseView#showToast(CharSequence)
     */

    public final void showLongToast(CharSequence text)
    {
        if(TextUtils.isEmpty(text)
                || getBaseActivity() == null) {
            return;
        }
        Toast.makeText(getBaseActivity(), text, Toast.LENGTH_LONG).show();
    }

    /*
        Helper Methods
     */

    /**
     * Call this method to show a Not Implemented {@link Toast}
     *
     * Note : It is a good practice to show Not Implemented on
     * features that are not yet ready for testing
     *
     */

    public final void notImplemented()
    {
        showToast(getString(R.string.not_implemented));
    }

    /**
     * @see View#findViewById(int)
     */

    @SuppressWarnings("unchecked")
    protected final <T extends View> T findViewById(@IdRes int viewID)
    {
        if(view == null) {
            return null;
        }
        return (T) view.findViewById(viewID);
    }

    public String getString(@StringRes int resID) {

        if(resID < 1 || getBaseActivity() == null) {
            return "";
        }

        return getBaseActivity().getString(resID);
    }

    /*
        Progress Loader
     */

    /**
     * Call this method to show a loader.
     * Make sure to return resource ID of a {@link ProgressBar}
     * object from the {@link BaseView#getProgressBarId()}
     *
     * <br/>Override this method to show a custom loader
     *
     * @see BaseView#hideLoader()
     * @see BaseView#getProgressBarId()
     */

    public void showLoader()
    {
        if (layoutLoading == null)
        {
            return;
        }
        layoutLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Call this method to hide a loader.
     * Make sure to return resource ID of a {@link ProgressBar}
     * object from the {@link BaseView#getProgressBarId()}
     *
     * <br/>Override this method to hide a custom loader
     *
     * @see BaseView#showLoader()
     * @see BaseView#getProgressBarId()
     */

    public void hideLoader()
    {
        if (layoutLoading == null)
        {
            return;
        }

        layoutLoading.setVisibility(View.GONE);
    }

	/*
		Toolbar Setup
	 */

    /**
     * Return a valid resource ID of {@link Toolbar}
     * from this method
     *
     * @see BaseView#onToolBarSetup(Toolbar) ()
     * @see BaseActivity#hasToolbar()
     * @see BaseView#invalidateToolBar()
     */

    @IdRes
    protected int getToolbarId()
    {
        return 0;
    }

    /**
     * Return a valid resource ID of {@link ProgressBar}
     * from the this method to enable the {@link BaseView#showLoader()}
     * and {@link BaseView#hideLoader()} methods
     *
     * @see BaseView#showLoader()
     * @see BaseView#hideLoader()
     */
    @IdRes
    protected int getProgressBarId()
    {
        return 0;
    }

    /**
     * Override this method for setting up {@link Toolbar}
     * <br/> This method will be called once you return {@code true}
     * from {@link BaseActivity#hasToolbar()}
     * <br/> You also need to return a valid resource ID for
     * {@link Toolbar} in {@link BaseView#getToolbarId()}
     *
     * <br/><br/> To invoke this method, you can use {@link BaseView#invalidateToolBar()}
     *
     * @see BaseView#invalidateToolBar() ()
     * @see BaseView#getToolbarId() ()
     * @see BaseActivity#hasToolbar()
     */

    @SuppressWarnings({"NoopMethodInAbstractClass", "UnusedParameters"})
    protected void onToolBarSetup(Toolbar toolBar)
    {
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                controller.getBaseActivity().finish();
            }
        } );
    }

    protected final BaseActivity getBaseActivity() {
        return controller.getBaseActivity();
    }

}
