package com.example.deluxerealty.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.window.SplashScreen;

import com.example.deluxerealty.R;
import com.example.deluxerealty.screens.ui.ContactUsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    Button btn_back,booking_history_btn,btn_contact,share_btn;
    Button logout_btn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        btn_back = findViewById(R.id.btn_back);
        btn_contact = findViewById(R.id.btn_contact);
        logout_btn = findViewById(R.id.logout_btn);
        share_btn = findViewById(R.id.share_btn);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(SettingsActivity.this, SplashscreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ActivityCompat.finishAffinity(SettingsActivity.this);
            }
        });
        onBackClick();
        onContactUsClick();
//        onlogoutclicks();
    }


    private void onBackClick(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, "Deluxe Realty App");
                share.putExtra(Intent.EXTRA_TEXT, "Hey, check out this app Deluxe Realty");
                startActivity(Intent.createChooser(share, "Share link!"));

            }
        });
}

    private void onContactUsClick(){
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
                startActivity(intent);
            }
        });
    }
//    private void onlogoutclicks(){
//        logout_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                FirebaseAuth.getInstance().signOut();
//
//                Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                ActivityCompat.finishAffinity(SettingsActivity.this);
//
//            }
//        });
//    }
}