package beyond_imagination.midmia.pChildInfoActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Created by laggu on 2017-07-05.
 */

public class AgeSpinner extends android.support.v7.widget.AppCompatSpinner {

    private ChildInfoActivity activity;
    private ArrayAdapter<Integer> adapter;
    private Integer[] items = new Integer[8];

    public AgeSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (ChildInfoActivity)context;
        init();
    }

    private void init(){
        for(int i = 0; i < 8; ++i)
            items[i] = i+3;
        adapter = new ArrayAdapter<Integer>(activity, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setAdapter(adapter);
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity.getChild().setAge(items[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void setChildAge(int age){
        setSelection(age-3);
    }
}