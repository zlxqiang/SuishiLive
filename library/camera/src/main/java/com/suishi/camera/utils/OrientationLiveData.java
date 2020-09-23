package com.suishi.camera.utils;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.view.OrientationEventListener;
import android.view.Surface;

import androidx.lifecycle.LiveData;

public class OrientationLiveData extends LiveData<Integer> {


    private Context mContenx;

    private CameraCharacteristics mCharacteristics;

    private OrientationEventListener listener;

    public OrientationLiveData(Context contenx, CameraCharacteristics characteristics) {
        this.mContenx = contenx;
        this.mCharacteristics = characteristics;

        listener=new OrientationEventListener(mContenx.getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation=0;
                if(rotation<=45) {
                    rotation=Surface.ROTATION_0;
                }else if(rotation<=135) {
                    rotation = Surface.ROTATION_90;
                }else if(rotation<=225) {
                    rotation = Surface.ROTATION_180;
                }else if(rotation<=315) {
                    rotation=Surface.ROTATION_270;
                }else {
                    rotation = Surface.ROTATION_0;
                }
                int relative=computeRelativeRotation(mCharacteristics,rotation);
//                if(relative!=getValue()){
//                    postValue(relative);
//                }
            }
        };
    }

    private static int computeRelativeRotation(CameraCharacteristics characteristics, int surfaceRotation) {
        int sensorOrientationDegrees = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int deviceOrientationDegrees;
        switch (surfaceRotation) {
            case Surface.ROTATION_0: {
                deviceOrientationDegrees = 0;
            }
            break;
            case Surface.ROTATION_90: {
                deviceOrientationDegrees = 90;
            }
            break;
            case Surface.ROTATION_180: {
                deviceOrientationDegrees = 180;
            }
            break;
            case Surface.ROTATION_270: {
                deviceOrientationDegrees = 270;
            }
            break;
            default: {
                deviceOrientationDegrees = 0;
            }
        }
        int sign=0;
        if(characteristics.get(CameraCharacteristics.LENS_FACING)==CameraCharacteristics.LENS_FACING_FRONT) {
            sign=1;
        }else {
            sign=-1;
        }
        return (sensorOrientationDegrees-(deviceOrientationDegrees*sign)+360)%360;

    }


    @Override
    protected void onActive() {
        super.onActive();
        listener.enable();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        listener.disable();
    }
}



