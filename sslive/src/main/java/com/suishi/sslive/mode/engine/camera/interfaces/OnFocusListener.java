package com.suishi.sslive.mode.engine.camera.interfaces;

/**
 * 摄像头聚焦回调
 *
 * @author Created by jz on 2017/5/2 16:56
 */
public interface OnFocusListener {
    void onFocusStart(int x, int y);

    void onFocusEnd();
}