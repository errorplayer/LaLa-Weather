package com.errorplayer.lala_weather;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.errorplayer.lala_weather.gson.Forecast;
import com.errorplayer.lala_weather.gson.WeatherInfo;
import com.errorplayer.lala_weather.util.HttpUtil;
import com.errorplayer.lala_weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherFragment extends Fragment {
    private String lastLocationCache_la;

    private String lastLocationCache_lo;
    public DrawerLayout drawerLayout;


    private Button naviButton;

    public SwipeRefreshLayout swipeRefresh;

    private  String mWeatherId;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView qltyText;

    private TextView pm25Text;

    private  TextView carWashText;

    private TextView comfortText;

    private TextView sportText;

    private ImageView weatherImage;

    private TextView drsgText;

    private TextView fluText;

    private TextView uvText;

    private Fragment weatherFragment;







    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather,container,false);
        lastLocationCache_la = "";
        lastLocationCache_lo = "";
        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);

        drawerLayout = (DrawerLayout)view.findViewById(R.id.drawer_layout);
        naviButton = (Button)view.findViewById(R.id.navi_button);
        weatherLayout = (ScrollView)view.findViewById(R.id.weather_layout);
        forecastLayout = (LinearLayout)view.findViewById(R.id.forecast_layout);
        titleCity = (TextView)view. findViewById(R.id.title_city);
        titleUpdateTime = (TextView)view. findViewById(R.id.title_update_time);
        degreeText = (TextView) view.findViewById(R.id.degree_text);
        weatherInfoText = (TextView)view. findViewById(R.id.weather_info_text);
        weatherImage = (ImageView) view.findViewById(R.id.weather_image);

        qltyText = (TextView) view.findViewById(R.id.qlty_text);
        pm25Text = (TextView)view. findViewById(R.id.pm25_text);
        carWashText = (TextView) view.findViewById(R.id.car_wash_text);
        comfortText = (TextView) view.findViewById(R.id.comfort_text);
        sportText = (TextView) view.findViewById(R.id.sport_text);
        drsgText = (TextView) view.findViewById(R.id.drsg_text);
        fluText = (TextView) view.findViewById(R.id.flu_text);
        uvText = (TextView)view. findViewById(R.id.uv_text);
        return view;
    }

    public static final WeatherFragment newInstance(String title, String message)
    {
        WeatherFragment f = new WeatherFragment();
        Bundle bdl = new Bundle(2);
        bdl.putString(title, message);
        f.setArguments(bdl);
        return f;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null)
        {
            WeatherInfo weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId =weather.basic.weatherId;
            showWeatherInfo(weather);
        }else if (!TextUtils.isEmpty(getArguments().getString("weather_id")) ){
            mWeatherId = getArguments().getString("weather_id");
            Log.d("ChooseAreaFragment",mWeatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }else if(!TextUtils.isEmpty(getArguments().getString("weather_latitude")))
        {
            String latitude =getArguments().getString("weather_latitude")+"";
            String longitude = getArguments().getString("weather_longitude")+"";
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(latitude,longitude);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        naviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=8ac5c8e5219b440694de3be0ff010fb2";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        Toast.makeText(getParentFragment().getActivity(), "获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);

                    }
                });

            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final WeatherInfo weather = Utility.handleWeatherResponse(responseText);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null&&"ok".equals(weather.status) )
                        {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            Log.d("ChooseAreaFragment","onResponse");

                            showWeatherInfo(weather);
                            mWeatherId = weatherId;

                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });



                    }
                });
            }


    public void requestWeather(final String latitude,final String longitude) {
        if (!TextUtils.isEmpty(latitude)&&!TextUtils.isEmpty(longitude)) {
            lastLocationCache_la = latitude;
            lastLocationCache_lo = longitude;
            String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + latitude + "," + longitude + "&key=8ac5c8e5219b440694de3be0ff010fb2";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();

                    Toast.makeText(getParentFragment().getActivity(), "获取天气信息失败",
                            Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);


                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    final WeatherInfo weather = Utility.handleWeatherResponse(responseText);

                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                        Toast.makeText(getActivity(), "定位成功！请及时关闭GPS。", Toast.LENGTH_SHORT).show();

                        mWeatherId = weather.basic.weatherId;

                    } else {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getActivity(), "请稍后再试。", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }

                }
            });
        }
    }
    public void requestVirtualWeather()
    {
        requestWeather(lastLocationCache_la,lastLocationCache_lo);
    }
    private void showWeatherInfo(WeatherInfo weather) {
        Log.d("ChooseAreaFragment","showWeatherInfo");
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree  = weather.now.temperature;
        String weatherInfo = weather.now.more.info;



        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        if (weatherInfo.contains("云"))
            weatherImage.setImageResource(R.drawable.cloudy);
        if (weatherInfo.contains("雨"))
            weatherImage.setImageResource(R.drawable.rain);
        if (weatherInfo.contains("雷"))
            weatherImage.setImageResource(R.drawable.thunder);
        if (weatherInfo.contains("雪"))
            weatherImage.setImageResource(R.drawable.snow);
        if (weatherInfo.contains("晴"))
            weatherImage.setImageResource(R.drawable.sun);
        if (weatherInfo.contains("阴"))
            weatherImage.setImageResource(R.drawable.overcast);


        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastsList)
        {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateTime = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);

            String month = forecast.date.split("-")[1];
            String day = forecast.date.split("-")[2];

            dateTime.setText(month+"月"+day+"日");
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            Log.d("ChooseAreaFragment",forecast.temperature.max+" "+forecast.temperature.min);

            forecastLayout.addView(view);
        }
        if (weather.aqi != null)
        {
            if (weather.aqi.city.qlty.length() > 2)
                qltyText.setTextSize(35);
            qltyText.setText(weather.aqi.city.qlty);
            pm25Text.setText(weather.aqi.city.pm25);
        }else
        {
            qltyText.setText("无");
            pm25Text.setText("无");
        }

        String comfort = "舒适度："+weather.suggestion.comfort.Info;
        String carWash = "洗车指数："+weather.suggestion.carWash.Info;
        String sport = "运动建议："+weather.suggestion.sport.Info;
        String Uv = "紫外线："+weather.suggestion.uv.Info;
        String Flu = "流感："+weather.suggestion.flu.Info;
        String Dress = "穿衣建议："+weather.suggestion.dress.Info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        fluText.setText(Flu);
        uvText.setText(Uv);
        drsgText.setText(Dress);

        weatherLayout.setVisibility(View.VISIBLE);

    }


}