package cn.lenovo.voiceservice.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.lenovo.voiceservice.music.service.AudioPlayer;

/**
 * 来电/耳机拔出时暂停播放
 * Created by chao on 2018/4/1.
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AudioPlayer.get().playPause();
    }
}
