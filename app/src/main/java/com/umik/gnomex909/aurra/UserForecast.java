package com.umik.gnomex909.aurra;

import android.util.Log;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;


/**
 * Created by Gnomex on 27.11.2017.
 * Klasa służaca do przechowywania gotowych prognóz pogody dla wyjścia użytkownika.
 * tutaj jest również kod w jaki sposób wyliczana jest pogoda na bazie godizny startu, wyjścia i listy Weateher Forecast
 */

public class UserForecast extends Forecast{
    private static final String TAG = "UserForecast";
    private String startDate;
    private double minTemperature = 100;
    private double maxTemperature = -255;
    private double minPressure = 1100;
    private double maxPressure = 900;
    private boolean goodPressure;
    public UserForecast() {
    }

    public UserForecast(String startTime, String endTime, String startDate) {
        this.startDate = startDate;
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        Log.d(TAG, "UserForecast: starts with " + startDate + "and" + this.getEndTime() + "and " +this.getStartTime());
    }

    public void setUserForecast(List<WeatherForecast> mWeatherList){
        String[] split = this.getStartTime().split(":");
        int userStartHour = Integer.parseInt(split[0]);
        split = this.getEndTime().split(":");
        int userEndHour = Integer.parseInt(split[0]);
        for(WeatherForecast wf : mWeatherList){
            split = wf.getEndTime().split(":");
           // Log.d(TAG, "setUserForecast: " +split[0]);
            int forecastTime = Integer.parseInt(split[0]);

            if(forecastTime == 0){
                forecastTime = 24;
            }

            if(userStartHour <= forecastTime && this.getStartDate().equals(wf.getDate())){
             //   Log.d(TAG, "setUserForecast: Got to second check");
                split = wf.getStartTime().split(":");
                forecastTime = Integer.parseInt(split[0]);
               // Log.d(TAG, "setUserForecast: "+userEndHour + ":" + forecastTime);
                if(userEndHour > forecastTime){
                    Log.d(TAG, "setUserForecast: " + wf.toString());
                    this.setMinTemperature(min(this.getMinTemperature(),wf.getTemperature()));
                    this.setMaxTemperature(max(this.getMaxTemperature(),wf.getTemperature()));
                    this.setMinPressure(min(this.getMinPressure(),wf.getPressure()));
                    this.setMaxPressure(max(this.getMaxPressure(),wf.getPressure()));
                    this.setWindSpeed(max(this.getWindSpeed(),wf.getWindSpeed()));
                    this.setWeatherType(max(this.getWeatherType(),wf.getWeatherType()));
                }
            }
        }
        if((this.maxPressure - this.minPressure)<8)
            setGoodPressure(true);
        else
            setGoodPressure(false);
        Log.d(TAG, "setUserForecast: " + this.toString());

    }
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }


    public double getMinPressure() {
        return minPressure;
    }

    public void setMinPressure(double minPressure) {
        this.minPressure = minPressure;
    }

    public double getMaxPressure() {
        return maxPressure;
    }

    public void setMaxPressure(double maxPressure) {
        this.maxPressure = maxPressure;
    }

    public boolean isGoodPressure() {
        return goodPressure;
    }

    public void setGoodPressure(boolean goodPressure) {
        this.goodPressure = goodPressure;
    }

    @Override
    public String toString() {
        return "{" +
                "startDate='" + startDate + '\'' +
                ", minTemperature=" + minTemperature +
                ", maxTemperature=" + maxTemperature +
                ", windSpeed= " + getWindSpeed() +
                ", weatherType= " + getWeatherType()+
                ", goodPressure= " + goodPressure+
                '}';
    }
}
