package com.suishi.camera.feature.takepicture;

import com.suishi.camera.camera.ICameraBuilder;
import com.suishi.camera.feature.Assembly;

/**
 * 拍照
 */
public abstract class TakePicture<B extends ICameraBuilder> extends Assembly<B> {

    @Override
    public void cameraBuilder(B builder) {
        super.cameraBuilder(builder);
    }

    @Override
    public void cameraUnBuilder() {

    }

    @Override
    public void onRelease() {

    }

}
