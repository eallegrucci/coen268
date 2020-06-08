package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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

    private BroadcastReceiver m_safeLogoutReceiver;
    public static final String LOGOUT_RECEIVER = "ACCOUNT_LOGOUT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(Constants.KEY_USER);
        final Account curThis = this;
        m_safeLogoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FirebaseAuth.getInstance().signOut();
                curThis.getActivity().finish();
            }
        };
        getActivity().registerReceiver(m_safeLogoutReceiver, new IntentFilter(Account.LOGOUT_RECEIVER));
    }

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
            }
        });


        username = v.findViewById(R.id.tvUserName);

        String name = user.getDisplayName();
        username.setText("Hello, " + name);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (m_safeLogoutReceiver != null) {
            getActivity().unregisterReceiver(m_safeLogoutReceiver);
        }
    }
}