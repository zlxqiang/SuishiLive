package com.suishi.camera.player;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by weight68kg on 2018/7/18.
 */

public class MinimalDisplay implements IMDisplay {

    private SurfaceView surfaceView;

    public MinimalDisplay(SurfaceView surfaceView){
        this.surfaceView=surfaceView;
    }

    @Override
    public View getDisplayView() {
        return surfaceView;
    }

    @Override
    public SurfaceHolder getHolder() {
        return surfaceView.getHolder();
    }

    @Override
    public void onStart(IMPlayer player) {

    }

    @Override
    public void onPause(IMPlayer player) {

    }

    @Override
    public void onResume(IMPlayer player) {

    }

    @Override
    public void onComplete(IMPlayer player) {

    }
}