package com.example.clustering;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;



public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "Test2", null, 1);

        db = databaseHelper.getWritableDatabase();

         Louvain a = new Louvain();
         a.init("tag_info","tag_t", db);
         a.louvain();
         update("tag_info", a);

        db.close();
        databaseHelper.close();

    }

    void update(String tablename, Louvain a){

        Map<Integer,Integer> result_c = new HashMap<>();

        for(int i=0;i<a.global_n;i++){
            result_c.put(i, a.global_cluster[i]);
        }
        Iterator<String> it = a.keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            int value = a.tag_list.get(key);
            if (result_c.containsKey(value)) {
                db.execSQL("UPDATE " + tablename + " SET count = " + result_c.get(value) + " WHERE tag = '" + key + "';");

            }
        }
    }
}
