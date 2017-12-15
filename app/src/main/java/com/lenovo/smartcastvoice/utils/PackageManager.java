package com.lenovo.smartcastvoice.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linsen on 17-11-24.
 */

public class PackageManager {

    private static final String TAG = "SC-PackageManager";
    private Context mContext;
    static Map<String,String> appinfo;
    private String apps[];
    private final boolean DEBUG = false;


    public PackageManager(Context context){
        mContext = context;
        appinfo = new HashMap<String, String>();
        getInstalledApps();
    }

    private void getInstalledApps() {
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
        List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>(packages.size());

        ArrayList<String> app=new ArrayList<String>();
        for (int j = 0; j < packages.size(); j++) {
            Map<String, Object> map = new HashMap<String, Object>();

            PackageInfo packageInfo = packages.get(j);
            //显示非系统软件
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0){
                map.put("img", packageInfo.applicationInfo.loadIcon(mContext.getPackageManager()).getCurrent());
                map.put("name", packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString());
                app.add(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString());

                appinfo.put(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString(), packageInfo.packageName);

                if(DEBUG) Log.i(TAG, packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString()+packageInfo.packageName);
                map.put("desc", packageInfo.packageName);
                listMap.add(map);
            }
        }
        apps = new String[app.size()];
        for (int i = 0, j = app.size(); i < j; i++) {
            apps[i] = app.get(i);
        }
    }

    public void openApp(String appName) {
        String packageName = appinfo.get(appName.trim());
        android.content.pm.PackageManager packageManager = mContext.getPackageManager();
        Intent intent = new Intent();
        intent =packageManager.getLaunchIntentForPackage(packageName);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }

}
