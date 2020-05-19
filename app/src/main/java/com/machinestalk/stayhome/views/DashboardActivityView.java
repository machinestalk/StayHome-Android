package com.machinestalk.stayhome.views;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.components.SlideMenuItemView;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.fragments.BaseFragment;
import com.machinestalk.stayhome.fragments.BraceletFragment;
import com.machinestalk.stayhome.fragments.CheckInFragment;
import com.machinestalk.stayhome.fragments.EmergencyFragment;
import com.machinestalk.stayhome.fragments.HomeFragment;
import com.machinestalk.stayhome.fragments.HouseHolderDashboardFragment;
import com.machinestalk.stayhome.fragments.SupportFragment;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.base.BaseView;

import java.util.ArrayList;

import static com.machinestalk.stayhome.views.HouseHolderDashboardView.MY_LOCATION_NAME;


/**
 * Created on 12/21/2016.
 */
public class DashboardActivityView extends BaseView implements SlideMenuItemView.OnSlideMenuItemClickListener {

    public static final int INDEX_HOUSE_HOLDER_FRAGMENT = 1;
    public static final int INDEX_HOME_FRAGMENT = 0;
    public static final int INDEX_RIDER_DASHBOARD_FRAGMENT = 2;
    public static final int INDEX_EMPTY_FRAGMENT = 5;
    public static final int INDEX_DRIVER_DASHBOARD_FRAGMENT = 7;

    private DrawerLayout dlDashboard;
    private Toolbar toolBar;
    private TextView txtVersion;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private LinearLayout profileLayout;
    private TextView txtUserName;
    private FrameLayout container;
    private ArrayList<BaseFragment> fragments;
    private int currentFragmentIndex;
    private SlideMenuItemView mDashboardSlideMenuItem;
    private SlideMenuItemView mCheckInSlideMenuItem;
    private SlideMenuItemView mBraceletSlideMenuItem;
    private SlideMenuItemView mHomeSlideMenuItem;
    private SlideMenuItemView mLogoutSlideMenuItem;
    private SlideMenuItemView mAboutUsSlideMenuItem;
    private SlideMenuItemView mEmergencySlideMenuItem;
    private SlideMenuItemView mSupportSlideMenuItem;
    private boolean isComeFromRoadAssistance = false;
    private LinearLayout mLanguageLinearLayout;

    private View mProgressView;

    public DashboardActivityView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {

        return R.layout.view_dashboard_activity;
    }

    @Override
    protected void onCreate() {

        addFragmentsInArrayList();
        initUI();
        setData();
//        ((DashboardActivity) controller).getConfiguration();
    }


    private void initUI() {
        dlDashboard = findViewById(R.id.dlDashboard);
        txtVersion = findViewById(R.id.txtVersion);
        profileLayout = findViewById(R.id.profileLay);
        txtUserName = findViewById(R.id.txtUserName);
        container = findViewById(R.id.container);
        mDashboardSlideMenuItem = findViewById(R.id.dashboard_slide_menu_item);
        mCheckInSlideMenuItem = findViewById(R.id.check_in_slide_menu_item);
        mBraceletSlideMenuItem = findViewById(R.id.bracelet_slide_menu_item);
        mHomeSlideMenuItem = findViewById(R.id.home_slide_menu_item);
        mLogoutSlideMenuItem = findViewById(R.id.logout_slide_menu_item);
        mEmergencySlideMenuItem = findViewById(R.id.emergency_slide_menu_item);
        mSupportSlideMenuItem = findViewById(R.id.support_slide_menu_item);
        mLanguageLinearLayout = findViewById(R.id.view_header_main_language_selector_container);
        mProgressView = findViewById(R.id.progress);
    }

    private void setDotViewvisibility(boolean visibility) {
        // mSubscriptionSlideMenuItem.getmIconDeviceStatusMenuImageView().setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void setData() {
        User user = AppConfig.getInstance().getUser();
        String url = "";
        txtVersion.setText(getString(R.string.Gen_Gen_lbl_version_app) + "  1.0.0 ");

        mDashboardSlideMenuItem.setOnSwitchListener(this);
        mCheckInSlideMenuItem.setOnSwitchListener(this);
        mBraceletSlideMenuItem.setOnSwitchListener(this);
        mHomeSlideMenuItem.setOnSwitchListener(this);
        mLogoutSlideMenuItem.setOnSwitchListener(this);

        mEmergencySlideMenuItem.setOnSwitchListener(this);
        mSupportSlideMenuItem.setOnSwitchListener(this);
    }


    @Override
    protected void onToolBarSetup(Toolbar toolBar) {

        super.onToolBarSetup(toolBar);
        this.toolBar = toolBar;

    }

    @Override
    protected int getToolbarId() {

        return R.id.toolbar;
    }

    @Override
    protected void setActionListeners() {

        mLanguageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.getInstance().getLanguage().equals(AppConstants.LANGUAGE_LITERAL_ENGLISH)) {
                    Util.showConfirmationLanguageDialog(getBaseActivity(), AppConstants.LANGUAGE_LITERAL_ARABIC);
                } else {
                    Util.showConfirmationLanguageDialog(getBaseActivity(), AppConstants.LANGUAGE_LITERAL_ENGLISH);
                }
            }
        });

    }

    @Override
    protected void onPostInitialize() {

        super.onPostInitialize();
        showDashboards();
        setupNavigationDrawer();
    }

    public void setupNavigationDrawer() {

        actionBarDrawerToggle = new ActionBarDrawerToggle(controller.getBaseActivity(), dlDashboard, toolBar,
                R.string.ApDr_ApDr_lbl_open_drawer, R.string.ApDr_Gen_lbl_close_drawer) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
                Util.hideSoftKeyboard(getBaseActivity());
            }
        };

        dlDashboard.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);

        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_dashboard_menu);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlDashboard.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

            }
        });

    }

    public boolean closeDrawer() {

        if (dlDashboard.isDrawerOpen(GravityCompat.START)) {
            dlDashboard.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }



    public void openDrawer() {
        dlDashboard.openDrawer(GravityCompat.START);
    }

    public boolean isDrawerOpened() {
        return dlDashboard.isDrawerOpen(GravityCompat.START) ;
    }


    public FrameLayout getContainer() {

        return container;
    }

    private void showDashboards() {
        switchToRoleDashboard(null);
    }

    public void showHome() {

        showHomeDashboardFragment(null);
    }



    public void switchToRoleDashboard(Bundle args) {

        int roleEntity = 5;

        switch (roleEntity) {

            case AppConstants.ROLE_HOUSE_HOLDER:
                ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new HouseHolderDashboardFragment(), R.string.ApDr_ApDr_SBtn_zones);
                break;

            case AppConstants.ROLE_DRIVER:
                ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new HouseHolderDashboardFragment(), R.string.ApDr_ApDr_SBtn_zones);
                break;

            case AppConstants.ROLE_RIDER:
                if (!AppConfig.getInstance().getUser().isANewDevice()){
                    showHomeDashboardFragment();
                }else {
                    ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new HouseHolderDashboardFragment(), R.string.ApDr_ApDr_SBtn_zones);

                }
                break;

            default:
                showEmptyFragment();
        }
    }


    @Override
    public void setTitle(CharSequence title) {

        super.setTitle(title);
    }

    private void onSelectFragment(int tag, Bundle args) {

        mDashboardSlideMenuItem.setItemSelected(false);
        this.currentFragmentIndex = tag;
        BaseFragment fragment = this.fragments.get(tag);
        setFragmentOnLayout(fragment, tag, args);
    }


    public static void replaceFragment(FragmentManager fragmentManager, BaseFragment fragment, Boolean addToBackStack, Bundle args) {

        String fragmentTag = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(fragmentTag, 0);
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);

        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass()))
            return;

        if (args != null) {

            fragment.setArguments(args);
        }

        if (addToBackStack && !fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.container, fragment).addToBackStack(fragmentTag) // was
                    // 'backStateName'
                    .commit();
        } else {
            if (!addToBackStack)
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).disallowAddToBackStack().commit();
        }

    }

    public void setFragmentOnLayout(final BaseFragment fragment, int tag, Bundle args) {

        FragmentManager manager = ((DashboardActivity) controller).getSupportFragmentManager();

        if (fragment != null) {

            ConnectedCar.getInstance().setLastVisibleFragment(fragment);

            fragment.setOnFragmentAttachedListener(new BaseFragment.OnFragmentAttachedListener() {

                @Override
                public void onAttached(BaseFragment baseFragment) {

                    setTitle(fragment.getActionBarTitle());
                    setupNavigationDrawer();
                }

                @Override
                public void onCreateView(BaseFragment baseFragment) {

                    setTitle(fragment.getActionBarTitle());
                }

            });

            replaceFragment(manager, fragment, true, args);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    closeDrawer();
                }
            }, 100);
        }

    }


    private void addFragmentsInArrayList() {

        this.fragments = new ArrayList<>();
        this.fragments.add(getHomeFragment());
        //        this.fragments.add(getHouseHolderFragment());

        // this.fragments.add(getLiveTrackingFragment());
        //this.fragments.add(getRideCalendar());
        // this.fragments.add(getEmptyFragment());

    }

    private BaseFragment getHouseHolderFragment() {

        HouseHolderDashboardFragment fragment = new HouseHolderDashboardFragment();
        return fragment;
    }
    private BaseFragment getHomeFragment() {

        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public void showHomeDashboardFragment(Bundle args) {

        onSelectFragment(INDEX_HOME_FRAGMENT, args);
    }


    public void showHouseholderDashboardFragment(Bundle args) {

        onSelectFragment(INDEX_HOUSE_HOLDER_FRAGMENT, args);
    }

    public void showDriverDashboardFragment() {

        onSelectFragment(INDEX_DRIVER_DASHBOARD_FRAGMENT, null);

    }


    public void showHomeDashboardFragment() {

        onSelectFragment(INDEX_HOME_FRAGMENT, null);
    }

    public void showEmptyFragment() {

        onSelectFragment(INDEX_EMPTY_FRAGMENT, null);
    }


    public DrawerLayout getDlDashboard() {
        return dlDashboard;
    }


    public Toolbar getToolbar() {

        return toolBar;
    }

    public ArrayList<BaseFragment> getFragments() {

        return fragments;
    }


    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    @Override
    public void onItemClick(String item, boolean openFragment) {

        if (item.equals(getString(R.string.ApDr_ApDr_SBtn_home))) {
            mDashboardSlideMenuItem.setItemSelected(false);
            mCheckInSlideMenuItem.setItemSelected(false);
            mBraceletSlideMenuItem.setItemSelected(false);
            mHomeSlideMenuItem.setItemSelected(true);
            mLogoutSlideMenuItem.setItemSelected(false);
            mEmergencySlideMenuItem.setItemSelected(false);
            mSupportSlideMenuItem.setItemSelected(false);
            if (openFragment) {
                ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new HomeFragment(), R.string.ApDr_ApDr_SBtn_home);
            }
        }
        else if (item.equals(getString(R.string.ApDr_ApDr_SBtn_zones))) {
            PreferenceUtility.getInstance(getBaseActivity()).putBoolean(MY_LOCATION_NAME, false);
            mDashboardSlideMenuItem.setItemSelected(true);
            mCheckInSlideMenuItem.setItemSelected(false);
            mBraceletSlideMenuItem.setItemSelected(false);
            mHomeSlideMenuItem.setItemSelected(false);
            mLogoutSlideMenuItem.setItemSelected(false);
            mEmergencySlideMenuItem.setItemSelected(false);
            mSupportSlideMenuItem.setItemSelected(false);
            isComeFromRoadAssistance = false;
            if (openFragment)
                ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new HouseHolderDashboardFragment(), R.string.ApDr_ApDr_SBtn_zones);

        } else if (item.equals(getString(R.string.ApDr_ApDr_SBtn_support))) {
            PreferenceUtility.getInstance(getBaseActivity()).putBoolean(MY_LOCATION_NAME, false);
            mDashboardSlideMenuItem.setItemSelected(false);
            mCheckInSlideMenuItem.setItemSelected(false);
            mBraceletSlideMenuItem.setItemSelected(false);
            mHomeSlideMenuItem.setItemSelected(false);
            mLogoutSlideMenuItem.setItemSelected(false);
            mEmergencySlideMenuItem.setItemSelected(false);
            mSupportSlideMenuItem.setItemSelected(true);
            isComeFromRoadAssistance = false;

            if (openFragment) {
                ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new SupportFragment(), R.string.ApDr_ApDr_SBtn_support);
            }

        } else if (item.equals(getString(R.string.ApDr_ApDr_SBtn_emergency))) {
            PreferenceUtility.getInstance(getBaseActivity()).putBoolean(MY_LOCATION_NAME, false);
            mDashboardSlideMenuItem.setItemSelected(false);
            mCheckInSlideMenuItem.setItemSelected(false);
            mBraceletSlideMenuItem.setItemSelected(false);
            mHomeSlideMenuItem.setItemSelected(false);
            mLogoutSlideMenuItem.setItemSelected(false);
            mEmergencySlideMenuItem.setItemSelected(true);
            mSupportSlideMenuItem.setItemSelected(false);
            isComeFromRoadAssistance = false;

            if (openFragment) {
                ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new EmergencyFragment(), R.string.ApDr_ApDr_SBtn_emergency);
            }

        } else if(item.equals(getString(R.string.ApDr_ApDr_SBtn_check_in))) {

            mDashboardSlideMenuItem.setItemSelected(false);
            mCheckInSlideMenuItem.setItemSelected(true);
            mBraceletSlideMenuItem.setItemSelected(false);
            mHomeSlideMenuItem.setItemSelected(false);
            mLogoutSlideMenuItem.setItemSelected(false);
            mEmergencySlideMenuItem.setItemSelected(false);
            mSupportSlideMenuItem.setItemSelected(false);
            ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new CheckInFragment(), R.string.ApDr_ApDr_SBtn_check_in);
        }

        else if(item.equals(getString(R.string.ApDr_ApDr_SBtn_bracelet))) {

            mDashboardSlideMenuItem.setItemSelected(false);
            mCheckInSlideMenuItem.setItemSelected(false);
            mBraceletSlideMenuItem.setItemSelected(true);
            mHomeSlideMenuItem.setItemSelected(false);
            mLogoutSlideMenuItem.setItemSelected(false);
            mEmergencySlideMenuItem.setItemSelected(false);
            mSupportSlideMenuItem.setItemSelected(false);
            ((DashboardActivity) controller.getBaseActivity()).navigateToFragment(new BraceletFragment(), R.string.ApDr_ApDr_SBtn_bracelet);
        } else if (item.equals(getString(R.string.ApDr_ApDr_SBtn_logout))) {
            ((DashboardActivity) controller.getBaseActivity()).onLogout();

        }
    }

    /**
     *
     */
    public void showProgress() {
        if (mProgressView == null)
            return;

        mProgressView.setVisibility(View.VISIBLE);
    }

    /**
     *
     */
    public void hideProgress() {
        if (mProgressView == null)
            return;

        mProgressView.setVisibility(View.GONE);
    }

    public void setDeviceMenuVisible(boolean visible) {
        //  mMotornaWifiSlideMenuItem.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
