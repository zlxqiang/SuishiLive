package com.suishi.sslive.mode.mediacodec;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;

import com.suishi.sslive.mode.engine.audio.AudioManager;

import java.nio.ByteBuffer;


/**
 */
@TargetApi(18)
public class AudioMediaCodec implements AudioManager.AudioFrameCallBack{

    private OnMediaCodecAudioEncodeListener mListener;

    private MediaCodec mMediaCodec;

    MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    public  boolean init(AudioMediaConfig configuration){
        MediaFormat format = MediaFormat.createAudioFormat(AudioMediaConfig.mime, AudioMediaConfig.frequency, configuration.channelCount);
        if(AudioMediaConfig.mime.equals("audio/mp4a-latm")) {
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, AudioMediaConfig.aacProfile);
        }
        format.setInteger(MediaFormat.KEY_BIT_RATE, configuration.maxBps * 1024);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, AudioMediaConfig.frequency);
        int maxInputSize = getRecordBufferSize(configuration);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxInputSize);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, configuration.channelCount);

        try {
            mMediaCodec = MediaCodec.createEncoderByType(AudioMediaConfig.mime);
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

    public MediaCodec getAudioMediaCodec(){
        return mMediaCodec;
    }

    private static int getRecordBufferSize(AudioMediaConfig audioConfiguration) {
        int frequency = AudioMediaConfig.frequency;
        int audioEncoding = audioConfiguration.encoding;
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        if(audioConfiguration.channelCount == 2) {
            channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        }
        int size = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        return size;
    }

    public void setAudioEncodeListener(OnMediaCodecAudioEncodeListener listener) {
        mListener = listener;
    }

    void start() {
        mMediaCodec.start();
    }

    private void encoder(byte[] input) {
        if(mMediaCodec == null) {
            return;
        }
        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(12000);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(input);
            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
        }

        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 12000);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            if(mListener != null) {
                mListener.onMediaCodecAudioEncode(outputBuffer, mBufferInfo);
            }
            mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 0);
        }
    }

    @Override
    public void onAudioFrame(byte[] chunkPCM, int length) {
        encoder(chunkPCM);
    }


    synchronized public void stop() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
    }

    public interface OnMediaCodecAudioEncodeListener {
        void onMediaCodecAudioEncode(ByteBuffer bb, MediaCodec.BufferInfo bi);
    }
}
