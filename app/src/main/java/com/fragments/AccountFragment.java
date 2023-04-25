package com.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.deluxerealty.R;
import com.example.deluxerealty.model.User;
import com.example.deluxerealty.screens.DetailsActivity;
import com.example.deluxerealty.screens.HomeActivity;
import com.example.deluxerealty.screens.LoginActivity;
import com.example.deluxerealty.screens.MyAdsActivity;
import com.example.deluxerealty.screens.PublishAdActivity;
import com.example.deluxerealty.screens.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {


    private CircleImageView userProfile;
    private EditText userName,userEmail,phoneET;
    private ImageView settingsImage;
    TextView typeTV;
    private AppCompatButton updateButton, adBtn;
    private DatabaseReference ref;
    private int Pick_Image = 1;
    private Uri uri;
    private String id;
    private DatabaseReference reference;
    private Boolean img_check = false;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfile = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        settingsImage = view.findViewById(R.id.settingsImg);
        userEmail = view.findViewById(R.id.user_email);
        phoneET = view.findViewById(R.id.user_phone);
        typeTV = view.findViewById(R.id.accountTypeTV);
        updateButton = view.findViewById(R.id.update_button);
        adBtn = view.findViewById(R.id.myads_button);

        ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try {
                    user = snapshot.getValue(User.class);
                    String f_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if(user.getId().equals(f_id) ){
                        userName.setText(user.getName());
                        userEmail.setText(user.getEmail());
                        //userProfile.setImageResource(user.getImage());
                        phoneET.setText(user.getPhone());
                        typeTV.setText(user.getAccountType());
                        id = snapshot.getKey();
                        Glide.with(getContext())
                                .load(user.getImage())
                                .centerCrop()
                                .placeholder(R.drawable.ic_account)
                                .into(userProfile);
                    }
                }catch (NullPointerException e){
                    System.err.println("Null Exception");
                }


        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    userProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Selected Picture"),Pick_Image);

        }
    });

    settingsImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        }
    });

    updateButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (img_check){
                uploadImage();
            }else{

                Map<String,String> map = new HashMap<>();
                map.put("name",userName.getText().toString().trim());
                map.put("email",userEmail.getText().toString());
                map.put("phone",phoneET.getText().toString());
                map.put("image", user.getImage());
                map.put("id", FirebaseAuth.getInstance().getUid());

                updateProfile(map);
            }

        }
    });

    adBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), MyAdsActivity.class);
            startActivity(intent);
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
                userProfile.setImageURI(uri);
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

                        // update data in firebase database
                        Map<String,String> map = new HashMap<>();
                        map.put("name",userName.getText().toString().trim());
                        map.put("email",userEmail.getText().toString());
                        map.put("phone",phoneET.getText().toString());
                        map.put("image", user.getImage());
                        map.put("id", FirebaseAuth.getInstance().getUid());

                        if(uri != null){
                            map.put("image",uri.toString());
                        }else {
                            map.put("image","image");
                        }
                        updateProfile(map);

                    }
                });
            }
        });

    }

    private void updateProfile( Map<String,String> map){

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(id);
        reference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // update user's authenticated email
                FirebaseAuth.getInstance().getCurrentUser()
                        .updateEmail(userEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isComplete()){

                                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                }else{

                                    Toast.makeText(getContext(), "fails", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }

}