package com.example.coen268;

import java.util.ArrayList;
import java.util.List;

public class Reservation {
    String business_id;
    List<String> user_ids;
    Number quota;

    public Reservation(){

    }
    public Reservation(String business_id, List<String> user_ids, Number quota){
        this.business_id = business_id;
        this.user_ids = user_ids;
        this.quota = quota;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public List<String> getUser_ids() {
        return user_ids;
    }

    public Number getQuota(){return quota;}
}
