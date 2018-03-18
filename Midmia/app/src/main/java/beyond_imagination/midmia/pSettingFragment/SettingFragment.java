package beyond_imagination.midmia.pSettingFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import beyond_imagination.midmia.R;
import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.pMain.Database;
import beyond_imagination.midmia.pMain.MainActivity;
import beyond_imagination.midmia.pChildInfoActivity.ChildInfoActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by laggu on 2017-07-15.
 */

public class SettingFragment extends ListFragment {
    public static final int CHILD_INFO_ACTIVITY = 3001;

    private MainActivity activity;
    private ChildAdapter adapter;
    private ArrayList<Child> children;
    ViewGroup rootView;
    private int lastClickedPosition;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        activity = (MainActivity)getActivity();
        children = activity.getChildren();
        adapter = new ChildAdapter(children);
        setListAdapter(adapter);

        // 현재 fragment에 대한 정보를 main에다가 넣는다.
        ((MainActivity) getActivity()).setFragment(getFragmentManager().findFragmentByTag("setting"));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Child child = children.get(position);
        lastClickedPosition = position;

        Intent intent = new Intent(getActivity(), ChildInfoActivity.class);
        intent.putExtra("flag",2);
        intent.putExtra("child",child);

        startActivityForResult(intent, CHILD_INFO_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHILD_INFO_ACTIVITY:
                Bundle bundle = data.getExtras();
                Child child = bundle.getParcelable("child");

                // 라구가 가지고 있는 코드를 사용할 것.
                children.set(lastClickedPosition, child);
                ((MainActivity)getActivity()).background_service.startBlueTooth(data);
                Log.d("mainfragment", "새로운 아이 생성!");
                Database.updateRecord(child);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 1, 0, "수정");
        menu.add(0, 2, 0, "제거");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        ChildItemView view = (ChildItemView)menuInfo.targetView;
        Child child = view.getChild();
        switch(item.getItemId()){
            case 1:
                Intent intent = new Intent(getActivity(), ChildInfoActivity.class);
                intent.putExtra("flag",2);
                intent.putExtra("child",child);
                startActivityForResult(intent, CHILD_INFO_ACTIVITY);
                return true;
            case 2:
                Database.deleteRecord(child);
                ((MainActivity)getActivity()).background_service.disConnectChild(child);
                children.remove(child);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                return true;
        }
        return false;
    }
}