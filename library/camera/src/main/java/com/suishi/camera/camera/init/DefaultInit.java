package com.suishi.camera.camera.init;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.util.Size;

import com.yzq.zxinglibrary.camera.OpenCameraInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultInit extends Init{

    private CameraManager mCameraManager;

    private ArrayList<CameraInfo> mCameraInfo;

    private CameraCharacteristics mCharacteristics;


    public DefaultInit(Context context) {
        mCameraManager= (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        mCameraInfo=getCamerList();
        try {
            mCharacteristics=mCameraManager.getCameraCharacteristics(mCameraInfo.get(0).getCameraId());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }


    public ArrayList<CameraInfo> getCamerList(){
        try {
            ArrayList<CameraInfo> list=new ArrayList();
            for (String id:mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
                String orientation=lensOrientationString(characteristics.get(CameraCharacteristics.LENS_FACING));
                int[] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                StreamConfigurationMap cameraConfig = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if( Arrays.asList(capabilities).contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE)){
                    for (Size size:cameraConfig.getOutputSizes( MediaRecorder.class)
                         ) {
                        double secondsPerFrame = cameraConfig.getOutputMinFrameDuration(MediaRecorder.class, size) / 1_000_000_000.0;
                        int fps=0;
                        if(secondsPerFrame>0) {
                            fps=(int)(1.0/secondsPerFrame);
                        }
                        String fpsLabel=fps> 0?"$fps" : "N/A";
                        list.add(new CameraInfo("$orientation ($id) $size $fpsLabel FPS", id, size, fps));
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String lensOrientationString(int value){
        switch (value){
            case CameraCharacteristics.LENS_FACING_BACK: {
               return  "Back";
            }
            case CameraCharacteristics.LENS_FACING_FRONT : {
                return "Front";
            }
            case CameraCharacteristics.LENS_FACING_EXTERNAL : {
                return "External";
            }
            default:{
               return  "UnKnown";
            }
        }
    }

}
