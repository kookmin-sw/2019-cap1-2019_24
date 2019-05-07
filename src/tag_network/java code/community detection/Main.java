package Louvain;

import java.io.BufferedWriter;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.util.ArrayList;  
  
public class Main {  

    static void printCommunityNum(Louvain a) throws IOException{
        for(int i=0;i<a.global_n;i++){  
            System.out.println(a.global_cluster[i]);     
        }  
    }
    public static void main(String[] args) throws IOException {  
        // TODO Auto-generated method stub  
        Louvain a = new Louvain();  
        a.init("/Users/MY/eclipse-workspace/Louvain/src/Louvain/input.txt");  
        a.louvain();  

        printCommunityNum(a);
    }  
  
}  