package cn.lenovo.voiceservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.lenovo.voiceservice.RecordActivity;
import cn.lenovo.voiceservice.SCVoiceService;

/**
 * Created by linsen on 18-1-30.
 */

public class VoiceBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "SC-VoiceReceiver";

    private Handler mHandler;
    public VoiceBroadCastReceiver(Handler handler){
        mHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
            String packageName = intent.getDataString().split(":")[1];
            Log.d(TAG, "add packageName = " + packageName);
            Message msg = new Message();
            msg.what = RecordActivity.PACKAGE_ADD;
            msg.obj = packageName;
            mHandler.sendMessage(msg);
        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
            String packageName = intent.getDataString().split(":")[1];
            Log.d(TAG, "add packageName = " + packageName);
            Message msg = new Message();
            msg.what = RecordActivity.PACKAGE_REMOVE;
            msg.obj = packageName;
            mHandler.sendMessage(msg);
        }
    }
}
