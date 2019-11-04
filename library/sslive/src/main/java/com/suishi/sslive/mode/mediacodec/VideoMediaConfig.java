package com.suishi.sslive.mode.mediacodec;

import com.suishi.sslive.mode.engine.video.VideoConfig;
import com.suishi.sslive.mode.engine.video.VideoManager;

/**
 * Created by admin on 2018/4/3.
 */

public class VideoMediaConfig {
    public static final int DEFAULT_HEIGHT = 640;
    public static final int DEFAULT_WIDTH = 360;
    public static final int DEFAULT_FPS = 15;
    public static final int DEFAULT_MAX_BPS = 1300;
    public static final int DEFAULT_MIN_BPS = 400;
    public static final int DEFAULT_IFI = 2;
    public static final String DEFAULT_MIME = "video/avc";

    public static int height = VideoConfig.height;

    public static int width = VideoConfig.width;

    public final int minBps=400;

    public final int maxBps=1300;

    public final int fps=25;

    public final int ifi=2;

    public final String mime="video/avc";
}
