package com.umik.gnomex909.aurra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Gnomex on 08.12.2017.
 * Klasa mająca za zadanie dostarczyć bazę danym w chwili, gdy jest ona potrzebna, lub też stworzyć ją przy pierwszym uruchomieniu
 */

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "Aura.db";
    public static final int DATABASE_VERION = 1;
    private static AppDatabase instance = null;

    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERION);
        Log.d(TAG, "AppDatabase: constructor");
    }


    static AppDatabase getInstace(Context context){
        if(instance == null){
            Log.d(TAG, "getInstace: creating new instance");
            instance = new AppDatabase(context);
        }
        return instance;

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: starts");
        String sSQL;
        //sSQL = "CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER, CategoryID INTEGER);";
            sSQL = "CREATE TABLE " + UserForecastsContract.TABLE_NAME
                    + " (" + UserForecastsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                    + UserForecastsContract.Columns.FORECAST_DAY + " TEXT NOT NULL, "
                    + UserForecastsContract.Columns.FORECAST_START + " TEXT NOT NULL, "
                    + UserForecastsContract.Columns.FORECAST_END + " TEXT NOT NULL, "
                    + UserForecastsContract.Columns.FORECAST_SORTORDER + " INTEGER);";

        sqLiteDatabase.execSQL(sSQL);
        sSQL = "CREATE TABLE " + UserForecastsContract.OUTSIDE_TABLE_NAME
                + " (" + UserForecastsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + UserForecastsContract.Columns.FORECAST_DAY + " TEXT NOT NULL, "
                + UserForecastsContract.Columns.FORECAST_START + " TEXT NOT NULL, "
                + UserForecastsContract.Columns.FORECAST_END + " TEXT NOT NULL, "
                + UserForecastsContract.Columns.FORECAST_DATE + " TEXT, "
                + UserForecastsContract.Columns.FORECAST_SORTORDER + " INTEGER);";
        sqLiteDatabase.execSQL(sSQL);

        Log.d(TAG, "onCreate: ends");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpgrade: starts");
        switch(i){
            case 1:
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: "+ i1);

        };
        Log.d(TAG, "onUpgrade: ends");

    }
}

