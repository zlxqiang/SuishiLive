package com.suishi.camera.feature;

import com.suishi.camera.camera.ICameraBuilder;

public class Assembly<T extends ICameraBuilder> {

    protected T mCameraBuilder;

    public void cameraBuilder(T builder){
        mCameraBuilder=builder;
    }

}
