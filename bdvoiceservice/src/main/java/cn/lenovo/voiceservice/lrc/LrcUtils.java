package cn.lenovo.voiceservice.lrc;

import android.text.format.DateUtils;

import java.util.Locale;

/**
 * Created by tmac on 2018/04/06.
 */
public class LrcUtils {

    public static String formatTime(long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return mm + ":" + ss;
    }
}
