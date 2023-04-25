package com.example.deluxerealty.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.example.deluxerealty.R;
import com.example.deluxerealty.adapters.HomeAdapter;
import com.example.deluxerealty.listeners.ItemListener;
import com.example.deluxerealty.model.Item;
import com.example.deluxerealty.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyAdsActivity extends AppCompatActivity implements ItemListener {

    RecyclerView recyclerView;
    private List<Item> itemList;
    private DatabaseReference ref;
    private HomeAdapter adapter;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        recyclerView = findViewById(R.id.myAdRV);

        itemList = new ArrayList<>();
        getList();
    }

    public void setAdapter(){

        adapter = new HomeAdapter(this,itemList,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void getList(){
        FirebaseDatabase.getInstance().getReference().child("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String f_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if ( Objects.requireNonNull(dataSnapshot.child("user_id").getValue()).toString().equals(f_id)){
                                itemList.add(new Item(
                                        Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString(),
                                        Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString(),
                                        Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString(),
                                        Objects.requireNonNull(dataSnapshot.child("shortDescription").getValue()).toString(),
                                        Objects.requireNonNull(dataSnapshot.child("propertyType").getValue()).toString(),
                                        Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString(),
                                        Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString(),
                                        Objects.requireNonNull(dataSnapshot.child("user_id").getValue()).toString()
                                ));
                            }

                        }
                        setAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getUser(){
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User tempUser;
                tempUser = snapshot.getValue(User.class);
                String f_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(tempUser.getId().equals(f_id) ){
                    user = tempUser;
                    getList();
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
    }


    @Override
    public void OnItemPosition(int position) {
        Intent intent = new Intent(MyAdsActivity.this, DetailsActivity.class);
        intent.putExtra("price",itemList.get(position).getPrice());
        intent.putExtra("location",itemList.get(position).getLocation());
        intent.putExtra("description",itemList.get(position).getDescription());
        intent.putExtra("p_type",itemList.get(position).getPropertyType());
        intent.putExtra("shortDescription",itemList.get(position).getShortDescription());
        intent.putExtra("image",itemList.get(position).getImage());
        intent.putExtra("user_id",itemList.get(position).getUser_id());
        intent.putExtra("id",itemList.get(position).getId());
        intent.putExtra("fromMyAd",true);
        startActivity(intent);
    }
}