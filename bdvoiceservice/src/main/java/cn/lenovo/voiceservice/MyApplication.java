package cn.lenovo.voiceservice;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;

import cn.lenovo.voiceservice.jsonbean.WeatherBean;
import cn.lenovo.voiceservice.location.LocationManager;
import cn.lenovo.voiceservice.location.LocationService;
import cn.lenovo.voiceservice.music.utils.FileUtils;


/**
 * Created by tmac on 17-11-30.
 */

public class MyApplication extends Application {

    private static final String TAG = "SC-Application";
    private static WeatherBean mWeatherBean;
    private static String mWeekWeather;

    public LocationService locationService;
    public Vibrator mVibrator;
    private static String mCity = "北京";

    public static boolean isLocation = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mWeatherBean = new WeatherBean();
        ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetInfo.isConnected() && wifiNetInfo.getState() == NetworkInfo.State.CONNECTED) {// connect network
            LocationManager.getInstance(getApplicationContext());
        }else {
            isLocation = true;
        }

        FileUtils.copyDBToSD(getApplicationContext());

    }

    public static void setWeatherBean(WeatherBean weatherBean){
        mWeatherBean = weatherBean;
    }

    public static WeatherBean getWeatherBean(){
        return mWeatherBean;
    }

    public static void setWeekWeather(String weekWeather){
        mWeekWeather = weekWeather;
    }

    public static String getmWeekWeather(){
        return mWeekWeather;
    }

    public static String getLocationCity(){
        return mCity;
    }

    public static void setLocationCity(String city){
        mCity = city;
    }



}
