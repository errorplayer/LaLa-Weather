package com.errorplayer.lala_weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.errorplayer.lala_weather.db.City;
import com.errorplayer.lala_weather.db.County;
import com.errorplayer.lala_weather.db.Province;
import com.errorplayer.lala_weather.gson.GuardianNewsItem;
import com.errorplayer.lala_weather.gson.GuardianRecv;
import com.errorplayer.lala_weather.gson.Juhe_News_Gson.JuheNewsAPIresult;
import com.errorplayer.lala_weather.gson.Juhe_News_Gson.JuheNewsItem;
import com.errorplayer.lala_weather.gson.WeatherInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linze on 2017/7/7.
 */

public class Utility {

//    处理和解析服务器返回的省级数据
    public static  boolean handleProvinceResponse(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            Log.d("ment", response);
            try{
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++)
                {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                    //Log.d("ment", "handleProvinceResponse: ");
                }
                return true;
            }catch (JSONException E)
            {
                E.printStackTrace();
            }
        }
        return false;
    }

    public static  boolean handleCityResponse(String response,int provinceId)
    {
        if (!TextUtils.isEmpty(response))
        {
            try{
                JSONArray allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++)
                {
                    Log.d("main", "handleCityResponse: 开始处理返回数据 ");
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException E)
            {
                E.printStackTrace();
            }
        }else {
            Log.d("MAIN", "返回为空 ");
        }
        return false;
    }

    public static  boolean handleCountyResponse(String response,int cityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            try{
                JSONArray allCounties = new JSONArray(response);
                for (int i=0;i<allCounties.length();i++)
                {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException E)
            {
                E.printStackTrace();
            }
        }
        return false;
    }

    public static List<GuardianNewsItem> handleGuardianResponse(String response)
    {
        GuardianRecv Recv = new GuardianRecv();
        List<GuardianNewsItem> resultsList = new ArrayList<>();
        if (!TextUtils.isEmpty(response))
        {
            try{
                 Gson gson = new Gson();
                Recv = gson.fromJson(response,GuardianRecv.class);
                resultsList = Recv.guardianResponse.GuadianNewsItemList;
                return resultsList;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    public static WeatherInfo handleWeatherResponse(String response)
    {

        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            if (TextUtils.isEmpty(weatherContent))
                Log.d("DDD", "weathercontent is null ");
            return new Gson().fromJson(weatherContent,WeatherInfo.class);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static JuheNewsAPIresult handleJuheNewsAPIresult(String response)
    {

        try{
            JSONObject Main_jsonObject = new JSONObject(response);
            /*JSONObject jsonReason = Main_jsonObject.getJSONObject("reason");
            if (jsonReason.toString().contains("超过每日"))
            {
                return null;
            }*/
            JSONObject jsonObject = Main_jsonObject.getJSONObject("result");
            String JuheNewsContent = jsonObject.toString();
            if (TextUtils.isEmpty(JuheNewsContent))
                Log.d("DDD", "JuheNewsContent is null ");
            return new Gson().fromJson(JuheNewsContent,JuheNewsAPIresult.class);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
