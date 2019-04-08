package com.example.WITTYPHOTOS;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.example.WITTYPHOTOS.R;

public class MainActivity extends AppCompatActivity {

    private ImageView album;
    private ImageView graph;


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
                startActivity(new Intent(MainActivity.this,GraphDir.class));
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}


