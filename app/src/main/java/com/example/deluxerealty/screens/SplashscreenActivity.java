package com.example.deluxerealty.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.deluxerealty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashscreenActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        gotologin();

    }

    private void gotologin(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(user != null){
                    // If user is already logged in then it will go to home screen, no need to login again
                    startActivity(new Intent(SplashscreenActivity.this , HomeActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashscreenActivity.this , LoginActivity.class));
                    finish();
                }

            }
        },3000) ;

    }
}
