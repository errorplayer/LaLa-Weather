package com.errorplayer.lala_weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linze on 2017/7/8.
 */

public class SuggestionInfo {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String Info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String Info;
    }

    public class Sport{
        @SerializedName("txt")
        public String Info;
    }


}
