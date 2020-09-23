package com.suishi.camera.feature.open;

import com.suishi.camera.camera.ICameraBuilder;
import com.suishi.camera.feature.Assembly;

public abstract class Open<B extends ICameraBuilder> extends Assembly<B> {

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
