package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BusinessReservation extends AppCompatActivity {
    private static final String TAG = "databaseError";
    DatabaseReference m_databaseReservation;
    ListView m_myBusinessReserveListView;
    ArrayList<String> m_userIdsTarget;
    Button m_acceptbtn;
    BusinessReservationAdapter m_BusinessReservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_reservation);


        m_databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
//        databaseReservation = FirebaseDatabase.getInstance().getReference();
        m_myBusinessReserveListView = (ListView)findViewById(R.id.listview_busniessReservation);

        m_userIdsTarget = new ArrayList<>();

        m_BusinessReservationAdapter =
                new BusinessReservationAdapter(BusinessReservation.this, m_userIdsTarget, m_acceptbtn);

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