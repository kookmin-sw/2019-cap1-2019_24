package com.example.WITTYPHOTOS;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DjangoApi {
    String DJANGO_SITE = "http://jangjieun.pythonanywhere.com/";
    @Multipart
    @POST("uploadImage/")
    Call<RequestBody>  uploadFile(@Part MultipartBody.Part file);

    @POST("objectDetect/")
    Call<RetrofitTagDatas> getAutoTag();

    @POST("faceRecog/")
    Call<RetrofitFaceDatas> getFaceInfo();

}