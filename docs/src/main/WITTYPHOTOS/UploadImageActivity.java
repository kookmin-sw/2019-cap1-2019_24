package com.example.WITTYPHOTOS;
/*
 *
 * 앨범의 모든 사진 정보를 불러와 데이터베이스에 저장된 사진들과 비교한 후
 * 기존에 없던 새로운 이미지들은 서버에 전송하는 클래스입니다.
 *
 */
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //기기에 저장된 모든 이미지의 경로(Path+Name)를 불러온다.
        ArrayList<String> allImageList = getPathOfAllImages();

        //<<추가>>이미지테이블에서 새로운 이미지를 검색하여 이미지리스트를 만든다 (Path+Name붙어있어야함)
        ArrayList<String> oldImageList = App.mDB.imgPath();
        ArrayList<String> newImageList = new ArrayList<>();

        
        for (String ai : allImageList){
            int cnt = 0;
            for(String oi : oldImageList){
                if(ai.compareTo(oi) ==0){
                    cnt ++;
                }
            }
            if(cnt ==0){
                newImageList.add(ai);
            }
        }
        
        //null값이 아니면 각각의 이미지 경로를 uploadImage함수에 전달하여 실행한다.
        if(allImageList != null){
            for (String list : allImageList){
                uploadImage(list);
            }

            //서버에서 자동 태그를 받아오는 Get Auto Tag Activity로 전환된다.
            startActivity(new Intent(UploadImageActivity.this,GetAutoTagActivity.class));
        }

    }

    //서버에 이미지를 업로드하는 함수
    private void uploadImage(String string) {

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

    //기기내의 모든 이미지의 path+name을 받아오는 함수
    private ArrayList<String> getPathOfAllImages() {

        ArrayList<String> result = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        int lastIndex;

        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(columnIndex);
            String nameOfFile = cursor.getString(columnDisplayname);
            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
            lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;
            if (!TextUtils.isEmpty(absolutePathOfImage)) {
                result.add(absolutePathOfImage);
            }
        }

        return result;
    }

}
