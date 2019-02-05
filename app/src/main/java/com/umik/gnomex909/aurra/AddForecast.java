package com.umik.gnomex909.aurra;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * Created by Gnomex on 20.11.2017.
 * Klasa zajmująca się aktywnością, która zajmuje się dodawaniem do tabel SQL wartości. Jest to aktywność jedynie do pokazów i debugowania, nie będzie w finalnym produkcie
 */
public class AddForecast extends AppCompatActivity {
    private static final String TAG = "AddForecast";
    Button btnDatePicker, btnStartTimePicker, btnEndTimePicker, btnSave, btnBack;
    EditText txtDate, txtStartTime, txtEndTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mDayOfWeek;
    private Uri contentUri;
    public static boolean inOutTable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forecast);
        Log.d(TAG, "onCreate: InOutTable is " + inOutTable);
        if(inOutTable){
            contentUri=UserForecastsContract.OUTSIDE_CONTENT_URI;
        }
        else
            contentUri=UserForecastsContract.CONTENT_URI;



        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnEndTimePicker = (Button) findViewById(R.id.btn_end_time);
        btnStartTimePicker = (Button) findViewById(R.id.btn_start_time);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnBack = (Button) findViewById(R.id.btn_back);

        txtDate = (EditText) findViewById(R.id.input_date);
        txtStartTime = (EditText) findViewById(R.id.input_start_hour);
        txtEndTime = (EditText) findViewById(R.id.input_end_hour);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (view == btnDatePicker){
                  final Calendar c = Calendar.getInstance();
                  mDay = c.get(Calendar.DAY_OF_MONTH);
                  mMonth = c.get(Calendar.MONTH);
                  mYear = c.get(Calendar.YEAR);


                  DatePickerDialog datePickerDialog = new DatePickerDialog( AddForecast.this,
                          new DatePickerDialog.OnDateSetListener() {

                              @Override
                              public void onDateSet(DatePicker view, int year,
                                                    int monthOfYear, int dayOfMonth) {
                                  SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                                  GregorianCalendar gregorianCalendar = new GregorianCalendar(year,monthOfYear,dayOfMonth -1);
                                  int dayOfWeek = gregorianCalendar.get(gregorianCalendar.DAY_OF_WEEK);
                                  switch(dayOfWeek){
                                      case 1:
                                          mDayOfWeek = "Monday";
                                          break;
                                      case 2:
                                          mDayOfWeek = "Tuesday";
                                          break;
                                      case 3:
                                          mDayOfWeek = "Wednesday";
                                          break;
                                      case 4:
                                          mDayOfWeek = "Thursday";
                                          break;
                                      case 5:
                                          mDayOfWeek = "Friday";
                                          break;
                                      case 6:
                                          mDayOfWeek = "Saturday";
                                          break;
                                      case 7:
                                          mDayOfWeek = "Sunday";
                                          break;
                                      default:
                                          mDayOfWeek = "error";
                                          break;
                                  }
                                  txtDate.setText(mDayOfWeek);
                              }
                          }, mYear, mMonth, mDay);
                  datePickerDialog.show();
              }
              else if (view == btnEndTimePicker || view ==btnStartTimePicker) {
                  final Boolean startTime;
                    if(view ==btnStartTimePicker)
                        startTime = true;
                    else
                        startTime = false;
                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddForecast.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    String mMinute = "" +minute;
                                    if(minute<10){
                                        mMinute = "0" + minute;
                                    }
                                    if(startTime)
                                    txtStartTime.setText(hourOfDay + ":" + mMinute);
                                    else
                                        txtEndTime.setText(hourOfDay + ":" + mMinute);

                                }
                            }, mHour, mMinute, true);
                    timePickerDialog.show();
                }
                else if(view ==btnSave){
                    if(txtStartTime.getText().toString().length() >0 && txtStartTime.getText().toString(). length()>0 && txtStartTime.getText().toString(). length()>0) {
                        ContentResolver contentResolver = getContentResolver();
                        ContentValues values = new ContentValues();

                        values.put(UserForecastsContract.Columns.FORECAST_START, txtStartTime.getText().toString());
                        values.put(UserForecastsContract.Columns.FORECAST_END, txtEndTime.getText().toString());
                        values.put(UserForecastsContract.Columns.FORECAST_DAY, txtDate.getText().toString());
                        contentResolver.insert(contentUri, values);
                        Toast.makeText(AddForecast.this, "Dodano wpis do tablicy", Toast.LENGTH_SHORT).show();
                    }
                    AddForecast.this.finish();

                }
                else if(view ==btnBack ){
                    AddForecast.this.finish();
                }
            }
        };
        btnStartTimePicker.setOnClickListener(onClickListener);
        btnEndTimePicker.setOnClickListener(onClickListener);
        btnDatePicker.setOnClickListener(onClickListener);
        btnSave.setOnClickListener(onClickListener);
        btnBack.setOnClickListener(onClickListener);
    }
}
