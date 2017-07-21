package com.errorplayer.lala_weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by linze on 2017/7/7.
 */

public class County extends DataSupport {

    private  int id;

    private String countyName;

    private String weatherId;

    private int cityId;

    private String NowTemperature;

    private String NowStatus;

    public String getNowStatus() {
        return NowStatus;
    }

    public void setNowStatus(String nowStatus) {
        NowStatus = nowStatus;
    }

    public String getNowTemperature() {
        return NowTemperature;
    }

    public void setNowTemperature(String nowTemperature) {
        NowTemperature = nowTemperature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
