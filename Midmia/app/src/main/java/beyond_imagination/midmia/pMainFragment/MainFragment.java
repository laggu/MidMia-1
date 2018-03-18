package beyond_imagination.midmia.pMainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.pMain.Database;
import beyond_imagination.midmia.pMain.MainActivity;
import beyond_imagination.midmia.pMain.MyLocation;
import beyond_imagination.midmia.R;
import beyond_imagination.midmia.pChildInfoActivity.ChildInfoActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by laggu on 2017-07-15.
 */

public class MainFragment extends Fragment {
    public static final int CHILD_INFO_ACTIVITY = 1001;

    private MyLocation myLocation;
    private ArrayList<Child> children;
    private ImageView addChildImage;
    private TextView signalCycle;
    private TextView distance;
    private FrameLayout dangerAreaImage;
    private ChildrenViewFlipper childrenViewFlipper;
    private ArrayList<PointImageView> points;
    ViewGroup rootView;

    private OrientationSensing orientationSensing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        myLocation = ((MainActivity)getActivity()).getMyLocation();
        children = ((MainActivity)getActivity()).getChildren();

        signalCycle = (TextView) rootView.findViewById(R.id.SignalCycle);
        distance = (TextView) rootView.findViewById(R.id.Distance);
        dangerAreaImage = (FrameLayout) rootView.findViewById(R.id.dangerarea_image);

        setPoints();
        setAddChildButton();
        setCycleDistance(0,0);

        orientationSensing = new OrientationSensing((MainActivity)getActivity(), points);

        ((MainActivity) getActivity()).setFragment(getFragmentManager().findFragmentByTag("main"));

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHILD_INFO_ACTIVITY:
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = data.getExtras();
                        Child child = bundle.getParcelable("child");
                        //child.getLocation().setLatitude(50);
                        //child.getLocation().setLongitude(127);
                        //children.add(child);
                        ((MainActivity)getActivity()).background_service.startBlueTooth(data);
                        Log.d("mainfragment", "새로운 아이 생성!");
                        Database.insertRecord(child);
                    }
                });
                thread.start();

                // Fragment restart
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
        }
    }

    private void setAddChildButton(){
        addChildImage = (ImageView) rootView.findViewById(R.id.newchildimage);
        addChildImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChildInfoActivity.class);
                intent.putExtra("flag",1);
                startActivityForResult(intent, CHILD_INFO_ACTIVITY);
            }
        });
    }

    private void setPoints()
    {
        points = new ArrayList<>();
        for(int i = 0; i < children.size(); i++) {
            PointImageView point = new PointImageView(getContext(), myLocation, children.get(i));

            ConstraintLayout layout = (ConstraintLayout) (rootView.findViewById(R.id.constraintLayout_mainFragment));
            ConstraintSet set = new ConstraintSet();

            point.setId(100+i);
            point.setImageResource(R.drawable.point);
            layout.addView(point);
            set.constrainHeight(point.getId(), 600);
            set.constrainWidth(point.getId(), 1400);
            set.centerHorizontally(point.getId(), ConstraintSet.PARENT_ID);
            set.centerVertically(point.getId(), ConstraintSet.PARENT_ID);

            set.applyTo(layout);

            points.add(point);
        }
    }

    public void settingDangerousArea(boolean result)
    {
        if (result) {
            dangerAreaImage.setVisibility(View.VISIBLE);
        } else {
            dangerAreaImage.setVisibility(View.INVISIBLE);
            ((MainActivity)getActivity()).readChildrenData();
        }
    }

    public void setCycleDistance(int cycle, int distance) {
        signalCycle.setText(String.valueOf(cycle));
        this.distance.setText(String.valueOf(distance));
    }

    @Override
    public void onResume() {
        super.onResume();
        orientationSensing.registerListener();

        Log.d("mainfragment", ""+((MainActivity)getActivity()).getisDangerousArea());
        if(((MainActivity)getActivity()).getisDangerousArea() == true){
            settingDangerousArea(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        orientationSensing.unregisterListener();
    }

    public ArrayList getChildren(){
        return children;
    }
}