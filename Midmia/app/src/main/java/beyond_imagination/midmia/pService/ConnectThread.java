package beyond_imagination.midmia.pService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.pMain.ChildLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cru65 on 2017-07-17.
 */

public class ConnectThread extends Thread {
    /*** Variable ***/
    private Context mContext;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mDefaultChar;

    private ArrayList<BluetoothGattService> mGattServices
            = new ArrayList<BluetoothGattService>();
    private BluetoothGattService mDefaultService = null;

    private ArrayList<BluetoothGattCharacteristic> mGattCharacteristics
            = new ArrayList<BluetoothGattCharacteristic>();
    private ArrayList<BluetoothGattCharacteristic> mWritableCharacteristics
            = new ArrayList<BluetoothGattCharacteristic>();

    private Child child;

    private int state;
    private boolean isRunning;
    private boolean isUrgent;
    private String[] result;

    // String codes
    public static final int CONNECT = 1001;
    public static final int DISCONNECT = 1002;
    public static final int WAIT = 1003;

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // CallBack
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("connectThread", "onConnectionStateChange execute" + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                state = CONNECT;
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                state = DISCONNECT;
                disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //if (status == BluetoothGatt.GATT_SUCCESS) {
            //    BluetoothGattService service = gatt.getService(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));
            //    mBluetoothGattCharacteristic = service.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));
            //} else {
            Log.d("connectThread", "onServiceDiscovered execute");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                checkGattServices(gatt.getServices());
            } else {
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.d("connectThread", "onCharacteristicRead execute");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // getData(characteristic.getStringValue(10));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d("connectThread", "onCharacteristicChanged execute" + characteristic.getUuid());
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                //for(byte byteChar : data)
                //	stringBuilder.append(String.format("%02X ", byteChar));
                stringBuilder.append(data);
                getData(new String(data));
            }

            if (mDefaultChar == null && isWritableCharacteristic(characteristic)) {
                mDefaultChar = characteristic;
            }
        }
    };

    /*** Function ***/
    public void init(Child child, BluetoothAdapter adapter, Context context) {
        mBluetoothAdapter = adapter;
        mContext = context;
        this.child = child;

        state = WAIT;
        isRunning = true;
        isUrgent = false;
    }

    public ConnectThread(Child child, BluetoothAdapter adapter, Context context) {
        // Initialize
        init(child, adapter, context);
        Log.d("connectthread", "constructor execute");

        if (mBluetoothAdapter == null || child.getDeviceInfo() == null) {
            Log.w("ConnectThread", "BluetoothAdapter not initialized or unspecified address.");
        }

        mDevice = mBluetoothAdapter.getRemoteDevice(child.getDeviceInfo());

        if (mDevice == null) {
            Log.d("ConnectThread", "Device not found");
        }

        mBluetoothGatt = mDevice.connectGatt(context, false, mGattCallback);
        this.child.setIsConnect(1);
    }

    // Input, Output을 따로 구분하여 push하는 것이 아니라, pull을 사용하여 일반(1), 긴급(-1)로 구분하여 데이터 통신 한다.
    public void run() {
        Log.d("connectthread", "Thread가 시작되었습니다.");
        String sendData = "i";
        byte[] data = sendData.getBytes();
        byte[] buffer = new byte[1024];
        boolean isSend = false;


        long time = System.currentTimeMillis();
        long temp;
        while (true) {
            // Reading start.
            while (isRunning) {
                if (state != CONNECT) {
                    continue;
                }

                temp = System.currentTimeMillis();

                if (isUrgent) {
                    Log.d("connectThread", "긴급긴급!, data :" + data + "data length : " + data.length);

                    write(null, data);

                    isUrgent = false;
                } else if ((temp - time) > (child.getCycle() * 1000)) {
                    Log.d("connectThread", "돌아가는 중, data :" + data);

                    write(null, data);

                    time = temp;
                }
            }
        }
    }

    private int checkGattServices(List<BluetoothGattService> gattServices) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d("ConnectThread", "BluetoothAdapter not initialize");
            return -1;
        }

        mGattServices.clear();
        mGattCharacteristics.clear();
        mWritableCharacteristics.clear();

        Log.d("connectthread", gattServices.toString());

        for (BluetoothGattService gattService : gattServices) {
            mGattServices.add(gattService);

            // Extract characteristics
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                // Remember characteristic
                mGattCharacteristics.add(gattCharacteristic);

                boolean isWritable = isWritableCharacteristic(gattCharacteristic);
                if (isWritable) {
                    mWritableCharacteristics.add(gattCharacteristic);
                    Log.d("ConnectThread", "writeCharacteristic");
                }

                boolean isReadable = isReadableCharacteristic(gattCharacteristic);
                if (isReadable) {
                    readCharacteristic(gattCharacteristic);
                    Log.d("ConnectThread", "readChracteristic");
                }

                if (isNotificationCharacteristic(gattCharacteristic)) {
                    setCharacteristicNotification(gattCharacteristic, true);
                    Log.d("ConnectThread", "notification");
                    if (isWritable && isReadable) {
                        mDefaultChar = gattCharacteristic;
                    }
                }
            }
        }
        Log.d("connectthread", mWritableCharacteristics.size() + "");

        return mWritableCharacteristics.size();
    }

    private boolean isWritableCharacteristic(BluetoothGattCharacteristic chr) {
        if (chr == null) return false;

        final int charaProp = chr.getProperties();
        if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isReadableCharacteristic(BluetoothGattCharacteristic chr) {
        if (chr == null) return false;

        final int charaProp = chr.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNotificationCharacteristic(BluetoothGattCharacteristic chr) {
        if (chr == null) return false;

        final int charaProp = chr.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d("connectthread", "# BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d("connectthread", "# BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * Send data to remote device.
     *
     * @param chr
     * @param data
     * @return
     */
    public boolean write(BluetoothGattCharacteristic chr, byte[] data) {
        if (mBluetoothGatt == null) {
            Log.d("connectthread", "# BluetoothGatt not initialized");
            return false;
        }
        Log.d("connectthread", "" + data);

        BluetoothGattCharacteristic writableChar = null;

        if (chr == null) {
            if (mDefaultChar == null) {
                for (BluetoothGattCharacteristic bgc : mWritableCharacteristics) {
                    if (isWritableCharacteristic(bgc)) {
                        writableChar = bgc;
                    }
                }
                if (writableChar == null) {
                    Log.d("connectthread", "# Write failed - No available characteristic");
                    return false;
                }
            } else {
                if (isWritableCharacteristic(mDefaultChar)) {
                    Log.d("connectthread", "# Default GattCharacteristic is PROPERY_WRITE | PROPERTY_WRITE_NO_RESPONSE");
                    writableChar = mDefaultChar;
                } else {
                    Log.d("connectthread", "# Default GattCharacteristic is not writable");
                    mDefaultChar = null;
                    return false;
                }
            }
        } else {
            if (isWritableCharacteristic(chr)) {
                Log.d("connectthread", "# user GattCharacteristic is PROPERY_WRITE | PROPERTY_WRITE_NO_RESPONSE");
                writableChar = chr;
            } else {
                Log.d("connectthread", "# user GattCharacteristic is not writable");
                return false;
            }
        }

        writableChar.setValue(data);
        writableChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        boolean num = mBluetoothGatt.writeCharacteristic(writableChar);
        Log.d("connectthread", "통신결과 : " + num + ", UUID : " + writableChar.getUuid());
        mDefaultChar = writableChar;
        return true;
    }

    /**
     * @param data
     * @brief Get BlueToothData and handle to ChildLocation data.
     */
    private void getData(String data) {
        Log.d("connectThread", "data : " + data);
        String[] result = data.split("/");

        ChildLocation temp = new ChildLocation(Double.valueOf(result[0]), Double.valueOf(result[1]));
        child.setLocation(temp);

        ((Background_Service) mContext).setChildrenDataUpdate(true);
    }

    public void getNowLocation() {
        // 긴급 메시지를 줘서 현재 데이터 값을 받아온다.
        isUrgent = true;
    }

    public boolean reconnect() {
        if (mBluetoothGatt.connect()) {
            isRunning = true;
            return true;
        } else {
            return false;
        }
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w("ConnectThread", "BluetoothAdapter not initialized");
            return;
        }
        isRunning = false;
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;

        isRunning = false;
    }

    ////
    // Getter and Setter
    ////

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getGattState() {
        return state;
    }

    public void setChildCycle(int childCycle) {
        child.setCycle(childCycle);
    }
}
