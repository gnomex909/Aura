package com.umik.gnomex909.aurra;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Gnomex on 13.12.2017.
 * Adapter stworzony dla RecyclerView, by w wybrany przez nas sposób wyświetlał elementy tabeli SQL [używając layoutu sql_item dla nich), zamiast domyślnych opcji
 */

public class SqlTableViewAdapter extends RecyclerView.Adapter<SqlTableViewAdapter.SqlTableElementViewHolder> {
private static final String TAG = "WeatherRecyclerViewAdap";

private OnSQLClikckListener mListener;
private Cursor mCursor;
private boolean mDateUsed;

private Context mContext;

interface OnSQLClikckListener{
    void onDeleteClick(SqlTableElement sqlTableElement);
}

public SqlTableViewAdapter(Cursor cursor, OnSQLClikckListener listener, boolean dateUsed){
       // mSqlTableElementList = sqlTableElementList;
        mListener = listener;
        mDateUsed = dateUsed;
        mCursor = cursor;
        }
@Override
public SqlTableElementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requestesd");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sql_item, parent,false);
        return new SqlTableElementViewHolder(view);
        }

@Override
public void onBindViewHolder(SqlTableElementViewHolder holder, int position) {
    Log.d(TAG, "onBindViewHolder: starts");
    if ((mCursor==null) || (mCursor.getCount()== 0)) {
        holder.sqlDate.setText("a");
        holder.sqlDay.setText("b");
        holder.sqlEnd.setText("c");
        holder.sqlStart.setText("d");
    } else {
        if(!mCursor.moveToPosition(position)){
            throw new IllegalStateException("Coun't move cursor to position " + position);
        }
        final SqlTableElement sqlTableElement = new SqlTableElement(mCursor.getString(mCursor.getColumnIndex(UserForecastsContract.Columns.FORECAST_START)),
                mCursor.getString(mCursor.getColumnIndex(UserForecastsContract.Columns.FORECAST_END)),
                mCursor.getString(mCursor.getColumnIndex(UserForecastsContract.Columns.FORECAST_DAY)));
        sqlTableElement.setId(mCursor.getLong(mCursor.getColumnIndex(UserForecastsContract.Columns._ID)));
        holder.sqlDay.setText(sqlTableElement.getDayOfWeek());
        holder.sqlEnd.setText(sqlTableElement.getEndTime());
        holder.sqlStart.setText(sqlTableElement.getStartTime());
        if(mDateUsed){
            sqlTableElement.setDate(mCursor.getString(mCursor.getColumnIndex(UserForecastsContract.Columns.FORECAST_DATE)));
            holder.sqlDate.setText(sqlTableElement.getDate());
        }
        else
            holder.sqlDate.setText("N/D");

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDeleteClick(sqlTableElement);
            }
        };
        holder.deleteButton.setOnClickListener(onClickListener);


    }
};

@Override
public int getItemCount() {
    if((mCursor == null) || (mCursor.getCount() == 0)) {
        return 1;
    }else{
        return mCursor.getCount();
    }
}

//public SqlTableElement getSqlElement(int position){
//        return ((mSqlTableElementList!=null)&& (mSqlTableElementList.size()!= 0) ? mSqlTableElementList.get(position) : null);
//        }

    Cursor swapCursor(Cursor newCursor){
        if(newCursor == mCursor){
            return null;
        }
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null){
            notifyDataSetChanged();
        }
        else{
            notifyItemRangeRemoved(0,getItemCount());
        }
        return oldCursor;
    }
static class SqlTableElementViewHolder extends RecyclerView.ViewHolder{
    private static final String TAG = "SqlTableElementViewHold";
    TextView sqlStart = null;
    TextView sqlEnd = null;
    TextView sqlDate = null;
    TextView sqlDay = null;
    Button deleteButton = null;

    public SqlTableElementViewHolder(View itemView){
        super (itemView);
        Log.d(TAG, "UserForecastViewHolder: starts");
        this.sqlStart = (TextView) itemView.findViewById(R.id.sql_start);
        this.sqlEnd = (TextView) itemView.findViewById(R.id.sql_end);
        this.sqlDay = (TextView) itemView.findViewById(R.id.sql_day);
        this.sqlDate = (TextView) itemView.findViewById(R.id.sql_date);
        this.deleteButton = (Button) itemView.findViewById(R.id.delete_button);
    }
}

    public void setDateUsed(boolean dateUsed) {
        mDateUsed = dateUsed;
    }
}
