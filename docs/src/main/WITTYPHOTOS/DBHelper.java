package com.example.WITTYPHOTOS;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public Context mContext;
    private static final String DBNAME = "wittyphotos.db";
    private static final int DBVERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DBNAME, null, DBVERSION);
        mContext = context;
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql;
        sql = "CREATE TABLE image_info (id integer primary key autoincrement, " +
                "imgid integer, imgpath text, tag_type boolean, modified date, created date) ";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
