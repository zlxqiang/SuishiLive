//
// Created by admin on 2018/3/22.
//

#ifndef SUISHILIVE_ENCODER_H
#define SUISHILIVE_ENCODER_H

#include "../PrefixHeader.h"
class Encoder{

protected:

    mutable std::mutex mut;

    Encoder();

    virtual ~Encoder();

    void RegisterAVCodec();

    void RegisterAVNetwork();

    virtual int StartEncode() = 0;

    virtual int InitEncode() = 0;

    virtual int CloseEncode() = 0;

    virtual int Release() = 0;

    static bool first;


};
#endif //SUISHILIVE_ENCODER_H
