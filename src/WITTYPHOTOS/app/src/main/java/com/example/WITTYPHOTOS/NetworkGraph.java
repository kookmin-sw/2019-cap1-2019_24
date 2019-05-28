package com.example.WITTYPHOTOS;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.android_viewer.util.DefaultFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class NetworkGraph extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netgraph);
        db = App.mDB.getWritableDatabase();


         Louvain a = new Louvain();
         a.init("Total_Tag_Info","Tag_network", db);
         a.louvain();
         update("Total_Tag_Info", a);

        Graph graph = new SingleGraph("Tutorial 1");

//        graph.setAttribute("ui.antialias");
//        graph.setAttribute("ui.stylesheet", styleSheet);
//
//        graph.setStrict(false);
//        graph.setAutoCreate(true);
//
//        graph.addNode("1");
//        graph.getNode("1").setAttribute("ui.label", graph.getNode("1").getId());
//        float color = colorSelector(0);
//        graph.getNode("1").setAttribute("ui.color", color);
        setting(graph);

        display(savedInstanceState, graph, true);
        db.close();
    }


    private void setting(Graph graph) {
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", styleSheet);

        graph.setStrict(false);
        graph.setAutoCreate(true);


        Cursor cursor = db.rawQuery("SELECT * FROM Total_Tag_Info", null);
        while (cursor.moveToNext()) {
            float color;
            String name = cursor.getString(2);
            int g_num = cursor.getInt(3);
            if (name != null) {
                graph.addNode(name);
                graph.getNode(name).setAttribute("ui.label", graph.getNode(name).getId());
                color = colorSelector(g_num);
                graph.getNode(name).setAttribute("ui.color", color);
            }

        }
        Cursor cursor2 = db.rawQuery("SELECT * FROM Tag_network", null);
        int i = 0;
        while (cursor2.moveToNext()) {
            String tag1 = cursor2.getString(1);
            String tag2 = cursor2.getString(2);
            Cursor c3 = db.rawQuery("SELECT tag_name FROM Total_Tag_Info WHERE tag_id = '" + tag1 + "';", null);
            c3.moveToFirst();
            String name1 = c3.getString(0);
            Cursor c4 = db.rawQuery("SELECT tag_name FROM Total_Tag_Info WHERE tag_id = '" + tag2 + "';", null);
            c4.moveToFirst();
            String name2 = c4.getString(0);
            if (name1 != null && name2 != null) {
                String numt = Integer.toString(i);
                graph.addEdge(numt, name1, name2);
                i++;
            }

        }
    }

    public float colorSelector(int groupNum){
        float color = 0.0f;

        if (groupNum == 0) {
            color = 0.0f;
        } else if (groupNum == 1) {
            color = 0.1f;
        } else if (groupNum == 2) {
            color = 0.2f;
        } else if (groupNum == 3) {
            color = 0.3f;
        } else if (groupNum == 4) {
            color = 0.4f;
        } else if (groupNum == 5) {
            color = 0.5f;
        } else if (groupNum == 6) {
            color = 0.6f;
        } else if (groupNum == 7) {
            color = 0.7f;
        } else if (groupNum == 8) {
            color = 0.8f;
        } else if (groupNum == 9) {
            color = 0.9f;
        } else if (groupNum == 10) {
            color = 1.0f;
        }
        return color;
    }



    protected String styleSheet =
            "graph { fill-mode: plain; fill-color: #ebe7da; padding: 60px;}"+
                    "node { fill-mode: dyn-plain; stroke-mode: none; size: 60px;"+
                    "fill-color: PaleVioletRed, LightCoral, SandyBrown, DarkGoldenrod, CadetBlue, DarkCyan, SteelBlue, SlateBlue, DarkMagenta, Indigo, DimGray;"+
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

        Map<Integer,Integer> result_c = new HashMap<>();

        for(int i=0;i<a.global_n;i++){
            result_c.put(i, a.global_cluster[i]);
        }
        Iterator<String> it = a.keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            int value = a.tag_list.get(key);
            if (result_c.containsKey(value)) {
                db.execSQL("UPDATE " + tablename + " SET group_num = " + result_c.get(value) + " WHERE tag_id = '" + key + "';");

            }
        }
    }

}
