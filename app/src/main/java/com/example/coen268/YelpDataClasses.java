package com.example.coen268;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class YelpSearchResults {
    @SerializedName("total") int total;
    @SerializedName("businesses") List<YelpRestaurant> restaurants;
}

class YelpRestaurant {
    String name;
    String display_phone;
    double rating;
    String price;
    int review_count;
    YelpLocation location;
    double distance;
    String image_url;
    List<YelpCategory> categories;
//    @SerializedName("hours") List<Hours> hours;

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

//class Hours  {
//    List<Time> open;
//    String hours_type;
//    boolean is_open_now;
//
//    @Override
//    public String toString() {
//        String hours = "Hours: \n";
//
//        hours += "Monday " + open.get(0).start + "-" + open.get(0).end + "\n";
//        hours += "Tuesday " + open.get(1).start + "-" + open.get(1).end + "\n";
//        hours += "Wednesday " + open.get(2).start + "-" + open.get(2).end + "\n";
//        hours += "Thursday " + open.get(3).start + "-" + open.get(3).end + "\n";
//        hours += "Friday " + open.get(4).start + "-" + open.get(4).end + "\n";
//        hours += "Saturday " + open.get(5).start + "-" + open.get(5).end + "\n";
//        hours += "Sunday " + open.get(6).start + "-" + open.get(6).end;
//
//        return hours;
//    }
//}
//
//class Time {
//    boolean is_overnight;
//    String start;
//    String end;
//    int day;
//}