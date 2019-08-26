//
//

#ifndef NATIVEAPP_VIDEOPROCESS_H
#define NATIVEAPP_VIDEOPROCESS_H


#import "../PrefixHeader.h"

using namespace libyuv;

/**
 * YUV色彩空间处理
 */

class VideoMachining {
public:

    /**
     * android camera data NV21 convert to I420
     */
    static int NV21TOI420(int in_width, int in_hegith,
                          const uint8_t *srcData,
                          uint8_t *dstData);

    /**
     * mirror I420
     */
    static int MirrorI420(int in_width, int in_hegith,
                          const uint8_t *srcData,
                          uint8_t *dstData);

    /**
     * rotate I420 by RotationMode
     */
    static int RotateI420(int in_width, int in_hegith,
                          const uint8_t *srcData,
                          uint8_t *dstData, int rotationMode);

    static int BGRA2I420(const uint8* src_argb, int src_stride_argb,
                         uint8* dst_y, int dst_stride_y,
                         uint8* dst_u, int dst_stride_u,
                         uint8* dst_v, int dst_stride_v,
                         int width, int height);

    static int RGBA2I420(const uint8* src_frame, int src_stride_frame,
                         uint8* dst_y, int dst_stride_y,
                         uint8* dst_u, int dst_stride_u,
                         uint8* dst_v, int dst_stride_v,
                         int width, int height);
};

#endif //NATIVEAPP_LIBVIDEOPROCESS_H
