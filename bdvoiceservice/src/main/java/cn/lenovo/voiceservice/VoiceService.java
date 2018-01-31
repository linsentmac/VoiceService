package cn.lenovo.voiceservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
//import android.support.annotation.Nullable;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.widget.Toast;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.VoiceRecognitionService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tmac on 2017/8/4.
 */

public class VoiceService extends Service implements RecognitionListener{

    private SpeechRecognizer speechRecognizer;
    private EventManager mWpEventManager;
    private static final String TAG = "VoiceService-BD";
    private static final int EVENT_ERROR = 11;
    private long speechEndTime = -1;
    private Intent intent;
    public static final String VOICE_ACTION = "com.lenovo.voiceService.VOICE_SERVICE";
    public static final String VOICE_EXTRA = "VOCON_RESULT";
    private static final String SUCCESS = "success";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "voice service start");
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        registerWakeUp();
        intent = new Intent(VOICE_ACTION);
    }

    private boolean stopWakeRegular = true;
    private void registerWakeUp(){
        // 唤醒功能打开步骤
        // 1) 创建唤醒事件管理器
        mWpEventManager = EventManagerFactory.create(VoiceService.this, "wp");

        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(new com.baidu.speech.EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
                try {
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word");
                        Log.d(TAG, "唤醒成功, 唤醒词: " + word);
                        //stopWakeRegular = true;
                        //tvResult.setText("唤醒成功, 唤醒词: " + word + "\r\n");
                        // 停止唤醒监听
                        mWpEventManager.send("wp.stop", null, null, 0, 0);
                        start();
                    } else if ("wp.exit".equals(name)) {
                        Log.d(TAG, "唤醒已经停止: " + params);
                        String stopRea = json.getString("desc");
                        if(!stopRea.equals(SUCCESS)){
                            Log.d(TAG, "network error");
                            Toast.makeText(VoiceService.this, "第一次使用语音服务请连接网络", Toast.LENGTH_LONG).show();
                            stopWakeRegular = false;
                            stopSelf();
                        }
                        //tvResult.setText("唤醒已经停止: " + params + "\r\n");
                    }
                } catch (JSONException e) {
                    throw new AndroidRuntimeException(e);
                }
            }
        });

        startWakeUp();
    }

    private void startWakeUp(){
        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap params = new HashMap();
        params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
    }

    private void start() {
        Log.d(TAG, "点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        {
            String args = sp.getString("args", "");
            if (null != args) {
                Log.d(TAG, "参数集：" + args);
                intent.putExtra("args", args);
            }
        }
        speechEndTime = -1;
        speechRecognizer.startListening(intent);
    }

    public void bindParams(Intent intent) {
        Log.d(TAG, "bindParams");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("tips_sound", true)) {
            Log.d(TAG, "tips_sound");
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        //if (sp.getBoolean(Constant.EXTRA_GRAMMAR, false)) {
        Log.d(TAG, "设置离线语法");
        intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        //}
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean stopService;
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "stop VoiceService");
        stopService = true;
        speechRecognizer.destroy();
        // 停止唤醒监听
        if(mWpEventManager != null){
            mWpEventManager.send("wp.stop", null, null, 0, 0);
            Log.d(TAG, "service stop wakeUp Listener");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "检测到用户的已经开始说话");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        Log.d(TAG, "检测到用户的已经停止说话");
    }

    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        Log.d(TAG, "识别失败：" + sb.toString());
        startWakeUp();
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String recogResult = Arrays.toString(nbest.toArray(new String[nbest.size()]));
        Log.d(TAG, "识别成功：" + recogResult);
        String convertResult = getEnWord(recogResult);
        Log.d(TAG, "转换结果 : " + convertResult);
        if(convertResult != null){
            intent.putExtra(VOICE_EXTRA, convertResult);
            sendBroadcast(intent);
            //sendBroadcast(intent, "com.android.permission.RECV.VOICE");
        }
        String json_res = results.getString("origin_result");
        try {
            Log.d(TAG, "origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            Log.d(TAG, "origin_result=[warning: bad json]\n" + json_res);
        }
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        //speechRecognizer.stopListening();
        startWakeUp();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            Log.d(TAG, "~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                Log.d(TAG, "EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                Log.d(TAG, "*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    String[] cnStr = new String[]{"[打开小鱼]", "[打开水族馆]", "[打开设置]", "[打开引擎]", "[打开数字博物馆]", "[显示主页]",
                                    "[调节亮度]", "[调节音量]", "[打开应用管理]", "[打开任务管理]", "[隐藏所有信息]",
                                    "[打开网络设置]", "[打开蓝牙设置]", "[打开显示设置]", "[打开语音设置]"};
    String[] enStr = new String[]{"open the Fish", "open the Aquarium", "open the SFSetting", "open the Engine", "open the Digital_Museum", "display the home page",
                                    "adjust brightness", "adjust the volume", "open the app manager", "open the task manager", "hide all information",
                                    "open network settings", "open bluetooth settings", "open display settings", "open voice settings"};
    private String getEnWord(String result){
        for (int i = 0; i < cnStr.length; i++){
            if(cnStr[i].equals(result)){
                return enStr[i];
            }
        }
        return null;
    }

}
