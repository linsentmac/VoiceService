package cn.lenovo.voiceservice.music.other;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import cn.lenovo.voiceservice.music.model.DownloadMusicInfo;
import cn.lenovo.voiceservice.music.model.Music;
import cn.lenovo.voiceservice.music.model.SheetInfo;
import cn.lenovo.voiceservice.music.preference.Preferences;
import cn.lenovo.voiceservice.music.utils.CoverLoader;
import cn.lenovo.voiceservice.music.utils.ScreenUtils;
import cn.lenovo.voiceservice.music.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * AppCache
 * Created by chao on 2018/4/1.
 */
public class AppCache {
    private Context mContext;
    private final List<Music> mLocalMusicList = new ArrayList<>();
    private final List<SheetInfo> mSheetList = new ArrayList<>();
    private final List<Activity> mActivityStack = new ArrayList<>();
    private final LongSparseArray<DownloadMusicInfo> mDownloadList = new LongSparseArray<>();

    private AppCache() {
    }

    private static class SingletonHolder {
        private static AppCache instance = new AppCache();
    }

    public static AppCache get() {
        return SingletonHolder.instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
        ToastUtils.init(mContext);
        Preferences.init(mContext);
        ScreenUtils.init(mContext);
        CoverLoader.get().init(mContext);
        application.registerActivityLifecycleCallbacks(new ActivityLifecycle());
    }

    public Context getContext() {
        return mContext;
    }

    public List<Music> getLocalMusicList() {
        return mLocalMusicList;
    }

    public List<SheetInfo> getSheetList() {
        return mSheetList;
    }

    public void clearStack() {
        List<Activity> activityStack = mActivityStack;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

    public LongSparseArray<DownloadMusicInfo> getDownloadList() {
        return mDownloadList;
    }

    private class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
        private static final String TAG = "Activity";

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(TAG, "onCreate: " + activity.getClass().getSimpleName());
            mActivityStack.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "onDestroy: " + activity.getClass().getSimpleName());
            mActivityStack.remove(activity);
        }
    }
}
