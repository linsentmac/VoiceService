package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lenovo.smartcastvoice.MyApplication;
import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.jsonbean.HourWeatherBean;
import com.lenovo.smartcastvoice.jsonbean.WeekWeatherBean;
import com.lenovo.smartcastvoice.utils.AnimationUtils;
import com.lenovo.smartcastvoice.utils.StatusBarUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener{

    private static final String TAG = "SC-WeatherActivity";
    private ImageView mic;
    private TextView weather_question;
    private TextView weather_answer;
    private TextView pm_Tv;
    private TextView detail_info;
    private TextView city;
    private TextView high_tem;
    private TextView low_tem;
    private TextView hour_temp;
    private GridView gridView;
    private GridViewAdapter adapter;
    private List<String> dayList = new ArrayList<>();
    private List<String> tempList = new ArrayList<>();
    private List<WeekWeatherBean> adapterList = new ArrayList<>();
    private int[] images = new int[]{
            R.mipmap.rain_1,
            R.mipmap.rain_1,
            R.mipmap.rain_1,
            R.mipmap.rain_1,
            R.mipmap.rain_1
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        StatusBarUtils.hideNavgationBar(this);
        initViews(getIntent());
        initEvents();

        dayList.add("今天 11/30");
        dayList.add("明天 12/01");
        dayList.add("周六 12/02");
        dayList.add("周日 12/03");
        dayList.add("周一 12/04");
        tempList.add("5°~10°");
        tempList.add("5°~10°");
        tempList.add("5°~10°");
        tempList.add("5°~10°");
        tempList.add("5°~10°");

        adapter = new GridViewAdapter(this, adapterList);
        gridView.setAdapter(adapter);

    }

    String result;
    String reply;
    private void initViews(Intent intent) {
        mic = findViewById(R.id.mic_iv);
        weather_question = findViewById(R.id.weather_question);
        weather_answer = findViewById(R.id.weather_answer);
        gridView = findViewById(R.id.wea_gridview);
        gridView.setEnabled(false);
        pm_Tv = findViewById(R.id.PM_value);
        detail_info = findViewById(R.id.detail_weather_info);
        city = findViewById(R.id.address_tv);
        high_tem = findViewById(R.id.high_Temp_tv);
        low_tem = findViewById(R.id.low_Temp_tv);
        hour_temp = findViewById(R.id.temp_tv);

        if(intent != null){
            result = intent.getStringExtra("result");
            reply = intent.getStringExtra("reply");
            weather_question.setText("“" + result + "?”");
            weather_answer.setText(reply);
            String weekWeather = MyApplication.getmWeekWeather();
            if(weekWeather != null){
                Gson gson = new Gson();
                List<WeekWeatherBean> weekList = gson.fromJson(weekWeather, new TypeToken<List<WeekWeatherBean>>(){}.getType());
                WeekWeatherBean currentDayWeather = weekList.get(0);
                pm_Tv.setText(currentDayWeather.getPm25());
                detail_info.setText(currentDayWeather.getWeather_night() + "," + currentDayWeather.getWind_direction() + currentDayWeather.getWind() + ",湿度" + currentDayWeather.getHumidity());
                city.setText(currentDayWeather.getCity());
                high_tem.setText(currentDayWeather.getHighTem());
                low_tem.setText(currentDayWeather.getLowTem());
                Log.d(TAG, "currentDayWeather.getHourWeather() = " + currentDayWeather.getHourWeather());
                List<HourWeatherBean> hourWeatherList = gson.fromJson("["+currentDayWeather.getHourWeather()+"]", new TypeToken<List<HourWeatherBean>>(){}.getType());
                Log.d(TAG, "hourWeatherBean " + hourWeatherList.get(0).getWeather().getHumidity());
                String hourTemp = hourWeatherList.get(0).getWeather().getTemperature();
                hour_temp.setText(hourTemp);
                for(int i = 1; i < 6; i++){
                    adapterList.add(weekList.get(i));
                }
                //adapter.notifyDataSetChanged();
                Log.d(TAG, "size = " + adapterList.size());
            }
        }
    }

    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    private void initEvents() {
        mic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mic_iv:
                AnimationUtils.playAnimation(mic, this);
                break;
        }
    }

    @Override
    public void EndAnimation(View view) {
        startActivity(new Intent(WeatherActivity.this, RecordActivity.class));
        finish();
    }


    private class GridViewAdapter extends BaseAdapter{

        private Context mContext;
        private List<WeekWeatherBean> mList;

        public GridViewAdapter(Context context, List<WeekWeatherBean> list){
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Log.d(TAG, "position = " + position);
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, parent, false);
                holder = new ViewHolder();
                holder.dayTv = convertView.findViewById(R.id.item_day_tv);
                holder.wea_View = convertView.findViewById(R.id.item_wea_icon);
                holder.tempTv = convertView.findViewById(R.id.item_temp);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            String date = mList.get(position).getDateTime();
            holder.dayTv.setText(date.substring(5, 7) + "/" + date.substring(8, 10));
            Drawable drawable = getResources().getDrawable(images[position]);
            holder.wea_View.setImageDrawable(drawable);
            holder.tempTv.setText(mList.get(position).getLowTem() + "～" + mList.get(position).getHighTem() + "℃");
            return convertView;
        }


        private class ViewHolder{
            private TextView dayTv;
            private ImageView wea_View;
            private TextView tempTv;
        }

    }
}
