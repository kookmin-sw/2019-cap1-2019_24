package com.example.WITTYPHOTOS;
/*
 *
 * Face info의 response 형식을 저장(2)
 *
 * */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetrofitFaceInfo {
    @SerializedName("imageFileName")
    @Expose
    private String imageFileName;

    @SerializedName("top")
    @Expose
    private int top;

    @SerializedName("right")
    @Expose
    private int right;

    @SerializedName("bottom")
    @Expose
    private int bottom;

    @SerializedName("left")
    @Expose
    private int left;

    @SerializedName("vector")
    @Expose
    private String vector;

    public String getImageFileName(){
        return imageFileName;
    }

    public void setImageFileName(){
        this.imageFileName = imageFileName;
    }

    public int getTop(){
        return top;
    }

    public void setTop(){
        this.top = top;
    }

    public int getRight(){
        return right;
    }

    public void setRight(){
        this.right = right;
    }

    public int getBottom(){
        return bottom;
    }

    public void setBottom(){
        this.bottom = bottom;
    }

    public int getLeft(){
        return left;
    }

    public void setLeft(){
        this.left = left;
    }

    public String getVector(){
        return vector;
    }

    public void setVector(){
        this.vector = vector;
    }

}