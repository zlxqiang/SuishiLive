//
// Created by 赵增强 on 2018/3/21.
// 音频处理器
//

#ifndef SUISHILIVE_AUDIOPROCESSOR_H
#define SUISHILIVE_AUDIOPROCESSOR_H


#include "AudioConfig.h"
#include "../AVBaseProcessor.h"
#include "../Data.h"

/**
 * 视频配置文件
 */
class AudioProcessor: public AVBaseProcessor{
private:
    AudioProcessor();

    AudioConfig *audioConfig = NULL;
public :
    ~AudioProcessor();

//    list<OriginData *> AudioCaptureDatalist;

    ThreadQueue<Data *> audioFrameQueue;

    static AudioProcessor *Get();

    bool CloseProcessor();

    bool StartProcessor();

    int Release();

    int PushAudioData(Data *data);

    Data *GetAudioData();
    /**
   * 设置编码参数
   */
    void SetAudioConfig(AudioConfig *audioConfig);

    AudioConfig *GetAudioConfig();

    /**
     * 获取音频采集状态
     */
    bool GetProcessorState();



};


#endif //SUISHILIVE_AUDIOPROCESSOR_H