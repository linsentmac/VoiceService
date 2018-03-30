package cn.lenovo.voiceservice;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

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
import cn.lenovo.voiceservice.utils.StatusBarUtils;
import cn.lenovo.voiceservice.view.SeismicWave;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends Activity {

    private static final String TAG = "SC-RecordActivity";
    private ImageView record_ball;
    private GifImageView gifImageView;
    private TTStoSpeech ttStoSpeech;
    private SeismicWave seismicWave;
    private SpeechRecognizer r;

    private static final boolean SEND_CAST = false;
    private final String VOICE_ACTION = "cn.lenovo.voiceservice.VOICE_SERVICE";
    private final String VOICE_EXTRA = "VOCON_RESULT";

    private Timer timer;
    private ActivityManager manager;
    private final String LENOVO_HOME_PACKAGE = "com.turing.tlpa";

    private final long DELAY = 1000;
    private final long PERIOD = 2000;

    static Map<String, String> appinfo = new HashMap<String, String>();
    long begintime, endtime;
    String names[];
    String apps[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        StatusBarUtils.hideNavgationBar(this);
        Log.d(TAG, "onCreate");
        initViews();
        //initEvents();
        getInstalledApps();
        initRecognition(apps);

        ttStoSpeech = new TTStoSpeech(this);
        manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        timer = new Timer(true);
        timer.schedule(task, DELAY, PERIOD);
        Log.d(TAG, "onCreate ===== ");
        requestPower();

    }

    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                Log.d(TAG, "request permission ..... ");
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        } else {
            Log.d(TAG, "hava permission ..... ");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        if (apps == null || apps.length == 0) {
            getInstalledApps();        //获取本机程序
        }
        startRecognition(apps);
    }

    private Intent recognizerIntent;

    private void initRecognition(String[] apps) {
        long time1 = System.currentTimeMillis();

        //readAllContacts();		//获取本机联系人
        if (apps == null || apps.length == 0) {
            getInstalledApps();        //获取本机程序
        }

        Log.i(TAG, "" + (System.currentTimeMillis() - time1));
        ComponentName com = new ComponentName(this,
                VoiceRecognitionService.class);
        r = SpeechRecognizer.createSpeechRecognizer(this, com);
        r.setRecognitionListener(mReListener);
        recognizerIntent = new Intent();
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);

        recognizerIntent.putExtra("<app>", new String[]{"打开<apps>", "<apps>"});
        recognizerIntent.putExtra("<apps>", apps);
        recognizerIntent.putExtra("<screen>", new String[]{"打开投影", "关闭投影"});
        recognizerIntent.putExtra("<calibration>", new String[]{"重新校准"});
        //recognizerIntent.putExtra("<was>", new String[]{"百度","新浪","人人","网易"});
        //recognizerIntent.putExtra("<vod>", new String[]{"小时代","功夫","致我们终将逝去的青春","霍比特人"});


        // recognizerIntent.putExtra("<place>", new String[] { "上地", "联想" }); ,
        // "从<place>到<place>怎么走"

        recognizerIntent.putExtra("speech_domain", "all"); // 识别联系人领域
    }


    public void startRecognition(String[] apps) {
        /*long time1 = System.currentTimeMillis();

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

        recognizerIntent.putExtra("speech_domain", "all"); // 识别联系人领域*/
        if (r != null) {
            r.startListening(recognizerIntent);
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

                if (SEND_CAST) {
                    Intent broadcastIntent = new Intent(VOICE_ACTION);
                    broadcastIntent.putExtra(VOICE_EXTRA, t);
                    sendBroadcast(broadcastIntent);
                }

                if (t.contains("打开投影")) {
                    wakeUp();
                    /*Intent intent = new Intent("android.intent.action.ACTION_PICO_ON");
                    sendBroadcast(intent);*/
                    return;
                }

                if (t.contains("关闭投影")) {
                    Intent intent = new Intent("android.intent.action.ACTION_PICO_OFF");
                    sendBroadcast(intent);
                    return;
                }

                if (t.contains("重新校准")) {
                    Intent intent = new Intent("com.android.gscalibration.RESTART");
                    sendBroadcast(intent);
                    return;
                }


                if (t.contains("打开")) {
                    String app = t.replace("打开", "");
                    Log.i(TAG, app);
                    String pn = appinfo.get(app.trim());

                    Log.i(TAG, "size: " + appinfo.size());

                    openApp(pn, t);
                } else if (t.contains("今天天气")) {
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
                            if (reply == null || reply.equals("") || reply.trim() == null) {
                                //startHintActivity(result);
                                return;
                            }
                            String weekWeather = response.body().getIntent().get(0).get未来7天天气();
                            MyApplication.setWeekWeather(weekWeather);
                            Log.i(TAG, "weekWeather Info :" + weekWeather);
                            Gson gson = new Gson();
                            List<WeekWeatherBean> weekList = gson.fromJson(weekWeather, new TypeToken<List<WeekWeatherBean>>() {
                            }.getType());
                            //Log.i(TAG, "weekWeather weather :" + weekList.get(0).getWeather());

                            if (domain.equals("天气")) {
                                Intent intent = new Intent(RecordActivity.this, WeatherActivity.class);
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
                } else {
                    startHintActivity(t);
                }

                long time = endtime - begintime;
                Log.d(TAG, "onResults---:" + t + "\r\n" + jo + "\r\n" + "使用时间"
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
            startHintActivity(null);
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
                    release();
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

    public void stopRecognition() {
        if (r != null) {
            Log.d(TAG, "stopRecognition");
            //r.stopListening();
            //r.cancel();
            r.destroy();
        }
    }

    private void startHintActivity(String result) {
        Intent intent = new Intent(RecordActivity.this, SpeekHintActivity.class);
        intent.putExtra("isResult", true);
        intent.putExtra("Result", result);
        startActivity(intent);
    }

    private PowerManager.WakeLock wakeLock;

    private void wakeUp() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "Voice");
        wakeLock.acquire();
    }

    /***
     * 获取本地程序列表
     * **/
    private void getInstalledApps() {
        /*List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
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
        }*/

        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ArrayList<String> app = new ArrayList<String>();
        // get all apps
        final List<ResolveInfo> allApps = packageManager.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < allApps.size(); i++) {
            ResolveInfo resolveInfo = allApps.get(i);
            app.add(resolveInfo.activityInfo.loadLabel(packageManager).toString());
            appinfo.put(resolveInfo.activityInfo.loadLabel(getPackageManager()).toString(), resolveInfo.activityInfo.packageName);
        }

        apps = new String[app.size()];
        for (int i = 0, j = app.size(); i < j; i++) {
            Log.d(TAG, "app name = " + app.get(i));
            apps[i] = app.get(i);
        }

    }


    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            List<ActivityManager.RunningTaskInfo> runningTask = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTask.get(0);
            String packageName = runningTaskInfo.topActivity.getPackageName();
            Log.d(TAG, "packageName" + packageName);
            if (packageName.equals(LENOVO_HOME_PACKAGE)) {
                release();
            }
        }
    };

    private void initViews() {
        seismicWave = findViewById(R.id.seismicwave);
        seismicWave.reStart().start();
        gifImageView = findViewById(R.id.ball_gif);
        //record_ball = findViewById(R.id.record_ball_gif);
        //Glide.with(this).load(R.mipmap.keywest_ball_1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(record_ball);
    }


    private void openApp(String packageName, String result) {
        if (packageName != null) {
            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent();
            intent = packageManager.getLaunchIntentForPackage(packageName);
            startActivity(intent);
            ttStoSpeech.speek("正在帮你打开");
        } else {
            ttStoSpeech.speek("无法理解您的意思");
            startHintActivity(result);
            Log.d(TAG, "package is null .... ");
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory");
        release();
    }

    private void release() {
        stopRecognition();
        if (ttStoSpeech != null) {
            ttStoSpeech.releaseTTS();
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
    }
}
