package com.example.weatherapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.weatherapp.adapters.HourlyAdapter;
import com.example.weatherapp.entities.FutureDomain;
import com.example.weatherapp.entities.Hourly;
import com.example.weatherapp.update.UpdateUI;
import com.example.weatherapp.url.URL;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private ArrayList<FutureDomain> itemsFuture;
    private FutureAdapter futureAdapter;
    private RecyclerView recyclerViewFuture;

    private ArrayList<Hourly> itemsHourly;
    private HourlyAdapter hourlyAdapter;
    private RecyclerView recyclerViewHourly;

    private TextView textNameCity, textNext5Days, textDateTime, textState, textTemperature;
    private TextView textPercentHumidity, textWindSpeed, textFeelsLike;
    private ImageView imgIconWeather, menuButton;
    private EditText editTextSearch;

    private long pressBackTime;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_FINE_LOCATION_PERMISSION = 2;
    private boolean flag=true;
    final int drawablePadding = 40;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String nameCity="";
    private boolean isCelsius=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setMapping();
        setupRecyclerView();

        sharedPreferences=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        nameCity=sharedPreferences.getString("CityName","");
        editTextSearch.setText(nameCity);
        if(!nameCity.isEmpty()){
            performSearch();
        }
        // Handle city name from Intent
        Intent intent = getIntent();
        if (intent.hasExtra("cityName")) {
            String cityName = intent.getStringExtra("cityName");
            Log.d("CityName",cityName);
            if (cityName != null && !cityName.isEmpty()) {
                Log.d("CityName","Inside if");
                nameCity = cityName;  // Update nameCity
                textNameCity.setText(nameCity);
                textNameCity.setVisibility(View.VISIBLE);
                getCurrentWeatherData(nameCity);
                getHourlyData(nameCity);
                get5DaysData(nameCity);
            }
        } else {
            // Default city
            nameCity="";
            textNameCity.setText(nameCity);
            textNameCity.setVisibility(View.VISIBLE);
            getCurrentWeatherData(nameCity);
            getHourlyData(nameCity);
            get5DaysData(nameCity);
        }




        editTextSearch.setOnTouchListener((v, event) -> {
            Log.d("TouchEvent", "Action: " + event.getAction() + ", X: " + event.getRawX());
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = editTextSearch.getCompoundDrawablesRelative()[2];
                if (drawableEnd != null && event.getRawX() >= (editTextSearch.getRight() - drawableEnd.getBounds().width()-drawablePadding)) {
                    Log.d("TouchEvent", "Drawable clicked");
                    performSearch();
                    return true;
                }
            }
            return false;
        });

        editTextSearch.post(() -> {
            Drawable drawableEnd = editTextSearch.getCompoundDrawablesRelative()[2];
            if (drawableEnd != null) {
                Log.d("Drawable", "Drawable bounds: " + drawableEnd.getBounds().toString());
            } else {
                Log.d("Drawable", "Drawable is null");
            }
        });


        requestLocationPermissions();

        menuButton.setOnClickListener(v -> {
            Intent intentToNextPage=new Intent(MainActivity.this,SavedCitiesActivity.class);
            startActivity(intentToNextPage);
        });


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        editor.clear();
        editor.apply();
        Log.d("CityName","value destroyed "+nameCity);
    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            if (isNetworkAvailable()) {
                nameCity = query;
                textNameCity.setText(nameCity);
                textNameCity.setVisibility(View.VISIBLE);
                getCurrentWeatherData(nameCity);
                getHourlyData(nameCity);
                get5DaysData(nameCity);
                Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                editTextSearch.setText("");
            } else {
                Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    private void setupRecyclerView() {
        recyclerViewHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemsHourly = new ArrayList<>();
        hourlyAdapter = new HourlyAdapter(itemsHourly);
        recyclerViewHourly.setAdapter(hourlyAdapter);

        recyclerViewFuture.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        itemsFuture = new ArrayList<>();
        futureAdapter = new FutureAdapter(itemsFuture);
        recyclerViewFuture.setAdapter(futureAdapter);
        recyclerViewFuture.setLayoutManager(new LinearLayoutManager(this));
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            Log.d("CityName","namecity inside request location permission"+nameCity);
            if(nameCity.equals("")) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(!nameCity.isEmpty()){
            return;
        }
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            boolean fineLocationGranted = false;
            boolean coarseLocationGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    fineLocationGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[i])) {
                    coarseLocationGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }

            if (fineLocationGranted) {

                getCurrentLocation();
            } else if (coarseLocationGranted) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_FINE_LOCATION_PERMISSION);
            } else {
                Toast.makeText(this, "Location permissions are required for this app", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_FINE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Fine location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d("CityName","Get current location called");
                Location location = task.getResult();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                getCurrentWeatherData(latitude, longitude);
                getHourlyData(latitude, longitude);
                get5DaysData(latitude,longitude);
            } else {
                Toast.makeText(MainActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMapping() {
        recyclerViewHourly = findViewById(R.id.recyclerViewHourly);
        recyclerViewFuture = findViewById(R.id.recyclerViewFuture);
        textDateTime = findViewById(R.id.textDateTime);
        editTextSearch = findViewById(R.id.editTextSearch);
        textState = findViewById(R.id.textState);
        textNameCity = findViewById(R.id.textNameCity);
        textTemperature = findViewById(R.id.textTemperature);
        imgIconWeather = findViewById(R.id.imgIconWeather);
        textPercentHumidity = findViewById(R.id.textPercentHumidity);
        textWindSpeed = findViewById(R.id.textWindSpeed);
        textFeelsLike = findViewById(R.id.textFeelsLike);
        menuButton=findViewById(R.id.menu_button);
    }



    private void getCurrentWeatherData(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        URL url = new URL();
        url.setLinkDay(city);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getLinkDay(),
                response -> {
                    try {
                        Log.d("CityName",response);
                        JSONObject jsonObject = new JSONObject(response);
                        parseWeatherResponse(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("Error", "Error: " + error.getMessage())
        );
        requestQueue.add(stringRequest);
    }

    private void getHourlyData(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        URL url = new URL();
        url.setLink(city);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getLink(),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        parseHourlyData(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("Error", "Error: " + error.getMessage())
        );
        requestQueue.add(stringRequest);
    }

    private void getHourlyData(double latitude, double longitude) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        URL url = new URL();
        url.setLink(latitude, longitude);  // Update this method to build the URL using latitude and longitude
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getLink(),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        parseHourlyData(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("Error", "Error: " + error.getMessage())
        );
        requestQueue.add(stringRequest);
    }

    private void getCurrentWeatherData(double latitude, double longitude) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        URL url = new URL();
        url.setLinkDay(latitude, longitude);  // Update this method to build the URL using latitude and longitude
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getLinkDay(),
                response -> {
                    try {
                        Log.d("CityName",response);
                        JSONObject jsonObject = new JSONObject(response);
                        parseWeatherResponse(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("Error", "Error: " + error.getMessage())
        );
        requestQueue.add(stringRequest);
    }


    private void get5DaysData(String city) {
        URL url = new URL();
        url.setLink(city);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getLink(),
                response -> {
                    try {
                        itemsFuture.clear();

                        // Use a TreeMap to automatically sort the entries by date
                        TreeMap<String, FutureDomain> dailyForecasts = new TreeMap<>();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        HashMap<String, ArrayList<FutureDomain>> dailyData = new HashMap<>();
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

                            FutureDomain futureDomain=new FutureDomain(dateTime,icon,status,max,min);

                            // Only store the first forecast of each day
                            if (!dailyData.containsKey(dateOnly)) {
                                dailyData.put(dateOnly,new ArrayList<>());
                            }
                            dailyData.get(dateOnly).add(futureDomain);
                        }


                        // Calculate daily max and min temperatures and store in dailyForecasts
                        for (Map.Entry<String, ArrayList<FutureDomain>> entry : dailyData.entrySet()) {
                            String date = entry.getKey();
                            ArrayList<FutureDomain> dailyEntries = entry.getValue();

                            int dailyMax = Integer.MIN_VALUE;
                            int dailyMin = Integer.MAX_VALUE;

                            for (FutureDomain entryData : dailyEntries) {
                                if (entryData.getHighTemp() > dailyMax) {
                                    dailyMax = entryData.getHighTemp();
                                }
                                if (entryData.getLowTemp() < dailyMin) {
                                    dailyMin = entryData.getLowTemp();
                                }
                            }

                            FutureDomain dailyForecast = new FutureDomain(dailyEntries.get(0).getDay(), dailyEntries.get(0).getPicPath(), dailyEntries.get(0).getStatus(), dailyMax, dailyMin);
                            dailyForecasts.put(date, dailyForecast);
                        }

                        itemsFuture.addAll(dailyForecasts.values());
                        futureAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("result", "JSON parsing error: " + error.getMessage()));
        requestQueue.add(stringRequest);
    }


    private void get5DaysData(Double latitude,double longitude) {
        URL url = new URL();
        url.setLink(latitude, longitude);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getLink(),
                response -> {
                    try {
                        itemsFuture.clear();

                        // Use a TreeMap to automatically sort the entries by date
                        TreeMap<String, FutureDomain> dailyForecasts = new TreeMap<>();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        HashMap<String, ArrayList<FutureDomain>> dailyData = new HashMap<>();
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

                            FutureDomain futureDomain=new FutureDomain(dateTime,icon,status,max,min);

                            // Only store the first forecast of each day
                            if (!dailyData.containsKey(dateOnly)) {
                                dailyData.put(dateOnly,new ArrayList<>());
                            }
                            dailyData.get(dateOnly).add(futureDomain);
                        }


                        // Calculate daily max and min temperatures and store in dailyForecasts
                        for (Map.Entry<String, ArrayList<FutureDomain>> entry : dailyData.entrySet()) {
                            String date = entry.getKey();
                            ArrayList<FutureDomain> dailyEntries = entry.getValue();

                            int dailyMax = Integer.MIN_VALUE;
                            int dailyMin = Integer.MAX_VALUE;

                            for (FutureDomain entryData : dailyEntries) {
                                if (entryData.getHighTemp() > dailyMax) {
                                    dailyMax = entryData.getHighTemp();
                                }
                                if (entryData.getLowTemp() < dailyMin) {
                                    dailyMin = entryData.getLowTemp();
                                }
                            }

                            FutureDomain dailyForecast = new FutureDomain(dailyEntries.get(0).getDay(), dailyEntries.get(0).getPicPath(), dailyEntries.get(0).getStatus(), dailyMax, dailyMin);
                            dailyForecasts.put(date, dailyForecast);
                        }

                        itemsFuture.addAll(dailyForecasts.values());
                        futureAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("result", "JSON parsing error: " + error.getMessage()));
        requestQueue.add(stringRequest);
    }

    private void parseWeatherResponse(JSONObject jsonObject) {
        try {
            // Extract city name and country name from the JSON response
            String cityName = jsonObject.getString("name"); // Adjust if needed
            JSONObject sys = jsonObject.getJSONObject("sys");
            String countryName = sys.getString("country"); // Adjust if needed

            // Extract other weather data
            JSONObject main = jsonObject.getJSONObject("main");
            String temp = main.getString("temp");
            String feelsLike = main.getString("feels_like");
            String humidity = main.getString("humidity");

            JSONObject wind = jsonObject.getJSONObject("wind");
            String speed = wind.getString("speed");

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String status = weather.getString("description");
            String icon = weather.getString("icon");

            long dt = jsonObject.getLong("dt");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
            String dateTime = sdf.format(new Date(dt * 1000L));

            // Update the UI with the fetched data
            UpdateUI updateUI = new UpdateUI(
                    cityName,
                    countryName,
                    dateTime,
                    status,
                    icon,
                    temp,
                    humidity,
                    feelsLike,
                    speed,
                    textNameCity,
                    textDateTime,
                    textState,
                    textTemperature,
                    textPercentHumidity,
                    textWindSpeed,
                    textFeelsLike,
                    imgIconWeather
            );

            updateUI.updateWeather();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseHourlyData(JSONObject jsonObject) {
        try {
            JSONArray hourlyArray = jsonObject.getJSONArray("list"); // Adjust according to your API response

            itemsHourly.clear(); // Clear previous data if needed

            // Get today's date in the same format as API response
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            for (int i = 0; i < hourlyArray.length(); i++) {
                JSONObject hourData = hourlyArray.getJSONObject(i);
                String dateTime = hourData.getString("dt_txt"); // Adjust according to your API response

                // Extract the date part from datetime
                String datePart = dateTime.split(" ")[0]; // Assumes "yyyy-MM-dd HH:mm:ss"

                if (datePart.equals(todayDate)) {
                    JSONObject main = hourData.getJSONObject("main");
                    String temp = main.getString("temp");
                    String humidity = main.getString("humidity");

                    JSONArray weatherArray = hourData.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String status = weather.getString("description");
                    String icon = weather.getString("icon");

                    Hourly hourly = new Hourly(dateTime, status, temp, humidity, icon);
                    itemsHourly.add(hourly);
                }
            }

            hourlyAdapter.notifyDataSetChanged(); // Notify adapter of data changes

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (pressBackTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressBackTime = System.currentTimeMillis();
    }
}
