package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;

import com.lenovo.smartcastvoice.jsonbean.DomainBean;
import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.httpRequest.AppClient;
import com.lenovo.smartcastvoice.utils.PackageManager;
import com.lenovo.smartcastvoice.utils.StatusBarUtils;
import com.lenovo.smartcastvoice.voice_manager.TTStoSpeech;
import com.lenovo.smartcastvoice.voice_manager.VoiceManager;
import com.lenovo.smartcastvoice.voice_manager.VoiceRecognitionListener;

import java.io.IOException;

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
    private PackageManager packageManager;
    private TTStoSpeech ttStoSpeech;
    private VoiceManager voiceManager;

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
        voiceManager.startRecognition();
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
            }
        }
    };

    @Override
    public void asrSuccess(String result) {
        mHandler.removeMessages(STOP_RECORD);
        Log.d(TAG, "result = " + result);
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
            Call<DomainBean> call = apiStores.getDomainBean(result, 55);
            call.enqueue(new Callback<DomainBean>() {
                @Override
                public void onResponse(Call<DomainBean> call, Response<DomainBean> response) {
                    String reply = response.body().getReply();
                    Log.i(TAG, "Request Info :" + reply);
                    ttStoSpeech.speek(reply);
                }

                @Override
                public void onFailure(Call<DomainBean> call, Throwable t) {
                    Log.d(TAG, "onFailure = " + t.getMessage());
                }
            });


        }
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
