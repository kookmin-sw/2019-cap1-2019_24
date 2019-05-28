/*
*
* 서버에 요청하여 얼굴 정보 (얼굴 위치, vector value) 를 받아오는 클래스입니다.
*
* */

package com.example.wittyphotos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetFaceValueActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //서버에 요청하여 얼굴 정보를 받아오는 함수 실행
        getFaceValue();
        //clustering 클래스로 전환된다.
        startActivity(new Intent(GetFaceValueActivity.this,FaceClusteringActivity.class));
    }

    private void getFaceValue(){

        //okHttp 설정
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build();

        //Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoApi.DJANGO_SITE)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DjangoApi postApi = retrofit.create(DjangoApi.class);

        //request 실행
        Call<RetrofitFaceDatas> call = postApi.getFaceInfo();
        call.enqueue(new Callback<RetrofitFaceDatas>() {
            @Override
            public void onResponse(Call<RetrofitFaceDatas> call, Response<RetrofitFaceDatas> response) {
                if (response.isSuccessful()) {
                    //전달받은 Json형식의 값을 변환
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(gson.toJson(response.body()));
                    JsonObject jo = element.getAsJsonObject();
                    RetrofitFaceDatas facedatas = gson.fromJson(jo, RetrofitFaceDatas.class);
                    List<RetrofitFaceInfo> flist = facedatas.getFaceInfos();
                    for (RetrofitFaceInfo fi : flist){
                        //이미지 파일 이름
                        String imageFileName = fi.getImageFileName();
                        //얼굴 위치
                        int top = fi.getTop();
                        int right = fi.getRight();
                        int left = fi.getLeft();
                        int bottom = fi.getBottom();
                        //벡터값
                        String vector = fi.getVector();
                        //<<추가>> DB에 저장
                    }
                }
            }
            @Override
            public void onFailure(Call<RetrofitFaceDatas> call, Throwable t) {
            }
        });
    }

}