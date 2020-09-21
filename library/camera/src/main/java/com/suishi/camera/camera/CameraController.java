package com.suishi.camera.camera;

/**
 * Created by cj on 2017/8/2.
 * desc 相机的管理类 主要是Camera的一些设置
 * 包括预览和录制尺寸、闪光灯、曝光、聚焦、摄像头切换等
 */

public class CameraController<M extends ICameraBuilder> extends ICameraWrapper<M> {

    private M mCameraBuilder;

    private ICameraWrapper mCameraWrapper;

    public CameraController(M cameraBuilder) {
        this.mCameraBuilder=cameraBuilder;
        this.mCameraWrapper=mCameraBuilder.build();
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



}
