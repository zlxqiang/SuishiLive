//
// Created by 赵增强 on 2018/3/21.
//

#include "AudioProcessor.h"


AudioProcessor::AudioProcessor() {

}

AudioProcessor::~AudioProcessor() {
    if (NULL != audioConfig) {
        delete audioConfig;
    }
}

AudioProcessor *AudioProcessor::Get() {
    static AudioProcessor audioCapture;
    return &audioCapture;
}

/**
 * 资源回收
 */
int AudioProcessor::Release() {
    LOG_D(DEBUG, "Release Audio Capture!");
    return 0;
}

/**
 *
 */
bool AudioProcessor::CloseProcessor() {
    std::lock_guard<std::mutex> lk(mut);
    if (!ExitCapture) {
        ExitCapture = true;
        LOG_D(DEBUG, "Close Audio Capture");
    }
    return ExitCapture;
}


bool AudioProcessor::StartProcessor() {
    std::lock_guard<std::mutex> lk(mut);
    ExitCapture = false;
    LOG_D(DEBUG, "Start Audio Capture");
    return !ExitCapture;
}


void AudioProcessor::SetAudioConfig(AudioConfig *audioEncodeArgs) {
    this->audioConfig = audioEncodeArgs;
}

AudioConfig *AudioProcessor::GetAudioConfig() {
    return this->audioConfig;
}

bool AudioProcessor::GetProcessorState() {
    return ExitCapture;
}

/**
 * 往除列添加音频原数据
 */
int AudioProcessor::PushAudioData(Data *originData) {
    if (ExitCapture) {
        return 0;
    }
    originData->mPts = av_gettime();
    LOG_D(DEBUG,"audio capture pts :%lld",originData->mPts);
    audioFrameQueue.push(originData);
    return 0;
}

Data *AudioProcessor::GetAudioData() {
    if (ExitCapture) {
        return NULL;
    }
    if (audioFrameQueue.empty()) {
        return NULL;
    } else {
        const shared_ptr<Data *> &ptr = audioFrameQueue.try_pop();
        return NULL == ptr ? NULL : *ptr.get();
    }
}
