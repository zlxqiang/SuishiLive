package com.suishi.camera.feature.privew;

import android.content.pm.ActivityInfo;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.suishi.camera.CameraView;
import com.suishi.camera.camera.CameraBuilder2;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.camera.feature.privew.DefaultPreview;
import com.suishi.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DefaultRecord extends DefaultPreview{

    private Surface recorderSurface;

    private MediaRecorder recorder;

    private static final int RECORDER_VIDEO_BITRATE = 10_000_000;
    private static final Long MIN_REQUIRED_TIME_MILLIS = 1000L;

    private File outputFile;

    private CaptureRequest recordRequest;

    private Long recordingStartMillis = 0L;

    CaptureRequest.Builder recordRequestBuild = null;

    private boolean isRecord=false;


    public DefaultRecord(@NonNull CameraView cameraView,@NonNull File outputFile) {
        super(cameraView);
        this.outputFile = outputFile;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        try {
            recorderSurface = MediaCodec.createPersistentInputSurface();
            MediaRecorder corder = createRecorder(builder, recorderSurface);
            corder.prepare();
            corder.release();
            recorder=createRecorder(builder, recorderSurface);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            DefaultOpen open = builder.getOpen();
            if (open != null) {
                CameraDevice device = open.getDevice();
                if (device != null) {
                    recordRequestBuild = device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                    recordRequestBuild.addTarget(recorderSurface);
                    recordRequest = recordRequestBuild.build();
                } else {
                    ToastUtil.shortToast("打开相机");
                }
            } else {
                throw new NullPointerException("camera not open");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        super.cameraBuilder(builder);
    }

    @Override
    public List<Surface> getSurface() {
        List<Surface> list = super.getSurface();
        ArrayList<Surface> surfaceList = new ArrayList(list);
        surfaceList.add(recorderSurface);
        return surfaceList;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        super.onConfigured(session);
        if(isRecord){
            startReCord();
        }
    }

    public Surface getRecorderSurface() {
        return recorderSurface;
    }

    public void startReCord() {
        if (mCaptureSession == null || recordRequestBuild == null) {
            isRecord=true;
            return;
        }
        try {
            // 开始预览，即一直发送预览的请求
            mCaptureSession.setRepeatingRequest(recordRequest, null, mCameraBuilder.getOpen().getCameraHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordingStartMillis = System.currentTimeMillis();
    }

    public void stopRecord() {
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        long elapsedTimeMillis = System.currentTimeMillis() - recordingStartMillis;
        if (elapsedTimeMillis < MIN_REQUIRED_TIME_MILLIS) {
            try {
                Thread.sleep(MIN_REQUIRED_TIME_MILLIS - elapsedTimeMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("camera activity", "recording stopped output: $outputFile");
        recorder.stop();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private MediaRecorder createRecorder(CameraBuilder2 builder,Surface surface) {
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(outputFile.getAbsoluteFile());
        recorder.setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE);
        DefaultInit init = builder.getInit();
        if (init != null) {
            if (init.getCurrentCamera().getFps() > 0)
                recorder.setVideoFrameRate(init.getCurrentCamera().getFps());
            recorder.setVideoSize(init.getCurrentCamera().getSize().getWidth(), init.getCurrentCamera().getSize().getHeight());
        }
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setInputSurface(surface);
        return recorder;
    }

}
