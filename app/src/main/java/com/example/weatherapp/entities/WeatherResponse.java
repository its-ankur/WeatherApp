package com.example.weatherapp.entities;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weatherList;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("name")
    private String name;

    @SerializedName("sys")
    private Sys sys;


    public double getTemperature() {
        // Convert temperature from Kelvin to Celsius
        return main != null ? main.getTemp() - 273.15 : 0;
    }

    public String getDescription() {
        if (weatherList != null && !weatherList.isEmpty()) {
            return weatherList.get(0).getDescription();
        }
        return "";
    }

    public String getIcon(){
        if(weatherList!=null && !weatherList.isEmpty()){
            return weatherList.get(0).getIcon();
        }
        return "";
    }


    public double getHumidity() {
        return main != null ? main.getHumidity() : 0;
    }

    public double getPressure() {
        return main != null ? main.getPressure() : 0;
    }

    public double getWindSpeed() {
        return wind != null ? wind.getSpeed() : 0;
    }

    public int getCloudCoverage() {
        return clouds != null ? clouds.getAll() : 0;
    }

    public String getCityName() {
        return name != null ? name : "";
    }

    public String getSunrise() {
        return sys != null ? convertUnixTimeToReadableFormat(sys.getSunrise()) : "";
    }

    public String getSunset() {
        return sys != null ? convertUnixTimeToReadableFormat(sys.getSunset()) : "";
    }

    private String convertUnixTimeToReadableFormat(long unixTime) {
        java.util.Date date = new java.util.Date(unixTime * 1000L); // Convert seconds to milliseconds
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a"); // Customize format as needed
        return sdf.format(date);
    }


    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("humidity")
        private double humidity;

        @SerializedName("pressure")
        private double pressure;

        public double getTemp() {
            return temp;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getPressure() {
            return pressure;
        }
    }

    public static class Weather {
        @SerializedName("description")
        private String description;
        private String icon;

        public String getDescription() {
            return description;
        }
        public String getIcon(){return icon;}
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;

        public int getAll() {
            return all;
        }
    }

    public static class Sys {
        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

        public long getSunrise() {
            return sunrise;
        }

        public long getSunset() {
            return sunset;
        }
    }

}
