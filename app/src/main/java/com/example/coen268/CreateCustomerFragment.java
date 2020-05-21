package com.example.coen268;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;


public class CreateCustomerFragment extends Fragment implements View.OnClickListener {

    private OnCreateAccountListener listener;

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

        listener.createAccount(new Customer(displayName, email, password));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreateAccountListener) {
            listener = (OnCreateAccountListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCreateAccountListener");
        }
    }

    public interface OnCreateAccountListener {
        void createAccount(Customer customer);
    }

    public class Customer {
        private String displayName;
        private String email;
        private String password;

        public Customer(String displayName, String email, String password) {
            this.displayName = displayName;
            this.email = email;
            this.password = password;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }


}
