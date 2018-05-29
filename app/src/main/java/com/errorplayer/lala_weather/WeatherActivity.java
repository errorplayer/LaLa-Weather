package com.errorplayer.lala_weather;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.errorplayer.lala_weather.CustomSth.LaLaBroadcastReceiver;
import com.errorplayer.lala_weather.CustomSth.ScrollViewWithListView;
import com.errorplayer.lala_weather.CustomSth.JuheNews.JuheNewsListAdapter;
import com.errorplayer.lala_weather.CustomSth.JuheNews.JuheNewsListItem;
import com.errorplayer.lala_weather.db.CityWeatherStore;
import com.errorplayer.lala_weather.db.County;
import com.errorplayer.lala_weather.gson.Forecast;
import com.errorplayer.lala_weather.gson.GuardianNewsItem;
import com.errorplayer.lala_weather.gson.Juhe_News_Gson.JuheNewsAPIresult;
import com.errorplayer.lala_weather.gson.Juhe_News_Gson.JuheNewsItem;
import com.errorplayer.lala_weather.gson.WeatherInfo;
import com.errorplayer.lala_weather.util.HttpUtil;
import com.errorplayer.lala_weather.util.SonicTools.SonicJavaScriptInterface;
import com.errorplayer.lala_weather.util.SonicTools.SonicRuntimeImpl;
import com.errorplayer.lala_weather.util.Utility;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;

import org.litepal.crud.DataSupport;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private Layout saving_board;

    private List<HashMap<String ,Object>> saving_dataList = new ArrayList<>();

    private ListView saving_weatherInfo_list;

    private SimpleAdapter adapter_savingWeatherInfo;

    private Button juheNews_refresh;

    public String lastLocationCache_la;

    public String lastLocationCache_lo;

    public DrawerLayout drawerLayout;

    private Button naviButton;

    public SwipeRefreshLayout swipeRefresh;

    private static String mWeatherId;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private static int memory_news_type  = 9;





    private Button NextActivity_Button;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    //private LinearLayout layout_AqiBoard=(LinearLayout)findViewById(R.id.AQI_BOARD);

    private TextView qltyText;

    private TextView pm25Text;

    private TextView carWashText;

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

    private String Family_Address = "lifeandstyle/family";

    private String Career_Address = "money/work-and-careers";

    private RadioGroup News_Select_Group;
    private RadioButton cliamte;
    private RadioButton politics;
    private RadioButton stock;
    private RadioButton fitting;
    private RadioButton food;
    private RadioButton pollution;
    private RadioButton love;
    private RadioButton career;

    private String Memory_News_Select;

    public static final int MODE_DEFAULT = 0;

    public static final int MODE_SONIC = 1;

    public IntentFilter intentFilter;

    public weatherBroadcastReceiver innerBR;


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

        intentFilter = new IntentFilter();
        intentFilter.addAction("lat_lng_request");
        innerBR = new weatherBroadcastReceiver();
        registerReceiver(innerBR,intentFilter);

        //saving_board = findViewById(R.layout.saving_weatherinfo);

        juheNews_refresh = findViewById(R.id.juheNews_refresh_button);

        lastLocationCache_la = "";
        lastLocationCache_lo = "";
        Memory_News_Select = "politics/politics";
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);

        bingPicImg = findViewById(R.id.bing_pic_img);

        drawerLayout = findViewById(R.id.drawer_layout);
        naviButton = findViewById(R.id.navi_button);
        weatherLayout = findViewById(R.id.weather_layout);
        forecastLayout = findViewById(R.id.forecast_layout);
        titleCity = findViewById(R.id.title_city);
        NextActivity_Button = findViewById(R.id.next_activity_button);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText =  findViewById(R.id.weather_info_text);
        weatherImage =  findViewById(R.id.weather_image);

        qltyText = findViewById(R.id.qlty_text);
        pm25Text =  findViewById(R.id.pm25_text);
        carWashText = findViewById(R.id.car_wash_text);
        comfortText =  findViewById(R.id.comfort_text);
        sportText = findViewById(R.id.sport_text);
        drsgText =  findViewById(R.id.drsg_text);
        fluText =  findViewById(R.id.flu_text);
        uvText = findViewById(R.id.uv_text);

        News1 =  findViewById(R.id.guardian_1);
        News2 =  findViewById(R.id.guardian_2);
        News3 = findViewById(R.id.guardian_3);
        News4 =  findViewById(R.id.guardian_4);
        News5 =  findViewById(R.id.guardian_5);
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
        love = (RadioButton) findViewById(R.id.family_relationship);
        career = (RadioButton) findViewById(R.id.career_business);
        News_Select_Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (cliamte.getId() == checkedId) {
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
                } else if (checkedId == food.getId()) {
                    Memory_News_Select = Food_Address;
                    requestNews();
                } else if (checkedId == love.getId()) {
                    Memory_News_Select = Family_Address;
                    requestNews();
                } else if (checkedId == career.getId()) {
                    Memory_News_Select = Career_Address;
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
            String longitude = getIntent().getStringExtra("weather_longitude") +"";
            //Toast.makeText(getApplicationContext(), latitude+","+longitude, Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),latitude+","+longitude,Toast.LENGTH_SHORT).show();

            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather("30.5", "121.0");
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                requestJuheNews();

            }
        });


        naviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NextActivity_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setClass(WeatherActivity.this, NewsBrowserPage.class);
                WeatherActivity.this.startActivity(intent);*/
                CityWeatherStore cWeatherInfo = new CityWeatherStore();
                cWeatherInfo.setWeatherId(mWeatherId);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                cWeatherInfo.setSaving_date(simpleDateFormat.format(date));
                cWeatherInfo.setCond_text(weatherInfoText.getText().toString());
                cWeatherInfo.setTemperature(degreeText.getText().toString());
                cWeatherInfo.save();

                try {
                    Thread.currentThread().sleep(600);//阻断2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }





            }
        });





        juheNews_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestJuheNews();
            }
        });
        requestJuheNews_forOncreate();
        memory_news_type = 9;
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
                editor.putString("bing_pic", bingPic);
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
        Update_Display_HistoryWeatherInfo();
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + weatherId + "&key=e6bdeb4c2c2b46efb4035de24d387f40";
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
                            mWeatherId = weatherId;

                        }
                        Log.d("test", "onResponse successfully");

                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
        mWeatherId = weatherId;




    }

    public void requestWeather(final String lat,final String lng) {
        //https://free-api.heweather.com/s6/weather?location=25.73049,110.818161&key=e6bdeb4c2c2b46efb4035de24d387f40
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + lat+","+lng+ "&key=e6bdeb4c2c2b46efb4035de24d387f40";
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
                            lastLocationCache_la = lat;
                            lastLocationCache_lo = lng;
                            mWeatherId = weather.basic.weatherId;

                        }
                        Log.d("test", "onResponse successfully");

                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
        lastLocationCache_la = lat;
        lastLocationCache_lo = lng;



    }



    public void requestNews() {
        init();
        String NewsUrl = "https://content.guardianapis.com/search?tag=" + Memory_News_Select + "&api-key=2c26debe-2b38-470c-a967-ad52b9c210dc";
        HttpUtil.sendOkHttpRequest(NewsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取新闻信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final List<GuardianNewsItem> News = Utility.handleGuardianResponse(responseText);
                //Log.d("news", News.toString());
                StorageNews = News;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (News != null && News.size() != 0) {
                            int num = News.size();
                            if (num >= 5) {
                                String showContent[] = News.get(0).webPublicationDate.split("T|Z");
                                String dateContent[] = showContent[0].split("-");
                                News1.setText("[" + dateContent[1] + "月" + dateContent[2] + "日" + showContent[1] + "]   " + News.get(0).webTitle);
                                showContent = News.get(1).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News2.setText("[" + dateContent[1] + "月" + dateContent[2] + "日" + showContent[1] + "]   " + News.get(1).webTitle);
                                showContent = News.get(2).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News3.setText("[" + dateContent[1] + "月" + dateContent[2] + "日" + showContent[1] + "]   " + News.get(2).webTitle);
                                showContent = News.get(3).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News4.setText("[" + dateContent[1] + "月" + dateContent[2] + "日" + showContent[1] + "]   " + News.get(3).webTitle);
                                showContent = News.get(4).webPublicationDate.split("T|Z");
                                dateContent = showContent[0].split("-");
                                News5.setText("[" + dateContent[1] + "月" + dateContent[2] + "日" + showContent[1] + "]   " + News.get(4).webTitle);

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

                //Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                //intent.putExtra("NewsURL", StorageNews.get(0).webUrl);
                startBrowserActivity(MODE_SONIC, StorageNews.get(0).webUrl);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                //intent.putExtra("NewsURL", StorageNews.get(1).webUrl);
                startBrowserActivity(MODE_SONIC, StorageNews.get(1).webUrl);

                //startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                //intent.putExtra("NewsURL", StorageNews.get(2).webUrl);
                startBrowserActivity(MODE_SONIC, StorageNews.get(2).webUrl);

                //startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                //intent.putExtra("NewsURL", StorageNews.get(3).webUrl);
                startBrowserActivity(MODE_SONIC, StorageNews.get(3).webUrl);

                //startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
        News5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Intent intent = new Intent(WeatherActivity.this,NewsBrowserPage.class);
                //intent.putExtra("NewsURL", StorageNews.get(4).webUrl);
                startBrowserActivity(MODE_SONIC, StorageNews.get(4).webUrl);

                //startActivity(intent);
                //Log.d("NNNNN",StorageNews.toString());
            }
        });
    }

    private void showWeatherInfo(WeatherInfo weather) {
        String cityName = weather.basic.cityName;
        //String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature;
        String weatherInfo = weather.now.more;


        titleCity.setText(cityName);
        //titleUpdateTime.setText(updateTime);
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
        for (Forecast forecast : weather.forecastsList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateTime = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            String month = forecast.date.split("-")[1];
            String day = forecast.date.split("-")[2];

            dateTime.setText(month + "月" + day + "日");
            infoText.setText(forecast.condition_info);
            maxText.setText(forecast.tmp_max);
            minText.setText(forecast.tmp_min);

            forecastLayout.addView(view);
        }
        /*if (weather.aqi != null)
        {
            if (weather.aqi.city.qlty.length() > 2)
                qltyText.setTextSize(35);
            qltyText.setText(weather.aqi.city.qlty);
            pm25Text.setText(weather.aqi.city.pm25);
            findViewById(R.id.AQI_BOARD).setVisibility(View.VISIBLE);
        }else
        {
            qltyText.setText("无");
            pm25Text.setText("无");
            findViewById(R.id.AQI_BOARD).setVisibility(View.GONE);
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
        drsgText.setText(Dress);*/
        weatherLayout.setVisibility(View.VISIBLE);


    }

    private void startBrowserActivity(int mode, String Url) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, Url);
        intent.putExtra(BrowserActivity.PARAM_MODE, mode);
        //intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        startActivity(intent);
    }

    private void init() {
        // init sonic engine
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }


    }

    public void requestJuheNews_forOncreate() {
        String NewsUrl = "https://v.juhe.cn/toutiao/index?type=top&key=999bdf2eb37c3198547c1590491224c6";
        HttpUtil.sendOkHttpRequest(NewsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取聚合新闻失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if (responseText.contains("超过每日"))
                    return;
                final JuheNewsAPIresult JuheResult = Utility.handleJuheNewsAPIresult(responseText);

                final ArrayList<JuheNewsListItem> juhe_newsList  = From_JuheNewsItem_To_JuheNewsListItem((ArrayList) JuheResult.JuheNewsItemList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (juhe_newsList != null && juhe_newsList.size() != 0) {
                            int num = juhe_newsList.size();
                            JuheNewsListAdapter adapter =
                                    new JuheNewsListAdapter(WeatherActivity.this, R.layout.juhenews_listitem, juhe_newsList);
                            ListView Juhe_News_listview = (ListView)findViewById(R.id.juheNews_list);

                            Juhe_News_listview.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(Juhe_News_listview);
                            Log.d("JuheNews", String.valueOf(num));

                            Juhe_News_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    JuheNewsListItem jhni = juhe_newsList.get(position);
                                    startBrowserActivity(MODE_SONIC,jhni.getNewsContentUrl());

                                }
                            });



                        }

                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });

    }

    public void requestJuheNews() {
        String[] type_array = {"shehui","guonei","guoji","yule","tiyu","junshi","keji","caijing","shishang"};
        String type_news ;
        if (memory_news_type == 9) {
            memory_news_type = 0;
            type_news = type_array[memory_news_type];
        }

        else {
            memory_news_type += 1;
            if (memory_news_type == 9)
               type_news = "top";
            else type_news = type_array[memory_news_type];
        }

        String NewsUrl = "https://v.juhe.cn/toutiao/index?type="+type_news+"&key=999bdf2eb37c3198547c1590491224c6";
        HttpUtil.sendOkHttpRequest(NewsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取聚合新闻失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if (responseText.contains("超过每日"))
                    return;
                final JuheNewsAPIresult JuheResult = Utility.handleJuheNewsAPIresult(responseText);
                final ArrayList<JuheNewsListItem> juhe_newsList  = From_JuheNewsItem_To_JuheNewsListItem((ArrayList) JuheResult.JuheNewsItemList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (juhe_newsList != null && juhe_newsList.size() != 0) {
                            int num = juhe_newsList.size();
                            JuheNewsListAdapter adapter =
                                    new JuheNewsListAdapter(WeatherActivity.this, R.layout.juhenews_listitem, juhe_newsList);
                            ListView Juhe_News_listview = (ListView)findViewById(R.id.juheNews_list);

                            Juhe_News_listview.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(Juhe_News_listview);
                            Log.d("JuheNews", String.valueOf(num));

                            Juhe_News_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    JuheNewsListItem jhni = juhe_newsList.get(position);
                                    startBrowserActivity(MODE_SONIC,jhni.getNewsContentUrl());

                                }
                            });



                        }

                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });

    }


    public ArrayList<JuheNewsListItem> From_JuheNewsItem_To_JuheNewsListItem(ArrayList<JuheNewsItem> al) {
        ArrayList<JuheNewsListItem> result = new ArrayList<>();
        for (JuheNewsItem i : al) {
            JuheNewsListItem ii = new JuheNewsListItem(i.newsItem_title, i.newsItem_picUrl,i.newsItem_url);
            result.add(ii);
        }
        return result;
    }




    public static void setListViewHeightBasedOnChildren(ListView listView) {
// 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1))+4500;
// listView.getDividerHeight()获取子项间分隔符占用的高度
// params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    private void Update_Display_HistoryWeatherInfo()
    {

        saving_weatherInfo_list = findViewById(R.id.saving_weatherInfo_list);


        //adapter = new ArrayAdapter<>(listView.getContext(),android.R.layout.simple_list_item_1,dataList);
        adapter_savingWeatherInfo = new SimpleAdapter(saving_weatherInfo_list.getContext(),
                saving_dataList,
                R.layout.saving_weatherinfo_item,
                new String[]{"s_temp","s_cond", "s_date","s_weatherId"},
                new int[]{R.id.saving_temperature, R.id.saving_cond_text,R.id.saving_date, R.id.saving_weather_id});
        saving_weatherInfo_list.setAdapter(adapter_savingWeatherInfo);
        setListViewHeightBasedOnChildren(saving_weatherInfo_list);
        Log.d("saving_datalist",mWeatherId);


        if (saving_dataList.size()!=0)
        {
            saving_dataList.clear();
            findViewById(R.id.SAVING_INFO_BOARD).setVisibility(View.GONE);

        }
        List<CityWeatherStore>  cws = DataSupport.where("weatherId = ?",String.valueOf(mWeatherId)).find(CityWeatherStore.class);

        for( CityWeatherStore cs : cws)
        {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("s_temp",cs.getTemperature());
            item.put("s_cond",cs.getCond_text());
            String  date = cs.getSaving_date().split("年")[1];
            Log.d("s_date", date);
            item.put("s_date",date);

            item.put("s_weatherId",cs.getWeatherId());

            saving_dataList.add(item);

        }

        if (saving_dataList.size()!=0)
        {
            Log.d("saving_datalist", String.valueOf(saving_dataList.size()));
            for (HashMap<String, Object> item : saving_dataList)
            {
                Log.d("saving_datalist",item.get("s_temp").toString()+" "+item.get("s_cond").toString()+" "+item.get("s_date").toString()+" "+item.get("s_weatherId").toString());

            }
            adapter_savingWeatherInfo.notifyDataSetChanged();
            findViewById(R.id.SAVING_INFO_BOARD).setVisibility(View.VISIBLE);

        }


    }



   class weatherBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String latitude = intent.getStringExtra("weather_latitude") + "";
            String longitude = intent.getStringExtra("weather_longitude") +"";
            swipeRefresh.setRefreshing(false);
            swipeRefresh.setRefreshing(true);
            requestWeather(latitude,longitude);
            Log.d("broadcast771", latitude+","+longitude);
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(innerBR);
    }

}