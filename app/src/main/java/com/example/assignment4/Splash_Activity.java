package com.example.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class Splash_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);

        Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate_animation);
        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_animation);

        // Start both animations on the logo ImageView
        logo.startAnimation(translate);
        logo.startAnimation(scale);

        FirebaseApp.initializeApp(this);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
            finish();
        }, 2000); // Delay for 2 seconds (to give time for the animation)
    }
}
