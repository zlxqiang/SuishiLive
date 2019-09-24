package com.suishi.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by weight68kg on 2018/5/3.
 */

public class ToastUtil {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void shortToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public static void toast(int msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
