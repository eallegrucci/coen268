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

import com.example.coen268.user.BusinessOwner;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusinessReservationFragment extends Fragment {
    private static final String TAG = "databaseError";
    private FirestoreService m_firestoreService;
    private DatabaseReference m_databaseReservation;
    private ListView m_myBusinessReserveListView;
    private TextView textView;

    private BusinessOwner businessOwner;

    private BusinessReservationAdapter m_BusinessReservationAdapter;
    private ValueEventListener m_realtimeDbListener;

    public BusinessReservationFragment(FirestoreService connectedService) {
        m_firestoreService = connectedService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        businessOwner = getArguments().getParcelable(Constants.KEY_USER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_reservation, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
        m_myBusinessReserveListView = (ListView) getView().findViewById(R.id.listview_busniessReservation);
        textView = view.findViewById(R.id.textView_reserve);

        m_BusinessReservationAdapter =
                new BusinessReservationAdapter(getActivity(), businessOwner);

        m_myBusinessReserveListView.setAdapter(m_BusinessReservationAdapter);
        initalizeData();
    }

    private void initalizeData() {
        m_realtimeDbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final Reservation reservation = ds.getValue(Reservation.class);
                    if (businessOwner.getBusinessId().equals(reservation.getBusiness_id())) {
                        final String key = ds.getKey();

                        if (reservation.getUser_ids() != null) {
                            m_firestoreService.query("users", "id", reservation.getUser_ids())
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            QuerySnapshot qs = task.getResult();
                                            List<String> userNames = new ArrayList<>();
                                            for (QueryDocumentSnapshot qds : qs) {
                                                String name = (String) qds.get("name");
                                                String id = (String) qds.get("id");
                                                int index = reservation.getUser_ids().indexOf(id);
                                                userNames.add(index, name);
                                            }

                                            m_BusinessReservationAdapter.setReservation(key, reservation, userNames);
                                            m_BusinessReservationAdapter.notifyDataSetChanged();
                                        }
                                    });
                            textView.setVisibility(View.INVISIBLE);
                        } else { // reservation.getUser_ids() == null
                            textView.setVisibility(View.VISIBLE);
                            m_BusinessReservationAdapter.setReservation(null, null, null);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (m_realtimeDbListener != null) {
            m_databaseReservation.removeEventListener(m_realtimeDbListener);
        }
    }
}
