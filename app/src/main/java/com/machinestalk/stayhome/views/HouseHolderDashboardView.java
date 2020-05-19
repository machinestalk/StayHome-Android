package com.machinestalk.stayhome.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.activities.FaceRegistrationActivity;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.components.Button;
import com.machinestalk.stayhome.components.FabLayout;
import com.machinestalk.stayhome.components.MyMapTracking;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.dialogs.AlertBottomDialog;
import com.machinestalk.stayhome.entities.TrackingInfoEntity;
import com.machinestalk.stayhome.entities.ZoneEntity;
import com.machinestalk.stayhome.fragments.HouseHolderDashboardFragment;
import com.machinestalk.stayhome.helpers.NotificationHelper;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;
import com.machinestalk.stayhome.listeners.OnMapReadyListener;
import com.machinestalk.stayhome.listeners.PermissionGrantedListener;
import com.machinestalk.stayhome.service.body.UserLocationBody;
import com.machinestalk.stayhome.service.body.UserStatusBody;
import com.machinestalk.stayhome.utils.BitmapUtil;
import com.machinestalk.stayhome.utils.LocationManager;
import com.machinestalk.stayhome.utils.LocationUtils;
import com.machinestalk.stayhome.utils.PermissionUtils;
import com.machinestalk.stayhome.utils.RxPermissionUtil;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.base.BaseView;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created on 1/19/2017.
 */

public class HouseHolderDashboardView extends BaseView implements PermissionGrantedListener
        , DashboardActivity.onMainCallbackListener {
    public static final String MY_LOCATION_NAME = "myLocation";
    public static final String ALL_VEHICLE_NAME = "allVehicle";
    public static final int MIN_RADIUS = 10;
    public static final int MAX_RADIUS = 120;
    public static final int MAX_ACCURACY = 120;

    /************Create geofences *******************/

    private Toolbar toolBar;
    Marker mk = null;
    private View progress;
    private FabLayout mFabLayout;
    private TextView txtRadius;
    private MyMapTracking mMapTracking;
    private ToggleButton mMapTrackingLocationButton;
    private boolean isMapReady = false;
    private boolean isPermissionDenied = false;
    private boolean isPermissionAllowed = false;
    private boolean isFirstTime = true;
    private boolean isMessageZoneOutShown = false;
    private boolean isMessageZoneInShown = false;
    private boolean isDragged = false;
    private float mDistanceZoneIn = 0;

    private ZoneEntity zoneEntity;
    public static List<Geofence> mGeofenceList = new ArrayList<>();
    private Circle mGeoFenceCircleLimits;
    private Polyline radiusLine;
    private Marker geoFenceMarker;
    //    private Marker currentMarker;
    private LatLng currentLocation;
    private Marker radiusEndMarker;
    private int endMarkerRadius;
    private LatLng fallbackLatLng;

    private TextView txtAddress;
    Button mStartButton;
    Button mNextButton;
    Button mContactUsButton;
    RelativeLayout mFirstSignUpRelativeLayout;
    RelativeLayout mSelfQuestionsRelativeLayout;

    private EventBus myEventBus = EventBus.getDefault();
    private CountDownTimer downTimer;
    private CountDownTimer downLocationSuccessfullyTimer;
    private int currentSecondValue = 0;
    private static int countpaquets = 0;
    private static String lastPaquet = "inZone";


    private SwitchCompat mCoughSwitchCompat;
    private SwitchCompat mHeadacheSwitchCompat;
    private SwitchCompat mFeverSwitchCompat;
    private SwitchCompat mBreathSwitchCompat;
    private SwitchCompat mNoSufferSwitchCompat;

    private LinearLayout mFirstStepMessageLinearLayout;
    private LinearLayout mConfirmationLocationMessageLinearLayout;
    private LinearLayout mViewDashboardWrongLocationLayout;
    private LinearLayout mLocationSavedMessageLinearLayout;
    private Button mFirstStepMessageNextButton;
    private Button mConfirmationLocationYesButton;
    private Button mConfirmationLocationNoButton;
    private Button mLocationSavedNextButton;
    private LatLng mSelectedLocation;
    private AppCompatImageView mImgStep;
    private ToggleButton mLocationButton;
    private LinearLayout view_dashboard_user_location_layout;


    public HouseHolderDashboardView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.view_house_holder_dashboard;
    }

    @Override
    protected void onCreate() {
        initUI();
        ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_zones), false);
        ((DashboardActivity) controller.getBaseActivity()).setonCallbackListener(this);
        ((BaseActivity) getBaseActivity()).setPermissionGrantedListener(this);

//        startTimer();

        if (!AppConfig.getInstance().getUser().isANewDevice()) {
            getBaseActivity().getSupportActionBar().setTitle(getBaseActivity().getString(R.string.ApDr_ApDr_SBtn_zones));
            mImgStep.setVisibility(GONE);
            mViewDashboardWrongLocationLayout.setVisibility(VISIBLE);
        } else {
            getBaseActivity().getSupportActionBar().setTitle(getBaseActivity().getString(R.string.HHD_HHDh_first_sign_up_screen_title));
            mLocationButton.setVisibility(VISIBLE);
            view_dashboard_user_location_layout.setVisibility(VISIBLE);
            AppDatabase.getInstance(getBaseActivity()).getZonesDao().deleteAllZone();
            mViewDashboardWrongLocationLayout.setVisibility(GONE);
            mImgStep.setVisibility(VISIBLE);
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
//        new Handler().postDelayed(() -> {
//            if (LocationManager.isLocationEnabled(getBaseActivity())){
//                isPermissionAllowed = true ;
//
//            }else {
//                askLocationPermission();
//
//            }
//        }, 1000);

    }

    private void askLocationPermission (){
        android.location.LocationManager lm = (android.location.LocationManager) getBaseActivity().getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {

            final AlertBottomDialog locationDialog = new AlertBottomDialog(getBaseActivity());
            locationDialog.setOnClickAlertListener(new OnClickAlertListener() {
                @Override
                public void onAlertClick() {
                    locationDialog.dismiss();
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getBaseActivity().startActivity(intent);

                }
            });

            locationDialog.setCancelable(false);
            locationDialog.create();
            locationDialog.setIvAvatar(R.drawable.red_wifi);
            locationDialog.setTextButton("OK");
            locationDialog.setTvTitle("Location Services Not Active");
            locationDialog.setTvSubTitle("Please enable Location Services and GPS");
            locationDialog.setTextButtonVisibility(true);
            locationDialog.setCanceledOnTouchOutside(true);
            if (getBaseActivity() != null && !getBaseActivity().isFinishing() && !locationDialog.isShowing())
                locationDialog.show();

        }

    }
        private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(getBaseActivity());
        RxPermissionUtil.getInstance().checkRxPermission(rxPermissions, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        ,Manifest.permission.ACCESS_COARSE_LOCATION},
                new RxPermissionUtil.onPermissionListener() {
                    @Override
                    public void onPermissionAllowed() {
                        if (LocationManager.isLocationEnabled(getBaseActivity()))
                        isPermissionAllowed = true ;
                    }

                    @Override
                    public void onPermissionDenied() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!getBaseActivity ().shouldShowRequestPermissionRationale (Manifest.permission.READ_CONTACTS)) {

                            }
                        }
                    }
                });
    }


    @Override
    protected void setTitle(CharSequence title) {
        super.setTitle(title);
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

    private void initUI() {

        mFabLayout = findViewById(R.id.view_dashboard_fab_Layout);
        progress = findViewById(R.id.progress);
        mMapTracking = findViewById(R.id.view_dashboard_map_tracking);
        mMapTrackingLocationButton = findViewById(R.id.toggleLocation);
        txtAddress = findViewById(R.id.txtAddress);
        txtRadius = findViewById(R.id.txtRadius);

        mFirstSignUpRelativeLayout = findViewById(R.id.view_dashboard_first_sign_up);
        mSelfQuestionsRelativeLayout = findViewById(R.id.view_dashboard_self_questions);

        mStartButton = findViewById(R.id.view_first_sign_up_save_button);
        mNextButton = findViewById(R.id.view_self_question_next_button);
        mContactUsButton = findViewById(R.id.view_dashboard_contact_us_button);

        mCoughSwitchCompat = findViewById(R.id.view_self_question_cough_switch);
        mHeadacheSwitchCompat = findViewById(R.id.view_self_question_headache_switch);
        mFeverSwitchCompat = findViewById(R.id.view_self_question_fever_switch);
        mBreathSwitchCompat = findViewById(R.id.view_self_question_breath_switch);
        mNoSufferSwitchCompat = findViewById(R.id.view_self_question_no_suffer_switch);
        mFirstStepMessageLinearLayout = findViewById(R.id.view_dashboard_first_step_layout);
        mFirstStepMessageNextButton = findViewById(R.id.view_dashboard_first_step_next_button);
        mConfirmationLocationMessageLinearLayout = findViewById(R.id.view_dashboard_confirm_location_layout);
        mViewDashboardWrongLocationLayout = findViewById(R.id.view_dashboard_wrong_location_layout);
        mConfirmationLocationYesButton = findViewById(R.id.view_dashboard_confirm_location_yes_button);
        mConfirmationLocationNoButton = findViewById(R.id.view_dashboard_confirm_location_no_button);
        mLocationSavedMessageLinearLayout = findViewById(R.id.view_dashboard_location_saved_layout);
        mLocationSavedNextButton = findViewById(R.id.view_dashboard_location_saved_next_button);
        mImgStep = findViewById(R.id.img_step);
        mLocationButton = findViewById(R.id.view_dashboard_user_location);
        view_dashboard_user_location_layout = findViewById(R.id.view_dashboard_user_location_layout);


        if (mMapTracking != null) {
            mMapTracking.setController(controller);
        }

        initMap();
        initFab();
    }

    @Override
    protected void onPostInitialize() {
        super.onPostInitialize();
    }

    /**
     *
     */

    private void initMap() {
        if (mMapTracking == null) {
            return;
        }
        mMapTracking.setToggleLocationVisible();
        mMapTracking.setFabVisibilityGone();
        mMapTracking.setShouldMoveToCurrentLocation(false);
        mMapTracking.setMapStateListener(new MyMapTracking.onMapGestureChangeListener() {

            @Override
            public void movedByUser() {
                mMapTrackingLocationButton.setChecked(false);
                mLocationButton.setChecked(false);
            }

            @Override
            public void movedByApp() {
                Log.d(getClass().getName(), "onCameraIdle: ");

            }

            @Override
            public void movedByCameraTap() {
                Log.d(getClass().getName(), "onCameraIdle: ");

            }

            @Override
            public void onMapRefresh() {
                Log.d(getClass().getName(), "onCameraIdle: ");
            }


            @Override
            public void toggleLocationClicked() {
                mMapTracking.getGoogleMap().setMyLocationEnabled(false);
                mMapTracking.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(true);
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setActionListeners() {

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstSignUpRelativeLayout.setVisibility(GONE);
                mSelfQuestionsRelativeLayout.setVisibility(VISIBLE);
            }
        });

        mContactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_support), true);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LocationManager.isLocationEnabled(getBaseActivity())){
                    if (!mCoughSwitchCompat.isChecked() && !mHeadacheSwitchCompat.isChecked() && !mBreathSwitchCompat.isChecked() &&
                            !mFeverSwitchCompat.isChecked() && !mNoSufferSwitchCompat.isChecked()) {
                        showLongToast("Please select one item");
                        return;
                    }

                    mFirstSignUpRelativeLayout.setVisibility(GONE);
                    mSelfQuestionsRelativeLayout.setVisibility(GONE);
                    List<ZoneEntity> list = AppDatabase.getInstance(getBaseActivity()).getZonesDao().getAll();
                    if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).getCenter() != null && isMapReady) {
                        mMapTracking.setDrawnCircle(null);
                        zoneEntity = list.get(0);
                        placeMaker(list.get(0).getCenter());
//                    drawZone();
                    } else {
//                    showLongToast("Please Select your zone");
                    }

                    UserStatusBody userStatusBody = new UserStatusBody();
                    if (mCoughSwitchCompat.isChecked()) {
                        userStatusBody.setCough(1);
                    }

                    if (mHeadacheSwitchCompat.isChecked()) {
                        userStatusBody.setHeadache(1);
                    }

                    if (mBreathSwitchCompat.isChecked()) {
                        userStatusBody.setShortBreath(1);
                    }

                    if (mFeverSwitchCompat.isChecked()) {
                        userStatusBody.setFever(1);
                    }

                    if (mNoSufferSwitchCompat.isChecked()) {
                        userStatusBody.setNoSuffer(1);
                    }

                    ((HouseHolderDashboardFragment) controller).sendUserStatus(userStatusBody);

                    //((HouseHolderDashboardFragment) controller).hideToolbar();
                    mLocationButton.setVisibility(VISIBLE);
                    view_dashboard_user_location_layout.setVisibility(VISIBLE);
                    mFirstStepMessageLinearLayout.setVisibility(VISIBLE);
                    mViewDashboardWrongLocationLayout.setVisibility(GONE);
                    placeMaker(currentLocation);
                }else {
                        askLocationPermission();
                }
            }
        });

        mFirstStepMessageNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationConfirmationDialog(mSelectedLocation);
            }
        });

        mConfirmationLocationYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedLocation(mSelectedLocation, "");
                mLocationSavedNextButton.setVisibility(View.INVISIBLE);
                mLocationSavedMessageLinearLayout.setVisibility(VISIBLE);
                startLocationSuccefullyTimer();
                mConfirmationLocationMessageLinearLayout.setVisibility(GONE);
                mViewDashboardWrongLocationLayout.setVisibility(GONE);
                mLocationButton.setVisibility(GONE);
                view_dashboard_user_location_layout.setVisibility(GONE);
            }
        });


        mConfirmationLocationNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGeoFenceCircleLimits != null) {
                    mGeoFenceCircleLimits.remove();
                }

                mConfirmationLocationMessageLinearLayout.setVisibility(GONE);
                mViewDashboardWrongLocationLayout.setVisibility(GONE);
                mFirstStepMessageLinearLayout.setVisibility(VISIBLE);
            }
        });

        mLocationSavedNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationSavedMessageLinearLayout.setVisibility(GONE);
                Intent intent = new Intent(getBaseActivity(), FaceRegistrationActivity.class);
                getBaseActivity().startActivity(intent);
            }
        });

        mNoSufferSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCoughSwitchCompat.setChecked(false);
                    mHeadacheSwitchCompat.setChecked(false);
                    mBreathSwitchCompat.setChecked(false);
                    mFeverSwitchCompat.setChecked(false);
                    setNextButtonStatus(true);
                } else {
                    if (!mCoughSwitchCompat.isChecked() && !mBreathSwitchCompat.isChecked() &&
                            !mFeverSwitchCompat.isChecked() && !mHeadacheSwitchCompat.isChecked()) {

                        setNextButtonStatus(false);
                    }
                }

            }
        });


        mCoughSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    setNextButtonStatus(true);
                } else {
                    if (!mHeadacheSwitchCompat.isChecked() && !mBreathSwitchCompat.isChecked() &&
                            !mFeverSwitchCompat.isChecked() && !mNoSufferSwitchCompat.isChecked()) {

                        setNextButtonStatus(false);
                    }
                }
            }
        });

        mHeadacheSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    setNextButtonStatus(true);
                } else {
                    if (!mCoughSwitchCompat.isChecked() && !mBreathSwitchCompat.isChecked() &&
                            !mFeverSwitchCompat.isChecked() && !mNoSufferSwitchCompat.isChecked()) {

                        setNextButtonStatus(false);
                    }
                }
            }
        });

        mBreathSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    setNextButtonStatus(true);
                } else {
                    if (!mCoughSwitchCompat.isChecked() && !mHeadacheSwitchCompat.isChecked() &&
                            !mFeverSwitchCompat.isChecked() && !mNoSufferSwitchCompat.isChecked()) {

                        setNextButtonStatus(false);
                    }
                }
            }
        });

        mFeverSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    setNextButtonStatus(true);
                } else {
                    if (!mCoughSwitchCompat.isChecked() && !mHeadacheSwitchCompat.isChecked() &&
                            !mBreathSwitchCompat.isChecked() && !mNoSufferSwitchCompat.isChecked()) {

                        setNextButtonStatus(false);
                    }
                }
            }
        });

        mFirstSignUpRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSelfQuestionsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mMapTracking.setFabVisibilityGone();
        mMapTracking.setOnMapReadyListener(new OnMapReadyListener() {
            @Override
            public void onMapReady() {
                isMapReady = true;
                if (mMapTracking.getGoogleMap() != null) {
                    mMapTracking.getGoogleMap().setMyLocationEnabled(false);
                }
                moveToCurrentLocation();
                List<ZoneEntity> zoneEntities = AppDatabase.getInstance(getBaseActivity()).getZonesDao().getAll();
                setupZonesList(zoneEntities);

            }
        });

        mMapTracking.setOnMapLoadedListener(new MyMapTracking.onMapLoadedListener() {
            @Override
            public void onMapLoaded() {

            }
        });

        mMapTracking.setmOnMarkerStartDragListener(new MyMapTracking.onMarkerStartDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                if (geoFenceMarker == null)
                    return;
//
//                radiusEndMarker = marker;
//                Util.setOnVibration(controller, 500);
//
//                // fallback position set onDragStart because marker change position just on long press. even drag have not started yet marker changes its postion.
//                radiusEndMarker.setPosition(fallbackLatLng);

            }
        });


        mMapTracking.setmOnMarkerDragListener(new MyMapTracking.onMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
//                isDragged = true;
//                radiusEndMarker.setPosition(marker.getPosition());
//
//                endMarkerRadius = Math.round(LocationUtils.getDistanceBetween(radiusEndMarker.getPosition(), geoFenceMarker.getPosition()));
//
//                if (isRadiusValid(endMarkerRadius)) {
//                    //drawPolyline(radiusEndMarker);
//                    drawZoneCircle(endMarkerRadius);
//                    setRadius(endMarkerRadius);
//                }
            }
        });


        mMapTracking.setmOnMarkerEndDragListener(new MyMapTracking.onMarkerEndDragListener() {

            @Override
            public void onMarkerDragEnd(Marker marker) {
//                if (isDragged) {
//
//                    if (!isRadiusValid(endMarkerRadius)) {
//                        showToast("Please ensure to choose a valid radius");
//
//                        if (endMarkerRadius > MAX_RADIUS)
//                            endMarkerRadius = MAX_RADIUS;
//
//                        else if (endMarkerRadius < MIN_RADIUS)
//                            endMarkerRadius = MIN_RADIUS;
//
//                        drawMapRadius(geoFenceMarker.getPosition(), null, endMarkerRadius);
//
//                    } else {
//                        drawMapRadius(geoFenceMarker.getPosition(), marker.getPosition(), endMarkerRadius);
//                    }
//                    setRadius(endMarkerRadius);
//                    fillZoneEntity();
//                    isDragged = false;
//                } else {
//                    radiusEndMarker.setPosition(fallbackLatLng);
//                    endMarkerRadius = Math.round(LocationUtils.getDistanceBetween(radiusEndMarker.getPosition(), geoFenceMarker.getPosition()));
//                    drawZoneCircle(endMarkerRadius);
//                }
            }
        });


        mMapTracking.setOnMapClickListener(new MyMapTracking.onMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                if (((HouseHolderDashboardFragment) controller).isZoneExist() && !AppConfig.getInstance().getUser().isANewDevice()) {
                    return;
                }

                if (mFirstStepMessageLinearLayout.isShown()) {
                    placeMaker(latLng);
                } else {
                    placeMaker(latLng);
                    drawZone();
                }


            }
        });


        txtAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ((HouseHolderDashboardFragment) controller).navigateToFavoriteZoneActivity();
            }
        });

        txtAddress.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                return false;
            }
        });

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationButton.isChecked()) {
                    onLocationToggleChecked();
                } else {
                    mMapTracking.moveToCurrentLocation();
                    mLocationButton.setChecked(true);
                    mMapTrackingLocationButton.setChecked(true);
                }
            }
        });


    }

    private void openFaceRegistrationActivity() {
        Intent intent = new Intent(getBaseActivity(), FaceRegistrationActivity.class);
        getBaseActivity().startActivity(intent);
    }

    /**
     *
     */
    private void initFab() {
        ArrayList<FloatingActionButton> floatingActionButtons = new ArrayList<>();
//        floatingActionButtons.add(Util.getFloatingActionButton(controller.getBaseActivity(), KeyConstants.KEY_BUTTON_ZONES, R.string.LMT_Gen_lbl_fab_zone, R.drawable.selector_toggle_zones));
        floatingActionButtons.add(Util.getFloatingActionButton(controller.getBaseActivity(), KeyConstants.KEY_BUTTON_TRAFFIC, R.string.LMT_Gen_lbl_fab_traffic, R.drawable.selector_toggle_traffic));
        floatingActionButtons.add(Util.getFloatingActionButton(controller.getBaseActivity(), KeyConstants.KEY_BUTTON_SATELLITE, R.string.LMT_Gen_lbl_fab_satellite, R.drawable.selector_toggle_satellite));
        mFabLayout.setItems(floatingActionButtons);
        mFabLayout.setOnFabButtonClickListener(new FabButtonClickListener());
    }

    private void startTimer() {

        downTimer = new CountDownTimer(10000, 2000) {


            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                currentSecondValue = 5;
            }
        }.start();
    }
    private void startLocationSuccefullyTimer() {

        downLocationSuccessfullyTimer = new CountDownTimer(3000, 2000) {


            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                mLocationSavedMessageLinearLayout.setVisibility(GONE);
                Intent intent = new Intent(getBaseActivity(), FaceRegistrationActivity.class);
                getBaseActivity().startActivity(intent);
            }
        }.start();
    }

    public void moveToCurrentLocation() {

        if (mMapTracking.getGoogleMap() != null) {
            final Location location = mMapTracking.fetchCurrentLocation();
            mMapTracking.getGoogleMap().setMyLocationEnabled(false);
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                checkYourPrsenceAtHome(null);
            } else {
                isFirstTime = true;
                LocationManager.make(getBaseActivity().getApplicationContext(), new LocationManager.ICurrentLocation() {
                    @Override
                    public void onLocationReceived(final Location address) {
                        TrackingInfoEntity trackingInfoEntity = null;
                        currentLocation = new LatLng(address.getLatitude(), address.getLongitude());
                        if (address.getAccuracy() < MAX_ACCURACY && !AppConfig.getInstance().getUser().isANewDevice()) {
                            if (mGeoFenceCircleLimits != null && geoFenceMarker != null) {
                                LocationManager.saveTrackingInfoEntity(mGeoFenceCircleLimits.getCenter().longitude,
                                        mGeoFenceCircleLimits.getCenter().latitude, address.getLongitude(), address.getLatitude());
                            }
                            if (currentSecondValue == 5 && mGeoFenceCircleLimits != null && mGeoFenceCircleLimits.getCenter() != null) {
                                currentSecondValue = 0;
                                trackingInfoEntity = new TrackingInfoEntity();
                                trackingInfoEntity.setId(1);
                                trackingInfoEntity.setCenterLatitude(mGeoFenceCircleLimits.getCenter().latitude);
                                trackingInfoEntity.setCenterLongitude(mGeoFenceCircleLimits.getCenter().longitude);
                                trackingInfoEntity.setCurrentLatitude(address.getLatitude());
                                trackingInfoEntity.setCurrentLongitude(address.getLongitude());
                                trackingInfoEntity.setBearing(address.getBearing());
                                trackingInfoEntity.setAccuracy(address.getAccuracy());
                                trackingInfoEntity.setSpeed(address.getSpeed());
                                trackingInfoEntity.setProvider(address.getProvider());
                                trackingInfoEntity.setTime(address.getTime());

                                AppDatabase.getInstance(getBaseActivity()).getTrackingInfoDao().insertTrackingInfoEntity(trackingInfoEntity);
                                startTimer();
                            }
                            mLocationButton.setChecked(true);

//                            checkYourPrsenceAtHome(trackingInfoEntity);
                        }
                    }
                }).fetch();

            }
        }
    }

    public void moveToCurrentLocationOnLocationReceived(LatLng latlng) {
        if (latlng != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(17).build();
            mMapTracking.getGoogleMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//            mMapTracking.getGoogleMap().setMyLocationEnabled(false);
            mMapTrackingLocationButton.setChecked(true);
            mLocationButton.setChecked(true);
        }
    }


    private void checkYourPrsenceAtHome(TrackingInfoEntity address) {
         if (mDistanceZoneIn < MAX_RADIUS
                    && !PreferenceUtility.getInstance(getBaseActivity()).getBoolean(PreferenceUtility.IS_MESSAGE_Z_IN_STATUS, false)) {
                if (countpaquets < 10 && !lastPaquet.equals("zoneOut")) {
                    countpaquets++ ;
                }else if (countpaquets == 10){
                    NotificationHelper.createNotificationWithIntent(NotificationHelper.ZONE_IN_NOTIFICATION_ID,
                            "You are in your zone, please stay at Home", getBaseActivity().getApplicationContext());
                    PreferenceUtility.getInstance(getBaseActivity().getApplicationContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_IN_STATUS, true);
                    PreferenceUtility.getInstance(getBaseActivity().getApplicationContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_OUT_STATUS, false);
                    Logger.i("User new location is " + address);
                    Logger.i("in Zone ----> Distance is :" + mDistanceZoneIn);
                    countpaquets = 0 ;
                }else {
                    countpaquets = 0;
                    lastPaquet = "inZone";
                }

            }else if (currentLocation != null && mGeoFenceCircleLimits != null
                && AppConfig.getInstance().getUser().isANewDevice()) {
            mDistanceZoneIn = LocationUtils.getDistanceBetween(currentLocation, mGeoFenceCircleLimits.getCenter());

            if (mDistanceZoneIn > MAX_RADIUS
                    && !PreferenceUtility.getInstance(getBaseActivity()).getBoolean(PreferenceUtility.IS_MESSAGE_Z_OUT_STATUS, false)) {
                if (countpaquets < 10 && !lastPaquet.equals("inZone")) {
                    countpaquets++ ;
                }else if (countpaquets == 10){
                    if (ConnectedCar.isAppInForeground()) {
                        EventBus.getDefault().post(AppConstants.EVENT_ZONE_OUT);
                    } else {
                        NotificationHelper.createNotificationWithIntent(NotificationHelper.ZONE_OUT_NOTIFICATION_ID,
                                "You are out of your zone, please back to your Home", getBaseActivity().getApplicationContext());
                    }
                    PreferenceUtility.getInstance(getBaseActivity().getApplicationContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_IN_STATUS, false);
                    PreferenceUtility.getInstance(getBaseActivity().getApplicationContext()).putBoolean(PreferenceUtility.IS_MESSAGE_Z_OUT_STATUS, true);
//                    ServiceApi.getInstance().sendCompliantStatus(0);
                    Logger.i("User new location is " + address);
                    Logger.i("Out of Zone ----> Distance is :" + mDistanceZoneIn);
                    countpaquets = 0 ;
                }
                else {
                    countpaquets = 0;
                    lastPaquet = "zoneOut";
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            isPermissionDenied = false;
        } else {
            mMapTrackingLocationButton.setChecked(false);
            mLocationButton.setChecked(false);
            isPermissionDenied = true;
        }
    }

    public boolean isPermissionDenied() {
        return isPermissionDenied;
    }




    @Override
    public void onVehiclesReceived(boolean isFromResume) {

    }


    private class FabButtonClickListener implements FabLayout.onFabButtonClickListener {
        @Override
        public void onFabButtonClicked(FloatingActionButton buttonId, String tag, boolean selected) {

            ConnectedCar.getInstance().addToState(tag, selected);
            if (mMapTracking.getGoogleMap() == null)
                return;
            if (tag.equalsIgnoreCase(KeyConstants.KEY_BUTTON_SATELLITE)) {
                mMapTracking.getGoogleMap().setMapType(selected ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL);

            } else if (tag.equalsIgnoreCase(KeyConstants.KEY_BUTTON_TRAFFIC)) {
                mMapTracking.getGoogleMap().setTrafficEnabled(selected);
            } else if (tag.equalsIgnoreCase(KeyConstants.KEY_BUTTON_ZONES)) {
                if (selected) {
                    toggleZones(buttonId, tag);
                } else {
                    mMapTracking.setDrawnCircle(null);
                    mMapTracking.clearZones();
                    //mMapTracking.clearMap();
                }
            }
        }
    }

    private void showLocationDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getBaseActivity());
        dialog.setMessage(getBaseActivity().getResources().getString(R.string.Gen_Gen_lbl_gps_network_not_enabled));
        dialog.setPositiveButton(getBaseActivity().getResources().getString(R.string.Gen_Gen_lbl_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                paramDialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton(getBaseActivity().getResources().getString(R.string.Gen_Gen_lbl_settings), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getBaseActivity().startActivity(myIntent);
            }
        });

        dialog.show();
    }


    /**
     * @param button
     * @param tag
     */
    private void toggleZones(FloatingActionButton button, String tag) {

//        if ((mZonesList != null) && !mZonesList.isEmpty()) {
//            mMapTracking.clearZones();
//            mMapTracking.drawZones(mZonesList);
//
//        } else {
//
//            mMapTracking.clearZones();
//            button.setSelected(false);
//            ConnectedCar.getInstance().addToState(tag, false);
//            showToast(getString(R.string.LMT_LvTrk_lbl_no_zones_available));
//        }

        mMapTracking.updateCamera(false);
    }


    /**
     * @param list
     */
    public void setupZonesList(List<ZoneEntity> list) {

        if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).getCenter() != null && isMapReady
                && !AppConfig.getInstance().getUser().isANewDevice()) {
            mMapTracking.setDrawnCircle(null);
            zoneEntity = list.get(0);
            placeMaker(list.get(0).getCenter());
            drawZone();
        } else {
//            showLongToast("Please Select your zone");
            mFirstSignUpRelativeLayout.setVisibility(VISIBLE);
        }
    }


    public void showProgress() {
        if (progress == null)
            return;

        progress.setVisibility(VISIBLE);
    }

    public void hideProgress() {
        if (progress == null)
            return;

        progress.setVisibility(GONE);
    }


    private void fetchAddress(final LatLng latLng) {
        ((HouseHolderDashboardFragment) controller).retrieveAddress(latLng);
    }

    public void setAddress(String featureName, String address) {
        if (StringUtility.isEmptyOrNull(featureName)) {
            txtAddress.setText(address);
        } else {
            txtAddress.setText(featureName);
        }

    }

    /**
     * @param location
     * @param address
     */
    private void saveSelectedLocation(LatLng location, String address) {
        List<ZoneEntity> zoneEntityList = AppDatabase.getInstance(getBaseActivity().getApplicationContext()).getZonesDao().getAll();
        if (zoneEntityList != null && zoneEntityList.size() > 0) {
            zoneEntity = AppDatabase.getInstance(getBaseActivity().getApplicationContext()).getZonesDao().getAll().get(0);
        }

        if (zoneEntity != null) {
            isFirstTime = false;
            zoneEntity.setCenter(location);
            zoneEntity.setPlaceName(address);
            zoneEntity.setRadius(getRadius());
            AppDatabase.getInstance(getBaseActivity().getApplicationContext()).getZonesDao().updateZone(zoneEntity);
        } else {
            isFirstTime = true;
            zoneEntity = new ZoneEntity();
            zoneEntity.setCenter(location);
            zoneEntity.setPlaceName(address);
            zoneEntity.setRadius(getRadius());
            AppDatabase.getInstance(getBaseActivity().getApplicationContext()).getZonesDao().insertZone(zoneEntity);
        }

        if (location != null) {
            // placeMaker(location);
            UserLocationBody userLocationBody = new UserLocationBody();
            userLocationBody.setLatitude(String.valueOf(location.latitude));
            userLocationBody.setLongitude(String.valueOf(location.longitude));
            userLocationBody.setRadius(String.valueOf(getRadius()));
            ((HouseHolderDashboardFragment) controller).sendUserLocationRequest(userLocationBody);
        }
    }

    private void placeMaker(LatLng latLng) {

        mSelectedLocation = latLng;
        if (mMapTracking.getDrawnMarkers() != null) {
            mMapTracking.clearDrawnMarkers();
        }

        if (geoFenceMarker != null) {
            geoFenceMarker.remove();
        }


        if (mMapTracking.getDrawnCircle() != null) {
            mMapTracking.clearDrawnCircles();
        }

        Bitmap bitmapMarker = BitmapUtil.getBitmap(getBaseActivity(), R.drawable.red_pin);
        geoFenceMarker = mMapTracking.getGoogleMap().addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker)));

        mMapTracking.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));

        ((HouseHolderDashboardFragment) controller).retrieveAddress(latLng);

        int radius = getRadius();

    }

    /**
     *
     */
    private void drawZone() {
        drawMapRadius(geoFenceMarker.getPosition(), null, getRadius());
        mMapTracking.getGoogleMap().setTrafficEnabled(false);
    }

    private void drawMapRadius(LatLng center, LatLng radiusEndPosition, int radius) {

        drawZoneCircle(radius);

//        if (radiusEndMarker != null) {
//            radiusEndMarker.remove();
//        }

        LatLngBounds bounds = LocationUtils.getBoundsFromRadius(center, radius);
        if (radiusEndPosition == null) {
            radiusEndPosition = LocationUtils.pointLatLng(center.latitude, center.longitude, radius, 90);
        }
//        radiusEndMarker = mMapTracking.getGoogleMap()
//                .addMarker(new MarkerOptions()
//                        .position(radiusEndPosition)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_end))
//                        .anchor(0.5f, 0.5f));
//
//        if (!this.radiusEndMarker.isDraggable())
//            this.radiusEndMarker.setDraggable(true);
//
//        //drawPolyline(radiusEndMarker);
//        fallbackLatLng = radiusEndMarker.getPosition();
//        if (!zoneEntity.isNew()) {
//            mMapTracking.getGoogleMap().setPadding(200, 0, 200, 850);
//            mMapTracking.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(center, 16f));
//        } else {
        mMapTracking.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(center, Util.getZoomLevel(radius)));
        mMapTracking.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//        }

    }


    public void drawPolyline(Marker marker) {

        if (radiusLine != null)
            radiusLine.remove();

        radiusLine = mMapTracking.getGoogleMap().addPolyline(new PolylineOptions()
                .add(geoFenceMarker.getPosition(), marker.getPosition())
                .width(3)
                .color(ContextCompat.getColor(controller.getBaseActivity(), R.color.colorRedBadInfo)));
    }


    private int getRadius() {
        try {
            return Integer.parseInt("" + MAX_RADIUS);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setRadius(int radius) {

        txtRadius.setText(String.valueOf(radius));
    }

    private boolean isRadiusValid(int radius) {

        return radius <= MAX_RADIUS;
    }

    public CountDownTimer getDownTimer() {
        return downTimer;
    }

    private void fillZoneEntity() {
        zoneEntity.setRadius(Integer.parseInt(txtRadius.getText().toString()));
    }

    private void drawZoneCircle(int endMarkerRadius) {

        if (mGeoFenceCircleLimits != null) {
            mGeoFenceCircleLimits.remove();
        }

        mGeoFenceCircleLimits = mMapTracking.getGoogleMap().addCircle(new CircleOptions()
                .center(geoFenceMarker.getPosition())
                .fillColor(Color.argb(50, 100, 0, 0))
                .radius(endMarkerRadius)
                .strokeWidth(2)
                .strokeColor(ContextCompat.getColor(controller.getBaseActivity(), R.color.colorTransparent)));
    }


    private void saveTrackingInfoLocationInRoomDataBase(Location location) {
    }

//    private void registerLocationDialog() {
//        alertVerifyFaceDialog = new AlertBottomDialog(getBaseActivity());
//        alertVerifyFaceDialog.setCancelable(false);
//        alertVerifyFaceDialog.create();
//        alertVerifyFaceDialog.setTextButton(getBaseActivity().getResources().getString(R.string.Gen_Gen_lbl_next));
//        alertVerifyFaceDialog.setTvTitle(getString(R.string.HHD_HHDh_register_location_message));
//        alertVerifyFaceDialog.show();
//
//        alertVerifyFaceDialog.setOnClickAlertListener(new OnClickAlertListener() {
//            @Override
//            public void onAlertClick() {
//                alertVerifyFaceDialog.dismiss();
//                locationConfirmationDialog(currentLocation);
//
//            }
//        });
//    }

    /**
     *
     */
    private void locationConfirmationDialog(final LatLng latLng) {
        if (latLng == null) {
            return;
        }
        mFirstStepMessageLinearLayout.setVisibility(GONE);
        mConfirmationLocationMessageLinearLayout.setVisibility(VISIBLE);
        mLocationButton.setVisibility(VISIBLE);
        mViewDashboardWrongLocationLayout.setVisibility(GONE);
        view_dashboard_user_location_layout.setVisibility(VISIBLE);
        drawZone();
    }

    /**
     * @param enabled
     */
    private void setNextButtonStatus(boolean enabled) {
        if (enabled) {
            mNextButton.setEnabled(true);
            mNextButton.setTextColor(ContextCompat.getColor(getBaseActivity(), R.color.white));
        } else {
            mNextButton.setEnabled(false);
            mNextButton.setTextColor(ContextCompat.getColor(getBaseActivity(), R.color.nex_button_self_questions_color_disabled));
        }
    }

    private void onLocationToggleChecked() {
        if (PermissionUtils.checkAndRequestPermissionLocation(controller)) {
            if (LocationManager.isLocationEnabled(getBaseActivity())) {
                mLocationButton.setChecked(true);
                mMapTrackingLocationButton.setChecked(true);
                mMapTracking.getGoogleMap().setMyLocationEnabled(false);
                mMapTracking.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
                mMapTracking.moveToCurrentLocation();

            } else {
                EventBus.getDefault().post(AppConstants.EVENT_LOCATION_DISABLED);
                mLocationButton.setChecked(false);
                mMapTrackingLocationButton.setChecked(false);
            }
        } else {

            mMapTracking.setPermissionDialogShown(true);
            mLocationButton.setChecked(false);
            mMapTrackingLocationButton.setChecked(false);


        }
    }


}

