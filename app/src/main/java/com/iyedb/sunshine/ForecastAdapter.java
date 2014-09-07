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

        View view;

        if (viewType == VIEW_TYPE_TODAY) {

            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast_today
                    , parent, false);
            TodayViewHolder holder = new TodayViewHolder(view);
            view.setTag(holder);
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {

            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast5,
                    parent, false);
            ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            return null;

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int viewType = getItemViewType(cursor.getPosition());
        boolean isMetric = Utility.isMetric(context);
        int weatherConditionResId = -1;

        // Get the common fields
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        int weatherConditionId = Integer.valueOf(cursor.getString(ForecastFragment.COL_WEATHER_WEATHER_ID));

        if (viewType == VIEW_TYPE_TODAY) {
            weatherConditionResId = Utility.getArtResourceForWeatherCondition(weatherConditionId);
            TodayViewHolder viewHolder = (TodayViewHolder) view.getTag();

            viewHolder.iconView.setImageResource(weatherConditionResId);

            viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

            viewHolder.forecastView.setText(description);

            viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

            viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));

            String location = cursor.getString(ForecastFragment.COL_LOCATION_SETTING);

            viewHolder.locationView.setText(location);

        } else {

            weatherConditionResId = Utility.getIconResourceForWeatherCondition(weatherConditionId);

            ViewHolder viewHolder = (ViewHolder) view.getTag();

            viewHolder.iconView.setImageResource(weatherConditionResId);

            viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

            viewHolder.forecastView.setText(description);

            viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

            viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));

        }

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

    public static class TodayViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView forecastView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView locationView;


        public TodayViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            locationView = (TextView) view.findViewById(R.id.list_item_location_textview);
        }
    }
}