package com.machinestalk.stayhome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.views.EmergencyFragmentView;


/**
 * Created on 12/20/2016.
 */
public class EmergencyFragment extends BaseFragment implements ServiceSecondaryEventHandler {



    @Override
    protected BaseView getViewForController(Controller controller) {
        return new EmergencyFragmentView(controller);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.guidance_emergency_contact);
    }


    @Override
    public void willStartCall() {

    }

    @Override
    public void didFinishCall(boolean isSuccess) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

}

