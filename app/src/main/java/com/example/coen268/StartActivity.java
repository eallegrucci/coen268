package com.example.coen268;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // TODO: Remove this line when a logout button is added
        // Uncomment the line below and run the application to remove an active session
        // FirebaseAuth.getInstance().signOut();

        mAuth = FirebaseAuth.getInstance();

        Button businessAccountButton = findViewById(R.id.businessAccount);
        businessAccountButton.setOnClickListener(this);

        Button customerAccountButton = findViewById(R.id.customerAccount);
        customerAccountButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if a user is already logged in.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
}
