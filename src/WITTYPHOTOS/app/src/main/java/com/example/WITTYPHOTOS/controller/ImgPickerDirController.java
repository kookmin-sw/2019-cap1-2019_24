package com.example.WITTYPHOTOS.controller;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.WITTYPHOTOS.PickerDir;

import java.util.ArrayList;
import java.util.HashMap;

public class ImgPickerDirController {

    private final String TAG = getClass().getSimpleName();

    public Activity mActivity;
    private ContentResolver resolver;
    private OnQueryListener mOnQueryListner;

    public ImgPickerDirController(Activity activity, OnQueryListener listener) {
        mActivity = activity;
        resolver = mActivity.getContentResolver();
        mOnQueryListner = listener;
    }

    public void loadDirList() {
        new LoadImageDirList().execute();
    }

    //background processing
    private class LoadImageDirList extends AsyncTask<Void, Void, ArrayList<PickerDir>> {

        @Override
        protected ArrayList<PickerDir> doInBackground(Void... voids) {

            Log.i(TAG, "doInBackground");
            HashMap<Long, PickerDir> dirHashMap = new HashMap<>();
            String orderBy = MediaStore.Images.Media._ID + " DESC";
            String[] projection = new String[] {

                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID
            };

            Cursor c = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, null, null, orderBy);

            int totalCount = 0;
            if (c != null) {

                int bucketData = c.getColumnIndex(MediaStore.Images.Media.DATA);
                int bucketColumn = c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int bucketColumnId = c.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

                dirHashMap.put((long)0,
                        new PickerDir(
                                0,
                                "전체보기",
                                null,
                                0 ));

                while (c.moveToNext()) {

                    totalCount++;
                    long bucketId = c.getInt(bucketColumnId);
                    PickerDir pickerDir = dirHashMap.get(bucketId);

                    if (pickerDir == null) {
                        int imgId = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));

                        Uri imgPath = Uri.withAppendedPath(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "" + imgId);

                        dirHashMap.put(
                                bucketId,
                                new PickerDir(bucketId,
                                        c.getString(bucketColumn),
                                        c.getString(bucketData),
                                        1));

                        if (dirHashMap.get((long)0).imgPath == null) {
                            dirHashMap.get((long)0).imgPath = imgPath.toString();
                        }

                    }
                    else {
                        pickerDir.count++;
                    }
                }

                PickerDir pickerDir = dirHashMap.get((long)0);
                if (pickerDir != null) {
                    pickerDir.count = totalCount;
                }

                c.close();
            }

            if (totalCount == 0) {
                dirHashMap.clear();
            }

            ArrayList<PickerDir> pickerDirs = new ArrayList<>();
            for (PickerDir d: dirHashMap.values()) {
                if (d.dirId == 0 ) {
                    pickerDirs.add(0, d);
                }
                else {
                    pickerDirs.add(d);
                }
            }
            return pickerDirs;
        }

        @Override
        protected void onPreExecute() {

            Log.i(TAG, "onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<PickerDir> pickerDirs) {

            Log.i(TAG, "onPostExecute");
            super.onPostExecute(pickerDirs);
            mOnQueryListner.onComplete(pickerDirs);
        }
    }
}
