package com.example.diego.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by diego on 25/04/2016.
 */
public class OnItemClickListener implements AdapterView.OnItemClickListener {

    private Activity currentActivity = null;

    public OnItemClickListener(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch(view.getId()) {
            case R.id.list_item_forecast_textview:
                String forecast = (String) parent.getAdapter().getItem(position);

                Intent intent = new Intent(this.currentActivity, DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);

                this.currentActivity.startActivity(intent);

                //Toast.makeText(this.currentActivity, forecast, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
