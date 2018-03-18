package beyond_imagination.midmia.pMain;

/**
 * Created by cru65 on 2017-07-18.
 */

public class MyLocation {

    private double mlat;
    private double mlon;

    public MyLocation()
    {
        init();
    }

    private void init()
    {
        mlat = 0;
        mlon = 0;
    }

    public double getLat(){ return mlat; }

    public void setLat(double lat){ mlat = lat; }

    public double getLon(){ return mlon; }

    public void setLon(double lon){ mlon = lon; }
}
