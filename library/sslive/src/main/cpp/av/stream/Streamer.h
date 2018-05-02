//
// Created by zhzq  on 2018/3/22.
//

#ifndef SUISHILIVE_STEAM_H
#define SUISHILIVE_STEAM_H

#include "../../PrefixHeader.h"
#include "../audio/AudioEncoder.h"
#include "../video/VideoEncoder.h"

/**
 * 推流
 */
class Streamer{
protected:
    Streamer();


    mutable std::mutex mut;
    mutable std::mutex mut1;
    mutable std::mutex mut2;

    AudioEncoder *audioEncoder = NULL;
    VideoEncoder *videoEncoder = NULL;

    AVStream *audioStream = NULL;
    AVStream *videoStream = NULL;

    AVCodecContext *audioCodecContext = NULL;
    AVCodecContext *videoCodecContext = NULL;
    AVFormatContext *iAvFormatContext = NULL;


    int videoStreamIndex = -1;
    int audioStreamIndex = -1;

    const char *outputUrl = NULL;

    bool isPushStream=false;

    /**
     * 初始化推流器
     */
    virtual int InitStreamer(const char *url) = 0;

    /**
     * 设置推流音频编码器,用于获取已编码数据
     */
    virtual int SetAudioEncoder(AudioEncoder *audioEncoder) = 0;

    /**
     * 设置推流视频编码器,用于获取已编码数据
     */
    virtual int SetVideoEncoder(VideoEncoder *videoEncoder) = 0;

    /**
     * 添加音视频流
     */
    virtual int AddStream(AVCodecContext *avCodecContext) = 0;

    /**
     * 开始推流
     */
    virtual int StartPushStream() = 0;

    /**
     * 关闭推流
     */
    virtual int ClosePushStream() = 0;

    ~Streamer();
};
#endif //SUISHILIVE_STEAM_H
