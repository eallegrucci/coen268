package com.example.coen268;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coen268.user.BusinessOwner;
import com.example.coen268.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class BusinessReservationFragment extends Fragment {
    private static final String TAG = "databaseError";
    private FirestoreService m_firestoreService;
    private FirebaseAuth m_Auth;
    FirebaseUser m_firebaseUser;
    DatabaseReference m_databaseReservation;
    ListView m_myBusinessReserveListView;
    BusinessReservationAdapter m_BusinessReservationAdapter;
    ValueEventListener m_realtimeDbListener;

    public BusinessReservationFragment(FirestoreService connectedService) {
        m_firestoreService = connectedService;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_reservation, container, false);
        m_Auth = FirebaseAuth.getInstance();
        m_firebaseUser = m_Auth.getCurrentUser();
        return v;
    }

    public void queryDataBaseGetBussinessID(){
        m_firestoreService.query("users","id", m_firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                   if(task.getResult().size() > 0){
                       QueryDocumentSnapshot snapshot =  task.getResult().iterator().next();
                       final String bizID = (String)snapshot.getData().get("business_id");
//                       System.out.print(bizID);
                       m_realtimeDbListener = new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               for(DataSnapshot ds:dataSnapshot.getChildren()){
                                   final Reservation reservation = ds.getValue(Reservation.class);
                                   String matchedBizId = reservation.getBusiness_id();
                                   if(matchedBizId.equals(bizID)){
                                       final String key = ds.getKey();

                                       if (reservation.getUser_ids() != null) {
                                           m_firestoreService.query("users", "id", reservation.getUser_ids())
                                                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                           QuerySnapshot qs = task.getResult();
                                                           List<String> userNames = new ArrayList<>();
                                                           int count = 0;
                                                           for (QueryDocumentSnapshot qds : qs) {
                                                               String name = (String) qds.get("name");
                                                               if (!qds.get("id").equals(reservation.getUser_ids().get(count))) {
                                                                   // throw new Exception("id not match name in correct order");
                                                                   Log.e(TAG, "Database ID not matching reservation ID in correct order");
                                                               }

                                                               userNames.add(name);
                                                               count++;
                                                           }

                                                           m_BusinessReservationAdapter.setReservation(key, reservation, userNames);
                                                           m_BusinessReservationAdapter.notifyDataSetChanged();
                                                       }
                                                   });
                                       } else { // reservation.getUser_ids() == null
                                           m_BusinessReservationAdapter.notifyDataSetChanged();
                                       }

                                       break;
                                   }
                               }
                           }
                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {
                               Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                           }
                       };
                       m_databaseReservation.addValueEventListener(m_realtimeDbListener);
                   }

                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
        m_myBusinessReserveListView = (ListView) getView().findViewById(R.id.listview_busniessReservation);

        m_BusinessReservationAdapter =
                new BusinessReservationAdapter(getActivity());

        m_myBusinessReserveListView.setAdapter(m_BusinessReservationAdapter);
        queryDataBaseGetBussinessID();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (m_realtimeDbListener != null) {
            m_databaseReservation.removeEventListener(m_realtimeDbListener);
        }
    }

}
