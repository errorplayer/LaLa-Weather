package com.errorplayer.lala_weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by ErrorPlayer on 2018/5/25.
 */

public class CityWeatherStore extends DataSupport {

    private String weatherId;

    private String cond_text;

    private String saving_date;

    private String Temperature;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCond_text() {
        return cond_text;
    }

    public void setCond_text(String cond_text) {
        this.cond_text = cond_text;
    }

    public String getSaving_date() {
        return saving_date;
    }

    public void setSaving_date(String saving_date) {
        this.saving_date = saving_date;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
    }
}
