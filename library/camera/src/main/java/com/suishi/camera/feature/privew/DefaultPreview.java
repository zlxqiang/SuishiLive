package com.suishi.camera.feature.privew;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.suishi.camera.camera.CameraBuilder2;
import com.suishi.camera.feature.init.CameraInfo;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.utils.ToastUtil;

import java.util.Arrays;

/**
 *
 */
public class DefaultPreview extends Preview<CameraBuilder2> {

    private SurfaceTexture mSurfaceTexture;

    private Surface mPreviewSurface;

    private CaptureRequest.Builder mPreviewRequestBuilder;

    private CaptureRequest mPreviewRequest;

    private CameraCaptureSession mCaptureSession;

    private boolean isPreview=false;

    public DefaultPreview(SurfaceTexture surfaceTexture) {
        this.mSurfaceTexture = surfaceTexture;
    }

    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);
        DefaultInit init = builder.getInit();
        if (init != null) {
            CameraInfo cameraInfo =init.getCurrentCamera();
            mSurfaceTexture.setDefaultBufferSize(cameraInfo.getSize().getWidth(), cameraInfo.getSize().getHeight());
            mPreviewSurface = new Surface(mSurfaceTexture);
            try {
                DefaultOpen open = builder.getOpen();
                if(open!=null) {
                    CameraDevice device = open.getDevice();
                    if(device!=null) {
                        mPreviewRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        mPreviewRequestBuilder.addTarget(mPreviewSurface);

                        device.createCaptureSession(Arrays.asList(mPreviewSurface),
                                new CameraCaptureSession.StateCallback() {

                                    @Override
                                    public void onConfigured(@NonNull CameraCaptureSession session) {
                                        mCaptureSession = session;
                                        // 设置连续自动对焦
                                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest
                                                .CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                        // 设置自动曝光
                                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest
                                                .CONTROL_AE_MODE_ON_AUTO_FLASH);
                                        // 设置完后自动开始预览
                                        mPreviewRequest = mPreviewRequestBuilder.build();
                                        if(isPreview){
                                            startPreview();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                        Log.e("", "ConfigureFailed. session: mCaptureSession");
                                    }
                                }, builder.getOpen().getCameraHandler());
                    }else{
                        ToastUtil.shortToast("打开相机");
                    }
                }else{
                    throw new NullPointerException("camera not open");
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }else{
            throw new NullPointerException("camera not init");
        }
    }

    public void startPreview() {
        if (mCaptureSession == null || mPreviewRequestBuilder == null) {
            isPreview=true;
            return;
        }
        try {
            // 开始预览，即一直发送预览的请求
            mCaptureSession.setRepeatingRequest(mPreviewRequest, null, mCameraBuilder.getOpen().getCameraHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (mCaptureSession == null || mPreviewRequestBuilder == null) {
            isPreview=false;
            return;
        }
        try {
            mCaptureSession.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
