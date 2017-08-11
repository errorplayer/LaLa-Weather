package com.errorplayer.lala_weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.*;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.errorplayer.lala_weather.gson.Forecast;
import com.errorplayer.lala_weather.gson.GuardianNewsItem;
import com.errorplayer.lala_weather.gson.WeatherInfo;
import com.errorplayer.lala_weather.util.HttpUtil;
import com.errorplayer.lala_weather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
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

    private ImageView bingPicImg;

    private WebView guardianNewPage;

    private TextView News1;

    private TextView News2;

    private TextView News3;

    private TextView News4;

    private TextView News5;

    private List<GuardianNewsItem> StorageNews = new ArrayList<>();

    private String Environment_Address = "environment/pollution";

    private String FittingBody_Address = "lifeandstyle/health-and-wellbeing";

    private String Politics_Address = "politics/politics";

    private String Food_Address = "lifeandstyle/food-and-drink";

    private String ClimateChange_Address = "environment/climate-change";

    private String StockMarketings_Address = "business/stock-markets";

    private RadioGroup News_Select_Group;
    private RadioButton cliamte;
    private RadioButton politics;
    private RadioButton stock;
    private RadioButton fitting;
    private RadioButton food;
    private RadioButton pollution;

    private String Memory_News_Select;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        lastLocationCache_la = "";
        lastLocationCache_lo = "";
        Memory_News_Select = "politics/politics";
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        naviButton = (Button) findViewById(R.id.navi_button);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        weatherImage = (ImageView) findViewById(R.id.weather_image);

        qltyText = (TextView) findViewById(R.id.qlty_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        drsgText = (TextView) findViewById(R.id.drsg_text);
        fluText = (TextView) findViewById(R.id.flu_text);
        uvText = (TextView) findViewById(R.id.uv_text);

        News1 = (TextView) findViewById(R.id.guardian_1);
        News2 = (TextView) findViewById(R.id.guardian_2);
        News3 = (TextView) findViewById(R.id.guardian_3);
        News4 = (TextView) findViewById(R.id.guardian_4);
        News5 = (TextView) findViewById(R.id.guardian_5);
        News1.setClickable(true);
        News2.setClickable(true);
        News3.setClickable(true);
        News4.setClickable(true);
        News5.setClickable(true);

        News_Select_Group = (RadioGroup) findViewById(R.id.news_sort_select);
        cliamte = (RadioButton) findViewById(R.id.climate_change_news);
        food = (RadioButton) findViewById(R.id.food_drink_news);
        politics = (RadioButton) findViewById(R.id.politics_news);
        pollution = (RadioButton) findViewById(R.id.environment_news);
        stock = (RadioButton) findViewById(R.id.stock_markets_news);
        fitting = (RadioButton) findViewById(R.id.health_wellbeing_news);

        News_Select_Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if ( cliamte.getId() ==checkedId) {
                    Memory_News_Select = ClimateChange_Address;
                    Log.d("weather", "cliamte ");
                    requestNews();
                } else if (checkedId == stock.getId()) {
                    Memory_News_Select = StockMarketings_Address;
                    requestNews();
                } else if (checkedId == fitting.getId()) {
                    Memory_News_Select = FittingBody_Address;
                    requestNews();
                } else if (checkedId == pollution.getId()) {
                    Memory_News_Select = Environment_Address;
                    requestNews();
                } else if (checkedId == politics.getId()) {
                    Memory_News_Select = Politics_Address;
                    requestNews();
                }
            }
        });
        //News6 = (TextView) findViewById(R.id.guardian_2);
        //guardianNewPage  = (WebView) findViewById(R.id.guardian_page_webview);
        //guardianNewPage.setWebViewClient(new WebViewClient());


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            WeatherInfo weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else if (!TextUtils.isEmpty(getIntent().getStringExtra("weather_id"))) {
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        } else if (!TextUtils.isEmpty(getIntent().getStringExtra("weather_latitude"))) {
            String latitude = getIntent().getStringExtra("weather_latitude") + "";
            String longitude = getIntent().getStringExtra("weather_longitude") + "";
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(latitude, longitude);
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
        requestNews();


    }




    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
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

                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
        requestNews();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败",
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
                            if (weather != null && "ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("weather", responseText);
                                editor.apply();
                                showWeatherInfo(weather);
                                mWeatherId = weather.basic.weatherId;

                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            });
            Toast.makeText(WeatherActivity.this,"定位成功！请及时关闭GPS。",Toast.LENGTH_SHORT).show();
        }else
        {

            Toast.makeText(WeatherActivity.this,"请稍后再试。",Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
        }
        loadBingPic();
    }
    public void requestVirtualWeather()
    {
        requestWeather(lastLocationCache_la,lastLocationCache_lo);
    }
    public void requestNews() {

        String NewsUrl = "https://content.guardianapis.com/search?tag="+Memory_News_Select+"&api-key=2c26debe-2b38-470c-a967-ad52b9c210dc";
        HttpUtil.sendOkHttpRequest(NewsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取新闻信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText  = response.body().string();
                final List<GuardianNewsItem> News = Utility.handleGuardianResponse(responseText);
                //Log.d("news", News.toString());
                StorageNews = News;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (News != null && News.size() != 0)
                        {
                            int num = News.size();
                            if (num >= 5 )
                            {
                                String showContent[] = News.get(0).webPublicationDate.split("T|Z");
                                String dateContent[] = showContent[0].split("-");
                                News1.setText("["+dateContent[1]+"月"+dateContent[2]+"日"+showContent[1]+"]   "+News.get(0).webTitle);
                                showContent = News.get(1).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News2.setText("["+dateContent[1]+"月"+dateContent[2]+"日"+showContent[1]+"]   "+News.get(1).webTitle);
                                showContent = News.get(2).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News3.setText("["+dateContent[1]+"月"+dateContent[2]+"日"+showContent[1]+"]   "+News.get(2).webTitle);
                                showContent = News.get(3).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News4.setText("["+dateContent[1]+"月"+dateContent[2]+"日"+showContent[1]+"]   "+News.get(3).webTitle);
                                showContent = News.get(4).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News5.setText("["+dateContent[1]+"月"+dateContent[2]+"日"+showContent[1]+"]   "+News.get(4).webTitle);

                            }

                        }

                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
        News1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                intent.putExtra("NewsURL", StorageNews.get(0).webUrl);
                startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                intent.putExtra("NewsURL", StorageNews.get(1).webUrl);
                startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                intent.putExtra("NewsURL", StorageNews.get(2).webUrl);
                startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                intent.putExtra("NewsURL", StorageNews.get(3).webUrl);
                startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                intent.putExtra("NewsURL", StorageNews.get(4).webUrl);
                startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
    }
    private void showWeatherInfo(WeatherInfo weather) {
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
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
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
