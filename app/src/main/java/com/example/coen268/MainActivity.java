package com.example.coen268;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String TAG = "MainActivity";

    //Yelp Key and ID
    private static final String YELP_API_KEY = "QvovY7ARu_g-PbylLUKjbNTaNRYScXpmYZYsJQ3kXrYiXcLDRoDw9BKON_MW8oYWwh5EQObD6nDoZ_fagp5IN3eTJe-ITcjtUnfvILCKixiIr8keyjDd-zfoVoXCXnYx";
    private static final String YELP_CLIENT_ID = "iR8bLXS3k8VWr71rfHcIag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = retrofit.create(YelpService.class);
        
        Callback<Object> callback = null;
        yelpService.searchRestaurants("Bearer "+ YELP_API_KEY, "Avocado Toast", "New York")
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Log.i(TAG, "Response: " + response);
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.i(TAG, "Failure: " + t);
                    }
                });
    }
}
