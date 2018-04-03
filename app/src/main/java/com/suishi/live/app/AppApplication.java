package com.suishi.live.app;

import android.app.Application;
import android.content.Context;

import com.seu.magicfilter.utils.MagicParams;
import com.suishi.live.app.utils.CrashHandler;

/**
 * Created by admin on 2017/7/1.
 */

public class AppApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        CrashHandler.getInstance().init(this);//初始化全局异常管理
        MagicParams.context=this;
    }

}
