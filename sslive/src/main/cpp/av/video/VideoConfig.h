//
// Created by 赵增强 on 2018/3/21.
//

#ifndef SUISHILIVE_VIDEOCONFIG_H
#define SUISHILIVE_VIDEOCONFIG_H

#include "../../PrefixHeader.h"

using namespace std;
/**
 * 视频配置文件
 */
struct VideoConfig{
    /**
     * 图片宽度
     */
    int in_width;
    /**
     * 图片高度
     */
    int in_height;
    /**
     * 输出宽度
     */
    int out_width;
    /**
     * 输出高度
     */
    int out_height;
    /**
     * 帧率
     */
    int fps;

    /**
     * 是否滤镜
     */
    int mirror;
};
#endif //SUISHILIVE_VIDEOCONFIG_H
