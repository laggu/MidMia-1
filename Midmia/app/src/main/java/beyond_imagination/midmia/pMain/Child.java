package beyond_imagination.midmia.pMain;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by laggu on 2017-07-10.
 */

public class Child implements Parcelable {

    private Bitmap photo;
    private String name;
    private int age;
    private int gender;
    private int distance;
    private int cycle;
    private String deviceInfo;
    private int isConnect;
    private ChildLocation location;

    public Child(){
        isConnect = 0;
        location = new ChildLocation();
    }

    public String checkFullFilled(){
        if(photo==null)
            return "사진";
        if(name==null||name.equals(""))
            return  "이름";
        if(age==0)
            return "나이";
        if(gender==0)
            return "성별";
        if(distance==0)
            return "안전거리";
        if(cycle==0)
            return "수신주기";
        // 블루투스 디바이스 정보와는 다름.
        //if(isConnect == 0) return "블루투스 연결되지않음";
        //if(name==null||deviceInfo.equals(""))
            //return "블루투스연결";
        // *** 블루투스 연결을 할 수 없으므로 임시로 주석 처리했음 ***
        return "성공";
    }

    protected Child(Parcel in) {
        photo = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        age = in.readInt();
        gender = in.readInt();
        distance = in.readInt();
        cycle = in.readInt();
        deviceInfo = in.readString();
        isConnect = in.readInt();
        location = new ChildLocation();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(photo, flags);
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeInt(gender);
        dest.writeInt(distance);
        dest.writeInt(cycle);
        dest.writeString(deviceInfo);
        dest.writeInt(isConnect);
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getDistance() { return distance; }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public ChildLocation getLocation() {
        return location;
    }

    public void setLocation(ChildLocation location) {
        this.location = location;
    }

    public int getIsConnect() {return isConnect; }

    public void setIsConnect(int isConnect) { this.isConnect = isConnect; }
}