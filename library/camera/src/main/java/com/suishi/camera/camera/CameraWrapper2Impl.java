package com.suishi.camera.camera;

import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.suishi.camera.feature.close.DefaultClose;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.camera.feature.open.OpenStateCallback;
import com.suishi.camera.feature.privew.DefaultPreview;
import com.suishi.camera.feature.privew.DefaultRecord;
import com.suishi.utils.LogUtils;

/**
 * 具体实现类
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

        mClose=mBuilder.getClose();
        if(mClose!=null){
            mClose.cameraBuilder(mBuilder);
        }

    }


    @Override
    public void open() {
        mOpen.openCamera(mInit.getCameraManager(), mInit.getCurrentCamera().getCameraId(), this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void close() {
        if(mClose!=null){
            mClose.cameraUnBuilder();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void startRecord() {
        if(mPreview!=null) {
            mPreview.startReCord();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void stopRecord() {
        if(mPreview!=null) {
            mPreview.stopRecord();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void switchCamera() {
       close();
        if(mInit!=null){
            mInit.switchCamera();
            open();
        }
    }

    @Override
    public void release() {
        if(mClose!=null){
            mClose.onRelease();
        }
    }


    @Override
    public CameraBuilder2 getBuilder() {
        return mBuilder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
