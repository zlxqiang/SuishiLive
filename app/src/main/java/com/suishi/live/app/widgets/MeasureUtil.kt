package com.suishi.live.app.widgets

import android.view.View

/**
 * Created by weight68kg on 2018/6/20.
 */
object MeasureUtil {
    const val ASPECT_RATIO_4_3 = 4

    /**
     * 原始尺寸
     */
    const val ASPECT_RATIO_ORIGIN = 0

    /**
     * 保持宽高比让短边铺满屏幕
     */
    const val ASPECT_RATIO_FIT_PARENT = 1

    /**
     * 保持宽高比让高宽都match_parent不留空隙
     */
    const val ASPECT_RATIO_PAVED_PARENT = 2

    /**
     * 按照指定比例 16:9
     */
    const val ASPECT_RATIO_16_9 = 3
    private const val TAG = "MeasureUtil"

    //int widthMeasureSpec, int heightMeasureSpec
    fun measure(displayAspectRatio: Int, widthMeasureSpec: Int, heightMeasureSpec: Int, videoWidth: Int, videoHeight: Int): Size {
        var width = View.getDefaultSize(videoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(videoHeight, heightMeasureSpec)
        return if (displayAspectRatio == ASPECT_RATIO_ORIGIN) {
            Size(videoWidth, videoHeight) //P
        } else if (videoWidth > 0 && videoHeight > 0) {
            val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
            if (widthMode == View.MeasureSpec.EXACTLY && heightMode == View.MeasureSpec.EXACTLY) {  //
                val percentView = widthSize.toFloat() / heightSize.toFloat()
                val percentVideo: Float
                percentVideo = when (displayAspectRatio) {
                    ASPECT_RATIO_FIT_PARENT, ASPECT_RATIO_PAVED_PARENT -> videoWidth.toFloat() / videoHeight.toFloat()
                    ASPECT_RATIO_16_9 -> 1.7777778f
                    ASPECT_RATIO_4_3 -> 1.3333334f
                    else -> videoWidth.toFloat() / videoHeight.toFloat()
                }
                when (displayAspectRatio) {
                    ASPECT_RATIO_FIT_PARENT, ASPECT_RATIO_16_9, ASPECT_RATIO_4_3 -> if (percentVideo > percentView) {
                        width = widthSize
                        height = (widthSize.toFloat() / percentVideo).toInt()
                    } else {
                        height = heightSize
                        width = (heightSize.toFloat() * percentVideo).toInt()
                    }
                    ASPECT_RATIO_PAVED_PARENT -> if (percentVideo > percentView) {
                        height = heightSize
                        width = (heightSize.toFloat() * percentVideo).toInt()
                    } else {
                        width = widthSize
                        height = (widthSize.toFloat() / percentVideo).toInt()
                    }
                }
            } else if (widthMode == View.MeasureSpec.EXACTLY) {
                width = widthSize
                height = widthSize * videoHeight / videoWidth
                if (heightMode == View.MeasureSpec.AT_MOST && height > heightSize) {
                    height = heightSize
                }
            } else if (heightMode == View.MeasureSpec.EXACTLY) {
                height = heightSize
                width = heightSize * videoWidth / videoHeight
                if (widthMode == View.MeasureSpec.AT_MOST && width > widthSize) {
                    width = widthSize
                }
            } else {
                width = videoWidth
                height = videoHeight
                if (heightMode == View.MeasureSpec.AT_MOST && videoHeight > heightSize) {
                    height = heightSize
                    width = heightSize * videoWidth / videoHeight
                }
                if (widthMode == View.MeasureSpec.AT_MOST && width > widthSize) {
                    width = widthSize
                    height = widthSize * videoHeight / videoWidth
                }
            }
            Size(width, height)
        } else {
            Size(width, height)
        }
    }

    class Size(val width: Int, val height: Int)
}