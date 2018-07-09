package beyond_imagination.midmia.pChildInfoActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.R;

import beyond_imagination.midmia.pChildInfoActivity.Bluetooth.*;

public class ChildInfoActivity extends AppCompatActivity {

    public static final int PICK_FROM_CAMERA = 4001;
    public static final int PICK_FROM_ALBUM = 4002;
    public static final int CROP_THE_IMAGE = 4003;
    private static final int REQUEST_CONNECT_DEVICE = 4004;
    private static final int REQUEST_ENABLE_BT = 4005;

    private ChildImageButton childImageButton;
    private NameEditText nameEditText;
    private AgeSpinner ageSpinner;
    private GenderRadioGroup genderRadioGroup;
    private SafeDistanceEditText safeDistanceEditText;
    private CommunicationCycleSpinner cycleSpinner;
    private SaveButton saveButton;
    private Button blueToothButton, cancelButton;
    private ImageView backgroundImage;

    private Child child;

    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childinfo);

        init();
    }
    private void init(){
        backgroundImage = (ImageView) findViewById(R.id.backgroundImageView_ChildInfo);
        childImageButton = (ChildImageButton) findViewById(R.id.childImageButton);
        nameEditText = (NameEditText) findViewById(R.id.nameEditText);
        ageSpinner = (AgeSpinner) findViewById(R.id.ageSpinner);
        genderRadioGroup = (GenderRadioGroup) findViewById(R.id.genderRadioGroup);
        safeDistanceEditText = (SafeDistanceEditText) findViewById(R.id.safeDistanceEditText);
        cycleSpinner = (CommunicationCycleSpinner) findViewById(R.id.communicationCycleSpinner);
        blueToothButton = (BlueToothButton) findViewById(R.id.connectingBluetoothButton);
        saveButton = (SaveButton) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeBackground();

        Intent intent = getIntent();
        int flag = intent.getIntExtra("flag",1);
        if(flag == 1) {
            child = new Child();
            Log.d("asdfasdf", "" + child.getLocation());
        }
        else if(flag == 2) {
            child = intent.getParcelableExtra("child");
            childImageButton.setImageBitmap(child.getPhoto());
            nameEditText.setText(child.getName());
            ageSpinner.setChildAge(child.getAge());
            genderRadioGroup.setGender(child.getGender());
            safeDistanceEditText.setText(String.valueOf(child.getDistance()));
            cycleSpinner.setCycle(child.getCycle());
        }
    }

    private void changeBackground()
    {
        String now = getCurrentTime();

        if (now.equals("아침") == true) {
            backgroundImage.setBackgroundResource(R.drawable.background6);
        } else if (now.equals("오후") == true) {
            backgroundImage.setBackgroundResource(R.drawable.background12);
        } else if (now.equals("오후") == true) {
            backgroundImage.setBackgroundResource(R.drawable.background18);
        } else {
            backgroundImage.setBackgroundResource(R.drawable.background24);
        }
    }

    public String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyy/MM/dd HH:mm:ss");
        String formatDate = sdfNow.format(date);
        String[] temp = formatDate.split(" ");
        temp = temp[1].split(":");
        int hour = Integer.valueOf(temp[0]);

        if (hour < 6) {
            return "밤";
        } else if (hour < 12) {
            return "아침";
        } else if (hour < 18) {
            return "오후";
        } else {
            return "저녁";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                mImageUri = data.getData();
                cropPickedImage(mImageUri);
                break;
            case PICK_FROM_CAMERA:
                mImageUri = childImageButton.getPhotoURI();
                cropPickedImage(mImageUri);
                break;
            case CROP_THE_IMAGE:
                Bundle extras = data.getExtras();

                if(extras != null){
                    Bitmap photo = extras.getParcelable("data");
                    childImageButton.setImageBitmap(photo);
                    child.setPhoto(photo);
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    // Set BluetoothData
                    child.setDeviceInfo(data.getExtras().getString((BluetoothDeviceList.EXTRA_DEVICE_ADDRESS)));
                    Log.d("ChildInfoActivity", child.getDeviceInfo());
                }
                break;
        }
    }

    private void cropPickedImage(Uri imageUri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");

        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_THE_IMAGE);
    }

    Child getChild(){
        return child;
    }
}