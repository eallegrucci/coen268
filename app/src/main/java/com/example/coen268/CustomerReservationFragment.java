package com.example.coen268;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerReservationFragment extends Fragment {
    private static final String TAG = CustomerReservationFragment.class.getSimpleName();

    private FirestoreService firestoreService;
    private DatabaseReference databaseReservation;
    private ListView reservationListView;
    private TextView textNoReservation;

    private CustomerReservationAdapter customerReservationAdapter;
    private ValueEventListener realtimeDbListener;

    public CustomerReservationFragment(FirestoreService connectedService) {
        firestoreService = connectedService;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customer_reservation, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
        reservationListView = (ListView) getView().findViewById(R.id.reservationList);
        textNoReservation = view.findViewById(R.id.textNoReservation);

        customerReservationAdapter =
                new CustomerReservationAdapter(getActivity());

        reservationListView.setAdapter(customerReservationAdapter);
        initalizeData();
    }

    private void initalizeData() {
        realtimeDbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                List<String> businessIdList = new ArrayList<>();
                final Map<String, CustomerReservationAdapter.ReservationDisplay> reservationDisplayMap = new HashMap<>();
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    final Reservation reservation = ds.getValue(Reservation.class);
                    if (reservation.getUser_ids() != null && reservation.getUser_ids().indexOf(uid) != -1) {
                        businessIdList.add(reservation.getBusiness_id());
                        reservationDisplayMap.put(reservation.getBusiness_id(), new CustomerReservationAdapter.ReservationDisplay(ds.getKey(), reservation));
                    }
                }
                if (reservationDisplayMap.isEmpty()) {
                    textNoReservation.setVisibility(View.VISIBLE);
                    customerReservationAdapter.setReservationList(null);
                    customerReservationAdapter.notifyDataSetChanged();
                } else {
                    firestoreService.query("users", "business_id", businessIdList)
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot qs = task.getResult();
                                    List<CustomerReservationAdapter.ReservationDisplay> reservationDisplayList = new ArrayList<>();
                                    for (QueryDocumentSnapshot qds : qs) {
                                        String name = (String) qds.get("name");
                                        String id = (String) qds.get("business_id");
                                        CustomerReservationAdapter.ReservationDisplay reservationDisplay = reservationDisplayMap.get(id);
                                        reservationDisplay.setBusinessName(name);
                                        reservationDisplayList.add(reservationDisplay);
                                    }

                                    customerReservationAdapter.setReservationList(reservationDisplayList);
                                    customerReservationAdapter.notifyDataSetChanged();
                                }
                            });
                    textNoReservation.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReservation.addValueEventListener(realtimeDbListener);
    }
}
