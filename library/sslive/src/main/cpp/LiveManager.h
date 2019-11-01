//
// Created by zhzq  on 2018/3/21.
//

#ifndef SUISHILIVE_LIVEMANAGER_H
#define SUISHILIVE_LIVEMANAGER_H

#include "PrefixHeader.h"
#include "av/audio/AudioProcessor.h"
#include "av/video/VideoProcessor.h"
#include "av/video/VideoEncoder.h"
#include "av/audio/AudioEncoder.h"
#include "av/stream/RtmpStreamer.h"

/**
 * 音视频管理器
 */
AudioProcessor *mAudioProcessor = NULL;
VideoProcessor *mVideoProcessor = NULL;

/**
 * 音视编码器
 */
VideoEncoder *mVideoEncoder = NULL;
AudioEncoder *mAudioEncoder = NULL;

/**
 * 推流器
 */
RtmpStreamer *mRtmpStreamer = NULL;

/**
 * 互斥锁
*/
mutex mMutex;

bool mAudioInit = false;

bool mVideoInit = false;

bool isClose = true;

bool isRelease = false;

bool mStartStream;


#endif //SUISHILIVE_LIVEMANAGER_H

