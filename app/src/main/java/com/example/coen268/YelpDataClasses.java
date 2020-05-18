package com.example.coen268;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class YelpSearchResults {
    @SerializedName("total") int total;
    @SerializedName("businesses") List<YelpRestaurant> restaurants;
}

class YelpRestaurant {
    String name;
    double rating;
    String price;
    int review_count;
    YelpLocation location;
    double distance;
    String image_url;
    List<YelpCategory> categories;

    String distanceInMiles() {
        double milesPerMeter = .000621371;
        String miles = String.format("%.2f", distance * milesPerMeter);
        return miles + " mi";
    }
}

class YelpLocation {
    String city;
    String country;
    String state;
    @SerializedName("address1") String address;
    String zip_code;
}

class YelpCategory {
    String alias;
    String title;
}