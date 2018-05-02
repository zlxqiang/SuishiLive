//
//

#include "VideoMachining.h"

int VideoMachining::MirrorI420(int in_width, int in_height,
                               const uint8_t *srcData, uint8_t *dstData) {
    return I420Mirror(srcData, in_width,
                      srcData + (in_width * in_height), in_width / 2,
                      srcData + (in_width * in_height * 5 / 4), in_width / 2,
                      dstData, in_width,
                      dstData + (in_width * in_height), in_width / 2,
                      dstData + (in_width * in_height * 5 / 4), in_width / 2,
                      in_width, in_height);
}

int VideoMachining::NV21TOI420(int in_width, int in_height,
                               const uint8_t *srcNV21Data, uint8_t *dstI420Data) {

    return NV21ToI420((const uint8_t *) srcNV21Data, in_width,
                      (uint8_t *) srcNV21Data + (in_width * in_height), in_width,
                      dstI420Data, in_width,
                      dstI420Data + (in_width * in_height), in_width / 2,
                      dstI420Data + (in_width * in_height * 5 / 4), in_width / 2,
                      in_width, in_height);
}

int VideoMachining::RotateI420(int in_width, int in_height,
                               const uint8_t *srcData, uint8_t *dstData,
                               int rotationValue) {

    RotationMode rotationMode = kRotate0;
    switch (rotationValue) {
        case 90:
            rotationMode = kRotate90;
            break;
        case 180:
            rotationMode = kRotate180;
            break;
        case 270:
            rotationMode = kRotate270;
            break;
    }
    return I420Rotate(srcData, in_width,
                      srcData + (in_width * in_height), in_width / 2,
                      srcData + (in_width * in_height * 5 / 4), in_width / 2,
                      dstData, in_height,
                      dstData + (in_width * in_height), in_height / 2,
                      dstData + (in_width * in_height * 5 / 4), in_height / 2,
                      in_width, in_height,
                      rotationMode);
}

/**
 *
 * @param src_argb 用于待转换的argb数据
 * @param src_stride_argb :argb数据每一行的大小，如果是argb_8888格式的话这个值为wX4，argb4444的话值为wX2
 * @param dst_y 用于保存y分量数据。
 * @param dst_stride_y 值为w*h。
 * @param dst_u 用于保存u分量数据。
 * @param dst_stride_u 值为(w+1)/2。
 * @param dst_v 用于保存分量数据。
 * @param dst_stride_v 值为(w+1)/2。
 * @param width 位图宽度
 * @param height 位图高度。
 * @return
 */
int VideoMachining::BGRA2I420(const uint8 *src_argb, int src_stride_argb,
                              uint8 *dst_y, int dst_stride_y,
                              uint8 *dst_u, int dst_stride_u,
                              uint8 *dst_v, int dst_stride_v,
                              int width, int height) {
    return ABGRToI420(src_argb,
                      src_stride_argb,
                      dst_y,
                      dst_stride_y,
                      dst_u,
                      dst_stride_u,
                      dst_v,
                      dst_stride_v,
                      width,
                      height);

}


int VideoMachining::RGBA2I420(const uint8* src_frame, int src_stride_frame,
                              uint8* dst_y, int dst_stride_y,
                              uint8* dst_u, int dst_stride_u,
                              uint8* dst_v, int dst_stride_v,
                              int width, int height) {
    return RGBAToI420(src_frame,
                      src_stride_frame,
                      dst_y,
                      dst_stride_y,
                      dst_u,
                      dst_stride_u,
                      dst_v,
                      dst_stride_v,
                      width,
                      height);
}