package com.suishi.camera.camera;


import com.suishi.camera.camera.init.OldInit;
import com.suishi.camera.camera.open.OldOpen;

public class CameraBuilder extends ICameraBuilder<OldInit, OldOpen,CameraWrapperImpl> {


    @Override
    CameraWrapperImpl build() {
        return new CameraWrapperImpl(this);
    }

}
