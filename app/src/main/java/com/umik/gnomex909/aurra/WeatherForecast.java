package com.umik.gnomex909.aurra;

/**
 * Created by Gnomex on 25.11.2017.
 * Klasa służąca do przechowywania danych o pogodzie ściągniętych bezpośrednio z serwera API pogodowego
 */

public class WeatherForecast extends Forecast{
    private double temperature;
    private String date;
    private double pressure;

    public WeatherForecast() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }


    @Override
    public String toString() {
        return "WeatherForecast{" +
                ", windSpeed=" + this.getWindSpeed() +
                ", pressure=" + this.getPressure() +
                ", startTime='" + this.getStartTime() + '\'' +
                ", endTime='" + this.getEndTime() + '\'' +
                ", weatherType=" + this.getWeatherType() +
                "temperature=" + temperature +
                ", date='" + date + '\'' +
                '}';
    }
}
