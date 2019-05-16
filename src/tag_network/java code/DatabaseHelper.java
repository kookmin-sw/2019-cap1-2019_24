package org.arielproject.dbtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    static String TABLE_TAGG = "tag_t";
    static String TABLE_TAGINFO = "tag_info";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        Log.d(TAG, "DataBaseHelper 생성자 호출");
    }

    String CREATE_TABLE_TAGG = "create table "+TABLE_TAGG+
            "( _id integer primary key autoincrement, "+
            "tag1 integer, "+
            "tag2 integer, "+
            "count integer );";

    String CREATE_TABLE_TAGINFO = "create table "+TABLE_TAGINFO+
            "( _id integer primary key autoincrement, "+
            "tag integer, "+
            "tagn text, "+
            "count integer );";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        Log.d(TAG, "Table Create");

        sqLiteDatabase.execSQL(CREATE_TABLE_TAGG);
        sqLiteDatabase.execSQL(CREATE_TABLE_TAGINFO);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "Table onUpgrade");
        // 테이블 재정의하기 위해 현재의 테이블 삭제
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGINFO);
    }
}
