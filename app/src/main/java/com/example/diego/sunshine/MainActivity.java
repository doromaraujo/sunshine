package com.example.diego.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private void showMap(String location) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri.Builder builder = Uri.parse("geo:0,0?").buildUpon();

        builder.appendQueryParameter("q", location)
                .appendQueryParameter("z", "23");

        intent.setData(builder.build());

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String retrieveLocationInfoFromSettings() {

        String locationInfo = null;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        locationInfo = settings.getString(getString(R.string.pref_location_key)
                , getString(R.string.pref_location_default));

        return locationInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);

            this.startActivity(intent);

            return true;

        }  else if (id == R.id.action_view_map) {

            String locationInfo = this.retrieveLocationInfoFromSettings();

            this.showMap(locationInfo);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
