package com.suishi.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongtaizhao on 2017/8/22.
 */

public class SoftKeyBoardUtils {
    /**
     * 隐藏键盘 当前页面有焦点 则隐藏键盘
     */
    public static void hideSoftInput(AppCompatActivity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = context.getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    /**
     * 隐藏键盘 忽视当前页面是否获取焦点
     */
    public static void hideSoftInputWithoutF(AppCompatActivity context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void hideSoftInputWithoutAllF(AppCompatActivity context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     **/
    public static void showSoftKeyBorad(AppCompatActivity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = context.getCurrentFocus();
            if (focusView != null) {
                imm.showSoftInput(focusView, 0);
            }
        }
    }

    /**
     * 前提 该Activity的AndroidManifest.xml配置为
     * android:windowSoftInputMode="stateVisible"
     * EditText获取焦点并显示键盘
     *
     * @param editText
     * @param context
     */
    public static void focusAndShowSoftKeyBoard(EditText editText, Context context) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager im = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
        im.showSoftInput(editText, 0);
    }

    /**
     * 可以看到，当SD卡存在或者SD卡不可被移除的时候，
     * 就调用getExternalCacheDir()方法来获取缓存路径，
     * 否则就调用getCacheDir()方法来获取缓存路径。
     * 前者获取到的就是 /sdcard/Android/data/<application package>/cache 这个路径，
     * 而后者获取到的是 /data/data/<application package>/cache 这个路径。
     *
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            cachePath = context.getExternalCacheDir().getPath();
//        } else {
//            cachePath = context.getCacheDir().getPath();
//        }
        if (context.getExternalCacheDir() == null) {
            cachePath = context.getCacheDir().getPath();
        } else {
            cachePath = context.getExternalCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 判断是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}
