//
// Created by admin on 2018/3/22.
//

#ifndef SUISHILIVE_DATA_H
#define SUISHILIVE_DATA_H


#include "../PrefixHeader.h"

/**
 * 音视频数据
 */
class Data {
public:
    /**
     *
     * @return
     */
    Data();

    /**
     *
     */
    ~Data();

    /**
     * 显示时间
     */
    int64_t mPts = 0;

    /**
     * 帧
     */
    AVFrame *mFrame = NULL;

    /**
     * AVPacket
     */
    AVPacket *mAVPacket = NULL;

    /**
     * 原始数据
     */
    uint8_t *mData = NULL;

    /**
     * 数据大小
     */
    int mSize = NULL;

    /**
     * 释放数据
     */
    void freeData();

};

#endif //SUISHILIVE_DATA_H
