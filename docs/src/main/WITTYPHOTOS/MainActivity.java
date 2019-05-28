package com.example.WITTYPHOTOS;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.WITTYPHOTOS.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String PERMISSION_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private ImageView album;
    private ImageView graph;
    private ImageButton test;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        album = findViewById(R.id.album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,ImagePickerDirectoryActivity.class));
            }
        });

        graph = findViewById(R.id.graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,PieGraph.class));
            }
        });

        checkPermissionAndRequestPermission(1);

    }

    public boolean checkPermissionAndRequestPermission(final int requestCode) {
        if (!isFinishing()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{PERMISSION_STORAGE}, requestCode);
            }
        }
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}


