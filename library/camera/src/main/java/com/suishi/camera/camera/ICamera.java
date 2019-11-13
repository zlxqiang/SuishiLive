package com.suishi.camera.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

/**
 * Created by cj on 2017/8/2.
 * desc 相机的接口控制类
 */

public interface ICamera {
    /**
     * 打开
     */
    void open(int cameraId);

    /**
     * @param texture
     */
    void setPreviewTexture(SurfaceTexture texture);

    /**
     * set the camera config
     */
    void setConfig(Config config);

    /**
     *
     * @param callback
     */
    void setOnPreviewFrameCallback(PreviewFrameCallback callback);

    /**
     *
     */
    void preview();

    /**
     *
     * @return
     */
    Point getPreviewSize();

    /**
     *
     * @return
     */
    Point getPictureSize();

    /**
     * close the camera
     */
    boolean close();

    /**
     *
     */
    class Config {
        public float rate = 1.778f; //宽高比
        public int minPreviewWidth;
        public int minPictureWidth;
    }

    /**
     *
     */
    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }
}
