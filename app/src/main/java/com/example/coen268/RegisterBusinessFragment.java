package com.example.coen268;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.example.coen268.user.BusinessOwner;
import com.example.coen268.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterBusinessFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = RegisterBusinessFragment.class.getSimpleName();

    private RecyclerView rvBusinesses;
    private RestaurantsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<YelpRestaurant> yelpRestaurants;

    private static final String BASE_URL = "https://api.yelp.com/v3/";

    private User.OnCreateAccountListener listener;
    private User user;

    private Button createButton;

    private TextInputLayout searchNameTextField;
    private TextInputLayout searchLocationTextField;
    private TextView textView;

    private String businessId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(Constants.KEY_USER);
        Log.i(TAG, user.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_business, container, false);
        createButton = view.findViewById(R.id.create);
        createButton.setOnClickListener(this);
        searchNameTextField = view.findViewById(R.id.search_name);
        searchNameTextField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN) ||
                        (event != null && event.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    searchRestaurant();
                }
                return false;
            }
        });

        searchLocationTextField = view.findViewById(R.id.search_location);
        searchLocationTextField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN) ||
                        (event != null && event.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    searchRestaurant();
                }
                return false;
            }
        });

        textView = view.findViewById(R.id.selection);

        yelpRestaurants = new ArrayList<>();

        rvBusinesses = view.findViewById(R.id.rvBusinesses);
        rvBusinesses.setHasFixedSize(true);

        mAdapter = new RestaurantsAdapter(getContext(), yelpRestaurants);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvBusinesses.setAdapter(mAdapter);
        rvBusinesses.setLayoutManager(mLayoutManager);

        mAdapter.setOnItemClickListener(new RestaurantsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                getBusinessId(position);
            }
        });

        return view;
    }

    private void searchRestaurant() {
        String searchName = searchNameTextField.getEditText().getText().toString();
        String searchLocation = searchLocationTextField.getEditText().getText().toString();

        if (searchName.isEmpty()) {
            searchNameTextField.setError("Required.");
            return;
        } else {
            searchNameTextField.setError(null);
        }

        if (searchLocation.isEmpty()) {
            searchLocationTextField.setError("Required.");
            return;
        } else {
            searchLocationTextField.setError(null);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = retrofit.create(YelpService.class);

        yelpService.searchRestaurants("Bearer "+ getString(R.string.yelp_api_key), searchName, searchLocation)
                .enqueue(new Callback<YelpSearchResults>() {
                    @Override
                    public void onResponse(Call<YelpSearchResults> call, Response<YelpSearchResults> response) {
                        Log.i(TAG, "Response: " + response);
                        YelpSearchResults body = response.body();
                        if (body == null) {
                            Log.w(TAG, "Did not receive valid response from Yelp API... exiting");
                            return;
                        }
                        yelpRestaurants.clear();
                        yelpRestaurants.addAll(body.restaurants);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<YelpSearchResults> call, Throwable t) {
                        Log.i(TAG, "Failure: " + t);
                    }
                });
    }

    private void getBusinessId(int position) {
        businessId = yelpRestaurants.get(position).id;
        textView.setText("Selected: " + yelpRestaurants.get(position).name);
    }

    @Override
    public void onClick(View v) {
        if (businessId.isEmpty()) {
            textView.setText("Please register a business.");
            return;
        }

        listener.createAccount(new BusinessOwner.BusinessOwnerBuilder(user.getAccountType())
                .setDisplayName(user.getDisplayName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setBusinessId(businessId)
                .build()
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof User.OnCreateAccountListener) {
            listener = (User.OnCreateAccountListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCreateAccountListener");
        }
    }
}
