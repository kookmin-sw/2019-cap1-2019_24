package com.example.my.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class degreeCentrality {
    //tag id와 node weight 저장
    public Map<String, Integer> node_weight = new HashMap<>();
    //각 그룹에 해당되는 tag id와 tag name 저장
    public Map<String, String> group_n = new HashMap<>();
    //각 그룹에 해당되는 edge set 저장
    public Map<String, String> edge_set = new HashMap<>();

    public void init(SQLiteDatabase db, int groupNum) {


        Cursor cursor = db.rawQuery("SELECT * FROM tag_info WHERE count = " + groupNum, null);
        while (cursor.moveToNext()) {

            String tag_id = cursor.getString(1);
            String tag_name = cursor.getString(2);

            group_n.put(tag_id, tag_name);
        }

        Cursor cursor2 = db.rawQuery("SELECT * FROM tag_t", null);
        while (cursor2.moveToNext()) {
            //노드 n1, n2, weight(동시 출현 빈도)
            String n1 = cursor2.getString(1);
            String n2 = cursor2.getString(2);
            int w = cursor2.getInt(3);
            if (group_n.containsKey(n1) & group_n.containsKey(n2)) {
                String node1 = group_n.get(n1);
                String node2 = group_n.get(n2);

                if (!node_weight.containsKey(n1)) {
                    node_weight.put(n1, w);
                } else {
                    node_weight.put(n1, node_weight.get(n1) + w);
                }

                if (!node_weight.containsKey(n2)) {
                    node_weight.put(n2, w);
                } else {
                    node_weight.put(n2, node_weight.get(n2) + w);
                }
            }

        }
    }
}

