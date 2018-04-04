package cn.lenovo.voiceservice.music.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import cn.lenovo.voiceservice.R;
import cn.lenovo.voiceservice.music.constants.RxBusTags;
import cn.lenovo.voiceservice.music.model.Music;
import cn.lenovo.voiceservice.music.other.AppCache;
import cn.lenovo.voiceservice.music.service.AudioPlayer;
import cn.lenovo.voiceservice.music.utils.MusicUtils;
import cn.lenovo.voiceservice.music.utils.PermissionReq;
import cn.lenovo.voiceservice.music.utils.ToastUtils;

import java.util.List;

public class RecognitionActivity extends Activity {
    public static final String TAG = "RecognitionActivity";
    private Context mContext = RecognitionActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        Button btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic("我们不一样");
            }
        });
    }

    private void playMusic(String musicName) {
        AppCache.get().init(this.getApplication());
        scanMusic(musicName);
    }

    @Subscribe(tags = {@Tag(RxBusTags.SCAN_MUSIC)})
    public void scanMusic(String musicName) {
        PermissionReq.with(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).result(new PermissionReq.Result() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onGranted() {
                new AsyncTask<Void, Void, List<Music>>() {
                    @Override
                    protected List<Music> doInBackground(Void... params) {
                        return MusicUtils.scanMusic(mContext);
                    }

                    @Override
                    protected void onPostExecute(List<Music> musicList) {
                        Log.e(TAG, "scan musicList: " + musicList.size());
                        AppCache.get().getLocalMusicList().clear();
                        AppCache.get().getLocalMusicList().addAll(musicList);

                        //扫描结束，开始匹配
                        for (int i = 0; i < musicList.size(); i++) {
                            if (musicName != null && musicName.equals(musicList.get(i).getTitle())) {
                                //找到了
                                AudioPlayer.get().setMusicList(musicList);
                                Intent intent = new Intent(mContext, MusicActivity.class);
                                intent.putExtra("music", musicList.get(i));
                                startActivity(intent);
                                break;
                            }
                            if (i == musicList.size() - 1) {
                                ToastUtils.show("本地没有此歌曲哦！");
                            }
                        }
                    }
                }.execute();
            }

            @Override
            public void onDenied() {
                ToastUtils.show("没有存储空间权限，无法扫描本地歌曲！");
            }
        }).request();
    }
}
