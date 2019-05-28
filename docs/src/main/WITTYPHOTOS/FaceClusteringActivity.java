/*
 *
 * DBSCAN을 이용하여 face clustering을 하는 클래스입니다.
 *
 * */

package com.example.wittyphotos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class FaceClusteringActivity extends AppCompatActivity {

    //총 vector의 수를 저장
    public int N = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //face vector value 테이블에서 모든 vector value를 가져온다.
        double[][] vectors = {{}};

        //eps, min값 저장
        final double eps = 0.3;
        final int minPts = 2;

        // DBSCAN
        int[] label = getDBSCAN(vectors, eps, minPts);
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
