package com.example.WITTYPHOTOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        autoInfo(context);

    }


    // 자동, 수동(), 페이스 벡터(벡터벨류값이 있어야지 클러스터링을 돌릴 수 있는 값을 전달할 수 있음)

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql;

        sql = "CREATE TABLE Image_info (id integer primary key autoincrement, " +
                "ImageNo integer, imageFileName text, imgFilePath text, created text, time text, gpsLat text, gpsLon integer)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Face_Tag (tagID text primary key," +
                "name string)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Face_Vector_Value (vectorId integer primary key," +
                "imgFileName text, imgLeft integer, imgTop integer, imgRight integer," +
                " imgBottom integer, vectorValue text, faceName text, label integer)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Auto_Tag (tagID text primary key, name_eng text, name_kor text, category text, tagState integer)";
        sqLiteDatabase.execSQL(sql);




        sql = "CREATE TABLE Manual_Tag (id integer primary key autoincrement, tagID text, tagName text)";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE Tag_Log ( tagID text, tag text, " +
                "imgFilePath text, ImageNo integer, created text)";
        sqLiteDatabase.execSQL(sql);
//검색 때 사용할 테이블은 tag_log 일 것 같습니다! 모든 태그 정보가 한번에 있는 테이블이라서용


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

    public void autoInfo(Context context) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> eng_name = new ArrayList<>();
        ArrayList<String> kor_name = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(R.raw.eng_data);
        InputStream inputStream2 = context.getResources().openRawResource(R.raw.kor_data);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        InputStreamReader inputreader2 = new InputStreamReader(inputStream2);
        BufferedReader buffreader2 = new BufferedReader(inputreader2);
        String line;

        try {

            while ((line = buffreader.readLine()) != null) {
                eng_name.add(line);
            }

            while ((line = buffreader2.readLine()) != null) {
                kor_name.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < eng_name.size(); i++) {
            ContentValues values = new ContentValues();
            String tag_id = "n";
            if(i == 0){
                //사람 태그이면 0
                values.put("tagID","n00000000");
                values.put("tagState",0);
            }
            else {
                int k = (int)(Math.log10(i)+1);
                for (int j = 0; j < 8 - k; j++) {
                    tag_id = tag_id + "0";
                }

                tag_id = tag_id + Integer.toString(i);
                values.put("tagID", tag_id);
                values.put("tagState",1);
            }

            values.put("name_eng", eng_name.get(i));
            values.put("name_kor", kor_name.get(i));
            db.insert("Auto_Tag", null, values);
        }
    }


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

                    values = new ContentValues();
                    values.put("imgFilePath", info.filePath);
                    values.put("ImageNo", info.id);
                    values.put("created", dateFormat.format(Calendar.getInstance().getTime()));
                    values.put("tag",info.tag_name);

                    //manual
                    ContentValues values2 = new ContentValues();
                    values2.put("tagName", info.tag_name);

                    //total
                    ContentValues values3 = new ContentValues();
                    values3.put("tag_name", info.tag_name);

                    String sql;
                    int tag_num;
                    int r1, r2;
                    Cursor cursor;
                    Cursor cursor2;
                    String tag_id;
                    int update_freq;
                    String sql2;

                    sql = "SELECT * FROM Total_Tag_Info WHERE tag_name = '"+info.tag_name+"'";
                    cursor = db.rawQuery(sql, null);
                    r1 = cursor.getCount();
                    sql2 ="SELECT * FROM Manual_Tag WHERE tagName = '"+info.tag_name+"'";
                    cursor2 = db.rawQuery(sql2, null);
                    r2 = cursor2.getCount();

                    if (r1 == 0 && r2 == 0){
                        sql = "SELECT id FROM Manual_Tag order by id DESC";
                        cursor = db.rawQuery(sql, null);
                        int r = cursor.getCount();
                        if(r == 0){
                            tag_id = "m00000001";
                        } else {
                            cursor.moveToFirst();
                            tag_num = cursor.getInt(0);
                            tag_id = "m";
                            tag_num = tag_num+1;
                            int k = (int)(Math.log10(tag_num)+1);
                            for (int j = 0; j < 8 - k; j++) {
                                tag_id = tag_id + "0";
                            }
                            tag_id = tag_id+Integer.toString(tag_num);
                        }

                        values.put("tagID",tag_id);
                        values2.put("tagID",tag_id);
                        values3.put("tag_id",tag_id);
                        values3.put("tag_freq",1);
                        addNetworkEdge(db, info.filePath, tag_id);
                        db.insert("Total_Tag_Info",null,values3);
                        db.insert("Manual_Tag",null,values2);
                        db.insert("Tag_Log", null, values);

                    }
                    else if( r1 == 0 && r2 == 1 ){
                        sql = "SELECT * FROM Manual_Tag WHERE tagName = '"+info.tag_name+"'";
                        cursor = db.rawQuery(sql, null);
                        cursor.moveToFirst();
                        tag_id = cursor.getString(1);
                        values.put("tagID",tag_id);
                        values3.put("tag_id",tag_id);
                        values3.put("tag_freq",1);
                        addNetworkEdge(db, info.filePath, tag_id);
                        db.insert("Total_Tag_Info",null,values3);
                        db.insert("Tag_Log", null, values);
                    }
                    else {
                        //이미 total에도 manual에도 있어.
                        cursor.moveToFirst();
                        tag_id = cursor.getString(1);
                        sql = "SELECT tag_freq FROM Total_Tag_Info WHERE tag_id = '"+tag_id+"'";
                        cursor = db.rawQuery(sql, null);
                        cursor.moveToFirst();
                        update_freq = cursor.getInt(0)+1;
                        db.execSQL("UPDATE Total_Tag_Info SET tag_freq = " + update_freq + " WHERE tag_id = '" + tag_id + "';");
                        values.put("tagID",tag_id);
                        addNetworkEdge(db, info.filePath, tag_id);
                        db.insert("Tag_Log", null, values);
                    }
                }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }

    }

    void addNetworkEdge(SQLiteDatabase db, String imagefilepath, String new_tag){
        //해당 태그 아이디랑 태그 네임이 들어올 것이고
        //사진의 태그를 전부 뽑아온다.
        //매칭 해서 넣어준다.
        ArrayList<String> tmp_tag_list = new ArrayList<String>();

        String sql = "SELECT tagID FROM Tag_Log WHERE imgFilePath = '"+imagefilepath+"';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String tag = cursor.getString(0);

            if (tag != null){
                tmp_tag_list.add(tag);
            }
        }

        for (int i =0 ;i<tmp_tag_list.size(); i++){
            String u = tmp_tag_list.get(i);
            matching(db, u, new_tag);
        }
    }
    void matching(SQLiteDatabase db, String u, String v){
        int update_weight;
        String sql;
        int r1, r2;
        String find_w;
        Cursor cursor;
        sql = "SELECT * FROM Tag_network WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "'";
        cursor = db.rawQuery(sql, null);
        r1 = cursor.getCount();

        sql = "SELECT * FROM Tag_network WHERE tag_1_id = '" + v + "' AND tag_2_id = '" + u + "'";
        cursor = db.rawQuery(sql, null);
        r2 = cursor.getCount();

        if (r1 == 1 && r2 == 0) {
            find_w = "SELECT weight FROM Tag_network  WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "'";
            cursor = db.rawQuery(find_w, null);
            cursor.moveToFirst();
            update_weight = cursor.getInt(0) + 1;
            db.execSQL("UPDATE Tag_network SET weight = " + update_weight + "  WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
        } else if (r2 == 1 && r1 == 0) {
            find_w = "SELECT weight FROM Tag_network WHERE tag_1_id = '" + v + "' AND tag_2_id = '" + u + "'";
            cursor = db.rawQuery(find_w, null);
            cursor.moveToFirst();
            update_weight = cursor.getInt(0) + 1;
            db.execSQL("UPDATE tag_network SET weight = " + update_weight + " WHERE tag_1_id = '" + v + "' AND tag_2_id = '" + u + "';");
        } else if (r1 == 0 && r2 == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag_1_id", u);
            contentValues.put("tag_2_id", v);
            contentValues.put("weight", 1);
            db.insert("Tag_network", null, contentValues);
        }
    }


    public void deleteImageInfo(int imgId, String tag, ImageTagInfo info) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String[] whereArgs = new String[2];
            whereArgs[0] = String.valueOf(imgId);
            whereArgs[1] = tag;
            db.delete("Tag_Log", "ImageNo=? and tag=?", whereArgs);
//            if (db.delete("Tag_Log", "ImageNo=? and tag=?", whereArgs) <= 0) {
//                Log.e(TAG, "Image_info, db.delete() error!");
//            }

            //Total_Tag_Info에서 삭제
            Cursor cursor;
            String sql = "SELECT tag_id, tag_freq FROM Total_Tag_Info WHERE tag_name = '"+tag+"'";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            String tag_id  = cursor.getString(0);
            int update_freq = cursor.getInt(1) - 1;
            if(update_freq == 0){
                db.execSQL("DELETE FROM Total_Tag_Info WHERE tag_id = '" + tag_id +"';");
            } else {
                db.execSQL("UPDATE Total_Tag_Info SET tag_freq = " + update_freq + "  WHERE tag_id = '" + tag_id +"';");
            }
            //Tag_network에서 삭제
            deleteNetworkEdge(db, String.valueOf(imgId), tag_id);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteNetworkEdge(SQLiteDatabase db, String imgno, String delete_tag) {

        ArrayList<String> tmp_tag_list = new ArrayList<String>();
        String sql = "SELECT tagID FROM Tag_Log WHERE ImageNo = '" + imgno + "';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String tag = cursor.getString(0);
            if (tag != null) {
                tmp_tag_list.add(tag);
            }

        }
        String v = delete_tag;
        for (int i = 0; i < tmp_tag_list.size(); i++) {
            String u = tmp_tag_list.get(i);
            int update_weight;
            int r1, r2;
            String find_w;
            sql = "SELECT * FROM Tag_network WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "'";
            cursor = db.rawQuery(sql, null);
            r1 = cursor.getCount();

            sql = "SELECT * FROM Tag_network WHERE tag_1_id = '" + v + "' AND tag_2_id = '" + u + "'";
            cursor = db.rawQuery(sql, null);
            r2 = cursor.getCount();

            if (r1 == 1 && r2 == 0) {
                find_w = "SELECT weight FROM Tag_network  WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "'";
                cursor = db.rawQuery(find_w, null);
                cursor.moveToFirst();
                update_weight = cursor.getInt(0) - 1;
                //update_weight가 0이라면 tag_network에서 삭제
                if (update_weight == 0) {
                    db.execSQL("DELETE FROM Tag_network WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                } else {
                    db.execSQL("UPDATE Tag_network SET weight = " + update_weight + "  WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                }

            } else if (r2 == 1 && r1 == 0) {
                find_w = "SELECT weight FROM Tag_network WHERE tag_1_id = '" + v + "' AND tag_2_id = '" + u + "'";
                cursor = db.rawQuery(find_w, null);
                cursor.moveToFirst();
                update_weight = cursor.getInt(0) - 1;
                if (update_weight == 0) {
                    db.execSQL("DELETE FROM Tag_network WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                } else {
                    db.execSQL("UPDATE Tag_network SET weight = " + update_weight + "  WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                }
            }
        }
    }


    public ArrayList<PickerImage> selectTagInfoByTags(String... tags) {
        if(tags == null || tags.length == 0) {
            return new ArrayList<>();
        }

        ArrayList<PickerImage> pickerImages = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] selection = new String[tags.length];

        String tagQuery = "";
        for(int i=0;i<tags.length;i++) {
            selection[i] = tags[i];
            if(i != 0) {
                tagQuery += " and ";
            }
            tagQuery +=  "tag=?";
        }

        try {
            Cursor c = db.query("Tag_Log", null, tagQuery, selection,null, null, null);

            if (c != null) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    PickerImage tagInfo = new PickerImage();
                    tagInfo.filePath = c.getString(c.getColumnIndex("imgFilePath"));
                    tagInfo.imgPath = Uri.fromFile(new File(c.getString(c.getColumnIndex("imgFilePath"))));
                    tagInfo.id = c.getInt(c.getColumnIndex("ImageNo"));
                    pickerImages.add(tagInfo);

                    c.moveToNext();
                }
                c.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pickerImages;

    }

    public ArrayList<PickerImage> selectMultiTag(String... tags) {
        if(tags == null || tags.length == 0) {
            return new ArrayList<>();
        }

        ArrayList<PickerImage> pickerImages = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] selection = new String[tags.length];

        try {
            Cursor c = db.query("Image_Info", null, null, null,null, null, null);

            if (c != null) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    boolean isAllMatched = true;
                    int imgId = c.getInt(c.getColumnIndex("ImageNo"));
                    ArrayList<String> tagLists = selectTagsById(imgId);
                    if(tagLists.size() > 0) {
                        for(String t : tags) {
                            if(!tagLists.contains(t)) {
                                isAllMatched = false;
                                continue;
                            }
                        }
                        if(isAllMatched) {
                            PickerImage tagInfo = new PickerImage();
                            tagInfo.filePath = c.getString(c.getColumnIndex("imgFilePath"));
                            tagInfo.imgPath = Uri.fromFile(new File(c.getString(c.getColumnIndex("imgFilePath"))));
                            tagInfo.id = c.getInt(c.getColumnIndex("ImageNo"));

                            boolean redup = true;
                            for(PickerImage p : pickerImages) {
                                if(tagInfo.id == p.id) {
                                    redup = false;
                                    continue;
                                }
                            }
                            if(redup) {
                                pickerImages.add(tagInfo);
                            }

                        }
                    }

                    c.moveToNext();
                }
                c.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pickerImages;

    }

    public ArrayList<String> selectTagsById(int id) {
        ArrayList<String> tags = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] selection = new String[1];
        selection[0] = String.valueOf(id);

        try {
            Cursor c = db.rawQuery("SELECT * FROM Tag_Log WHERE ImageNo= '" + id + "' ORDER BY created DESC", null);

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
