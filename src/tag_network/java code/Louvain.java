package com.example.clustering;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Louvain implements Cloneable {
    //tag id와 계산하여 반환하기 위해 임시로 주어지는 tag_num
    public Map<String,Integer> tag_list = new HashMap<>();
    Set<String> keys;
    int n;
    int m;
    int cluster[];
    Edge edge[];
    int head[];
    int top;
    double resolution;//1/2m 고정값
    double node_weight[];//다른 노드와 연결된 개수
    double totalEdgeWeight;
    double[] cluster_weight;
    double eps = 1e-14;

    int global_n;
    int global_cluster[];
    Edge[] new_edge;
    int[] new_head;
    int new_top = 0;
    final int iteration_time = 3; //최대 디버깅 수

    Edge global_edge[];
    int global_head[];
    int global_top=0;

    void addEdge(int u, int v, double weight) {
        if(edge[top]==null)
            edge[top]=new Edge();
        edge[top].v = v;
        edge[top].weight = weight;
        edge[top].next = head[u];
        head[u] = top++;
    }

    void addNewEdge(int u, int v, double weight) {
        if(new_edge[new_top]==null)
            new_edge[new_top]=new Edge();
        new_edge[new_top].v = v;
        new_edge[new_top].weight = weight;
        new_edge[new_top].next = new_head[u];
        new_head[u] = new_top++;
    }

    void addGlobalEdge(int u, int v, double weight) {
        if(global_edge[global_top]==null)
            global_edge[global_top]=new Edge();
        global_edge[global_top].v = v;
        global_edge[global_top].weight = weight;
        global_edge[global_top].next = global_head[u];
        global_head[u] = global_top++;
    }

    void init(String tablename1, String tablename2, SQLiteDatabase db){
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM "+tablename1, null);
        c1.moveToFirst();
        //global_n, n : 총 노드 갯수
        global_n = n = c1.getInt(0);;

        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM "+tablename2, null);
        c2.moveToFirst();
        //m : 총 엣지 개수x2
        m = c2.getInt(0)*2;

        edge = new Edge[m];
        head = new int[n];
        //초기화
        for (int i = 0; i < n; i++)
            head[i] = -1;
        top = 0;

        global_edge=new Edge[m];
        global_head = new int[n];
        //초기화
        for(int i=0;i<n;i++)
            global_head[i]=-1;
        global_top=0;

        global_cluster = new int[n];
        //초기에는 노드 개수 만큼의 클러스터가 있음.
        for (int i = 0; i < global_n; i++)
            global_cluster[i] = i;
        node_weight = new double[n];
        totalEdgeWeight = 0.0;

        int t_num = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM "+tablename2,null);
        while (cursor.moveToNext()) {
            int u=0;
            int v=0;

            String n1 = cursor.getString(1);
            if(tag_list.containsKey(n1)) {
                u = tag_list.get(n1);
            } else {
                tag_list.put(n1, t_num);
                u = t_num;
                t_num++;
            }
            String n2 = cursor.getString(2);
            if(tag_list.containsKey(n2)) {
                v = tag_list.get(n2);
            } else {
                tag_list.put(n2, t_num);
                v = t_num;
                t_num++;
            }

            double curw = cursor.getDouble(3);
            addEdge(u, v, curw);
            addEdge(v, u, curw);
            addGlobalEdge(u,v,curw);
            addGlobalEdge(v,u,curw);

            totalEdgeWeight += 2 * curw;
            node_weight[u] += curw;

            if (u != v) {
                node_weight[v] += curw;
            }
        }
        resolution = 1 / totalEdgeWeight;
        keys = tag_list.keySet();
    }

    void init_cluster() {
        cluster = new int[n];
        for (int i = 0; i < n; i++) {
            cluster[i] = i;
        }
    }
    //노드 i를 빼내서 재배치한 후 계산
    boolean try_move_i(int i) {
        double[] edgeW_cluster = new double[n];
        for (int j = head[i]; j != -1; j = edge[j].next) {
            //l : 노드 id가 위치한 커뮤니티의 번호
            int l = cluster[edge[j].v];
            edgeW_cluster[l] += edge[j].weight;
        }
        int bestCluster = -1;
        double maxQ = 0.0;
        boolean[] vis = new boolean[n];
        cluster_weight[cluster[i]] -= node_weight[i];
        for (int j = head[i]; j != -1; j = edge[j].next) {
            int s = cluster[edge[j].v];
            if (vis[s])
                continue;
            vis[s] = true;
            double cur_deltaQ = edgeW_cluster[s];
            cur_deltaQ -= node_weight[i] * cluster_weight[s] * resolution;
            //maxQ값이 큰 커뮤니티에 배속
            if (cur_deltaQ > maxQ) {
                bestCluster = s;
                maxQ = cur_deltaQ;
            }
            edgeW_cluster[s] = 0;
        }
        if (maxQ < eps) {
            bestCluster = cluster[i];
        }
        //System.out.println(maxQ);
        cluster_weight[bestCluster] += node_weight[i];
        if (bestCluster != cluster[i]) {
            cluster[i] = bestCluster;
            return true;
        }
        return false;
    }

    void rebuildGraph() {
        int[] change = new int[n];
        int change_size=0;
        boolean vis[] = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (vis[cluster[i]])
                continue;
            vis[cluster[i]] = true;
            change[change_size++]=cluster[i];
        }
        int[] index = new int[n];
        for (int i = 0; i < change_size; i++)
            index[change[i]] = i;
        int new_n = change_size;
        new_edge = new Edge[m];
        new_head = new int[new_n];
        new_top = 0;
        double new_node_weight[] = new double[new_n];
        for(int i=0;i<new_n;i++)
            new_head[i]=-1;

        ArrayList<Integer>[] nodeInCluster = new ArrayList[new_n];
        for (int i = 0; i < new_n; i++)
            nodeInCluster[i] = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            nodeInCluster[index[cluster[i]]].add(i);
        }
        for (int u = 0; u < new_n; u++) {
            boolean visindex[] = new boolean[new_n];
            double delta_w[] = new double[new_n];
            for (int i = 0; i < nodeInCluster[u].size(); i++) {
                int t = nodeInCluster[u].get(i);
                for (int k = head[t]; k != -1; k = edge[k].next) {
                    int j = edge[k].v;
                    int v = index[cluster[j]];
                    if (u != v) {
                        if (!visindex[v]) {
                            addNewEdge(u, v, 0);
                            visindex[v] = true;
                        }
                        delta_w[v] += edge[k].weight;
                    }
                }
                new_node_weight[u] += node_weight[t];
            }
            for (int k = new_head[u]; k != -1; k = new_edge[k].next) {
                int v = new_edge[k].v;
                new_edge[k].weight = delta_w[v];
            }
        }

        int[] new_global_cluster = new int[global_n];
        for (int i = 0; i < global_n; i++) {
            new_global_cluster[i] = index[cluster[global_cluster[i]]];
        }
        for (int i = 0; i < global_n; i++) {
            global_cluster[i] = new_global_cluster[i];
        }
        top = new_top;
        for (int i = 0; i < m; i++) {
            edge[i] = new_edge[i];
        }
        for (int i = 0; i < new_n; i++) {
            node_weight[i] = new_node_weight[i];
            head[i] = new_head[i];
        }
        n = new_n;
        init_cluster();
    }

    void louvain() {
        init_cluster();
        int count = 0;
        boolean update_flag;
        do {
            count++;
            cluster_weight = new double[n];
            for (int j = 0; j < n; j++) {
                cluster_weight[cluster[j]] += node_weight[j];
            }
            int[] order = new int[n];
            for (int i = 0; i < n; i++)
                order[i] = i;
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                int j = random.nextInt(n);
                int temp = order[i];
                order[i] = order[j];
                order[j] = temp;
            }
            int enum_time = 0;
            int point = 0;
            update_flag = false;
            do {
                int i = order[point];
                point = (point + 1) % n;
                if (try_move_i(i)) {
                    enum_time = 0;
                    update_flag = true;
                } else {
                    enum_time++;
                }
            } while (enum_time < n);
            if (count > iteration_time || !update_flag)
                break;
            rebuildGraph();
        } while (true);
    }
}
