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


uint8_t *VideoProcessor::NV21ProcessYUV420P(int w, int h, int out_width, int out_height, uint8_t *src,
                                          uint8_t *dst,
                                          CameraID cameraID,
                                          int needMirror) {
//    //I420
//    uint8_t *dstI420 = (uint8_t *) malloc(w * h * 3 / 2);
////    I420->mirror
//    uint8_t *mirror = (uint8_t *) malloc(w * h * 3 / 2);
//    //I420/I420mirror->rotate
//    uint8_t *rotate = (uint8_t *) malloc(w * h * 3 / 2);
//    //I420/I420mirror->rotate->scale
//    uint8_t *scale = (uint8_t *) malloc(w * h * 3 / 2);
//
//    uint8_t *dst = NULL;
//
//    //libyuv NV21 convert I420
//    NV21ToI420((const uint8_t *) src, w, (uint8_t *) src + (w * h), w, dstI420, w,
//               dstI420 + (w * h),
//               w / 2, dstI420 + (w * h * 5 / 4), w / 2, w, h);
//
//
//
//    uint8_t *src_y = dstI420;
//    uint8_t *src_u = dstI420 + w * h;
//    uint8_t *src_v = dstI420 + (w * h * 5 / 4);
////
//    uint8_t *dst_y = mirror;
//    uint8_t *dst_u = mirror + w * h;
//    uint8_t *dst_v = mirror + (w * h * 5 / 4);
//    //镜像与旋转
//    RotationMode rotationMode;
//    if (cameraID == FRONT) {
//        //libyuv 前置镜像
//        LOG_D(DEBUG, "camera face front");
//        LOG_D(DEBUG, "need mirror %d", needMirror);
//        rotationMode = kRotate90;
//        if (needMirror == 1) {
//            I420Mirror(src_y, w, src_u, w / 2, src_v, w / 2, dst_y,
//                       w, dst_u, w / 2, dst_v, w / 2, w, h);
//            //前置  注意:如果旋转90/270,旋转后,Y U V 行数据量,应该使用height计算
//            I420Rotate(mirror, w, mirror + (w * h), w / 2, mirror + (w * h * 5 / 4), w / 2, rotate,
//                       h, rotate + (w * h), h / 2, rotate + (w * h * 5 / 4), h / 2, w, h,
//                       rotationMode);
//        } else {
//            I420Rotate(src_y, w, src_u, w / 2, src_v, w / 2, rotate, h,
//                       rotate + (w * h), h / 2, rotate + (w * h * 5 / 4), h / 2, w, h,
//                       rotationMode);
//        }
//    } else if (cameraID == BACK) {
//        //后置  旋转90
//        LOG_D(DEBUG, "camera face back");
//        rotationMode = kRotate90;
//        I420Rotate(src_y, w, src_u, w / 2, src_v, w / 2, rotate, h,
//                   rotate + (w * h), h / 2, rotate + (w * h * 5 / 4), h / 2, w, h,
//                   rotationMode);
//    }
//    //缩放
//    if (w != out_width || h != out_height) {
//        LOG_D(DEBUG, "scale");
//        I420Scale(rotate, h, rotate + (w * h), h / 2, rotate + (w * h * 5 / 4), h / 2, w, h, scale,
//                  out_width, scale + (out_width * out_height), out_width / 2,
//                  scale + (out_width * out_height * 5 / 4), out_width / 2, out_width, out_height,
//                  kFilterNone);
//        dst = (uint8_t *) malloc(out_width * out_height * 3 / 2);
//        memcpy(dst, scale, out_width * out_height * 3 / 2);
//    } else {
//        dst = (uint8_t *) malloc(w * h * 3 / 2);
//        memcpy(dst, rotate, w * h * 3 / 2);
//    }
//    delete dstI420;
//    delete mirror;
//    delete rotate;
//    delete scale;



    //I420
    uint8_t *dstI420 = (uint8_t *) malloc(w * h * 3 / 2);
    //I420->scale
    uint8_t *scale = (uint8_t *) malloc(out_width * out_height * 3 / 2);

    uint8_t *src_y = NULL;
    uint8_t *src_u = NULL;
    uint8_t *src_v = NULL;

    //NV21 to I420
    NV21ToI420((const uint8_t *) src, w, (uint8_t *) src + (w * h), w, dstI420, w,
               dstI420 + (w * h),
               w / 2, dstI420 + (w * h * 5 / 4), w / 2, w, h);

    //I420 scale
//    if (w != out_width || h != out_height) {
//        LOG_D(DEBUG, "scale");
//        I420Scale(dstI420, w, dstI420 + (w * h), w / 2, dstI420 + (w * h * 5 / 4), w / 2, w, h,
//                  scale,
//                  out_width, scale + (out_width * out_height), out_width / 2,
//                  scale + (out_width * out_height * 5 / 4), out_width / 2, out_width, out_height,
//                  kFilterNone);
//
//        w = out_width;
//        h = out_height;
//
//        src_y = scale;
//        src_u = scale + w * h;
//        src_v = scale + (w * h * 5 / 4);
//
//    } else {
    src_y = dstI420;
    src_u = dstI420 + w * h;
    src_v = dstI420 + (w * h * 5 / 4);
//    }


    //I420->mirror
    uint8_t *mirror = (uint8_t *) malloc(w * h * 3 / 2);
    //I420/I420mirror->rotate
    uint8_t *rotate = (uint8_t *) malloc(w * h * 3 / 2);

    uint8_t *dst_y = mirror;
    uint8_t *dst_u = mirror + w * h;
    uint8_t *dst_v = mirror + (w * h * 5 / 4);
    //镜像与旋转
    RotationMode rotationMode;
    if (cameraID == FRONT) {
        //libyuv 前置镜像
        LOG_D(DEBUG, "camera face front");
        LOG_D(DEBUG, "need mirror %d", needMirror);
        rotationMode = kRotate90;
        if (needMirror == 1) {
            I420Mirror(src_y, w, src_u, w / 2, src_v, w / 2, dst_y,
                       w, dst_u, w / 2, dst_v, w / 2, w, h);
            //前置  注意:如果旋转90/270,旋转后,Y U V 行数据量,应该使用height计算
            I420Rotate(mirror, w, mirror + (w * h), w / 2, mirror + (w * h * 5 / 4), w / 2, rotate,
                       h, rotate + (w * h), h / 2, rotate + (w * h * 5 / 4), h / 2, w, h,
                       rotationMode);
        } else {
            I420Rotate(src_y, w, src_u, w / 2, src_v, w / 2, rotate, h,
                       rotate + (w * h), h / 2, rotate + (w * h * 5 / 4), h / 2, w, h,
                       rotationMode);
        }
    } else if (cameraID == BACK) {
        //后置  旋转90
        LOG_D(DEBUG, "camera face back");
        rotationMode = kRotate90;
        I420Rotate(src_y, w, src_u, w / 2, src_v, w / 2, rotate, h,
                   rotate + (w * h), h / 2, rotate + (w * h * 5 / 4), h / 2, w, h,
                   rotationMode);
    }
    memcpy(dst, rotate, w * h * 3 / 2);
    if (dstI420) {
        free(dstI420);
    }
    if (scale) {
        free(scale);
    }
    if (mirror) {
        free(mirror);
    }
    if (rotate) {
        free(rotate);
    }
    return dst;
}
