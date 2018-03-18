package beyond_imagination.midmia.pMapFragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.pBackground.DangerAreas;
import beyond_imagination.midmia.pMain.MainActivity;
import beyond_imagination.midmia.R;
//import beyond_imagination.midmia.pService.Server;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by laggu on 2017-07-15.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    ViewGroup rootView;
    LatLng myLatLng;
    ArrayList<Child> children;

    ArrayList<Marker> childMarker;
    ArrayList<Circle> childCircle;

    ArrayList<DangerAreas.DangerArea> dangerList;
    ArrayList<Circle> dangerCircle;

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;

    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;

    // Color
    int color[] = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN};

    // Codes
    private static final String TAG = "MapFragment";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // Location
    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    /*** Function ***/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleServiceIdentify();
        init();
    }

    public void init() {
        children = ((MainActivity) getActivity()).getChildren();
        dangerList = ((MainActivity) getActivity()).getmDangerAreas().getAreaList();
        childMarker = new ArrayList<Marker>();
        childCircle = new ArrayList<Circle>();
        dangerCircle = new ArrayList<Circle>();
    }

    private void googleServiceIdentify()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        ((MainActivity) getActivity()).setFragment(getFragmentManager().findFragmentByTag("map"));

        return rootView;
    }

    public void setCurrentLocation(Location location) {

        mMoveMapByUser = false;

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if ( mMoveMapByAPI ) {

            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    public void setDefaultLocation() {
        mMoveMapByUser = false;

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        myLatLng = DEFAULT_LOCATION;
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPSNO 활성 요부 확인하세요";

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }

    // 외부에서 아이에 대한 정보를 받아온다.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("asdfasdf", "onMapReady");
        mGoogleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPSNO 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(25));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (mMoveMapByUser == true && mRequestingLocationUpdates){
                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }
                mMoveMapByUser = true;
            }
        });

        // Child Marker
        // Color를 입히기 위해 int로 바꿈
        for (int i = 0; i < children.size(); i++) {
            Log.d("asdfasdf", children.get(i).getName());
            Log.d("asdfasdf", "" + children.get(i).getLocation());
            childMarker.add(googleMap.addMarker(MarkerOptions_t(new LatLng(children.get(i).getLocation().getLatitude(), children.get(i).getLocation().getLongitude()), children.get(i).getName(), i)));
            childCircle.add(googleMap.addCircle(new CircleOptions().center(myLatLng).radius(children.get(i).getDistance()).strokeColor(color[i])));
        }

        String[] data;
        String body;

        // DangerArea
        for (int i = 0; i < dangerList.size(); i++) {
            Log.d("asdfasdf", i + "");
            dangerCircle.add(googleMap.addCircle(new CircleOptions().center(dangerList.get(i).getmLatLng()).radius(dangerList.get(i).getRadius()).fillColor(Color.argb(100, 200,0,0)).strokeColor(Color.argb(100, 200,0,0))));
        }
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(25));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged : ");

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location);
        mCurrentLocatiion = location;
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        for(Circle circle : childCircle) {
            circle.remove();
        }

        childCircle.clear();
        Log.d("asdfasdf", "asdfasdf"+children+","+children.size());
        for (int i = 0; i < children.size(); i++) {
            childCircle.add(mGoogleMap.addCircle(new CircleOptions().center(myLatLng).radius(children.get(i).getDistance()).strokeColor(color[i])));
        }
    }

    public void onChildLocationChanged()
    {
        for(int i = 0; i<childMarker.size();i++) {
            childMarker.get(i).remove();
        }
        childMarker.clear();

        for (int i = 0; i < children.size(); i++) {
            childMarker.add(mGoogleMap.addMarker(MarkerOptions_t(new LatLng(children.get(i).getLocation().getLatitude(), children.get(i).getLocation().getLongitude()), children.get(i).getName(), i)));
        }
    }

    /**
     * FusedLocationAPI는 Google I/O 2013 에서 발표한 LocationManager의 상위단계!
     * 이것을 사용하여 위치정보를 찾는다!
     */
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest,this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);
        }

    }

    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        mRequestingLocationUpdates = false;
    }

    public MarkerOptions MarkerOptions_t(LatLng temp, String name, int num) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(temp);
        markerOptions.title(name);
        switch(num)
        {
            case -1:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
            case 0:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
            case 1:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                break;
            case 2:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case 3:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                break;
            case 4:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                break;
        }
        return markerOptions;
    }

    @Override
    public void onStart() {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    public void onStop() {
        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        if (i == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (i == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            // 재 실행 되었을 때를 대비해서, 현재 Fragment를 지워준다.
            ViewGroup parent = (ViewGroup) rootView.getParent();
            parent.removeView(rootView);
        }
    }

    ////// Permission check
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if ( mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionAccepted) {
                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }
            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getActivity().getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}