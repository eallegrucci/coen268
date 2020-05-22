package com.example.coen268.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coen268.R;
import com.example.coen268.user.Customer;
import com.example.coen268.user.User;
import com.google.android.material.textfield.TextInputLayout;


public class CreateCustomerFragment extends Fragment implements View.OnClickListener {

    private User.OnCreateAccountListener listener;

    private Button createButton;

    private TextInputLayout nameTextField;
    private TextInputLayout emailTextField;
    private TextInputLayout passwordTextField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_customer, container, false);
        createButton = view.findViewById(R.id.create);
        createButton.setOnClickListener(this);
        nameTextField = view.findViewById(R.id.name);
        emailTextField = view.findViewById(R.id.email);
        passwordTextField = view.findViewById(R.id.password);
        return view;
    }

    @Override
    public void onClick(View v) {
        String displayName = nameTextField.getEditText().getText().toString();
        String email = emailTextField.getEditText().getText().toString();
        String password = passwordTextField.getEditText().getText().toString();

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

        listener.createAccount(new Customer.CustomerBuilder()
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
}
