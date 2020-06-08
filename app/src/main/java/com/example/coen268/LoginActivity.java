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
import android.widget.EditText;
import android.widget.Toast;

import com.example.coen268.user.BusinessOwner;
import com.example.coen268.user.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;

    private String accountType = Constants.ACCOUNT_TYPE_CUSTOMER;

    private FirestoreService firestoreService;
    private boolean mBound;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private TextInputLayout emailTextField;
    private TextInputLayout passwordTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_login));

        // Get account type from Start activity
        accountType = getIntent().getStringExtra(Constants.KEY_ACCOUNT_TYPE);

        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the Google sign-in button.
        SignInButton signInButton = findViewById(R.id.googleSignInButton);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        emailTextField = findViewById(R.id.email);
        passwordTextField = findViewById(R.id.password);

        Button emailPwdButton = findViewById(R.id.emailPwdSignInButton);
        emailPwdButton.setOnClickListener(this);

        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleSignInButton:
                signInWithGoogle();
                break;
            case R.id.emailPwdSignInButton:
                signInWithEmailAndPassword();
                break;
            case R.id.createAccountButton:
                createAccount();
                break;
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithEmailAndPassword() {
        String email = emailTextField.getEditText().getText().toString();
        String password = passwordTextField.getEditText().getText().toString();
        if (email.isEmpty()) {
            emailTextField.setError("Required.");
            return;
        } else {
            emailTextField.setError(null);
        }
        if (password.isEmpty()) {
            passwordTextField.setError("Required.");
            return;
        } else {
            passwordTextField.setError(null);
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar.make(findViewById(R.id.emailPwdSignInButton), "Authentication Failed.", Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void createAccount() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.putExtra(Constants.KEY_ACCOUNT_TYPE, accountType);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Google sign in was successful, authenticate with Firebase
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "Google sign in failed", e);
            updateUI(null);
        }
    }

    /**
     * Authenticate user ID token with Firebase
     * @param account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew) {
                                // TODO fix account creation for Google authentication
                                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                                intent.putExtra(Constants.KEY_ACCOUNT_TYPE, accountType);
                                intent.putExtra(Constants.KEY_IS_GOOGLE_AUTH, true);
                                startActivity(intent);
                            } else {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.googleSignInButton), "Authentication Failed.", Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
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
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
