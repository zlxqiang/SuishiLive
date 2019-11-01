package com.suishi.sslive.mode.stream;

/**
 * Author : eric
 * CreateDate : 2017/11/1  15:32
 * Email : ericli_wang@163.com
 * Version : 2.0
 * Desc :
 * Modified :
 */

public class StreamManager {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("ffmpeg-handle");
    }

    private static StreamManager mInstance;


    public StreamManager() {

    }

    public synchronized static StreamManager getInstance() {
        if (mInstance == null) {
            mInstance = new StreamManager();
        }
        return mInstance;
    }


    /**
     * 前置后置摄像头
     */
    public enum CameraID {
        FRONT, BACK
    }

    /**
     * 初始化音频编码器
     */
    public static native int InitAudioEncoder();

    /**
     * 初始化视频编码器
     */
    public static native int InitVideoEncoder();

    /**
     * 初始化音频采集
     *
     * @param channles      音频通道
     * @param SampleRate    采样率
     * @param SampleBitRate 采样位数
     * @return
     */
    public static native int InitAudio(int channles, int SampleRate, int SampleBitRate);

    /**
     * 初始化视频采集
     *
     * @param inWidth   输入宽度
     * @param inHeight  输入高度
     * @param outWidth  输出宽度
     * @param outHeight 输出高度
     * @param fps       帧率
     * @param mirror    是否镜像
     * @return
     */
    public static native int InitVideo(int inWidth, int inHeight, int outWidth, int outHeight, int fps, boolean mirror);

    /**
     * 开启推流
     *
     * @param url 推流地址
     * @return
     */
    public static native int StartPush(String url);

    /**
     * 关闭推流
     *
     * @return
     */
    public static native int Close();

    /**
     * 底层资源回收与释放
     *
     * @return
     */
    public static native int Release();

    /**
     * 视频滤镜
     *
     * @param filterType  滤镜类型
     * @param filterValue 滤镜取值
     * @return
     */
    //  public static native int Filter(FilterType filterType, int filterValue);

    /**
     * 上传音频数据编码AAC
     *
     * @param audioBuffer 数据缓冲区
     * @param length      数据长度
     * @return
     */
    public static native int Encode2AAC(byte[] audioBuffer, int length);

    /**
     * 上传视频数据编码H264
     *
     * @param videoBuffer 数据缓冲区
     * @param length      数据长度
     * @return
     */
    public static native int Encode2H264(byte[] videoBuffer, int length);


    /**
     * 设置摄像头
     *
     * @param cameraID 相机ID
     */
    public static native void SetCameraID(int cameraID);


    /**
     * 视频添加文字
     *
     * @param text
     * @param x
     * @param y
     * @return
     */
    //  public static native int DrawText(String fontFilePath, String text, int x, int y);


    /**
     * water mark
     *
     * @param enable
     * @param waterMark
     * @param waterWidth
     * @param waterHeight
     * @param positionX
     * @param positionY
     * @return
     */
//    public static native int SetWaterMark(boolean enable,
//                                          byte[] waterMark,
//                                          int waterWidth,
//                                          int waterHeight,
//                                          int positionX,
//                                          int positionY);

    /**
     * 编码数据推流
     */
    public static native void avInput();

    public interface PushCallback {
        /**
         * @param pts
         * @param dts
         * @param duration
         * @param index
         */
        void onStreamCallback(long pts, long dts, long duration, long index);

        /**
         * @param error
         */
        void onStreamInitCallBack(String error);
    }

}
