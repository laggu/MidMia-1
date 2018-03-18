package beyond_imagination.midmia.pChildInfoActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import beyond_imagination.midmia.pMain.Child;

/**
 * Created by laggu on 2017-07-13.
 */

public class SaveButton extends android.support.v7.widget.AppCompatButton {
    private ChildInfoActivity activity;

    public SaveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (ChildInfoActivity)context;
        init();
    }

    private void init(){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Child child = activity.getChild();
                String str = child.checkFullFilled();
                if (str.equals("성공")) {
                    Intent intent = new Intent();
                    intent.putExtra("child", child);
                    Log.d("asdfasdf", child.getLocation() + " before parcelble");

                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
                else {
                    Toast.makeText(activity,str+" 입력 필요",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
