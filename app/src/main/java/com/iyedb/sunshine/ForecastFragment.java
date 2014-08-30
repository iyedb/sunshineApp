package com.iyedb.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.iyedb.sunshine.data.WeatherContract;
import com.iyedb.sunshine.data.WeatherContract.LocationEntry;
import com.iyedb.sunshine.data.WeatherContract.WeatherEntry;

import java.util.Date;


/**
 * Created by iyed on 18/07/2014.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_WEATHER_ID = 5;
    public static final int COL_LOCATION_SETTING = 6;

    private final static String TAG = ForecastFragment.class.getSimpleName();
    private static final int FORECAST_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_LOCATION_SETTING
    };

    private String mLocation;
    boolean mUseTodayView;

    public void useTodayView(boolean flag) {
        mUseTodayView = flag;
        if (mForecastAdapter != null) {
            mForecastAdapter.setmUseTodayViewType(mUseTodayView);
        }
    }

    private ForecastAdapter mForecastAdapter;
    TextView listViewHeaderTv;
    ListView mListView;
    private int mSelectedItemPosition = ListView.INVALID_POSITION;
    private final String POSITION_KEY = "postion";

    public ForecastFragment() {
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate() called");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        mForecastAdapter.setmUseTodayViewType(mUseTodayView);
        /*
        mForecastAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_forecast5,
                null,
                new String[]{
                        WeatherEntry.COLUMN_DATETEXT,
                        WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherEntry.COLUMN_MIN_TEMP,
                },
                new int[]{
                        R.id.list_item_date_textview,
                        R.id.list_item_forecast_textview,
                        R.id.list_item_high_textview,
                        R.id.list_item_low_textview
                },
                0
        );

        mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                boolean isMetric = Utility.isMetric(getActivity());
                switch (columnIndex) {
                    case COL_WEATHER_MAX_TEMP:
                    case COL_WEATHER_MIN_TEMP: {
                        // we have to do some formatting and possibly a conversion
                        ((TextView) view).setText(Utility.formatTemperature(
                                cursor.getDouble(columnIndex), isMetric));
                        return true;
                    }
                    case COL_WEATHER_DATE: {
                        String dateString = cursor.getString(columnIndex);

                        TextView dateView = (TextView) view;
                        dateView.setText(Utility.formatDate(dateString));

                        return true;
                    }
                }
                return false;
            }
        });
        */
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);

        //listViewHeaderTv = (TextView) inflater.inflate(R.layout.list_item_forecast_location,
        //        null, false);

        //listViewHeaderTv.setText(Utility.getPreferredLocation(getActivity()));
        //mListView.addHeaderView(listViewHeaderTv);
        mListView.setAdapter(mForecastAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                if (position == 0) {
                    openLocationInMap();
                } else {
                    ForecastAdapter adapter =
                            (ForecastAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter();

                    Cursor cursor = adapter.getCursor();

                    if (cursor != null && cursor.moveToPosition(position - 1)) {
                        ((Callback) getActivity()).onItemSelected(cursor.getString(COL_WEATHER_DATE));
                    }
                    mSelectedItemPosition = position;
                }
                */


                ForecastAdapter adapter =
                        (ForecastAdapter) parent.getAdapter();

                Cursor cursor = adapter.getCursor();


                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity()).onItemSelected(cursor.getString(COL_WEATHER_DATE));
                }

                //view.findViewById(R.id.list_item_more_info).setVisibility(View.VISIBLE);

                mSelectedItemPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mSelectedItemPosition = savedInstanceState.getInt(POSITION_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (mSelectedItemPosition != ListView.INVALID_POSITION)
            outState.putInt(POSITION_KEY, mSelectedItemPosition);
    }

    @Override
    public void onResume() {

        super.onResume();

        Log.d(TAG, "onResume");
        //listViewHeaderTv.setText(Utility.getPreferredLocation(getActivity()));

        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            Log.d(TAG, "onResume: restartLoader()");
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
        else {
            Log.d(TAG,
                    "onResume: no restartLoader(), " + Utility.getPreferredLocation(getActivity()));
        }

    }

    private void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(TAG, "onCreateLoader");
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());

        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mForecastAdapter.swapCursor(cursor);
        int idx = cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_LOCATION_SETTING);
        int idx_weather_id = cursor.getColumnIndexOrThrow(WeatherEntry.COLUMN_WEATHER_ID);

        Log.d(TAG, "COLUMN_WEATHER_ID = " + Integer.toString(idx_weather_id));
        Log.d(TAG, "COLUMN_LOCATION_SETTING = " + Integer.toString(idx));
        if (cursor.getCount() != 0)
            Log.d(TAG, "onLoadFinished: loaded some data" );
        else
            Log.d(TAG, "onLoadFinished: no data. Database Empty?!");

        if (mSelectedItemPosition != ListView.INVALID_POSITION)
            mListView.smoothScrollToPosition(mSelectedItemPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        mForecastAdapter.swapCursor(null);
    }

    private void openLocationInMap() {

        String location = Utility.getPreferredLocation(getActivity());

        Uri geoLocation = Uri.parse("geo:0:0?").buildUpon()
                .appendQueryParameter("q", location).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Could not show " + location + " on a map");
        }
    }

    interface Callback {
        public void onItemSelected(String date);
    }
}
