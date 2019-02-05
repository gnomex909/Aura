package com.umik.gnomex909.aurra;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gnomex on 25.11.2017.
 * Aktywnośc bazowa, zajmująca się wyświetlaniem informacji o pogodzie dla użytkownika i wszystkim co jest z tym związane, by działało i było aktualizowane
 */
public class MainActivity extends AppCompatActivity implements GetWeatherForecastData.OnDataAvailable, AppDialog.DialogEvents{
    private static final String TAG = "MainActivity";
    public static final String NEW = "JustInstalled";
    public static  int PERMISSIONS;
    public static Location mLastLocation = null;
    public static final int DIALOG_HOME = 1;
    public static final String HOME_LAT = "HomeLocationLat";
    public static final String HOME_LON = "HomeLocationLon";
    private LocationManager mLocationManager=null;
    private LocationListener locationListener=null;
    private WeatherRecyclerViewAdapter mWeatherRecyclerViewAdapter;



    @Override
    public void onDataAvailable(List<WeatherForecast> data, DownloadStatus status) {
        Log.d(TAG, "onDataAvailable: starts");
        List<UserForecast> userForecasts = getSqlData();

        for(UserForecast uf : userForecasts){
            uf.setUserForecast(data);
        }
        mWeatherRecyclerViewAdapter.loadNewData(userForecasts);

        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String startTime = sharedPreferences.getString(NEW,"");
        if(!(startTime.equals("It's not a new application!"))){
            insertSQL();
            Log.d(TAG, "onCreate: Populating SQL table");
            Location home = getLastBestLocation();
            if(home != null) {
                sharedPreferences.edit().putString(HOME_LAT, Location.convert(home.getLatitude(), Location.FORMAT_MINUTES)).apply();
                sharedPreferences.edit().putString(HOME_LON, Location.convert(home.getLongitude(), Location.FORMAT_MINUTES)).apply();
            }

            OutsideDataAdapter outsideDataAdapter = new OutsideDataAdapter(this);
            outsideDataAdapter.fullAdaptation();
            sharedPreferences.edit().putString(NEW, "It's not a new application!").apply();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton homeButton = (FloatingActionButton) findViewById(R.id.homeButton);

        startService(new Intent(this,InOutChecker.class));

        ModifyAlarm modifyAlarm = new ModifyAlarm();
        modifyAlarm.cancelAlarm(this);
        modifyAlarm.setAlarm(this);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID, DIALOG_HOME);
                args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.home_message));
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), null);
            }
        });


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLocationManager = (LocationManager)
                getApplicationContext().getSystemService(Context.LOCATION_SERVICE);



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        mWeatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(this, new ArrayList<UserForecast>());

        recyclerView.setAdapter(mWeatherRecyclerViewAdapter);



    }
    @Override
    protected void onResume(){
        super.onResume();
        mLastLocation = getLastBestLocation();
        GetWeatherForecastData getWeatherForecastData;
        if(null != mLastLocation){
            getWeatherForecastData = new GetWeatherForecastData(this, mLastLocation);

        }
        else {
            getWeatherForecastData = new GetWeatherForecastData(this);
        }
        String result = "";
        getWeatherForecastData.execute(result);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sql_user){
            Intent intent = new Intent(this, ShowSQLTableActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Location getLastBestLocation() {
        mLocationManager = (LocationManager)
                getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = null;
        Location locationNet = null;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLocationManager != null)
                locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (null != locationGPS)
                Log.d(TAG, "getLastBestLocation: " + locationGPS.toString());

        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(mLocationManager!= null)
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

    private List<UserForecast> getSqlData(){
        Log.d(TAG, "getSqlData: starts");

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Log.d(TAG, "getSqlData: date is " + today);
        DateFormat todayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dayOfWeek = new  SimpleDateFormat("EEEE", Locale.US);
        DateFormat currentHour = new SimpleDateFormat("H");
        int currentDateHour = Integer.parseInt(currentHour.format(today));
        String date = todayDateFormat.format(today);
        String day[] = {dayOfWeek.format(today)};
        //String day[] = {"Thursday"};
        Log.d(TAG, "getSqlData: Day is " + day[0]);

        String[] projection = {UserForecastsContract.Columns._ID, UserForecastsContract.Columns.FORECAST_DAY, UserForecastsContract.Columns.FORECAST_START, UserForecastsContract.Columns.FORECAST_END};
        Cursor cursor = getContentResolver().query(UserForecastsContract.CONTENT_URI, projection, UserForecastsContract.Columns.FORECAST_DAY+"=?", day, UserForecastsContract.Columns.FORECAST_START);
        List<UserForecast> userForecastList = new ArrayList<>();

        if(cursor.moveToFirst()){

            UserForecast userForecast = new UserForecast(cursor.getString(2), cursor.getString(3), date);
            Log.d(TAG, "getSqlData: " + userForecast.toString());
            String[] split  = userForecast.getEndTime().split(":");
            if(Integer.parseInt(split[0])>floor(currentDateHour,3))
            userForecastList.add(userForecast);
            while(cursor.moveToNext()){
                userForecast = new UserForecast(cursor.getString(2), cursor.getString(3), date);
                Log.d(TAG, "getSqlData: " + userForecast.toString());
                split  = userForecast.getEndTime().split(":");
                if(Integer.parseInt(split[0])>floor(currentDateHour,3))
                userForecastList.add(userForecast);
            }
        }
        return userForecastList;
    };



    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Location home = getLastBestLocation();
        if(home != null) {
            sharedPreferences.edit().putString(HOME_LAT, Location.convert(home.getLatitude(), Location.FORMAT_MINUTES)).apply();
            sharedPreferences.edit().putString(HOME_LON, Location.convert(home.getLongitude(), Location.FORMAT_MINUTES)).apply();
            Log.d(TAG, "onClick: Home location is " + home.toString());
        }

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCanceled(int dialogId) {

    }

    int floor(double i, int v){
        return (int)(Math.floor(i/v) * v);
    }

    private void insertSQL(){
        ContentResolver contentResolver = this.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(UserForecastsContract.Columns.FORECAST_START,"16:30");
        values.put(UserForecastsContract.Columns.FORECAST_END,"20:25");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Monday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"16:13");
        values.put(UserForecastsContract.Columns.FORECAST_END,"20:56");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Monday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"10:23");
        values.put(UserForecastsContract.Columns.FORECAST_END,"13:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Monday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);


        values.put(UserForecastsContract.Columns.FORECAST_START,"11:30");
        values.put(UserForecastsContract.Columns.FORECAST_END,"13:25");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Monday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"11:17");
        values.put(UserForecastsContract.Columns.FORECAST_END,"19:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Tuesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"11:05");
        values.put(UserForecastsContract.Columns.FORECAST_END,"18:20");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Tuesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"21:17");
        values.put(UserForecastsContract.Columns.FORECAST_END,"23:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Tuesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"22:02");
        values.put(UserForecastsContract.Columns.FORECAST_END,"23:59");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Tuesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"11:10");
        values.put(UserForecastsContract.Columns.FORECAST_END,"16:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Wednesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"11:45");
        values.put(UserForecastsContract.Columns.FORECAST_END,"17:00");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Wednesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"18:10");
        values.put(UserForecastsContract.Columns.FORECAST_END,"22:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Wednesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"18:45");
        values.put(UserForecastsContract.Columns.FORECAST_END,"23:00");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Wednesday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"9:10");
        values.put(UserForecastsContract.Columns.FORECAST_END,"15:15");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Thursday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"9:25");
        values.put(UserForecastsContract.Columns.FORECAST_END,"15:47");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Thursday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"19:25");
        values.put(UserForecastsContract.Columns.FORECAST_END,"21:47");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Thursday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"20:25");
        values.put(UserForecastsContract.Columns.FORECAST_END,"22:47");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Thursday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"13:11");
        values.put(UserForecastsContract.Columns.FORECAST_END,"19:00");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Friday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"13:50");
        values.put(UserForecastsContract.Columns.FORECAST_END,"18:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Friday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"20:11");
        values.put(UserForecastsContract.Columns.FORECAST_END,"23:59");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Friday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"20:01");
        values.put(UserForecastsContract.Columns.FORECAST_END,"23:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Friday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"9:11");
        values.put(UserForecastsContract.Columns.FORECAST_END,"13:00");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Saturday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"9:15");
        values.put(UserForecastsContract.Columns.FORECAST_END,"13:45");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Saturday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"16:11");
        values.put(UserForecastsContract.Columns.FORECAST_END,"18:27");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Saturday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"16:45");
        values.put(UserForecastsContract.Columns.FORECAST_END,"23:00");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Saturday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"10:11");
        values.put(UserForecastsContract.Columns.FORECAST_END,"13:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Sunday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"10:45");
        values.put(UserForecastsContract.Columns.FORECAST_END,"13:15");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Sunday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"17:11");
        values.put(UserForecastsContract.Columns.FORECAST_END,"18:30");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Sunday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);

        values.put(UserForecastsContract.Columns.FORECAST_START,"16:31");
        values.put(UserForecastsContract.Columns.FORECAST_END,"19:15");
        values.put(UserForecastsContract.Columns.FORECAST_DAY, "Sunday");
        contentResolver.insert(UserForecastsContract.OUTSIDE_CONTENT_URI, values);



    }
}
