package com.example.coen268;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends Fragment implements LocationListener {
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

    // For user location
    private LocationManager locationManager;
    private double longitude, latitude;
    private Location loc;
    private boolean mLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);

        yelpRestaurants = new ArrayList<>();

        initSearch(view);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        rvRestaurants.setHasFixedSize(true);

        mAdapter = new RestaurantsAdapter(getContext(), yelpRestaurants);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvRestaurants.setAdapter(mAdapter);
        rvRestaurants.setLayoutManager(mLayoutManager);

        mAdapter.setOnItemClickListener(new RestaurantsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(getContext(), RestaurantActivity.class);
                putInfo(position, i);
                startActivity(i);
            }
        });


        return view;
    }

    private void putInfo(int position, Intent i) {
        String image = yelpRestaurants.get(position).image_url;
        String n = yelpRestaurants.get(position).name;
        String phone = yelpRestaurants.get(position).display_phone;
        String mPrice = yelpRestaurants.get(position).price;
        String dist = yelpRestaurants.get(position).distanceInMiles();
        double rat = yelpRestaurants.get(position).rating;
        double revCount = yelpRestaurants.get(position).review_count;
        String street = yelpRestaurants.get(position).location.address;
        String address = yelpRestaurants.get(position).location.city + ", " +
                yelpRestaurants.get(position).location.state + " " +
                yelpRestaurants.get(position).location.zip_code;
        i.putExtra("image", image);
        i.putExtra("name", n);
        i.putExtra("phone", phone);
        i.putExtra("price", mPrice);
        i.putExtra("distance", dist);
        i.putExtra("rating", rat);
        i.putExtra("reviewCount", revCount);
        i.putExtra("street", street);
        i.putExtra("address", address);
    }

    private void initSearch(View v) {
        mSearchText = (EditText) v.findViewById(R.id.businessSearch);
        mSearchText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mSearchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mLocationText = (EditText) v.findViewById(R.id.locationSearch);
        mLocationText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mLocationText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    searchBusiness();
                }
                return false;
            }
        });
        mLocationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    searchBusiness();
                }
                return false;
            }
        });
    }

    private void searchBusiness() {
        String searchEntry = mSearchText.getText().toString();
        String location = mLocationText.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = retrofit.create(YelpService.class);

        if (location.isEmpty()) {
            getLocationPermission();
            if (mLocationPermissionsGranted) {
                onLocationChanged(loc);
                location = locationToCity();
                yelpSearch(yelpService, searchEntry, location);
                Toast.makeText(getContext(), "Using your current location for the search",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please input a location into the search field",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (searchEntry.isEmpty()) {
            Log.i("searchBusiness", "search entry is null");
            Toast.makeText(getContext(), "Please input a business into the search field",
                    Toast.LENGTH_SHORT).show();
        } else {
            yelpSearch(yelpService, searchEntry, location);
        }
    }

    private void yelpSearch(YelpService yelpService, String searchEntry, String location){
        yelpService.searchBusinesses("Bearer " + YELP_API_KEY, searchEntry, location)
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

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getContext(), COURSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                loc = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private String locationToCity(){
        String addr = "";
        try {
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> address = null;
            address = geocoder.getFromLocation(latitude, longitude, 1);
            addr = address.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addr;
    }
}
