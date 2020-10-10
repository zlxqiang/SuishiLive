package com.suishi.camera.camera;


import com.suishi.camera.feature.close.Close;
import com.suishi.camera.feature.init.Init;
import com.suishi.camera.feature.open.Open;
import com.suishi.camera.feature.privew.Preview;

/**
 * 负责使用功能
 */
public abstract class ICameraWrapper<W extends Init,T extends Open,C extends Preview,D extends Close,M extends ICameraBuilder> extends CameraAssembly<W,T,C,D> {

    protected M mBuilder;

    /**
     * 初始化
     */
    abstract void init();

    /**
     * 打开相机
     */
    public abstract void open();
    /**
     * 关闭
     */
    public abstract void close();
    /**
     * 设置预览
     */
    public abstract void startPreview();

    /**
     * 停止预览
     */
    public abstract void stopPreview();

    public abstract void startRecord();

    public abstract void stopRecord();

    /**
     * 切換相機
     */
    public abstract void switchCamera();

    /**
     * 释放
     */
    public abstract void release();

    public M getBuilder(){
        return mBuilder;
    }

}
