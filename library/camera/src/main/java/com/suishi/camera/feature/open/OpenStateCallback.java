package com.suishi.camera.feature.open;

import android.hardware.camera2.CameraDevice;

import androidx.annotation.NonNull;

public interface OpenStateCallback {

    void onOpened(@NonNull CameraDevice camera);

    void onDisconnected(@NonNull CameraDevice camera);

    void onError(@NonNull CameraDevice camera, int error);

}
