package cn.lenovo.voiceservice.music.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.lenovo.voiceservice.R;
import cn.lenovo.voiceservice.music.db.DBManager;
import cn.lenovo.voiceservice.music.model.Music;
import cn.lenovo.voiceservice.music.other.ForegroundObserver;
import cn.lenovo.voiceservice.music.service.AudioPlayer;
import cn.lenovo.voiceservice.music.service.OnPlayerEventListener;
import cn.lenovo.voiceservice.music.service.PlayService;
import cn.lenovo.voiceservice.music.utils.PermissionReq;
import cn.lenovo.voiceservice.music.utils.SystemUtils;

import cn.lenovo.voiceservice.utils.StatusBarUtils;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * MusicActivity
 * Created by chao on 2018/4/1.
 */
public class MusicActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnPlayerEventListener {
    public static final String TAG = "MusicActivity";
    private Context mContext = MusicActivity.this;

    private TextView tv_reco_title, tv_song_name, tv_singer_name, tv_play_time, tv_total_time;
    private ImageView iv_music_last, iv_music_last_anim, iv_music_play_paused, iv_music_play_paused_anim, iv_music_next, iv_music_next_anim, iv_start_voice_reco, iv_start_voice_reco_anim;
    private GifImageView pl_gifView;
    private SeekBar mSeekBar;
    private GifDrawable mGifDrawable;

    protected Handler handler;
    protected PlayService playService;
    private ServiceConnection serviceConnection;

    private int mLastProgress;
    private boolean isDraggingProgress;

    private Music recoMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        StatusBarUtils.hideNavgationBar(this);

        //从语音识别界面传过来的要播放的歌曲
        recoMusic = (Music) getIntent().getSerializableExtra("music");
        initView();
        addListener();
        initPlayer();
    }

    private void initView() {
        tv_reco_title = findViewById(R.id.tv_reco_title);
        tv_song_name = findViewById(R.id.tv_song_name);
        tv_singer_name = findViewById(R.id.tv_singer_name);
        tv_play_time = findViewById(R.id.tv_play_time);
        tv_total_time = findViewById(R.id.tv_total_time);
        iv_music_last = findViewById(R.id.iv_music_last);
        iv_music_last_anim = findViewById(R.id.iv_music_last_anim);
        iv_music_play_paused = findViewById(R.id.iv_music_play_paused);
        iv_music_play_paused_anim = findViewById(R.id.iv_music_play_paused_anim);
        iv_music_next = findViewById(R.id.iv_music_next);
        iv_music_next_anim = findViewById(R.id.iv_music_next_anim);
        iv_start_voice_reco = findViewById(R.id.iv_start_voice_reco);
        iv_start_voice_reco_anim = findViewById(R.id.iv_start_voice_reco_anim);
        pl_gifView = findViewById(R.id.pl_gifView);
        mSeekBar = findViewById(R.id.mSeekBar);
        mGifDrawable = (GifDrawable) pl_gifView.getDrawable();
        mGifDrawable.stop();
    }

    private void addListener() {
        iv_music_last.setOnClickListener(this);
        iv_music_play_paused.setOnClickListener(this);
        iv_music_next.setOnClickListener(this);
        iv_start_voice_reco.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initPlayer() {
        ForegroundObserver.init(this.getApplication());
        DBManager.get().init(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        handler = new Handler(Looper.getMainLooper());

        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        serviceConnection = new PlayServiceConnection();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        AudioPlayer.get().addOnPlayEventListener(this);
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = ((PlayService.PlayBinder) service).getService();
            if (recoMusic != null) AudioPlayer.get().addAndPlay(recoMusic);
            onChangeImpl(AudioPlayer.get().getPlayMusic());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(getClass().getSimpleName(), "service disconnected");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music_last://上一首
                AudioPlayer.get().prev();
                startClickAnim(iv_music_last_anim);
                break;

            case R.id.iv_music_play_paused://暂停或播放
                if (AudioPlayer.get().isPreparing() || AudioPlayer.get().isPlaying()) {
                    iv_music_play_paused.setImageResource(R.mipmap.music_play);
                    mGifDrawable.stop();
                } else {
                    iv_music_play_paused.setImageResource(R.mipmap.music_paused);
                    mGifDrawable.start();
                }
                AudioPlayer.get().playPause();
                startClickAnim(iv_music_play_paused_anim);
                break;

            case R.id.iv_music_next://下一首
                AudioPlayer.get().next();
                startClickAnim(iv_music_next_anim);
                break;

            case R.id.iv_start_voice_reco://启动语音识别界面
                startClickAnim(iv_start_voice_reco_anim);
                new Handler().postDelayed(() -> {
                    if (!isFinishing()) finish();
                }, 600);
                break;
        }
    }

    //按钮点击动画
    private void startClickAnim(View view) {
        ((ImageView) view).setImageResource(R.drawable.click_animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) view).getDrawable();
        animationDrawable.start();
    }

    private void onChangeImpl(Music music) {
        if (music == null) return;

        tv_song_name.setText(music.getTitle());
        tv_singer_name.setText(music.getArtist());
        mSeekBar.setProgress((int) AudioPlayer.get().getAudioPosition());
        mSeekBar.setSecondaryProgress(0);
        mSeekBar.setMax((int) music.getDuration());
        mLastProgress = 0;
        tv_play_time.setText("00:00");
        tv_total_time.setText(formatTime(music.getDuration()));
        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
            iv_music_play_paused.setImageResource(R.mipmap.music_paused);
            mGifDrawable.start();
        } else {
            iv_music_play_paused.setImageResource(R.mipmap.music_play);
            mGifDrawable.stop();
        }
    }

    // PlayerEvent Listener
    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        iv_music_play_paused.setImageResource(R.mipmap.music_paused);
        mGifDrawable.start();
    }

    @Override
    public void onPlayerPause() {
        iv_music_play_paused.setImageResource(R.mipmap.music_play);
        mGifDrawable.stop();
    }

    //更新播放进度
    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            mSeekBar.setProgress(progress);
        }
    }

    //缓冲百分比
    @Override
    public void onBufferingUpdate(int percent) {
        mSeekBar.setSecondaryProgress(mSeekBar.getMax() * 100 / percent);
    }

    // SeekBar ProgressChanged listener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mSeekBar) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tv_play_time.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == mSeekBar) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == mSeekBar) {
            isDraggingProgress = false;
            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
                int progress = seekBar.getProgress();
                AudioPlayer.get().seekTo(progress);
            } else {
                seekBar.setProgress(0);
            }
        }
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();
    }
}
