package com.suishi.live.app;


import androidx.multidex.MultiDexApplication;
import com.seu.magicfilter.utils.MagicParams;
import com.suishi.utils.CrashHandler;
import com.suishi.utils.DensityUtils;
import com.suishi.utils.ToastUtil;

/**
 * Created by admin on 2017/7/1.
 */
public class AppApplication extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        MagicParams.context=this;
        DensityUtils.init(this);
        ToastUtil.init(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

    }

}
