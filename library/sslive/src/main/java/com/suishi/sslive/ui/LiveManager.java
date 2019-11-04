package com.suishi.sslive.ui;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.suishi.sslive.mode.engine.audio.AudioConfig;
import com.suishi.sslive.mode.engine.audio.AudioManager;
import com.suishi.sslive.mode.engine.camera.CameraHelper;
import com.suishi.sslive.mode.engine.video.VideoConfig;
import com.suishi.sslive.mode.engine.video.VideoManager;
import com.suishi.sslive.mode.mediacodec.AudioMediaCodec;
import com.suishi.sslive.mode.mediacodec.MediaCodecManager;
import com.suishi.sslive.mode.mediacodec.VideoMediaCodec;
import com.suishi.sslive.mode.stream.StreamManager;
import com.suishi.sslive.utils.FpsTools;
import com.suishi.sslive.utils.HardWareSupport;
import com.suishi.sslive.widgets.CameraGlSurfaceView;
import com.suishi.utils.LogUtils;

import java.nio.ByteBuffer;

/**
 * 推流层
 * Created by admin on 2018/3/8.
 */

public class LiveManager extends OnLowMemoryCallBack implements StreamManager.PushCallback, AudioManager.AudioStackCallBack, AudioManager.AudioFrameCallBack, VideoManager.VideoFrameCallBack, AudioMediaCodec.OnMediaCodecAudioEncodeListener, VideoMediaCodec.OnMediaCodecVideoEncodeListener {

    private static String TAG = LiveManager.class.getSimpleName();

    private CameraGlSurfaceView mFilterGLSurfaceView;
    /**
     *
     */
    private String url = "rtmp://172.18.2.90/live/stream";

    private Context mContext;

    /**
     * 硬编码管理器
     */
    private MediaCodecManager mMediaCodecManager;

    private boolean isSupportHardWareEncode = false;

    private FpsTools mFpsTools = new FpsTools();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public LiveManager(Context context, CameraGlSurfaceView filterGLSurfaceView) {
        this.mContext = context;
        mFilterGLSurfaceView = filterGLSurfaceView;
    }


    /**
     * 初始化操作
     *
     * @return
     */
    public boolean init() {

        boolean videoInit = false;

        boolean audioInit = false;
        try {
            //1.视频采集器初始化
            if (VideoManager.instance().initCameraDevice() && mFilterGLSurfaceView.resume()) {
                VideoManager.instance().setFrameCallBack(this);
                videoInit = true;
            } else {
                Toast.makeText(mContext, "相机不能用", Toast.LENGTH_SHORT).show();
                return false;
            }

            //2 音频采集器初始化
            if (AudioManager.instance().initAudioDevice()) {
                AudioManager.instance().setCallBack(this);
                AudioManager.instance().setFrameCallBack(this);
                audioInit = true;
            }

            mMediaCodecManager = new MediaCodecManager();
            isSupportHardWareEncode = mMediaCodecManager.init();
            if (isSupportHardWareEncode) {
                mMediaCodecManager.setAudioEncodeListener(this);
                mMediaCodecManager.setVideoEncodeListener(this);
                mMediaCodecManager.start();
            }

            //3.native 推流器初始化
            if (InitNative()) {
                if (videoInit)
                    VideoManager.instance().start();

                if (audioInit)
                    AudioManager.instance().start();
            } else {
                Toast.makeText(mContext, "推流器不能用", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 初始化底层采集与编码器
     */
    private boolean InitNative() {
        int ret = 0;
        AudioConfig config = AudioManager.instance().getAudioConfig();
        ret = StreamManager.getInstance().InitAudio(config.getmChannel(), config.getSampleRate(), 16);
        if (ret < 0) {
            Log.e(TAG, "init audio capture failed!");
            return false;
        }
        ret = StreamManager.getInstance().InitVideo(VideoConfig.width, VideoConfig.height, VideoConfig.width, VideoConfig.height, 25, true);
        if (ret < 0) {
            Log.e(TAG, "init video capture failed!");
            return false;
        }
        ret = StreamManager.getInstance().InitAudioEncoder();
        if (ret < 0) {
            Log.e(TAG, "init AudioEncoder failed!");
            return false;
        }
        ret = StreamManager.getInstance().InitVideoEncoder();
        if (ret < 0) {
            Log.e(TAG, "init VideoEncoder failed!");
            return false;
        }
        ret = StreamManager.getInstance().StartPush(url);
        if (ret < 0) {
            Log.d(TAG, "native push failed!");
            return false;
        }
        //必须在initEncoder后调用
        Log.d(TAG, "native init success!");
        return true;
    }

    private boolean isStarting = false;

    /**
     * 开始推流
     */
    public void onStartStream() {
        if (!isStarting)
            isStarting = init();
    }

    /**
     * 是否可以推流
     *
     * @return
     */
    public boolean isStarting() {
        return isStarting;
    }

    /**
     * 停止推流
     */
    public void onStopStream() {
        destroy();
    }


    @Override
    public void onStreamCallback(long pts, long dts, long duration, long index) {

    }

    @Override
    public void onStreamInitCallBack(String error) {

    }

    public void switchCamera() {
        CameraHelper.getInstance().switchCamera();
    }

    /**
     * 锁毁推流器
     *
     * @return
     */
    public void destroy() {
        isStarting = false;
        mFilterGLSurfaceView.pause();
        AudioManager.instance().stop();
        VideoManager.instance().stop();
        StreamManager.getInstance().Close();
        StreamManager.getInstance().Release();
    }


    @Override
    public void onAudioInitSuccess() {

    }

    @Override
    public void onAudioStartSuccess() {

    }

    @Override
    public void onAudioStopSuccess() {

    }

    /**
     * 原始数据回调
     *
     * @param data
     * @param length
     */
    @Override
    public void onAudioFrame(byte[] data, int length) {
        if (false) {
            //硬编码
            //   mMediaCodecManager.getAudioMediaCodec().onAudioFrame(data, length);
        } else {
            //软编码
            StreamManager.getInstance().Encode2AAC(data, length);
        }
    }

    /**
     * 原始数据回调
     *
     * @param data
     * @param length
     */
    @Override
    public void onVideoFrame(byte[] data, int length) {
        if (isSupportHardWareEncode) {
            //硬编码
            byte[] dst = new byte[VideoConfig.height * VideoConfig.width * 3 / 2];
            StreamManager.abgr2yuv(data, dst);
            mMediaCodecManager.getVideoMediaCodec().onVideoFrame(dst, length);
        } else {
            //软编码
            StreamManager.getInstance().Encode2H264(data, length);
        }
        LogUtils.e(TAG, mFpsTools.fps() + "");
    }

    /**
     * 硬编码回调
     *
     * @param bb
     * @param bi
     */
    @Override
    public void onMediaCodecAudioEncode(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        StreamManager.getInstance().avOutputStream(bb.array());
        LogUtils.e(TAG, "hardware audio data");
    }

    /**
     * 硬编码回调
     *
     * @param bb
     * @param bi
     */
    @Override
    public void onMediaCodecVideoEncode(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        StreamManager.getInstance().avOutputStream(decodeValue(bb));
        LogUtils.e(TAG, "hardware video data");
    }

    public byte[] decodeValue(ByteBuffer bytes) {
        int len = bytes.limit() - bytes.position();
        byte[] bytes1 = new byte[len];
        bytes.get(bytes1);
        return bytes1;
    }

    /**
     * 低内存处理
     */
    @Override
    public void onTrimMemory(int level) {
        switch (level) {
            case TRIM_MEMORY_COMPLETE:
                //lru尾部很快被杀死
                break;
            case TRIM_MEMORY_MODERATE:
                //lru中部
                break;
            case TRIM_MEMORY_BACKGROUND:
                //lru头部，准备杀死中部进程
                break;
            case TRIM_MEMORY_UI_HIDDEN:
                //ui不可见
                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:
                //准备杀死后台进程
                break;
            case TRIM_MEMORY_RUNNING_LOW:
                //影响用户体验
                break;
            case TRIM_MEMORY_RUNNING_MODERATE:
                //低内存运行状态
                break;
        }
    }
}
