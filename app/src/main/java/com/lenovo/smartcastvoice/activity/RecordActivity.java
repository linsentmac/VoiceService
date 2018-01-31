package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lenovo.smartcastvoice.MyApplication;
import com.lenovo.smartcastvoice.jsonbean.DomainBean;
import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.httpRequest.AppClient;
import com.lenovo.smartcastvoice.jsonbean.HourWeatherBean;
import com.lenovo.smartcastvoice.jsonbean.WeatherBean;
import com.lenovo.smartcastvoice.jsonbean.WeekWeatherBean;
import com.lenovo.smartcastvoice.utils.PackageManager;
import com.lenovo.smartcastvoice.utils.StatusBarUtils;
import com.lenovo.smartcastvoice.view.SeismicWave;
import com.lenovo.smartcastvoice.voice_manager.TTStoSpeech;
import com.lenovo.smartcastvoice.voice_manager.VoiceManager;
import com.lenovo.smartcastvoice.voice_manager.VoiceRecognitionListener;

import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends Activity implements VoiceRecognitionListener{

    private static final String TAG = "SC-RecordActivity";
    private ImageView record_ball;
    private GifImageView gifImageView;
    private static final int STOP_RECORD = 0;
    private static final int STOP_DELAY = 10 * 1000;
    private static final int RECORD_SUCCESS = 1;
    private PackageManager packageManager;
    private TTStoSpeech ttStoSpeech;
    private VoiceManager voiceManager;
    private SeismicWave seismicWave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        StatusBarUtils.hideNavgationBar(this);
        Log.d(TAG, "onCreate");
        initViews();
        //initEvents();

        packageManager = new PackageManager(this);
        ttStoSpeech = new TTStoSpeech(this);
        voiceManager = new VoiceManager(this, this);
        voiceManager.startRecognition(packageManager.getAppinfo());
        mHandler.sendEmptyMessageDelayed(STOP_RECORD, STOP_DELAY);

    }

    private void initEvents() {
        try {
            MediaController mc = new MediaController(this);
            GifDrawable gifFromResource = new GifDrawable( getResources(), R.mipmap.keywest_ball_1);
            gifImageView.setImageDrawable(gifFromResource);
            //mc.setAnchorView(gifImageView);
            //gifFromResource.reset();
            while (!gifFromResource.isRunning()){
                gifFromResource.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initViews() {
        seismicWave = findViewById(R.id.seismicwave);
        seismicWave.reStart().start();
        gifImageView = findViewById(R.id.ball_gif);
        //record_ball = findViewById(R.id.record_ball_gif);
        //Glide.with(this).load(R.mipmap.keywest_ball_1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(record_ball);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STOP_RECORD:
                    release();
                    startActivity(new Intent(RecordActivity.this, SpeekHintActivity.class));
                    finish();
                    break;
                case RECORD_SUCCESS:
                    String result = (String) msg.obj;
                    dealWithRecResult(result);
                    break;
            }
        }
    };

    @Override
    public void asrSuccess(final String result) {
        mHandler.removeMessages(STOP_RECORD);
        Log.d(TAG, "result = " + result);
        seismicWave.setVisibility(View.GONE);
        gifImageView.setVisibility(View.VISIBLE);
        Message msg = new Message();
        msg.what = RECORD_SUCCESS;
        msg.obj = result;
        mHandler.sendMessageDelayed(msg, 0);
    }

    private void dealWithRecResult(final String result){
        if(result.contains("打开")){
            String app=result.replace("打开", "");
            Log.i("打开", app);
            packageManager.openApp(app);
        }else if(result.contains("你好联想")){

        }else {
            String url = AppClient.commonUrl + "sentence=" + result + "&userid=55/";
            AppClient.ApiStores apiStores = AppClient.retrofit(url).create(AppClient.ApiStores.class);
            /*RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    new Gson().toJson(new Enity("30921")));*/
            Call<WeatherBean> call = apiStores.getDomainBean(result, 55, "上海上海");
            call.enqueue(new Callback<WeatherBean>() {
                @Override
                public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                    String reply = response.body().getReply();
                    String domain = response.body().getDomain();
                    Log.i(TAG, "Request Info :" + reply);
                    if(reply == null || reply.equals("") || reply.trim() == null){
                        startHintActivity(result);
                        return;
                    }
                    String weekWeather = response.body().getIntent().get(0).get未来7天天气();
                    MyApplication.setWeekWeather(weekWeather);
                    Log.i(TAG, "weekWeather Info :" + weekWeather);
                    Gson gson = new Gson();
                    List<WeekWeatherBean> weekList = gson.fromJson(weekWeather, new TypeToken<List<WeekWeatherBean>>(){}.getType());
                    //Log.i(TAG, "weekWeather weather :" + weekList.get(0).getWeather());

                    if(domain.equals("天气")){
                        Intent intent = new Intent(RecordActivity.this, WeatherActivity.class);
                        intent.putExtra("result", result);
                        intent.putExtra("reply", reply);
                        //intent.putExtra("weekWeather", weekWeather);
                        startActivity(intent);
                    }
                    ttStoSpeech.speek(reply);
                }

                @Override
                public void onFailure(Call<WeatherBean> call, Throwable t) {
                    Log.d(TAG, "onFailure = " + t.getMessage());
                    startHintActivity(result);
                }
            });
        }
    }

    private void startHintActivity(String result){
        Intent intent = new Intent(RecordActivity.this, SpeekHintActivity.class);
        intent.putExtra("isResult", true);
        intent.putExtra("Result", result);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory");
        release();
        mHandler.removeMessages(STOP_RECORD);
    }

    private void release(){
        if(voiceManager != null){
            voiceManager.stopRecognition();
        }
        if(ttStoSpeech != null){
            ttStoSpeech.releaseTTS();
        }
    }
}
