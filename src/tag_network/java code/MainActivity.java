package org.arielproject.dbtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "Test", null, 1);

        //쓰기 가능한 SQLiteDatabase 인스턴스
        db = databaseHelper.getWritableDatabase();
        /**
        Louvain a = new Louvain();
        a.init("tag_info","tag_t", db);
        a.louvain();
        update("tag_info", a);
        **/
        db.close();
        databaseHelper.close();

    }

    void update(String tablename, Louvain a){
        for(int i=0;i<a.global_n;i++){
            db.execSQL("UPDATE "+tablename+" SET count = " + a.global_cluster[i] + " WHERE tag = " + i + ";");
        }
    }


    void dbInsert(String tableName, Integer t1, Integer t2, Integer c) {
        Log.d(TAG, "Insert Data ");

        ContentValues contentValues = new ContentValues();
        contentValues.put("tag1", t1);
        contentValues.put("tag2", t2);
        contentValues.put("count", c);

        // 리턴값: 생성된 데이터의 id
        long id = db.insert(tableName, null, contentValues);

        Log.d(TAG, "id: " + id);
    }

    void dbInsert(String tableName, Integer t1, String t2, Integer c) {
        Log.d(TAG, "Insert Data ");

        ContentValues contentValues = new ContentValues();
        contentValues.put("tag", t1);
        contentValues.put("tagn", t2);
        contentValues.put("count", c);

        // 리턴값: 생성된 데이터의 id
        long id = db.insert(tableName, null, contentValues);

        Log.d(TAG, "id: " + id);
    }

}