package beyond_imagination.midmia.pBackground;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import beyond_imagination.midmia.pMain.MainActivity;

/**
 * Created by cru65 on 2017-10-12.
 */

public class DangerAreas {
    /*** Variable ***/
    MainActivity mainActivity;
    ArrayList<DangerArea> areaList;

    /*** Function ***/
    public DangerAreas(Context context) {
        mainActivity = (MainActivity) context;
        areaList = new ArrayList<DangerArea>();
        readJSON();
    }

    private void readJSON() {
        Log.d("background", "readJson");
        AssetManager assetManager;
        InputStream input = null;
        String temp = "";
        JSONObject json;
        int size;
        byte buf[];
        String[] data;

        assetManager = mainActivity.getResources().getAssets();

        try {
            input = assetManager.open("dangerarea.json");
            size = input.available();
            Log.d("background", String.valueOf(size));
            buf = new byte[size];
            input.read(buf);

            temp = new String(buf);

            json = new JSONObject(temp);

            for(int i = 0; i<json.length(); i++) {
                temp = json.getString(String.valueOf(i));

                data = temp.split(",");

                areaList.add(new DangerArea(Double.valueOf(data[0]), Double.valueOf(data[1]), Integer.valueOf(data[2])));
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (input != null) {
            try { input.close() ;
            } catch (Exception e) {
                e.printStackTrace() ;
            }
        }
    }

    public int getLength() {
        return areaList.size();
    }

    public ArrayList<DangerArea> getAreaList() {
        return areaList;
    }

    public void setAreaList(ArrayList<DangerArea> areaList) {
        this.areaList = areaList;
    }

    public boolean checkInDangerArea(Location location){
        if (location == null) {
            return false;
        }
        float distance;

        for(DangerArea area : areaList){
            distance = location.distanceTo(area.getLocation());

            if (distance < area.getRadius()) {
                return true;
            }
        }
        return false;
    }

    public class DangerArea {
        private double lat;
        private double lon;
        private int radius;
        private LatLng mLatLng;
        private Location location;

        public DangerArea(double lat, double lon, int radius) {
            this.lat = lat;
            this.lon = lon;
            this.radius = radius;
            mLatLng = new LatLng(lat, lon);
            location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lon);
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public LatLng getmLatLng() {
            return mLatLng;
        }

        public void setmLatLng(LatLng mLatLng) {
            this.mLatLng = mLatLng;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }
}
