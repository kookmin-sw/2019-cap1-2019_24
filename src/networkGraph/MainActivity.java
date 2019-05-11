package org.arielproject.networkg.test;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.arielproject.networkg.R;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.android_viewer.util.DefaultFragment;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Graph graph = new SingleGraph("Tutorial 1");

        setting(graph);

        display(savedInstanceState, graph, true);
    }


    private void setting(Graph graph){
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", styleSheet);



        graph.setStrict(false);
        graph.setAutoCreate( true );
        graph.addEdge("1", "0", "3");
        graph.addEdge("2", "0", "11");
        graph.addEdge("3", "0", "14");
        graph.addEdge("4", "2", "1");
        graph.addEdge("5", "2,", "6");
        graph.addEdge("6", "3", "1");
        graph.addEdge("7", "3", "6");
        graph.addEdge("8", "3", "14");
        graph.addEdge("9", "5", "9");
        graph.addEdge("10", "5", "13");
        graph.addEdge("11", "6", "13");

        Random r = new Random();

        for(int i = 0 ; i < graph.getNodeCount() ; i++){
            graph.getNode(i).setAttribute("ui.label", graph.getNode(i).getId());
        }
        int a = 1;

        //색상은 노드 추가할때 바로 설정해야할 듯.
        for(int i = 0 ; i < graph.getNodeCount() ; i++){
            float color;
            color = 0.5f;
            graph.getNode(i).setAttribute("ui.color", color);
            if (graph.getNode(i).getId() == "0" ){
                color = 0.25f;
                graph.getNode(i).setAttribute("ui.color", color);
            }



            //color = 0.25f;
            // graph.getNode(i).setAttribute("ui.color", color);
            }
        }


    protected String styleSheet =
            "graph { fill-mode: plain; fill-color: white; padding: 60px;}"+
                    "node { fill-mode: dyn-plain; stroke-mode: none; size: 60px;"+
                    "fill-color:  green, yellow, purple, black, pink;"+
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


}
