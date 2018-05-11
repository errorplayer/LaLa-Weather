package com.errorplayer.lala_weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linze on 2017/7/8.
 */

public class NowWeatherInfo {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String more;

    /*public class More{
        @SerializedName("txt")
        public String info;
    }*/
}
