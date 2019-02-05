package com.umik.gnomex909.aurra;

/**
 * Created by Gnomex on 27.11.2017.
 * Klasa podstawowa dwóch klas związanych z pogodą, które zawierają prognozy pogody
 */

public class Forecast {
    private double windSpeed;
    private String startTime;
    private String endTime;
    private int weatherType;

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(int weatherType) {
        this.weatherType = weatherType;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" +
                ", windSpeed=" + windSpeed +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", weatherType=" + weatherType +
                '}';
    }
}
