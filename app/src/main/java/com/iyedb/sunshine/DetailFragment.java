package com.iyedb.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyedb.sunshine.data.WeatherContract;

/**
* Created by iyed on 03/08/2014.
*/
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_WIND_SPEED = 6;
    public static final int COL_WEATHER_PRESSURE =7;
    public static final int COL_WEATHER_WEATHER_ID = 8;
    public static final int COL_WEATHER_DEGREES = 9;
    public static final int COL_LOCATION_SETTING = 10;


    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };
    private String mForecastStr;

    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mForecastView;
    private ImageView mWeatherImgView;
    private TextView mLocationTextView;

    private static final String LOCATION_KEY = "location";

    String mLocation;
    String mDateStr;
    ShareActionProvider mShareActionProvider;




    public DetailFragment() {

        setHasOptionsMenu(true);
    }

    public static DetailFragment newInstance(String date) {
        DetailFragment fragment = new DetailFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(DetailActivity.DATE_KEY, date);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDateStr = arguments.getString(DetailActivity.DATE_KEY);
        }

        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }


        mDateView = (TextView)rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView)rootView.findViewById(R.id.detail_day_textview);
        mHumidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        mPressureView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);
        mWindView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
        mHighTempView = (TextView)rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView)rootView.findViewById(R.id.detail_low_textview);
        mForecastView  = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mWeatherImgView = (ImageView)rootView.findViewById(R.id.detail_weather_imageview);
        mLocationTextView = (TextView) rootView.findViewById(R.id.detail_location_textView);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OnActivityCreated");
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY) &&
                mLocation != null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            Log.d(LOG_TAG, "loader restarted");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "OnPause");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(LOG_TAG, "onCreateLoader");
        mLocation = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        Uri weatherForLocationAndDateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                mLocation, mDateStr);

        Log.v(LOG_TAG, weatherForLocationAndDateUri.toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationAndDateUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.d(LOG_TAG, "onLoadFinished");
        if (!cursor.moveToFirst()) {
            return;
        }

        boolean isMetric = Utility.isMetric(getActivity());

        String dbDate = cursor.getString(COL_WEATHER_DATE);
        String dateString = Utility.formatDate(dbDate);

        mDateView.setText(dateString);

        mFriendlyDateView.setText(Utility.getDayName(getActivity(), dbDate));


        String weatherDescription = cursor.getString(COL_WEATHER_DESC);
        mForecastView.setText(weatherDescription);


        String high = Utility.formatTemperature(getActivity(), cursor.getDouble(COL_WEATHER_MAX_TEMP),
                isMetric);
        mHighTempView.setText(high);

        String low = Utility.formatTemperature(getActivity(), cursor.getDouble(COL_WEATHER_MIN_TEMP),
                isMetric);
        mLowTempView.setText(low);

        String humidity = String.format(getActivity().getString(R.string.format_humidity), cursor.getDouble(COL_WEATHER_HUMIDITY));
        mHumidityView.setText(humidity);

        String pressure = String.format(getActivity().getString(R.string.format_pressure), cursor.getDouble(COL_WEATHER_PRESSURE));
        mPressureView.setText(pressure);

        String windSpeed = Utility.getFormattedWind(getActivity(),
                cursor.getFloat(COL_WEATHER_WIND_SPEED), cursor.getFloat(COL_WEATHER_DEGREES));
        mWindView.setText(windSpeed);

        int weather_cond_id = Integer.valueOf(cursor.getString(COL_WEATHER_WEATHER_ID));
        int weather_cond_res_id = Utility.getArtResourceForWeatherCondition(weather_cond_id);
        mWeatherImgView.setImageResource(weather_cond_res_id);


        String location = cursor.getString(COL_LOCATION_SETTING);
        mLocationTextView.setText(location);

        // We still need this for the share intent
        mForecastStr = String.format("%s - %s - %s - %s/%s",
                mLocation, dateString, weatherDescription, high, low);

        Log.d(LOG_TAG, "Forecast String: " + mForecastStr);

        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(createShareForecastIntent());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        int a = Intent.FLAG_ACTIVITY_CLEAR_TOP;
        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }
}
