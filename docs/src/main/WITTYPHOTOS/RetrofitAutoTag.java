package com.example.WITTYPHOTOS;
/*
 *
 * Auto tag의 response 형식을 저장(2)
 *
 * */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitAutoTag {

    @SerializedName("imageFileName")
    @Expose
    private String imageFileName;

    @SerializedName("tagList")
    @Expose
    private ArrayList<String> tagList;

    public String getImageFileName(){
        return imageFileName;
    }

    public void setImageFileName(){
        this.imageFileName = imageFileName;
    }

    public ArrayList<String> getTagList(){
        return tagList;
    }

    public void setTagList(){
        this.tagList = tagList;
    }

}