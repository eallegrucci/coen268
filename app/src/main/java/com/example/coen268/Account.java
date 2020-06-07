package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.coen268.user.User;
import com.google.firebase.auth.FirebaseAuth;

public class Account extends Fragment {

    private Button logout;
    private TextView username;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(Constants.KEY_USER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_account, container, false);

        logout = v.findViewById(R.id.btnLogOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), StartActivity.class);
                startActivity(intent);
            }
        });


        username = v.findViewById(R.id.tvUserName);

        String name = user.getDisplayName();
        username.setText("Hello, " + name);

        return v;
    }
}