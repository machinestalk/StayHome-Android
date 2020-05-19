package com.machinestalk.stayhome.views;


import android.view.View;

import com.machinestalk.android.components.ImageView;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.responses.Configuration;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.base.BaseView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class HomeFragmentView extends BaseView {

    TextView title, description;
    ImageView remainingDaysImageView;
    private View mProgress;

    public HomeFragmentView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onCreate() {
        title = findViewById(R.id.title_quarantine);
        description = findViewById(R.id.desc_quarantine);
        remainingDaysImageView = findViewById(R.id.remaining_day_img);
        mProgress = findViewById(R.id.view_fragment_home_progress);
        try {
            ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_home), false);
        } catch (Exception e) {
        }



    }

    @Override
    protected void setActionListeners() {

    }

    public void checkRemainingDays( int remainingDays){
        String mCurrentDate = Util.getCurrentDate();
        //int remainingDays = 14 - Integer.parseInt(Util.getDifferenceBtwDates(AppConfig.getInstance().getUser().getDateSignup(), mCurrentDate)) ;

        Configuration configuration = AppDatabase.getInstance(getBaseActivity()).getConfigurationDao().getconfbyKey("Day " + remainingDays);

            switch (remainingDays) {
                case 1:
                    remainingDaysImageView.setImageResource(R.drawable.day1);
                    break;
                case 2:
                    remainingDaysImageView.setImageResource(R.drawable.day2);
                    break;
                case 3:
                    remainingDaysImageView.setImageResource(R.drawable.day3);
                    break;
                case 4:
                    remainingDaysImageView.setImageResource(R.drawable.day4);
                    break;
                case 5:
                    remainingDaysImageView.setImageResource(R.drawable.day5);
                    break;
                case 6:
                    remainingDaysImageView.setImageResource(R.drawable.day6);
                    break;
                case 7:
                    remainingDaysImageView.setImageResource(R.drawable.day7);
                    break;
                case 8:
                    remainingDaysImageView.setImageResource(R.drawable.day8);
                    break;
                case 9:
                    remainingDaysImageView.setImageResource(R.drawable.day9);
                    break;
                case 10:
                    remainingDaysImageView.setImageResource(R.drawable.day10);
                    break;
                case 11:
                    remainingDaysImageView.setImageResource(R.drawable.day11);
                    break;
                case 12:
                    remainingDaysImageView.setImageResource(R.drawable.day12);
                    break;
                case 13:
                    remainingDaysImageView.setImageResource(R.drawable.day13);
                    break;
                case 14:
                    remainingDaysImageView.setImageResource(R.drawable.day14);
                    break;

                default:
                    break;
            }

        if (configuration != null && !StringUtility.isEmptyOrNull(configuration.getBody()) && !StringUtility.isEmptyOrNull(configuration.getTitle())) {
            title.setText("\""+ configuration.getTitle()+"\"");
            description.setText(configuration.getBody());
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        //checkRemainingDays();
    }

    public void showProgress() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(VISIBLE);
    }

    public void hideProgress() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(GONE);
    }
}
