package com.errorplayer.lala_weather;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.errorplayer.lala_weather.SupportActivity.TmapActivity;
import com.errorplayer.lala_weather.db.City;
import com.errorplayer.lala_weather.db.County;
import com.errorplayer.lala_weather.db.Province;
import com.errorplayer.lala_weather.gson.WeatherInfo;
import com.errorplayer.lala_weather.util.HttpUtil;
import com.errorplayer.lala_weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.LOCATION_SERVICE;


/**
 * Created by linze on 2017/7/7.
 */

public class ChooseAreaFragment extends Fragment {



    private Button Amap_button;

    private LocationListener locationListener;

    private LocationManager locationManager;

    private Location location;

    private Button locateButton;

    public static final String TAG = "ChooseAreaFragment:";

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    //private ArrayAdapter<String> adapter;

    private SimpleAdapter adapter;




    private List<HashMap<String ,Object>> dataList = new ArrayList<>();

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private Province selectedProvince;

    private  City selectedCity;

    private int currentLevel;



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area,container,false);
        locateButton = (Button) view.findViewById(R.id.locate_button);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        Amap_button = (Button) view.findViewById(R.id.Amap_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //adapter = new ArrayAdapter<>(listView.getContext(),android.R.layout.simple_list_item_1,dataList);
        adapter = new SimpleAdapter(listView.getContext(),
                dataList,
                R.layout.county_item,
                new String[]{"nowtemperature","countyname", "nowstatus","weatherid"},
                new int[]{R.id.now_temperature, R.id.county_name,R.id.county_nowstatus, R.id.weather_id});
        listView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    Log.d(TAG, "onClick: 省份 ");
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    Log.d(TAG, "onClick: 城市 ");
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
                    String weatherId = data.get("weatherid").toString();

                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);

                    }

                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLoc();
                if (getActivity() instanceof WeatherActivity) {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing(true);
                    //activity.requestVirtualWeather();
                }


            }

        });

        Amap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getActivity() instanceof MainActivity) {
                    Intent intent = new Intent(getActivity(), TmapActivity.class);
                    //intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof WeatherActivity) {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing(true);
                    //activity.requestWeather(weatherId);

                   Intent intent = new Intent(getActivity(), TmapActivity.class);
                   if (activity.lastLocationCache_lo  != "")
                   {
                       intent.putExtra("lat",activity.lastLocationCache_la);
                       intent.putExtra("lng",activity.lastLocationCache_lo);
                   }

                    //getActivity().finish();
                    startActivity(intent);

                }


            }

        });

    }

    private void queryCounties() {

        Log.d("queryCounties", "successful 1! ");
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0)
        {
            Log.d("queryCounties", "successful 2! ");
            dataList.clear();
            for (int index = 0; index != countyList.size();index++)
            {
                String weatherUrl = "https://free-api.heweather.com/s6/weather?location="+countyList.get(index).getWeatherId()+"&key=e6bdeb4c2c2b46efb4035de24d387f40";
                //String weatherUrl = "https://free-api.heweather.com/s6/weather?location=CN101080402&key=8ac5c8e5219b440694de3be0ff010fb2";
                HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.d("queryCounties",countyList.get(0).getWeatherId());
                        Log.d("queryCounties", "queryCounties: http网络请求失败");

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        Log.d("queryCounties", "queryCounties: http网络请求成功");
                        final WeatherInfo weather = Utility.handleWeatherResponse(responseText);
                        //Log.d("queryWEATHER", weather.now.more);


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (weather != null&&"ok".equals(weather.status) )
                                {

                                    Log.d(TAG, weather.basic.cityName+weather.now.temperature);
                                    String content = "";
                                    content = weather.now.temperature+"    "+weather.basic.cityName+"      ";
                                    HashMap<String, Object> item = new HashMap<String, Object>();
                                    item.put("nowtemperature",weather.now.temperature);
                                    item.put("countyname",weather.basic.cityName);
                                    item.put("weatherid",weather.basic.weatherId);
                                    item.put("nowstatus",weather.now.more);

                                    dataList.add(item);

                                    adapter.notifyDataSetChanged();
                                }

                            }
                        });

                    }
                });


            }

            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0)
        {
            Log.d(TAG, "数据库已有");
            dataList.clear();
            for (City city : cityList)
            {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("nowtemperature","  ");
                item.put("countyname",city.getCityName());
                item.put("weatherid","");

                dataList.add(item);

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            Log.d(TAG, "向服务器请求");
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0)
        {
            dataList.clear();
            for(Province province:provinceList)
            {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("nowtemperature","  ");
                item.put("countyname",province.getProvinceName());
                item.put("weatherid","");

                dataList.add(item);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        //Log.d("12121", "handleProvinceResponse");

                        //Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
                // e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type))
                {

                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type))
                {
                    Log.d(TAG, "onResponse: 开始处理请求");
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type))
                {
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type))
                            {
                                queryProvinces();
                            }else if ("city".equals(type))
                            {
                                Log.d(TAG, "run: 请求成功，再次更新显示");
                                queryCities();
                            }else if ("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }
            }

        });

    }

    private void closeProgressDialog() {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void getLoc() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            List<String> permissionList = new ArrayList<>();
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        }
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        String provider;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
        criteria.setAltitudeRequired(false);// 不要求海拔
        criteria.setBearingRequired(false);// 不要求方位
        criteria.setCostAllowed(true);// 允许有花费
        criteria.setPowerRequirement(Criteria.ACCURACY_HIGH);// 低功耗
        // 从可用的位置提供器中，匹配以上标准的最佳提供器
        provider = locationManager.getBestProvider(criteria, true);

        location = locationManager.getLastKnownLocation(provider);
        if (location != null)
        {
            String latitude = location.getLatitude() + "";
            String longitude = location.getLongitude() + "";
            if (getActivity() instanceof MainActivity) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                intent.putExtra("weather_latitude", latitude);
                intent.putExtra("weather_longitude", longitude);
                startActivity(intent);
                getActivity().finish();
            } else if (getActivity() instanceof WeatherActivity) {
                WeatherActivity activity = (WeatherActivity) getActivity();
                activity.drawerLayout.closeDrawers();
                activity.swipeRefresh.setRefreshing(true);
                activity.requestWeather(latitude, longitude);
            }
            return ;
        }else {
            Toast.makeText(getActivity(),"请确认GPS已经开启。",Toast.LENGTH_SHORT).show();

        }
        locationListener = new LocationListener() {
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }
            public void onLocationChanged(Location location) {
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
//                Log.d("ChooseAreaFragment", lat);
//                Log.d("ChooseAreaFragment", lon);
                if (location != null)
                {
                    String latitude = location.getLatitude() + "";
                    String longitude = location.getLongitude() + "";
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_latitude", lat);
                        intent.putExtra("weather_longitude", lon);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(latitude, longitude);
                    }
                }


            }
        };
        locationManager.requestLocationUpdates(provider,500, 1, locationListener);


    }



}
