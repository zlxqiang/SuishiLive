package com.suishi.camera.feature.open;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.suishi.camera.camera.CameraBuilder2;
import com.suishi.utils.ToastUtil;

import static android.hardware.camera2.CameraDevice.*;

public class DefaultOpen extends Open<CameraBuilder2>{

    private Activity mContext;

    private  CameraDevice mDevice = null;

    private HandlerThread cameraThread;
    private Handler cameraHandler;

    public DefaultOpen(Activity context) {
        this.mContext = context;
        this.cameraThread =new HandlerThread("CameraThread");
        cameraThread.start();
        this.cameraHandler=new Handler(cameraThread.getLooper());
    }

    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);

    }

    public CameraDevice getDevice() {
        return mDevice;
    }

    public Handler getCameraHandler() {
        return cameraHandler;
    }

    public CameraDevice openCamera(CameraManager manager, String cameraId, final OpenStateCallback stateCallback){
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.shortToast("没有相机权限");
        }else {
            StateCallback callback=new StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mDevice =camera;
                    stateCallback.onOpened(camera);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    stateCallback.onDisconnected(camera);
                    mContext.finish();

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    stateCallback.onError(camera,error);
                    String msg;
                    switch (error) {
                        case ERROR_CAMERA_DEVICE :{
                            msg="相机设备发生了一个致命错误";
                        }
                        break;
                        case ERROR_CAMERA_DISABLED:{
                            msg="Device policy";
                        }
                        break;
                        case ERROR_CAMERA_IN_USE :{
                            msg="当前相机设备已经在一个更高优先级的地方打开了";
                        }
                        break;
                        case ERROR_MAX_CAMERAS_IN_USE :{
                            msg="已打开相机数量到上限了，无法再打开新的相机了";
                        }
                        break;
                        default: {
                            msg="UnKnown";
                        }
                    }
                    RuntimeException exc =new  RuntimeException(msg);
                    Log.e("open camera", exc.getMessage(), exc);
                }

            };
            try {
                manager.openCamera(cameraId,callback, cameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return mDevice;
    }

}
