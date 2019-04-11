package com.example.wittyphotos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class image_tag_list extends MainActivity {

    TextView get_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_tag_list);

        get_tag = findViewById(R.id.get_tag);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String tag = bundle.getString("tag");

        get_tag.setText(tag);
    }

    public void onClick(View view)
    {
        Intent intent = new Intent(this, add_tag.class);
        startActivity(intent);
    }

}