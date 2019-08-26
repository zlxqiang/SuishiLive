//
// Created by zhzq  on 2018/3/22.
//

#ifndef SUISHILIVE_RTMPSTREAMER_H
#define SUISHILIVE_RTMPSTREAMER_H

#include "Streamer.h"
/**
 * rtmp 推流
 */
class RtmpStreamer: public Streamer{

private:
    RtmpStreamer();


public:
    ~RtmpStreamer();

    bool writeHeadFinish=false;

    pthread_t t1;
    pthread_t t2;
    pthread_t t3;

    static RtmpStreamer *Get();

    static void *PushAudioStreamTask(void *pObj);

    static void *PushVideoStreamTask(void *pObj);

    static void *PushStreamTask(void *pObj);

    static void *WriteHead(void *pObj);

    int InitStreamer(const char *url);

    /**
     * 设置音频编码器
     */
    int SetAudioEncoder(AudioEncoder *audioEncoder);

    /**
     * 设置视频编码器
     */
    int SetVideoEncoder(VideoEncoder *videoEncoder);

    /**
     * 添加音视频流
     */
    int AddStream(AVCodecContext *avCodecContext);

    /**
     * 写入头
     */


    /**
     * 开启推流
     */
    int StartPushStream();

    /**
    * 关闭推流
    */
    int ClosePushStream();

    int SendAudioFrame(Data *originData, int streamIndex);

    int SendVideoFrame(Data *originData, int streamIndex);

    int SendFrame(Data *pData, int i);
};

#endif //SUISHILIVE_RTMPSTREAMER_H
