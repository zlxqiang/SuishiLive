package com.suishi.camera.player;

import android.view.SurfaceHolder;
import android.view.View;

/**
 * Created by weight68kg on 2018/7/18.
 */

public interface IMDisplay extends IMPlayListener {

    View getDisplayView();
    SurfaceHolder getHolder();

}