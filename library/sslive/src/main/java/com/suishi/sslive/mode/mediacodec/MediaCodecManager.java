package com.suishi.sslive.mode.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import com.suishi.sslive.utils.HardWareSupport;
import com.suishi.sslive.utils.LiveLog;

/**
 *
 */
@TargetApi(18)
public class MediaCodecManager {


    private AudioMediaCodec mAudioMediaCodec;

    private VideoMediaCodec mVideoMediaCodec;

    public MediaCodecManager() {
        mAudioMediaCodec = new AudioMediaCodec();
        mVideoMediaCodec = new VideoMediaCodec();
    }

    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }


    /**
     * 初始化检测
     *
     * @return
     */
    public boolean init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            LiveLog.w(this, "Android sdk version error");
            return false;
        }
        boolean isSupportHardWareEncode = HardWareSupport.isH265EncoderSupport();
        if (!isSupportHardWareEncode) {
            return isSupportHardWareEncode;
        }

        AudioMediaConfig mAudioConfiguration = new AudioMediaConfig();
        VideoMediaConfig mVideoConfiguration = new VideoMediaConfig();

        MediaCodecInfo mVideoMediaCodecInfo = selectCodec(mVideoConfiguration.mime);
        if (mVideoMediaCodecInfo == null) {
            LiveLog.w(this, "Video type error");
            return false;
        }

        MediaCodecInfo mAudioMediaCodecInfo = selectCodec(AudioMediaConfig.mime);
        if (mAudioMediaCodecInfo == null) {
            LiveLog.w(this, "Audio type error");
            return false;
        }

        if (!mVideoMediaCodec.init(mVideoConfiguration)) {
            LiveLog.w(this, "Video mediacodec configuration error");
            return false;
        }

        if (!mAudioMediaCodec.init(mAudioConfiguration)) {
            LiveLog.w(this, "Audio mediacodec configuration error");
            return false;
        }
        //是否已经运行
        if (false) {
            LiveLog.w(this, "Can not record the audio");
            return false;
        }
        return true;
    }

    /**
     * 开始
     */
    public void start() {
        mAudioMediaCodec.start();
        mVideoMediaCodec.start();
    }

    /**
     * 停止
     */
    public void stop() {
        mAudioMediaCodec.stop();
        mVideoMediaCodec.stop();
    }

    /**
     * 视频编码器
     *
     * @return
     */
    public VideoMediaCodec getVideoMediaCodec() {
        return mVideoMediaCodec;
    }


    /**
     * 音频编码器
     *
     * @return
     */
    public AudioMediaCodec getAudioMediaCodec() {
        return mAudioMediaCodec;
    }


    /**
     * 音频编码回调
     *
     * @param listener
     */
    public void setAudioEncodeListener(AudioMediaCodec.OnMediaCodecAudioEncodeListener listener) {
        mAudioMediaCodec.setAudioEncodeListener(listener);
    }

    /**
     * 视频编码回调
     *
     * @param listener
     */
    public void setVideoEncodeListener(VideoMediaCodec.OnMediaCodecVideoEncodeListener listener) {
        mVideoMediaCodec.setVideoEncodeListener(listener);
    }

}
