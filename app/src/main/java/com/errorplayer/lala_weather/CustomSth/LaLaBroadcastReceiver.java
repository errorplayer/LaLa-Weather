package com.errorplayer.lala_weather.CustomSth;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class LaLaBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context,Intent intent)
    {
        String latitude = intent.getStringExtra("weather_latitude") + "";
        String longitude = intent.getStringExtra("weather_longitude") +"";

        Log.d("broadcast11", latitude+","+longitude);
    }
}
