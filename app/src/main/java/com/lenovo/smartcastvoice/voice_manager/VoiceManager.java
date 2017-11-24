package com.lenovo.smartcastvoice.voice_manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.lenovo.smartcastvoice.VoiceService;
import com.lenovo.smartcastvoice.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by linsen on 17-11-23.
 */

public class VoiceManager {

    private static VoiceManager mInstance;
    private static final String TAG = "SC-VoiceManager";

    private Context mContext;
    static Map<String,String> appinfo = new HashMap<String, String>();
    long begintime, endtime;
    String names[];
    String apps[];
    private SpeechRecognizer r;
    private VoiceRecognitionListener mListenr;


    public VoiceManager(Context context, VoiceRecognitionListener listener){
        mContext = context;
        mListenr = listener;
    }

    /**
     * 开启一次识别任务
     */
    private void start() {
        ComponentName com = new ComponentName("com.lenovo.lasf",
                "com.lenovo.lasf.speech.LasfService");
        SpeechRecognizer r = SpeechRecognizer.createSpeechRecognizer(mContext, com);
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

    private void startWakeUp(){
        ComponentName com = new ComponentName("com.lenovo.lasf",
                "com.lenovo.lasf.speech.LasfService");
        SpeechRecognizer r = SpeechRecognizer.createSpeechRecognizer(mContext, com);
        r.setRecognitionListener(mReListener);
        Intent recognizerIntent = new Intent();

        recognizerIntent.putExtra("<main>", "你好联想");

        /*recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
                "<name>", "打电话给<name>","打开<apps>","<apps>","<was>网","<was>","<vod>","你好联想" });

        recognizerIntent.putExtra("<name>", names);
        recognizerIntent.putExtra("<apps>", apps);
        recognizerIntent.putExtra("<was>", new String[]{"百度","新浪","人人","网易"});
        recognizerIntent.putExtra("<vod>", new String[]{"小时代","功夫","致我们终将逝去的青春","霍比特人"});



        // recognizerIntent.putExtra("<place>", new String[] { "上地", "联想" }); ,
        // "从<place>到<place>怎么走"

        recognizerIntent.putExtra("speech_domain", "all"); // 识别联系人领域*/

        r.startListening(recognizerIntent);
    }


    public void startRecognition() {
        long time1 = System.currentTimeMillis();

        readAllContacts();		//获取本机联系人
        getInstalledApps();		//获取本机程序

        Log.i("读取联系人时间", "" + (System.currentTimeMillis() - time1));
        ComponentName com = new ComponentName("com.lenovo.lasf",
                "com.lenovo.lasf.speech.LasfService");
        r = SpeechRecognizer.createSpeechRecognizer(mContext, com);
        r.setRecognitionListener(mReListener);
        Intent recognizerIntent = new Intent();
        recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
                "<name>", "打电话给<name>","打开<apps>","<apps>","<was>网","<was>","<vod>","你好联想" });

        recognizerIntent.putExtra("<name>", names);
        recognizerIntent.putExtra("<apps>", apps);
        recognizerIntent.putExtra("<was>", new String[]{"百度","新浪","人人","网易"});
        recognizerIntent.putExtra("<vod>", new String[]{"小时代","功夫","致我们终将逝去的青春","霍比特人"});



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
            /*runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final android.view.ViewGroup.LayoutParams lp = voice.getLayoutParams();

                    lp.width = (int) (rmsdB / 20);
                    voice.setLayoutParams(lp);
//					voice.setWidth((int) (rmsdB / 100));
                    pb.setProgress((int) (rmsdB / 100));
                    // tvMsg.append("当前音量" + rmsdB + "\r\n");
                }
            });*/
        }

        @Override
        public void onResults(Bundle results) {

            List<String> rr = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            List<String> list = results
                    .getStringArrayList("results_recognition");
            String jo = results.getString("nlp_result_origin");

            Log.i("返回结果", "" + rr.size() + jo + list);

            if (rr.size() > 0) {
                StringBuffer tt = new StringBuffer();
                for (int m = 0; m < rr.size(); m++) {
                    tt.append(rr.get(m)).append("\r\n");
                    Log.i("tt", tt.toString());
                }

                final String t = tt.toString();
                endtime = System.currentTimeMillis();

                mListenr.asrSuccess(t);

                /*if(t.contains("打开")){
                    String app=t.replace("打开", "");
                    Log.i("打开", app);
                    String pn=appinfo.get(app.trim());

                    Log.i("打开程序","size: "+appinfo.size());

                    openApp(pn);
                }else if(t.contains("你好联想")){

                }else {
                    Log.d(TAG, "t = " + t);
                }*/

                long time = endtime - begintime;
                Log.d(TAG, "onResults---:" + t +"\r\n"+ jo +"\r\n" + "使用时间"
                        + time);
                /*tvMsg.append("onResults---:" + t +"\r\n"+ jo +"\r\n" + "使用时间"
                        + time);*/
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						endtime = System.currentTimeMillis();
//
//						if(t.contains("打开")){
//							String app=t.replace("打开", "");
//							Log.i("打开", app);
//							String pn=appinfo.get(app);
//						Log.i("打开程序",pn);
////							openApp(pm);
//						}
//
//						long time = endtime - begintime;
//						tvMsg.append("onResults---:" + t + "\r\n" + "使用时间"
//								+ time);
//					}
//				});
            }

            Bundle bundle = new Bundle();

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech 可以说话了");
            /*runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tvMsg.append("onReadyForSpeech	可以说话了\r\n");
                }
            });*/
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            List<String> rr = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (rr.size() > 0) {
                final String t = rr.get(0);
                Log.d(TAG, "onPartialResults = " + t);
                /*runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tvMsg.append(t + "\r\n");
                    }
                });*/
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(final int error) {
            // TODO Auto-generated method stub

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
            /*runOnUiThread(new Runnable() {

                @Override
                public void run() {

                }
            });*/
        }

        @Override
        public void onEndOfSpeech() {
            begintime = System.currentTimeMillis();
            Log.d(TAG, "onEndOfSpeech 收音结束");
            /*runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    begintime = System.currentTimeMillis();
                    tvMsg.append("onEndOfSpeech 收音结束" + "\r\n");
                }
            });*/
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech ");
            /*runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tvMsg.append("onBeginningOfSpeech " + "\r\n");
                }
            });*/
        }
    };

    public void readAllContacts() {
        ArrayList<String> contacts = new ArrayList<String>();

        Cursor cursor = mContext
                .getContentResolver()
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
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
        List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>(packages.size());

        ArrayList<String> app=new ArrayList<String>();
        for (int j = 0; j < packages.size(); j++) {
            Map<String, Object> map = new HashMap<String, Object>();

            PackageInfo packageInfo = packages.get(j);
            //显示非系统软件
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0){
                map.put("img", packageInfo.applicationInfo.loadIcon(mContext.getPackageManager()).getCurrent());
                map.put("name", packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString());
                app.add(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString());

                appinfo.put(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString(), packageInfo.packageName);

                Log.i("chengxu", packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString()+packageInfo.packageName);
                map.put("desc", packageInfo.packageName);
                listMap.add(map);
            }
        }
        apps = new String[app.size()];
        for (int i = 0, j = app.size(); i < j; i++) {
            apps[i] = app.get(i);
        }
//		Log.i("本机程序", ""+apps.toString());
        //		return listMap;
    }
    private void openApp(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent=new Intent();
        intent =packageManager.getLaunchIntentForPackage(packageName);
        mContext.startActivity(intent);
    }


}
