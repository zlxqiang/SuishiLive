//
// Created by admin on 2018/3/21.
//

#ifndef SUISHILIVE_VIDEOENCODE_H
#define SUISHILIVE_VIDEOENCODE_H

#include "VideoProcessor.h"
#include "../Encoder.h"
#include "../Data.h"
/**
 * 视频编码
 */
class VideoEncoder : public Encoder {
private:
    VideoProcessor *videoProcessor = NULL;

public:


    bool isEncoding = false;

    static VideoEncoder *Get();

    static void *EncodeTask(void *obj);

    /**
     * 已编码数据队列
     */
    ThreadQueue<Data *> videoFrameQueue;

    VideoEncoder();

    ~VideoEncoder();

    /**
     * 开启编码
     */
    int StartEncode();

    /**
     * 初始化H264视频编码器
     */
    int InitEncode();

    /**
     * 关闭编码器
     */
    int CloseEncode();


    int EncodeH264(Data **originData);

    /**
     * 资源回收
     */
    int Release();

    /**
     * 设置数据采集
     */
    void SetVideoProcessor(VideoProcessor *videoCapture);


    VideoProcessor *GetVideoProcessor();


    /**
     * 获取编码器状态
     */
    bool GetEncodeState();


    /**
     * 前置镜像
     */
    void YUVProcessMirror();

    AVCodec *avCodec = NULL;

    AVStream *outStream = NULL;

    AVFrame *vOutFrame = NULL;

    AVCodecContext *videoCodecContext = NULL;

    AVFrame *outputYUVFrame = NULL;

    AVFrame *inputYUVFrame = NULL;

    AVPacket videoPacket = {0};

    AVFilter *buffersrc = nullptr;

    AVFilter *buffersink = nullptr;

    AVFilterInOut *outputs= nullptr;
    AVFilterInOut *inputs= nullptr;
    AVFilterGraph *filter_graph= nullptr;

    AVFilterContext *buffersink_ctx  = nullptr;;
    AVFilterContext *buffersrc_ctx  = nullptr;;
};

#endif //SUISHILIVE_VIDEOENCODE_H
