package com.example.weatherapp.url;

import android.util.Log;

import com.example.weatherapp.location.LocationCord;

public class URL {
    private String linkDay;
    private String link;
    private String link5Days;

    public URL() {
    }

    // Method for setting link with city name
    public void setLinkDay(String city) {
        linkDay = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + LocationCord.API_KEY;
    }

    // Overloaded method for setting link with latitude and longitude
    public void setLinkDay(double latitude, double longitude) {
        linkDay = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + LocationCord.API_KEY;
    }

    public String getLinkDay() {
        return linkDay;
    }

    // Method for setting link with city name
    public void setLink(String city) {
        link = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + LocationCord.API_KEY + "&units=metric";
    }

    // Overloaded method for setting link with latitude and longitude
    public void setLink(double latitude, double longitude) {
        link = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + LocationCord.API_KEY + "&units=metric";
    }

    public String getLink() {
        return link;
    }

    public void setLink5Days(String city) {
        this.link5Days = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + LocationCord.API_KEY + "&units=metric";
        Log.d("URLDebug", "Constructed URL: " + this.link5Days);
    }


    public String getLink5Days() {
        return link5Days;
    }


}
