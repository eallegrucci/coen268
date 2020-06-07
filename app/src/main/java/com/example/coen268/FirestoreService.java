package com.example.coen268;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coen268.user.BusinessOwner;
import com.example.coen268.user.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class FirestoreService extends Service {
    private final static String TAG = FirestoreService.class.getSimpleName();

    private final IBinder binder = new FirestoreBinder();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class FirestoreBinder extends Binder {
        FirestoreService getService() {
            return FirestoreService.this;
        }
    }

    public void addUser(User user) {
        HashMap<String, Object> record = new HashMap<>();
        record.put("id", user.getId());
        record.put("name", user.getDisplayName());
        record.put("email", user.getEmail());
        record.put("type", user.getAccountType());
        if (user.getAccountType().equals(Constants.ACCOUNT_TYPE_BUSINESS)) {
            record.put("business_id", ((BusinessOwner) user).getBusinessId());
        }

        db.collection("users")
                .document(user.getId())
                .set(record)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User document successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing user document", e);
                    }
                });
    }

    public Task<DocumentSnapshot> getDocument(String collection, String docId) {
        DocumentReference docRef = db.collection(collection).document(docId);
        return docRef.get();
    }

    public Task<QuerySnapshot> query(String collection, String field, Object value) {
        Query query = db.collection(collection).whereEqualTo(field, value);
        return query.get();
    }

    public Task<QuerySnapshot> query(String collection, String field, List<? extends Object> values) {
        Query query = db.collection(collection).whereIn(field, values);
        return query.get();
    }

}
