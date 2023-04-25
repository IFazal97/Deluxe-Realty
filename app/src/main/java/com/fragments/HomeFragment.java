package com.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.example.deluxerealty.R;
import com.example.deluxerealty.adapters.HomeAdapter;
import com.example.deluxerealty.listeners.ItemListener;
import com.example.deluxerealty.model.Item;
import com.example.deluxerealty.model.User;
import com.example.deluxerealty.screens.DetailsActivity;
import com.example.deluxerealty.screens.PublishAdActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements ItemListener, Filterable {

    private RecyclerView topDealRV;
    private HomeAdapter adapter;
    private List<Item> itemList;
    private List<Item> searchList;
    private CircleImageView profileImage;
    private TextView username;
    private Button publishBtn;
    private DatabaseReference ref;
    SearchView searchView;
    String user_id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topDealRV = view.findViewById(R.id.top_deals_RV);
        profileImage = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.user_name);
        publishBtn = view.findViewById(R.id.btn_publish_your_ad);
        searchView = view.findViewById(R.id.searchView);


        itemList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
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
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        adapter = new HomeAdapter(getContext(),itemList,this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        topDealRV.setLayoutManager(linearLayoutManager);
//        topDealRV.setAdapter(adapter);

        setAdapter(itemList);

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PublishAdActivity.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getFilter().filter(s);
                return false;
            }
        });
    }

    void setAdapter(List<Item> list){
        adapter = new HomeAdapter(getContext(),list,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        topDealRV.setLayoutManager(linearLayoutManager);
        topDealRV.setAdapter(adapter);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try {
                    User user = snapshot.getValue(User.class);
                    String f_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if(user.getId().equals(f_id)){
                        user_id = user.getId();
                        username.setText(user.getName());

                        Glide.with(getContext())
                                .load(user.getImage())
                                .centerCrop()
                                .placeholder(R.drawable.ic_account)
                                .into(profileImage);
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
    }

    @Override
    public void OnItemPosition(int position) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("price",itemList.get(position).getPrice());
        intent.putExtra("location",itemList.get(position).getLocation());
        intent.putExtra("description",itemList.get(position).getDescription());
        intent.putExtra("shortDescription",itemList.get(position).getShortDescription());
        intent.putExtra("image",itemList.get(position).getImage());
        intent.putExtra("id",itemList.get(position).getId());
        intent.putExtra("user_id",itemList.get(position).getUser_id());
        intent.putExtra("fromMyAd",false);
        startActivity(intent);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

                List<Item> filtered_list = new ArrayList<>();
                searchList = itemList;
                if (charSequence.toString().isEmpty()){
                    filtered_list.addAll(itemList);
                }else{
                    for (Item item: searchList) {
                        if (item.getLocation().toLowerCase().contains(charSequence.toString().toLowerCase())
                                || item.getPrice().toLowerCase().contains(charSequence.toString().toLowerCase())
                                || item.getPropertyType().toLowerCase().contains(charSequence.toString().toLowerCase())
                                || item.getShortDescription().toLowerCase().contains(charSequence.toString().toLowerCase())
                                || item.getDescription().toLowerCase().contains(charSequence.toString().toLowerCase()) ) {
                            filtered_list.add(item);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered_list;

                return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            searchList.clear();
            searchList.addAll((Collection<? extends Item>) filterResults.values);
            adapter.notifyDataSetChanged();
            setAdapter(searchList);
        }
    };
}