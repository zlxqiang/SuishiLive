package com.suishi.camera.feature;

import com.suishi.camera.camera.ICameraBuilder;

/**
 * 定义各个模块生命周期
 * @param <T>
 */
public abstract class Assembly<T extends ICameraBuilder> {

    protected T mCameraBuilder;

    /**
     * 初始化参数
     */
    public Assembly() {

    }

    /**
     * 获取依赖模块,为功能使用准备
     * @param builder
     */
    public void cameraBuilder(T builder){
        mCameraBuilder=builder;
    }

    /**
     * 销毁builder的成员变量
     */
    public abstract void cameraUnBuilder();

    /**
     * 释放
     */
    public abstract void onRelease();

}
