package beyond_imagination.midmia.pService;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import beyond_imagination.midmia.pBackground.DangerAreas;
import beyond_imagination.midmia.pMain.Child;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cru65 on 2017-07-13.
 */

public class Background_Service extends Service {
    /*** Variable ***/
    // Linked count
    private static int conn_count;

    // Children data
    private ArrayList<Child> children;
    private boolean isChildrenDataUpdate;

    // DangerArea
    private DangerAreas dangerAreas = null;

    // Mia
    private CheckMia checkMia;

    // Binder
    IBinder mBinder = new MyBinder();

    // Bluetooth
    private BluetoothAdapter btAdapter;
    //private ConnectThread[] mConnectThread;
    private ArrayList<ConnectThread> mConnectThread;
    private ReconnectBluetooth reconnectBluetooth;

    // onServiceConnected에서 연결되고나면 IBinder을 매개변수로 전달 받는데, 이 변수를 통해 생성된 서비스와 연결하기 위해 만든 클래스.
    public class MyBinder extends Binder {
        public Background_Service getService() {
            return Background_Service.this;
        }
    }

    // 액티비티에 접근하기위한 콜백
    private ICallback mCallback;

    /*** Function ***/
    public Background_Service()
    {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    public void init() {
        isChildrenDataUpdate = false;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        //mConnectThread = new ConnectThread[5];
        mConnectThread = new ArrayList<ConnectThread>();
        reconnectBluetooth = new ReconnectBluetooth(this);

        // Callback 함수를 이용하여 children 동기화
        // mCallback.connectChildData();
        // ******<중요>******
        // init함수 내에서 Callback함수를 사용하여 children을 초기화하려고했으나, init이 실행된 후에 MainActivity.conn serviceconnected 함수가 실행이 되므로  mCallback은 null값을 가져 실행이 되지 않는다. 따라서, children은 나중에 초기화를 시켜준다.
        // [순서] servicebind -> service create -> service connected
        // ******************
        conn_count = 0;

        checkMia = new CheckMia(this, dangerAreas, children);
    }

    public void startBlueTooth(Intent data) {
        Log.d("backgoundservice", "startBluetooth");
        Child newChild = data.getParcelableExtra("child");

        //////// 쓰레드 중복 확인해서 만들도록 하기.
        for(Child child : children)
        {
            Log.d("backgroundservice", child.getName());
            if(child.getName().equals(newChild.getName()) == true) {
                Log.d("backgoundservice", "중복되는 아이가 있음");
                return;
            }
        }

        // 중복 아이가 없으면 아이의 데이터 추가후 쓰레드 실행.
        children.add(newChild);

        // connect 부분 여기로 합칠 것인지 생각해보기.
        connect(newChild);
    }

    public synchronized  void connect(Child child) {
        Log.d("backgoundservice", "connect start");
        ConnectThread temp = new ConnectThread(child, btAdapter, this);
        Log.d("asdfasdf", getApplicationContext().toString());
        Log.d("asdfasdf", this.toString());

        while(temp.getGattState() == ConnectThread.WAIT) {
        }

        if(temp.getGattState() == ConnectThread.CONNECT){
            Log.d("backgoundservice", "connect success");
            temp.start();
        }else{
            Log.d("backgoundservice", "connect fail");
            return;
        }
        //mConnectThread[conn_count++] = temp;
        Log.d("backgoundservice", "실행 중" + mConnectThread.size());
        mConnectThread.add(temp);
        Log.d("backgoundservice", "실행 중" + mConnectThread.size());
        conn_count++;
        // mConnectThread[conn_count++].start();
    }

    public void disConnect()
    {
        if(conn_count != 0)
            conn_count--;
    }

    public void disConnectChild(Child child)
    {
        if(conn_count >0) {
            for (ConnectThread connectThread : mConnectThread) {
                if (connectThread.getChild().equals(child)) {
                    connectThread.close();

                    disConnect();
                }
            }
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    // Callback 함수를 이용하여 children 동기화
    public interface ICallback {
        public void connectChildData(ArrayList<Child> children);
    }

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    public void reNewChildrenData()
    {
        if(conn_count > 0) {
            for (ConnectThread connectThread : mConnectThread) {
                connectThread.getNowLocation();
            }
        }
    }

    ////
    // Getter and Setter
    ////

    public ArrayList<Child> getChildren() {
        return children;
    }

    public ArrayList<ConnectThread> getmConnectThread() {
        return mConnectThread;
    }

    public boolean isChildrenDataUpdate() {
        return isChildrenDataUpdate;
    }

    public synchronized void setChildrenDataUpdate(boolean childrenDataUpdate) {
        isChildrenDataUpdate = childrenDataUpdate;
        Log.d("backgroundservice", "childrendataupdate - " + childrenDataUpdate);
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
    }

    public void setDangerAreas(DangerAreas dangerAreas){
        this.dangerAreas = dangerAreas;
    }
}
