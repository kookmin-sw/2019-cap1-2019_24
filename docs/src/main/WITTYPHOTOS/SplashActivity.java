package com.example.WITTYPHOTOS;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.WITTYPHOTOS.R;

public class SplashActivity extends AppCompatActivity {

    ImageView typo;
    ImageView logo_anim;
    Animation frombottom;
    Animation fromtop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        typo = (ImageView) findViewById(R.id.typo);
        logo_anim = (ImageView) findViewById(R.id.logo_anim);

        frombottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        typo.setAnimation(frombottom);

        fromtop = AnimationUtils.loadAnimation(this,R.anim.from_top);
        logo_anim.setAnimation(fromtop);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },1500);
    }
}
