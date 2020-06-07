package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navBar;

    private ServiceConnection m_connection;
    private FirestoreService m_fireStoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navBar = findViewById(R.id.bottomNavigation);

        m_connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                FirestoreService.FirestoreBinder binder = (FirestoreService.FirestoreBinder) service;
                m_fireStoreService = binder.getService();

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
                                selectedFrag = new BusinessReservationFragment(m_fireStoreService);
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

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };

        // Bind to FirestoreService
        Intent intent = new Intent(this, FirestoreService.class);
        bindService(intent, m_connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_connection != null) {
            unbindService(m_connection);
        }
    }
}
