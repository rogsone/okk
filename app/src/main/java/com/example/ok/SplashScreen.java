package com.example.ok;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

import com.example.ok.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class SplashScreen extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("APP ID") // Required for Analytics.
                .setProjectId("PROJECT ID") // Required for Firebase Installations.
                .setApiKey("GOOGLE API KEY") // Required for Auth.
                .build();
        FirebaseApp.initializeApp(this, options, "com.example.ok");
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        },2000);
    }

}
