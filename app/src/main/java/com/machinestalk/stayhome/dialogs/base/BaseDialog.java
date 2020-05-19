package com.machinestalk.stayhome.dialogs.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

/**
 * Created on 9/15/16.
 */
public abstract class BaseDialog extends DialogFragment {

    /*
        Life  cycle Methods
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getLayout(), container);
        initView(view);
        return view;
    }

    protected abstract void initView(View view);
    protected abstract int getLayout() ;
    public String getTagText() {
        return getClass().getSimpleName();
    }


}
