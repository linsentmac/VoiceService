package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.utils.AnimationUtils;
import com.lenovo.smartcastvoice.utils.StatusBarUtils;
import com.lenovo.smartcastvoice.view.SeismicWave;

import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener{


    private static final String TAG = "SC-MainActivity";
    private TextView hourTime;
    private TextView monthTime;
    private TextView workDayTime;
    private ImageButton menuBtn;
    private ImageButton musicBtn;
    private SeismicWave menuSeis;
    private TimeZone tz;
    private Calendar c;

    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtils.hideNavgationBar(this);

        initViews();
        initEvents();
        startTimeThread();
    }

    private void initViews() {
        hourTime = findViewById(R.id.Hour_time_tv);
        monthTime = findViewById(R.id.Month_time_tv);
        workDayTime = findViewById(R.id.WorkDay_time_tv);
        menuBtn = findViewById(R.id.menu_iv);
        musicBtn = findViewById(R.id.record_iv);
        menuSeis = findViewById(R.id.menu_seismicwave);
    }

    private void initEvents() {
        tz = TimeZone.getTimeZone("GMT");
        c = Calendar.getInstance(tz);
        menuBtn.setOnClickListener(this);
        musicBtn.setOnClickListener(this);
    }

    private void startTimeThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        mHandler.sendEmptyMessage(0);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while (true);
            }
        }).start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long sysTime = System.currentTimeMillis();
            CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);
            hourTime.setText(sysTimeStr); //更新时间
            //Log.d(TAG, "month = " + c.get(Calendar.MONTH) + "\n" + "day = " + c.get(Calendar.DAY_OF_MONTH));
            monthTime.setText((c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日");
            workDayTime.setText("星期" + convertNum(c.get(Calendar.DAY_OF_WEEK)));
        }
    };

    private String convertNum(int num){
        //Log.d(TAG, "conNum = " + num);
        String conNum = null;
        switch (num){
            case 1:
                conNum = "天";
                break;
            case 2:
                conNum = "一";
                break;
            case 3:
                conNum = "二";
                break;
            case 4:
                conNum = "三";
                break;
            case 5:
                conNum = "四";
                break;
            case 6:
                conNum = "五";
                break;
            case 7:
                conNum = "六";
                break;
        }
        return conNum;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_iv:
                //musicBtn.startAnimation(AnimationUtils.getAnimation());
                AnimationUtils.playAnimation(musicBtn, this);

                break;
            case R.id.menu_iv:
                //menuBtn.startAnimation(AnimationUtils.getAnimation());
                AnimationUtils.playAnimation(menuBtn, this);
                break;
        }
    }


    /**
     * 对view进行缩放。
     */
    public static void scaleTo(View v, float scale) {
        if (Build.VERSION.SDK_INT >= 11) {
            v.setScaleX(scale);
            v.setScaleY(scale);
        } else {
            float oldScale = 1;
            if (v.getTag(Integer.MIN_VALUE) != null) {
                oldScale = (Float) v.getTag(Integer.MIN_VALUE);
            }
            final ViewGroup.LayoutParams params = v.getLayoutParams();
            params.width = (int) ((params.width / oldScale) * scale);
            params.height = (int) ((params.height / oldScale) * scale);
            v.setTag(Integer.MIN_VALUE, scale);
        }
    }

    @Override
    public void EndAnimation(View view) {
        switch (view.getId()){
            case R.id.menu_iv:
                finish();
                break;
            case R.id.record_iv:
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
                break;
        }

    }
}
