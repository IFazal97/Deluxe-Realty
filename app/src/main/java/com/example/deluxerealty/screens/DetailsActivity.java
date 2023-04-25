package com.example.deluxerealty.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.deluxerealty.R;
import com.example.deluxerealty.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView price,shortDescription,description;
    String pri,des,shdes,img, loc, p_type, user_id,id;
    Button contact_btn, del_btn;
    Boolean fromMyAd;
    private DatabaseReference reference;
    DatabaseReference database;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        reference = FirebaseDatabase.getInstance().getReference();
        imageView = findViewById(R.id.imageView);
        price = findViewById(R.id.price);
        shortDescription = findViewById(R.id.short_description);
        description = findViewById(R.id.description);
        contact_btn = findViewById(R.id.contact_seller_button);
        del_btn = findViewById(R.id.del_button);

        pri = getIntent().getStringExtra("price");
        des = getIntent().getStringExtra("description");
        loc = getIntent().getStringExtra("location");
        p_type = getIntent().getStringExtra("p_type");
        user_id = getIntent().getStringExtra("user_id");
        id = getIntent().getStringExtra("id");
        img = getIntent().getStringExtra("image");
        shdes = getIntent().getStringExtra("shortDescription");
        fromMyAd = getIntent().getBooleanExtra("fromMyAd",false);

        price.setText(pri);
        description.setText(des);
        shortDescription.setText(shdes);
        Glide.with(this)
                .load(img)
                .centerCrop()
                .placeholder(R.drawable.ic_account)
                .into(imageView);

       if (fromMyAd){
           contact_btn.setText("Edit");

           contact_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(DetailsActivity.this, PublishAdActivity.class);
                   intent.putExtra("editMode",true);
                   intent.putExtra("price",pri);
                   intent.putExtra("location",loc);
                   intent.putExtra("description",des);
                   intent.putExtra("shortDescription",shdes);
                   intent.putExtra("propertyType",p_type);
                   intent.putExtra("image",img);
                   intent.putExtra("id",id);
                   intent.putExtra("user_id",user_id);

                   startActivity(intent);
               }
           });

           del_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   reference.child("posts").child(id).removeValue();

                   Toast.makeText(DetailsActivity.this, "Ad Deleted Successfully", Toast.LENGTH_SHORT).show();

                   Intent intent = new Intent(DetailsActivity.this, SplashscreenActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
                   ActivityCompat.finishAffinity(DetailsActivity.this);
               }
           });
       }else {
           del_btn.setVisibility(View.GONE);
           getSellerDetail();

           contact_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String url = "https://api.whatsapp.com/send?phone="+user.getPhone();
                   Intent i = new Intent(Intent.ACTION_VIEW);
                   i.setData(Uri.parse(url));
                   startActivity(i);
               }
           });
       }

    }

    private void getSellerDetail(){
        database = FirebaseDatabase.getInstance().getReference("users");
        database.child(user_id).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            user = snapshot.getValue(User.class);

                        }else {

                            Toast.makeText(DetailsActivity.this, "An Error Occurred",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}