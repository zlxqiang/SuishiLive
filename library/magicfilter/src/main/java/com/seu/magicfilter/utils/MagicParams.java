package com.seu.magicfilter.utils;

import android.content.Context;
import android.os.Environment;


/**
 * Created by why8222 on 2016/2/26.
 */
public class MagicParams {
    public static Context context;

    public static String videoPath = Environment.getExternalStorageDirectory().getPath();
    public static String videoName = "MagicCamera_test.mp4";

    public static int beautyLevel = 5;

    public MagicParams() {

    }
}
