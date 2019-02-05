package com.umik.gnomex909.aurra;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Gnomex on 08.12.2017.
 * Klasa która ma w sobie kod służacy do uproszczenia i zabezpieczenia wszelkich komend związanych z SQL i dostępem do baz danych. Definiuje polecenia jak insert, query, delete itp.
 */

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";
    private AppDatabase mOpenHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.umik.gnomex909.aurra.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int FORECAST = 100;
    private static final int FORECAST_ID = 101;
    private static final int OUTSIDE = 200;
    private static final int OUTSIDE_ID = 201;

    /*
    private static final int TASK_TIMINGS = 300;
    private static final int TASK_TIMINGS-ID = 301;
     */
    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID=401;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, UserForecastsContract.TABLE_NAME, FORECAST);
        matcher.addURI(CONTENT_AUTHORITY, UserForecastsContract.TABLE_NAME +"/#", FORECAST_ID);

        matcher.addURI(CONTENT_AUTHORITY, UserForecastsContract.OUTSIDE_TABLE_NAME, OUTSIDE);
        matcher.addURI(CONTENT_AUTHORITY, UserForecastsContract.OUTSIDE_TABLE_NAME+"/#", OUTSIDE_ID);
//
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS);
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME +"/#", TASK_DURATIONS_ID);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstace(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match){
            case FORECAST:
                queryBuilder.setTables(UserForecastsContract.TABLE_NAME);
                break;
            case FORECAST_ID:
                queryBuilder.setTables(UserForecastsContract.TABLE_NAME);
                long taskId = UserForecastsContract.getTaskId(uri);
                queryBuilder.appendWhere(UserForecastsContract.Columns._ID + " = " + taskId);
                break;
            case OUTSIDE:
                queryBuilder.setTables(UserForecastsContract.OUTSIDE_TABLE_NAME);
                break;
            case OUTSIDE_ID:
                queryBuilder.setTables(UserForecastsContract.OUTSIDE_TABLE_NAME);
                long timingsId = UserForecastsContract.getOutsideId(uri);
                queryBuilder.appendWhere(UserForecastsContract.OUTSIDE_TABLE_NAME + " = " + timingsId);
                break;
//            case TASK_DURATIONS:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;
//            case TASK_DURATIONS_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationId = DurationsContract.getTimingId(uri);
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationId);
//                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+ uri);


        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null,null,sortOrder);
        Log.d(TAG, "query: rows in returned cursor = " + cursor.getCount());
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FORECAST:
                return UserForecastsContract.CONTENT_TYPE;
            case FORECAST_ID:
                return UserForecastsContract.CONTENT_ITEM_TYPE;
            case OUTSIDE:
                return UserForecastsContract.OUTSIDE_CONTENT_TYPE;
            case OUTSIDE_ID:
                  return UserForecastsContract.OUTSIDE_CONTENT_TYPE;
//            case TASK_DURATIONS:
//                return TimingsContract.TaskDurations.CONTENT_TYPE;
//            case TASK_DURATIONS_ID:
//                return TimingsContract.TaskDurations.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        Log.d(TAG, "insert: with URI: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "Match is " + match);
        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;
        switch(match){
            case FORECAST:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(UserForecastsContract.TABLE_NAME,null,contentValues);
                if(recordId >=0){
                    returnUri = UserForecastsContract.buildTaskUri(recordId);
                }
                else{
                    throw new android.database.SQLException("Failed to insert into "+ uri.toString());
                }
                break;
               case OUTSIDE:
                   db = mOpenHelper.getWritableDatabase();
                     recordId = db.insert(UserForecastsContract.OUTSIDE_TABLE_NAME,null,contentValues);
                  if(recordId >=0){
                       returnUri = UserForecastsContract.buildOutsideUri(recordId);
                   }
                 else{
                      throw new android.database.SQLException("Failed to insert into "+ uri.toString());
                 }
                 break;

            default:
                throw new  IllegalArgumentException("Unknown uri: "+uri);
        }
        if(recordId >= 0){
            Log.d(TAG, "insert: setting notify Chhanged with " + uri);
            getContext().getContentResolver().notifyChange(uri,null );
        }
        else{
            Log.d(TAG, "insert: nothing inserted");
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match is " + match);
        final SQLiteDatabase db;
        int count;

        String selectionCriteria;
        switch(match) {
            case FORECAST:
                db=mOpenHelper.getWritableDatabase();
                count = db.delete(UserForecastsContract.TABLE_NAME, selection,selectionArgs);
                break;
            case FORECAST_ID:
                db=mOpenHelper.getWritableDatabase();
                long taskId = UserForecastsContract.getTaskId(uri);

                selectionCriteria= UserForecastsContract.Columns._ID+" = " + taskId;
                if((selection!=null)&& (selection.length()>0)) {
                    selectionCriteria+= "AND ("+selection+ ")";
                }
                count = db.delete(UserForecastsContract.TABLE_NAME, selectionCriteria,selectionArgs);
                break;
            case OUTSIDE:
                db=mOpenHelper.getWritableDatabase();
                count = db.delete(UserForecastsContract.OUTSIDE_TABLE_NAME, selection,selectionArgs);
                break;
            case OUTSIDE_ID:
                db=mOpenHelper.getWritableDatabase();
                long timingsId = UserForecastsContract.getOutsideId(uri);

                selectionCriteria= UserForecastsContract.Columns._ID+" = " + timingsId;
                Log.d(TAG, "delete: Selection Criteria is " + selectionCriteria);
                if((selection!=null)&& (selection.length()>0)) {
                    selectionCriteria+= "AND ("+selection+ ")";
                }
                count = db.delete(UserForecastsContract.OUTSIDE_TABLE_NAME,selectionCriteria,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);

        }
        if(count>0){
            getContext().getContentResolver().notifyChange(uri,null);
            Log.d(TAG, "delete: setting notifyChange with " + uri);
        } else{
            Log.d(TAG, "delete: nothing deleted");
        }
        Log.d(TAG, "update: returning" + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match is " + match);
        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch(match) {
            case FORECAST:
                db=mOpenHelper.getWritableDatabase();
                count = db.update(UserForecastsContract.TABLE_NAME, contentValues, selection,selectionArgs);
                break;
            case FORECAST_ID:
                db=mOpenHelper.getWritableDatabase();
                long taskId = UserForecastsContract.getTaskId(uri);

                selectionCriteria = UserForecastsContract.Columns._ID +" = " + taskId;
                if((selection!=null)&& (selection.length()>0)) {
                    selectionCriteria+= "AND ("+selection+ ")";
                }
                count = db.update(UserForecastsContract.TABLE_NAME,contentValues,selectionCriteria,selectionArgs);
                break;
            case OUTSIDE:
                db=mOpenHelper.getWritableDatabase();
                count = db.update(UserForecastsContract.OUTSIDE_TABLE_NAME, contentValues, selection,selectionArgs);
                break;
            case OUTSIDE_ID:
                db=mOpenHelper.getWritableDatabase();
                long timingsId = UserForecastsContract.getOutsideId(uri);

                selectionCriteria= UserForecastsContract.Columns._ID+" = " + timingsId;
                if((selection!=null)&& (selection.length()>0)) {
                    selectionCriteria+= "AND ("+selection+ ")";
                }
                count = db.update(UserForecastsContract.OUTSIDE_TABLE_NAME,contentValues,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);

        }
        if(count>0){
            getContext().getContentResolver().notifyChange(uri,null);
            Log.d(TAG, "update: setting notifyChange with " + uri);
        } else{
            Log.d(TAG, "update: nothing changed");
        }
        Log.d(TAG, "update: returning" + count);
        return count;
    }
}
