package com.example.weatherapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.entities.City;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private final List<City> cityList;
    private final OnCityClickListener onCityClickListener;

    public CityAdapter(List<City> cityList, OnCityClickListener onCityClickListener) {
        this.cityList = cityList;
        this.onCityClickListener = onCityClickListener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.bind(city);
        holder.itemView.setOnClickListener(v -> onCityClickListener.onCityClicked(city));
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public interface OnCityClickListener {
        void onCityClicked(City city);
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final TextView temperatureTextView;
        private final TextView descriptionTextView;
        private final TextView humidityTextView;
        private final ImageView imageIconWeather;
        private final TextView sunrise,sunset;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            humidityTextView = itemView.findViewById(R.id.humidityTextView);
            imageIconWeather = itemView.findViewById(R.id.weatherIconImageView);
            sunrise=itemView.findViewById(R.id.sunrise);
            sunset=itemView.findViewById(R.id.sunset);
        }

        public void bind(City city) {
            nameTextView.setText(city.getName());
            temperatureTextView.setText(city.getTemperature());
            descriptionTextView.setText(city.getDescription());
            humidityTextView.setText(String.format("Humidity: %.1f%%", city.getHumidity()));
            // Example: Set weather icon based on weather condition
            int iconResId = getWeatherIconResId(city.getIconCode());// Implement this method to map icon names to drawable resources
            Log.d("CityIcon", String.valueOf(iconResId));
            imageIconWeather.setImageResource(iconResId);
            sunrise.setText(city.getSunrise());
            sunset.setText(city.getSunset());
        }

        // Example method to get drawable resource ID from weather icon name
        public static int getWeatherIconResId(String icon) {
            switch (icon) {
                case "01d": return R.drawable.sun;
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
    }
}
