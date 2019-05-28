package com.example.WITTYPHOTOS;
/*
 *
 * 서버에 요청하여 자동 태그 리스트를 받아오는 클래스입니다.
 *
 * */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetAutoTagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //서버에 요청하여 자동 태그를 받아오는 함수를 실행한다.
        getAutoTag();

        //얼굴 인식을 하는 클래스로 전환된다.
        startActivity(new Intent(GetAutoTagActivity.this, GetFaceValueActivity.class));

    }


    private void getAutoTag(){

        //OkHttp설정 (응답 시간 등 설정)
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        //Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoApi.DJANGO_SITE)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DjangoApi postApi = retrofit.create(DjangoApi.class);
        //request 실행
        Call<RetrofitTagDatas> call = postApi.getAutoTag();
        call.enqueue(new Callback<RetrofitTagDatas>() {
            @Override
            public void onResponse(Call<RetrofitTagDatas> call, Response<RetrofitTagDatas> response) {
                if (response.isSuccessful()) {
                    //전달 받은 Json 형식 데이터의 Form 을 변환한다.
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(gson.toJson(response.body()));
                    JsonObject jo = element.getAsJsonObject();
                    RetrofitTagDatas tagdatas = gson.fromJson(jo, RetrofitTagDatas.class);
                    List<RetrofitAutoTag> list = tagdatas.getTagDatas();

                    ArrayList<String> tmp_list = new ArrayList<>();
                    for (RetrofitAutoTag at : list){
                        //이미지 이름
                        String ImageFileName = at.getImageFileName();
                        //태그 리스트
                        ArrayList<String> taglist = at.getTagList();
                        //<<추가>> DB에 저장한다.
                    }

                }

            }

            @Override
            public void onFailure(Call<RetrofitTagDatas> call, Throwable t) {
            }
        });
    }

}