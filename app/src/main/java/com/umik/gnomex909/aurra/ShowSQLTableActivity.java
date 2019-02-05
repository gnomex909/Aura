package com.umik.gnomex909.aurra;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Gnomex on 08.12.2017.
 * Aktywność, która ma za zadanie wyświetlać dane z tabel SQL, umożliwiać usuwanie i dodawanie danych do nich.
 * Jest to opcja głównie służaca debugowaniu i testom, jednak ma sens również w wypadku użytkownika
 */
public class ShowSQLTableActivity extends AppCompatActivity implements SqlTableViewAdapter.OnSQLClikckListener, AppDialog.DialogEvents {
    private boolean outsideTable = false;
    private Cursor mCursor;
    private SqlTableViewAdapter mSqlTableViewAdapter;
    private String mSelectionArgs[] = null;
    private String mSelection = null;
    private String[] projection;
    public static final int DIALOG_ADAPTATION = 2;
    FloatingActionButton mFloatingActionButton = null;

    @Override
    public void onDeleteClick(SqlTableElement sqlTableElement) {
        Toast.makeText(this, "Usunięto wpis z tablicy", Toast.LENGTH_SHORT).show();
        Long sqlId = sqlTableElement.getId();
        if (outsideTable){
            getContentResolver().delete((UserForecastsContract.buildOutsideUri(sqlId)), null,null);
        }
        else
        getContentResolver().delete((UserForecastsContract.buildTaskUri(sqlId)), null,null);
        changeCursor();
        mSqlTableViewAdapter.swapCursor(mCursor);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sqltable);
        AddForecast.inOutTable = false;
        changeCursor();
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID, DIALOG_ADAPTATION);
                args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.adaptation_message));
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sql_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSqlTableViewAdapter = new SqlTableViewAdapter(mCursor,(SqlTableViewAdapter.OnSQLClikckListener) this,outsideTable);
        recyclerView.setAdapter(mSqlTableViewAdapter);

    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        final OutsideDataAdapter outsideDataAdapter = new OutsideDataAdapter(this);
        if(mSelectionArgs == null){
            outsideDataAdapter.fullAdaptation();
        }
        else{
            outsideDataAdapter.oneDayAdaptation(mSelectionArgs[0]);
        }
        changeCursor();
        mSqlTableViewAdapter.swapCursor(mCursor);

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCanceled(int dialogId) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        changeCursor();
        mSqlTableViewAdapter.swapCursor(mCursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sql, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.sql_all);
        menuItem.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sql_all) {
            mSelectionArgs = null;
            mSelection = null;
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        } else if (id == R.id.sql_monday){
            mSelectionArgs = new String[]{"Monday"};
            mSelection = UserForecastsContract.Columns.FORECAST_DAY +"=?";
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        }
        else if (id == R.id.sql_tuesday){
            mSelectionArgs = new String[]{"Tuesday"};
            mSelection = UserForecastsContract.Columns.FORECAST_DAY +"=?";
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        }
        else if (id == R.id.sql_wednesday){
            mSelectionArgs = new String[]{"Wednesday"};
            mSelection = UserForecastsContract.Columns.FORECAST_DAY +"=?";
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        }
        else if (id == R.id.sql_thursday){
            mSelectionArgs = new String[]{"Thursday"};
            mSelection = UserForecastsContract.Columns.FORECAST_DAY +"=?";
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        }
        else if (id == R.id.sql_friday){
            mSelectionArgs = new String[]{"Friday"};
            mSelection = UserForecastsContract.Columns.FORECAST_DAY +"=?";
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        }
        else if (id == R.id.sql_saturday){
            mSelectionArgs = new String[]{"Saturday"};
            mSelection = UserForecastsContract.Columns.FORECAST_DAY +"=?";
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        }
        else if (id == R.id.sql_sunday){
            mSelectionArgs = new String[]{"Sunday"};
            mSelection = UserForecastsContract.Columns.FORECAST_DAY +"=?";
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);
            if (!item.isChecked())
                item.setChecked(true);
            return true;
        }
        else if (id == R.id.sql_add_element){
            Intent intent = new Intent(this, AddForecast.class);
            startActivity(intent);

            return true;
        }
        else if (id == R.id.sql_change_table){
            if(outsideTable){
                outsideTable=false;
                mSqlTableViewAdapter.setDateUsed(false);
                AddForecast.inOutTable = false;
                Toast.makeText(this, "Zmiana tablicy na używaną przez aplikację", Toast.LENGTH_SHORT).show();
            }
            else{
                outsideTable=true;
                mSqlTableViewAdapter.setDateUsed(true);
                AddForecast.inOutTable = true;
                Toast.makeText(this, "Zmiana tablicy na zawierającą surowe dane", Toast.LENGTH_SHORT).show();
            }
            changeCursor();
            mSqlTableViewAdapter.swapCursor(mCursor);

        }

        return super.onOptionsItemSelected(item);
    }
    private void changeCursor(){
        if(outsideTable){
            projection = new String[]{UserForecastsContract.Columns._ID, UserForecastsContract.Columns.FORECAST_START, UserForecastsContract.Columns.FORECAST_END, UserForecastsContract.Columns.FORECAST_DAY, UserForecastsContract.Columns.FORECAST_DATE};
            mCursor = this.getContentResolver().query(UserForecastsContract.OUTSIDE_CONTENT_URI, projection, mSelection, mSelectionArgs, UserForecastsContract.Columns.FORECAST_DAY);

        }
        else {
            projection = new String[]{UserForecastsContract.Columns._ID, UserForecastsContract.Columns.FORECAST_DAY, UserForecastsContract.Columns.FORECAST_START, UserForecastsContract.Columns.FORECAST_END};
            mCursor = getContentResolver().query(UserForecastsContract.CONTENT_URI, projection, mSelection, mSelectionArgs, UserForecastsContract.Columns.FORECAST_DAY);
        }
    }
}
