//
// Created by zhzq  on 2018/3/21.
// 视频处理器
//

#ifndef SUISHILIVE_VIDEOPROCESSOR_H
#define SUISHILIVE_VIDEOPROCESSOR_H

#include "VideoConfig.h"
#include "../AVBaseProcessor.h"
#include "../Data.h"

/**
 *
 */
class VideoProcessor:public AVBaseProcessor{
private:
    VideoProcessor();

public:
    ~VideoProcessor();

    static VideoProcessor *Get();

    bool CloseProcessor();

    bool StartProcessor();

    int Release();

    int PushVideoData(Data *originData);

    void SetCameraID(CameraID cameraID);

    VideoConfig *videoEncodeArgs = NULL;

    CameraID GetCameraID();

    Data *GetVideoData();


    CameraID mCameraId;


    /**
   * 设置编码参数
   */
    void SetVideoConfig(VideoConfig *videoEncodeArgs);

    VideoConfig *GetVideoConfig();


//    list<OriginData *> VideoCaptureDatalist;

    ThreadQueue<Data *> videoFrameQueue;
    /**
     * 获取视频采集状态
     */
    bool GetProcessorState();

    /**
   * nv21源数据处理(旋转)
   */
    uint8_t *NV21ProcessYUV420P(int in_width, int in_height, int out_width, int out_heigth,
                                uint8_t *src, uint8_t *dst, CameraID cameraID, int needMirror);

    bool enableWaterMark;
};
#endif //SUISHILIVE_VIDEOPROCESSOR_H
