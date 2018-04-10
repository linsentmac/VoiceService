package cn.lenovo.voiceservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.lenovo.voiceservice.service.WakeUpService;

/**
 * Created by tmac on 18-4-10.
 */

public class WakeUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Intent wakeUpIntent = new Intent(context, WakeUpService.class);
        context.startService(wakeUpIntent);*/
    }

}
