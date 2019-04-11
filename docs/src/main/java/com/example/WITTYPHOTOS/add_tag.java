package com.example.wittyphotos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class add_tag extends MainActivity {

    public EditText tag_name;
    public Button add_tag_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag);

        tag_name = findViewById(R.id.tag_name);
        add_tag_confirm = findViewById(R.id.add_tag_confirm);

        add_tag_confirm.setClickable(true);
        add_tag_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = tag_name.getText().toString();

                Intent intent = new Intent(add_tag.this, image_tag_list.class);
                intent.putExtra("tag", tag);
                startActivity(intent);
            }
        });
    }


}