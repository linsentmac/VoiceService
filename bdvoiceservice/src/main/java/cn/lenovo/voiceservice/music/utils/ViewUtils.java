package cn.lenovo.voiceservice.music.utils;

import android.view.View;

import cn.lenovo.voiceservice.music.enums.LoadStateEnum;

/**
 * 视图工具类
 * Created by chao on 2018/4/1.
 */
public class ViewUtils {
    public static void changeViewState(View success, View loading, View fail, LoadStateEnum state) {
        switch (state) {
            case LOADING:
                success.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                fail.setVisibility(View.GONE);
                break;
            case LOAD_SUCCESS:
                success.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                fail.setVisibility(View.GONE);
                break;
            case LOAD_FAIL:
                success.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                fail.setVisibility(View.VISIBLE);
                break;
        }
    }
}
