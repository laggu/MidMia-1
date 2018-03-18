package beyond_imagination.midmia.pMain;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by laggu on 2017-07-06.
 */

public class Database {
    private static final String DBNAME = "midmia";
    private static final String CHILDTABLENAME = "child";

    private static Activity activity;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase db;

    public static void setDatabase(Activity activity){
        Database.activity = activity;

        databaseHelper = new DatabaseHelper();
        db = databaseHelper.getWritableDatabase();
    }

    public static void insertRecord(Child child){
        try{
            SQLiteStatement p = db.compileStatement("insert into " + CHILDTABLENAME + " values (?,?,?,?,?,?)");
            p.bindString(1, child.getName());
            p.bindLong(2, child.getAge());
            p.bindLong(3, child.getGender());
            p.bindLong(4, child.getDistance());
            p.bindLong(5, child.getCycle());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            child.getPhoto().compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] photo = stream.toByteArray();

            p.bindBlob(6, photo);

            p.execute();
        }
        catch (Exception e){

        }
    }

    public static void updateRecord(Child child){
        ContentValues recordValues = new ContentValues();
        recordValues.put("age", child.getAge());
        recordValues.put("gender", child.getGender());
        recordValues.put("distance", child.getDistance());
        recordValues.put("cycle", child.getCycle());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        child.getPhoto().compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] photo = stream.toByteArray();

        recordValues.put("photo", photo);
        String[] whereArgs = {child.getName()};
        db.update(CHILDTABLENAME, recordValues, "name = ?", whereArgs);
    }

    public static void deleteRecord(Child child){
        String[] whereArgs = {child.getName()};

        db.delete(CHILDTABLENAME, "name = ?", whereArgs);
    }

    public static ArrayList<Child> readFromDatabase(){
        ArrayList<Child> list = new ArrayList<Child>(5);
        try {
            Cursor c = db.rawQuery("select * from " + CHILDTABLENAME, null);
            for(int i = 0; i < c.getCount(); ++i){
                c.moveToNext();
                Child child = new Child();
                child.setName(c.getString(0));
                child.setAge(c.getInt(1));
                child.setGender(c.getInt(2));
                child.setDistance(c.getInt(3));
                child.setCycle(c.getInt(4));

                byte[] b = c.getBlob(5);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0 , b.length);

                child.setPhoto(bitmap);
                list.add(child);
            }
        }
        catch (Exception e) {
            Toast.makeText(activity, "아이 정보를 불러올수 없습니다.", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        private DatabaseHelper(){
            super(activity, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + CHILDTABLENAME +
                    "(name text PRIMARY KEY," +
                    "age integer," +
                    "gender integer," +
                    "distance integer," +
                    "cycle integer," +
                    "photo blob)";
            try{
                db.execSQL(sql);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
