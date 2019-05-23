package com.example.wittyphotos;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface RetrofitService {
    @Multipart
    @POST("objectDetect/")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);
//
//    @GET("objectDetect/")
//    Call<RetrofitRepo> getAutoTags(@Url String url);
//
//    @GET("faceRecog/")
//    Call<RetrofitRepo> getFaceInfo(@Url String url);
}

