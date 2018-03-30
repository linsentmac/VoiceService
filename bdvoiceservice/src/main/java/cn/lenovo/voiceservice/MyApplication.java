package cn.lenovo.voiceservice;

import android.app.Application;

import cn.lenovo.voiceservice.jsonbean.WeatherBean;


/**
 * Created by tmac on 17-11-30.
 */

public class MyApplication extends Application {

    private static WeatherBean mWeatherBean;
    private static String mWeekWeather;

    @Override
    public void onCreate() {
        super.onCreate();
        mWeatherBean = new WeatherBean();
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

}
