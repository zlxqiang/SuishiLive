package com.suishi.camera.camera;

import com.suishi.camera.camera.init.Init;
import com.suishi.camera.camera.open.Open;

public abstract class ICameraWrapper<M extends ICameraBuilder> {

    protected M mBuilder;

    abstract void init();

    M getBuilder(){
        return mBuilder;
    }

    abstract void open();

}
