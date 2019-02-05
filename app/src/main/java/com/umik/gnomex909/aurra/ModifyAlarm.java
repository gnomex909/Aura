package com.umik.gnomex909.aurra;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Calendar;

import static com.umik.gnomex909.aurra.MainActivity.HOME_LAT;
import static com.umik.gnomex909.aurra.MainActivity.HOME_LON;

/**
 * Created by Gnomex on 17.12.2017.
 * Alarm, który ma za zadanie raz na dobę zaktualizować lokalizację domową użytkownika i tablice wyjść.
 */

public class ModifyAlarm extends BroadcastReceiver {
    private static final String TAG = "ModifyAlarm";
    public static final String MODIFY_ALARM = "com.umik.alarms.MODIFY_ALARM";
    private LocationManager mLocationManager;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: started");
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location home = getLastBestLocation();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        if(home != null) {
            sharedPreferences.edit().putString(HOME_LAT, Location.convert(home.getLatitude(), Location.FORMAT_SECONDS)).apply();
            sharedPreferences.edit().putString(HOME_LON, Location.convert(home.getLongitude(), Location.FORMAT_SECONDS)).apply();
            Log.d(TAG, "onClick: Home location is " + home.toString());
        }
        Log.d(TAG, "onReceive: ending ");
        OutsideDataAdapter outsideDataAdapter = new OutsideDataAdapter(context);
        outsideDataAdapter.fullAdaptation();
    }

    public void setAlarm(Context context){
        Log.d(TAG, "setAlarm: started");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent (context, ModifyAlarm.class);
        intent.setAction(ModifyAlarm.MODIFY_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        long date = calendar.getTimeInMillis();
        Log.d(TAG, "setAlarm: time in Millis is " + date);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 00);
        Log.d(TAG, "setAlarm:  time in millis is " + calendar.getTimeInMillis());

        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
    }

    public void cancelAlarm(Context context)
    {
        Log.d(TAG, "cancelAlarm: started");
        Intent intent = new Intent(context, ModifyAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private Location getLastBestLocation() {
        Log.d(TAG, "getLastBestLocation: started");
        Location locationGPS = null;
        Location locationNet = null;

        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(null != locationGPS)
                Log.d(TAG, "getLastBestLocation: " + locationGPS.toString());
        }
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(null != locationNet)
                Log.d(TAG, "getLastBestLocation: " + locationNet.toString());
        }

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }


    }


}
