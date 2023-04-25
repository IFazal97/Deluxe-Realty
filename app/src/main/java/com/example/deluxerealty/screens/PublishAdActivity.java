package com.example.deluxerealty.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.deluxerealty.R;
import com.example.deluxerealty.model.Item;
import com.example.deluxerealty.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PublishAdActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private EditText locationET, propertyTypeET, totalBedET, rentET, descET;
    private Button submitBtn, uploadImgBtn;
    ImageView adImage;
    String user_id, id;
    String pri,des,shdes,img, loc, propertyType;
    private int Pick_Image = 1;
    private Uri uri;
    Boolean img_check = false;
    Boolean editMode;

//https://cdn.pixabay.com/photo/2016/11/18/17/20/living-room-1835923__480.jpg
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_ad);
        reference = FirebaseDatabase.getInstance().getReference();

        locationET = findViewById(R.id.property_location);
        propertyTypeET = findViewById(R.id.property_type);
        totalBedET = findViewById(R.id.property_bedroom);
        rentET = findViewById(R.id.property_rent);
        descET = findViewById(R.id.property_description);
        submitBtn = findViewById(R.id.btn_submit_ad);
        uploadImgBtn = findViewById(R.id.addImageBtn);
        adImage = findViewById(R.id.imgView);

        user_id = getIntent().getStringExtra("user_id");
        editMode = getIntent().getBooleanExtra("editMode", false);
        pri = getIntent().getStringExtra("price");
        des = getIntent().getStringExtra("description");
        loc = getIntent().getStringExtra("location");
        propertyType = getIntent().getStringExtra("propertyType");
        img = getIntent().getStringExtra("image");
        shdes = getIntent().getStringExtra("shortDescription");
        id = getIntent().getStringExtra("id");

        if (editMode){
            locationET.setText(loc);
            propertyTypeET.setText(propertyType);
            totalBedET.setText(shdes);
            rentET.setText(pri);
            descET.setText(des);
            Glide.with(this)
                    .load(img)
                    .centerCrop()
                    .placeholder(R.drawable.ic_account)
                    .into(adImage);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editMode){
                    if (locationET.getText().toString() == "" || propertyTypeET.getText().toString() == "" ||
                            totalBedET.getText().toString() == "" || rentET.getText().toString() == "" ||
                            descET.getText().toString() == ""){

                        Toast.makeText(PublishAdActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

                    }else {
                        if (img_check){
                            uploadImage();
                        }else {
                            setData(img);
                        }
                    }
                }else {
                    if (locationET.getText().toString() == "" || propertyTypeET.getText().toString() == "" ||
                            totalBedET.getText().toString() == "" || rentET.getText().toString() == "" ||
                            descET.getText().toString() == "" || !img_check){

                        Toast.makeText(PublishAdActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

                    }else {

                        uploadImage();
                    }
                }


            }
        });

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Selected Picture"),Pick_Image);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Pick_Image && resultCode == RESULT_OK) {
            assert data != null;
            if (data.getData() != null) {

                uri = data.getData();
                adImage.setImageURI(uri);
                img_check = true;
            }
        }
    }


    private void uploadImage() {

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference().child("images/"+ UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("URL", "onSuccess: "+uri);

                        if(uri != null){

                            setData(uri.toString());
                        }else {
                            setData("image");
                        }


                    }
                });
            }
        });

    }

    void setData(String img){
        Item item = new Item();

        item.setLocation(locationET.getText().toString());
        item.setDescription(descET.getText().toString());
        item.setPrice(rentET.getText().toString());
        item.setPropertyType(propertyTypeET.getText().toString());
        item.setUser_id(user_id);
        item.setImage(img);
        String short_desc = totalBedET.getText().toString();
        item.setShortDescription(short_desc);

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        if (editMode){
            item.setId(id);
            editAd(item, id);
        }else {
            item.setId(ts);
            publishAds(item, ts);
            Log.d("TAG", "setData: ");
        }

    }

    private void publishAds(Item item, String ts){

        FirebaseDatabase.getInstance().getReference("posts")
                .child(ts)
                .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(PublishAdActivity.this, "Ad published", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {

                            Toast.makeText(PublishAdActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        reference.child("posts").push().setValue(item);

    }
    private void editAd(Item item, String ts){
        reference.child("posts").child(ts).setValue(item);
        Toast.makeText(PublishAdActivity.this, "Ad published", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(PublishAdActivity.this, SplashscreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ActivityCompat.finishAffinity(PublishAdActivity.this);
    }
}