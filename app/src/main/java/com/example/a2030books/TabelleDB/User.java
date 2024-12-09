package com.example.a2030books.TabelleDB;

import android.util.Pair;

public class User {

    private String email;
    private String nickname;
    private Pair<Double, Double> place;
    private Pair<String, String> day_hour;

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public double getLatitude(){
        return place.first;
    }

    public double getLongitude(){
        return place.second;
    }

    public String getDay(){
        return day_hour.first;
    }

    public String getHour(){
        return day_hour.second;
    }


}
