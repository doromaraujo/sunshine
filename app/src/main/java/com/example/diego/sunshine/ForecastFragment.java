package com.example.diego.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter = null;

    private String[] weatherData = null;

    private String locationInfo = null;

    private double longitude = 0;
    private double latitude = 0;

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    private final String LOCATION_TAG = "location";

    private static Context context = null;

    private Location location = null;

    public ForecastFragment() {
        this.locationInfo = "20031,br";
    }

    private void retrieveLocationCoordinates() {

        Location location = this.location;

        if (location == null) {

            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.putExtra("enabled", true);
                    startActivity(intent);
                }

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    return;
                }

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null) {
                    Criteria crit = new Criteria();
                    crit.setPowerRequirement(Criteria.POWER_LOW);
                    crit.setAccuracy(Criteria.ACCURACY_FINE);

                    locationManager.requestSingleUpdate(
                            crit, new com.example.diego.sunshine.LocationListener(this)
                            , null);
                }
            }

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }

        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }

    private String retrieveLocationInfoFromSettings() {
        String locationInfo = null;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        locationInfo = settings.getString(getString(R.string.pref_location_key)
                , getString(R.string.pref_location_default));

        if (locationInfo == null || locationInfo.isEmpty()) {
            locationInfo = this.locationInfo;
        }

        return locationInfo;
    }

    private void updateWeatherData() {

        String locationInfo = this.retrieveLocationInfoFromSettings();

        new FetchWeatherTask(this).execute(locationInfo,
                Double.toString(this.longitude),
                Double.toString(this.latitude));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        if (context == null) {
            context = getActivity();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.updateWeatherData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {

//            this.retrieveLocationCoordinates();

            this.updateWeatherData();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.forecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new OnItemClickListener(getActivity()));

        return rootView;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setWeatherData(String[] weatherData) {
        this.weatherData = weatherData;

        if (weatherData != null) {
            this.forecastAdapter.clear();

            this.forecastAdapter.addAll(weatherData);
        }
    }
}
