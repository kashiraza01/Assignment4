package com.example.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Splash_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);

        // Load the animations from the 'res/anim' folder
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate_animation);
        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_animation);

        // Start both animations on the logo ImageView
        logo.startAnimation(translate);
        logo.startAnimation(scale);

        // After the animation ends, go to the LoginActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash_Activity.this, LoginActivity.class));
            finish(); // Close the SplashActivity
        }, 2000); // Delay for 2 seconds (to give time for the animation)
    }
}
