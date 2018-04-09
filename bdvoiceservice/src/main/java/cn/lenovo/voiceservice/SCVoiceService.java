package cn.lenovo.voiceservice;

import android.app.ActivityManager;
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

import com.baidu.speech.VoiceRecognitionService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.lenovo.voiceservice.httpRequest.AppClient;
import cn.lenovo.voiceservice.jsonbean.WeatherBean;
import cn.lenovo.voiceservice.jsonbean.WeekWeatherBean;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by linsen on 18-1-25.
 */

public class SCVoiceService extends Service {

    private static final String TAG = "SC-VoiceService";

    private static final boolean SEND_CAST = false;
    private final String VOICE_ACTION = "cn.lenovo.voiceservice.VOICE_SERVICE";
    private final String VOICE_EXTRA = "VOCON_RESULT";

    static Map<String,String> appinfo = new HashMap<String, String>();
    long begintime, endtime;
    String names[];
    String apps[];
    private SpeechRecognizer r;
    private TTStoSpeech ttStoSpeech;
    private boolean isFirstLoad;
    private final String LENOVO_HOME_PACKAGE = "com.turing.tlpa";
    private Timer timer;
    private ActivityManager manager;

    private final long DELAY = 1000;
    private final long PERIOD = 2000;

    @Override
    public void onCreate() {
        super.onCreate();
        ttStoSpeech = TTStoSpeech.getInstance(this);
        manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        timer = new Timer(true);
        timer.schedule(task, DELAY, PERIOD);
        Log.d(TAG, "onCreate ===== ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand ===== ");
        if(wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        if(apps == null || apps.length == 0) {
            getInstalledApps();		//获取本机程序
        }
        startRecognition(apps);

        return super.onStartCommand(intent, flags, startId);
    }


    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            List<ActivityManager.RunningTaskInfo> runningTask = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTask.get(0);
            String packageName = runningTaskInfo.topActivity.getPackageName();
            Log.d(TAG, "packageName" + packageName);
            if(packageName.equals(LENOVO_HOME_PACKAGE)){
                stopSelf();
            }
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
        ComponentName com = new ComponentName(this,
                VoiceRecognitionService.class);
        r = SpeechRecognizer.createSpeechRecognizer(this, com);
        r.setRecognitionListener(mReListener);
        Intent recognizerIntent = new Intent();
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);

        recognizerIntent.putExtra("<app>", new String[] { "打开<apps>","<apps>"});
        recognizerIntent.putExtra("<apps>", apps);
        recognizerIntent.putExtra("<screen>", new String[]{"打开投影", "关闭投影"});
        recognizerIntent.putExtra("<calibration>", new String[]{"重新校准"});
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

            Log.i(TAG, "onResult = " + rr.size() + "\n" + jo + "\n" + list);

            if (rr.size() > 0) {
                StringBuffer tt = new StringBuffer();
                for (int m = 0; m < rr.size(); m++) {
                    tt.append(rr.get(m)).append("\r\n");
                    Log.i(TAG, " append " + tt.toString() + "\n");
                }

                //final String t = tt.toString();
                final String t = rr.get(0);
                endtime = System.currentTimeMillis();

                if(SEND_CAST){
                    Intent broadcastIntent = new Intent(VOICE_ACTION);
                    broadcastIntent.putExtra(VOICE_EXTRA, t);
                    sendBroadcast(broadcastIntent);
                }

                if(t.contains("打开投影")){
                    wakeUp();
                    /*Intent intent = new Intent("android.intent.action.ACTION_PICO_ON");
                    sendBroadcast(intent);*/
                    return;
                }

                if(t.contains("关闭投影")){
                    Intent intent = new Intent("android.intent.action.ACTION_PICO_OFF");
                    sendBroadcast(intent);
                    return;
                }

                if(t.contains("重新校准")){
                    Intent intent = new Intent("com.android.gscalibration.RESTART");
                    sendBroadcast(intent);
                    return;
                }


                if(t.contains("打开")){

                    String app=t.replace("打开", "");
                    Log.i(TAG, app);
                    String pn=appinfo.get(app.trim());

                    Log.i(TAG,"size: "+appinfo.size());

                    openApp(pn);
                }else if(t.contains("今天天气")){
                    //ttStoSpeech.speek("无法理解您的意思");
                    String url = AppClient.commonUrl + "sentence=" + t + "&userid=55/";
                    AppClient.ApiStores apiStores = AppClient.retrofit(url).create(AppClient.ApiStores.class);
                    /*RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    new Gson().toJson(new Enity("30921")));*/
                    Call<WeatherBean> call = apiStores.getDomainBean(t, 55, "上海上海");
                    call.enqueue(new Callback<WeatherBean>() {
                        @Override
                        public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                            String reply = response.body().getReply();
                            String domain = response.body().getDomain();
                            Log.i(TAG, "Request Info :" + reply);
                            if(reply == null || reply.equals("") || reply.trim() == null){
                                //startHintActivity(result);
                                return;
                            }
                            String weekWeather = response.body().getIntent().get(0).get未来7天天气();
                            MyApplication.setWeekWeather(weekWeather);
                            Log.i(TAG, "weekWeather Info :" + weekWeather);
                            Gson gson = new Gson();
                            List<WeekWeatherBean> weekList = gson.fromJson(weekWeather, new TypeToken<List<WeekWeatherBean>>(){}.getType());
                            //Log.i(TAG, "weekWeather weather :" + weekList.get(0).getWeather());

                            if(domain.equals("天气")){
                                Intent intent = new Intent(SCVoiceService.this, WeatherActivity.class);
                                intent.putExtra("result", t);
                                intent.putExtra("reply", reply);
                                //intent.putExtra("weekWeather", weekWeather);
                                startActivity(intent);
                            }
                            ttStoSpeech.speek(reply);
                        }

                        @Override
                        public void onFailure(Call<WeatherBean> call, Throwable t) {
                            Log.d(TAG, "onFailure = " + t.getMessage());
                            //startHintActivity(result);
                        }
                    });
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
            //ttStoSpeech.speek("我不知道你在说什么");
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
                    stopSelf();
                    break;

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
        if(timer != null){
            timer.purge();
            timer.cancel();
        }
    }
}
