package beyond_imagination.midmia.pMainFragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.pMain.MyLocation;
import beyond_imagination.midmia.pMain.ChildLocation;

/**
 * Created by laggu on 2017-07-18.
 */

public class PointImageView extends android.support.v7.widget.AppCompatImageView {
    private float currentDegree = 0;
    private MyLocation myLocation;
    private ChildLocation childLocation;

    public PointImageView(Context context, MyLocation myLocation, Child child) {
        super(context);
        this.myLocation = myLocation;
        childLocation = child.getLocation();
    }

    public PointImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void rotateViewTo(float toDegree){
        float temp = getAngle(toDegree);
        float temp2 = toDegree;
        RotateAnimation r = new RotateAnimation(currentDegree, temp, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        currentDegree = temp;
        r.setDuration(400);
        r.setFillAfter(true);
        startAnimation(r);
    }
    //***********
    //삼각 함수 사용 시, 1,2,3,4 사분면 구분을 잘 해줘야 한다. 아니면 망함
    //***********
    public float getAngle(float angle)
    {
        double temp = 0;
        double x = childLocation.getLongitude() - myLocation.getLon();
        x *=10000;
        double y = childLocation.getLatitude() - myLocation.getLat();
        y*=10000;
        //*******************************
        // Math.atan2는 인수로 y축, x축 순서로 집어 넣어줘야 한다.
        // 결과값은 radian으로 나오므로 degree로 바꿔줘야한다.
        //*******************************
        if ((x > 0) && (y > 0)) {
            temp = 90 - Math.atan2(Math.abs(y),Math.abs(x))*180/Math.PI;
            //Log.d("asdfasdf", "1111111111 "+Double.toString(temp));
        } else if ((x < 0) && (y > 0)) {
            temp = -90 + Math.atan2(Math.abs(y),Math.abs(x))*180/Math.PI;
            //Log.d("asdfasdf", "22222222 "+Double.toString(temp));
        } else if ((x < 0) && (y < 0)) {
            temp = -90 - Math.atan2(Math.abs(y),Math.abs(x))*180/Math.PI;
            //Log.d("asdfasdf", "3333333333 "+ Double.toString(temp));
        } else if ((x > 0) && (y < 0)) {
            temp = 90 + Math.atan2(Math.abs(y),Math.abs(x))*180/Math.PI;
            //Log.d("asdfasdf", "4444444444  "+Double.toString(temp));
        }
        //temp = 90 - Math.toDegrees(Math.atan((childLocation.getLatitude() - myLocation.getLat())/(childLocation.getLongitude() - myLocation.getLon())));
        //temp = 90 - Math.atan2(y,x);

        temp = temp + angle;

        if(temp > 360.0)
            return (float)(temp - 360.0);
        else if(temp < -360)
            return (float) (temp + 360.0);
        else
            return (float)temp;
    }
}