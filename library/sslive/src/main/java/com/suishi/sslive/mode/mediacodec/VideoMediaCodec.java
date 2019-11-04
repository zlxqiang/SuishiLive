package com.suishi.sslive.mode.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.suishi.sslive.mode.engine.video.VideoManager;

import java.nio.ByteBuffer;


/**
 *
 */
public class VideoMediaCodec implements VideoManager.VideoFrameCallBack {

    private MediaCodec mMediaCodec;

    private OnMediaCodecVideoEncodeListener mListener;

    MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    public VideoMediaCodec() {
    }

    public boolean init(VideoMediaConfig videoConfiguration) {
        int videoWidth = getVideoSize(videoConfiguration.width);
        int videoHeight = getVideoSize(videoConfiguration.height);
        MediaFormat format = MediaFormat.createVideoFormat(videoConfiguration.mime, videoWidth, videoHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        format.setInteger(MediaFormat.KEY_BIT_RATE, videoConfiguration.maxBps * 1024);
        int fps = videoConfiguration.fps;
        //设置摄像头预览帧率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, videoConfiguration.ifi);
        format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
        format.setInteger(MediaFormat.KEY_COMPLEXITY, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        //  format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1228800);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(videoConfiguration.mime);
            mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
            if (mMediaCodec != null) {
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
            }
            return false;
        }
        return true;
    }

    public static int getVideoSize(int size) {
        int multiple = (int) Math.ceil(size / 16.0);
        return multiple * 16;
    }

    public void setVideoEncodeListener(OnMediaCodecVideoEncodeListener listener) {
        mListener = listener;
    }

    void start() {
        mMediaCodec.start();
    }

    private void encoder(byte[] input) {
        if (mMediaCodec == null) {
            return;
        }
        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(12000);
        if (inputBufferIndex >= 0) {
            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
//            int longs=0;
//            if(input.length<= inputBuffer.remaining()) {
//                inputBuffer.put(input);
//                longs=input.length;
//            } else {
//                // 这样会截掉一部分
//                byte[] in=subBytes(input,0,inputBuffer.remaining());
//                inputBuffer.put(in);
//                longs=in.length;
//            }
            inputBuffer.put(input);
            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
        }

        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 12000);
        while (outputBufferIndex >= 0) {
            ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            if (mListener != null) {
                mListener.onMediaCodecVideoEncode(outputBuffer, mBufferInfo);
            }
            mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 0);
        }
    }


    public void stop() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
    }

    @Override
    public void onVideoFrame(byte[] chunkPCM, int length) {
        encoder(chunkPCM);
    }

    public interface OnMediaCodecVideoEncodeListener {
        void onMediaCodecVideoEncode(ByteBuffer bb, MediaCodec.BufferInfo bi);
    }
}
