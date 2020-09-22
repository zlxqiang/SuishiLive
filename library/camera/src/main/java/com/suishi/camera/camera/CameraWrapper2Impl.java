package com.suishi.camera.camera;

import android.hardware.camera2.CameraDevice;
import android.os.Build;

import androidx.annotation.NonNull;

import com.suishi.camera.feature.close.DefaultClose;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.camera.feature.open.OpenStateCallback;
import com.suishi.camera.feature.privew.DefaultPreview;
import com.suishi.camera.feature.privew.DefaultRecord;
import com.suishi.utils.LogUtils;

/**
 * Camera2包装类
 */
public class CameraWrapper2Impl extends ICameraWrapper<DefaultInit, DefaultOpen, DefaultRecord, DefaultClose,CameraBuilder2> implements OpenStateCallback {

    public CameraWrapper2Impl(CameraBuilder2 mBuilder) {
        this.mBuilder = mBuilder;
    }


    /**
     *
     */
    @Override
    void init() {
        mInit = mBuilder.getInit();
        if (mInit != null) {
            mInit.cameraBuilder(mBuilder);
        }

        mOpen=mBuilder.getOpen();
        if(mOpen!=null){
            mOpen.cameraBuilder(mBuilder);
        }

    }


    @Override
    public void open() {
        mOpen.openCamera(mInit.getCameraManager(), mInit.getCamerList().get(0).getCameraId(), this);
    }

    @Override
    public void close() {

    }

    @Override
    public void startPreview() {
        if(mOpen.getDevice()!=null) {
            mPreview.startPreview();
        }
    }

    @Override
    public void stopPreview() {
        if(mOpen.getDevice()!=null) {
            mPreview.stopPreview();
        }
    }

    @Override
    public void startRecord() {
        if(mPreview!=null) {
            mPreview.startReCord();
        }
    }

    @Override
    public void stopRecord() {
        if(mPreview!=null) {
            mPreview.stopRecord();
        }
    }

    @Override
    public void switchCamera() {

    }

    @Override
    public void release() {

    }


    @Override
    public CameraBuilder2 getBuilder() {
        return mBuilder;
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        LogUtils.e("camera","opened camera success");
        mPreview=mBuilder.getPreview();
        if(mPreview!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mPreview.cameraBuilder(mBuilder);
            }else{
                throw new IllegalStateException("系统版本低");
            }
        }

        mClose=mBuilder.getClose();
        if(mClose!=null){
            mClose.cameraBuilder(mBuilder);
        }

        startPreview();

    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        LogUtils.e("camera","camera onDisconnected");
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        LogUtils.e("camera","camera onError");

    }
}
