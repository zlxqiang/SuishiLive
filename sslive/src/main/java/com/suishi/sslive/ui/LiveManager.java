package com.suishi.sslive.ui;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.suishi.sslive.mode.engine.audio.AudioConfig;
import com.suishi.sslive.mode.engine.audio.AudioManager;
import com.suishi.sslive.mode.engine.camera.CameraHelper;
import com.suishi.sslive.mode.engine.video.VideoConfig;
import com.suishi.sslive.mode.engine.video.VideoManager;
import com.suishi.sslive.mode.stream.StreamManager;
import com.suishi.sslive.widgets.CameraGlSurfaceView;

/**
 * 推流层
 * Created by admin on 2018/3/8.
 */

public class LiveManager implements StreamManager.PushCallback,AudioManager.AudioStackCallBack,AudioManager.AudioFrameCallBack,VideoManager.VideoFrameCallBack{

    private CameraGlSurfaceView mFilterGLSurfaceView;
    /**
     *
     */
    private String url = "rtmp://192.168.1.101/live/stream";

    private Context mContext;

    public LiveManager(Context context) {

    }

    public LiveManager(Context context, CameraGlSurfaceView filterGLSurfaceView) {
        this.mContext=context;
        mFilterGLSurfaceView=filterGLSurfaceView;
    }


    public boolean init() {

        boolean videoInit=false;

        boolean audioInit=false;
        try {
            if(VideoManager.instance().initCameraDevice() && mFilterGLSurfaceView.resume()) {
                VideoManager.instance().setFrameCallBack(this);
                videoInit=true;
            }else{
                Toast.makeText(mContext,"相机不能用",Toast.LENGTH_SHORT).show();
                return false;
            }

            if (AudioManager.instance().initAudioDevice()) {
                AudioManager.instance().setCallBack(this);
                AudioManager.instance().setFrameCallBack(this);
                audioInit=true;
            }
            if(InitNative()) {
                if(videoInit)
                VideoManager.instance().start();

                if(audioInit)
                AudioManager.instance().start();
            }else {
                Toast.makeText(mContext,"推流器不能用",Toast.LENGTH_SHORT).show();
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
        ret = StreamManager.getInstance().InitAudio(config.getmChannel(), config.getmSampleRate(), 16);
        if (ret < 0) {
            Log.e("initNative", "init audio capture failed!");
            return false;
        }
        ret = StreamManager.getInstance().InitVideo(VideoConfig.width,VideoConfig.height,VideoConfig.width, VideoConfig.height, 25, true);
        if (ret < 0) {
            Log.e("initNative", "init video capture failed!");
            return false;
        }
        ret = StreamManager.getInstance().InitAudioEncoder();
        if (ret < 0) {
            Log.e("initNative", "init AudioEncoder failed!");
            return false;
        }
        ret = StreamManager.getInstance().InitVideoEncoder();
        if (ret < 0) {
            Log.e("initNative", "init VideoEncoder failed!");
            return false;
        }
        ret = StreamManager.getInstance().StartPush(url);
        if (ret < 0) {
            Log.d("initNative", "native push failed!");
            return false;
        }
        //必须在initEncoder后调用
        Log.d("initNative", "native init success!");
        return true;
    }

    private boolean isStarting=false;

    /**
     * 开始推流
     */
    public void onStartStream() {
        if(!isStarting)
        isStarting=init();
    }

    /**
     * 是否可以推流
     * @return
     */
    public boolean isStarting(){
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

    public void switchCamera(){
        CameraHelper.getInstance().switchCamera();
    }


    /**
     * 锁毁推流器
     *
     * @return
     */
    public void destroy() {
        isStarting=false;
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

    @Override
    public void onAudioFrame(byte[] chunkPCM, int length) {
        StreamManager.getInstance().Encode2AAC(chunkPCM,length);
    }

    @Override
    public void onVideoFrame(byte[] data, int length) {
        StreamManager.getInstance().Encode2H264(data,length);
    }
}
