package cn.lenovo.voiceservice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by linsen on 17-11-24.
 */

public class TTStoSpeech implements TextToSpeech.OnInitListener{

    private static final String TAG = "SC-TTStoSpeech";
    private Context mContext;
    private TextToSpeech ttS;

    // xunfei "com.iflytek.speechcloud"
    private TTStoSpeech(Context context){
        mContext = context;
        Log.d(TAG, "new TTStoSpeech ... ");
        ttS = new TextToSpeech(context, this, "com.iflytek.speechcloud");
    }

    private static TTStoSpeech mInstance;
    public static TTStoSpeech getInstance(Context context){
        if(mInstance == null){
            mInstance = new TTStoSpeech(context);
        }
        return mInstance;
    }

    @Override
    public void onInit(int status) {
        Log.e(TAG, "onInit for tts");
        if (status != TextToSpeech.SUCCESS) {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
            return;
        }

        if (ttS == null) {
            Log.e(TAG, "null tts");
            return;
        }
    }


    public void speek(String voice){
        Log.d(TAG, "voice = " + voice);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UUID.randomUUID()
                .toString());
        // String str = "你好啊，hello 我是TTS";
        if(ttS == null){
            ttS = new TextToSpeech(mContext, this, "com.iflytek.speechcloud");
        }else {
            ttS.setLanguage(Locale.CHINA);
            ttS.speak(voice, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    public void releaseTTS(){
        if(ttS != null){
            Log.d(TAG, "releaseTTS");
            ttS.stop();
            ttS.shutdown();
            ttS = null;
        }
        mInstance = null;
    }

    public void stopTTs(){
        if(ttS != null
                && ttS.isSpeaking()){
            Log.d(TAG, "stopTTs");
            ttS.stop();
        }
    }

}
