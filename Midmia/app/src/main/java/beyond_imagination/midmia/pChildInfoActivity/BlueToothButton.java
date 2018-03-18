package beyond_imagination.midmia.pChildInfoActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import beyond_imagination.midmia.pChildInfoActivity.Bluetooth.BluetoothDeviceList;

/**
 * Created by cru65 on 2017-07-17.
 */

public class BlueToothButton extends android.support.v7.widget.AppCompatButton {

    private ChildInfoActivity mActivity;
    private BluetoothDeviceList bluetoothDeviceList;

    private static final int REQUEST_CONNECT_DEVICE = 4004;

    public BlueToothButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mActivity = (ChildInfoActivity)context;

        init();
    }

    private void init(){
        //bt = new BlueToothConnecting(mActivity);
        bluetoothDeviceList = new BluetoothDeviceList();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (bt.getDeviceState()) {
                //    bt.enableBluetooth();
                //}else{
                //    Toast.makeText(mActivity, "BlueTooth를 사용할 수가 없습니다.", Toast.LENGTH_SHORT).show();
                //}

                Intent intent = new Intent(mActivity, BluetoothDeviceList.class);
                mActivity.startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
            }
        });
    }
}
