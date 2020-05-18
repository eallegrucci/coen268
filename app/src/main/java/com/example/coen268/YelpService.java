package com.example.coen268;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {

    @GET("businesses/search")
    Call<Object> searchRestaurants(
            @Header("Authorization") String authHeader,
            @Query("term") String  searchTerm,
            @Query("location") String  location);
}
