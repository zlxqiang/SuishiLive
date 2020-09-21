package com.suishi.camera.camera;

import android.hardware.camera2.CameraDevice;

import androidx.annotation.NonNull;

import com.suishi.camera.feature.close.DefaultClose;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.camera.feature.privew.DefaultPreview;
import com.suishi.camera.feature.record.DefaultRecord;

/**
 * Camera2包装类
 */
public class CameraWrapper2Impl extends ICameraWrapper<DefaultInit, DefaultOpen, DefaultPreview, DefaultClose, DefaultRecord,CameraBuilder2> {

    public CameraWrapper2Impl(CameraBuilder2 mBuilder) {
        this.mBuilder = mBuilder;
    }

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
        mOpen.openCamera(mInit.getCameraManager(), mInit.getCamerList().get(0).getCameraId(), new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mPreview=mBuilder.getPreview();
                if(mPreview!=null){
                    mPreview.cameraBuilder(mBuilder);
                }

                mRecord=mBuilder.getRecord();
                if(mRecord!=null){
                    mRecord.cameraBuilder(mBuilder);
                }

                mClose=mBuilder.getClose();
                if(mClose!=null){
                    mClose.cameraBuilder(mBuilder);
                }

                startPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {

            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {

            }
        });
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
    public void switchCamera() {

    }


    @Override
    public CameraBuilder2 getBuilder() {
        return mBuilder;
    }

}
