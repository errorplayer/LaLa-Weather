package com.errorplayer.lala_weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by linze on 2017/7/8.
 */

public class WeatherInfo {

    public String status;

    public BasicWeatherInfo basic;

    //public AQIInfo aqi;

    public NowWeatherInfo now;

    //public SuggestionInfo lifestyle;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastsList;
}
