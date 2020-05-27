package com.example.coen268.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coen268.Constants;
import com.example.coen268.R;
import com.example.coen268.user.User;
import com.google.android.material.textfield.TextInputLayout;


public class CreateAccountCredentialsFragment extends Fragment implements View.OnClickListener {

    private User.OnCreateAccountListener listener;

    private String accountType;

    private Button nextButton;

    private TextInputLayout nameTextField;
    private TextInputLayout emailTextField;
    private TextInputLayout passwordTextField;
    private TextInputLayout confirmPasswordTextField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountType = getArguments().getString(Constants.KEY_ACCOUNT_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account_credentials, container, false);
        nextButton = view.findViewById(R.id.next);
        nextButton.setOnClickListener(this);
        nameTextField = view.findViewById(R.id.name);
        emailTextField = view.findViewById(R.id.email);
        passwordTextField = view.findViewById(R.id.password);
        confirmPasswordTextField = view.findViewById(R.id.confirmPassword);

        updateUI();

        return view;
    }

    @Override
    public void onClick(View v) {
        String displayName = nameTextField.getEditText().getText().toString();
        String email = emailTextField.getEditText().getText().toString();
        String password = passwordTextField.getEditText().getText().toString();
        String confirmPassword = confirmPasswordTextField.getEditText().getText().toString();

        if (displayName.isEmpty()) {
            nameTextField.setError("Required.");
            return;
        } else {
            nameTextField.setError(null);
        }

        if (email.isEmpty()) {
            emailTextField.setError("Required.");
            return;
        } else {
            emailTextField.setError(null);
        }

        if (password.isEmpty()) {
            passwordTextField.setError("Required.");
            return;
        } else if (password.length() < 6) {
            passwordTextField.setError("Password must be at least 6 characters.");
            return;
        } else {
            passwordTextField.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordTextField.setError("Required.");
            return;
        } else if (confirmPassword.length() < 6) {
            confirmPasswordTextField.setError("Password must be at least 6 characters.");
            return;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordTextField.setError("Password mismatch.");
            return;
        } else {
            confirmPasswordTextField.setError(null);
        }

        listener.onCredentialsCompleted(new User.UserBuilder(accountType)
                .setDisplayName(displayName)
                .setEmail(email)
                .setPassword(password)
                .build()
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof User.OnCreateAccountListener) {
            listener = (User.OnCreateAccountListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCreateAccountListener");
        }
    }

    private void updateUI() {
        switch(accountType) {
            case Constants.ACCOUNT_TYPE_CUSTOMER:
                nameTextField.setHint(getResources().getString(R.string.prompt_name));
                break;
            case Constants.ACCOUNT_TYPE_BUSINESS:
                nameTextField.setHint(getResources().getString(R.string.prompt_business));
                break;
        }
    }

}
