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

import com.example.coen268.user.BusinessOwner;
import com.example.coen268.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.function.Function;

public class CreateAccountActivity extends AppCompatActivity implements User.OnCreateAccountListener {
    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private String accountType;

    private FirestoreService firestoreService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        accountType = getIntent().getStringExtra(Constants.KEY_ACCOUNT_TYPE);
        initializeDisplay();
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

    private void initializeDisplay() {
        CreateAccountCredentialsFragment accountCredentialsFragment = new CreateAccountCredentialsFragment();

        Bundle args = new Bundle();
        args.putString(Constants.KEY_ACCOUNT_TYPE, accountType);
        accountCredentialsFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, accountCredentialsFragment).commit();
    }

    public void onCredentialsCompleted(User user) {
        RegisterBusinessFragment accountAddressFragment = new RegisterBusinessFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_USER, user);
        accountAddressFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, accountAddressFragment).addToBackStack(null).commit();
    }

    @Override
    public void createAccount(final User user) {
        if (user.getAccountType().equals(Constants.ACCOUNT_TYPE_BUSINESS)) {
            firestoreService.query("users", "business_id", ((BusinessOwner) user).getBusinessId())
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null || task.getResult().size() != 0) {
                                    Snackbar.make(findViewById(R.id.fragment_container), "Failed to create account. Account already exists.", Snackbar.LENGTH_LONG).show();
                                    Log.d(TAG, "An account for " + user.getDisplayName() + " already exists");
                                } else {
                                    createAccountWithEmailPassword(user);
                                }
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            createAccountWithEmailPassword(user);
        }
    }

    private void createAccountWithEmailPassword(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
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
                            user.setId(firebaseUser.getUid());
                            if (mBound) {
                                firestoreService.addUser(user);
                            }
                            updateUI(firebaseUser);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            FirestoreService.FirestoreBinder binder = (FirestoreService.FirestoreBinder) service;
            firestoreService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
