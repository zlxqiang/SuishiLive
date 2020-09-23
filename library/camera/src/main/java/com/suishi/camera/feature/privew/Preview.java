package com.suishi.camera.feature.privew;

import com.suishi.camera.camera.ICameraBuilder;
import com.suishi.camera.feature.Assembly;

/**
 * 预览功能
 */
public abstract class Preview<B extends ICameraBuilder> extends Assembly<B> {


    public Preview() {
        super();
    }

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
