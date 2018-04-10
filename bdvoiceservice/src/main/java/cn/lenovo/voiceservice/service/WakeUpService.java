package cn.lenovo.voiceservice.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by tmac on 18-4-10.
 */

public class WakeUpService extends Service {

    private static final String TAG = "SC-WakeUpService";

    @Override
    public void onCreate() {
        super.onCreate();
        wakeUp();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private PowerManager.WakeLock wakeLock;
    private final String MUSIC_WAKE_TAG = "SCVoice";
    private void wakeUp() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, MUSIC_WAKE_TAG);
        wakeLock.acquire();
        Log.d(TAG, "start Wake up acquire .....");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wakeLock != null){
            wakeLock.release();
            Log.d(TAG, "Release Wake up lock .....");
        }
    }

}
