package com.suishi.camera.camera;


import com.suishi.camera.feature.close.Close;
import com.suishi.camera.feature.init.Init;
import com.suishi.camera.feature.open.Open;
import com.suishi.camera.feature.privew.Preview;
import com.suishi.camera.feature.record.Record;

/**
 * 功能创建者
 *
 */
public abstract class ICameraBuilder<W extends Init,
        T extends Open,
        C extends Preview,
        D extends Close,
        E extends Record,
        Z extends ICameraWrapper> extends CameraAssembly<W,T,C,D,E>{


    public ICameraBuilder<W, T, C, D, E, Z> setInit(W init){
        this.mInit=init;
        return this;
    }

    public W getInit(){
        return mInit;
    }

    /**
     * 打开
     */
    public ICameraBuilder<W, T,C,D,E,Z> useOpen(T open){
        this.mOpen=open;
        return this;
    }

    public ICameraBuilder<W, T,C,D,E,Z> usePreview(C preview){
        this.mPreview=preview;
        return this;
    }

    public C getPreview(){
        return mPreview;
    }

    /**
     *
     * @return
     */
    public T getOpen(){
        return mOpen;
    }

    public ICameraBuilder<W, T,C,D,E,Z> useRecord(E record){
        mRecord=record;
        return this;
    }

    public E getRecord(){
        return  mRecord;
    }

    /**
     * close the camera
     */
    public ICameraBuilder<W, T,C,D,E,Z> close(D close){
        mClose=close;
        return this;
    }

    public D getClose(){
        return mClose;
    }

    abstract Z build();

}
