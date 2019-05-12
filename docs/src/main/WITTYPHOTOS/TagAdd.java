package com.example.WITTYPHOTOS;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;



public class TagAdd extends AppCompatActivity implements View.OnClickListener {


    private Button btnInput;
    private Uri imgPath;
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_add);
        btnInput = (Button)findViewById(R.id.btn_input);
        imageView = findViewById(R.id.imageView);
        btnInput.setOnClickListener(this);
        imgPath = getIntent().getParcelableExtra("path);
                                
        Glide.with(this).load(imgPath).apply(new RequestOptions().centerInside().diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView);
                                                 
                                               
    }

    @Override
    public void onClick(View v) {


         if(v.equals(btnInput)) {

            final EditText editText = new EditText(this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(TagAdd.this);
            dialog.setTitle("태그 추가");
            dialog.setView(editText);

            dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String inputValue = editText.getText().toString();
                    Toast.makeText(TagAdd.this, "inputValue", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }

}
