package com.suishi.live.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import com.seu.magicfilter.utils.MagicParams;
import com.suishi.live.app.utils.CrashHandler;

/**
 * Created by admin on 2017/7/1.
 */

public class AppApplication extends Application implements Application.ActivityLifecycleCallbacks{

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        CrashHandler.getInstance().init(this);//初始化全局异常管理
        MagicParams.context=this;
        registerActivityLifecycleCallbacks(this);
        registerComponentCallbacks(this);

        //存储空间检测，是否充足

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

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

    }
}
