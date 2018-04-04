package cn.lenovo.voiceservice.music.model;

import com.google.gson.annotations.SerializedName;

/**
 * JavaBean
 * Created by chao on 2018/4/1.
 */
public class Lrc {
    @SerializedName("lrcContent")
    private String lrcContent;

    public String getLrcContent() {
        return lrcContent;
    }

    public void setLrcContent(String lrcContent) {
        this.lrcContent = lrcContent;
    }
}
