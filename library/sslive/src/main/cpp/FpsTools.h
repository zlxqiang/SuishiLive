//
// Created by admin on 2019/10/31.
//

#ifndef SUISHILIVE_FPSTOOLS_H
#define SUISHILIVE_FPSTOOLS_H

#include "sys/time.h"

class FpsTools {

public:
    ~FpsTools();

    int fps();

    static int getTime();

    FpsTools();
};


#endif //SUISHILIVE_FPSTOOLS_H
