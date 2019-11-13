package com.suishi.camera.player;

/**
 * Created by weight68kg on 2018/7/18.
 */

public interface IMPlayListener {

    void onStart(IMPlayer player);
    void onPause(IMPlayer player);
    void onResume(IMPlayer player);
    void onComplete(IMPlayer player);

}