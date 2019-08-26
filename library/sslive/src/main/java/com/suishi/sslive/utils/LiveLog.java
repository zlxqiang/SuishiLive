package com.suishi.sslive.utils;

import android.util.Log;

public class LiveLog {
    /**
     *
     */
    private static boolean mOpen = false;

    public static void isOpen(boolean isOpen) {
        mOpen = isOpen;
    }

    public static void d(String tag, String msg) {
        if (mOpen) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mOpen) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (mOpen) {
            Log.e(tag, msg);
        }
    }



    public static void d(Object obj, String msg) {
        if (mOpen) {
            Log.d(obj.getClass().getSimpleName(), msg);
        }
    }

    public static void w(Object obj, String msg) {
        if (mOpen) {
            Log.w(obj.getClass().getSimpleName(), msg);
        }
    }
    public static void e(Object obj,String msg){
        if(mOpen){
            Log.e(obj.getClass().getSimpleName(),msg);
        }
    }
}
