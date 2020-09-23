package com.suishi.camera.feature.init;

import com.suishi.camera.camera.ICameraBuilder;
import com.suishi.camera.feature.Assembly;

public abstract class Init<T extends ICameraBuilder> extends Assembly<T> {

    public Init() {
        super();
    }

    @Override
    public void cameraBuilder(T builder) {
        super.cameraBuilder(builder);
    }

    @Override
    public void cameraUnBuilder() {

    }

    @Override
    public void onRelease() {

    }
}
