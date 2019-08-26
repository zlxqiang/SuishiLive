//
// Created by developer on 8/17/17.
//


#ifndef NATIVEAPP_BASE_INCLUDE_H
#define NATIVEAPP_BASE_INCLUDE_H


#include <jni.h>
#include <iostream>
#include <string>
#include "debug.h"
#include <android/log.h>
#include "av/ThreadQueue.cpp"
#ifdef __cplusplus
extern "C" {
#endif
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <malloc.h>
#include "libavfilter/avfiltergraph.h"
#include "libavfilter/buffersink.h"
#include "libavfilter/buffersrc.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/opt.h"
#include "libavutil/avutil.h"
#include "libavutil/frame.h"
#include "libavutil/time.h"
#include "libavutil/channel_layout.h"
#include "libavfilter/avfilter.h"
#include "libswscale/swscale.h"
#include "libavutil/pixfmt.h"
#include "libavutil/imgutils.h"
#include "libyuv.h"
#include "libswresample/swresample.h"

#ifdef __cplusplus
}
#endif



#endif //NATIVEAPP_BASE_INCLUDE_H
