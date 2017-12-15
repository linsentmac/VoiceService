package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.utils.AnimationUtils;
import com.lenovo.smartcastvoice.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener{

    private static final String TAG = "SC-WeatherActivity";
    private ImageView mic;
    private TextView weather_question;
    private TextView weather_answer;
    private GridView gridView;
    private GridViewAdapter adapter;
    private List<String> dayList = new ArrayList<>();
    private List<String> tempList = new ArrayList<>();
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

        adapter = new GridViewAdapter(this);
        gridView.setAdapter(adapter);


    }

    String result;
    String reply;
    private void initViews(Intent intent) {
        if(intent != null){
            result = intent.getStringExtra("result");
            reply = intent.getStringExtra("reply");
        }
        mic = findViewById(R.id.mic_iv);
        weather_question = findViewById(R.id.weather_question);
        weather_answer = findViewById(R.id.weather_answer);
        weather_question.setText("“" + result + "?”");
        weather_answer.setText(reply);
        gridView = findViewById(R.id.wea_gridview);
        gridView.setEnabled(false);
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

        public GridViewAdapter(Context context){
            mContext = context;
        }

        @Override
        public int getCount() {
            return dayList.size();
        }

        @Override
        public Object getItem(int position) {
            return dayList.get(position);
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
            holder.dayTv.setText(dayList.get(position));
            Drawable drawable = getResources().getDrawable(images[position]);
            holder.wea_View.setImageDrawable(drawable);
            holder.tempTv.setText(tempList.get(position));
            return convertView;
        }


        private class ViewHolder{
            private TextView dayTv;
            private ImageView wea_View;
            private TextView tempTv;
        }

    }
}
