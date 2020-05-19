package com.machinestalk.stayhome.views;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.components.Button;
import com.machinestalk.stayhome.views.base.BaseView;
/**
 * Created on 12/20/2016.
 */
public class EmergencyFragmentView extends BaseView {

    private Toolbar toolBar;
    private Button mCallButton;

    public EmergencyFragmentView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.view_emergency_activity;
    }

    @Override
    protected void onCreate() {
        init();
        try {
            ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_emergency), false);
        } catch (Exception e) {
        }

    }

    private void init() {

        mCallButton = findViewById(R.id.view_emergency_call_button);
    }

    @Override
    protected void setActionListeners() {

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_DIAL);
                sendIntent.setData(Uri.parse("tel:" + "937"));
                getBaseActivity().startActivity(sendIntent);
            }
        });
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected void onToolBarSetup(Toolbar toolBar) {
        super.onToolBarSetup(toolBar);
        this.toolBar = toolBar;
    }
}
