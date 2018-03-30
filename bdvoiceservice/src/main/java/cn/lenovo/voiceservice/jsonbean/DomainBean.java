package cn.lenovo.voiceservice.jsonbean;

import java.util.List;

/**
 * Created by tmac on 17-11-24.
 */

public class DomainBean {


    /**
     * domain : 日历
     * confidence : 0.75973207
     * reply : 今天是星期五。
     * userid : 55
     * intent : [{"查询对象":"星期","查询星期":"星期五"},{"查询对象":"星期","查询星期":"星期五"}]
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
         * 查询对象 : 星期
         * 查询星期 : 星期五
         */

        private String 查询对象;
        private String 查询星期;

        public String get查询对象() {
            return 查询对象;
        }

        public void set查询对象(String 查询对象) {
            this.查询对象 = 查询对象;
        }

        public String get查询星期() {
            return 查询星期;
        }

        public void set查询星期(String 查询星期) {
            this.查询星期 = 查询星期;
        }
    }
}
