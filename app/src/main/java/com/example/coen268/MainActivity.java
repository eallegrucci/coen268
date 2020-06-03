package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
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
                new SearchFragment()).addToBackStack(null).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFrag = new SearchActivity();
                    switch (item.getItemId()) {
                        case R.id.search_fragment:
                            selectedFrag = new SearchFragment();
                            break;
//                        case R.id.AccountFragment:
//                            selectedFrag = new SearchActivity();
//                            break;
//                        case R.id.ReservationFragment:
//                            selectedFrag = new SearchActivity();
//                            break;
//                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFrag).addToBackStack(null).commit();
                    return true;
                }
            };
}
