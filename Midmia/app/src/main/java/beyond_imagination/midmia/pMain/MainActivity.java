package beyond_imagination.midmia.pMain;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import beyond_imagination.midmia.R;
import beyond_imagination.midmia.pBackground.Background;
import beyond_imagination.midmia.pBackground.DangerAreas;
import beyond_imagination.midmia.pBackground.GPS;
import beyond_imagination.midmia.pMainFragment.MainFragment;
import beyond_imagination.midmia.pMapFragment.MapFragment;
import beyond_imagination.midmia.pService.Background_Service;
import beyond_imagination.midmia.pService.MiaDialog;
import beyond_imagination.midmia.pSettingFragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    /*** Variable ***/
    private FragmentTabHost mTabHost;
    private ArrayList<Child> children = null;
    private ImageView imageView;

    public static MyLocation myLocation;

    // Background
    private Background mBackground;
    private DangerAreas mDangerAreas;

    // Fragment
    private int nowFragment;
    public Fragment fragment;

    // Mia
    private MiaDialog miaDialog;

    // Gps
    private GPS mGps;

    // Googlemap 사용 중인지 아닌지.
    private boolean isMapRunning = false;
    private boolean isDangerousArea = false;

    // Bluetooth
    public static BluetoothAdapter mBluetoothAdapter;

    // String Codes
    public static final int LOCATION_UPDATE = 1001;
    public static final int MAIN_SHOW_CHILD_INFO = 1002;
    public static final int FRAGMENT_MAIN = 1;
    public static final int FRAGMENT_MAP = 2;
    public static final int FRAGMENT_SETTING = 3;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    public static final String BR_MIA_DIALOG = "mia";

    // Service
    public Background_Service background_service;
    private boolean isService = false;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Background_Service.MyBinder mb = (Background_Service.MyBinder) service;
            background_service = mb.getService();
            background_service.registerCallback(mCallback);

            // 둘 다 사용 가능한 방법이다. Callback함수를 사용하는 방법은 추후 사용할 수도 있으니 남겨두자.
            // mCallback.connectChildData(background_service.getChildren());
            background_service.setChildren(getChildren());
            background_service.setDangerAreas(getmDangerAreas());

            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    };

    // Callback(Service)
    private Background_Service.ICallback mCallback = new Background_Service.ICallback() {
        public void connectChildData(ArrayList<Child> children) {
            //background_service.setChildren(getChildren());
            children = getChildren();
        }
    };

    // Handler
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCATION_UPDATE:
                    ((MapFragment)fragment).onChildLocationChanged();
                    break;

                case MAIN_SHOW_CHILD_INFO:
                    if(nowFragment == FRAGMENT_MAIN){
                        Bundle bundle = msg.getData();
                        ((MainFragment)fragment).setCycleDistance(bundle.getInt("cycle"), bundle.getInt("distance"));
                    }
                    break;
            }
        }
    };

    // BroadcastReceiver
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("asdfasdf", "broadcastReceiver");
            if (intent.getStringExtra("type").equals(BR_MIA_DIALOG)) {
                miaDialog.show();
            }
        }
    };

    /*** Function ***/
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionAccepted) {
                if ( mGps.getmGoogleApiClient().isConnected() == false) {
                    mGps.getmGoogleApiClient().connect();
                }
            } else {
                mGps.checkPermissions();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.mainBackground);

        changeBackground();

        checkBluetoothAvailable();

        mBackground = new Background(this);
        mBackground.execute();

        readChildrenData();

        myLocation = new MyLocation();

        miaDialog = new MiaDialog(this, null);

        registerBR();

        Intent intent = new Intent(MainActivity.this, Background_Service.class);
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        setUpTabHost();
    }

    public void readChildrenData()
    {
        Database.setDatabase(this);
        children = Database.readFromDatabase();
    }

    private void registerBR() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("asdf");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBluetoothAvailable() {
        Log.d("MainActivity", "checkBluetoothAvailable");

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            }else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show();
        }

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "해당 기기는 블루투스 4.0을 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if ( mBluetoothAdapter == null){
            Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("main").setIndicator("main"), MainFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("map").setIndicator("map"), MapFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("setting").setIndicator("setting"), SettingFragment.class, null);
        mTabHost.setCurrentTab(0);
    }

    // 내 위치 갱신
    public void setMyLocation(double lat, double lon) {
        myLocation.setLat(lat);
        myLocation.setLon(lon);
    }

    // 현재 위치와 아이들의 현재 위치를 재 갱신.
    public void updateLocation() {
        background_service.reNewChildrenData();
    }

    private void changeBackground()
    {
        String now = getCurrentTime();

        if (now.equals("아침") == true) {
            imageView.setBackgroundResource(R.drawable.background6);
        } else if (now.equals("오후") == true) {
            imageView.setBackgroundResource(R.drawable.background12);
        } else if (now.equals("오후") == true) {
            imageView.setBackgroundResource(R.drawable.background18);
        } else {
            imageView.setBackgroundResource(R.drawable.background24);
        }
    }

    public String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyy/MM/dd HH:mm:ss");
        String formatDate = sdfNow.format(date);
        String[] temp = formatDate.split(" ");
        temp = temp[1].split(":");
        int hour = Integer.valueOf(temp[0]);

        if (hour < 6) {
            return "밤";
        } else if (hour < 12) {
            return "아침";
        } else if (hour < 18) {
            return "오후";
        } else {
            return "저녁";
        }
    }

    public void setFragment(Fragment fragment) {
        Log.d("MainActivity", "setFragment execute");

        this.fragment = fragment;

        if(fragment.getClass() == MainFragment.class)
        {
            nowFragment = FRAGMENT_MAIN;
        }

        // MapFragment
        if (fragment.getClass() == MapFragment.class) {
            nowFragment = FRAGMENT_MAP;
        }

        // SettingFragment
        if(fragment.getClass() == SettingFragment.class)
        {
            nowFragment = FRAGMENT_SETTING;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        unbindService(conn);
    }

    ////
    // Getter, Setter
    ////


    public Handler getHandler() {
        return handler;
    }

    public ArrayList getChildren() {
        return children;
    }

    public MyLocation getMyLocation() {
        return myLocation;
    }

    public GPS getmGps() {
        return mGps;
    }

    public void setmGps(GPS mGps2) {
        this.mGps = mGps2;
    }

    public int getNowFragment() {
        return nowFragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public DangerAreas getmDangerAreas() {
        return mDangerAreas;
    }

    public void setmDangerAreas(DangerAreas mDangerAreas) {
        this.mDangerAreas = mDangerAreas;
    }

    public boolean getisDangerousArea() {
        return isDangerousArea;
    }

    /**
     * 위험한 지역이면 아이들 안전거리, 주기를 조절해주고, 위험한 지역이 아니면 DB에 저장했던 원래 정보를 가져온다.
     * @param dangerousArea
     */
    public void setisDangerousArea(boolean dangerousArea) {
        if (isDangerousArea != dangerousArea) {

            isDangerousArea = dangerousArea;

            if (isDangerousArea) {
                for (Child child : children) {
                    child.setDistance(10);
                    child.setCycle(1);
                }
            } else {
                children = Database.readFromDatabase();
            }
        }
    }
}
