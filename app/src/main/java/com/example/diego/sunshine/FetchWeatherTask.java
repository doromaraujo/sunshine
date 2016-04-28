package com.example.diego.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by diego on 20/04/2016.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private ForecastFragment fragment;

    public FetchWeatherTask(ForecastFragment fragment) {
        this.fragment = fragment;
    }

    private String retrieveUnitFormatFromSettings()
    {
        String unit = null;

        Context context = this.fragment.getContext();

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);

        unit = settings.getString(context.getString(R.string.pref_units_key)
                , context.getString(R.string.pref_unit_entry_metric));

        return unit;
    }

    @Override
    @Nullable
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String locationInfo = params[0];
        String longitude = null;
        String latitude = null;

//        if (params.length == 3) {
//            longitude = params[1];
//            latitude = params[2];
//        }

        String requestResult = null;
        String line;

        URL url = null;

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("appId", BuildConfig.OPEN_WEATHER_APP_ID)
                .appendQueryParameter("apiKey", BuildConfig.OPEN_WEATHER_API_KEY)
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("cnt", "7");

        if (latitude != null && longitude != null) {
            builder.appendQueryParameter("lat", latitude)
                    .appendQueryParameter("lon", longitude);
        }
        else {
            builder.appendQueryParameter("q", locationInfo);
        }

        try {
            url = new URL(builder.build().toString());
        }
        catch (MalformedURLException e) {
            Log.e(this.LOG_TAG, "Wrong weather request URL", e);
        }

        if (url == null) {
            return null;
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream stream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (stream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\r\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            requestResult = buffer.toString();
        }
        catch (Exception e) {
            Log.e(this.LOG_TAG, "Error while retrieving data from remote weather service",
                    e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e) {
                    Log.e(this.LOG_TAG, "Error while closing the reader", e);
                }
            }
        }

        WeatherDataParser weatherParser = new WeatherDataParser();

        String[] result = null;

        String temperatureUnit = this.retrieveUnitFormatFromSettings();

        try {
            result = weatherParser.getWeatherDataFromJson(requestResult, 7, temperatureUnit);
        }
        catch (JSONException e) {
            Log.e(this.LOG_TAG, "error while parsing weather data", e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String[] s) {
        this.fragment.setWeatherData(s);
    }
}
