package com.suishi.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

public class DensityUtils {

    private static final String TAG = "DensityUtils";

    private static Context mContent;

    private DensityUtils() {
    }

    public static void init(Context context) {
        mContent = context;
    }

    /**
     * dp转px
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContent.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, mContent.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     */
    public static float px2dp(float pxVal) {
        final float scale = mContent.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     */
    public static float px2sp(float pxVal) {
        return (pxVal / mContent.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth() {
        int width = 0;
        WindowManager wm = (WindowManager) mContent.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        return width;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenHeight() {
        int width = 0;
        WindowManager wm = (WindowManager) mContent.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.heightPixels;
        return width;
    }


    public static void getDeviceInfos() {
        // 获取屏幕密度（方法1）
        WindowManager wm = (WindowManager) mContent.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();        // 屏幕宽（像素，如：480px）
        int screenHeight = wm.getDefaultDisplay().getHeight();        // 屏幕高（像素，如：800p）

        Log.e(TAG, "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);


        // 获取屏幕密度（方法2）
        DisplayMetrics dm = new DisplayMetrics();
        dm = mContent.getResources().getDisplayMetrics();

        float density = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        int densityDPI = dm.densityDpi;        // 屏幕密度（每寸像素：120/160/240/320）
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;

        Log.e(TAG, "xdpi=" + xdpi + "; ydpi=" + ydpi);
        Log.e(TAG, "density=" + density + "; densityDPI=" + densityDPI);

        screenWidth = dm.widthPixels;        // 屏幕宽（像素，如：480px）
        screenHeight = dm.heightPixels;        // 屏幕高（像素，如：800px）

        Log.e(TAG, "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);


        // 获取屏幕密度（方法3）
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        density = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        densityDPI = dm.densityDpi;        // 屏幕密度（每寸像素：120/160/240/320）
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;

        Log.e(TAG, "xdpi=" + xdpi + "; ydpi=" + ydpi);
        Log.e(TAG, "density=" + density + "; densityDPI=" + densityDPI);

        int screenWidthDip = dm.widthPixels;        // 屏幕宽（dip，如：320dip）
        int screenHeightDip = dm.heightPixels;        // 屏幕宽（dip，如：533dip）

        Log.e(TAG, "screenWidthDip=" + screenWidthDip + "; screenHeightDip=" + screenHeightDip);

        screenWidth = (int) (dm.widthPixels * density + 0.5f);        // 屏幕宽（px，如：480px）
        screenHeight = (int) (dm.heightPixels * density + 0.5f);        // 屏幕高（px，如：800px）

        Log.e(TAG, "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);
    }

    private static final float WIDTH = 392;//参考设备的宽，单位是dp
    private static float appDensity;//表示屏幕密度
    private static float appScaleDensity;//字体缩放比列，默认AppDensity

    public static void setDensity(final Application application, Activity activity) {
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (appDensity == 0) {
            //初始化赋值操作
            appDensity = displayMetrics.density;
            appScaleDensity = displayMetrics.scaledDensity;

            //添加字体变化监听回调
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体发生更改，重新对ScaleDensity进行赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        appScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        //计算目标值density,scaleDensity,densityDpi
        float targetDensity = displayMetrics.widthPixels / WIDTH;//1080/360=3.0
        float targetScaleDensity = targetDensity * (appScaleDensity / appDensity);
        int targetDensityDpi = (int) (targetDensity * 160);

        //替换Activity的density，scaleDensity,densityDpi
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        dm.density = targetDensity;
        dm.scaledDensity = targetScaleDensity;
        dm.densityDpi = targetDensityDpi;
    }

}
