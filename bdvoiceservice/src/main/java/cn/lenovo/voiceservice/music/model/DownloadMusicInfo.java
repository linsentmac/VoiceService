package cn.lenovo.voiceservice.music.model;

/**
 * DownloadMusicInfo
 * Created by chao on 2018/4/1.
 */
public class DownloadMusicInfo {
    private String title;
    private String musicPath;
    private String coverPath;

    public DownloadMusicInfo(String title, String musicPath, String coverPath) {
        this.title = title;
        this.musicPath = musicPath;
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public String getCoverPath() {
        return coverPath;
    }
}
