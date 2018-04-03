package com.seu.magicfilter.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

import com.seu.magicfilter.camera.utils.CameraUtils;

import java.io.IOException;
import java.util.List;

public class CameraEngine {

    private static Camera camera = null;

    private static int cameraID = 1;

    private static SurfaceTexture surfaceTexture;

    public static int minLength;

    public static CameraConfiguration cameraConfiguration=CameraConfiguration.createDefault();

    private static byte[] m_nv21 = null;

    public static Camera getCamera(){
        return camera;
    }

    public static boolean openCamera(){
        if(camera == null){
            try{
                camera = Camera.open(cameraID);
                setDefaultParameters2();
                return true;
            }catch(RuntimeException e){
                return false;
            }
        }
        return false;
    }

    public static boolean openCamera(int id){
        if(camera == null){
            try{
                camera = Camera.open(id);
                cameraID = id;
                setDefaultParameters2();
                return true;
            }catch(RuntimeException e){
                return false;
            }
        }
        return false;
    }

    public static void releaseCamera(){
        if(camera != null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private static Camera.PreviewCallback mCallBack;

    public static void setPreviewCallBack(Camera.PreviewCallback callback){
        mCallBack=callback;
        if(camera==null){
            openCamera();
        }
        if(mCallBack!=null) {
            addCallbackBuffer();
            camera.setPreviewCallbackWithBuffer(mCallBack);
        }
    }

    public static void addCallbackBuffer(){
        if(camera!=null && m_nv21!=null)
        camera.addCallbackBuffer(m_nv21);
    }

    public void resumeCamera(){
        openCamera();
    }

    public void setParameters(Parameters parameters){
        camera.setParameters(parameters);
    }

    public Parameters getParameters(){
        if(camera != null)
            camera.getParameters();
        return null;
    }

    public static void switchCamera(){
        releaseCamera();
        cameraID = cameraID == 0 ? 1 : 0;
        openCamera(cameraID);
        startPreview(surfaceTexture);
    }

    private static void setDefaultParameters(){
        Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        Size previewSize = CameraUtils.getLargePreviewSize(camera);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        Size pictureSize = CameraUtils.getLargePictureSize(camera);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setRotation(90);
        camera.setParameters(parameters);
    }


    private static void setDefaultParameters2(){
        Parameters params = camera.getParameters();
        int[] range =params.getSupportedPreviewFpsRange().get(0);
        List supportedPreviewSizeList = params.getSupportedPreviewSizes();

        for(int j = 0; j < supportedPreviewSizeList.size(); ++j) {
            Size size = (Size) supportedPreviewSizeList.get(j);
            if (size.width == cameraConfiguration.width && size.height == cameraConfiguration.height) {
                params.setPreviewFormat(17);
                int nFormat = params.getPreviewFormat();
                minLength=cameraConfiguration.width * cameraConfiguration.height * ImageFormat.getBitsPerPixel(nFormat) / 8;
                //  this.m_nMaxZoom = params.getMaxZoom();
                m_nv21=new byte[minLength];
                //预览旋转
                //camera.setDisplayOrientation(90);
                params.setPreviewSize(cameraConfiguration.width, cameraConfiguration.height);
               // Size previewSize = CameraUtils.getLargePreviewSize(camera);
                //params.setPreviewSize(previewSize.width, previewSize.height);
                if (cameraConfiguration.fps <= 0) {
                    //configuration.fps = 1;
                }

                // this.m_nSumTime = 0L;
                // this.m_nDuration = 1000 / configuration.fps;
                if (cameraConfiguration.fps * 1000 < range[0]) {
                    params.setPreviewFrameRate((int) ((double) range[0] / 1000.0D + 0.9D));
                } else if (cameraConfiguration.fps * 1000 < range[1]) {
                    params.setPreviewFrameRate(cameraConfiguration.fps);
                } else {
                    params.setPreviewFrameRate(range[0] / 1000);
                }

                List focusModes = params.getSupportedFocusModes();
                if (focusModes.contains("continuous-video")) {
                    params.setFocusMode("continuous-video");
                } else if (focusModes.contains("auto")) {
                    params.setFocusMode("auto");
                }
                //相机旋转
                params.setRotation(90);
                camera.setParameters(params);
                return;
            }
        }
    }

    private static Size getPreviewSize(){
        return camera.getParameters().getPreviewSize();
    }

    private static Size getPictureSize(){
        return camera.getParameters().getPictureSize();
    }

    public static void startPreview(SurfaceTexture surfaceTexture){
        if(camera != null)
            try {
                camera.setPreviewTexture(surfaceTexture);
                CameraEngine.surfaceTexture = surfaceTexture;
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void startPreview(){
        if(camera != null)
            camera.startPreview();
    }

    public static void stopPreview(){
        camera.stopPreview();
    }

    public static void setRotation(int rotation){
        Parameters params = camera.getParameters();
        params.setRotation(rotation);
        camera.setParameters(params);
    }

    public static void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback,
                                   Camera.PictureCallback jpegCallback){
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public static com.seu.magicfilter.camera.utils.CameraInfo getCameraInfo(){
        com.seu.magicfilter.camera.utils.CameraInfo info = new com.seu.magicfilter.camera.utils.CameraInfo();
        Size size = getPreviewSize();
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);
        info.previewWidth = size.width;
        info.previewHeight = size.height;
        info.orientation = cameraInfo.orientation;
        info.isFront = cameraID == 1;
        size = getPictureSize();
        info.pictureWidth = size.width;
        info.pictureHeight = size.height;
        return info;
    }
}