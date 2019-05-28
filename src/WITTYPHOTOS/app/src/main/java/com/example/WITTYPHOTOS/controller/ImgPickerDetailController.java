package com.example.WITTYPHOTOS.controller;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.example.WITTYPHOTOS.App;
import com.example.WITTYPHOTOS.PickerImage;

import java.util.ArrayList;

public class ImgPickerDetailController extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    public Activity mActivity;
    private ContentResolver resolver;
    private OnQueryListener mOnQueryListner;
    private String path = "";



    public ImgPickerDetailController(Activity mActivity, OnQueryListener mOnQueryListner) {
        this.mActivity = mActivity;
        this.resolver = mActivity.getContentResolver();
        this.mOnQueryListner = mOnQueryListner;
    }

    public void loadImages(long bucketId) {
        new loadImage(bucketId).execute();
    }

    private class loadImage extends AsyncTask<Void, Void, ArrayList<PickerImage>> {

        private long bucketId;

        public loadImage(long bucketId) {
            this.bucketId = bucketId;
        }


        @Override
        protected ArrayList<PickerImage> doInBackground(Void... voids) {
            SQLiteDatabase db = App.mDB.getWritableDatabase();
            String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
            String bucketId = String.valueOf(this.bucketId);
            String sort = MediaStore.Images.Media._ID + " DESC";
            String[] selectionArgs = {bucketId};
            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor c;

            if (bucketId.equals("0")) {
                c = resolver.query(images, null, null, null, sort);
            } else {
                c = resolver.query(images, null, selection, selectionArgs, sort);
            }

            ArrayList<PickerImage> pickerImages = new ArrayList<>();

            if (c != null) {
                if (c.moveToNext()) {
                    setPathDir(c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)),
                            c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                    do {
                        int imageId = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
                        Uri path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "" + imageId);
                        PickerImage pickerImage = new PickerImage(path);
                        pickerImage.id = imageId;
                        pickerImage.filePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                        pickerImage.fileName = c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        pickerImages.add(pickerImage);
                        //ImageNo integer, imageFileName text, imgFilePath text,
                        String sql = "SELECT * FROM Image_info WHERE ImageNo = "+imageId;
                        Cursor cursor;
                        cursor = db.rawQuery(sql, null);
                        if(cursor.getCount() == 0){
                            ContentValues values = new ContentValues();
                            values.put("ImageNo", imageId);
                            values.put("imageFileName", c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                            values.put("imgFilePath", c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)));
                            db.insert("Image_info", null, values);
                        }
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }

            return pickerImages;

        }

        @Override
        protected void onPostExecute(ArrayList<PickerImage> pickerImages) {
            super.onPostExecute(pickerImages);
            mOnQueryListner.onComplete(pickerImages);
        }

    }


    private String setPathDir(String path, String fileName) {

        return this.path = path.replace("/" + fileName, "");
    }
}


