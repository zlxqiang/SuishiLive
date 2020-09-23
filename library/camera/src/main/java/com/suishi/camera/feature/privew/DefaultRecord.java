package com.suishi.camera.feature.privew;

import android.content.pm.ActivityInfo;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.util.Range;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.suishi.camera.CameraView;
import com.suishi.camera.camera.CameraBuilder2;
import com.suishi.camera.feature.init.CameraInfo;
import com.suishi.camera.feature.init.DefaultInit;
import com.suishi.camera.feature.open.DefaultOpen;
import com.suishi.camera.feature.privew.DefaultPreview;
import com.suishi.utils.LogUtils;
import com.suishi.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DefaultRecord extends DefaultPreview{

    private Surface recorderSurface;

    private MediaRecorder mRecorder;

    private static final int RECORDER_VIDEO_BITRATE = 10_000_000;
    private static final Long MIN_REQUIRED_TIME_MILLIS = 1000L;

    private File outputFile;

    private CaptureRequest recordRequest;

    private Long recordingStartMillis = 0L;

    CaptureRequest.Builder mRecordRequestBuild = null;

    private boolean isRecord=false;


    public DefaultRecord(@NonNull CameraView cameraView,@NonNull File outputFile) {
        super(cameraView);
        this.outputFile = outputFile;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);
        try {
            recorderSurface = MediaCodec.createPersistentInputSurface();
            MediaRecorder corder = createRecorder(builder, recorderSurface);
            corder.prepare();
            corder.release();
            mRecorder=createRecorder(builder, recorderSurface);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            DefaultOpen open = builder.getOpen();
            if (open != null) {
                CameraDevice device = open.getDevice();
                if (device != null) {
                    mRecordRequestBuild = device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                    mRecordRequestBuild.addTarget(recorderSurface);
                    mRecordRequestBuild.addTarget(mPreviewSurface);
                    DefaultInit init = builder.getInit();
                    if(init!=null) {
                        CameraInfo cameraInfo = init.getCurrentCamera();
                        if(cameraInfo!=null) {
                            mRecordRequestBuild.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<Integer>(cameraInfo.getFps(),cameraInfo.getFps()));
                        }
                    }
                    recordRequest = mRecordRequestBuild.build();
                } else {
                   // throw new IllegalStateException("camera not open");
                }
            } else {
                throw new NullPointerException("camera not open model");
            }
        } catch (CameraAccessException e) {
            throw new NullPointerException(e.getMessage());
        }

        createCaptureSession();
    }

    @Override
    public List<Surface> getSurface() {
        List<Surface> list = super.getSurface();
        ArrayList<Surface> surfaceList = new ArrayList(list);
        surfaceList.add(recorderSurface);
        return surfaceList;
    }

    public Surface getRecorderSurface() {
        return recorderSurface;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startReCord() {
        try {
            if (canUse() && !isRecord && !recordRequest.isReprocess()) {
            isRecord=true;
            mCaptureSession.setRepeatingRequest(recordRequest,  new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                    //LogUtils.e("record setRepeatingRequest","onCaptureStarted");
                }

                @Override
                public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                    super.onCaptureProgressed(session, request, partialResult);
                   // LogUtils.e("record setRepeatingRequest","onCaptureProgressed");
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                   // LogUtils.e("record setRepeatingRequest","onCaptureCompleted");
                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    LogUtils.e("record setRepeatingRequest","onCaptureFailed"+failure.toString());
                }

                @Override
                public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
                    super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                    LogUtils.e("record setRepeatingRequest","onCaptureSequenceCompleted");
                }

                @Override
                public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
                    super.onCaptureSequenceAborted(session, sequenceId);
                    LogUtils.e("record setRepeatingRequest","onCaptureSequenceAborted");
                }

                @Override
                public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
                    super.onCaptureBufferLost(session, request, target, frameNumber);
                    LogUtils.e("record setRepeatingRequest","onCaptureBufferLost");
                }
            }, mCameraBuilder.getOpen().getCameraHandler());
            mRecorder.prepare();
            mRecorder.start();
            recordingStartMillis = System.currentTimeMillis();
            }
        } catch (CameraAccessException | IOException e) {
            LogUtils.e("start record",e.getMessage());
        }
    }

    public boolean canUse(){
        return mCaptureSession != null && recordRequest != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onPause(){
        if(isRecord){
            mRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onResume(){
        if(isRecord){
            mRecorder.resume();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void stopRecord() {
        if (canUse() && isRecord ) {
            long elapsedTimeMillis = System.currentTimeMillis() - recordingStartMillis;
            if (elapsedTimeMillis < MIN_REQUIRED_TIME_MILLIS) {
                try {
                    Thread.sleep(MIN_REQUIRED_TIME_MILLIS - elapsedTimeMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mRecorder.stop();
            isRecord=false;
            LogUtils.e("camera activity", "recording stopped output: $outputFile");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private MediaRecorder createRecorder(CameraBuilder2 builder, Surface surface) {
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void cameraUnBuilder() {
        stopRecord();
        super.cameraUnBuilder();
    }

    @Override
    public void onRelease() {
        super.onRelease();
        if(mRecorder!=null) {
            mRecorder.release();
            mRecorder = null;
        }
        if(recorderSurface!=null){
            recorderSurface.release();
        }
    }
}
