package com.suishi.camera.camera.media;

/**
 * Created by Administrator on 2017/6/29 0029.
 * 视频的信息bean
 */

public class VideoInfo {
    /**
     * 路径
     */
    public String path;
    /**
     * 旋转角度
     */
    public int rotation;
    /**
     * 宽
     */
    public int width;
    /**
     * 高
     */
    public int height;
    /**
     * 比特率
     */
    public int bitRate;//
    /**
     * 帧率
     */
    public int frameRate;
    /**
     * 关键帧间隔
     */
    public int frameInterval;//
    /**
     * 时长
     */
    public int duration;//
    /**
     *
     */
    public int expWidth;//期望宽度
    /**
     * 期望高度
     */
    public int expHeight;//
    /**
     * 剪切的开始点
     */
    public int cutPoint;//
    /**
     * 剪切的时长
     */
    public int cutDuration;//
}
