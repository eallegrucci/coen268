package com.example.coen268;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView rvRestaurants;
    private RestaurantsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<YelpRestaurant> yelpRestaurants;


    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String TAG = "MainActivity";

    //Yelp Key and ID
    private static final String YELP_API_KEY = "QvovY7ARu_g-PbylLUKjbNTaNRYScXpmYZYsJQ3kXrYiXcLDRoDw9BKON_MW8oYWwh5EQObD6nDoZ_fagp5IN3eTJe-ITcjtUnfvILCKixiIr8keyjDd-zfoVoXCXnYx";
    private static final String YELP_CLIENT_ID = "iR8bLXS3k8VWr71rfHcIag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        yelpRestaurants = new ArrayList<>();

        rvRestaurants = findViewById(R.id.rvRestaurants);
        rvRestaurants.setHasFixedSize(true);

        mAdapter = new RestaurantsAdapter(this, yelpRestaurants);
        mLayoutManager = new LinearLayoutManager(this);
        rvRestaurants.setAdapter(mAdapter);
        rvRestaurants.setLayoutManager(mLayoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = retrofit.create(YelpService.class);

        Callback<Object> callback = null;
        yelpService.searchRestaurants("Bearer "+ YELP_API_KEY, "Avocado Toast", "New York")
                .enqueue(new Callback<YelpSearchResults>() {
                    @Override
                    public void onResponse(Call<YelpSearchResults> call, Response<YelpSearchResults> response) {
                        Log.i(TAG, "Response: " + response);
                        YelpSearchResults body = response.body();
                        if (body == null) {
                            Log.w(TAG, "Did not receive valid response from Yelp API... exiting");
                            return;
                        }
                        yelpRestaurants.addAll(body.restaurants);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<YelpSearchResults> call, Throwable t) {
                        Log.i(TAG, "Failure: " + t);
                    }
                });
    }
}
