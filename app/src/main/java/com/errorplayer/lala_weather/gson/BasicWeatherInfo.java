package com.errorplayer.lala_weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linze on 2017/7/8.
 */

public class BasicWeatherInfo {
    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String weatherId;

    //public Update update;

    /*public class Update
    {
        @SerializedName("loc")
        public String updateTime;
    }*/


}
