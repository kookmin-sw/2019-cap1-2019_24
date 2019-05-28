package com.example.WITTYPHOTOS;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerActivity {

    public static void server(ArrayList<String> allImageList) {
        uImage(allImageList);
        getAutoTag();
        getFaceValue();
    }

    public static void uImage(ArrayList<String> allImageList){

        //<<추가>>이미지테이블에서 새로운 이미지를 검색하여 이미지리스트를 만든다 (Path+Name붙어있어야함)
        ArrayList<String> oldImageList = App.mDB.imgpath();

        //null값이 아니면 각각의 이미지 경로를 uploadImage함수에 전달하여 실행한다.
        for (String ai : allImageList){
            int cnt = 0;

            for(String oi : oldImageList){
                if(ai.compareTo(oi) ==0){
                    cnt ++;
                }
            }

            if(cnt ==0){
                uploadImage(ai);
            }

        }
    }

    //서버에 이미지를 업로드하는 함수
    protected static void uploadImage(String string) {

        File imageFile = new File(string);

        //retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoApi.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DjangoApi postApi= retrofit.create(DjangoApi.class);

        //multipart 사용하여 request 설정
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/data"), imageFile);
        MultipartBody.Part multiPartBody = MultipartBody.Part
                .createFormData("model_pic", imageFile.getName(), requestBody);

        //request 시작
        Call<RequestBody> call = postApi.uploadFile(multiPartBody);
        call.enqueue(new Callback<RequestBody>() {
            //response를 받았을 경우
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
            }
            //response가 실패했을 경우
            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
            }
        });

    }
    private static void getAutoTag(){

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

                    for (RetrofitAutoTag at : list){
                        //이미지 이름
                        String ImageFileName = at.getImageFileName();
                        //태그 리스트
                        ArrayList<String> taglist = at.getTagList();
                        //<<추가>> DB에 저장한다.
                        App.mDB.insertAutoTag(ImageFileName, taglist);
                    }

                }

            }

            @Override
            public void onFailure(Call<RetrofitTagDatas> call, Throwable t) {
            }
        });
    }

    private static void getFaceValue(){

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
                        App.mDB.insertFaceInfo(imageFileName, top,right,left,bottom,vector);
                    }
                }
            }
            @Override
            public void onFailure(Call<RetrofitFaceDatas> call, Throwable t) {
            }
        });
    }

}