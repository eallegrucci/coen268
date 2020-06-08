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
import android.util.Log;
import android.view.MenuItem;

import com.example.coen268.user.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navBar;

    private User user;

    private FirestoreService firestoreService;
    private boolean mBound;

    private ServiceConnection m_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = getIntent().getParcelableExtra(Constants.KEY_USER);

        navBar = findViewById(R.id.bottomNavigation);
        m_connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                FirestoreService.FirestoreBinder binder = (FirestoreService.FirestoreBinder) service;
                firestoreService = binder.getService();

                if (user.getAccountType().equals(Constants.ACCOUNT_TYPE_CUSTOMER)) {
                    navBar.inflateMenu(R.menu.bottom_navigation);
                    getSupportActionBar().setTitle(getResources().getString(R.string.title_search));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new SearchActivity()).addToBackStack(null).commit();
                } else {
                    navBar.inflateMenu(R.menu.business_bottom_nav);
                    getSupportActionBar().setTitle(getResources().getString(R.string.title_home));
                    BusinessReservationFragment businessReservationFragment = new BusinessReservationFragment(firestoreService);
                    Bundle args = new Bundle();
                    args.putParcelable(Constants.KEY_USER, user);
                    businessReservationFragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            businessReservationFragment).addToBackStack(null).commit();
                }

                navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFrag = null;
                        Bundle args = new Bundle();
                        switch (item.getItemId()) {
                            case R.id.homeItem:
                                Log.i("onNavigationItemSelected", "search fragment selected");
                                selectedFrag = new SearchActivity();
                                getSupportActionBar().setTitle(getResources().getString(R.string.title_search));
                                break;
                            case R.id.accountItem:
                                Log.i("onNavigationItemSelected", "account fragment selected");
                                selectedFrag = new Account();
                                args.putParcelable(Constants.KEY_USER, user);
                                selectedFrag.setArguments(args);
                                getSupportActionBar().setTitle(getResources().getString(R.string.title_account));
                                break;
                            case R.id.myReservationsItem:
                                Log.i("onNavigationItemSelected", "reservation fragment selected");
                                selectedFrag = new CustomerReservationFragment(firestoreService);
                                getSupportActionBar().setTitle(getResources().getString(R.string.title_reservations));
                                break;
                            case R.id.businessHomeItem:
                                Log.i("onNavigationItemSelected", "business home fragment selected");
                                selectedFrag = new BusinessReservationFragment(firestoreService);
                                args = new Bundle();
                                args.putParcelable(Constants.KEY_USER, user);
                                selectedFrag.setArguments(args);
                                getSupportActionBar().setTitle(getResources().getString(R.string.title_home));
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
