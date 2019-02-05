package com.umik.gnomex909.aurra;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gnomex on 25.11.2017.
 * Klasa, która ma za zadanie pobrać w tle surowe dane z serwera HTTP API pogodowego i zamienić go na obiekty typu WeatherForecast, zawierające interesujące nas dane o pogodzie
 */

public class GetWeatherForecastData extends AsyncTask<String,Void, List<WeatherForecast>> implements GetRawData.OnDownloadComplete{
    private static final String TAG = "GetWeatherForecastData";
    private boolean runningOnSameThread =false;

    private String mBaseURL="http://api.openweathermap.org/data/2.5/forecast";
    private String city="Warsaw";
    private String countryCode ="pl";
    private String mode = "xml";
    private String units = "metric";
    private String key = "de04d02ac194576754b317db876db678";
    private Location mLocation = null;
    private final OnDataAvailable mCallBack;
    List<WeatherForecast> mWeatherForecasts;

    interface OnDataAvailable {
        void onDataAvailable(List<WeatherForecast> data, DownloadStatus status);
    }

    public GetWeatherForecastData(OnDataAvailable callBack) {
        mCallBack = callBack;
    }
    public GetWeatherForecastData(OnDataAvailable callBack, Location location) {mCallBack = callBack; mLocation = location;}
    @Override
    protected List<WeatherForecast> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUrl();
        Log.d(TAG, "doInBackground: URL is " + destinationUri);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");
        return mWeatherForecasts;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus downloadStatus) {
        Log.d(TAG, "onDownloadComplete: starts");
        if(downloadStatus == DownloadStatus.FAILED_OR_EMPTY.OK){
            WeatherForecast currentRecord = null;
            boolean inEntry = false;
            String textValue = "";
            mWeatherForecasts = new ArrayList<>();
            try{
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(data));
                int eventType = xpp.getEventType();
                while(eventType != XmlPullParser.END_DOCUMENT){
                    String tagName = xpp.getName();
                    switch(eventType) {
                        case XmlPullParser.START_TAG:
                            //     Log.d(TAG, "parse: Starting tag for" + tagName);
                            if("time".equalsIgnoreCase(tagName)) {
                                inEntry = true;
                                currentRecord = new WeatherForecast();

                                String timeData = xpp.getAttributeValue(0);
                                String[] dateTime = timeData.split("T");
                                currentRecord.setStartTime(dateTime[1]);
                                currentRecord.setDate(dateTime[0]);

                                timeData = xpp.getAttributeValue(1);
                                dateTime = timeData.split("T");
                                currentRecord.setEndTime(dateTime[1]);

                    }else if("symbol".equalsIgnoreCase(tagName)) {
                                int weatherCode = (Integer.parseInt(xpp.getAttributeValue(0)));
                               //Log.d(TAG, "onDownloadComplete: weatherCode is " + weatherCode);
                                if (weatherCode < 300 && weatherCode >= 200)
                                    currentRecord.setWeatherType(7);
                                else if (weatherCode < 400 && weatherCode >= 300)
                                    currentRecord.setWeatherType(4);
                                else if (weatherCode < 600 && weatherCode >= 500)
                                    currentRecord.setWeatherType(6);
                                else if (weatherCode < 700 && weatherCode >= 600)
                                    currentRecord.setWeatherType(5);
                                else if (weatherCode < 800 && weatherCode >= 700)
                                    currentRecord.setWeatherType(3);
                                else if (weatherCode == 800)
                                    currentRecord.setWeatherType(1);
                                else if (weatherCode < 900 && weatherCode > 800)
                                    currentRecord.setWeatherType(2);
                                else if (weatherCode < 1000 && weatherCode >= 900)
                                    currentRecord.setWeatherType(8);
                                else
                                    currentRecord.setWeatherType(9);

                    }else if ("windSpeed".equalsIgnoreCase(tagName)){
                        currentRecord.setWindSpeed(Double.parseDouble(xpp.getAttributeValue(0)));
                    }else if ("temperature".equalsIgnoreCase(tagName)){
                        currentRecord.setTemperature(Double.parseDouble(xpp.getAttributeValue(1)));
                    }
                    else if ("pressure".equalsIgnoreCase(tagName)){
                        currentRecord.setPressure(Double.parseDouble(xpp.getAttributeValue(1)));
                    }
                            break;
                        case XmlPullParser.TEXT:
                            textValue = xpp.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            // Log.d(TAG, "parse: Ending tag for" + tagName);
                            if(inEntry){
                                if("time".equalsIgnoreCase(tagName)) {
                                    mWeatherForecasts.add(currentRecord);
                                    inEntry = false;
                                }
                                break;
                            }
                        default:


                    }
                    eventType = xpp.next();
                }


            } catch(Exception e){
                e.printStackTrace();
            }
//            for(WeatherForecast wf : mWeatherForecasts){
//                Log.d(TAG, "onDownloadComplete: Weather forecast is: " + wf.toString());
//            }


        }
        if (runningOnSameThread && (mCallBack != null)){
            mCallBack.onDataAvailable(mWeatherForecasts, downloadStatus);
        }

    }
    @Override
    protected void onPostExecute(List<WeatherForecast> weatherForecasts) {
        Log.d(TAG, "onPostExecute: starts");
        if(mCallBack != null){
            mCallBack.onDataAvailable((weatherForecasts),DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }
    private String createUrl(){
        Log.d(TAG, "createUrl: starts");
        String uri;
        if(null != mLocation){
            uri = Uri.parse(mBaseURL).buildUpon()
                    .appendQueryParameter("lat", ""+mLocation.getLatitude())
                    .appendQueryParameter("lon", ""+mLocation.getLongitude())
                    .appendQueryParameter("mode", mode)
                    .appendQueryParameter("units", units)
                    .appendQueryParameter("APPID", key)
                    .build().toString();
        }
        else {
            uri = Uri.parse(mBaseURL).buildUpon()

                    .appendQueryParameter("mode", mode)
                    .appendQueryParameter("units", units)
                    .appendQueryParameter("APPID", key)
                    .build().toString();
            uri += "&q=" + city + "," + countryCode;
        }
        return uri;
    }
}
