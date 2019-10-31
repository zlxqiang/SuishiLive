//
// Created by admin on 2019/10/31.
//

#include "FpsTools.h"
#include "debug.h"

FpsTools::FpsTools() {

}

/**
 * 计算fps
 * @return
 */
int FpsTools::fps() {
    static int fps = 0;
    static int lastTime = getTime();
    static int frameCount = 0;

    ++frameCount;

    int curTime = getTime();
    LOG_D(DEBUG, "curTime:%d", curTime);
    if (curTime - lastTime > 1000) // 取固定时间间隔为1秒
    {
        fps = frameCount;
        frameCount = 0;
        lastTime = curTime;
    }
    return fps;
}

int FpsTools::getTime() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000; // 徽秒
}


FpsTools::~FpsTools() {

}

