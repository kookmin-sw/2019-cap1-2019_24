package com.example.WITTYPHOTOS;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PickerImage implements Parcelable {
    public Uri imgPath;
    public String fileName;
    public String filePath;
    public int id;
    public boolean isChecked;

    public PickerImage(Uri imgPath) {
        this.imgPath = imgPath;
    }

    public PickerImage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.imgPath, flags);
        dest.writeString(this.fileName);
        dest.writeString(this.filePath);
        dest.writeInt(this.id);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    protected PickerImage(Parcel in) {
        this.imgPath = in.readParcelable(Uri.class.getClassLoader());
        this.fileName = in.readString();
        this.filePath = in.readString();
        this.id = in.readInt();
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PickerImage> CREATOR = new Parcelable.Creator<PickerImage>() {
        @Override
        public PickerImage createFromParcel(Parcel source) {
            return new PickerImage(source);
        }

        @Override
        public PickerImage[] newArray(int size) {
            return new PickerImage[size];
        }
    };
}