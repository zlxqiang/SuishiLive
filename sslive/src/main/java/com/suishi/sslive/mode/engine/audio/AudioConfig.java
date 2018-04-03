package com.suishi.sslive.mode.engine.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by admin on 2018/3/24.
 */

public class AudioConfig {

    private   int mSampleRate = 48000;

    private  int mChannel = 2;

    private  int mChannelConfig =AudioFormat.CHANNEL_IN_STEREO;

    private  int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private  int mBuffsize = 4096;


    public AudioConfig() {
    }

    public AudioConfig setChannel(int channel) {
        mChannel = channel;
        if (mChannel == 2) {
            mChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
        } else {
            mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
        }
        return this;
    }

    public AudioConfig setaudioFormat(int format) {
        if (format == 16) {
            mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
        } else {
            mAudioFormat = AudioFormat.ENCODING_PCM_8BIT;
        }
        return this;
    }


    public int getmSampleRate() {
        return mSampleRate;
    }

    public int getmChannel() {
        return mChannel;
    }

    public int getmChannelConfig() {
        return mChannelConfig;
    }

    public int getmAudioFormat() {
        return mAudioFormat;
    }

    public int getmBuffsize() {
        return mBuffsize;
    }
}
