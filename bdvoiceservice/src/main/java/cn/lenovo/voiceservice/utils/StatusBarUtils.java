package cn.lenovo.voiceservice.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by tmac on 17-11-23.
 */

public class StatusBarUtils {


    public static void hideNavgationBar(Context context){
        // Hide navigationBar and statusBar
        int uiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

}
