package cn.lenovo.voiceservice.story;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.lenovo.voiceservice.R;
import cn.lenovo.voiceservice.RecordActivity;
import cn.lenovo.voiceservice.music.utils.SystemUtils;
import cn.lenovo.voiceservice.utils.AnimationUtils;
import cn.lenovo.voiceservice.utils.StatusBarUtils;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by yunduo on 18-3-29.
 */

public class StoryActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener, SeekBar.OnSeekBarChangeListener{
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE_CODE = 1001;

    private ImageView iv_picture,iv_record,iv_start;
    private TextView tv_speaker,tv_storyName,tv_currentTime,tv_totalTime;
    private MyView tv_storyContent;//自定义拼音TextView
    private SeekBar mSeekBar;
    private String fileName;
    private boolean isDraggingProgress;
    private int mLastProgress;
    private boolean isPause = false;//是否暂停
    private Timer timer;

    private int type;
    private String storyName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        if(intent != null){
            type = intent.getIntExtra("storyType", 0);
            storyName = intent.getStringExtra("storyName");
        }


        initDatas(type, storyName);
    }



    private String[] storyNames = new String[]{"白雪公主", "灰姑娘", "卖火柴的小女孩", "睡美人", "小红帽"};
    //private int[] storyMp3 = new int[]{R.raw.baixue, R.raw.hgn, R.raw.mhcdxnh, R.raw.smr, R.raw.xhm};
    //private int[] storyText = new int[]{R.raw.baixuegongzhu, R.raw.huiguniang, R.raw.maihuochaidexiaonvhai, R.raw.shuimeiren, R.raw.xiaohongmao};
    private int[] storyMp3 = new int[]{};
    private int[] storyText = new int[]{};
    private int[] bxImage = new int[]{R.mipmap.baixuegongzhu_1, R.mipmap.baixuegongzhu_2, R.mipmap.baixuegongzhu_3};
    private int[] hgnImage = new int[]{R.mipmap.huiguniang_1, R.mipmap.huiguniang_2, R.mipmap.huiguniang_3, R.mipmap.huiguniang_4};
    private int[] mhcImage = new int[]{R.mipmap.maihuochaidexiaonvhai_1, R.mipmap.maihuochaidexiaonvhai_2, R.mipmap.maihuochaidexiaonvhai_3, R.mipmap.maihuochaidexiaonvhai_4, R.mipmap.maihuochaidexiaonvhai_5, R.mipmap.maihuochaidexiaonvhai_6};
    private int[] smrImage = new int[]{R.mipmap.shuimeiren_1, R.mipmap.shuimeiren_2, R.mipmap.shuimeiren_3, R.mipmap.shuimeiren_4, R.mipmap.shuimeiren_5, R.mipmap.shuimeiren_6};
    private int[] xhmImage = new int[]{R.mipmap.xiaohongmao_1, R.mipmap.xiaohongmao_2, R.mipmap.xiaohongmao_3};
    private List<int[]> imageList;


    private int dstStoryMp3;
    private int dstStoryText;
    private int[] dstImage;

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
        } else if(type == 1){
            for(int i = 0; i < storyNames.length; i ++){
                if(storyNames[i].equals(storyName)){
                    dstStoryMp3 = storyMp3[i];
                    dstStoryText = storyText[i];
                    dstImage = imageList.get(i);
                    break;
                }else if(i == storyNames.length - 1){
                    // 没找到故事, 跳到HintActivity

                    return;
                }
            }
        }
        // 执行播放音频, 显示文字
        initView();
        initFile();
        initEvent();
    }

    private void initFile() {
        try{
            Pair<List<String>, List<String>> data = parseUtils.readDataFormFile(dstStoryText, getResources());
            String[] h = new String[data.first.size()];
            data.first.toArray(h);
            tv_storyContent.setHanzi(h);
            if (data.second.size() != data.first.size()) {
                int sub = data.first.size() - data.second.size();
                for (int i =0; i< sub; i ++) {
                    data.second.add("");
                }
            }
            String[] p = new String[data.second.size()];
            data.second.toArray(p);
            tv_storyContent.setPinyin(p);
            tv_storyContent.setLineHeight(30);
            tv_storyContent.setTextSize(8);
            tv_storyContent.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SC-Story", "error = " + e.getMessage());
            Toast.makeText(StoryActivity.this, "本地无该文件", LENGTH_SHORT).show();
        }
    }

    private void initView() {
        iv_picture=findViewById(R.id.iv_picture);
        iv_record=findViewById(R.id.iv_record);
        iv_start=findViewById(R.id.iv_start);
        tv_speaker=findViewById(R.id.tv_speaker);
        tv_storyName=findViewById(R.id.tv_storyName);
        tv_storyContent=findViewById(R.id.tv_storyContent);
        tv_currentTime=findViewById(R.id.tv_currentTime);
        tv_totalTime=findViewById(R.id.tv_totalTime);
        mSeekBar =findViewById(R.id.seekBar);

    }

    private void initEvent() {
        iv_record.setOnClickListener(this);
        iv_start.setOnClickListener(this);//60 * 1000
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setSecondaryProgress(0);
        PlayVoice.playVoice(StoryActivity.this, dstStoryMp3);
        tv_storyContent.startRenderColor( PlayVoice.getMediaPlayer().getDuration() );
        tv_currentTime.setText("00:00");
        tv_totalTime.setText(formatTime(PlayVoice.getMediaPlayer().getDuration()));
        mSeekBar.setProgress((int) PlayVoice.getMediaPlayer().getCurrentPosition());
        mSeekBar.setMax((int) PlayVoice.getMediaPlayer().getDuration());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_record:
                AnimationUtils.playAnimation(iv_record, this);
                break;
            case R.id.iv_start://暂停继续播放音频
//                mSeekBar.setProgress((int) PlayVoice.getMediaPlayer().getCurrentPosition());
//                mSeekBar.setMax((int) PlayVoice.getMediaPlayer().getDuration());
                if (PlayVoice.getMediaPlayer().isPlaying() && !isPause) {
                    PlayVoice.getMediaPlayer().pause();//暂停播放
                    tv_storyContent.pauseRenderColor();
                    isPause = true;
                    iv_start.setImageResource(R.mipmap.pause);
                } else {
                    PlayVoice.getMediaPlayer().start();//继续播放
                    tv_storyContent.resumeRenderColor();
                    isPause = false;
                    iv_start.setImageResource(R.mipmap.start);
                    break;
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtils.hideNavgationBar(this);
        timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayVoice.stopVoice();
        if(task != null){
            task.cancel();
            task = null;
        }
        if(timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(PlayVoice.getMediaPlayer().isPlaying()){
            PlayVoice.getMediaPlayer().stop();//停止音频的播放
        }
        PlayVoice.getMediaPlayer().release();//释放资源
    }

    @Override
    public void EndAnimation(View view) {
        switch (view.getId()){
            case R.id.iv_record:
                startActivity(new Intent(StoryActivity.this, RecordActivity.class));
                break;
            case R.id.iv_start:
                break;
        }
    }




    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    /*
    * seekbar改变时的事件监听处理
    * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mSeekBar) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tv_currentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    /*
    * 按住seekbar时的事件监听处理
    * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == mSeekBar) {
            isDraggingProgress = true;
        }
    }
    /*
    * 放开seekbar时的时间监听处理
    * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == mSeekBar) {
            isDraggingProgress = false;
            if (PlayVoice.getMediaPlayer().isPlaying()||PlayVoice.getMediaPlayer().isLooping()){
                int progress = seekBar.getProgress();
                PlayVoice.getMediaPlayer().seekTo(progress);
            }else {
                seekBar.setProgress(0);
            }
        }
    }



    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            //更新进度
            final int position = PlayVoice.getMediaPlayer().getCurrentPosition();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_currentTime.setText(formatTime(position));
                    mSeekBar.setProgress(position);
                }
            });

        }
    };

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
