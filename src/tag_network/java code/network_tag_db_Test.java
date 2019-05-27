package com.example.dbtester;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "Test", null, 1);

        //쓰기 가능한 SQLiteDatabase 인스턴스
        db = databaseHelper.getWritableDatabase();

        //한 사진에서 태그가 들어올 경우에 돌리는 함수
        String tag_id ="";
        //String tag_name;
        ArrayList<String> tmp_tag_list = new ArrayList<String>();
        //ArrayList<String> tmp_tag_name = new ArrayList<String>();
        //tag_name = find_tag_name(tag_id);


//        tmp_tag_list.add("moo1");
//        tmp_tag_list.add("moo2");
//        tmp_tag_list.add("noo1");
//        tmp_tag_list.add("moo3");
//        tmp_tag_list.add("foo5");
        //tmp_tag_name.add(tag_name);
//        addNetworkEdge("mmmmmmmmggg//sss", "f005");

//        createNetworkEdge(tmp_tag_list);
        deleteNetworkEdge("mmmmmmmmggg//sss", "f005");

        db.close();
        databaseHelper.close();

    }

    String find_tag_name(String tag_id){
        String sql;
        String tag_name ="";
        if(tag_id.startsWith("n")){
            //object_auto_tag 가서 이름 찾을 것 - 이름 찾아서
            //where로 글자 비교 할 시에는  '  ' 사이에 들어가야함 잊지말것!!!
            sql = "SELECT KoreanTag FROM Object_Auto_tag WHERE tagID = "+tag_id;
            //ag_name = ~;
        } else if(tag_id.startsWith("m")){
            //object_manual_tag 가서 이름 찾을 것
            sql = "SELECT tag FROM Object_manual_tag WHERE tagID = "+tag_id;
        } else if(tag_id.startsWith("l")){
            //face_tag 가서 이름을 찾을 것
            //아마 가져와야하는 이름은 faceid가 아니라 label이여야할 것 같음
        }
        return tag_name;

    }

    //사진이 하나가 돌고 나서 네트워크를 디비에 만들어 넣을 때 사용
    //tag_id가 하나씩 넘어오면 sql문으로 tag이름 찾아서 이름이랑 같이 hash map에 넣기
    //단 태그가 얼굴일 경우에는 facename 가져와야함 -> facename이 null일 경우 null로...
    //clustering 한다음에 face_name 넣어줘도 됨
    void createNetworkEdge(ArrayList<String> tmp_tag_list) {
        for (int i = 0; i < tmp_tag_list.size(); i++) {
            for (int j = i+1; j < tmp_tag_list.size(); j++) {
                String u = tmp_tag_list.get(i);
                String v = tmp_tag_list.get(j);
                matching(u,v);
            }
        }
    }

    void addNetworkEdge(String imagefilepath, String new_tag){
        //해당 태그 아이디랑 태그 네임이 들어올 것이고
        //사진의 태그를 전부 뽑아온다.
        //매칭 해서 넣어준다.
        ArrayList<String> tmp_tag_list = new ArrayList<String>();
        String sql = "SELECT autoTagId, manualTagId, faceTagId FROM Tag_log WHERE imgFilePath = '"+imagefilepath+"';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String auto_Tag = cursor.getString(0);
            String manual_Tag = cursor.getString(1);
            String face_Tag = cursor.getString(2);

            if (auto_Tag != null){
                tmp_tag_list.add(auto_Tag);
            } else if (manual_Tag != null){
                tmp_tag_list.add(manual_Tag);
            } else if (face_Tag != null){
                tmp_tag_list.add(face_Tag);
            }
        }

        for (int i =0 ;i<tmp_tag_list.size(); i++){
            String u = tmp_tag_list.get(i);
            matching(u,new_tag);
        }
    }

    void deleteNetworkEdge(String imagefilepath, String delete_tag){
        //일단 우선 지워진 tag_log를 가져온다는 전제 하에.
        //delete 되어야하는 tag id받아온다.

        //해당 사진 파일 이름에 있는 태그들을 다 불러온다.(태그 아이디만 가져오면 됨)
        //지워지는 태그를 제외한 모든 태그 노드들과의 연결을 -1씩 해준다(tag id만 비교)
        //만약 -1을 했을때 0이 된다면 아예 삭제

        ArrayList<String> tmp_tag_list = new ArrayList<String>();
        String sql = "SELECT autoTagId, manualTagId, faceTagId FROM Tag_Log WHERE imgFilePath = '"+imagefilepath+"';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String auto_Tag = cursor.getString(0);
            String manual_Tag = cursor.getString(1);
            String face_Tag = cursor.getString(2);
            if (auto_Tag != null){
                tmp_tag_list.add(auto_Tag);
            } else if (manual_Tag != null){
                tmp_tag_list.add(manual_Tag);
            } else if (face_Tag != null){
                tmp_tag_list.add(face_Tag);
            }
        }
        String v = delete_tag;
        for (int i =0 ;i<tmp_tag_list.size(); i++){
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
                if (update_weight == 0){
                    db.execSQL("DELETE FROM Tag_network WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                } else {
                    db.execSQL("UPDATE Tag_network SET weight = " + update_weight + "  WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                }

            } else if (r2 == 1 && r1 == 0) {
                find_w = "SELECT weight FROM Tag_network WHERE tag_1_id = '" + v + "' AND tag_2_id = '" + u + "'";
                cursor = db.rawQuery(find_w, null);
                cursor.moveToFirst();
                update_weight = cursor.getInt(0) - 1;
                if (update_weight == 0){
                    db.execSQL("DELETE FROM Tag_network WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                } else {
                    db.execSQL("DELETE Tag_network SET weight = " + update_weight + "  WHERE tag_1_id = '" + u + "' AND tag_2_id = '" + v + "';");
                }
            }
        }
    }

    void matching(String u, String v){
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
            insertdata("Tag_network",u, v, 1);
        }
    }

    void insertdata(String tablename, String id1, String id2, int weight){
        ContentValues contentValues = new ContentValues();
        contentValues.put("tag_1_id", id1);
        contentValues.put("tag_2_id", id2);
        contentValues.put("weight", weight);
        db.insert(tablename, null, contentValues);
    }


//    void insertdata2(String tablename, String filepath, String autoid, String mid, String fid){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("autoTagId", autoid);
//        contentValues.put("manualTagId", mid);
//        contentValues.put("faceTagId", fid);
//        contentValues.put("imgFilePath", filepath);
//        db.insert(tablename, null, contentValues);
//    }

}
