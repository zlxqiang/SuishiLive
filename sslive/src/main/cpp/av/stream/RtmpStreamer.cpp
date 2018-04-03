//
// Created by admin on 2018/3/22.
//

#include "RtmpStreamer.h"

RtmpStreamer::RtmpStreamer() {

}

RtmpStreamer::~RtmpStreamer() {
    if (NULL != audioStream) {
        av_free(audioStream);
        audioStream = NULL;
    }
    if (NULL != videoStream) {
        av_free(videoStream);
        videoStream = NULL;
    }
    if (NULL != audioCodecContext) {
        avcodec_free_context(&audioCodecContext);
        audioCodecContext = NULL;
    }
    if (NULL != videoCodecContext) {
        avcodec_free_context(&videoCodecContext);
        audioCodecContext = NULL;
    }
    if (NULL != iAvFormatContext) {
        avformat_free_context(iAvFormatContext);
        iAvFormatContext = NULL;
    }
}

RtmpStreamer *RtmpStreamer::Get() {
    static RtmpStreamer rtmpStreamer;
    return &rtmpStreamer;
}

int RtmpStreamer::InitStreamer(const char *url) {
    std::lock_guard<std::mutex> lk(mut);
    this->outputUrl = url;
    int ret = 0;
    ret = avformat_alloc_output_context2(&iAvFormatContext, NULL, "flv", outputUrl);
    if (ret < 0) {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "avformat alloc output context2 failed: %s", buf);

        return -1;
    }
    LOG_D(DEBUG, "Rtmp InitStreamer Success!");
    return 0;
}

int RtmpStreamer::SetAudioEncoder(AudioEncoder *audioEncoder) {
    this->audioEncoder = audioEncoder;
    return this->audioEncoder != NULL ? 0 : -1;
}

int RtmpStreamer::SetVideoEncoder(VideoEncoder *videoEncoder) {
    this->videoEncoder = videoEncoder;
    return this->videoEncoder != NULL ? 0 : -1;
}

int RtmpStreamer::AddStream(AVCodecContext *avCodecContext) {
    std::lock_guard<std::mutex> lk(mut);
    AVStream *pStream = avformat_new_stream(iAvFormatContext, avCodecContext->codec);
    if (!pStream) {
        LOG_D(DEBUG, "avformat_new_stream failed!");
        return -1;
    }
    LOG_D(DEBUG, "avformat new stream success!");
    int ret = 0;
    ret = avcodec_parameters_from_context(pStream->codecpar, avCodecContext);
    if (ret < 0) {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "avcodec_parameters_from_context failed :%s", buf);
        return -1;
    }
    LOG_D(DEBUG, "avcodec_parameters_from_context success!");
    pStream->codecpar->codec_tag = 0;
    if (avCodecContext->codec_type == AVMEDIA_TYPE_VIDEO) {
        LOG_D(DEBUG, "Add video stream success!");
        videoStream = pStream;
        videoCodecContext = avCodecContext;
    } else if (avCodecContext->codec_type == AVMEDIA_TYPE_AUDIO) {
        LOG_D(DEBUG, "Add audio stream success!");
        audioStream = pStream;
        audioCodecContext = avCodecContext;
    }
    return pStream->index;
}


int RtmpStreamer::SendAudioFrame(Data *originData, int streamIndex) {
    std::lock_guard<std::mutex> lk(mut1);
    AVRational stime;
    AVRational dtime;

    AVPacket *packet = originData->mAVPacket;
    packet->stream_index = streamIndex;
#ifdef SHOW_DEBUG_INFO
    LOG_D(DEBUG, "packet index:%d    index:%d", packet->stream_index, streamIndex);
#endif
    stime = audioCodecContext->time_base;
    dtime = audioStream->time_base;
    //push
    packet->pts = av_rescale_q(packet->pts, stime, dtime);
    packet->dts = av_rescale_q(packet->dts, stime, dtime);
    packet->duration = av_rescale_q(packet->duration, stime, dtime);
#ifdef SHOW_DEBUG_INFO
    LOG_D(DEBUG, "writer frame stream Index:%d   size:%d",
          packet->stream_index,
          packet->size);
#endif
    int ret = av_interleaved_write_frame(iAvFormatContext, packet);
    if (ret == 0) {
        LOG_D(DEBUG, "write ++++++++++++++audio frame sucess!");
    } else {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "writer******************* audio frame failed! :%s", buf);
    }
    delete originData;
    return 0;

}

int RtmpStreamer::SendVideoFrame(Data *originData, int streamIndex) {
    std::lock_guard<std::mutex> lk(mut1);
    AVRational stime;
    AVRational dtime;

    AVPacket *packet = originData->mAVPacket;
    packet->stream_index = streamIndex;
#ifdef SHOW_DEBUG_INFO
    LOG_D(DEBUG, "video packet index:%d    index:%d", packet->stream_index, streamIndex);
#endif
    stime = videoCodecContext->time_base;
    dtime = videoStream->time_base;
    packet->pts = originData->mPts;
    packet->dts = packet->pts;
    packet->pts = av_rescale_q_rnd(packet->pts, stime, dtime,
                                   (AVRounding) (AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
    packet->dts = av_rescale_q_rnd(packet->dts, stime, dtime,
                                   (AVRounding) (AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
    packet->duration = av_rescale_q(packet->duration, stime, dtime);
#ifdef SHOW_DEBUG_INFO
    LOG_D(DEBUG, "writer frame stream Index:%d   size:%d",
          packet->stream_index,
          packet->size);

#endif
    int ret = av_interleaved_write_frame(iAvFormatContext, packet);
    if (ret == 0) {
        LOG_D(DEBUG, "write ------------------video frame success!");
    } else {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "writer*******************video frame failed! :%s", buf);
    }
    delete originData;
    return 0;

}


/**
 * 音频推流任务
 */
void *RtmpStreamer::PushAudioStreamTask(void *pObj) {
    RtmpStreamer *rtmpStreamer = (RtmpStreamer *) pObj;
    rtmpStreamer->isPushStream = true;

    if (NULL == rtmpStreamer->audioEncoder) {
        return 0;
    }
    AudioProcessor *pAudioCapture = rtmpStreamer->audioEncoder->GetAudioProcessor();

    if (NULL == pAudioCapture) {
        return 0;
    }
    int64_t beginTime = av_gettime();
    while (true) {

        if (!rtmpStreamer->isPushStream ||
            pAudioCapture->GetProcessorState()) {
            break;
        }
        Data *pAudioData = pAudioCapture->GetAudioData();
        if (pAudioData != NULL && pAudioData->mData) {
            pAudioData->mPts = pAudioData->mPts - beginTime;
            LOG_D(DEBUG, "before audio encode pts:%lld", pAudioData->mPts);
            rtmpStreamer->audioEncoder->EncodeAAC(&pAudioData);
            LOG_D(DEBUG, "after audio encode pts:%lld", pAudioData->mAVPacket->pts);
        }
        if (pAudioData != NULL && pAudioData->mAVPacket->size > 0) {
            rtmpStreamer->SendFrame(pAudioData, rtmpStreamer->audioStreamIndex);
        }
    }

    return 0;
}

/**
* 视频推流任务
*/
void *RtmpStreamer::PushVideoStreamTask(void *pObj) {
    RtmpStreamer *rtmpStreamer = (RtmpStreamer *) pObj;
    rtmpStreamer->isPushStream = true;

    if (NULL == rtmpStreamer->videoEncoder) {
        return 0;
    }
    VideoProcessor *pVideoCapture = rtmpStreamer->videoEncoder->GetVideoProcessor();

    if (NULL == pVideoCapture) {
        return 0;
    }
    int64_t beginTime = av_gettime();
    while (true) {

        if (!rtmpStreamer->isPushStream ||
            pVideoCapture->GetProcessorState()) {
            break;
        }

        Data *pVideoData = pVideoCapture->GetVideoData();
        //h264 encode
        if (pVideoData != NULL && pVideoData->mData) {
            pVideoData->mPts = pVideoData->mPts - beginTime;
            LOG_D(DEBUG, "before video encode pts:%lld", pVideoData->mPts);
            rtmpStreamer->videoEncoder->EncodeH264(&pVideoData);
            LOG_D(DEBUG, "after video encode pts:%lld", pVideoData->mAVPacket->pts);
        }

        if (pVideoData != NULL && pVideoData->mAVPacket->size > 0) {
            rtmpStreamer->SendFrame(pVideoData, rtmpStreamer->videoStreamIndex);
        }
    }
    return 0;
}

/**
* 音视频同时推流任务
*/
void *RtmpStreamer::PushStreamTask(void *pObj) {
    RtmpStreamer *rtmpStreamer = (RtmpStreamer *) pObj;
    rtmpStreamer->isPushStream = true;

    if (NULL == rtmpStreamer->videoEncoder || NULL == rtmpStreamer->audioEncoder) {
        return 0;
    }
    VideoProcessor *pVideoCapture = rtmpStreamer->videoEncoder->GetVideoProcessor();
    AudioProcessor *pAudioCapture = rtmpStreamer->audioEncoder->GetAudioProcessor();

    if (NULL == pVideoCapture || NULL == pAudioCapture) {
        return 0;
    }
    int64_t beginTime = av_gettime();
    if (NULL != pVideoCapture) {
        pVideoCapture->videoFrameQueue.clear();
    }
    if (NULL != pAudioCapture) {
        pAudioCapture->audioFrameQueue.clear();
    }
    int64_t lastAudioPts = 0;
    while (true) {

        if (!rtmpStreamer->isPushStream ||
            pVideoCapture->GetProcessorState() ||
            pAudioCapture->GetProcessorState()) {
            break;
        }
        Data *pVideoData = pVideoCapture->GetVideoData();
        Data *pAudioData = pAudioCapture->GetAudioData();

        if (pAudioData != NULL && pAudioData->mData) {
            pAudioData->mPts = pAudioData->mPts - beginTime;
            LOG_D(DEBUG, "before audio encode pts:%lld", pAudioData->mPts);
            rtmpStreamer->audioEncoder->EncodeAAC(&pAudioData);
            LOG_D(DEBUG, "after audio encode pts:%lld", pAudioData->mAVPacket->pts);
        }


        if (pAudioData != NULL && pAudioData->mAVPacket->size > 0) {
            rtmpStreamer->SendFrame(pAudioData, rtmpStreamer->audioStreamIndex);
        }

        //h264 encode
        if (pVideoData != NULL && pVideoData->mData) {
            pVideoData->mPts = pVideoData->mPts - beginTime;
            LOG_D(DEBUG, "before video encode pts:%lld", pVideoData->mPts);
            rtmpStreamer->videoEncoder->EncodeH264(&pVideoData);
            LOG_D(DEBUG, "after video encode pts:%lld", pVideoData->mAVPacket->pts);
        }

        if (pVideoData != NULL && pVideoData->mAVPacket->size > 0) {
            rtmpStreamer->SendFrame(pVideoData, rtmpStreamer->videoStreamIndex);
        }
    }
    return 0;
}

int RtmpStreamer::StartPushStream() {
    videoStreamIndex = AddStream(videoEncoder->videoCodecContext);
    audioStreamIndex = AddStream(audioEncoder->audioCodecContext);
    //线程3
    pthread_create(&t3, NULL, RtmpStreamer::WriteHead, this);
    pthread_join(t3, NULL);

    VideoProcessor *pVideoCapture = videoEncoder->GetVideoProcessor();
    AudioProcessor *pAudioCapture = audioEncoder->GetAudioProcessor();
    pVideoCapture->videoFrameQueue.clear();
    pAudioCapture->audioFrameQueue.clear();

    if(writeHeadFinish) {
        //线程1，2
        pthread_create(&t1, NULL, RtmpStreamer::PushAudioStreamTask, this);
        pthread_create(&t2, NULL, RtmpStreamer::PushVideoStreamTask, this);
    }else{
        return -1;
    }
    return 0;
}

int RtmpStreamer::ClosePushStream() {
    if (isPushStream) {
        isPushStream = false;
        pthread_join(t1, NULL);
        pthread_join(t2, NULL);
        if (NULL != iAvFormatContext) {
            av_write_trailer(iAvFormatContext);
            avio_close(iAvFormatContext->pb);
        }
    }
    writeHeadFinish=false;
    return 0;
}

/**
 * notice:AVStream创建完成开始写头信息
 */
void *RtmpStreamer::WriteHead(void *pObj) {
    RtmpStreamer *rtmpStreamer = (RtmpStreamer *) pObj;
    int ret = 0;
    ret = avio_open(&rtmpStreamer->iAvFormatContext->pb, rtmpStreamer->outputUrl, AVIO_FLAG_WRITE);
    if (ret < 0) {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "avio open failed: %s", buf);
        return 0;
    }
    LOG_D(DEBUG, "avio open success!");
    ret = avformat_write_header(rtmpStreamer->iAvFormatContext, NULL);
    if (ret != 0) {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "avformat write header failed!: %s", buf);
        return 0;
    }
    rtmpStreamer->writeHeadFinish=true;
    return 0;
}

int RtmpStreamer::SendFrame(Data *pData, int streamIndex) {
    std::lock_guard<std::mutex> lk(mut1);
    AVRational stime;
    AVRational dtime;
    AVPacket *packet = pData->mAVPacket;
    packet->stream_index = streamIndex;
    LOG_D(DEBUG, "write packet index:%d    index:%d   pts:%lld", packet->stream_index, streamIndex,
          packet->pts);
    //判断是音频还是视频
    if (packet->stream_index == videoStreamIndex) {
        stime = videoCodecContext->time_base;
        dtime = videoStream->time_base;
    }
    else if (packet->stream_index == audioStreamIndex) {
        stime = audioCodecContext->time_base;
        dtime = audioStream->time_base;
    }
    else {
        LOG_D(DEBUG, "unknow stream index");
        return -1;
    }
    packet->pts = av_rescale_q(packet->pts, stime, dtime);
    packet->dts = av_rescale_q(packet->dts, stime, dtime);
    packet->duration = av_rescale_q(packet->duration, stime, dtime);
    int ret = av_interleaved_write_frame(iAvFormatContext, packet);

    if (ret == 0) {
        if (streamIndex == audioStreamIndex) {
            LOG_D(DEBUG, "---------->write audio frame success------->!");
        } else if (streamIndex == videoStreamIndex) {
            LOG_D(DEBUG, "---------->write video frame success------->!");
        }
    } else {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf));
        LOG_D(DEBUG, "stream index %d writer frame failed! :%s", streamIndex, buf);
    }
    return 0;
}





