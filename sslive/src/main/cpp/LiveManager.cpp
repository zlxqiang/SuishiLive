//
// Created by 赵增强 on 2018/3/22.
//
#include <android/bitmap.h>
#include "LiveManager.h"
#include "av/video/VideoMachining.h"

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 初始化视频
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_InitVideo(JNIEnv *env, jclass type, jint inWidth,
                                                           jint inHeight, jint outWidth,
                                                           jint outHeight,
                                                           jint fps,
                                                           jboolean mirror) {
    mMutex.lock();
    mVideoProcessor = VideoProcessor::Get();
    VideoConfig *videoEncodeArgs = (VideoConfig *) malloc(sizeof(VideoConfig));
    videoEncodeArgs->fps = fps;
    videoEncodeArgs->in_height = inHeight;
    videoEncodeArgs->in_width = inWidth;
    videoEncodeArgs->out_height = outHeight;
    videoEncodeArgs->out_width = outWidth;
    videoEncodeArgs->mirror = mirror;
    mVideoProcessor->SetVideoConfig(videoEncodeArgs);
    mVideoInit = true;
    isRelease = false;
    mMutex.unlock();
    return 0;
}

/**
 * 初始化音频
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_InitAudio(JNIEnv *env, jclass type, jint channles,
                                                           jint SampleRate, jint SampleBitRate) {
    mMutex.lock();
    mAudioProcessor = AudioProcessor::Get();
    AudioConfig *audioEncodeArgs = (AudioConfig *) malloc(sizeof(AudioConfig));
    audioEncodeArgs->avSampleFormat = AV_SAMPLE_FMT_S16;
    audioEncodeArgs->sampleRate = SampleRate;
    audioEncodeArgs->bitRate = SampleBitRate;
    audioEncodeArgs->channels = channles;
    if (audioEncodeArgs->channels == 1) {
        audioEncodeArgs->ch_layout = AV_CH_LAYOUT_MONO;
    } else if (audioEncodeArgs->channels == 2) {
        audioEncodeArgs->ch_layout = AV_CH_LAYOUT_STEREO;
    }
    audioEncodeArgs->nb_samples = 1024;
    mAudioProcessor->SetAudioConfig(audioEncodeArgs);
    mAudioInit = true;
    isRelease = false;
    mMutex.unlock();
    return 0;
}

/**
 * 初始化视频编码器
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_InitVideoEncoder(JNIEnv *env, jclass type) {
    mMutex.lock();
    if (mVideoInit) {
        mVideoEncoder = VideoEncoder::Get();
        mVideoEncoder->SetVideoProcessor(mVideoProcessor);
        mMutex.unlock();
        return mVideoEncoder->InitEncode();
    }
    LOG_D(DEBUG, "jni InitVideoEncoder failed!");
    mMutex.unlock();
    return -1;
}

/**
 * 初始化音频编码器
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_InitAudioEncoder(JNIEnv *env, jclass type) {
    mMutex.lock();
    if (mAudioInit) {
        mAudioEncoder = AudioEncoder::Get();
        mAudioEncoder->SetAudioProcessor(mAudioProcessor);
        mMutex.unlock();
        return mAudioEncoder->InitEncode();

    }
    LOG_D(DEBUG, "jni InitAudioEncoder failed!");
    mMutex.unlock();
    return -1;
}

/**
 * 推送音视频
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_StartPush(JNIEnv *env, jclass type, jstring url_) {
    mMutex.lock();
    if (mVideoInit && mAudioInit) {
        mStartStream = true;
        isClose = false;
        mVideoProcessor->StartProcessor();
        mAudioProcessor->StartProcessor();
        const char *url = env->GetStringUTFChars(url_, 0);
        mRtmpStreamer = RtmpStreamer::Get();
        //初始化推流器
        if (mRtmpStreamer->InitStreamer(url) != 0) {
            LOG_D(DEBUG, "jni initStreamer success!");
            mMutex.unlock();
            return -1;
        }
        mRtmpStreamer->SetVideoEncoder(mVideoEncoder);
        mRtmpStreamer->SetAudioEncoder(mAudioEncoder);
        if (mRtmpStreamer->StartPushStream() != 0) {
            LOG_D(DEBUG, "jni push stream failed!");
            mVideoProcessor->CloseProcessor();
            mAudioProcessor->CloseProcessor();
            mRtmpStreamer->ClosePushStream();
            mMutex.unlock();
            return -1;
        }
        LOG_D(DEBUG, "jni push stream success!");
        env->ReleaseStringUTFChars(url_, url);
    }
    mMutex.unlock();
    return 0;
}
/**
 * 接收APP层数据,H264编码
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_Encode2H264(JNIEnv *env, jclass type,
                                                             jbyteArray videoBuffer_, jint length) {
    if (mVideoInit && !isClose) {
        jbyte *videoSrc = env->GetByteArrayElements(videoBuffer_, 0);
        int len = mVideoProcessor->videoEncodeArgs->in_width *
                  mVideoProcessor->videoEncodeArgs->in_height * 3 / 2;
        uint8_t *videoDstData = (uint8_t *) malloc(len);
        uint8_t *dstData = (uint8_t *) malloc(len);

        //格式转换
//        VideoMachining::NV21TOI420(mVideoProcessor->videoEncodeArgs->in_width,
//                                   mVideoProcessor->videoEncodeArgs->in_height,
//                                   (const uint8_t *) videoSrc,
//                                   (uint8_t *) videoDstData);
        int width=mVideoProcessor->videoEncodeArgs->in_width;
        int height=mVideoProcessor->videoEncodeArgs->in_height;
        int y_stride = width;
        int u_stride = width >> 1;
        int v_stride = u_stride;
        size_t ySize = (size_t) (width * height);
        size_t uSize = (size_t) (width * height /4);
        VideoMachining::BGRA2I420((const uint8 *) videoSrc,
                                  4* width,
                                  (uint8 *) videoDstData, y_stride,//y
                                  (uint8 *) (videoDstData) + ySize, u_stride,//u
                                  (uint8 *) (videoDstData) + ySize + uSize, v_stride,//v
                                  width,
                                  height);
        //旋转
//        VideoMachining::RotateI420(mVideoProcessor->videoEncodeArgs->in_width,
//                                   mVideoProcessor->videoEncodeArgs->in_height,
//                                   (const uint8_t *) videoDstData,
//                                   (uint8_t *) dstData, 0);

        Data *videoData = new Data();
        videoData->mSize = len;
        videoData->mData = videoDstData;
        mVideoProcessor->PushVideoData(videoData);
        env->ReleaseByteArrayElements(videoBuffer_, videoSrc, 0);
    }
    return 0;

}

/**
 * 接收APP层数据,AAC编码
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_Encode2AAC(JNIEnv *env, jclass type,
                                                            jbyteArray audioBuffer_, jint length) {
    if (mAudioInit && !isClose) {
        jbyte *audioSrc = env->GetByteArrayElements(audioBuffer_, 0);
        uint8_t *audioDstData = (uint8_t *) malloc(length);
        memcpy(audioDstData, audioSrc, length);
        Data *audioData = new Data();
        audioData->mSize = length;
        audioData->mData = audioDstData;
        mAudioProcessor->PushAudioData(audioData);
        env->ReleaseByteArrayElements(audioBuffer_, audioSrc, 0);
    }
    return 0;
}


/**
 * 关闭底层采集、编码、推流
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_Close(JNIEnv *env, jclass type) {
    mMutex.lock();

    //关闭采集
    if (NULL != mAudioProcessor) {
        mAudioProcessor->CloseProcessor();
    }
    if (NULL != mVideoProcessor) {
        mVideoProcessor->CloseProcessor();
    }
    //关闭推流
    if (NULL != mRtmpStreamer) {
        mRtmpStreamer->ClosePushStream();
    }
    //关闭编码
    if (NULL != mVideoEncoder) {
        mVideoEncoder->CloseEncode();
    }
    if (NULL != mAudioEncoder) {
        mAudioEncoder->CloseEncode();
    }


    isClose = true;
    mStartStream = false;
    LOG_D(DEBUG, "jni close");
    mMutex.unlock();
    return 0;
}
/**
 * 底层资源回收
 */
JNIEXPORT jint JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_Release(JNIEnv *env, jclass type) {
    mMutex.lock();
    isRelease = true;
    mMutex.unlock();
    return 0;
}
/**
 * 设置相机id
 */
JNIEXPORT void JNICALL
Java_com_suishi_sslive_mode_stream_StreamManager_SetCameraID(JNIEnv *env, jclass type,
                                                             jint cameraID) {
    if (mAudioInit && !isClose && !isRelease) {
        if (cameraID == CameraID::FRONT) {
            mVideoProcessor->SetCameraID(CameraID::FRONT);
        } else if (cameraID == CameraID::BACK) {
            mVideoProcessor->SetCameraID(CameraID::BACK);
        }
    }
    return;
}



#ifdef __cplusplus
}
#endif