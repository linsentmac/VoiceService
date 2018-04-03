package cn.lenovo.voiceservice.jsonbean;

import java.util.List;

/**
 * Created by linsen on 18-4-2.
 */

public class MusicBean {

    /**
     * domain : 音乐
     * confidence : 0.9
     * reply :
     * userid : 12345
     * intent : [{"歌手名":"陈奕迅","歌曲名":"爱情转移"},{"歌曲名":"爱情转移"}]
     */

    private String domain;
    private double confidence;
    private String reply;
    private String userid;
    private List<IntentBean> intent;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<IntentBean> getIntent() {
        return intent;
    }

    public void setIntent(List<IntentBean> intent) {
        this.intent = intent;
    }

    public static class IntentBean {
        /**
         * 歌手名 : 陈奕迅
         * 歌曲名 : 爱情转移
         */

        private String 歌手名;
        private String 歌曲名;

        public String get歌手名() {
            return 歌手名;
        }

        public void set歌手名(String 歌手名) {
            this.歌手名 = 歌手名;
        }

        public String get歌曲名() {
            return 歌曲名;
        }

        public void set歌曲名(String 歌曲名) {
            this.歌曲名 = 歌曲名;
        }
    }
}
