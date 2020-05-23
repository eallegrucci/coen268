package com.example.coen268;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class RestaurantActivity extends AppCompatActivity {
    private String image_url, name, phone, price, street, address, distance;
    private double rating, reviewCount;

    Button reserveSpotButton, returnHomeButton;
    TextView tvName, tvCount, tvPrice, tvDistance, tvPhone, tvStreet, tvAddress, tvHours;
    ImageView ivImage;
    RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        getInfo(i);

        reserveSpotButton = (Button) findViewById(R.id.reserveButton);
        returnHomeButton = (Button) findViewById(R.id.returnHomeButton);

        tvName = (TextView) findViewById(R.id.restaurantName);
        tvCount = (TextView) findViewById(R.id.restaurantRevCount);
        tvPrice = (TextView) findViewById(R.id.restaurantPrice);
        tvDistance = (TextView) findViewById(R.id.restaurantDistance);
        tvPhone = (TextView) findViewById(R.id.restaurantPhoneNumber);
        tvStreet = (TextView) findViewById(R.id.restaurantStreetAddress);
        tvAddress = (TextView) findViewById(R.id.restaurantCityStateZip);
        tvHours = (TextView) findViewById(R.id.restaurantHours);

        ivImage = (ImageView) findViewById(R.id.restaurantImage);

        mRatingBar = (RatingBar) findViewById(R.id.restaurantRatingBar);

        setViews();
    }

    private void getInfo(Intent i) {
        image_url = i.getStringExtra("image");
        name = i.getStringExtra("name");
        phone = i.getStringExtra("phone");
        price = i.getStringExtra("price");
        distance = i.getStringExtra("distance");
        rating = i.getDoubleExtra("rating", 0);
        reviewCount = i.getDoubleExtra("reviewCount", 0);
        street = i.getStringExtra("street");
        address = i.getStringExtra("address");
    }

    private void setViews() {
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
}
