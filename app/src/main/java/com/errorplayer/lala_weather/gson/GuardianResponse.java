package com.errorplayer.lala_weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Zeyang on 2017/8/10.
 */

public class GuardianResponse {
    public String status;

    public String userTier;

    public long  total;

    @SerializedName("results")
    public List<GuardianNewsItem> GuadianNewsItemList;
}
