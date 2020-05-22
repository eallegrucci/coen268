package com.example.coen268;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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
    private EditText mSearchText;
    private EditText mLocationText;

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

        initSearch();

        rvRestaurants = findViewById(R.id.rvRestaurants);
        rvRestaurants.setHasFixedSize(true);

        mAdapter = new RestaurantsAdapter(this, yelpRestaurants);
        mLayoutManager = new LinearLayoutManager(this);
        rvRestaurants.setAdapter(mAdapter);
        rvRestaurants.setLayoutManager(mLayoutManager);

        mAdapter.setOnItemClickListener(new RestaurantsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //TODO: store and put all the info to the intent
                String im_url = yelpRestaurants.get(position).image_url;
                String n = yelpRestaurants.get(position).name;
//                String im_url = yelpRestaurants.get(position).image_url;
//                String im_url = yelpRestaurants.get(position).image_url;
//                String im_url = yelpRestaurants.get(position).image_url;
//                String im_url = yelpRestaurants.get(position).image_url;
//                String im_url = yelpRestaurants.get(position).image_url;
//                String im_url = yelpRestaurants.get(position).image_url;
//                String im_url = yelpRestaurants.get(position).image_url;
                Intent i = new Intent(SearchActivity.this, RestaurantActivity.class);
                i.putExtra("im_url", im_url);
                i.putExtra("name", n);
                startActivity(i);
            }
        });
    }

    private void initSearch() {
        mSearchText = (EditText) findViewById(R.id.restaurantSearch);
        mSearchText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mSearchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mLocationText = (EditText) findViewById(R.id.locationSearch);
        mLocationText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mLocationText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    searchRestaurant();
                }
                return false;
            }
        });
        mLocationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    searchRestaurant();
                }
                return false;
            }
        });
    }

    private void searchRestaurant() {
        String searchEntry = mSearchText.getText().toString();
        String location = mLocationText.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = retrofit.create(YelpService.class);

        yelpService.searchRestaurants("Bearer "+ YELP_API_KEY, searchEntry, location)
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
