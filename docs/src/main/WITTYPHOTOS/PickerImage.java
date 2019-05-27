package com.example.WITTYPHOTOS;

import android.net.Uri;

public class PickerImage {

    public Uri imgPath;
    public boolean isChecked;

    public PickerImage(Uri imgPath) {
        this.imgPath = imgPath;
    }
}
