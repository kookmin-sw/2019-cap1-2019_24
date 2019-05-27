package com.example.WITTYPHOTOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static android.support.constraint.Constraints.TAG;


public class DBHelper extends SQLiteOpenHelper {

    public Context mContext;
    private static final String DBNAME = "wittyphotos.db";
    private static final int DBVERSION = 2;

    public DBHelper(@Nullable Context context) {
        super(context, DBNAME, null, DBVERSION);
        mContext = context;
        getWritableDatabase();
    }


    // 자동, 수동(), 페이스 벡터(벡터벨류값이 있어야지 클러스터링을 돌릴 수 있는 값을 전달할 수 있음)

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql;

        sql = "CREATE TABLE Category (categoryID text primary key, category_name text)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Image_info (id integer primary key autoincrement, " +
                "ImageNo integer, imageFileName text, imgFilePath text, created text, time text, gpsLat text, gpsLon integer)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Face_Tag (tagID text primary key," +
                "name string, tagCount integer, categoryID string)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Face_Vector_Value (vectorId integer primary key," +
                "imgFileName text, imgLeft integer, imgTop integer, imgRight integer," +
                " imgBottom integer, vectorValue text, faceName text, label integer)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Tag_Log (autoTagId integer primary key autoincrement," +
                "imgFilePath text, ImageNo integer, tag text, created text, manualTagId text, faceTagId text)";
        sqLiteDatabase.execSQL(sql);
//검색 때 사용할 테이블은 tag_log 일 것 같습니다! 모든 태그 정보가 한번에 있는 테이블이라서용

        sql = "CREATE TABLE Tags (id integer primary key autoincrement, img_id integer, tag text, " +
                "tag_type boolean, created date, modified date)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Tag_network ( _id integer primary key autoincrement, tag_1_id text," +
                "tag_2_id text, weight integer )";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Total_Tag_Info ( _id integer primary key autoincrement, tag_id text, " +
                "tag_name text, group_num integer, tag_freq integer)";
        sqLiteDatabase.execSQL(sql);


//        if (oldVersion < {currentVersion}) {
//    sql = "CREATE TABLE Tags (id integer primary key autoincrement, img_id integer, tag text, " +
//            "tag_trype boolean, created date, modifies date)";
//    sqLiteDatabase.execSQL(sql);

        //tag_log도 같이 업데이트가 되어야 하는거죵?
//    sql = "CREATE TABLE Tag_Log (logNo interger primary key autoincrement, " +
//            "imgFileName string, autoTagId string, manualTagId string, faceTagId string)";
//    sqLiteDatabase.execSQL(sql);

    }
//
    public void insertImageInfo(ImageTagInfo info) {
        if (info == null) {
            return;
        }

        int showCount = 0;
        try {

            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            values.put("created", dateFormat.format(Calendar.getInstance().getTime()));

            showCount = 1;
            values.put("ImageNo", info.id);
            values.put("imageFileName", info.fileName);
            values.put("imgFilePath", info.filePath);

            if (db.insert("Image_info", null, values) > 0) {

                for (int i = 0; i < info.tags.size(); i++) {
                    values = new ContentValues();
                    values.put("imgFilePath", info.filePath);
                    values.put("ImageNo", info.id);
                    values.put("created", dateFormat.format(Calendar.getInstance().getTime()));
                    values.put("tag", info.tags.get(i));

                    if(db.insert("Tag_Log", null, values) <= 0 ) {
                        Log.e(TAG, "Tag_Log, db.insert() error!");
                    }
                }


            } else {
                Log.e(TAG, "Image_info, db.insert() error!");
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }

    }

    public void deleteImageInfo(int imgId, String tag) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String[] whereArgs = new String[2];
            whereArgs[0] = String.valueOf(imgId);
            whereArgs[1] = tag;

            if (db.delete("Tag_Log", "ImageNo=? and tag=?", whereArgs) <= 0) {
                Log.e(TAG, "Image_info, db.delete() error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//
////    @Override
////    public void onCreate(SQLiteDatabase sqLiteDatabase) {
////        String sql;
////        sql = "CREATE TABLE Image_info (imageFileName text primary key autoincrement, " +
////                "imgFilePath text, date text, time text, gpsLat text, gpsLon integer)";
////        sqLiteDatabase.execSQL(sql);
////
////    }
//
    public ArrayList<String> selectTagsById(int id) {
        ArrayList<String> tags = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] selection = new String[1];
        selection[0] = String.valueOf(id);

        try {
            Cursor c = db.rawQuery("SELECT * FROM Tag_Log WHERE ImageNo= '" + String.valueOf(id) + "' ORDER BY created DESC", null);

            if (c != null) {
                c.moveToFirst();
                while (!c.isAfterLast()) {

                    tags.add(c.getString(c.getColumnIndex("tag")));

                    c.moveToNext();
                }
                c.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int currentVersion) {

    }


}
