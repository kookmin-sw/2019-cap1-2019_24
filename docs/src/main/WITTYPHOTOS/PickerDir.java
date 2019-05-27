package com.example.WITTYPHOTOS;

public class PickerDir {

    public String dirName;
    public long dirId;
    public int imgResId;
    public String imgPath;
    public int count;

    public PickerDir(String dirName, int imgResId) {
        this.dirName = dirName;
        this.imgResId = imgResId;
    }

    public PickerDir(long bucketId, String bucketName, String imgPath, int count) {

        this.dirId = bucketId;
        this.dirName = bucketName;
        this.imgPath = imgPath;
        this.count = count;
    }

    public PickerDir() {

        count = 0;
    }
}
