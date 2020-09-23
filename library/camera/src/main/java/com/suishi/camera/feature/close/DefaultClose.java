package com.suishi.camera.feature.close;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.suishi.camera.camera.CameraBuilder2;

public class DefaultClose extends Close<CameraBuilder2> {


    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void cameraUnBuilder() {
      if(mCameraBuilder!=null && mCameraBuilder.getOpen()!=null && mCameraBuilder.getOpen().getDevice()!=null){
          mCameraBuilder.getPreview().cameraUnBuilder();
          mCameraBuilder.getOpen().cameraUnBuilder();
          mCameraBuilder.getInit().cameraUnBuilder();
      }
    }

    @Override
    public void onRelease() {
        if(mCameraBuilder!=null){
            mCameraBuilder.getPreview().onRelease();
            mCameraBuilder.getOpen().onRelease();
            mCameraBuilder.getInit().onRelease();
        }
    }
}
