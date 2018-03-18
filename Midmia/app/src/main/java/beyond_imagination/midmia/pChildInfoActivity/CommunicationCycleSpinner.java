package beyond_imagination.midmia.pChildInfoActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Created by laggu on 2017-07-05.
 */

public class CommunicationCycleSpinner extends android.support.v7.widget.AppCompatSpinner {

    private ChildInfoActivity activity;
    private ArrayAdapter<String> adapter;
    private String[] items = {"실시간", "5초", "15초", "30초", "1분", "3분", "5분", "10분"};

    public CommunicationCycleSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (ChildInfoActivity)context;
        init();
    }

    private void init(){
        adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setAdapter(adapter);
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity.getChild().setCycle(getCycle(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void setCycle(int seconds){
        switch (seconds){
            case 1:
                setSelection(0);
                break;
            case 5:
                setSelection(1);
                break;
            case 15:
                setSelection(2);
                break;
            case 30:
                setSelection(3);
                break;
            case 60:
                setSelection(4);
                break;
            case 180:
                setSelection(5);
                break;
            case 300:
                setSelection(6);
                break;
            case 600:
                setSelection(7);
                break;
            default:
                setSelection(4);
                break;
        }
    }

    private int getCycle(int position){
        switch (position){
            case 0:
                return 1;
            case 1:
                return 5;
            case 2:
                return 15;
            case 3:
                return 30;
            case 4:
                return 60;
            case 5:
                return 180;
            case 6:
                return 300;
            case 7:
                return 600;
            default:
                return 0;
        }
    }
}