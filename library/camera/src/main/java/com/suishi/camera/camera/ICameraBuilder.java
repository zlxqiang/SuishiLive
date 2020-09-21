package com.suishi.camera.camera;

import com.suishi.camera.camera.init.Init;
import com.suishi.camera.camera.open.Open;

/**
 * 功能创建者
 *
 */
public abstract class ICameraBuilder<W extends Init,T extends Open,Z extends ICameraWrapper> {

    private W mInit;

    private T mOpen;


    public ICameraBuilder<W, T,Z> setInit(W init){
        this.mInit=init;
        return this;
    }

    public W getInit(){
        return mInit;
    }

    /**
     * 打开
     */
    public ICameraBuilder<W, T,Z> userOpen(T open){
        this.mOpen=open;
        return this;
    }

    /**
     *
     * @return
     */
    public T getOpen(){
        return mOpen;
    }


    /**
     * close the camera
     */
    public ICameraBuilder<W, T,Z> close(){
        return this;
    }


    abstract Z build();

}
