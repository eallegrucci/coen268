package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navBar = findViewById(R.id.bottomNavigation);
        navBar.setOnNavigationItemSelectedListener(navListener);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SearchActivity()).addToBackStack(null).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFrag = new SearchActivity();
                    switch (item.getItemId()) {
                        case R.id.search_fragment:
                            Log.i("onNavigationItemSelected", "search fragment selected");
                            selectedFrag = new SearchActivity();
                            break;
                        case R.id.account_fragment:
                            Log.i("onNavigationItemSelected", "account fragment selected");
                            selectedFrag = new SearchActivity();
                            break;
                        case R.id.reservation_fragment:
                            Log.i("onNavigationItemSelected", "reservation fragment selected");
                            selectedFrag = new SearchActivity();
                            break;
                    }
                    if (selectedFrag != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFrag).addToBackStack(null).commit();
                        return true;
                    }
                    return false;
                }
            };
}
