package com.example.weatherapp.update;



import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.weatherapp.R;

public class UpdateUI {
    private String name, country, dateTime, status, icon, temp, humidity, feelsLike, speed;
    private TextView textNameCity, textDateTime, textState, textTemperature, textPercentHumidity, textWindSpeed, textFeelsLike;
    private ImageView imgIconWeather;
    private ConstraintLayout constraintLayout;

    public UpdateUI(String name, String country, String dateTime, String status, String icon, String temp,
                    String humidity, String feelsLike, String speed, TextView textNameCity, TextView textDateTime,
                    TextView textState, TextView textTemperature, TextView textPercentHumidity, TextView textWindSpeed,
                    TextView textFeelsLike, ImageView imgIconWeather,ConstraintLayout constraintLayout) {
        this.name = name;
        this.country = country;
        this.dateTime = dateTime;
        this.status = status;
        this.icon = icon;
        this.temp = temp;
        this.humidity = humidity;
        this.feelsLike = feelsLike;
        this.speed = speed;
        this.textNameCity = textNameCity;
        this.textDateTime = textDateTime;
        this.textState = textState;
        this.textTemperature = textTemperature;
        this.textPercentHumidity = textPercentHumidity;
        this.textWindSpeed = textWindSpeed;
        this.textFeelsLike = textFeelsLike;
        this.imgIconWeather = imgIconWeather;
        this.constraintLayout=constraintLayout;
    }

    public UpdateUI() {

    }

    public String getTemp(){
        return temp;
    }
    public void updateWeather() {
        textNameCity.setText(name + ", " + country);
        textDateTime.setText(dateTime);
        textState.setText(status);
        textTemperature.setText(temp + "°C");
        textPercentHumidity.setText(humidity + "%");
        textWindSpeed.setText(speed + " m/s");
        textFeelsLike.setText("Feels like " + feelsLike + "°C");

        int iconResId = getIconID(icon);
        imgIconWeather.setImageResource(iconResId);
        int background=getBackground(icon);
        Log.d("UpdateUI","Setting background to: "+background);
        constraintLayout.setBackgroundResource(background);
    }

    public static int getIconID(String icon) {
        switch (icon) {
            case "01d": return R.drawable.sunny;
            case "01n": return R.drawable.clear_night;
            case "02d": return R.drawable.cloudy_sunny;
            case "02n": return R.drawable.clear_night;
            case "03d":
            case "03n": return R.drawable.cloudy;
            case "04d":
            case "04n": return R.drawable.cloudy;
            case "09d":
            case "09n": return R.drawable.rainy2;
            case "10d": return R.drawable.rainy2;
            case "10n": return R.drawable.rainy2;
            case "11d":
            case "11n": return R.drawable.storm;
            case "13d":
            case "13n": return R.drawable.snowy;
            case "50d":
            case "50n": return R.drawable.wind;
            default: return R.drawable.wind; // Provide a default image if needed
        }
    }


    public static int getBackground(String icon) {
        switch (icon) {
            case "01d": return R.drawable.sun;
            case "01n": return R.drawable.clearnight;
            case "02d": return R.drawable.cloudysunny;
            case "02n": return R.drawable.clearnight;
            case "03d":
            case "03n": return R.drawable.cloud;
            case "04d":
            case "04n": return R.drawable.cloud;
            case "09d":
            case "09n": return R.drawable.rainy;
            case "10d": return R.drawable.rainy;
            case "10n": return R.drawable.rainy;
            case "11d":
            case "11n": return R.drawable.stormy;
            case "13d":
            case "13n": return R.drawable.snow;
            case "50d":
            case "50n": return R.drawable.winds;
            default: return R.drawable.winds; // Provide a default image if needed
        }
    }

}
