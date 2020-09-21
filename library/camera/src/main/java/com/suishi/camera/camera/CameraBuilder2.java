package com.suishi.camera.camera;

import com.suishi.camera.camera.init.DefaultInit;
import com.suishi.camera.camera.init.Init;
import com.suishi.camera.camera.open.DefaultOpen;

public class CameraBuilder2 extends ICameraBuilder<DefaultInit, DefaultOpen,CameraWrapper2Impl>{


    @Override
    CameraWrapper2Impl build() {
        return new CameraWrapper2Impl(this);
    }

}
