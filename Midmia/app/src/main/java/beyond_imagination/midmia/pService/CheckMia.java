package beyond_imagination.midmia.pService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

import beyond_imagination.midmia.R;
import beyond_imagination.midmia.pBackground.DangerAreas;
import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.pMain.MainActivity;

/**
 * Created by cru65 on 2017-10-15.
 */

public class CheckMia extends Thread {
    private Context backgroundContext;
    private DangerAreas dangerAreas;
    private ArrayList<Child> children;

    private NotificationManager nm;
    private Notification noti;

    private boolean isRunning = false;

    public CheckMia(Context context, DangerAreas dangerAreas, ArrayList<Child> children){
        Log.d("checkmia", "constructor execute");
        backgroundContext = context;
        this.dangerAreas = dangerAreas;
        this.children = children;

         nm = (NotificationManager) backgroundContext.getSystemService(Context.NOTIFICATION_SERVICE);

        isRunning = true;

        start();
    }

    private void notification()
    {
        Log.d("checkmia", "notification execute");

        Intent intent = new Intent(backgroundContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(backgroundContext, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        noti = new Notification.Builder(backgroundContext.getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("WARNING!!!").setContentText("아이가 안전거리를 벗어났습니다!").setContentIntent(pendingIntent).setTicker("WARNING-미아!").setAutoCancel(true).build();
        noti.vibrate = new long[]{500, 500, 500};

        nm.notify(1234, noti);

        intent = new Intent("asdf");
        intent.putExtra("type", MainActivity.BR_MIA_DIALOG);

        backgroundContext.sendBroadcast(intent);

        ((Background_Service)backgroundContext).setChildrenDataUpdate(false);
    }

    @Override
    public void run() {
        Location location = new Location("");

        while (isRunning) {
            if (((Background_Service)backgroundContext).isChildrenDataUpdate()) {
                if (children != null) {
                    Log.d("checkmia", "새로운 미아 위치 데이터로 위험지역 판별 중");
                    for (Child child : children) {
                        location.setLatitude(child.getLocation().getLatitude());
                        location.setLongitude(child.getLocation().getLongitude());

                        if (dangerAreas.checkInDangerArea(location)) {
                            notification();
                        }
                    }
                }
            }
        }
    }
}
