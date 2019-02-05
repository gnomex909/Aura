package com.umik.gnomex909.aurra;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gnomex on 25.11.2017.
 * Klasa służąca do pobrania danych z adresu HTTP i zwrócenia go jako string
 */
enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK};


class GetRawData extends AsyncTask<String,Void,String> {
    private static final String TAG = "GetRawData";
    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallback;

    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadStatus status);
    }
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: started");
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        if(strings== null){
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return  null;
        }
        try{
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was " + response);

            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while(null != ( line = reader.readLine())){
                result.append(line).append("\n");
            }
            mDownloadStatus = DownloadStatus.OK;
            Log.d(TAG, "doInBackground: Result is" + result);
            return  result.toString();
        //catch(MalformedURLException e) {
         //   Log.e(TAG, "doInBackground: Invalid URL");
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IO Exception reading data: "+ e.getMessage());
        }catch(SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception. Needs permission?");
        }finally {
            if(connection!= null){
                connection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                    Log.e(TAG, "doInBackground: Error closing stream "+ e.getMessage());
                }
            }
        }
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    public GetRawData(OnDownloadComplete callback) {
        this.mDownloadStatus = DownloadStatus.IDLE;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(String s) {
//        Log.d(TAG, "onPostExecute: parameter = " + s);
        if(mCallback != null){
            mCallback.onDownloadComplete(s, mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");
    }
    void runInSameThread(String s){
        Log.d(TAG, "runInSameThread: starts");
        if(mCallback!=null){
            String result = doInBackground(s);
            mCallback.onDownloadComplete(result,mDownloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");
    }
}
