package com.errorplayer.lala_weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linze on 2017/7/8.
 */

public class Forecast {



    //@SerializedName("tmp")
    //public Temperature temperature;


    public String date;
    public String tmp_max;
    public String tmp_min;

    @SerializedName("cond_txt_d")
    public String condition_info;

    //public class More{
     //   @SerializedName("cond_txt_d")
     //   public String info;
    //}
}
