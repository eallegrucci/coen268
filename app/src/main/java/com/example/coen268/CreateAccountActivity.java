package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.coen268.fragment.CreateBusinessFragment;
import com.example.coen268.fragment.CreateCustomerFragment;
import com.example.coen268.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateAccountActivity extends AppCompatActivity implements User.OnCreateAccountListener {
    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        accountType = getIntent().getStringExtra(Constants.KEY_ACCOUNT_TYPE);
        initializeDisplay();
    }

    private void initializeDisplay() {
        switch (accountType) {
            case Constants.ACCOUNT_TYPE_CUSTOMER:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new CreateCustomerFragment()).commit();
                break;
            case Constants.ACCOUNT_TYPE_BUSINESS:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new CreateBusinessFragment()).commit();
                break;
        }
    }

    @Override
    public void createAccount(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            updateProfile(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(findViewById(R.id.fragment_container), "Failed to create account.", Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateProfile(final User user) {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getDisplayName())
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            updateUI(firebaseUser);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            // Stay on page.
        } else {
            // TODO: Go to main activity
        }
    }

}
