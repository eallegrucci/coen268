package com.example.coen268.fragment;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coen268.Constants;
import com.example.coen268.R;
import com.example.coen268.user.User;
import com.google.android.material.textfield.TextInputLayout;

public class CreateAccountAddressFragment extends Fragment implements View.OnClickListener {

    private User.OnCreateAccountListener listener;
    private User user;

    private Button createButton;

    private TextInputLayout streetTextField;
    private TextInputLayout cityTextField;
    private TextInputLayout zipTextField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(Constants.KEY_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account_address, container, false);
        createButton = view.findViewById(R.id.create);
        createButton.setOnClickListener(this);
        streetTextField = view.findViewById(R.id.street);
        cityTextField = view.findViewById(R.id.city);
        zipTextField = view.findViewById(R.id.zip);

        return view;
    }

    @Override
    public void onClick(View v) {
        String street = streetTextField.getEditText().getText().toString();
        String city = cityTextField.getEditText().getText().toString();
        String zip = zipTextField.getEditText().getText().toString();

        if (street.isEmpty()) {
            streetTextField.setError("Required.");
            return;
        } else {
            streetTextField.setError(null);
        }

        if (city.isEmpty()) {
            cityTextField.setError("Required.");
            return;
        } else {
            cityTextField.setError(null);
        }

        if (zip.isEmpty()) {
            zipTextField.setError("Required.");
            return;
        } else {
            zipTextField.setError(null);
        }

        listener.createAccount(new User.UserBuilder(user.getAccountType())
                .setDisplayName(user.getDisplayName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setStreetAddress(street)
                .setCity(city)
                .setZip(zip)
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
