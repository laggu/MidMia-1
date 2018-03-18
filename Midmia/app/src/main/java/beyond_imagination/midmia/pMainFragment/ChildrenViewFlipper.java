package beyond_imagination.midmia.pMainFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.pMain.MainActivity;
import beyond_imagination.midmia.R;
import beyond_imagination.midmia.pService.MiaDialog;

/**
 * Created by laggu on 2017-07-17.
 */

public class ChildrenViewFlipper extends ViewFlipper {
    MainActivity activity;
    Handler mHandler;
    private ArrayList<Child> children;
    Animation slide_in_left, slide_out_right, slide_in_right, slide_out_left;
    float downX;
    int currentIndex;
    MiaDialog miaDialog;

    public ChildrenViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (MainActivity) context;
        miaDialog = new MiaDialog(activity, null);
        init();
        setSlide();
    }

    private void init() {
        children = activity.getChildren();
        mHandler = activity.getHandler();

        if(children.size() == 0){
            currentIndex = 0;
        } else{
            for (int i = 0; i < children.size(); ++i) {
                ChildImageView image = new ChildImageView(getContext(), children.get(i).getPhoto());
                addView(image);
            }

            showChildInfo(children.get(0).getCycle(), children.get(0).getDistance());
        }
    }

    private void setSlide() {
        slide_in_left = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(activity, android.R.anim.slide_out_right);
        slide_in_right = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
        slide_out_left = AnimationUtils.loadAnimation(activity, R.anim.slide_out_left);

        setOnTouchListener(MyTouchListener);
    }

    private void MoveNextView() {
        setInAnimation(slide_in_right);
        setOutAnimation(slide_out_left);
        showNext();
        currentIndex = (++currentIndex)%children.size();
        showChildInfo(children.get(currentIndex).getCycle(), children.get(currentIndex).getDistance());
    }

    private void MovewPreviousView() {
        setInAnimation(slide_in_left);
        setOutAnimation(slide_out_right);
        showPrevious();
        --currentIndex;
        if(currentIndex < 0)
            currentIndex += children.size();
        showChildInfo(children.get(currentIndex).getCycle(), children.get(currentIndex).getDistance());
    }

    private void showChildInfo(int cycle, int distance)
    {
        Message msg =  mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        msg.what = MainActivity.MAIN_SHOW_CHILD_INFO;
        bundle.putInt("cycle", cycle);
        bundle.putInt("distance", distance);
        msg.setData(bundle);

        msg.sendToTarget();
    }

    private void UpdateChildrenLocation() {
        activity.updateLocation();

        miaDialog.show();
    }

    View.OnTouchListener MyTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                downX = (int) event.getX();
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                int upX = (int) event.getX();

                // left slide
                if (upX < downX - 30) {
                    MoveNextView();
                }
                // right slide
                else if (upX > downX + 30) {
                    MovewPreviousView();
                }
                // click
                else {
                    UpdateChildrenLocation();
                }

                downX = upX;
            }

            return true;
        }
    };
}
