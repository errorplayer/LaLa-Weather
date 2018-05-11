package com.errorplayer.lala_weather.util;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by linze on 2017/7/7.
 */

public class HttpUtil {

    private static String TAG = "HU";

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "successful 1! ");
        Request request = new Request.Builder().url(address).build();
        Log.d(TAG, "successful 2! ");
        client.newCall(request).enqueue(callback);
        //Response response = client.newCall(request).execute();

        Log.d(TAG, "success3!");
    }
}
