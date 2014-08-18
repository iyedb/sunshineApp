package com.iyedb.sunshine;

/**
 * Created by iyed on 02/08/2014.
 */


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */

public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;



    private boolean mUseTodayViewType = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setmUseTodayViewType(boolean mUseTodayViewType) {
        this.mUseTodayViewType = mUseTodayViewType;
    }

    @Override
    public int getViewTypeCount() {

        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayViewType) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY)
            layoutId = R.layout.list_item_forecast_today;
        else if (viewType == VIEW_TYPE_FUTURE_DAY)
            layoutId = R.layout.list_item_forecast5;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);


        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read weather icon ID from cursor
        //int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        int viewType = getItemViewType(cursor.getPosition());


        int weather_cond_id = Integer.valueOf(cursor.getString(ForecastFragment.COL_WEATHER_WEATHER_ID));
        int weather_cond_res_id = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            weather_cond_res_id = Utility.getArtResourceForWeatherCondition(weather_cond_id);
        } else {
            weather_cond_res_id = Utility.getIconResourceForWeatherCondition(weather_cond_id);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Use placeholder image for now
        //ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        //iconView.setImageResource(R.drawable.ic_launcher_);
        viewHolder.iconView.setImageResource(weather_cond_res_id);

        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        //TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        //dateView.setText(Utility.getFriendlyDayString(context, dateString));
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        //TextView descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        //descriptionView.setText(description);
        viewHolder.forecastView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);

        //TextView tvHighTemp = (TextView) view.findViewById(R.id.list_item_high_textview);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);

        //TextView tvLowTemp = (TextView) view.findViewById(R.id.list_item_low_textview);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));


    }


    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView forecastView;
        public final TextView highTempView;
        public final TextView lowTempView;


        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}