package com.umik.gnomex909.aurra;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.umik.gnomex909.aurra.AppProvider.CONTENT_AUTHORITY;
import static com.umik.gnomex909.aurra.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * Created by Gnomex on 08.12.2017.
 * Klasa służaca zapisaniu zmiennych służących do dostępu do tabel SQL
 */

public class UserForecastsContract {
    static final String TABLE_NAME = "UserForecast";;
    static final String OUTSIDE_TABLE_NAME = "InOutTable";
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String FORECAST_START = "StartHour";
        public static final String FORECAST_END = "EndHour";
        public static final String FORECAST_DAY = "DayOfWeek";
        public static final String FORECAST_SORTORDER = "SortOrder";
        public static final String FORECAST_DATE = "Date";

        private Columns() {

        }
    }
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+CONTENT_AUTHORITY+"."+TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."+CONTENT_AUTHORITY+"."+TABLE_NAME;

    public static final Uri OUTSIDE_CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, OUTSIDE_TABLE_NAME);
    static final String OUTSIDE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+CONTENT_AUTHORITY+"."+OUTSIDE_TABLE_NAME;
    static final String OUTSIDE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."+CONTENT_AUTHORITY+"."+OUTSIDE_TABLE_NAME;


    static Uri buildTaskUri(long taskId){
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }
    static long getTaskId(Uri uri){
        return ContentUris.parseId(uri);
    }

    static Uri buildOutsideUri(long taskId) {return ContentUris.withAppendedId(OUTSIDE_CONTENT_URI, taskId); }
    static long getOutsideId(Uri uri) { return  ContentUris.parseId(uri);}
}
