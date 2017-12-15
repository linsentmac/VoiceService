package com.lenovo.smartcastvoice;

import android.app.Application;

import com.lenovo.smartcastvoice.jsonbean.WeatherBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tmac on 17-11-30.
 */

public class MyApplication extends Application {

    private static WeatherBean mWeatherBean;

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

}
