package com.errorplayer.lala_weather;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.errorplayer.lala_weather.db.City;
import com.errorplayer.lala_weather.db.County;
import com.errorplayer.lala_weather.db.Province;
import com.errorplayer.lala_weather.util.HttpUtil;
import com.errorplayer.lala_weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.LOCATION_SERVICE;


/**
 * Created by linze on 2017/7/7.
 */

public class ChooseAreaFragment extends Fragment {

    private double latitude = 0.0;
    private double longitude = 0.0;

    private LocationListener locationListener;

    private int locateFlag;

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

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

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
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(listView.getContext(),android.R.layout.simple_list_item_1,dataList);
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
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
//                        Intent intent = new Intent(getActivity(), WeatherFragment.class);
//                        intent.putExtra("weather_id", weatherId);
//                        startActivity(intent);
//                        getActivity().finish();
                        Log.d(TAG, "准备进入天气碎片 ");
//                        Fragment weatherFragment = Fragment.instantiate(getActivity(), WeatherFragment.class.getName());
//                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                        Bundle args = new Bundle();
//                        args.putString("weather_id",weatherId);
//
//                        weatherFragment.setArguments(args);
//                        fragmentTransaction.replace(R.id.choose_area_fragment, weatherFragment);
//                        fragmentTransaction.commit();

                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.choose_area_fragment,
                                        WeatherFragment.newInstance("weather_id", weatherId))
                                .commit();
                        Log.d(TAG, "进入天气碎片准备结束 ");
                    } else if (getParentFragment() instanceof WeatherFragment) {
                        WeatherFragment weatherFragment = (WeatherFragment) getParentFragment();
                        weatherFragment.drawerLayout.closeDrawers();
                        weatherFragment.swipeRefresh.setRefreshing(true);
                        weatherFragment.requestWeather(weatherId);
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
                if (getParentFragment() instanceof WeatherFragment) {
                    WeatherFragment weatherFragment = (WeatherFragment) getParentFragment();
                    weatherFragment.drawerLayout.closeDrawers();
                    weatherFragment.swipeRefresh.setRefreshing(true);
                    weatherFragment.requestVirtualWeather();
                }


            }

        });

    }



    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0)
        {
            dataList.clear();
            for (County county : countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
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
                dataList.add(city.getCityName());
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
                dataList.add(province.getProvinceName());
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
                        //Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

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
                Intent intent = new Intent(getActivity(), WeatherFragment.class);
                intent.putExtra("weather_latitude", latitude);
                intent.putExtra("weather_longitude", longitude);
                startActivity(intent);
                getActivity().finish();
            } else if (getParentFragment() instanceof WeatherFragment) {
                WeatherFragment weatherFragment = (WeatherFragment) getParentFragment();
                weatherFragment.drawerLayout.closeDrawers();
                weatherFragment.swipeRefresh.setRefreshing(true);
                weatherFragment.requestWeather(latitude, longitude);
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
                        Intent intent = new Intent(getActivity(), WeatherFragment.class);
                        intent.putExtra("weather_latitude", lat);
                        intent.putExtra("weather_longitude", lon);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getParentFragment() instanceof WeatherFragment) {
                    WeatherFragment weatherFragment = (WeatherFragment) getParentFragment();
                    weatherFragment.drawerLayout.closeDrawers();
                    weatherFragment.swipeRefresh.setRefreshing(true);
                    weatherFragment.requestWeather(latitude, longitude);
                    }
                }


            }
        };
        locationManager.requestLocationUpdates(provider,500, 1, locationListener);


    }

}
