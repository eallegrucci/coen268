package com.example.coen268;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class FCMService extends FirebaseMessagingService {
    public static final String FCM_CMDCHANNEL = "FCM_CMDCHANNEL";
    public static final String CMD_LOGIN = "LOGIN";
    public static final String CMD_LOGOUT = "LOGOUT";

    private static final String TAG = "FCMService";

    private FirebaseFirestore m_db;
    private String m_curRegToken;

    private BroadcastReceiver m_commandReceiver;

    private void associateLoggedInUserAndRegToken(String uid) {
        HashMap<String, Object> record = new HashMap<>();
        record.put("regToken", m_curRegToken);

        m_db.collection("userSession")
                .document(uid)
                .set(record)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "associateLoggedInUserAndRegToken succeeded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing user document", e);
                    }
                });
    }

    private void eraseLoggedInUser(String uid) {
        // Note that success / failure listeners are likely never fired in our app for delete
        // Since before the callback occurs, mAuth may have been already signed out, given this
        // function is called with LOGOUT command
        m_db.collection("userSession")
                .document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "eraseLoggedInUser succeeded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing user document", e);
                    }
                });
    }

    @Override
    public void onCreate() {
        m_db = FirebaseFirestore.getInstance();
        m_commandReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle mBundle = intent.getExtras();
                if(mBundle != null){
                    String cmd = mBundle.getString("Command");
                    if (cmd != null) {
                        final String loggedInUid = mBundle.getString("uid");
                        if (loggedInUid != null) {
                            if (cmd.equals(CMD_LOGIN)) {
                                associateLoggedInUserAndRegToken(loggedInUid);
                            } else if (cmd.equals(CMD_LOGOUT)){
                                eraseLoggedInUser(loggedInUid);
                            }
                        } else {
                            Log.e(TAG, "loggedInUid is null");
                        }
                    }
                }
            }
        };

        registerReceiver(m_commandReceiver, new IntentFilter(FCM_CMDCHANNEL));
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        m_curRegToken = token;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }
//        System.out.println(remoteMessage.getNotification().getBody());

    }
}
