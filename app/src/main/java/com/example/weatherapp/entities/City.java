package com.example.weatherapp.entities;

import android.widget.ImageView;

public class City {
    private String name;
    private String temperature;
    private String description;
    private double humidity;
    private String iconCode;
    private String weatherIconUrl;
    private ImageView imageIconWeather;
    private String sunrise;
    private String sunset;

    public City(String name, String temperature, String description, double humidity,String iconCode,String sunrise,String sunset) {
        this.name = name;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.iconCode=iconCode;
        this.sunrise=sunrise;
        this.sunset=sunset;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getIconCode(){
        return iconCode;
    }

    public void setIconCode(String iconCode){
        this.iconCode=iconCode;
    }

    public String getWeatherIconUrl(){
        return weatherIconUrl;
    }

    public void setWeatherIconUrl(String weatherIconUrl){
        this.weatherIconUrl=weatherIconUrl;
    }

    public String getSunrise() { return sunrise; }
    public String getSunset() { return sunset; }
}
