package beyond_imagination.midmia.pChildInfoActivity;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import beyond_imagination.midmia.R;

/**
 * Created by laggu on 2017-07-13.
 */

public class GenderRadioGroup extends RadioGroup {
    private ChildInfoActivity activity;

    public GenderRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (ChildInfoActivity)context;
        init();
    }

    private void init(){
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.boy) {
                    activity.getChild().setGender(1);
                }
                else if(checkedId == R.id.girl){
                    activity.getChild().setGender(2);
                }
            }
        });
    }

    void setGender(int gender){
        if (gender == 1) {
            check(R.id.boy);
        }
        else if (gender == 2) {
            check(R.id.girl);
        }
    }
}