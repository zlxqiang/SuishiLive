package com.suishi.utils;


import android.util.Log;

import com.resource.app.BuildConfig;


/**
 * @Author: zzq
 * @Description:
 * @CreateDate: 2019/5/5 18:48
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/5/5 18:48
 * @Version: 1.0
 */
public class LogUtils {

    private static final String PREFIX = "Mylog- "; //

    private static boolean isDebug = BuildConfig.DEBUG;

    public static void setDebug(boolean is) {
        isDebug = is;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void i(String tag, String message) {

        if (isDebug()) {
            Log.i(PREFIX.concat(tag), message);
        }
    }

    public static void v(String tag, String message) {

        if (isDebug()) {
            Log.v(PREFIX.concat(tag), message);
        }

    }

    public static void safeCheckCrash(String tag, String msg, Throwable tr) {
        if (isDebug()) {
            throw new RuntimeException(PREFIX.concat(tag) + " " + msg, tr);
        } else {
            Log.e(PREFIX.concat(tag), msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    public static void e(String tag, String message) {

        if (isDebug()) {
            Log.e(PREFIX.concat(tag), message);
        }
    }

}