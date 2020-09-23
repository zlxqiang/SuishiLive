package com.suishi.camera.utils;

import android.hardware.Camera;
import android.view.Surface;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by weight68kg on 2018/5/8.
 */

public class CameraUtils {
    public static int orientationDegree = 0;

    public static void setCameraDisplayOrientation(AppCompatActivity activity, int cameraId, Camera camera) {
        orientationDegree = getCameraPhotoDegree(activity,cameraId);
        camera.setDisplayOrientation(orientationDegree);
    }

    public static int getCameraPhotoDegree(AppCompatActivity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        //前置
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        }
        //后置
        else {
            result = (info.orientation - degrees + 360) % 360;
        }
        result = (info.orientation - degrees + 360) % 360;
        return result;
    }
}
