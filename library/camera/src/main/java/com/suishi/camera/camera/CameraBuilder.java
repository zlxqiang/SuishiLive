package com.suishi.camera.camera;


import com.suishi.camera.feature.close.Close;
import com.suishi.camera.feature.init.OldInit;
import com.suishi.camera.feature.open.OldOpen;
import com.suishi.camera.feature.privew.Preview;

public class CameraBuilder extends ICameraBuilder<OldInit, OldOpen, Preview, Close,CameraWrapperImpl> {


    @Override
    CameraWrapperImpl build() {
        return new CameraWrapperImpl(this);
    }

}
