package com.example.WITTYPHOTOS;

import android.content.DialogInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


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

        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;
        Bitmap mBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.id.imageView,
                options);

        //produce rectangle for face detecting
        Paint mRectPaint = new Paint();
        mRectPaint.setStrokeWidth(5);
        mRectPaint.setColor(Color.BLACK);
        mRectPaint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.RGB_565);

        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(mBitmap, 0,0, null);
        
        //face detecting 
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if(!faceDetector.isOperational()) {
            new AlertDialog.Builder(imageView.getContext()).setMessage("Could not set up the face" +
                    "detector!").show();
            return ;
        }

        Frame frame = new Frame.Builder().setBitmap(mBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        //get value 
        for(int i = 0; i < faces.size(); i++) {
            Face thisface = faces.valueAt(i);

            float x1 = thisface.getPosition().x;
            float y1 = thisface.getPosition().y;
            float x2 = x1 + thisface.getWidth();
            float y2 = y1 + thisface.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, mRectPaint) ;
        }

        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));




        btnInput.setOnClickListener(this);
        imgPath = getIntent().getParcelableExtra("path");

        Glide.with(this)
                .load(imgPath)
                .apply(new RequestOptions()
                        .centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView);






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
            // on this part, we need to delete specific tag data, not cancel actions.

            dialog.show();
        }
    }

}
