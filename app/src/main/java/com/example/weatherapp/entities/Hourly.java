package com.example.weatherapp.entities;

public class Hourly {
    private String dateTime;
    private String status;
    private String temp;
    private String humidity;
    private String icon;

    // Constructor to initialize all fields
    public Hourly(String dateTime, String status, String temp, String humidity, String icon) {
        this.dateTime = dateTime;
        this.status = status;
        this.temp = temp;
        this.humidity = humidity;
        this.icon = icon;
    }

    // Getter and Setter methods

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    // Optionally, you can add a toString() method for easy logging and debugging
    @Override
    public String toString() {
        return "Hourly{" +
                "dateTime='" + dateTime + '\'' +
                ", status='" + status + '\'' +
                ", temp='" + temp + '\'' +
                ", humidity='" + humidity + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
