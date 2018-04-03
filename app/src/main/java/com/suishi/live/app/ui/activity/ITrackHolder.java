package com.suishi.live.app.ui.activity;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * Created by admin on 2018/3/12.
 */

public  interface ITrackHolder {
    ITrackInfo[] getTrackInfo();
    int getSelectedTrack(int trackType);
    void selectTrack(int stream);
    void deselectTrack(int stream);
}