package com.machinestalk.stayhome.components;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.CollectionUtility;
import com.machinestalk.android.utilities.UIUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.entities.ZoneEntity;
import com.machinestalk.stayhome.listeners.OnMapReadyListener;
import com.machinestalk.stayhome.utils.LocationManager;
import com.machinestalk.stayhome.utils.PermissionUtils;
import com.machinestalk.stayhome.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created on 1/3/2017.
 */

public class MyMapTracking extends RelativeLayout implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener,
        LocationListener {

    public static final String DEFAULT_COLOR_FOR_ROUTE = "#7A9ECB";
    private static final int RADIUS_DEFAULT = 500;
    public static final String MAP_FRAGMENT = "map_fragment";
    private View view;
    private Context context;
    private GoogleMap googleMap;
    private Marker liveMarker;
    private List<Marker> drawnMarkers = new ArrayList<>();
    private List<MyMapTracking.CustomZone> drawnCircle;
    private List<Polyline> drawnLines = new ArrayList<>();
    private Circle zoneCircle;
    private MyMapTracking.onMapGestureChangeListener gestureChangeListener;
    private FabLayout fabLayout;
    private LinearLayout centerLocationBlock;
    private onMapLocationListener locationListener;
    private onMapMarkerClickedListener markerClickListener;
    private LatLng centerLatLng;
    private PolylineOptions polylineOptions;
    private ToggleButton toggleLocation;
    private Controller controller;
    OnMapReadyListener onMapReadyListener;
    onMapLoadedListener mMapLoadedListener;
    onMapClickListener onMapClickListener;
    onMarkerStartDragListener mOnMarkerStartDragListener;
    onMarkerDragListener mOnMarkerDragListener;
    onMarkerEndDragListener mOnMarkerEndDragListener;

    private boolean isFirstTime;
    private boolean shouldMoveToCurrentLocation = true;
    boolean isPermissionDialogShown = false;
    private LinkedHashMap<Integer, Marker> vehicleMarkerMap = new LinkedHashMap<>();
    private FrameLayout mBubbleFrameLayout;
    private ArrayList<Marker> mMarkerZoneList = new ArrayList<>();
    private ArrayList<Circle> mCircleZoneList = new ArrayList<>();
    private Location mLocation;
    private Marker mCurrLocationMarker;

    public MyMapTracking(Context context) {

        super(context);
        init(context, null);
    }

    public MyMapTracking(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(context, attrs);
    }

    public void setOnMapReadyListener(OnMapReadyListener onMapReadyListener) {

        this.onMapReadyListener = onMapReadyListener;
    }

    public MyMapTracking(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyMapTracking(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void init(Context context, AttributeSet attrs) {

        this.context = context;

        this.view = inflateView(context);
        this.setupView(view);

        drawnMarkers = new ArrayList<>();
    }

    protected View inflateView(Context context) {

        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.layout_my_map_tracking, this, true);
        } catch (Exception e) {

        }
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void setupView(View view) {

        this.fabLayout = (FabLayout) view.findViewById(R.id.fabLayout);
        this.centerLocationBlock = (LinearLayout) view.findViewById(R.id.selectLocationBlock);
        this.toggleLocation = (ToggleButton) view.findViewById(R.id.toggleLocation);
        this.mBubbleFrameLayout = (FrameLayout) view.findViewById(R.id.location_bubble_container);

        toggleLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toggleLocation.isChecked()) {
                    onLocationToggleChecked();
                } else {
                    toggleLocation.setChecked(true);
                }

                if (gestureChangeListener != null) {
                    gestureChangeListener.toggleLocationClicked();
                }
            }
        });
        setupMap();

    }

    public void onLocationToggleChecked() {

        if (controller == null) {
            return;
        }
        if (PermissionUtils.checkAndRequestPermissionLocation(controller)) {
            if (LocationManager.isLocationEnabled(context)) {

                if (shouldMoveToCurrentLocation && toggleLocation.isChecked()){
                    moveToCurrentLocation();
                }
                else {
                    setShouldMoveToCurrentLocation(true);
                    toggleLocation.setChecked(false);
                }
            } else if (!LocationManager.isLocationEnabled(context)) {
//                ((BaseActivity) controller.getBaseActivity()).showLocationToggleDialog(toggleLocation);
                EventBus.getDefault().post(AppConstants.EVENT_LOCATION_DISABLED);

            } else {
                ((BaseActivity) controller.getBaseActivity()).showLocationDialog();
                toggleLocation.setChecked(false);
            }
        } else {
            toggleLocation.setChecked(false);
            setPermissionDialogShown(true);
        }
    }

    public boolean isPermissionDialogShown() {

        return isPermissionDialogShown;
    }

    public void setPermissionDialogShown(boolean state) {

        isPermissionDialogShown = state;
    }

    public ToggleButton getToggleLocation() {

        return toggleLocation;
    }

    public void setupMap() {

        if (isInEditMode())
            return;

        try {
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            FrameLayout frame = (FrameLayout) view.findViewById(R.id.mapContainer);
            FragmentTransaction fragmentTransaction = ((BaseActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(frame.getId(), supportMapFragment, MAP_FRAGMENT);
            fragmentTransaction.commit();
            supportMapFragment.getMapAsync(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (onMapClickListener != null){
            onMapClickListener.onMapClick(latLng);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json));

            if (!success) {
            }
        } catch (Exception e) {
        }
        this.centerLatLng = googleMap.getCameraPosition().target;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.setOnCameraMoveStartedListener(this);
        this.googleMap.setOnCameraMoveListener(this);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//        this.googleMap.setMyLocationEnabled(false);
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AppConstants.RIYADH_LAT_LNG, 5.0f));
        this.googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }

        });
        this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {


            }
        });

        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
           // this.googleMap.setMyLocationEnabled(true);
        }
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);

        toggleLocation.setChecked(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                onLocationToggleChecked();
            }
        }, 500);

        if (gestureChangeListener != null)
            gestureChangeListener.onMapRefresh();

        if (onMapReadyListener != null)
            onMapReadyListener.onMapReady();

        this.googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (mMapLoadedListener != null){
                    mMapLoadedListener.onMapLoaded();
                }
            }
        });

        this.googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                if (mOnMarkerStartDragListener != null){
                    mOnMarkerStartDragListener.onMarkerDragStart(marker);
                }

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                if (mOnMarkerDragListener != null){
                    mOnMarkerDragListener.onMarkerDrag(marker);
                }

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (mOnMarkerEndDragListener != null){
                    mOnMarkerEndDragListener.onMarkerDragEnd(marker);
                }

            }
        });


    }


    public Marker placeRouteMaker(LatLng position, int markerImg) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.anchor(-0.7f,0.2f) ;
        markerOptions.icon(BitmapDescriptorFactory.fromResource(markerImg));

        Marker marker = drawMarker(position, markerOptions);
        return marker;

    }

    public Marker drawMarker(LatLng points) {

        Marker marker = drawMarkerWithoutAnimate(points);
        return marker;
    }

    public Marker drawMarkerWithoutAnimate(LatLng points) {

        return googleMap.addMarker(getCustomizedMarker(points, R.drawable.red_pin));
    }

    public Marker drawMarker(LatLng points, MarkerOptions markerOptions) {

        Marker marker = drawMarkerWithoutAnimate(points, markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points, 16.0f));

        return marker;
    }

    public Marker drawMarker(LatLng points, MarkerOptions markerOptions, boolean shouldNotMoveCamera) {

        Marker marker = drawMarkerWithoutAnimate(points, markerOptions);
        if (shouldNotMoveCamera) {
            return marker;
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points, 16.0f));
        return marker;
    }

    public Marker drawMarkerWithoutAnimate(LatLng points, MarkerOptions markerOptions) {

        Marker marker = googleMap.addMarker(markerOptions);
        if (drawnMarkers == null) {
            drawnMarkers = new ArrayList<>();
        }
        drawnMarkers.add(marker);
        return marker;
    }

    public GoogleMap getGoogleMap() {

        return googleMap;
    }

    public void updateCamera(boolean includeUserLocation) {

        if (googleMap == null) {
            return;
        }
        LatLngBounds.Builder builder = LatLngBounds.builder();
        int markerCount = 0;
        if (!CollectionUtility.isEmptyOrNull(drawnMarkers)) {
            for (Marker marker : drawnMarkers) {
                builder.include(marker.getPosition());
                markerCount++;
            }
        }

        if (includeUserLocation && googleMap.isMyLocationEnabled()) {
            Location location = googleMap.getMyLocation();
            if (location != null) {
                toBounds(builder, new LatLng(location.getLatitude(), location.getLongitude()), RADIUS_DEFAULT);
                markerCount++;
            } else
                updateCameraAfterFetchingLocation();
        }

        if (!CollectionUtility.isEmptyOrNull(drawnCircle)) {
            for (MyMapTracking.CustomZone customZone : drawnCircle) {
                toBounds(builder, customZone.getMarker().getPosition(), customZone.getCircle().getRadius());
                markerCount++;
            }
        }

        if ((drawnLines != null) && !CollectionUtility.isEmptyOrNull(drawnLines)) {
            for (Polyline point : drawnLines) {
                for (LatLng lat : point.getPoints()) {
                    builder.include(lat);
                }
                markerCount++;
            }
        }

        if (markerCount < 1) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AppConstants.RIYADH_LAT_LNG, 5.0f));
            return;
        }

        if (markerCount == 1 && !CollectionUtility.isEmptyOrNull(drawnMarkers)){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drawnMarkers.get(0).getPosition(), 16.0f));
            return;
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), UIUtility.convertDpToPixel(110, getContext())));
        googleMap.setPadding(0,220,0,0);
    }

    /**
     *
     */
    public void showDefaultPosition(){
        if (googleMap == null) {
            return;
                        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AppConstants.RIYADH_LAT_LNG, 5.0f));
    }

    private void updateCameraAfterFetchingLocation() {
        isFirstTime = true;
        LocationManager.make(context, new LocationManager.ICurrentLocation() {
            @Override
            public void onLocationReceived(final Location address) {

                if (isFirstTime) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateCamera(true);
                        }
                    }, 500);
                    isFirstTime = false;
                }
            }
        }).fetch();
    }




    /**
     * Clear all drawn markers from the map
     */
    public void clearMarkers() {

        if (liveMarker != null) {
            liveMarker.remove();
        }

        if ((drawnMarkers != null) && !drawnMarkers.isEmpty()) {
            for (Marker point : drawnMarkers) {
                point.remove();
            }
            drawnMarkers.clear();
        }
    }

    public void clearMarkers(Marker marker) {

        if (marker != null) {
            marker.remove();
        }
        if ((drawnMarkers != null) && !drawnMarkers.isEmpty()) {
            if (drawnMarkers.contains(marker)) {
                drawnMarkers.remove(marker);
            }
        }
    }

    public void clearDrawnMarkers() {

        if ((drawnMarkers != null) && !drawnMarkers.isEmpty()) {
            for (Marker point : drawnMarkers) {
                point.remove();
            }
            drawnMarkers.clear();
        }
    }
    public void clearDrawnCircles() {

        if ((drawnCircle != null) && !drawnCircle.isEmpty()) {
            for (MyMapTracking.CustomZone customZone : drawnCircle) {
                customZone.getCircle().remove();
            }
            drawnMarkers.clear();
        }
    }

    public void clearPolyline() {

        if ((drawnLines != null) && !drawnLines.isEmpty()) {
            drawnLines.clear();
        }
    }

    public void clearPolyline(Polyline polyline) {

        if (polyline != null) {
            polyline.remove();
        }

        if ((drawnLines != null) && !drawnLines.isEmpty()) {
            if (drawnLines.contains(polyline)) {
                drawnLines.remove(polyline);
            }
        }
    }

    /**
     * Draw zones on the Map
     *
     * @param zones
     */
    public void drawZones(List<ZoneEntity> zones) {

        drawnCircle = new ArrayList<>();

        MyMapTracking.CustomZone customZones;
        for (ZoneEntity zone : zones) {
//            Marker marker = googleMap.addMarker(getCustomizedMarker(zone.getCenter(), R.drawable.zone_marker_updated));
            Circle circle = googleMap.addCircle(getCustomizedCircle(zone.getCenter(), zone.getRadius()));
            customZones = new MyMapTracking.CustomZone(circle, null);
            drawnCircle.add(customZones);
//            mMarkerZoneList.add(marker);
            mCircleZoneList.add(circle);
        }

    }
    public void drawZone(ZoneEntity zone) {

        drawnCircle = new ArrayList<>();

        MyMapTracking.CustomZone customZones;
//            Marker marker = googleMap.addMarker(getCustomizedMarker(zone.getCenter(), R.drawable.zone_marker_updated));
            Circle circle = googleMap.addCircle(getCustomizedCircle(zone.getCenter(), zone.getRadius()));
            customZones = new MyMapTracking.CustomZone(circle, null);
            drawnCircle.add(customZones);
//            mMarkerZoneList.add(marker);
            mCircleZoneList.add(circle);

    }

    public Polyline drawRoute(Collection<LatLng> values) {

        Polyline polyline = drawPolyline((List<LatLng>) values, DEFAULT_COLOR_FOR_ROUTE);
        return polyline;
    }

    public Polyline drawRoute(String route) {

        List<LatLng> decodedPath = PolyUtil.decode(route);
        Polyline polyline = drawPolyline(decodedPath, DEFAULT_COLOR_FOR_ROUTE);
        return polyline;
    }

    public Polyline drawRoute(String route, String color) {

        List<LatLng> decodedPath = PolyUtil.decode(route);
        Polyline polyline = drawPolyline(decodedPath, color);
        return polyline;
    }

    @NonNull
    public Polyline drawPolyline(List<LatLng> decodedPath, String colorCode) {

        polylineOptions = new PolylineOptions().addAll(decodedPath);
        polylineOptions.color(Color.parseColor(colorCode));
        Polyline polyline = this.googleMap.addPolyline(polylineOptions);
        drawnLines.add(polyline);
        updateCamera(false);
        return polyline;
    }

    /**
     * Clear all zones from the Map
     */
    public void clearZones() {

        if ((drawnCircle != null) && (drawnCircle.size() > 0)) {
            for (MyMapTracking.CustomZone cZone : drawnCircle) {
                cZone.getCircle().remove();
                cZone.getMarker().remove();

            }
        }

        if (mMarkerZoneList.size() > 0) {
            for (int i = 0; i < mMarkerZoneList.size(); i++) {
                mMarkerZoneList.get(i).remove();
            }
            mMarkerZoneList.clear();
        }

        if (mCircleZoneList.size() > 0) {
            for (int i = 0; i < mCircleZoneList.size(); i++) {
                mCircleZoneList.get(i).remove();
            }
            mCircleZoneList.clear();
        }
    }

    private MarkerOptions getCustomizedMarker(LatLng location, @DrawableRes int icon) {

        return new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(icon));
    }

    private CircleOptions getCustomizedCircle(LatLng location, int radius) {

        return new CircleOptions().center(location).radius(radius)
                .fillColor(Color.argb(20, 0, 70, 0))
                .strokeWidth(2)
                .strokeColor(ContextCompat.getColor(controller.getBaseActivity(), R.color.zone_radius_color));
    }


    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            /**
             * The user gestured on the map.
             */
            toggleLocation.setChecked(false);
            if (gestureChangeListener != null)
                gestureChangeListener.movedByUser();

        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            /**
             * The user tapped something on the map.
             */
            if (gestureChangeListener != null)
                gestureChangeListener.movedByCameraTap();

        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            /**
             * The app moved the camera.
             */
            if (gestureChangeListener != null)
                gestureChangeListener.movedByApp();
        }
    }

    public FabLayout getFabLayout() {

        return fabLayout;
    }

    public void setMapStateListener(MyMapTracking.onMapGestureChangeListener gestureChangeListener) {

        this.gestureChangeListener = gestureChangeListener;
    }

    public void setMapMarkerClickedListener(onMapMarkerClickedListener clickListener) {

        markerClickListener = clickListener;

        if (this.googleMap == null)
            return;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                return (markerClickListener != null) && markerClickListener.onMarkerClicked(marker);
            }
        });
    }

    public void setMapLocationListener(final onMapLocationListener locationListener) {

        this.locationListener = locationListener;

        if (this.googleMap == null)
            return;

        centerLatLng = this.googleMap.getCameraPosition().target;
        try {
            if (locationListener != null)
                locationListener.onLocationSelected(centerLatLng);
        } catch (Exception e) {

        }

        this.googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                centerLatLng = cameraPosition.target;
                try {
                    if (locationListener != null)
                        locationListener.onLocationSelected(centerLatLng);
                } catch (Exception e) {
                }
            }
        });
    }

    public List<CustomZone> getDrawnCircle() {

        return drawnCircle;
    }

    public void setDrawnCircle(List<CustomZone> drawnCircle) {

        this.drawnCircle = drawnCircle;
    }

    public void showCenterLocationBlock() {

        if (centerLocationBlock == null)
            return;
        mBubbleFrameLayout.setVisibility(View.VISIBLE);
        centerLocationBlock.setVisibility(View.VISIBLE);
    }

    public void hideCenterLocationBlock() {

        if (centerLocationBlock == null)
            return;

        centerLocationBlock.setVisibility(View.GONE);
    }

    public void showBubbleText() {

        if (mBubbleFrameLayout == null)
            return;

        mBubbleFrameLayout.setVisibility(View.VISIBLE);
    }

    public void hideBubbleText() {

        if (mBubbleFrameLayout == null)
            return;

        mBubbleFrameLayout.setVisibility(View.GONE);
    }


    public void clearMap() {

        if (googleMap != null)
            googleMap.clear();
        clearDrawnMarkers();
        clearPolyline();
        clearZones();
    }

    private void toBounds(LatLngBounds.Builder builder, LatLng center, double radius) {

        builder.include(SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225));
        builder.include(SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45));
    }

    public Controller getController() {

        return controller;
    }

    public void setController(Controller controller) {

        this.controller = controller;
    }

    public void moveToCurrentLocation() {

        if (getGoogleMap() != null) {

            final Location location = null;

            if (location != null) {
                moveToCurrentLocationOnLocationReceived(new LatLng(location.getLatitude(), location.getLongitude()) );
            } else {
                isFirstTime = true;
                LocationManager.make(context, new LocationManager.ICurrentLocation() {
                    @Override
                    public void onLocationReceived(final Location address) {

                        if (isFirstTime || toggleLocation.isChecked()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    getGoogleMap().setMyLocationEnabled(false);
                                    LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                                    moveToCurrentLocationOnLocationReceived(location);

                                }
                            }, 500);
                            isFirstTime = false;
                        }
                    }
                }).fetch();

            }
        }
    }

    public Location fetchCurrentLocation() {

        if (getGoogleMap() != null && PermissionUtils.isLocationPermissionAvailable(controller) && LocationManager.isLocationEnabled(controller.getBaseActivity())) {

            getGoogleMap().setMyLocationEnabled(true);
            final Location location = getGoogleMap().getMyLocation();

            return location;
        }

        return null;
    }

    public void moveToCurrentLocationOnLocationReceived(LatLng location) {

        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(17f).build();
            getGoogleMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (mCurrLocationMarker != null){
                mCurrLocationMarker.remove();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.anchor(-0.7f,0.2f) ;
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_circle));
                mCurrLocationMarker = drawMarker(location, markerOptions);
            }else {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.anchor(-0.7f,0.2f) ;
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_circle));
                mCurrLocationMarker = drawMarker(location, markerOptions);
            }
            toggleLocation.setChecked(true);
        }
    }

    public void setFabVisibilityGone() {
        fabLayout.setVisibility(GONE);
    }

    public void setFabVisibility(boolean visibility) {
        fabLayout.setVisibility(visibility ?VISIBLE: GONE);
    }

    @Override
    public void onLocationChanged(Location location) {

//        getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
    }

    public interface onMapGestureChangeListener {
        void movedByUser();

        void movedByApp();

        void movedByCameraTap();

        void onMapRefresh();

        void toggleLocationClicked();
    }

    public interface onMapLocationListener {
        void onLocationSelected(LatLng bound);
    }

    public interface onMapMarkerClickedListener {
        boolean onMarkerClicked(Marker marker);
    }

    public static class CustomZone {
        Circle circle;
        Marker marker;
        private Controller controller;

        public CustomZone(Circle circle, Marker marker) {

            this.circle = circle;
            this.marker = marker;
        }

        public Circle getCircle() {

            return circle;
        }

        public Marker getMarker() {

            return marker;
        }

    }

    public LinkedHashMap<Integer, Marker> getVehicleMarkerMap() {

        return vehicleMarkerMap;
    }

    public void setVehicleMarkerMap(LinkedHashMap<Integer, Marker> vehicleMarkerMap) {

        this.vehicleMarkerMap = vehicleMarkerMap;
    }
    public void setAllGesturesEnabled(boolean allGesturesEnabled) {

        this.getGoogleMap().getUiSettings().setAllGesturesEnabled(allGesturesEnabled);
    }

    public void clearVehicleMarkerMap() {

        if (vehicleMarkerMap != null) {
            vehicleMarkerMap.clear();
            vehicleMarkerMap = null;
        }
    }

    public boolean shouldMoveToCurrentLocation() {

        return shouldMoveToCurrentLocation;
    }

    public void setShouldMoveToCurrentLocation(boolean shouldMoveToCurrentLocation) {

        this.shouldMoveToCurrentLocation = shouldMoveToCurrentLocation;
    }

    public void initFab() {

        ArrayList<FloatingActionButton> floatingActionButtons = new ArrayList<>();
        floatingActionButtons.add(Util.getFloatingActionButton(controller.getBaseActivity(), KeyConstants.KEY_BUTTON_TRAFFIC, R.string.LMT_Gen_lbl_fab_traffic, R.drawable.selector_toggle_traffic));
        floatingActionButtons.add(Util.getFloatingActionButton(controller.getBaseActivity(), KeyConstants.KEY_BUTTON_SATELLITE, R.string.LMT_Gen_lbl_fab_satellite, R.drawable.selector_toggle_satellite));
        getFabLayout().setItems(floatingActionButtons);
        getFabLayout().setOnFabButtonClickListener(new FabButtonClickListener());
    }

    public void setPreselectedState() {

        if (getGoogleMap() != null && getFabLayout() != null) {
            getFabLayout().setPreSelectedState();
//            getGoogleMap().getUiSettings().setAllGesturesEnabled(false);
        }

    }

    private class FabButtonClickListener implements FabLayout.onFabButtonClickListener {
        @Override
        public void onFabButtonClicked(FloatingActionButton button, String tag, boolean selected) {

            if (getGoogleMap() == null)
                return;
            ConnectedCar.getInstance().addToState(tag, selected);
            if (tag.equalsIgnoreCase(KeyConstants.KEY_BUTTON_SATELLITE)) {
                getGoogleMap().setMapType(selected ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_TERRAIN);
            } else if (tag.equalsIgnoreCase(KeyConstants.KEY_BUTTON_TRAFFIC)) {
                getGoogleMap().setTrafficEnabled(selected);
            }
        }

    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Context getCTx() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     *
     */
    public void setToggleLocationInvisible() {
        toggleLocation.setVisibility(GONE);
    }
    public void setToggleLocationVisible() {
        toggleLocation.setVisibility(VISIBLE);
    }


    public void setOnMapLoadedListener(onMapLoadedListener onMapLoadedListener) {

        mMapLoadedListener = onMapLoadedListener;
    }

    public void setOnMapClickListener(MyMapTracking.onMapClickListener onMapClickListener) {
        this.onMapClickListener = onMapClickListener;
    }

    /**
     *
     */
    public interface onMapLoadedListener{
        void onMapLoaded();
    }

    public interface onMapClickListener{
        void onMapClick(LatLng latLng);
    }
    public interface onMarkerStartDragListener {
        void onMarkerDragStart(Marker  marker);
    }
    public interface onMarkerDragListener {
        void onMarkerDrag(Marker  marker);
    }
    public interface onMarkerEndDragListener {
        void onMarkerDragEnd(Marker  marker);
    }


    public void setmOnMarkerStartDragListener(onMarkerStartDragListener mOnMarkerStartDragListener) {
        this.mOnMarkerStartDragListener = mOnMarkerStartDragListener;
    }

    public void setmOnMarkerDragListener(onMarkerDragListener mOnMarkerDragListener) {
        this.mOnMarkerDragListener = mOnMarkerDragListener;
    }

    public void setmOnMarkerEndDragListener(onMarkerEndDragListener mOnMarkerEndDragListener) {
        this.mOnMarkerEndDragListener = mOnMarkerEndDragListener;
    }

    public Marker drawMarkerSmooth(final LatLng startPosition, final LatLng toPosition, MarkerOptions markerOptions, boolean shouldNotMoveCamera , final boolean hideMarker) {

        final Marker marker = drawMarkerWithoutAnimate(toPosition, markerOptions);
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final long duration = 3000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(toPosition, 16.0f));

        return marker;

    }

    public List<Marker> getDrawnMarkers() {

        return drawnMarkers;
    }


}
