package com.umik.gnomex909.aurra;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gnomex on 17.12.2017.
 * Serwis mający za zadanie sprawdzanie czy użytkownik znajduje się w domu, czy też poza nim. Działa w tle przez cały czas działania telefonu po uruchomieniu aplikacji.
 */

public class InOutChecker extends Service {
    private static final String TAG = "InOutChecker";
    private LocationManager mLocationManager = null;
    //private Boolean isOut;
    private static final int LOCATION_INTERVAL = 1000 * 60 * 30;
    private static final float LOCATION_DISTANCE = 200;
    public static final String OUT_TIME = "outTime";
    public static final String GOT_OUT = "userGotOut";


    private class LocationListener implements android.location.LocationListener
    {
        private Context mContext;

        public LocationListener(String provider, Context context)
        {
            mContext = context;
            Log.d(TAG, "LocationListener " + provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.d(TAG, "onLocationChanged: " + location);
            boolean inHome;
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

            double homeLat = Location.convert(sharedPreferences.getString(MainActivity.HOME_LAT,"-122.0840").replace(",","."));
            double homeLong = Location.convert(sharedPreferences.getString(MainActivity.HOME_LON,"37.422").replace(",","."));
            float[] distance = new float[1];
            Log.d(TAG, "onLocationChanged: Home location is :" + homeLat +"|" + homeLong);
            Log.d(TAG, "onLocationChanged: Current location is: " + location.getLongitude()+"|" + location.getLatitude());
            Location.distanceBetween(homeLat, homeLong, location.getLatitude(), location.getLongitude(), distance);
            Log.d(TAG, "onLocationChanged: distance is " + distance[0]);
           // Log.d(TAG, "onLocationChanged: second distance is " + distance[1]);
            if(distance[0]> 100){
                inHome = false;
            }
            else {
                inHome = true;
            }
            boolean isOut = sharedPreferences.getBoolean(GOT_OUT, false);
            if(isOut){
                if(inHome){
                    sharedPreferences.edit().putBoolean(GOT_OUT, false).apply();
                    String startTime =sharedPreferences.getString(OUT_TIME,"");
                    if(startTime.length()>0){
                        sharedPreferences.edit().putBoolean(GOT_OUT, true);
                        Calendar calendar = Calendar.getInstance();
                        Date now = calendar.getTime();
                        DateFormat time = new SimpleDateFormat("H:m");
                        DateFormat todayDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat dayOfWeek = new  SimpleDateFormat("EEEE", Locale.US);
                        String endTime = time.format(now);
                        String date = todayDateFormat.format(now);
                        String day = dayOfWeek.format(now);
                        Log.d(TAG, "onLocationChanged: Day is " + day);

                        ContentResolver contentResolver = mContext.getContentResolver();
                        ContentValues values = new ContentValues();

                        values.put(UserForecastsContract.Columns.FORECAST_START, startTime);
                        values.put(UserForecastsContract.Columns.FORECAST_END, endTime);
                        values.put(UserForecastsContract.Columns.FORECAST_DAY, day);
                        values.put(UserForecastsContract.Columns.FORECAST_DATE, date);
                        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);
                        sharedPreferences.edit().putString(OUT_TIME,"").apply();


                    }

                }

            }
            else {
                if(!inHome){
                    sharedPreferences.edit().putBoolean(GOT_OUT, true).apply();
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    DateFormat timeFormat = new SimpleDateFormat("H:m");
                    sharedPreferences.edit().putString(OUT_TIME, timeFormat.format(now)).apply();
                }
            }



        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
           // Log.d(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER, this),
            new LocationListener(LocationManager.NETWORK_PROVIDER, this)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
