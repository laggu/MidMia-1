package beyond_imagination.midmia.pService;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by cru65 on 2017-10-15.
 */

public class ReconnectBluetooth extends Thread {
    private Background_Service background_service;
    private ArrayList<ConnectThread> connectThreads;

    private boolean isRunning = false;

    public ReconnectBluetooth(Context context) {
        background_service = (Background_Service) context;
        connectThreads = background_service.getmConnectThread();

        isRunning = true;

        start();
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        long temp = time;

        while (isRunning) {
            time = System.currentTimeMillis();

            if ((time - temp) > 1000) {
                Log.d("reconnectbluetooth", "실행 중" + connectThreads.size());
                Log.d("reconnectbluetooth", "실행 중" + background_service.getmConnectThread().size());
                if (connectThreads != null) {
                    for (ConnectThread connectThread : connectThreads) {
                        if (connectThread.isRunning() == false) {
                            // 디바이스 연결.
                            // 연결 되면 다시 초기화.
                            // start();
                            reConnectDevice(connectThread);
                        }
                    }
                }
                temp = time;
            }
        }

    }

    private void reConnectDevice(ConnectThread connectThread) {
        if (connectThread.reconnect()) {
            Log.d("reconnectbluetooth", "reconnect 되었습니다.");
        } else {
            Log.d("reconnectbluetooth", "reconnect 실패했습니다.");
        }
    }
}

