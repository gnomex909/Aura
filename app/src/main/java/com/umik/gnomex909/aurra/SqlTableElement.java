package com.umik.gnomex909.aurra;

/**
 * Created by Gnomex on 13.12.2017.
 * Klasa zawierająca w sobie wszystkie informacje, jakie aplikacja może pobrać z bazy danych SQL
 */

public class SqlTableElement {
    private String startTime;
    private String endTime;
    private String date;
    private String dayOfWeek;
    private long id;

    public SqlTableElement(String startTime, String endTime, String dayOfWeek) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
    }

    public SqlTableElement(long id, String startTime, String endTime, String dayOfWeek, String date) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
