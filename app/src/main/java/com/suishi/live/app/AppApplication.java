package com.suishi.live.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.seu.magicfilter.utils.MagicParams;
import com.suishi.utils.CrashHandler;

/**
 * Created by admin on 2017/7/1.
 */
public class AppApplication extends MultiDexApplication{

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        CrashHandler.getInstance().init(this);
        MagicParams.context=this;
        //存储空间检测，是否充足

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);



    }

}
