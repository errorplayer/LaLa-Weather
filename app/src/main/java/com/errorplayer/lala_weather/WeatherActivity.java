package com.errorplayer.lala_weather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import static android.R.attr.value;
import static com.errorplayer.lala_weather.R.color.colorAccent;

public class WeatherActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        naviButton = (Button)findViewById(R.id.navi_button);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        qltyText = (TextView) findViewById(R.id.qlty_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null)
        {
            WeatherInfo weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId =weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
             final String responseText = response.body().string();
             final WeatherInfo weather = Utility.handleWeatherResponse(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null&&"ok".equals(weather.status) )
                        {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                            mWeatherId = weatherId;

                        }else
                        {

                            Toast.makeText(WeatherActivity.this,"刷新失败",Toast.LENGTH_LONG).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(WeatherInfo weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree  = weather.now.temperature+"°C";
        String weatherInfo = weather.now.more.info;



        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastsList)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateTime = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateTime.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }
        if (weather.aqi != null)
        {
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
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }


}
