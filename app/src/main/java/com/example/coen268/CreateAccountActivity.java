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
import android.widget.Toast;

import com.example.coen268.user.BusinessOwner;
import com.example.coen268.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.function.Function;

public class CreateAccountActivity extends AppCompatActivity implements User.OnCreateAccountListener {
    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private String accountType;
    private boolean isGoogleAuth;

    private FirestoreService firestoreService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getSupportActionBar().setTitle(getResources().getString(R.string.title_create_account));

        mAuth = FirebaseAuth.getInstance();

        accountType = getIntent().getStringExtra(Constants.KEY_ACCOUNT_TYPE);
        isGoogleAuth = getIntent().getBooleanExtra(Constants.KEY_IS_GOOGLE_AUTH, false);
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
        args.putBoolean(Constants.KEY_IS_GOOGLE_AUTH, isGoogleAuth);
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
                                if (task.getResult().size() != 0) {
                                    if (isGoogleAuth) {
                                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User account deleted.");

                                                    Toast.makeText(CreateAccountActivity.this, "Failed to create account. Account already exists.", Toast.LENGTH_LONG).show();
                                                    Log.d(TAG, "An account for " + user.getDisplayName() + " already exists");

                                                    finish();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(CreateAccountActivity.this, "Failed to create account. Account already exists.", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "An account for " + user.getDisplayName() + " already exists");

                                        finish();
                                    }
                                } else {
                                    createAccountWithEmailPassword(user);
                                }
                            } else {
                                Snackbar.make(findViewById(R.id.fragment_container), "Error retrieving documents.", Snackbar.LENGTH_LONG);
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            createAccountWithEmailPassword(user);
        }
    }

    private void createAccountWithEmailPassword(final User user) {
        if (isGoogleAuth) {
            updateProfile(user);
        } else {
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");

                                AuthResult res = task.getResult();
                                String createdUid = res.getUser().getUid();

                                Intent intent = new Intent(FCMService.FCM_CMDCHANNEL);
                                intent.putExtra("Command", FCMService.CMD_LOGIN);
                                intent.putExtra("uid", createdUid);
                                sendBroadcast(intent);

                                updateProfile(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Snackbar.make(findViewById(R.id.fragment_container), "Failed to create account.", Snackbar.LENGTH_LONG).show();
                                updateUI(null, null);
                            }
                        }
                    });
        }
        // Register a document in real time database for business reservation info
        if (user.getAccountType().equals(Constants.ACCOUNT_TYPE_BUSINESS)) {
            DatabaseReference databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");

            // Assume the firestore database is working; that means when code reaches here,
            // the business must be a new business, and thus doesn't exist in the realtime database
            DatabaseReference newEntry = databaseReservation.push();
            BusinessOwner bizUser = (BusinessOwner) user;

            final Number quota = 3; // TODO: if time allows, please add UI that can change this quota
            Reservation newReservation = new Reservation(bizUser.getBusinessId(), new ArrayList<String>(), quota.toString());
            newEntry.setValue(newReservation);
        }
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
                            updateUI(firebaseUser, user);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser fbUser, User user) {
        if (fbUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
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
