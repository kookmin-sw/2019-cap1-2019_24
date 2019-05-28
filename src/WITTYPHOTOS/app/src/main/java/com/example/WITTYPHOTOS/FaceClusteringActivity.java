package com.example.WITTYPHOTOS;
/*
 *
 * DBSCAN을 이용하여 face clustering을 하는 클래스입니다.
 *
 * */
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class FaceClusteringActivity extends AppCompatActivity {

    //총 vector의 수를 저장
    SQLiteDatabase db;
    public int N;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = App.mDB.getWritableDatabase();
        ArrayList<Integer> vectorId = new ArrayList<>();
        double[][] vectors = {{0}};
        //face vector value 테이블에서 모든 vector value를 가져온다.
        String sql = "SELECT * FROM Face_Vector_Value";
        Cursor cursor;
        cursor = db.rawQuery(sql,null);
        N = cursor.getCount();
        String[] tmp;
        while(cursor.moveToNext()){
            vectorId.add(cursor.getInt(0));
            String vector = cursor.getString(6);
            for(int i = 0; i<N;i++){
                tmp = vector.split(" ");
                for(int j= 0; j<128; j++){
                    vectors[i][j] = Double.parseDouble(tmp[j]);

                }
            }
        }

        //eps, min값 저장
        final double eps = 0.3;
        final int minPts = 2;

        // DBSCAN
        int[] label = getDBSCAN(vectors, eps, minPts);
        for(int i=0; i<N;i++){
            sql = "UPDATE Face_Vector_Value SET label = '"+label[i]+"' WHERE vectorId = "+i;
            db.execSQL(sql);
        }

        printLabel(label);
    }


    public double getDist (double[] A, double[] B) {
        double dist = 0d;
        for (int i = 0; i < A.length; i++) {
            dist += (A[i]-B[i])*(A[i]-B[i]);
        }
        return Math.sqrt(dist);
    }

    //DBSCAN함수
    public int[] getDBSCAN(double[][] vectors, double eps, int minPts) {
        int[] label = new int[N];
        // 0: undefined
        // -1: noise (border)
        // 1 ... : cluster number
        int C = 0; // cluster initializing
        for (int i = 0; i < vectors.length; i++) {
            if (label[i] != 0)
                continue;
            ArrayList<Integer> neighbors = rangeQuery(vectors, i, eps);
            if (neighbors.size() < minPts) {
                label[i] = -1;
                continue;
            }
            C++;
            label[i] = C;
            ArrayList<Integer> seed = new ArrayList<Integer>();
            seed.addAll(neighbors);
            for (int j = 0; j < seed.size(); j++) {
                int Q = seed.get(j);
                if (label[Q] == -1)
                    label[Q] = C;
                if (label[Q] != 0)
                    continue;
                label[Q] = C;
                neighbors = rangeQuery(vectors, Q, eps);
                if (neighbors.size() >= minPts)
                    seed.addAll(neighbors);
            }
        }
        return label;
    }

    public ArrayList<Integer> rangeQuery(double[][] vectors, int Q, double eps) {
        ArrayList<Integer> neighbors = new ArrayList<Integer>();
        for (int i = 0; i < vectors.length; i++) {
            double[] P = vectors[i];
            if (getDist(P, vectors[Q]) <= eps && i!=Q)
                neighbors.add(i);
        }
        return neighbors;
    }

    //label값을 출력한다.
    public void printLabel(int[] label) {
        for (int i = 0; i < label.length; i++){
            //label[i];
            //DB에 저장
        }
    }

//    정확도를 확인하는 함수.
//    일단 사용하지 않으므로 주석처리함
//    public double getAccuracy(int[] A, int[] B) {
//        if (A.length != B.length) {
//            System.out.println("A, B are not same length!");
//            return -1d;
//        }
//
//        int count = 0;
//
//        for (int i = 0; i < A.length; i++) {
//            if (A[i] == B[i])
//                count++;
//        }
//
//        return (double)count / (double)A.length;
//    }

}