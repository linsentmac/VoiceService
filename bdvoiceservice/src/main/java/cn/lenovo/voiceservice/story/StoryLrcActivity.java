package cn.lenovo.voiceservice.story;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.lenovo.voiceservice.RecordActivity;
import cn.lenovo.voiceservice.SpeekHintActivity;
import cn.lenovo.voiceservice.lrc.LrcView;
import cn.lenovo.voiceservice.music.utils.SystemUtils;
import cn.lenovo.voiceservice.utils.AnimationUtils;
import cn.lenovo.voiceservice.utils.StatusBarUtils;


import cn.lenovo.voiceservice.R;

public class StoryLrcActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener{


    private ImageView iv_picture,iv_record,iv_start;
    private TextView tv_speaker,tv_storyName,tv_currentTime,tv_totalTime;
    private SeekBar mSeekBar;
    private LrcView lrcView;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();

    private int mLastProgress;

    private int type;
    private String storyName;
    private String storyResult;

    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_lrc);

        Intent intent = getIntent();
        if(intent != null){
            type = intent.getIntExtra("storyType", 0);
            storyName = intent.getStringExtra("storyName");
            storyResult = intent.getStringExtra("storyResult");
        }

        initView();
        initDatas(type, storyName);

        timer = new Timer();
        timer.schedule(task, 0, 300);


    }


    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtils.hideNavgationBar(this);
    }

    private void initView() {
        iv_picture=findViewById(R.id.iv_picture);
        iv_record=findViewById(R.id.iv_record);
        iv_start=findViewById(R.id.iv_start);
        tv_speaker=findViewById(R.id.tv_speaker);
        tv_storyName=findViewById(R.id.tv_storyName);
        tv_currentTime=findViewById(R.id.tv_currentTime);
        tv_totalTime=findViewById(R.id.tv_totalTime);
        mSeekBar =findViewById(R.id.seekBar);
        lrcView = findViewById(R.id.lrc_view);

        tv_currentTime.setText("00:00");
        tv_speaker.setText(storyResult);
    }

    private String[] storyNames = new String[]{"白雪公主", "灰姑娘", "卖火柴的小女孩", "睡美人", "小红帽"};
    private String[] storyMp3 = new String[]{"baixue.mp3", "hgn.mp3", "mhcdxnh.mp3", "smr.mp3", "xhm.mp3"};
    private String[] storyText = new String[]{"baixuegongzhu.txt", "huiguniang.txt", "maihuochaidexiaonvhai.txt", "shuimeiren.txt", "xiaohongmao.txt"};
    private int[] bxImage = new int[]{R.mipmap.baixuegongzhu_1, R.mipmap.baixuegongzhu_2, R.mipmap.baixuegongzhu_3};
    private int[] hgnImage = new int[]{R.mipmap.huiguniang_1, R.mipmap.huiguniang_2, R.mipmap.huiguniang_3, R.mipmap.huiguniang_4};
    private int[] mhcImage = new int[]{R.mipmap.maihuochaidexiaonvhai_1, R.mipmap.maihuochaidexiaonvhai_2, R.mipmap.maihuochaidexiaonvhai_3, R.mipmap.maihuochaidexiaonvhai_4, R.mipmap.maihuochaidexiaonvhai_5, R.mipmap.maihuochaidexiaonvhai_6};
    private int[] smrImage = new int[]{R.mipmap.shuimeiren_1, R.mipmap.shuimeiren_2, R.mipmap.shuimeiren_3, R.mipmap.shuimeiren_4, R.mipmap.shuimeiren_5, R.mipmap.shuimeiren_6};
    private int[] xhmImage = new int[]{R.mipmap.xiaohongmao_1, R.mipmap.xiaohongmao_2, R.mipmap.xiaohongmao_3};
    private List<int[]> imageList;


    private String dstStoryMp3;
    private String dstStoryText;
    private int[] dstImage;
    private String dstStoryName;

    private void initDatas(int type, String storyName) {
        imageList = new ArrayList<>();
        imageList.add(bxImage);
        imageList.add(hgnImage);
        imageList.add(mhcImage);
        imageList.add(smrImage);
        imageList.add(xhmImage);


        if (type == 0) {
            Random random = new Random();
            int position = random.nextInt(storyNames.length);
            dstStoryMp3 = storyMp3[position];
            dstStoryText = storyText[position];
            dstImage = imageList.get(position);
            dstStoryName = storyNames[position];
        } else if(type == 1){
            for(int i = 0; i < storyNames.length; i ++){
                if(storyNames[i].equals(storyName)){
                    dstStoryMp3 = storyMp3[i];
                    dstStoryText = storyText[i];
                    dstImage = imageList.get(i);
                    dstStoryName = storyNames[i];
                    break;
                }else if(i == storyNames.length - 1){
                    // 没找到故事, 跳到HintActivity
                    Intent intent = new Intent(StoryLrcActivity.this, SpeekHintActivity.class);
                    intent.putExtra("pkgName", "null");
                    intent.putExtra("appName", "null");
                    intent.putExtra("openApp", false);
                    intent.putExtra("isResult", true);
                    intent.putExtra("Result", storyName);
                    intent.putExtra("hintContent", "找不到该故事");
                    startActivity(intent);

                    return;
                }
            }
        }
        // 显示图片
        Drawable drawable = getResources().getDrawable(dstImage[0]);
        iv_picture.setImageDrawable(drawable);
        // 显示故事名
        tv_storyName.setText("好的，我们来讲" + dstStoryName + "故事");
        // 执行播放音频, 显示文字
        initMediaPlay(dstStoryMp3, dstStoryText);
        //initEvent();
    }


    private void initMediaPlay(String dstMp3Name, String dstLrcName){
        try {
            //handler.postDelayed(runnable, 100);
            //mediaPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd(dstMp3Name);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mSeekBar.setMax(mediaPlayer.getDuration());
                    mSeekBar.setProgress(0);
                    tv_currentTime.setText("00:00");
                    tv_totalTime.setText(formatTime(mediaPlayer.getDuration()));
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    lrcView.updateTime(0);
                    mSeekBar.setProgress(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        lrcView.loadLrc(getLrcText(dstLrcName));

        lrcView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                mediaPlayer.seekTo((int) time);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    //handler.post(runnable);
                }
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
            }
        }, 300);

        iv_record.setOnClickListener(this);
        iv_start.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar == mSeekBar) {
                    if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                        tv_currentTime.setText(formatTime(progress));
                        mLastProgress = progress;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                lrcView.updateTime(seekBar.getProgress());
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_record:
                AnimationUtils.playAnimation(iv_record, this);
                break;
            case R.id.iv_start://暂停继续播放音频
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    //handler.post(runnable);
                    iv_start.setImageResource(R.mipmap.start);
                } else {
                    mediaPlayer.pause();
                    //handler.removeCallbacks(runnable);
                    iv_start.setImageResource(R.mipmap.pause_story);
                }
                break;
        }
    }

    private String getLrcText(String fileName) {
        String lrcText = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lrcText = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcText;
    }

    private int currentImgIndex = 0;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                int time = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                lrcView.updateTime(time);
                mSeekBar.setProgress(time);
                tv_currentTime.setText(formatTime(time));

                int imageIndex = time * dstImage.length /duration;
                if(imageIndex != currentImgIndex){
                    Drawable drawable = getResources().getDrawable(dstImage[imageIndex]);
                    iv_picture.setImageDrawable(drawable);
                    currentImgIndex = imageIndex;
                }

            }

            handler.postDelayed(this, 300);
        }
    };

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                int time = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lrcView.updateTime(time);
                        mSeekBar.setProgress(time);
                        tv_currentTime.setText(formatTime(time));

                        int imageIndex = time * dstImage.length /duration;
                        if(imageIndex != currentImgIndex
                                && imageIndex < dstImage.length){
                            Drawable drawable = getResources().getDrawable(dstImage[imageIndex]);
                            iv_picture.setImageDrawable(drawable);
                            currentImgIndex = imageIndex;
                        }
                    }
                });


            }
        }
    };

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    @Override
    protected void onDestroy() {
        //handler.removeCallbacks(runnable);
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        if(task != null){
            task.cancel();
            task = null;
        }
        if(timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    @Override
    public void EndAnimation(View view) {
        switch (view.getId()){
            case R.id.iv_record:
                startActivity(new Intent(StoryLrcActivity.this, RecordActivity.class));
                break;

        }
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
}
