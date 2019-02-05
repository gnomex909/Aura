package com.umik.gnomex909.aurra;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gnomex on 16.12.2017.
 * Adapter pomiędzy tablicą surowych danych, a przetworzonymi danymi, które są używane przez aplikację.
 */

public class OutsideDataAdapter {
    private static final String TAG = "OutsideDataAdapter";
    private final int[] hourBreakpoints = {0,3,6,9,12,15,18,21,24};
    List<ArrayList<SqlTableElement>> mSqlTableElementsList;
    List<String[]> selectionArgs;
    Context mContext;

    public OutsideDataAdapter(Context context){
        mSqlTableElementsList = new ArrayList<>();
        selectionArgs = new ArrayList<>();
        mContext = context;
    }
    public void oneDayAdaptation(String day){
        String[] selection = {day};
        String[] projection = {UserForecastsContract.Columns._ID, UserForecastsContract.Columns.FORECAST_START, UserForecastsContract.Columns.FORECAST_END, UserForecastsContract.Columns.FORECAST_DAY, UserForecastsContract.Columns.FORECAST_DATE};
        mContext.getContentResolver().delete(UserForecastsContract.CONTENT_URI,UserForecastsContract.Columns.FORECAST_DAY+"=?",selection);
        Cursor cursor = mContext.getContentResolver().query(UserForecastsContract.OUTSIDE_CONTENT_URI, projection, UserForecastsContract.Columns.FORECAST_DAY+"=?", selection, UserForecastsContract.Columns.FORECAST_START);
        sqlReader(cursor);
    }
    public void fullAdaptation(){
        String[] selectionElement = {"Monday"};
        selectionArgs.add(selectionElement);
        selectionElement = new String[]{"Tuesday"};
        selectionArgs.add(selectionElement);
        selectionElement = new String[]{"Wednesday"};
        selectionArgs.add(selectionElement);
        selectionElement = new String[]{"Thursday"};
        selectionArgs.add(selectionElement);
        selectionElement = new String[]{"Friday"};
        selectionArgs.add(selectionElement);
        selectionElement = new String[]{"Saturday"};
        selectionArgs.add(selectionElement);
        selectionElement = new String[]{"Sunday"};
        selectionArgs.add(selectionElement);
        for(int i=0; i<7; i++){
            String[] projection = {UserForecastsContract.Columns._ID, UserForecastsContract.Columns.FORECAST_START, UserForecastsContract.Columns.FORECAST_END, UserForecastsContract.Columns.FORECAST_DAY, UserForecastsContract.Columns.FORECAST_DATE};
            mContext.getContentResolver().delete(UserForecastsContract.CONTENT_URI,UserForecastsContract.Columns.FORECAST_DAY+"=?",selectionArgs.get(i));
            Cursor cursor = mContext.getContentResolver().query(UserForecastsContract.OUTSIDE_CONTENT_URI, projection, UserForecastsContract.Columns.FORECAST_DAY+"=?", selectionArgs.get(i), UserForecastsContract.Columns.FORECAST_START);
            sqlReader(cursor);
        }
    }

    public void sqlReader(Cursor cursor){
        ArrayList<SqlTableElement> outsideTableList = new ArrayList<>();
        if(cursor.moveToFirst()){
            SqlTableElement sqlTableElement = new SqlTableElement(cursor.getLong(0), cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4));
            outsideTableList.add(sqlTableElement);
            while(cursor.moveToNext()){
                sqlTableElement = new SqlTableElement(cursor.getLong(0), cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4));
                outsideTableList.add(sqlTableElement);
            }
        }
        if (!outsideTableList.isEmpty()){
            dataAdapter(outsideTableList);
        }
    }

    public void dataAdapter(List<SqlTableElement> sqlTableElementList){
        for(int k=0;k<hourBreakpoints.length-1 ;k++){
            ArrayList<SqlTableElement> placeholderList = new ArrayList<>();
            mSqlTableElementsList.add(placeholderList);
        }
        for(SqlTableElement sqlTableElement : sqlTableElementList){
            String[] startTime = sqlTableElement.getStartTime().split(":");
            int startHour = Integer.parseInt(startTime[0]);
            for(int i =0; i < hourBreakpoints.length-1; i++){
                if(startHour>= hourBreakpoints[i] && startHour<hourBreakpoints[i+1]){
                    Log.d(TAG, "dataAdapter: adding element to list number " + i);
                    mSqlTableElementsList.get(i).add(sqlTableElement);
                }
            }
        }
        for(ArrayList<SqlTableElement> list : mSqlTableElementsList){
            if(!list.isEmpty()){
                int startTimeInMinutes=0;
                int endTimeInMinutes =0;
                int length = list.size();
                Log.d(TAG, "dataAdapter: lenght is " + length);
                String day = list.get(0).getDayOfWeek();
                for(SqlTableElement sqlTableElement : list){
                    String[] time = sqlTableElement.getStartTime().split(":");
                    startTimeInMinutes += (Integer.parseInt(time[0])*60)+ Integer.parseInt(time[1]);
                    time = sqlTableElement.getEndTime().split(":");
                    endTimeInMinutes += (Integer.parseInt(time[0])*60)+ Integer.parseInt(time[1]);
                }
                Log.d(TAG, "dataAdapter: Start time is :" + startTimeInMinutes + ": end time is:" +endTimeInMinutes);

                startTimeInMinutes = startTimeInMinutes / length;
                endTimeInMinutes = endTimeInMinutes/length;
                Log.d(TAG, "dataAdapter:  FINAL start time is :" + startTimeInMinutes + ": end time is:" +endTimeInMinutes);
                String startTime;

                int hour = startTimeInMinutes/60;
                if(hour<10){
                    startTime = "0" + hour;
                }
                else
                    startTime = ""+hour;
                startTime +=":";
                int minute = startTimeInMinutes % 60;
                if(minute<10){
                    startTime +="0"+minute;
                }
                else
                    startTime+=minute;

                Log.d(TAG, "dataAdapter: User Start Time is " + startTime);

                String endTime;
                 hour = endTimeInMinutes/60;
                if(hour<10){
                    endTime = "0" + hour;
                }
                else
                    endTime = ""+hour;
                endTime +=":";
                 minute = endTimeInMinutes % 60;
                if(minute<10){
                    endTime +="0"+minute;
                }
                else
                    endTime+=minute;

                Log.d(TAG, "dataAdapter: User End Time is " + endTime);

                ContentResolver contentResolver = mContext.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(UserForecastsContract.Columns.FORECAST_START,startTime);
                values.put(UserForecastsContract.Columns.FORECAST_END,endTime);
                values.put(UserForecastsContract.Columns.FORECAST_DAY, day);
                contentResolver.insert(UserForecastsContract.CONTENT_URI, values);

            }
            mSqlTableElementsList = new ArrayList<>();
        }
    }
}
