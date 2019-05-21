package com.example.WITTYPHOTOS;

import java.io.BufferedReader;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.InputStreamReader;  
import java.util.ArrayList;  
import java.util.Random;  
  
public class Louvain implements Cloneable{  
    int n; 
    int m;
    int cluster[];
    Edge edge[];
    int head[];
    int top;
    double resolution;//1/2m ������
    double node_weight[];//�ٸ� ���� ����� ����
    double totalEdgeWeight;
    double[] cluster_weight;  
    double eps = 1e-14;
  
    int global_n;  
    int global_cluster[];  
    Edge[] new_edge;
    int[] new_head;  
    int new_top = 0;  
    final int iteration_time = 3; //�ִ� ����� ��
    
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
  
    void init(String filePath) {  
        try {  
            String encoding = "UTF-8";  
            File file = new File(filePath);  
            if (file.isFile() && file.exists()) { 
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);  
                String lineTxt = null;  
                lineTxt = bufferedReader.readLine();  
  

                String cur2[] = lineTxt.split(" ");
                //global_n, n : �� ��� ����
                global_n = n = Integer.parseInt(cur2[0]);
                //m : �� ���� ����x2
                m = Integer.parseInt(cur2[1])*2;
                //m = Integer.parseInt(cur2[1]);
                //m *= 2;
                edge = new Edge[m];
                head = new int[n]; 
                //�ʱ�ȭ
                for (int i = 0; i < n; i++)  
                    head[i] = -1;  
                top = 0;  
                
                global_edge=new Edge[m];
                global_head = new int[n];  
                //�ʱ�ȭ
                for(int i=0;i<n;i++)  
                    global_head[i]=-1;  
                global_top=0;  
                
                global_cluster = new int[n];
                //�ʱ⿡�� ��� ���� ��ŭ�� Ŭ�����Ͱ� ����.
                for (int i = 0; i < global_n; i++)
                    global_cluster[i] = i;
                node_weight = new double[n];  
                totalEdgeWeight = 0.0;
                
                //���� �о����
                while ((lineTxt = bufferedReader.readLine()) != null) {  
                    String cur[] = lineTxt.split(" ");  
                    int u = Integer.parseInt(cur[0]); 
                    int v = Integer.parseInt(cur[1]);
                    //System.out.println(u+" "+v);
                    double curw;
                    if (cur.length > 2) {  
                        curw = Double.parseDouble(cur[2]);
                        //weight �Է��� �ִ� ��� �� ������.
                    } else {  
                    	//weight �Է��� ���� ���� 1.0����.
                        curw = 1.0;  
                    }
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
                //totalEdgeWeight = 2m
                resolution = 1 / totalEdgeWeight;  
                //System.out.println(totalEdgeWeight);
                read.close();  
            } else {  
                System.out.println("�����Ͻ� ������ ã�� �� �����ϴ�.");  
            }  
        } catch (Exception e) {  
            System.out.println("���� ���� �б� ����");  
            e.printStackTrace();  
        }  
    }  
    
    void init_cluster() {  
        cluster = new int[n];  
        for (int i = 0; i < n; i++) {  
            cluster[i] = i;  
        }  
    }  
    //��� i�� ������ ���ġ�� �� ���
    boolean try_move_i(int i) { 
        double[] edgeW_cluster = new double[n];  
        for (int j = head[i]; j != -1; j = edge[j].next) { 
        	//l : ��� id�� ��ġ�� Ŀ�´�Ƽ�� ��ȣ
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
            //maxQ���� ū Ŀ�´�Ƽ�� ���
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
    /*
    void print() {  
        for (int i = 0; i < global_n; i++) {  
            System.out.println(i + ": " + global_cluster[i]);  
        }  
        System.out.println("-------");  
    }  
  	*/
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