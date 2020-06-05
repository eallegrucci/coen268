package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Button btn_reservationActivity;
    BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navBar = findViewById(R.id.bottomNavigation);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFrag = null;
                switch (item.getItemId()) {
                    case R.id.homeItem:
                        Log.i("onNavigationItemSelected", "search fragment selected");
                        selectedFrag = new SearchActivity();
                        break;
                    case R.id.accountItem:
                        Log.i("onNavigationItemSelected", "account fragment selected");
                        selectedFrag = new Account();
                        break;
                    case R.id.myReservationsItem:
                        Log.i("onNavigationItemSelected", "reservation fragment selected");
                        selectedFrag = new ReservationFragment();
                        break;
                }
                if (selectedFrag != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFrag).addToBackStack(null).commit();
                    return true;
                } else {
                    Log.i("onNavigationItemSelected", "selected frag is null");
                    return false;
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SearchActivity()).addToBackStack(null).commit();

        btn_reservationActivity = findViewById(R.id.reservationActivity);
        btn_reservationActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reservationActivity) {
            Intent i = new Intent(MainActivity.this, BusinessReservation.class);
            startActivity(i);
        }
    }
}
