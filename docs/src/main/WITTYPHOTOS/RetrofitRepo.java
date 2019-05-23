package com.example.wittyphotos;

import java.util.ArrayList;

import retrofit2.http.Headers;

public class RetrofitRepo {
    private String imageFileName;
    private ArrayList<String> tagList;

    public String getImageFileName(){
        return imageFileName;
    }
    public ArrayList<String> getTagList(){
        return tagList;
    }
}
