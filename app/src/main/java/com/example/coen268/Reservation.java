package com.example.coen268;

import java.util.ArrayList;
import java.util.List;

public class Reservation {
    String business_id;
    List<String> user_ids;

    public Reservation(){

    }
    public Reservation(String business_id, List<String> user_ids){
        this.business_id = business_id;
        this.user_ids = user_ids;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public List<String> getUser_ids() {
        return user_ids;
    }
}
