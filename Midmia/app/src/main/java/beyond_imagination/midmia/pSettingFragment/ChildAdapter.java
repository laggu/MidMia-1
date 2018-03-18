package beyond_imagination.midmia.pSettingFragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import beyond_imagination.midmia.pMain.Child;

/**
 * Created by laggu on 2017-07-16.
 */

public class ChildAdapter extends BaseAdapter {
    ArrayList<Child> items;

    ChildAdapter(ArrayList<Child> children){
        items = children;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        ChildItemView view = new ChildItemView(context);
        Child child = items.get(position);

        view.setChild(child);
        view.setChildImageView(child.getPhoto());
        view.setNameTextView(child.getName());
        view.setAgeTextView(String.valueOf(child.getAge()));
        view.setGenderTextView(child.getGender()==1?"남":"여");

        return view;
    }
}
