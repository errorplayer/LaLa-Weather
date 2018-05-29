package com.errorplayer.lala_weather.gson.Juhe_News_Gson;

import com.errorplayer.lala_weather.gson.Forecast;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ErrorPlayer on 2018/5/23.
 */

public class JuheNewsAPIresult {

    public String stat;

    @SerializedName("data")
    public List<JuheNewsItem> JuheNewsItemList;


}
