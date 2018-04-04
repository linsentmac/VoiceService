package cn.lenovo.voiceservice.jsonbean;

import java.util.List;

/**
 * Created by tmac on 18-4-4.
 */

public class StoryBean {

    private String domain;
    private double confidence;
    private String reply;
    private String userid;
    private List<StoryBean.IntentBean> intent;

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

    public List<StoryBean.IntentBean> getIntent() {
        return intent;
    }

    public void setIntent(List<StoryBean.IntentBean> intent) {
        this.intent = intent;
    }

    public static class IntentBean {

        private String 风格;

        public String get风格() {
            return 风格;
        }

        public void set风格(String 风格) {
            this.风格 = 风格;
        }

    }

}
