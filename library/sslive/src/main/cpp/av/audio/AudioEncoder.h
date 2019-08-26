//
// Created by zhzq  on 2018/3/21.
//

#ifndef SUISHILIVE_AUDIOENCODE_H
#define SUISHILIVE_AUDIOENCODE_H


#include "../Encoder.h"
#include "AudioProcessor.h"

/**
 * 音频编码
 */
class AudioEncoder:public Encoder{

private:
    AudioProcessor *audioProcessor = NULL;

public:
    AVCodec *avCodec = NULL;
    AVStream *outStream = NULL;
    AVFrame *outputFrame = NULL;
    AVCodecContext *audioCodecContext = NULL;
    AVPacket audioPacket = {0};


    static AudioEncoder *Get();


    bool isEncoding = false;

    static void *EncodeTask(void *p);

    /**
      * 音频已编码数据队列
      */
    ThreadQueue<Data *> aframeQueue;


//    list<OriginData *> AudioDatalist;

    AudioEncoder();

    ~AudioEncoder();

    /**
     * 开启编码
     */
    int StartEncode();

    /**
     * 初始化视频编码器
     */
    int InitEncode();

    /**
    * 关闭编码器
    */
    int CloseEncode();


    int EncodeAAC(Data **data);

    /**
     * 资源回收
     */
    int Release();

    /**
     * 设置数据采集
     */
    void SetAudioProcessor(AudioProcessor *audioCapture);

    /**
     * 获取控制器
     * @return
     */
    AudioProcessor *GetAudioProcessor();

    /**
    * 获取编码器状态
    */
    bool GetEncodeState();
};

#endif //SUISHILIVE_AUDIOENCODE_H
