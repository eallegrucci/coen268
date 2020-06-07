package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {
    private String image_url, name, phone, price, street, address, distance, hours, id;
    private double rating, reviewCount;
    private static final String TAG ="database ERROR";
    Button reserveSpotButton, returnBackButton;
    TextView tvName, tvCount, tvPrice, tvDistance, tvPhone, tvStreet, tvAddress,restaurantNumOpenSpots;
    ImageView ivImage;
    RatingBar mRatingBar;
    DatabaseReference m_databaseReservation;
    private FirebaseAuth m_Auth;
    FirebaseUser m_firebaseUser;
    String uID;
    Boolean IsCancelReserveBtn;
    Reservation m_reservation;
    String m_keyReservation;
    ValueEventListener m_realtimeDbListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_Auth = FirebaseAuth.getInstance();
        m_firebaseUser = m_Auth.getCurrentUser();
        uID = m_firebaseUser.getUid();

        Intent i = getIntent();
        getInfo(i);
        m_databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
        m_realtimeDbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    m_reservation = ds.getValue(Reservation.class);
                    if(m_reservation.getBusiness_id().equals(id)){
                        m_keyReservation = ds.getKey();
                        reserveSpotButton.setEnabled(true);
                        List<String> user_ids = m_reservation.getUser_ids();

                        Integer quotaLeft =
                                Integer.parseInt(m_reservation.getQuota()) - (user_ids == null? 0:user_ids.size());
                        restaurantNumOpenSpots.setText(quotaLeft.toString());

                        Boolean hasReserveSpot = false;

                        if(user_ids != null){
                            for(int id_index = 0; id_index < user_ids.size(); id_index ++){
                                if(user_ids.get(id_index).equals(uID)){
                                    hasReserveSpot = true;
                                    IsCancelReserveBtn = true;
                                    reserveSpotButton.setText("Cancel Reservation");
                                }
                            }
                        }
                        if(hasReserveSpot == false){
                            if(quotaLeft == 0){
                                reserveSpotButton.setEnabled(false);
                                reserveSpotButton.setText("Full");
                            }
                            else{
                                IsCancelReserveBtn = false;
                                reserveSpotButton.setText("Reserve Spot");
                            }

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

        reserveSpotButton = (Button) findViewById(R.id.reserveButton);
        returnBackButton = (Button) findViewById(R.id.returnBackButton);
        restaurantNumOpenSpots = (TextView)findViewById(R.id.restaurantNumOpenSpots);
        tvName = (TextView) findViewById(R.id.restaurantName);
        tvCount = (TextView) findViewById(R.id.restaurantRevCount);
        tvPrice = (TextView) findViewById(R.id.restaurantPrice);
        tvDistance = (TextView) findViewById(R.id.restaurantDistance);
        tvPhone = (TextView) findViewById(R.id.restaurantPhoneNumber);
        tvStreet = (TextView) findViewById(R.id.restaurantStreetAddress);
        tvAddress = (TextView) findViewById(R.id.restaurantCityStateZip);
        ivImage = (ImageView) findViewById(R.id.restaurantImage);
        mRatingBar = (RatingBar) findViewById(R.id.restaurantRatingBar);

        setViews();
        //this listener need to be added last.
        m_databaseReservation.addValueEventListener(m_realtimeDbListener);

    }

    private void getInfo(Intent i) {
        id = i.getStringExtra("id");
        image_url = i.getStringExtra("image");
        name = i.getStringExtra("name");
        phone = i.getStringExtra("phone");
        price = i.getStringExtra("price");
        distance = i.getStringExtra("distance");
        rating = i.getDoubleExtra("rating", 0);
        reviewCount = i.getDoubleExtra("reviewCount", 0);
        street = i.getStringExtra("street");
        address = i.getStringExtra("address");
        hours = i.getStringExtra("hours");
    }

    private void setViews() {
        restaurantNumOpenSpots.setText("Not Available");//initialize the openSpot.
        tvName.setText(name);
        tvCount.setText(reviewCount + " Reviews");
        tvPrice.setText(price);
        tvDistance.setText(distance);
        tvPhone.setText(phone);
        tvStreet.setText(street);
        tvAddress.setText(address);
        Glide.with(this).load(image_url).transform(new CenterCrop()).into(ivImage);
        mRatingBar.setRating((float) rating);
    }
    public void ReserveSpot(View view){
        List<String>user_ids = m_reservation.getUser_ids();
        if(IsCancelReserveBtn){//remove user from real database
           for(int index = 0; index < user_ids.size(); index++){
               if(user_ids.get(index).equals(uID)){
                   //uID is unique, so there should be only one in the list of reservation.getUser_ids()
                   user_ids.remove(index);
                   break;
               }
           }
        }
        else{
            if(user_ids == null){
                user_ids = new ArrayList<>();
                m_reservation.setUser_ids(user_ids);
            }
            user_ids.add(uID);
        }
        m_databaseReservation.child(m_keyReservation).setValue(m_reservation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (m_realtimeDbListener != null) {
            m_databaseReservation.removeEventListener(m_realtimeDbListener);
        }
    }

    public void ReturnBackButtonClick(View v) {
        finish();
    }

}
