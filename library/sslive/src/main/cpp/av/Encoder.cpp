//
// Created by admin on 2018/3/22.
//

#include "Encoder.h"

bool Encoder::first = true;

Encoder::Encoder() {
    RegisterAVCodec();
}

Encoder::~Encoder() {

}

/**
 * 注册公用编码器并初始化网络
 */
void Encoder::RegisterAVCodec() {
    //一次初始化
    if (first) {
        first = false;
        av_register_all();
        avfilter_register_all();//
        RegisterAVNetwork();
        LOG_D(DEBUG, "MediaEncoder av_register_all success!");
        LOG_D(DEBUG, "MediaEncoder avformat_network_init success!");
    }
    return;
}

void Encoder::RegisterAVNetwork() {
    avformat_network_init();
    return;
}