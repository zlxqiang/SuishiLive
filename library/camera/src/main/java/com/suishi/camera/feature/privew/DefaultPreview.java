package com.suishi.camera.feature.privew;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.seu.magicfilter.utils.Rotation;
import com.suishi.camera.CameraView;
import com.suishi.camera.camera.CameraBuilder2;
import com.suishi.camera.feature.init.CameraInfo;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.utils.LogUtils;
import com.suishi.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class DefaultPreview extends Preview<CameraBuilder2> implements MyStateCallback {

    private CameraView mCameraView;

    private SurfaceTexture mSurfaceTexture;

    protected Surface mPreviewSurface;

    private CaptureRequest.Builder mPreviewRequestBuilder;

    private CaptureRequest mPreviewRequest;

    protected CameraCaptureSession mCaptureSession;

    private boolean isPreview=false;

    public DefaultPreview(@NonNull CameraView cameraView) {
        this.mCameraView = cameraView;
    }

    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);
        DefaultInit init = builder.getInit();
        if (init != null) {
            CameraInfo cameraInfo =init.getCurrentCamera();
            mCameraView.getRender().setRotation(Rotation.ROTATION_90, init.isFrontCamera(), false);
            mCameraView.getRender().setPreviewSize(cameraInfo.getSize());
            mSurfaceTexture=mCameraView.getRender().getSurfaceTexture();
            mSurfaceTexture.setDefaultBufferSize(cameraInfo.getSize().getWidth(), cameraInfo.getSize().getHeight());
            mPreviewSurface = new Surface(mSurfaceTexture);
            try {
                DefaultOpen open = builder.getOpen();
                if(open!=null) {
                    CameraDevice device = open.getDevice();
                    if(device!=null) {
                        mPreviewRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        // 设置连续自动对焦
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest
                                .CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 设置自动曝光
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest
                                .CONTROL_AE_MODE_ON_AUTO_FLASH);
                        mPreviewRequestBuilder.addTarget(mPreviewSurface);
                        mPreviewRequest = mPreviewRequestBuilder.build();
                    }else{
                        throw new IllegalStateException("camera not open");
                    }
                }else{
                    throw new NullPointerException("camera not open");
                }
            } catch (CameraAccessException e) {
                throw new NullPointerException(e.getMessage());
            }
        }else{
            throw new NullPointerException("camera not init");
        }
    }


    public List<Surface> getSurface(){
        return Arrays.asList(mPreviewSurface);
    }

    protected void createCaptureSession(){
        try {
            CameraDevice cameraDevice = mCameraBuilder.getOpen().getDevice();
            if(cameraDevice!=null) {
                cameraDevice.createCaptureSession(getSurface(), new CameraCaptureSession.StateCallback() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        DefaultPreview.this.onConfigured(session);
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        DefaultPreview.this.onConfigureFailed(session);
                    }

                }, mCameraBuilder.getOpen().getCameraHandler());
            }else{
                throw new NullPointerException("camera device no open");
            }
        } catch (CameraAccessException e) {
            LogUtils.e("createCaptureSession",e.getMessage());
            throw new IllegalStateException("cant create cap turesession:"+e.getMessage());
        }
    }

    public CameraCaptureSession getCaptureSession() {
        return mCaptureSession;
    }

    /**
     * 等待开启预览
     */
    private boolean isWaitStart=false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public synchronized void startPreview() {
        try {
            if(canUse() && !isPreview && !isWaitStart) {
                isWaitStart=true;
                mCaptureSession.setRepeatingRequest(mPreviewRequest, new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                        super.onCaptureStarted(session, request, timestamp, frameNumber);
                        isPreview = true;
                        isWaitStart=false;
                       // LogUtils.e("preview setRepeatingRequest","onCaptureStarted");
                    }

                    @Override
                    public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                        super.onCaptureProgressed(session, request, partialResult);
                        //LogUtils.e("preview setRepeatingRequest","onCaptureProgressed");
                    }

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                       // LogUtils.e("preview setRepeatingRequest","onCaptureCompleted");
                    }

                    @Override
                    public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                        super.onCaptureFailed(session, request, failure);
                        LogUtils.e("preview setRepeatingRequest","onCaptureFailed"+failure.toString());
                        isPreview=false;
                    }

                    @Override
                    public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
                        super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                        LogUtils.e("preview setRepeatingRequest","onCaptureSequenceCompleted");
                        isPreview=false;
                    }

                    @Override
                    public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
                        super.onCaptureSequenceAborted(session, sequenceId);
                        LogUtils.e("preview setRepeatingRequest","onCaptureSequenceAborted");
                    }

                    @Override
                    public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
                        super.onCaptureBufferLost(session, request, target, frameNumber);
                        LogUtils.e("preview setRepeatingRequest","onCaptureBufferLost");
                    }
                }, mCameraBuilder.getOpen().getCameraHandler());
            }
            LogUtils.e("preview","start preview ：isPreview="+isPreview+";isWaitStart="+isWaitStart);
        } catch (CameraAccessException e) {
            LogUtils.e("preview start",e.getMessage());
        }
    }

    public void stopPreview() {
        try {
            if(canUse() && isPreview && !isWaitStart) {
                isPreview = false;
                mCaptureSession.stopRepeating();
            }
            LogUtils.e("preview","stopRepeating");
        } catch (CameraAccessException e) {
            LogUtils.e("preview stop",e.getMessage());
        }
    }

    private boolean canUse(){
       return mCaptureSession != null && mPreviewRequest != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        LogUtils.e("createCaptureSession","success");
        mCaptureSession = session;
        startPreview();
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        LogUtils.e("preview", "ConfigureFailed. session: mCaptureSession");
    }

    @Override
    public void cameraUnBuilder() {
        super.cameraUnBuilder();
        stopPreview();
        if(mPreviewRequest!=null){
            mPreviewRequest=null;
        }
        if(mCaptureSession!=null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }

    }

    @Override
    public void onRelease() {
        super.onRelease();

    }
}
