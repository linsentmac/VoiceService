package cn.lenovo.voiceservice.music.service;

import cn.lenovo.voiceservice.music.model.Music;

/**
 * 播放进度监听器
 * Created by chao on 2018/4/1.
 */
public interface OnPlayerEventListener {

    /**
     * 切换歌曲
     */
    void onChange(Music music);

    /**
     * 继续播放
     */
    void onPlayerStart();

    /**
     * 暂停播放
     */
    void onPlayerPause();

    /**
     * 更新进度
     */
    void onPublish(int progress);

    /**
     * 缓冲百分比
     */
    void onBufferingUpdate(int percent);
}
