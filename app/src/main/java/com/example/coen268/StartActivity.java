package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.coen268.user.BusinessOwner;
import com.example.coen268.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = StartActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirestoreService firestoreService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();

        Button businessAccountButton = findViewById(R.id.businessAccount);
        businessAccountButton.setOnClickListener(this);

        Button customerAccountButton = findViewById(R.id.customerAccount);
        customerAccountButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to FirestoreService
        Intent intent = new Intent(this, FirestoreService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private void updateUI(FirebaseUser fbUser) {
        if (fbUser != null) {
            firestoreService.getDocument("users", fbUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            User user = null;
                            String id = (String) document.getData().get("id");
                            String type = (String) document.getData().get("type");
                            String name = (String) document.getData().get("name");
                            String email = (String) document.getData().get("email");
                            if (type.equals(Constants.ACCOUNT_TYPE_CUSTOMER)) {
                                user = new User.UserBuilder(type)
                                        .setId(id)
                                        .setDisplayName(name)
                                        .setEmail(email)
                                        .build();
                            } else if (type.equals(Constants.ACCOUNT_TYPE_BUSINESS)) {
                                String businessId = (String) document.getData().get("business_id");
                                user = new BusinessOwner.BusinessOwnerBuilder(type)
                                        .setId(id)
                                        .setDisplayName(name)
                                        .setEmail(email)
                                        .setBusinessId(businessId)
                                        .build();
                            } else {
                                Snackbar.make(findViewById(R.id.emailPwdSignInButton), "User is invalid.", Snackbar.LENGTH_LONG).show();
                            }

                            if (user != null) {
                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                intent.putExtra(Constants.KEY_USER, user);
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "unable to find document");
                            Snackbar.make(findViewById(R.id.emailPwdSignInButton), "User not found.", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Snackbar.make(findViewById(R.id.emailPwdSignInButton), "Unable to retrieve user.", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        String accountType = "";
        switch (v.getId()) {
            case R.id.businessAccount:
                accountType = Constants.ACCOUNT_TYPE_BUSINESS;
                break;
            case R.id.customerAccount:
                accountType = Constants.ACCOUNT_TYPE_CUSTOMER;
                break;
        }

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.KEY_ACCOUNT_TYPE, accountType);
        startActivity(intent);
    }

    private void onServiceAttached() {
        // Check if a user is already logged in.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            FirestoreService.FirestoreBinder binder = (FirestoreService.FirestoreBinder) service;
            firestoreService = binder.getService();
            mBound = true;
            onServiceAttached();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
