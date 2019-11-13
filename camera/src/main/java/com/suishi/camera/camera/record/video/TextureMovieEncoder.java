/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.suishi.camera.camera.record.video;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.suishi.camera.ContextInstance;
import com.suishi.camera.camera.filter.AFilter;
import com.suishi.camera.camera.filter.NoFilter;
import com.suishi.camera.camera.gpufilter.basefilter.GPUImageFilter;
import com.suishi.camera.camera.gpufilter.basefilter.MagicCameraInputFilter;
import com.suishi.camera.camera.record.gles.EglCore;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;

/**
 *
 */
public class TextureMovieEncoder implements Runnable {
    /**
     *
     */
    private static final String TAG ="TextureMovieEncoder";

    /**
     *
     */
    private static final boolean VERBOSE = false;
    /**
     *
     */
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_FRAME_AVAILABLE = 2;
    private static final int MSG_SET_TEXTURE_ID = 3;
    private static final int MSG_UPDATE_SHARED_CONTEXT = 4;
    private static final int MSG_QUIT = 5;
    private static final int MSG_PAUSE = 6;
    private static final int MSG_RESUME = 7;

    // ----- accessed exclusively by encoder thread -----
    private WindowSurface mInputWindowSurface;
    private EglCore mEglCore;
    private MagicCameraInputFilter mInput;
    private int mTextureId;

    private VideoEncoderCore mVideoEncoder;

    // ----- accessed by multiple threads -----
    private volatile EncoderHandler mHandler;
    // guards ready/running
    private Object mReadyFence = new Object();
    private boolean mReady;
    private boolean mRunning;
    private GPUImageFilter filter;
    private FloatBuffer gLCubeBuffer;
    private FloatBuffer gLTextureBuffer;
    //第一帧的时间戳
    private long baseTimeStamp = -1;

    public TextureMovieEncoder() {

    }

    public static class EncoderConfig {
        final String path;
        final int mWidth;
        final int mHeight;
        final int mBitRate;
        final EGLContext mEglContext;

        public EncoderConfig(String path, int width, int height, int bitRate,
                             EGLContext sharedEglContext, Camera.CameraInfo info) {
            this.path = path;
            mWidth = width;
            mHeight = height;
            mBitRate = bitRate;
            mEglContext = sharedEglContext;
        }

        @Override
        public String toString() {
            return "EncoderConfig: " + mWidth + "x" + mHeight + " @" + mBitRate +
                    " to '" + path + "' ctxt=" + mEglContext;
        }
    }

    public void startRecording(EncoderConfig config) {
        Log.d(TAG, "Encoder: startRecording()");
        synchronized (mReadyFence) {
            if (mRunning) {
                Log.w(TAG, "Encoder thread already running");
                return;
            }
            mRunning = true;
            new Thread(this, "TextureMovieEncoder").start();
            while (!mReady) {
                try {
                    mReadyFence.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_START_RECORDING, config));
    }

    public void stopRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
        mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
    }

    public void pauseRecording() {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage(MSG_PAUSE);
            if (message != null) {
                mHandler.sendMessage(message);
            }
        }
    }

    public void resumeRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_RESUME));
    }

    public boolean isRecording() {
        synchronized (mReadyFence) {
            return mRunning;
        }
    }

    public void updateSharedContext(EGLContext sharedContext) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SHARED_CONTEXT, sharedContext));
    }

    public void frameAvailable(SurfaceTexture st) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }

        float[] transform = new float[16];
        st.getTransformMatrix(transform);
        long timestamp = st.getTimestamp();
        if (timestamp == 0) {
            Log.w(TAG, "HEY: got SurfaceTexture with timestamp of zero");
            return;
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE,
                (int) (timestamp >> 32), (int) timestamp, transform));
    }

    public void setTextureId(int id) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TEXTURE_ID, id, 0, null));
    }

    @Override
    public void run() {
        // Establish a Looper for this thread, and define a Handler for it.
        Looper.prepare();
        synchronized (mReadyFence) {
            mHandler = new EncoderHandler(this);
            mReady = true;
            mReadyFence.notify();
        }
        Looper.loop();

        Log.d(TAG, "Encoder thread exiting");
        synchronized (mReadyFence) {
            mReady = mRunning = false;
            mHandler = null;
        }
    }


    private static class EncoderHandler extends Handler {
        private WeakReference<TextureMovieEncoder> mWeakEncoder;

        public EncoderHandler(TextureMovieEncoder encoder) {
            mWeakEncoder = new WeakReference<TextureMovieEncoder>(encoder);
        }

        @Override  // runs on encoder thread
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;

            TextureMovieEncoder encoder = mWeakEncoder.get();
            if (encoder == null) {
                Log.w(TAG, "EncoderHandler.handleMessage: encoder is null");
                return;
            }

            switch (what) {
                case MSG_START_RECORDING:
                    encoder.handleStartRecording((EncoderConfig) obj);
                    break;
                case MSG_STOP_RECORDING:
                    encoder.handleStopRecording();
                    break;
                case MSG_FRAME_AVAILABLE:
                    long timestamp = (((long) inputMessage.arg1) << 32) |
                            (((long) inputMessage.arg2) & 0xffffffffL);
                    encoder.handleFrameAvailable((float[]) obj, timestamp);
                    break;
                case MSG_SET_TEXTURE_ID:
                    encoder.handleSetTexture(inputMessage.arg1);
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    encoder.handleUpdateSharedContext((EGLContext) inputMessage.obj);
                    break;
                case MSG_QUIT:
                    Looper.myLooper().quit();
                    break;
                case MSG_PAUSE:
                    encoder.handlePauseRecording();
                    break;
                case MSG_RESUME:
                    encoder.handleResumeRecording();
                    break;
                default:
                    throw new RuntimeException("Unhandled msg what=" + what);
            }
        }
    }

    /**
     * Starts recording.
     */
    private void handleStartRecording(EncoderConfig config) {
        Log.d(TAG, "handleStartRecording " + config);
        prepareEncoder(config.mEglContext, config.mWidth, config.mHeight, config.mBitRate,
                config.path);
    }

    private void handleFrameAvailable(float[] transform, long timestampNanos) {
        if (VERBOSE) Log.d(TAG, "handleFrameAvailable tr=" + transform);
        mVideoEncoder.drainEncoder(false);
        Log.e("hero", "---setTextureId==" + mTextureId);
        mShowFilter.setTextureId(mTextureId);
        mShowFilter.draw();
        if (baseTimeStamp == -1) {
            baseTimeStamp = System.nanoTime();
            mVideoEncoder.startRecord();
        }
        long nano = System.nanoTime();
        long time = nano - baseTimeStamp - pauseDelayTime;
        System.out.println("TimeStampVideo=" + time + ";nanoTime=" + nano + ";baseTimeStamp=" + baseTimeStamp + ";pauseDelay=" + pauseDelayTime);
        mInputWindowSurface.setPresentationTime(time);
        mInputWindowSurface.swapBuffers();
    }

    long pauseDelayTime;
    long onceDelayTime;

    private void handlePauseRecording() {
        onceDelayTime = System.nanoTime();
        mVideoEncoder.pauseRecording();
    }

    private void handleResumeRecording() {
        onceDelayTime = System.nanoTime() - onceDelayTime;
        pauseDelayTime += onceDelayTime;
        mVideoEncoder.resumeRecording();
    }

    private void handleStopRecording() {
        Log.d(TAG, "handleStopRecording");
        mVideoEncoder.drainEncoder(true);
        mVideoEncoder.stopAudRecord();
        releaseEncoder();
    }

    private void handleSetTexture(int id) {
        //Log.d(TAG, "handleSetTexture " + id);
        mTextureId = id;
    }

    private void handleUpdateSharedContext(EGLContext newSharedContext) {
        Log.d(TAG, "handleUpdatedSharedContext " + newSharedContext);

        // Release the EGLSurface and EGLContext.
        mInputWindowSurface.releaseEglSurface();
        mInput.destroy();
        mEglCore.release();

        // Create a new EGLContext and recreate the window surface.
        mEglCore = new EglCore(newSharedContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface.recreate(mEglCore);
        mInputWindowSurface.makeCurrent();

        // Create new programs and such for the new context.
        mInput = new MagicCameraInputFilter();
        mInput.init();
        filter = null;
        if (filter != null) {
            filter.init();
            filter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
            filter.onDisplaySizeChanged(mVideoWidth, mVideoHeight);
        }
    }

    private void prepareEncoder(EGLContext sharedContext, int width, int height, int bitRate,
                                String path) {
        try {
            mVideoEncoder = new VideoEncoderCore(width, height, bitRate, path);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mVideoWidth = width;
        mVideoHeight = height;
        mEglCore = new EglCore(sharedContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface = new WindowSurface(mEglCore, mVideoEncoder.getInputSurface(), true);
        mInputWindowSurface.makeCurrent();

        mInput = new MagicCameraInputFilter();
        mInput.init();
        filter = null;
        if (filter != null) {
            filter.init();
            filter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
            filter.onDisplaySizeChanged(mVideoWidth, mVideoHeight);
        }
        mShowFilter.create();
        baseTimeStamp = -1;
    }

    private void releaseEncoder() {
        baseTimeStamp = -1;
        mVideoEncoder.release();
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
        if (mInput != null) {
            mInput.destroy();
            mInput = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        if (filter != null) {
            filter.destroy();
            filter = null;
//            type = MagicFilterType.NONE;
        }
    }

    //    private MagicFilterType type = MagicFilterType.NONE;
    private AFilter mShowFilter = new NoFilter(ContextInstance.getInstance().getResources());
//    public void setFilter(MagicFilterType type) {
//        this.type = type;
//    }

    private int mPreviewWidth = -1;
    private int mPreviewHeight = -1;
    private int mVideoWidth = -1;
    private int mVideoHeight = -1;

    public void setPreviewSize(int width, int height) {
        mPreviewWidth = width;
        mPreviewHeight = height;
    }

    public void setTextureBuffer(FloatBuffer gLTextureBuffer) {
        this.gLTextureBuffer = gLTextureBuffer;
    }

    public void setCubeBuffer(FloatBuffer gLCubeBuffer) {
        this.gLCubeBuffer = gLCubeBuffer;
    }
}
