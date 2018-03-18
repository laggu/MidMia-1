package beyond_imagination.midmia.pBackground;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import beyond_imagination.midmia.pMain.MainActivity;
import beyond_imagination.midmia.pMainFragment.MainFragment;

/**
 * Created by cru65 on 2017-10-12.
 */

public class Background extends AsyncTask<Void, Boolean, Void> {
    /***** Variable *****/
    private MainActivity mMainActivity;
    private DangerAreas dangerAreas;

    private DangerAreaDialog dangerAreaDialog;

    private GPS mGps;

    private boolean isRunning = false;

    /***** Function *****/
    public Background(Context context) {
        super();
        mMainActivity = (MainActivity) context;
    }

    private void init() {
        dangerAreas = new DangerAreas(mMainActivity);
        mMainActivity.setmDangerAreas(dangerAreas);
        dangerAreaDialog = new DangerAreaDialog(mMainActivity);
        mGps = new GPS(mMainActivity);
        mMainActivity.setmGps(mGps);

        isRunning = true;
    }

    @Override
    protected void onPreExecute() {
        Log.d("Background", "Background work start");
        init();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        if(mMainActivity.getisDangerousArea() == values[0])
            return;

        if(values[0] == true) {
            mMainActivity.setisDangerousArea(true);

            if (mMainActivity.getNowFragment() == MainActivity.FRAGMENT_MAIN) {
                ((MainFragment)mMainActivity.getFragment()).settingDangerousArea(true);
            }

            dangerAreaDialog.show();

        } else {
            mMainActivity.setisDangerousArea(false);

            if (mMainActivity.getNowFragment() == MainActivity.FRAGMENT_MAIN) {
                ((MainFragment)mMainActivity.getFragment()).settingDangerousArea(false);
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        long time = System.currentTimeMillis();
        long temp = time;

        while (isRunning) {
            time = System.currentTimeMillis();

            if (time - temp >= 1000) {
                if (dangerAreas.checkInDangerArea(mGps.getmCurrentLocatiion())) {
                    publishProgress(true);
                } else {
                    publishProgress(false);
                }

                temp = time;
            }
        }
        return null;
    }
}
