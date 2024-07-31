package com.example.weatherapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.adapters.CityAdapter;
import com.example.weatherapp.entities.City;
import com.example.weatherapp.entities.WeatherResponse;
import com.example.weatherapp.location.LocationCord;
import com.example.weatherapp.url.ApiClient;
import com.example.weatherapp.url.ApiService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SavedCitiesActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private ImageView  searchButton,backButton;
    private RecyclerView recyclerViewCities;
    private CityAdapter cityAdapter;
    private List<City> cityList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activities_saved_cities);

        editTextSearch = findViewById(R.id.editTextSearch);
        backButton=findViewById(R.id.back_button);
        searchButton = findViewById(R.id.search_button);
        recyclerViewCities = findViewById(R.id.recyclerViewCities);
        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        cityList = loadCitiesFromPreferences();
        cityAdapter = new CityAdapter(cityList, this::onCityClicked);
        recyclerViewCities.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCities.setAdapter(cityAdapter);

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchButton.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String cityName = editTextSearch.getText().toString();
        if (!cityName.isEmpty()) {
            fetchWeatherDetails(cityName);
            editTextSearch.setText("");
        } else {
            Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchWeatherDetails(String cityName) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<WeatherResponse> call = apiService.getWeatherByCityName(cityName, LocationCord.API_KEY);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CitiesRes", String.valueOf(response.body()));
                    WeatherResponse weatherResponse = response.body();


                    // Create a new City object with additional data
                    City city = new City(
                            weatherResponse.getCityName(),
                            String.format("%.1f°C", weatherResponse.getTemperature()), // Convert temperature to Celsius
                            weatherResponse.getDescription(),
                            weatherResponse.getHumidity(),
                            weatherResponse.getIcon(),
                            weatherResponse.getSunrise(),
                            weatherResponse.getSunset()
                    );
                    cityList.add(city);
                    cityAdapter.notifyDataSetChanged();
                    saveCitiesToPreferences();
                } else {
                    Toast.makeText(SavedCitiesActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(SavedCitiesActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCitiesToPreferences() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (City city : cityList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", city.getName());
                jsonObject.put("temperature", city.getTemperature());
                jsonObject.put("description", city.getDescription());
                jsonObject.put("humidity", city.getHumidity());
                jsonObject.put("icon", city.getIconCode());
                jsonObject.put("sunrise", city.getSunrise());
                jsonObject.put("sunset", city.getSunset());
                jsonArray.put(jsonObject);
            }
            editor.putString("savedCities", jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<City> loadCitiesFromPreferences() {
        List<City> cities = new ArrayList<>();
        String savedCities = sharedPreferences.getString("savedCities", null);
        if (savedCities != null) {
            try {
                JSONArray jsonArray = new JSONArray(savedCities);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City(
                            jsonObject.getString("name"),
                            jsonObject.getString("temperature"),
                            jsonObject.getString("description"),
                            jsonObject.getDouble("humidity"),
                            jsonObject.getString("icon"),
                            jsonObject.getString("sunrise"),
                            jsonObject.getString("sunset")
                    );
                    cities.add(city);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cities;
    }

    private String convertUnixTimeToReadableFormat(long unixTime) {
        // Example conversion: You may use libraries such as Joda-Time or Java's DateTime API for better handling
        java.util.Date date = new java.util.Date(unixTime * 1000L); // Convert seconds to milliseconds
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a"); // Customize format as needed
        return sdf.format(date);
    }


    private void onCityClicked(City city) {
        if (city != null) {
            String cityName = city.getName();
            Log.d("SavedCitiesActivity1", "City Name: " + cityName);

            Intent intent = new Intent(SavedCitiesActivity.this, MainActivity.class);
            intent.putExtra("cityName", cityName);
            editor.putString("CityName",cityName);
            editor.apply();
            startActivity(intent);
        } else {
            Log.e("SavedCitiesActivity", "City object is null");
        }
    }
}
