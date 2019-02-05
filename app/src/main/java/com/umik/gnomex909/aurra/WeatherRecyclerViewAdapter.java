package com.umik.gnomex909.aurra;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.umik.gnomex909.aurra.MainActivity.mLastLocation;

/**
 * Created by Gnomex on 03.12.2017.
 * Adapter Recyclerview stworzony dla Main Activity. Ma za zadanie wyświetlać dane UserForecast w wybrany przez nas sposób (weather_item) zamiast w domyślny.
 */

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.UserForecastViewHolder> {
    private static final String TAG = "WeatherRecyclerViewAdap";
    private List<UserForecast> mUserForecasts;
    private Context mContext;

    public WeatherRecyclerViewAdapter(Context context, List<UserForecast> userForecastList){
        mUserForecasts = userForecastList;
        mContext = context;
    }
    @Override
    public UserForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requestesd");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent,false);
        return new UserForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserForecastViewHolder holder, int position) {
        if((mUserForecasts==null) && (mUserForecasts.size() == 0)){
            holder.weatherTime.setText(mContext.getString(R.string.weather_item_time,"0:00:00","0:00;00"));
            holder.weatherPressure.setText(mContext.getString(R.string.weather_pressure,"błędne"));
            holder.weatherTempMin.setText("0");
            holder.weatherTempMax.setText("0");
            holder.weatherWindSpeed.setText("0");
            holder.weatherDate.setText("Brak prognozy pogody");
            holder.weatherType.setText("ERROR");
        }
        else{
            UserForecast userForecast = mUserForecasts.get(position);
            holder.weatherTime.setText(mContext.getString(R.string.weather_item_time,userForecast.getStartTime(),userForecast.getEndTime()));
            String cityName=null;
            Geocoder gcd = new Geocoder(mContext,
                    Locale.getDefault());
            List<Address>  addresses;
            if(mLastLocation != null)
            try {
                addresses = gcd.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName=addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(null != cityName){
                holder.weatherLocation.setText(cityName);
            }
            else{
                holder.weatherLocation.setText("Warszawa");
            }
            holder.weatherTempMin.setText(userForecast.getMinTemperature()+" [°C]");
            holder.weatherTempMax.setText(userForecast.getMaxTemperature()+" [°C]");
            holder.weatherWindSpeed.setText(userForecast.getWindSpeed()+"");
            holder.weatherDate.setText(userForecast.getStartDate());
            if(userForecast.isGoodPressure()){
                holder.weatherPressure.setText(mContext.getString(R.string.weather_pressure, "korzystne"));
            }
            else
                holder.weatherPressure.setText(mContext.getString(R.string.weather_pressure, "niekorzystne"));
            switch(userForecast.getWeatherType()){
                case 1:
                    holder.weatherType.setText("Słońce");
                    break;
                case 2:
                    holder.weatherType.setText("Chmury");
                    break;
                case 3:
                    holder.weatherType.setText("Mgła");
                    break;
                case 4:
                    holder.weatherType.setText("Mżawka");
                    break;
                case 5:
                    holder.weatherType.setText("Śnieg");
                    break;
                case 6:
                    holder.weatherType.setText("Deszcz");
                    break;
                case 7:
                    holder.weatherType.setText("Burza");
                    break;
                case 8:
                    holder.weatherType.setText("Extreme");
                    break;
                case 9:
                    holder.weatherType.setText("Błąd");
                    break;
            }
        }
    }
    void loadNewData(List<UserForecast>  newForecast){
        mUserForecasts = newForecast;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return ((mUserForecasts!=null)&& (mUserForecasts.size()!= 0) ? mUserForecasts.size() : 0);
    }
    public UserForecast getUserForecast(int position){
        return ((mUserForecasts!=null)&& (mUserForecasts.size()!= 0) ? mUserForecasts.get(position) : null);
    }
    static class UserForecastViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "UserForecastViewHolder";
        TextView weatherTempMin = null;
        TextView weatherTempMax = null;
        TextView weatherType = null;
        TextView weatherWindSpeed = null;
        TextView weatherPressure = null;
        TextView weatherDate = null;
        TextView weatherTime = null;
        TextView weatherLocation = null;
        public UserForecastViewHolder(View itemView){
            super (itemView);
            Log.d(TAG, "UserForecastViewHolder: starts");
            this.weatherTempMin = (TextView) itemView.findViewById(R.id.weather_temp_min);
            this.weatherTempMax = (TextView) itemView.findViewById(R.id.weather_temp_max);
            this.weatherType = (TextView) itemView.findViewById(R.id.weather_type);
            this.weatherWindSpeed = (TextView) itemView.findViewById(R.id.weather_wind);
            this.weatherPressure = (TextView) itemView.findViewById(R.id.weather_pressure);
            this.weatherDate = (TextView) itemView.findViewById(R.id.weather_date);
            this.weatherTime = (TextView) itemView.findViewById(R.id.weather_time);
            this.weatherLocation = (TextView) itemView.findViewById(R.id.weather_localisation);
        }
    }


}
