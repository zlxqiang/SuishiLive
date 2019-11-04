//
// Created by zhzq  on 2018/3/21.
//


#include "VideoProcessor.h"
#include "VideoMachining.h"


using namespace libyuv;

VideoProcessor::VideoProcessor() {

}

VideoProcessor::~VideoProcessor() {
    if (NULL != videoEncodeArgs) {
        delete videoEncodeArgs;
    }
}

VideoProcessor *VideoProcessor::Get() {
    static VideoProcessor videoCapture;
    return &videoCapture;
}

void VideoProcessor::SetCameraID(CameraID cameraID) {
    this->mCameraId = cameraID;
}

CameraID VideoProcessor::GetCameraID() {
    return this->mCameraId;
}


int VideoProcessor::Release() {
    LOG_D(DEBUG, "Release Video Capture!");
    return 0;
}

/**
 * 关闭采集数据输入
 */
bool VideoProcessor::CloseProcessor() {
    std::lock_guard<std::mutex> lk(mut);
    if (!ExitCapture) {
        ExitCapture = true;
    }
    LOG_D(DEBUG, "Close Video Capture");
    return ExitCapture;
}


bool VideoProcessor::StartProcessor() {
    std::lock_guard<std::mutex> lk(mut);
    ExitCapture = false;
    LOG_D(DEBUG, "Start Video Capture");
    return !ExitCapture;
}

/**
 * 往队列中添加视频原数据
 */
int VideoProcessor::PushVideoData(Data *originData) {
    if (ExitCapture) {
        return 0;
    }
    originData->mPts = av_gettime();
    LOG_D(DEBUG,"video capture pts :%lld",originData->mPts);
    videoFrameQueue.push(originData);
    return originData->mSize;
}

void VideoProcessor::SetVideoConfig(VideoConfig *videoEncodeArgs) {
    this->videoEncodeArgs = videoEncodeArgs;
}

VideoConfig *VideoProcessor::GetVideoConfig() {
    return this->videoEncodeArgs;
}

/**
 *从队列中获取视频原数据
 */
Data *VideoProcessor::GetVideoData() {
    if (ExitCapture) {
        return NULL;
    }
    if (videoFrameQueue.empty()) {
        return NULL;
    } else {
        const shared_ptr<Data *> &ptr = videoFrameQueue.try_pop();
        return NULL == ptr ? NULL : *ptr.get();
    }
}


bool VideoProcessor::GetProcessorState() {
    return ExitCapture;
}

