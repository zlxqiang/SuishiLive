//
// Created by zhzq  on 2018/3/21.
//

#ifndef SUISHILIVE_AUDIOCONFIG_H
#define SUISHILIVE_AUDIOCONFIG_H


#include "../../PrefixHeader.h"

/**
 * 音频配置文件
 */
struct AudioConfig{
    /**
     * 声道数
     */
    int channels;
    /**
     * 编码格式
     */
    AVSampleFormat avSampleFormat;
    /**
     * 布局方式
     */
    int ch_layout=AV_CH_LAYOUT_STEREO;
    /**
     * 采样率
     */
    int sampleRate;
    /**
     * bit率
     */
    int bitRate;
    /**
     * 帧率
     */
    int nb_samples;
};

#endif //SUISHILIVE_AUDIOCONFIG_H
