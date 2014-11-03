package com.iyedb.sunshine;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.iyedb.sunshine.sync.SunshineSyncAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final String TAG = MainActivity.class.getSimpleName();
    private LocationManager mLocationManager;
    private boolean mTwoPane = false;
    private String mProvider;
    private Geocoder mGeoCoder;
    private LocationListener mLocationListener;

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
            } else {
                Log.d(TAG, "savedInstanceState not null");
            }
        } else {
            mTwoPane = false;
        }

        ForecastFragment frag = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        frag.useTodayView(!mTwoPane);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mGeoCoder = new Geocoder(getApplication(), Locale.getDefault());

        // Get a good enough location provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);
        criteria.setBearingRequired(false);

        mProvider = mLocationManager.getBestProvider(criteria, true);
        Log.d(TAG, "using provider " + mProvider);
        Log.d(TAG, "OnCreate called");

    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(mLocationListener);
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
        SunshineSyncAdapter.initializeSyncAdapter(getApplicationContext());

    }

    void setupLocationListener() {

        int timeDelta = 1000 * 60 * 120;
        int distanceDelta = 2000;

        mLocationListener =
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        try {
                            List<Address> addresses = mGeoCoder.getFromLocation(location.getLatitude(),
                                    location.getLongitude(), 1);

                            String locality = addresses.get(0).getLocality();

                            String savedLocality = Utility.getPreferredLocation(getApplicationContext());

                            Log.d(TAG, "Location updated: " + locality);

                            if (!locality.equals(savedLocality)) {
                                Log.d(TAG, "Location changed");
                                Utility.saveLocationPref(getApplicationContext(), locality);
                                SunshineSyncAdapter.syncImmediately(getApplicationContext());
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };


        mLocationManager.requestLocationUpdates(mProvider, timeDelta,
                distanceDelta, mLocationListener);
    }

    String getLastLocationFix() throws IOException {

        Location location = mLocationManager.getLastKnownLocation(mProvider);

        String locality = null;
            List<Address> addresses = mGeoCoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0)
                locality = addresses.get(0).getLocality();
            return locality;
    }

    @Override
    protected void onStart() {
        super.onStart();

        String lastLocation = null;


        try {
            lastLocation = getLastLocationFix();
            Log.d(TAG, "Last location fix : " + lastLocation);
        } catch (IOException e) {
            lastLocation = new String();
            e.printStackTrace();
        }

        Utility.saveLocationPref(getApplicationContext(), lastLocation);

        setupLocationListener();


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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String date) {

        if (mTwoPane) {
            DetailFragment fragment = DetailFragment.newInstance(date);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_to_left, R.anim.slide_out_to_left);
        }
    }


}