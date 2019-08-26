package com.suishi.sslive.mode.mediacodec;

import android.media.AudioFormat;
import android.media.MediaCodecInfo;

import com.suishi.sslive.mode.engine.audio.AudioManager;

/**
 * Created by zhaozq on 2018/4/3.
 */

public class AudioMediaConfig {

    public static final int DEFAULT_FREQUENCY = 44100;
    public static final int DEFAULT_MAX_BPS = 64;
    public static final int DEFAULT_MIN_BPS = 32;
    public static final int DEFAULT_ADTS = 0;
    public static final String DEFAULT_MIME = "audio/mp4a-latm";
    public static final int DEFAULT_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public static final int DEFAULT_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    public static final int DEFAULT_CHANNEL_COUNT = 1;
    public static final boolean DEFAULT_AEC = false;


    public static final String mime="audio/mp4a-latm";

    public static final int aacProfile=MediaCodecInfo.CodecProfileLevel.AACObjectLC;

    public static final int frequency= AudioManager.instance().getAudioConfig().getSampleRate();

    public int maxBps=64;

    public int channelCount=AudioManager.instance().getAudioConfig().getmChannel();

    public int encoding=AudioManager.instance().getAudioConfig().getAudioFormat();
}
