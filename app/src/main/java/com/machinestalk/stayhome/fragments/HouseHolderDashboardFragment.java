package com.machinestalk.stayhome.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.machinestalk.android.components.Loader;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.interfaces.ServiceSecondaryEventHandler;
import com.machinestalk.android.service.ServiceCallback;
import com.machinestalk.android.views.BaseView;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.entities.ZoneEntity;
import com.machinestalk.stayhome.responses.HomeConfigurationResponse;
import com.machinestalk.stayhome.service.ServiceFactory;
import com.machinestalk.stayhome.service.body.UserLocationBody;
import com.machinestalk.stayhome.service.body.UserStatusBody;
import com.machinestalk.stayhome.utils.LocationUtils;
import com.machinestalk.stayhome.views.HouseHolderDashboardView;

import java.util.List;

/**
 * Created on 1/19/2017.
 */

public class HouseHolderDashboardFragment extends BaseFragment implements ServiceSecondaryEventHandler {

    private GetZonesTask mGetZonesTask;
    private Activity mActivity;
    private int typeConnection;

    @Override
    public void willStartCall() {
    }

    @Override
    public void didFinishCall(boolean isSuccess) {
        ((HouseHolderDashboardView) view).hideProgress();
        Loader.hideLoader();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        typeConnection = checkConnectionType();

        if (AppConfig.getInstance().getUser().isANewDevice()){
            getConfiguration();
        }
        loadZones();

    }

    @Override
    public void onViewCreated(View mView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);
        showToolbar();
        MapsInitializer.initialize(ConnectedCar.getInstance());
    }

    @Override
    protected BaseView getViewForController(Controller controller) {
        return new HouseHolderDashboardView(controller);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     *
     */
    public void showToolbar() {
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (Exception e) {

        }
    }

    @Override
    public String getActionBarTitle() {
        if ( !AppConfig.getInstance().getUser().isANewDevice()) {
            return getString(R.string.ApDr_ApDr_SBtn_zones);
        }else {
            return getString(R.string.HHD_HHDh_first_sign_up_screen_title);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //mActivity.unregisterReceiver(WiFiDirectBroadcastReceiver);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (((HouseHolderDashboardView) view).getDownTimer() != null) {
            ((HouseHolderDashboardView) view).getDownTimer().cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }


    /**
     * load zones from database.
     */
    public void loadZones() {

        if (mGetZonesTask != null && mGetZonesTask.isCancelled() == false) {
            mGetZonesTask.cancel(true);
        } else {
            mGetZonesTask = new GetZonesTask();
            mGetZonesTask.execute();
        }
    }


    class GetZonesTask extends AsyncTask<Void, Void, List<ZoneEntity>> {

        @Override
        protected List<ZoneEntity> doInBackground(Void... voids) {
            if (isCancelled()) {
                return null;
            }
            List<ZoneEntity> zoneEntities = AppDatabase.getInstance(mActivity).getZonesDao().getAll();

            return zoneEntities;
        }

        @Override
        protected void onPostExecute(List<ZoneEntity> zoneEntities) {
//            fetchZones();


            mGetZonesTask = null;
        }
    }


    private int checkConnectionType() {
        ConnectivityManager connMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        int typeConn = ConnectivityManager.TYPE_WIFI;

        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                typeConn = ConnectivityManager.TYPE_WIFI;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                typeConn = ConnectivityManager.TYPE_MOBILE;
            }
        }
        return typeConn;
    }

    public void navigateToFavoriteZoneActivity() {
//        ConnectedCar.getInstance().showCurrentLocationOption(-1);
//        Intent intent = new Intent(getBaseActivity(), PlaceSearchActivity.class);
//        intent.putExtra("Zone", true);
//        startActivityForResult(intent, FAVORITE_ZONE_REQUEST_CODE);
    }

    public void retrieveAddress(final LatLng location) {
        // ((HouseHolderDashboardView) view).showProgress();
        LocationUtils.fetchAddress(this, location, new LocationUtils.IGeoCodingListener() {
            @Override
            public void onAddressReceived(String address) {
                ((HouseHolderDashboardView) view).hideProgress();
                ((HouseHolderDashboardView) view).setAddress(address, address);
            }

            @Override
            public void onAddressFailed() {
                ((HouseHolderDashboardView) view).hideProgress();
                showToast(getString(R.string.Zon_ZonDt_lbl_zone_error_no_address));
            }

            @Override
            public void onInternetConnectionFailed() {
                ((HouseHolderDashboardView) view).hideProgress();
            }
        });
    }

    public static Intent makeNotificationIntent(Context geofenceService) {
        return new Intent(geofenceService, HouseHolderDashboardFragment.class);
    }

    /**
     *
     */
    public void sendUserLocationRequest(UserLocationBody userLocationBody) {

        ((ServiceFactory) serviceFactory).getUserService()
                .sendUserLocation(userLocationBody, AppConfig.getInstance().getUser().getCustomerId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {
                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                        ((HouseHolderDashboardView) view).hideProgress();
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {

                             Log.i(getClass().getName(), "user location request onSuccess:");
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        showToast(errorMessage);
                        //   Logger.i("user location request onFailure:");
                    }
                });
    }


    /**
     *
     */
    public void sendUserStatus(UserStatusBody userStatusBody) {

        ((ServiceFactory) serviceFactory).getUserService()
                .sendUserStatus(userStatusBody, AppConfig.getInstance().getUser().getCustomerId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {

                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                        ((HouseHolderDashboardView) view).hideProgress();
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        //   Logger.i("user status request onSuccess:");
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        showToast(errorMessage);
                        //    Logger.i("user status request
                        //   :");
                    }
                });
    }



    /**
     * @return
     */
    public boolean isZoneExist() {
        List<ZoneEntity> list = AppDatabase.getInstance(getBaseActivity()).getZonesDao().getAll();
        if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).getCenter() != null) {
            return true;
        }
        return false;
    }
    public void getConfiguration() {

        ConnectedCar.getInstance().getServiceFactory().getUserService()
                .getConfiguration(AppConfig.getInstance().getUser().getTenantId())
                .enableRetry(false)
                .enqueue(new ServiceCallback(this, new ServiceSecondaryEventHandler() {
                    @Override
                    public void willStartCall() {

                    }

                    @Override
                    public void didFinishCall(boolean isSuccess) {
                        ((HouseHolderDashboardView) view).hideProgress();
                    }
                }) {
                    @Override
                    protected void onSuccess(Object response, int code) {
                        Log.i(getClass().getName(),"get configuration list success");
                        List<com.machinestalk.stayhome.responses.Configuration> configurations = ((HomeConfigurationResponse)response).getList();
                        if (configurations != null && configurations.size() > 0) {
                            AppDatabase.getInstance(getBaseActivity()).getConfigurationDao().insertAll(configurations);
                        }
                    }

                    @Override
                    protected void onFailure(String errorMessage, int code) {
                        showToast(errorMessage);
                        //    Logger.i("user status request onFailure:");
                    }
                });
    }


}
