package com.example.diego.sunshine;

import android.location.Location;
import android.os.Bundle;

/**
 * Created by diego on 25/04/2016.
 */
public class LocationListener implements android.location.LocationListener {

    private ForecastFragment forecastFragment;

    public LocationListener(ForecastFragment location) {

        this.forecastFragment = location;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.forecastFragment.setLocation(location);
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
}
