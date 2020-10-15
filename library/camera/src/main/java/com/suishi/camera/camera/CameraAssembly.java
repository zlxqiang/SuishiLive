package com.suishi.camera.camera;

import com.suishi.camera.feature.close.Close;
import com.suishi.camera.feature.init.Init;
import com.suishi.camera.feature.open.Open;
import com.suishi.camera.feature.privew.Preview;

/**
 * 支持的功能模块
 */
public abstract class CameraAssembly<W extends Init,
                T extends Open,
                C extends Preview,
                D extends Close>{

    protected W mInit;

    protected T mOpen;

    protected C mPreview;

    protected D mClose;

}
