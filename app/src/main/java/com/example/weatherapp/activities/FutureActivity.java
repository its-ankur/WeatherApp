package com.example.weatherapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.R;
import com.example.weatherapp.adapters.FutureAdapter;
import com.example.weatherapp.entities.FutureDomain;
import com.example.weatherapp.update.UpdateUI;
import com.example.weatherapp.url.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

public class FutureActivity extends AppCompatActivity {
    private ArrayList<FutureDomain> items;
    private FutureAdapter futureAdapter;
    private RecyclerView recyclerViewFuture;

    private TextView textTemperatureToday, textWeatherToday;
    private TextView textFeels, textWind, textHumidity;
    private ImageView imgIcon, imgBack;

    private String nameCity = "Kharar";

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_future);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setMapping();
        recyclerViewFuture.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        items = new ArrayList<>();
        futureAdapter = new FutureAdapter(items);
        recyclerViewFuture.setAdapter(futureAdapter);

        imgBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        textTemperatureToday.setText(intent.getStringExtra("temperature"));
        textWeatherToday.setText(intent.getStringExtra("state"));
        textFeels.setText(intent.getStringExtra("feelsLike"));
        textWind.setText(intent.getStringExtra("windSpeed"));
        textHumidity.setText(intent.getStringExtra("humidity"));
        String iconImg = intent.getStringExtra("imgIconWeather");

        if (iconImg != null) {
            imgIcon.setImageResource(getResources().getIdentifier(String.valueOf(UpdateUI.getIconID(iconImg)), "drawable", getPackageName()));
        }
        Log.d("result", "Du lieu qua: " + city);
        nameCity = city;
        get5DaysData(nameCity);
    }

    private void setMapping() {
        textTemperatureToday = findViewById(R.id.textTemperatureToday);
        textWeatherToday = findViewById(R.id.textWeatherToday);
        textFeels = findViewById(R.id.textFeels);
        textWind = findViewById(R.id.textWind);
        textHumidity = findViewById(R.id.textHumidity);
        imgIcon = findViewById(R.id.imgIcon);
        recyclerViewFuture = findViewById(R.id.recyclerViewFuture);
        imgBack = findViewById(R.id.imgback);
    }

    private void get5DaysData(String city) {
        URL url = new URL();
        url.setLink(city);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getLink(),
                response -> {
                    try {
                        items.clear();

                        // Use a TreeMap to automatically sort the entries by date
                        TreeMap<String, FutureDomain> dailyForecasts = new TreeMap<>();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                            String day = jsonObjectList.getString("dt");

                            long dt = Long.parseLong(day);
                            Date date = new Date(dt * 1000L);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
                            String dateTime = simpleDateFormat.format(date);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            String dateOnly = dateFormat.format(date);

                            JSONObject jsonObjectMain = jsonObjectList.getJSONObject("main");
                            String maxTemp = jsonObjectMain.getString("temp_max");
                            String minTemp = jsonObjectMain.getString("temp_min");

                            double a = Double.parseDouble(maxTemp);
                            double b = Double.parseDouble(minTemp);
                            int max = (int) a;
                            int min = (int) b;

                            JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String status = jsonObjectWeather.getString("description");
                            String icon = jsonObjectWeather.getString("icon");

                            // Only store the first forecast of each day
                            if (!dailyForecasts.containsKey(dateOnly)) {
                                dailyForecasts.put(dateOnly, new FutureDomain(dateTime, icon, status, max, min));
                            }
                        }
                        items.addAll(dailyForecasts.values());
                        futureAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("result", "JSON parsing error: " + error.getMessage()));
        requestQueue.add(stringRequest);
    }
}