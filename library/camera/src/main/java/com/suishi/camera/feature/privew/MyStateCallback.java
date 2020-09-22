package com.suishi.camera.feature.privew;

import android.hardware.camera2.CameraCaptureSession;

import androidx.annotation.NonNull;

public interface MyStateCallback {

     void onConfigured(@NonNull CameraCaptureSession session);


     void onConfigureFailed(@NonNull CameraCaptureSession session);

}
