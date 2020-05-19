package com.machinestalk.stayhome.views;

import android.content.Intent;
import android.view.View;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.LandingActivity;
import com.machinestalk.stayhome.activities.TermsConditionActivity;
import com.machinestalk.stayhome.components.Button;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.views.base.BaseView;


/**
 * Created on 4/4/2017.
 */

public class LandingActivityView extends BaseView {

    private Button mSignInButton;
    private TextView mTermsAndConditionsMessage;


    public LandingActivityView(Controller controller) {

        super(controller);
    }

    @Override
    protected int getViewLayout() {

        return R.layout.layout_landing_activity_new;
    }

    @Override
    protected void onCreate() {

        mSignInButton = findViewById(R.id.view_landing_page_sign_in_button);
        mTermsAndConditionsMessage = findViewById(R.id.terms2_label);
    }


    @Override
    protected void setActionListeners() {

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((LandingActivity) controller).navigateToLogin();

            }
        });
        mTermsAndConditionsMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToTermConditionActivity();
            }
        });


    }

    public void navigateToTermConditionActivity() {
        Intent intent = new Intent(getBaseActivity(), TermsConditionActivity.class);
        getBaseActivity().startActivity(intent);
    }



    public void showProgress() {

//        if (mProgress == null)
//            return;
//
//        mProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {

//        if (mProgress == null)
//            return;
//
//        mProgress.setVisibility(View.GONE);
    }
}
