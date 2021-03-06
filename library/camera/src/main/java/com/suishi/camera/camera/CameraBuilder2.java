package com.suishi.camera.camera;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.suishi.camera.feature.close.DefaultClose;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.camera.feature.privew.DefaultPreview;
import com.suishi.camera.feature.privew.DefaultRecord;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CameraBuilder2 extends ICameraBuilder<DefaultInit, DefaultOpen, DefaultRecord, DefaultClose,CameraWrapper2Impl>{

    @Override
    CameraWrapper2Impl build() {
        return new CameraWrapper2Impl(this);
    }

}
