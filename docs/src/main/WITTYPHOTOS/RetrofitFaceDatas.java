/*
 *
 * Face info 의 response 형식을 저장(1)
 *
 * */
package com.example.wittyphotos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitFaceDatas {

    @SerializedName("faceInfos")
    private List<RetrofitFaceInfo> FaceInfos;

    public List<RetrofitFaceInfo> getFaceInfos(){
        return FaceInfos;
    }

}
