package com.suishi.camera.player;

/**
 * Created by weight68kg on 2018/7/18.
 */

public interface IMPlayer {

    /**
     * 设置资源
     * @param url 资源路径
     * @throws MPlayerException
     */
    void setSource(String url) throws MPlayerException;

    /**
     * 设置显示视频的载体
     * @param display 视频播放的载体及相关界面
     */
    void setDisplay(IMDisplay display);

    /**
     * 播放视频
     * @throws MPlayerException
     */
    void play() throws MPlayerException;

    /**
     * 暂停视频
     */
    void pause();


    void onPause();
    void onResume();
    void onDestroy();

}