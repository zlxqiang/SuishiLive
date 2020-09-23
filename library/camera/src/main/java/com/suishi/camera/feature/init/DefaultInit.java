package com.suishi.camera.feature.init;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.util.Size;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.suishi.camera.camera.CameraBuilder2;
import com.suishi.camera.utils.OrientationLiveData;

import java.util.ArrayList;
import java.util.Arrays;


public class DefaultInit extends Init<CameraBuilder2>{

    private Context mContext;

    private LifecycleOwner mLifecycleOwner;

    private CameraManager mCameraManager;

    private ArrayList<CameraInfo> mBaceCameraInfo;

    private ArrayList<CameraInfo> mFrontCameraInfo;

    private CameraCharacteristics mCharacteristics;

    private OrientationLiveData relativeOrientation;

    private CameraInfo mCurrentCamera;

    public DefaultInit(Context context, LifecycleOwner lifecycleOwner) {
        mContext=context;
        mLifecycleOwner=lifecycleOwner;
        mCameraManager= (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        getCamerList();
    }

    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);
        try {
            if(mBaceCameraInfo!=null && mBaceCameraInfo.size()>0) {
                mCurrentCamera = mBaceCameraInfo.get(0);
                mCharacteristics = mCameraManager.getCameraCharacteristics(mCurrentCamera.getCameraId());
            }else if(mFrontCameraInfo!=null && mFrontCameraInfo.size()>0){
                mCurrentCamera = mFrontCameraInfo.get(0);
                mCharacteristics = mCameraManager.getCameraCharacteristics(mCurrentCamera.getCameraId());
            }else{
                throw new IllegalStateException("沒有可用相機");
            }
            relativeOrientation=new OrientationLiveData(mContext,mCharacteristics);
            relativeOrientation.observe(mLifecycleOwner, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {

                }
            });
        } catch (CameraAccessException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }


    public void getCamerList(){
        mBaceCameraInfo=new ArrayList<>();
        mFrontCameraInfo=new ArrayList<>();
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
                        if(characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT){
                            mFrontCameraInfo.add(info);
                        }
                        if(characteristics.get(CameraCharacteristics.LENS_FACING)== CameraCharacteristics.LENS_FACING_BACK){
                            mBaceCameraInfo.add(info);
                        }

                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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


    public CameraInfo getCurrentCamera() {
        return mCurrentCamera;
    }

    public void switchCamera(){
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCurrentCamera.getCameraId());
            switch (characteristics.get(CameraCharacteristics.LENS_FACING)){
                case CameraCharacteristics.LENS_FACING_BACK:{
                   mCurrentCamera=mFrontCameraInfo.get(0);
                }
                break;
                case CameraCharacteristics.LENS_FACING_FRONT:{
                    mCurrentCamera=mBaceCameraInfo.get(0);
                }
                break;
                case CameraCharacteristics.LENS_FACING_EXTERNAL:{

                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isFrontCamera(){
        if(mCurrentCamera!=null){
            try {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCurrentCamera.getCameraId());
                switch (characteristics.get(CameraCharacteristics.LENS_FACING)){
                    case CameraCharacteristics.LENS_FACING_BACK:{
                       return false;
                    }
                    case CameraCharacteristics.LENS_FACING_FRONT:{
                        return true;
                    }
                    default:{
                        return false;
                    }
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }else{
            throw new NullPointerException("no camera can use");
        }
        return false;
    }

    @Override
    public void cameraUnBuilder() {
        super.cameraUnBuilder();
    }

    @Override
    public void onRelease() {
        super.onRelease();
        mContext=null;
        mCameraManager=null;
        mBaceCameraInfo=null;
        mCurrentCamera=null;
    }
}
