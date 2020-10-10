package com.suishi.camera.camera;

import com.suishi.camera.feature.close.Close;
import com.suishi.camera.feature.init.Init;
import com.suishi.camera.feature.open.Open;
import com.suishi.camera.feature.privew.Preview;

import java.time.Instant;

/**
 *
 * desc 对外提供统一的访问
 * 包括预览和录制尺寸、闪光灯、曝光、聚焦、摄像头切换等
 */

public class CameraController<M extends ICameraBuilder> extends ICameraWrapper<Init, Open, Preview, Close,M> {


    private ICameraWrapper mCameraWrapper;

    public CameraController(M cameraBuilder) {
        this.mBuilder=cameraBuilder;
        this.mCameraWrapper=mBuilder.build();
        init();
    }


    @Override
    void init() {
        mCameraWrapper.init();
    }

    @Override
    public void open() {
        mCameraWrapper.open();
    }

    @Override
    public void close() {
        mCameraWrapper.close();
    }

    @Override
    public void startPreview() {
        mCameraWrapper.startPreview();
    }

    @Override
    public void stopPreview() {
        mCameraWrapper.stopPreview();
    }

    @Override
    public void startRecord() {
        mCameraWrapper.startRecord();
    }

    @Override
    public void stopRecord() {
        mCameraWrapper.stopRecord();
    }

    @Override
    public void switchCamera() {
     mCameraWrapper.switchCamera();
    }

    @Override
    public void release() {
        mCameraWrapper.release();
    }


}
