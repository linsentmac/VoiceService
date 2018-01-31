package cn.lenovo.levoiceservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linsen on 18-1-25.
 */

public class VoiceService extends Service {

    private static final String TAG = "SC-VoiceService";

    static Map<String,String> appinfo = new HashMap<String, String>();
    long begintime, endtime;
    String names[];
    String apps[];
    private SpeechRecognizer r;
    private TTStoSpeech ttStoSpeech;
    private boolean isFirstLoad;

    @Override
    public void onCreate() {
        super.onCreate();
        ttStoSpeech = new TTStoSpeech(this);
        Log.d(TAG, "onCreate ===== ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand ===== ");
        if(wakeLock != null){
            wakeLock.release();
            wakeLock = null;
        }
        if(apps == null || apps.length == 0){
            getInstalledApps();		//获取本机程序
        }

        ttStoSpeech.speek("请说");
        mHandler.sendEmptyMessageDelayed(0, 400);


        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startRecognition(apps);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    /**
     * 开启一次识别任务
     */
    private void start() {
        ComponentName com = new ComponentName("com.lenovo.lasf",
                "com.lenovo.lasf.speech.LasfService");
        SpeechRecognizer r = SpeechRecognizer.createSpeechRecognizer(this, com);
        r.setRecognitionListener(mReListener);
        Intent recognizerIntent = new Intent();
        // recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
        // "打电话给<name>" });
        // recognizerIntent.putExtra("<name>", new String[] { "王小虎", "王晓虎" });
        // recognizerIntent.putExtra("<place>", new String[] { "上地", "联想" }); ,
        // "从<place>到<place>怎么走"
        recognizerIntent.putExtra("speech_sce", "cmd");
        recognizerIntent.putExtra("speech_domain", "all"); // 识别联系人领域

        r.startListening(recognizerIntent);
    }


    public void startRecognition(String[] apps) {
        long time1 = System.currentTimeMillis();

        //readAllContacts();		//获取本机联系人
        if(apps == null || apps.length == 0){
            getInstalledApps();		//获取本机程序
        }

        Log.i(TAG, "" + (System.currentTimeMillis() - time1));
        ComponentName com = new ComponentName("com.lenovo.lasf",
                "com.lenovo.lasf.speech.LasfService");
        r = SpeechRecognizer.createSpeechRecognizer(this, com);
        r.setRecognitionListener(mReListener);
        Intent recognizerIntent = new Intent();
        /*recognizerIntent.putExtra("sound_start", R.raw.bdspeech_recognition_start);
        recognizerIntent.putExtra("sound_success", R.raw.bdspeech_recognition_success);
        recognizerIntent.putExtra("sound_end", R.raw.bdspeech_speech_end);
        recognizerIntent.putExtra("sound_error", R.raw.bdspeech_recognition_error);*/
        recognizerIntent.putExtra("<app>", new String[] { "打开<apps>","<apps>"});
        recognizerIntent.putExtra("<apps>", apps);
        //recognizerIntent.putExtra("<was>", new String[]{"百度","新浪","人人","网易"});
        //recognizerIntent.putExtra("<vod>", new String[]{"小时代","功夫","致我们终将逝去的青春","霍比特人"});


        // recognizerIntent.putExtra("<place>", new String[] { "上地", "联想" }); ,
        // "从<place>到<place>怎么走"

        recognizerIntent.putExtra("speech_domain", "all"); // 识别联系人领域
        r.startListening(recognizerIntent);
    }

    public void stopRecognition(){
        if(r != null){
            Log.d(TAG, "stopRecognition");
            //r.stopListening();
            //r.cancel();
            r.destroy();
        }
    }

    private RecognitionListener mReListener = new RecognitionListener() {

        @Override
        public void onRmsChanged(final float rmsdB) {
            Log.d(TAG, "onRmsChanged = " + rmsdB);

        }

        @Override
        public void onResults(Bundle results) {

            List<String> rr = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            List<String> list = results
                    .getStringArrayList("results_recognition");
            String jo = results.getString("nlp_result_origin");

            Log.i(TAG, "" + rr.size() + "\n" + jo + "\n" + list);

            if (rr.size() > 0) {
                StringBuffer tt = new StringBuffer();
                for (int m = 0; m < rr.size(); m++) {
                    tt.append(rr.get(m)).append("\r\n");
                    Log.i(TAG, tt.toString());
                }

                //final String t = tt.toString();
                final String t = rr.get(0);
                endtime = System.currentTimeMillis();

                if(t.contains("打开投影")){
                    wakeUp();
                    return;
                }


                if(t.contains("打开")){
                    String app=t.replace("打开", "");
                    Log.i(TAG, app);
                    String pn=appinfo.get(app.trim());

                    Log.i(TAG,"size: "+appinfo.size());

                    openApp(pn);
                }else {
                    ttStoSpeech.speek("无法理解您的意思");
                }

                long time = endtime - begintime;
                Log.d(TAG, "onResults---:" + t +"\r\n"+ jo +"\r\n" + "使用时间"
                        + time);


            }


        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech 可以说话了");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            List<String> rr = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (rr.size() > 0) {
                final String t = rr.get(0);
                Log.d(TAG, "onPartialResults = " + t);

            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(final int error) {
            // TODO Auto-generated method stub
            ttStoSpeech.speek("我不知道你在说什么");
            switch (error) {
                case 1:
                    Log.d(TAG, "出错了 " + error + "网络超时\r\n");
                    break;
                case 2:
                    Log.d(TAG, "出错了 " + error + "网络错误\r\n");
                    break;
                case 3:
                    Log.d(TAG, "出错了 " + error + "录音出错\r\n");
                    break;
                case 4:
                    Log.d(TAG, "出错了 " + error + "服务器返回错误状态\r\n");
                    break;
                case 5:
                    Log.d(TAG, "出错了 " + error
                            + "客户端调用错误，如识别结果返回之前再次请求识别\r\n");
                    break;
                case 6:
                    Log.d(TAG, "出错了 " + error + "无语音输入\r\n");
                    break;
                case 7:
                    Log.d(TAG, "出错了 " + error + "没有与输入的语音匹配的识别结果\r\n");
                    break;
                case 8:
                    Log.d(TAG, "出错了 " + error + "引擎忙\r\n");
                    break;
                default:
                    Log.d(TAG, "出错了 " + error + "");

            }
        }

        @Override
        public void onEndOfSpeech() {
            begintime = System.currentTimeMillis();
            Log.d(TAG, "onEndOfSpeech 收音结束");

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech ");

        }
    };

    public void readAllContacts() {
        ArrayList<String> contacts = new ArrayList<String>();

        Cursor cursor = getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                        null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIdIndex = cursor
                    .getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            contacts.add(name);
        }
        names = new String[contacts.size()];
        for (int i = 0, j = contacts.size(); i < j; i++) {
            names[i] = contacts.get(i);
        }
        Log.i("本机程序", ""+names.toString());
        cursor.close();

    }

    /***
     * 获取本地程序列表
     * **/
    private void getInstalledApps() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>(packages.size());

        ArrayList<String> app=new ArrayList<String>();
        for (int j = 0; j < packages.size(); j++) {
            Map<String, Object> map = new HashMap<String, Object>();

            PackageInfo packageInfo = packages.get(j);
            //显示非系统软件
            //if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0){
            map.put("img", packageInfo.applicationInfo.loadIcon(getPackageManager()).getCurrent());
            map.put("name", packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            app.add(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());

            appinfo.put(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(), packageInfo.packageName);

            Log.i(TAG, packageInfo.applicationInfo.loadLabel(getPackageManager()).toString()+packageInfo.packageName);
            map.put("desc", packageInfo.packageName);
            listMap.add(map);
            //}
        }
        apps = new String[app.size()];
        for (int i = 0, j = app.size(); i < j; i++) {
            Log.d(TAG, "app name = " + app.get(i));
            apps[i] = app.get(i);
        }

    }

    private void openApp(String packageName) {
        if(packageName != null){
            PackageManager packageManager = getPackageManager();
            Intent intent=new Intent();
            intent =packageManager.getLaunchIntentForPackage(packageName);
            startActivity(intent);
            ttStoSpeech.speek("正在帮你打开");
        }else {
            ttStoSpeech.speek("无法理解您的意思");
            Log.d(TAG, "package is null .... ");
        }

    }


    private PowerManager.WakeLock wakeLock;
    private void wakeUp(){
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.SCREEN_DIM_WAKE_LOCK, "Voice");
        wakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory");
        stopRecognition();
        ttStoSpeech.releaseTTS();
    }
}
