package com.example.my.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.android_viewer.util.DefaultFragment;

import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "Test2", null, 1);

        db = databaseHelper.getWritableDatabase();
        degreeCentrality dc = new degreeCentrality();

        //나중에 버튼으로 부터 받아와야하는 값
        int groupNum = 1;

        dc.init(db, groupNum);
        Graph graph = new SingleGraph("Tutorial 1");
        setting(graph, groupNum, dc);
        display(savedInstanceState, graph, true);

        db.close();
        databaseHelper.close();
    }

    private void setting(Graph graph, int groupNum, degreeCentrality dc){
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", styleSheet);

        graph.setStrict(false);
        graph.setAutoCreate( true );
        float color;



        Set<String> keys = dc.group_n.keySet();
        Iterator<String> it = (keys).iterator();
        while (it.hasNext()) {
            String key = it.next();
            String tag_name = dc.group_n.get(key);
            graph.addNode(tag_name);
            graph.getNode(tag_name).setAttribute("ui.label", graph.getNode(tag_name).getId());
            color = colorSelector(groupNum);
            graph.getNode(tag_name).setAttribute("ui.color", color);
            int node_size = dc.node_weight.get(key)*5;
            graph.getNode(tag_name).setAttribute("ui.size",node_size);
        }

        int label_num = 0;
        String edge_label;
        Cursor cursor = db.rawQuery("SELECT * FROM tag_t", null);
        while (cursor.moveToNext()) {
            //노드 n1, n2, weight(동시 출현 빈도)
            String n1 = cursor.getString(1);
            String n2 = cursor.getString(2);

            if (dc.group_n.containsKey(n1) & dc.group_n.containsKey(n2)) {
                String node1 = dc.group_n.get(n1);
                String node2 = dc.group_n.get(n2);
                edge_label = Integer.toString(label_num);
                graph.addEdge(edge_label, node1, node2);
                label_num++;
            }
        }

/**
        int label_num = 0;
        String edge_label;
        Set<String> keys2 = dc.edge_set.keySet();
        Iterator<String> it2 = (keys2).iterator();
        while (it2.hasNext()) {
            String key2 = it2.next();
            String value2 = dc.edge_set.get(key2);
            edge_label = Integer.toString(label_num);
            textView.append(key2+" "+value2+"\n");
            graph.addEdge(edge_label, key2, value2);
            label_num++;

        }
 **/

        //color = 0.25f;
        // graph.getNode(i).setAttribute("ui.color", color);
    }
    public float colorSelector(int groupNum){
        float color = 0.0f;
        if (groupNum == 0) {
            color = 0.0f;
        } else if (groupNum == 1) {
            color = 0.25f;
        } else if (groupNum == 2) {
            color = 0.5f;
        } else if (groupNum == 3) {
            color = 0.75f;
        }
        return color;
    }


    protected String styleSheet =
            "graph { fill-mode: plain; fill-color: white; padding: 80px;}"+
                    "node { fill-mode: dyn-plain; stroke-mode: none; size-mode: dyn-size;"+
                    "fill-color: green, yellow, purple, black, pink;"+
                    "text-size: 30; text-alignment: above; text-color: black; text-background-mode: plain; text-background-color: white;}"+
                    "edge { fill-mode: dyn-plain; fill-color: gray; size: 3px;}";

    //그래프 출력 부분
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

}
