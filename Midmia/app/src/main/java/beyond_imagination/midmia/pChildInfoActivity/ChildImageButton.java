package beyond_imagination.midmia.pChildInfoActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static beyond_imagination.midmia.pChildInfoActivity.ChildInfoActivity.PICK_FROM_CAMERA;

/**
 * Created by laggu on 2017-07-06.
 */

public class ChildImageButton extends android.support.v7.widget.AppCompatImageButton {

    private Activity activity;
    private Uri photoURI;

    public ChildImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity)context;
        init();
    }
    private void init(){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takePhotoByCamera();
                            }
                        })
                        .setNeutralButton("앨범선택", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takePhotoFromAlbum();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void takePhotoByCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "/Pictures/Midmia/tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        photoURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        activity.startActivityForResult(intent, ChildInfoActivity.PICK_FROM_CAMERA);
    }

    private void takePhotoFromAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        activity.startActivityForResult(intent, ChildInfoActivity.PICK_FROM_ALBUM);
    }

    Uri getPhotoURI(){
        return photoURI;
    }
}
