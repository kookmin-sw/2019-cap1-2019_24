package com.example.WITTYPHOTOS;
/*
 *
 * Auto tag의 response 형식을 저장(1)
 *
 * */
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitTagDatas {

    @SerializedName("tagDatas")
    private List<RetrofitAutoTag> TagDatas;

    public List<RetrofitAutoTag> getTagDatas(){
        return TagDatas;
    }

}