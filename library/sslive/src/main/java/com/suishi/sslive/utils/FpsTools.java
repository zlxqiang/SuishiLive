package com.suishi.sslive.utils;

import android.os.SystemClock;

/**
 * @Author: zzq
 * @Description:
 * @CreateDate: 2019/11/1 18:29
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/1 18:29
 * @Version: 1.0
 */
public class FpsTools {

    static int frameCount = 0;
    static int fps = 0;
    static long lastTime = 0;

    public static int fps() {
        ++frameCount;
        long curTime = SystemClock.currentThreadTimeMillis();
        if (curTime - lastTime > 1000) // 取固定时间间隔为1秒
        {
            fps = frameCount;
            frameCount = 0;
            lastTime = curTime;
        }
        return fps;
    }

}
