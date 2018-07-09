package beyond_imagination.midmia.pMainFragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

import beyond_imagination.midmia.pMain.MainActivity;

/**
 * Created by laggu on 2017-07-18.
 */

public class OrientationSensing implements SensorEventListener{
    private MainActivity activity;
    private Sensor s;
    private SensorManager sm;
    private ArrayList<PointImageView> points;

    OrientationSensing(MainActivity activity, ArrayList<PointImageView> points){
        this.activity = activity;
        sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        this.points = points;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
            float azimuth =  event.values[0];

            azimuth += 90;

            if (azimuth > 360) {
                azimuth -=360;
            }

            try {
                for (PointImageView point : points)
                    point.rotateViewTo((-1) * (azimuth));
            }
            catch (Exception e){

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void registerListener(){
        sm.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
    }

    void unregisterListener(){
        sm.unregisterListener(this);
    }
}
