package cn.lenovo.voiceservice.story;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import cn.lenovo.voiceservice.R;
import cn.lenovo.voiceservice.utils.StatusBarUtils;

public class BearActivity extends Activity {

    private static final String TAG = "SC-BearActivity";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private final String[] bearMp3File = new String[]{"bearcategory.mp3", "beartype.mp3"};
    private int bearType;

    private TextView tv_question,tv_answer;
    private ImageView iv_picture,iv_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bear);

        Intent intent = getIntent();
        if(intent != null){
            bearType = intent.getIntExtra("bearType", 0);
            initMediaPlay(bearMp3File[bearType]);
        }

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtils.hideNavgationBar(this);
    }

    private void initView() {
        tv_question=findViewById(R.id.tv_question);
        tv_answer=findViewById(R.id.tv_answer);
        iv_picture=findViewById(R.id.iv_picture);
        iv_record=findViewById(R.id.iv_record);

        if (bearType == 0){
            tv_question.setText("世界上有多少种熊？");
            tv_answer.setText("世界上熊科动物一共有八种，如：美洲黑熊，棕熊，大熊猫等。数量最多的是美洲黑熊，超过90万头。");
            iv_picture.setImageResource(R.mipmap.bear_category);
        }else if (bearType == 1){
            tv_question.setText("熊大熊二是什么熊？");
            tv_answer.setText("熊大熊二是卡通动物，生物特征更接近亚洲黑熊。他们还会说人话，会直立行走，是光头强的死对头。");
            iv_picture.setImageResource(R.mipmap.bear_type);
        }
    }

    private void initMediaPlay(String dstMp3Name){
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd(dstMp3Name);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "MediaPlayer onPrepared ... ");
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "MediaPlayer onCompletion ... ");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
            }
        }, 300);

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
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
