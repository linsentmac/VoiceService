package cn.lenovo.levoiceservice;

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
    public TTStoSpeech(Context context){
        ttS = new TextToSpeech(context, this, "com.lenovo.lasf.tts");
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
        ttS.setLanguage(Locale.CHINA);
        ttS.speak(voice, TextToSpeech.QUEUE_FLUSH, map);
    }

    public void releaseTTS(){
        if(ttS != null){
            ttS.stop();
            ttS.shutdown();
        }
    }

}
