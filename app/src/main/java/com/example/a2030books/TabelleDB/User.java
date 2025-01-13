package com.example.a2030books.TabelleDB;

import android.util.Pair;

public class User {

    private String email;
    private String nickname;
    private Pair<Double, Double> place; // first = latitude, second = longitude
    private Pair<String, String> day_hour;

    public User() {}

    public User(String email, String nickname, Pair<Double, Double> place, Pair<String, String> day_hour, String tel) {
        this.email = email;
        this.nickname = nickname;
        this.place = place;
        this.day_hour = day_hour;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public double getLatitude(){
        return place.first;
    }

    public void setLatitude(double latitude) {
        if(place != null)
            place = new Pair<>(latitude, place.second);
        else
            place = new Pair<>(latitude, 0.0d);
    }

    public double getLongitude(){
        return place.second;
    }

    public void setLongitude(double longitude){
        if(place != null)
            place = new Pair<>(place.first, longitude);
        else
            place = new Pair<>(0.0d, longitude);

    }

    public String getDay(){
        return day_hour.first;
    }

    public void setDay(String day){
        day_hour = new Pair<>(day, day_hour.second);
    }

    public String getHour(){
        return day_hour.second;
    }

    public void setHour(String hour){
        day_hour = new Pair<>(day_hour.first, hour);
    }
}
