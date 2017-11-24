package com.lenovo.smartcastvoice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lenovo.smartcastvoice.VoiceService;

/**
 * Created by linsen on 17-11-21.
 */

public class SmartCastReceiver extends BroadcastReceiver {

    private static final String TAG = "SC-SmartCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "action = " + action);
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, VoiceService.class);
            serviceIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(serviceIntent);
        }
    }
}
