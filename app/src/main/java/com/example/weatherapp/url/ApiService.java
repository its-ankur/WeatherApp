package com.example.weatherapp.url;


import com.example.weatherapp.entities.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("weather")
    Call<WeatherResponse> getWeatherByCityName(
            @Query("q") String cityName,
            @Query("appid") String apiKey
    );
}
