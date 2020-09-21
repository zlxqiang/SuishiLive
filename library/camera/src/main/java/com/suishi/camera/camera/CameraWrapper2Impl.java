package com.suishi.camera.camera;

/**
 * Camera2包装类
 */
public class CameraWrapper2Impl extends ICameraWrapper<CameraBuilder2> {

    public CameraWrapper2Impl(CameraBuilder2 mBuilder) {
        this.mBuilder = mBuilder;
    }

    @Override
    void init() {
      if(mBuilder!=null && mBuilder.getInit()!=null){
          mBuilder.getInit().getCamerList();
      }
    }

    @Override
    public CameraBuilder2 getBuilder() {
        return mBuilder;
    }

    @Override
    public void open() {

    }

}
