package com.errorplayer.lala_weather.gson;

/**
 * Created by linze on 2017/7/8.
 */

public class AQIInfo {
    public AQICity city;


    public class AQICity
    {
        public String qlty;

        public String pm25;
    }
}
