package com.example.deluxerealty.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.deluxerealty.R;
import com.fragments.AccountFragment;
import com.fragments.ChatFragment;
import com.fragments.FavoriteFragment;
import com.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); 

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        loadFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment  = null;
        switch (item.getItemId()){

            case R.id.menu_home:
                fragment = new HomeFragment();
                break;
            case R.id.menu_fav:
                fragment = new FavoriteFragment();
                break;

            case R.id.menu_account:
                fragment = new AccountFragment();

                break;

        }

                return loadFragment(fragment);
    }

    boolean loadFragment(Fragment fragment){

        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }


}