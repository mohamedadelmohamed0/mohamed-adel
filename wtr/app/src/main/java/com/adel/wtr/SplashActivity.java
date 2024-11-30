package com.adel.wtr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        start();
    }

    public void start()
    {
        findViewById(R.id.getStartedButton).setOnClickListener(view -> {
            startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            finish();
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
//                finish();
//            }
//        }, 3000); // 3000
    }
}