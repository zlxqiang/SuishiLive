//
// Created by zhzq  on 2018/3/21.
//

#ifndef SUISHILIVE_AVBASE_H
#define SUISHILIVE_AVBASE_H


#include "../PrefixHeader.h"


class AVBaseProcessor {

protected:

    mutable mutex mut;

    /**
     * 构造方法
     * @return
     */
    AVBaseProcessor();

    /**
     * 析构方法
     */
    virtual ~AVBaseProcessor();

    /**
     * 打开/关闭
     * @return
     */
    virtual bool CloseProcessor() = 0;

    /**
     * 释放
     * @return
     */
    virtual int Release() = 0;


    bool ExitCapture;

public:

};


#endif //SUISHILIVE_AVBASE_H