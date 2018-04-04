package cn.lenovo.voiceservice.story;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by yunduo on 18-3-30.
 */

public class PlayVoice {
    private static MediaPlayer mediaPlayer;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    //开始播放声音
    public static void playVoice(Context context, int storyMp3){
        try {
            mediaPlayer= MediaPlayer.create(context, storyMp3 );
//            mediaPlayer=new MediaPlayer();
//            mediaPlayer.setDataSource("/sdcard/baixue.mp3");
//            AssetFileDescriptor fileDescriptor = getAssets().openFd("rain.mp3");
            mediaPlayer.start();
            //播完重新播放
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停止播放声音
    public  static void stopVoice(){
        if(null!=mediaPlayer) {
            mediaPlayer.stop();
        }
    }
}
