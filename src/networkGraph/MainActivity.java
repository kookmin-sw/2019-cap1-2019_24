package org.arielproject.networkgraph;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.android_viewer.util.DefaultFragment;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "Test2", null, 1);

        //쓰기 가능한 SQLiteDatabase 인스턴스
        db = databaseHelper.getWritableDatabase();
        textView = (TextView)findViewById(R.id.textView);
        /**

         Louvain a = new Louvain();
         a.init("tag_info","tag_t", db);
         a.louvain();
         update("tag_info", a);
        **/
        Graph graph = new SingleGraph("Tutorial 1");
        setting(graph);
        display(savedInstanceState, graph, true);

        db.close();
        databaseHelper.close();

        //Graph graph = new SingleGraph("Tutorial 1");
        //setting(graph);
        //display(savedInstanceState, graph, true);
    }


    private void setting(Graph graph){
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", styleSheet);

        graph.setStrict(false);
        graph.setAutoCreate( true );


        Cursor cursor = db.rawQuery("SELECT * FROM tag_info",null);
        while (cursor.moveToNext()) {
            float color;
            String name = cursor.getString(2);
            int tmp = cursor.getInt(3);

            graph.addNode(name);
            graph.getNode(name).setAttribute("ui.label", graph.getNode(name).getId());
            if (tmp == 0) {
                color = 0.0f;
                graph.getNode(name).setAttribute("ui.color", color);
            } else if (tmp == 1) {
                color = 0.25f;
                graph.getNode(name).setAttribute("ui.color", color);
            } else if (tmp == 2) {
                color = 0.5f;
                graph.getNode(name).setAttribute("ui.color", color);
            } else if (tmp == 3) {
                color = 0.75f;
                graph.getNode(name).setAttribute("ui.color", color);
            }
        }
        Cursor cursor2 = db.rawQuery("SELECT * FROM tag_t",null);
        int i = 0;
        while (cursor2.moveToNext()) {
            int tag1 = cursor2.getInt(1);
            int tag2 = cursor2.getInt(2);
            Cursor c3 = db.rawQuery("SELECT tagn FROM tag_info WHERE tag = " + tag1 + ";",null);
            c3.moveToFirst();
            String name1 = c3.getString(0);
            Cursor c4 = db.rawQuery("SELECT tagn FROM tag_info WHERE tag = " + tag2 + ";",null);
            c4.moveToFirst();
            String name2 = c4.getString(0);
            String numt = Integer.toString(i);
            graph.addEdge(numt, name1, name2);
            i++;

        }

            //color = 0.25f;
            // graph.getNode(i).setAttribute("ui.color", color);
    }


    protected String styleSheet =
            "graph { fill-mode: plain; fill-color: white; padding: 60px;}"+
                    "node { fill-mode: dyn-plain; stroke-mode: none; size: 60px;"+
                    "fill-color: green, yellow, purple, black, pink;"+
                    "text-size: 30; text-alignment: above; text-color: black; text-background-mode: plain; text-background-color: white;}"+
                    "edge { fill-mode: dyn-plain; fill-color: gray; size: 3px;}";

    //요기는 출력 부분
    public void display(Bundle savedInstanceState, Graph graph, boolean autoLayout) {
        if (savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            DefaultFragment fragment = (DefaultFragment) fm.findFragmentByTag("fragment_tag");

            if (null == fragment) {
                fragment = new DefaultFragment();
                fragment.init(graph, autoLayout);
            }

            FragmentTransaction ft = fm.beginTransaction() ;
            ft.add(R.id.layoutFragment, fragment).commit();
        }
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
