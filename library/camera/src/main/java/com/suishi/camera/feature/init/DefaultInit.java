package com.suishi.camera.feature.init;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.util.Size;

import com.suishi.camera.camera.CameraBuilder2;

import java.util.ArrayList;
import java.util.Arrays;


public class DefaultInit extends Init<CameraBuilder2>{

    private Context mContext;

    private CameraManager mCameraManager;

    private ArrayList<CameraInfo> mCameraInfo;

    private CameraCharacteristics mCharacteristics;

    private CameraInfo mCurrentCamera;

    public DefaultInit(Context context) {
        mContext=context;
        mCameraManager= (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraInfo=getCamerList();
            if(mCameraInfo!=null && mCameraInfo.size()>0) {
                mCurrentCamera = mCameraInfo.get(0);
                mCharacteristics = mCameraManager.getCameraCharacteristics(mCurrentCamera.getCameraId());
            }else{
                throw new IllegalStateException("沒有可用相機");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);

    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }


    public ArrayList<CameraInfo> getCamerList(){
        ArrayList cameraInfoList=new ArrayList<CameraInfo>();
        try {
            for (String id:mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
                String orientation=lensOrientationString(characteristics.get(CameraCharacteristics.LENS_FACING));
                int[] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                StreamConfigurationMap cameraConfig = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if(contains(capabilities)){
                    Size[] config = cameraConfig.getOutputSizes(MediaRecorder.class);
                    for (Size size:config) {
                        double secondsPerFrame = cameraConfig.getOutputMinFrameDuration(MediaRecorder.class, size) / 1_000_000_000.0;
                        int fps=0;
                        if(secondsPerFrame>0) {
                            fps=(int)(1.0/secondsPerFrame);
                        }
                        String fpsLabel=fps> 0?"$fps" : "N/A";
                        CameraInfo info = new CameraInfo("$orientation ($id) $size $fpsLabel FPS", id, size, fps);
                        cameraInfoList.add(info);
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return cameraInfoList;
    }

    private boolean contains(int[] capabilities){
        for (int i: capabilities){
            if(i==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE){
                return true;
            }
        }
        return false;
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


    public ArrayList<CameraInfo> getCameraInfo() {
        return mCameraInfo;
    }

    public CameraInfo getCurrentCamera() {
        return mCurrentCamera;
    }
}
