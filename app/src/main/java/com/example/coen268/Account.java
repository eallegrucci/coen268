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

import com.google.firebase.auth.FirebaseAuth;

public class Account extends Fragment {

    private Button logout;
    private TextView username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_account, container, false);

        logout = v.findViewById(R.id.btnLogOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FCMService.FCM_CMDCHANNEL);
                intent.putExtra("Command", FCMService.CMD_LOGOUT);
                intent.putExtra("uid", FirebaseAuth.getInstance().getUid());
                getActivity().sendBroadcast(intent);

                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });


        username = v.findViewById(R.id.tvUserName);

        //TODO: Add user's name below
        String name = "";
        username.setText("Hello, " + name);

        return v;
    }
}