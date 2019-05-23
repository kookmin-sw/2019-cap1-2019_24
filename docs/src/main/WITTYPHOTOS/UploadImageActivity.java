/*
* 앨범의 모든 사진 정보 (path+name)을 불러와 데이터베이스에 저장된 사진들과 비교한 후
* 기존에 없던 새로운 이미지들은 서버에 전송하는 클래스입니다
* */

package com.example.wittyphotos;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadimage);
        ArrayList<String> allImageList = getPathOfAllImages();
        //db와 비교후 new image 빼오기
        if(allImageList != null){
            for (String string : allImageList){
                uploadImage(string);
            }
        }
    }

    private void getTagList(String imageFileName){

    }
    //서버로 이미지를 전송하는 함수
    private void uploadImage(String imageFileName) {
        final String FImageFileName = imageFileName;
        File imagefile = new File(imageFileName);
        final TextView text = (TextView) findViewById(R.id.textView);

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image"), imagefile);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", imagefile.getName(), fileReqBody);
        Call call = retrofitService.uploadImage(part);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ResponseBody repo = (ResponseBody) response.body();
                    text.setText("yes");
                } else {
                    text.setText(response.message());
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                text.setText(t.toString());
            }
        });
    }

    //갤러리의 모든 이미지의 path+name을 받아오는 함수
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