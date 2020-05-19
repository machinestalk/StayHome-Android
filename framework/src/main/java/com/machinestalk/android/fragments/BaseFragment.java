package com.machinestalk.android.fragments;


import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.machinestalk.android.activities.BaseActivity;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.service.ServiceFactory;
import com.machinestalk.android.views.BaseView;

import static com.machinestalk.android.activities.BaseActivity.isIsMovingToAnotherActivity;

/**
 * {@link Fragment}'s subclass, provides a base for implementing your fragments.
 * Always inherit from this class when using fragments.
 *
 * <br/> The class can be used as {@code Controller} for {@code Views}
 */

public abstract class BaseFragment extends Fragment implements Controller
{
    @SuppressWarnings("WeakerAccess")
    protected ServiceFactory serviceFactory;
    @SuppressWarnings("WeakerAccess")
    protected BaseView view;
    private Snackbar snackbar;

    /*
        Life  cycle Methods
     */



    /**
     * A call to {@code super.onCreateView()} is necessary if
     * you override this method. It sets up the fragment and has
     * other boiler plate code
     *
     * @see Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = getViewForController(this);
        View containerView = view.getView();

        initializeServiceFactory();
        view.initialize();

        return containerView;
    }

    @Override
    public final BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    /**
     * This method is used to set default {@link androidx.appcompat.app.ActionBar}
     * title.
     *
     * @return Return title {@link String}
     * @see BaseFragment#hasToolbar()
     * @see BaseFragment#invalidateToolBar()
     */

    @Override
    public String getActionBarTitle() {
        return "";
    }

    /**
     * This method is used to know if {@link Fragment} has its own
     * {@link Toolbar}
     *
     * @return Return {@code true} if this {@link Fragment} implements its on {@link Toolbar}
     * @see BaseFragment#invalidateToolBar()
     * @see BaseFragment#getActionBarTitle()
     */

    @Override
    public boolean hasToolbar() {
        return false;
    }

    /**
     * Call this method when you want to refresh
     * {@link Toolbar}
     *
     * @see BaseFragment#hasToolbar()
     * @see BaseFragment#getActionBarTitle()
     */

    protected final void invalidateToolBar()
    {
        view.invalidateToolBar();
    }


    @Override
    public void startActivity(Intent intent)
    {
        BaseActivity.setIsMovingToAnotherActivity (true);
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode)
    {
        BaseActivity.setIsMovingToAnotherActivity(true) ;
        super.startActivityForResult(intent, requestCode);
    }

    private void initializeServiceFactory()
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

    /**
     * This method must return an object of a subclass of
     * {@link BaseView}. This view will bind to this {@link Fragment} through
     * {@link Controller} interface
     *
     * @param controller an implementation of {@link Controller} interface
     * @return an object of a subclass of {@link BaseView}
     */

    protected abstract BaseView getViewForController(Controller controller);

    /**
     * Call this method to show a loader
     *
     */
    protected void showLoader()
    {
        view.showLoader();
    }

    /**
     * Call this method to hide a loader
     *
     */

    protected void hideLoader()
    {
        view.hideLoader();
    }

    /**
     * Call this method to show a short {@link android.widget.Toast}
     *
     * @param text Text to show in the {@link android.widget.Toast}
     *
     * @see BaseActivity#showLongToast(CharSequence)
     */

    protected final void showToast(CharSequence text)
    {
        view.showToast(text);
    }


    /**
     * Call this method to show a long {@link android.widget.Toast}
     *
     * @param text Text to show in the {@link android.widget.Toast}
     *
     * @see BaseActivity#showToast(CharSequence)
     */
    protected final void showLongToast(CharSequence text)
    {
        view.showLongToast(text);
    }

    /**
     * A call to {@code super.onResume()} is necessary if
     * you override this method
     *
     * @see Fragment#onResume()
     */

    @Override
    public void onResume()
    {
        super.onResume();
        view.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        view.onPause();
        if(snackbar == null) {
            return;
        }

        snackbar.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view.onDestroy();

    }
    @Override
    public final View getView() {
        return view.getView();
    }

    @Override
    public void setSnackbar(Snackbar snackBar) {
        snackbar = snackBar;
    }
}
