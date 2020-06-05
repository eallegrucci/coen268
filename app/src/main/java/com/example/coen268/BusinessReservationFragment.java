package com.example.coen268;

// TODO: convert reservation activity to fragment
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BusinessReservationFragment extends Fragment {
    private static final String TAG = "databaseError";
    DatabaseReference m_databaseReservation;
    ListView m_myBusinessReserveListView;
    ArrayList<String> m_userIdsTarget;
    Button m_acceptbtn;
    BusinessReservationAdapter m_BusinessReservationAdapter;

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
        try{
            m_myBusinessReserveListView = (ListView)getView().findViewById(R.id.listview_busniessReservation);
        }catch(Exception e){
            System.out.print("");
        }


        m_userIdsTarget = new ArrayList<>();

        m_BusinessReservationAdapter =
                new BusinessReservationAdapter(getActivity(), m_userIdsTarget, m_acceptbtn);

        m_myBusinessReserveListView.setAdapter(m_BusinessReservationAdapter);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Reservation reservation = ds.getValue(Reservation.class);
//                    System.out.println(reservation);
                    List<String> userIdSrc =reservation.getUser_ids();
                    m_userIdsTarget.clear();
                    for(String str: userIdSrc){
                        m_userIdsTarget.add(str);
                    }
                    m_BusinessReservationAdapter.notifyDataSetChanged();

                    break; // temporily jup out of this loop for testing purpose since we only have one data now
                    // TODO: need to remove the break and do a search for the business id we want to display
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        m_databaseReservation.addValueEventListener(postListener);

    }
}
