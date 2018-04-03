//
// Created by zhzq  on 2018/3/21.
//

#include <pthread.h>
#include "AudioEncoder.h"

AudioEncoder *AudioEncoder::Get() {
    static AudioEncoder audioEncoder;
    return &audioEncoder;
}

AudioEncoder::AudioEncoder() {

}

AudioEncoder::~AudioEncoder() {
    LOG_D(DEBUG, "delete AudioEncoder");
}

void AudioEncoder::SetAudioProcessor(AudioProcessor *audioCapture) {
    this->audioProcessor = audioCapture;
}

AudioProcessor *AudioEncoder::GetAudioProcessor() {
    return this->audioProcessor;
}

int AudioEncoder::EncodeAAC(Data **originData) {

    int ret = 0;
    ret = avcodec_fill_audio_frame(outputFrame,
                                   audioCodecContext->channels,
                                   audioCodecContext->sample_fmt, (*originData)->mData,
                                   8192, 0);
    outputFrame->pts = (*originData)->mPts;
    ret = avcodec_send_frame(audioCodecContext, outputFrame);
    if (ret != 0) {
#ifdef SHOW_DEBUG_INFO
        LOG_D(DEBUG, "send frame failed!");
#endif
    }
    av_packet_unref(&audioPacket);

    ret = avcodec_receive_packet(audioCodecContext, &audioPacket);
    if (ret != 0) {
#ifdef SHOW_DEBUG_INFO
        LOG_D(DEBUG, "receive packet failed!");
#endif
    }
    (*originData)->freeData();
    (*originData)->mAVPacket = &audioPacket;

#ifdef SHOW_DEBUG_INFO
    LOG_D(DEBUG, "encode audio packet size:%d pts:%lld", (*originData)->avPacket->size,
          (*originData)->avPacket->pts);
    LOG_D(DEBUG, "Audio frame encode success!");
#endif
    //(*originData)->mAVPacket->size;
    return audioPacket.size;
}

void *AudioEncoder::EncodeTask(void *p) {
    AudioEncoder *audioEncoder = (AudioEncoder *) p;
    audioEncoder->isEncoding = true;
    AudioConfig *args = audioEncoder->audioProcessor->GetAudioConfig();

    FILE *PCM = fopen("/mnt/sdcard/iaudio.pcm", "wb+");
    LOG_D(DEBUG, "begin audio encode");
    audioEncoder->audioProcessor->audioFrameQueue.clear();
    int64_t beginTime = av_gettime();
    int64_t lastPts = 0;
    while (true) {
        //线程中断标记
        if (audioEncoder->audioProcessor->GetProcessorState()) {
            break;
        }
        //获取音频采集中的对列中的数据
        if (audioEncoder->audioProcessor->audioFrameQueue.empty()) {
            continue;
        }
        Data *srcData = audioEncoder->audioProcessor->GetAudioData();
        if (NULL == srcData) {
#ifdef SHOW_DEBUG_INFO
            LOG_D(DEBUG, "audio data NULL");
#endif
            continue;
        }
        if (NULL == srcData->mData) {
#ifdef SHOW_DEBUG_INFO
            LOG_D(DEBUG, "audio src data NULL");
#endif
            continue;
        }
        audioEncoder->outputFrame->pts = srcData->mPts - beginTime;
        if (lastPts == audioEncoder->outputFrame->pts) {
            audioEncoder->outputFrame->pts += 1300;
        }
        lastPts = audioEncoder->outputFrame->pts;

        int ret = 0;
        ret = avcodec_fill_audio_frame(audioEncoder->outputFrame,
                                       audioEncoder->audioCodecContext->channels,
                                       audioEncoder->audioCodecContext->sample_fmt, srcData->mData,
                                       4096, 0);
        if (ret < 0) {
            LOG_D(DEBUG, "fill frame failed!");
            continue;
        }

#ifdef PTS_INFO
        LOG_D(DEBUG, "audio pts:%lld", audioEncoder->outputFrame->pts);
#endif
        ret = avcodec_send_frame(audioEncoder->audioCodecContext, audioEncoder->outputFrame);
        if (ret != 0) {
#ifdef SHOW_DEBUG_INFO
            LOG_D(DEBUG, "send frame failed!");
#endif
            continue;
        }
        av_packet_unref(&audioEncoder->audioPacket);

        ret = avcodec_receive_packet(audioEncoder->audioCodecContext, &audioEncoder->audioPacket);
        if (ret != 0) {
#ifdef SHOW_DEBUG_INFO
            LOG_D(DEBUG, "receive packet failed!");
#endif
            continue;
        }
        srcData->freeData();
//        //TODO 编码完成(数据传递问题不知道有没有问题)
        srcData->mAVPacket = &audioEncoder->audioPacket;
#ifdef SHOW_DEBUG_INFO
        LOG_D(DEBUG, "encode audio packet size:%d pts:%lld", srcData->avPacket->size,
              srcData->avPacket->pts);
        LOG_D(DEBUG, "Audio frame encode success!");
#endif
        audioEncoder->aframeQueue.push(srcData);

#ifdef WRITE_PCM_TO_FILE
        fwrite(audioEncoder->outputFrame->data[0], 1, 4096, PCM);//V
        fflush(PCM);
#endif
    }
#ifdef WRITE_PCM_TO_FILE
    fclose(PCM);
#endif
    return 0;
}

int AudioEncoder::StartEncode() {
    std::lock_guard<std::mutex> lk(mut);
    pthread_t t1;
    pthread_create(&t1, NULL, AudioEncoder::EncodeTask, this);
    LOG_D(DEBUG, "Start Audio Encode task!")
    return 0;
}


int AudioEncoder::InitEncode() {
    std::lock_guard<std::mutex> lk(mut);
    avCodec = avcodec_find_encoder_by_name("libfdk_aac");


    int ret = 0;
    if (!avCodec) {
        LOG_D(DEBUG, "aac encoder not found!")
        return -1;
    }
    audioCodecContext = avcodec_alloc_context3(avCodec);
    if (!audioCodecContext) {
        LOG_D(DEBUG, "avcodec alloc context3 failed!");
        return -1;
    }
    audioCodecContext->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;
    audioCodecContext->sample_fmt = AV_SAMPLE_FMT_S16;
    audioCodecContext->sample_rate = audioProcessor->GetAudioConfig()->sampleRate;
    audioCodecContext->thread_count = 8;
    audioCodecContext->bit_rate = 50 * 1024 * 8;
    audioCodecContext->channels = audioProcessor->GetAudioConfig()->channels;
    audioCodecContext->frame_size = audioProcessor->GetAudioConfig()->nb_samples;
    audioCodecContext->time_base = {1, 1000000};//AUDIO VIDEO 两边时间基数要相同
    audioCodecContext->channel_layout = av_get_default_channel_layout(audioCodecContext->channels);


    outputFrame = av_frame_alloc();
    outputFrame->channels = audioCodecContext->channels;
    outputFrame->channel_layout = av_get_default_channel_layout(outputFrame->channels);
    outputFrame->format = audioCodecContext->sample_fmt;
    outputFrame->nb_samples = 1024;
    ret = av_frame_get_buffer(outputFrame, 0);
    if (ret != 0) {
        LOG_D(DEBUG, "av_frame_get_buffer failed!");
        return -1;
    }
    LOG_D(DEBUG, "av_frame_get_buffer success!");

    ret = avcodec_open2(audioCodecContext, NULL, NULL);
    if (ret != 0) {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "avcodec open failed! info:%s", buf);
        return -1;
    }
    LOG_D(DEBUG, "open audio codec success!");
    LOG_D(DEBUG, "Complete init Audio Encode!")
    return 0;
}

int AudioEncoder::Release() {
    LOG_D(DEBUG, "Release Audio Encode!");
    return 0;
}

int AudioEncoder::CloseEncode() {
    std::lock_guard<std::mutex> lk(mut);
    if (isEncoding) {
        isEncoding = false;
        avcodec_close(audioCodecContext);
        LOG_D(DEBUG, "Close Audio Encode!")
    }
    return 0;
}

bool AudioEncoder::GetEncodeState() {
    return isEncoding;
}
