package com.machinestalk.android.interfaces;

import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import com.machinestalk.android.activities.BaseActivity;

/**
 * An interface to be implemented by class
 * that wish to act as Controllers for {@code Views}
 *
 * @see com.machinestalk.android.views.BaseView
 * @see BaseActivity
 * @see com.machinestalk.android.fragments.BaseFragment
 */
public interface Controller {

    BaseActivity getBaseActivity();
    String getActionBarTitle();
    boolean hasToolbar();
    View getView();
    void setSnackbar(Snackbar snackBar);
}
