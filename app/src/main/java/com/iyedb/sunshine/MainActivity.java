package com.iyedb.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final String TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
            else {
                Log.d(TAG, "savedInstanceState not null");
            }
        } else {
            mTwoPane = false;
        }
        ForecastFragment frag = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        frag.useTodayView(!mTwoPane);
        Log.d(TAG, "OnCreate called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_show_on_map) {
            openLocationInMap();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openLocationInMap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String location = sharedPreferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_val));

        Uri geoLocation = Uri.parse("geo:0:0?").buildUpon()
                .appendQueryParameter("q", location).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Could not show " + location + " on a map");
        }


    }


    @Override
    public void onItemSelected(String date) {

        if (mTwoPane) {
            DetailFragment fragment = DetailFragment.newInstance(date);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_to_left, R.anim.slide_out_to_left);
        }


    }
}