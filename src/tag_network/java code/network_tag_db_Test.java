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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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

        //한 사진에서 태그가 들어올 경우에 돌리는 함수
        String tag_id ="";
        String tag_name;
        ArrayList<String> tmp_tag_id = new ArrayList<String>();
        ArrayList<String> tmp_tag_name = new ArrayList<String>();
        tag_name = find_tag_name(tag_id);
        tmp_tag_id.add(tag_id);
        tmp_tag_name.add(tag_name);

        createNetworkEdge(tmp_tag_id,tmp_tag_name);

        db.close();
        databaseHelper.close();

    }

    String find_tag_name(String tag_id){
        String sql;
        String tag_name ="";
        if(tag_id.startsWith("n")){
            //object_auto_tag 가서 이름 찾을 것 - 이름 찾아서
            sql = "SELECT KoreanTag FROM Object_Auto_tag WHERE tagID = "+tag_id;
            //ag_name = ~;
        } else if(tag_id.startsWith("m")){
            //object_manual_tag 가서 이름 찾을 것
            sql = "SELECT tag FROM Object_manual_tag WHERE tagID = "+tag_id;
        } else if(tag_id.startsWith("f")){
            //face_tag 가서 이름 찾을 것
        }
        return tag_name;

    }

    //사진이 하나가 돌고 나서 네트워크를 디비에 만들어 넣을 때 사용
    //tag_id가 하나씩 넘어오면 sql문으로 tag이름 찾아서 이름이랑 같이 hash map에 넣기
    //단 태그가 얼굴일 경우에는 facename 가져와야함 -> facename이 null일 경우 null로...
    //clustering 한다음에 face_name 넣어줘도 됨
    void createNetworkEdge(ArrayList<String> tmp_tag_id, ArrayList<String> tmp_tag_name){
        for(int i=0; i<tmp_tag_id.size(); i++){
            for(int j=0; j<tmp_tag_id.size(); j++) {
                //우선 순서대로 정렬이 안돼었을 경우를 위해서
                int update_weight;
                String find_weight;
                Cursor c1;
                Cursor c1_1;

                String sql = "SELECT COUNT(*) FROM tag_network WHERE tag_1_id = "+ tmp_tag_id.get(i) +" AND tag_2_id = "+tmp_tag_id.get(j);
                c1 = db.rawQuery(sql, null);
                c1.moveToFirst();
                int r1 = c1.getInt(0);

                String sql2 = "SELECT COUNT(*) FROM tag_network WHERE tag_1_id = "+ tmp_tag_id.get(j) +" AND tag_2_id = "+tmp_tag_id.get(i);
                c1 = db.rawQuery(sql2, null);
                c1.moveToFirst();
                int r2 = c1.getInt(0);

                if(r1==1){
                    find_weight = "SELECT weight FROM tag_network WHERE tag_1_id = "+ tmp_tag_id.get(i) +" AND tag_2_id = "+tmp_tag_id.get(j);
                    c1_1 = db.rawQuery(find_weight, null);
                    c1_1.moveToFirst();
                    update_weight = c1_1.getInt(0)+1;
                    db.execSQL("UPDATE tag_network SET weight = "+update_weight+" WHERE tag_1_id = "+ tmp_tag_id.get(i) +" AND tag_2_id = "+ tmp_tag_id.get(j));
                } else if (r2==1){
                    find_weight = "SELECT weight FROM tag_network WHERE tag_1_id = "+  tmp_tag_id.get(j) +" AND tag_2_id = "+tmp_tag_id.get(i);
                    c1_1 = db.rawQuery(find_weight, null);
                    c1_1.moveToFirst();
                    update_weight = c1_1.getInt(0)+1;
                    db.execSQL("UPDATE tag_network SET weight = "+update_weight+" WHERE tag_1_id = "+  tmp_tag_id.get(j) +" AND tag_2_id = "+tmp_tag_id.get(i));
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("tag_1_id", tmp_tag_id.get(i));
                    contentValues.put("tag_1_name", tmp_tag_name.get(i));
                    contentValues.put("tag_2_id", tmp_tag_id.get(j));
                    contentValues.put("tag_2_name", tmp_tag_name.get(j));
                    contentValues.put("weight", 1);
                    db.insert("tag_network", null, contentValues);
                }

            }
        }
    }

    void addNetworkEdge(){
        //해당 사진 파일 이름에 있는 태그들을 다 불러온다.(total_tag_list에서 태그 아이디와 매칭해서 태그 네임 같이 가져올 것)
        //새로 들어온 태그(아이디와 이름 정보가 있을테니까)와 매칭
        //역시나 각 순서쌍으로 createNetworkEdge() 이용해서 하면 됨.

    }

    void deleteNetworkEdge(){
        //나중에 delete tag 기능에서 total_tag_list에서도 같이 지우거나 count 내리도록 하는 것 추가 해야함

        //해당 사진 파일 이름에 있는 태그들을 다 불러온다.(태그 아이디만 가져오면 됨)
        //지워지는 태그를 제외한 모든 태그 노드들과의 연결을 -1씩 해준다(tag id만 비교)
        //만약 -1을 했을때 0이 된다면 아예 삭제
    }
}