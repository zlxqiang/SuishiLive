package com.suishi.sslive.utils;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * @Author: zzq
 * @Description:
 * @CreateDate: 2019/11/1 16:22
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/1 16:22
 * @Version: 1.0
 */
public class HardWareSupport {

    /**
     * 检测是否支持H265硬编码
     *
     * @return 检测结果
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isH265EncoderSupport() {
        boolean result = false;
        int count = MediaCodecList.getCodecCount();
        for (int i = 0; i < count; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            String name = info.getName();
            boolean b = info.isEncoder();
            if (b && name.contains("hevc")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检测是否支持H265硬解码
     *
     * @return 检测结果
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isH265DecoderSupport() {
        int count = MediaCodecList.getCodecCount();
        for (int i = 0; i < count; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            String name = info.getName();
            if (name.contains("decoder") && name.contains("hevc")) {
                return true;
            }
        }
        return false;
    }

}
