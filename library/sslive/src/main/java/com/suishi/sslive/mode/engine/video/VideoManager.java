package com.suishi.sslive.mode.engine.video;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.suishi.sslive.mode.engine.camera.CameraHelper;
import com.suishi.sslive.utils.LiveLog;
import com.suishi.utils.LogUtils;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by zhzq on 2018/3/25.
 */

public class VideoManager implements Camera.PreviewCallback  {

    private static String TAG = VideoManager.class.getSimpleName();

    private static VideoManager sHolder;

    private ArrayBlockingQueue<byte[]> mQueue;

    private int m_nDuration=1000 / CameraHelper.FPS;

    private long m_nSumTime;

    private long m_nPrevTime;

    private long m_nCurrentTime;

    private Thread mEncodeThread;

    private boolean isRecord = false;

    /**
     * 回掉
     */
    private VideoStackCallBack mCallBack;

    private VideoFrameCallBack mFrameCallBack;

    /**
     * 纹理对象
     */
    private SurfaceTexture mSurfaceTexture;


    private VideoManager(){
    }


    public static synchronized VideoManager instance() {
        if (sHolder == null) {
            sHolder = new VideoManager();
        }
        return sHolder;
    }

    public void setSurface(SurfaceTexture serface){
        mSurfaceTexture=serface;
    }

    public boolean initCameraDevice(){
        mQueue = new ArrayBlockingQueue<byte[]>(3);
            return true;
    }

    public void start(){
        mQueue.clear();
        //每帧时间
        this.m_nDuration = 1000 / CameraHelper.FPS;
        this.m_nSumTime = 0L;
        this.m_nPrevTime = System.currentTimeMillis();
        isRecord=true;
        mEncodeThread = new Thread(new EncodeRunnable());
        mEncodeThread.start();

    }

    public void stop(){
        isRecord=false;
    }

    /**
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(mQueue==null)
            return;
        if(this.mQueue.size() == 2) {
            this.mQueue.poll();
        }
        this.m_nCurrentTime = System.currentTimeMillis();
        this.m_nSumTime = this.m_nSumTime + this.m_nCurrentTime - this.m_nPrevTime;
        if(this.m_nSumTime > (long)this.m_nDuration) {
            this.mQueue.offer(data);
            this.m_nSumTime %= (long)this.m_nDuration;
        } else {
            LogUtils.e(TAG, "丢帧");
        }

        this.m_nPrevTime = this.m_nCurrentTime;
    }

    public void setCallBack(VideoManager.VideoStackCallBack callBack){
        this.mCallBack=callBack;
    }

    public void setFrameCallBack(VideoManager.VideoFrameCallBack callBack){
        this.mFrameCallBack=callBack;
    }

    private class EncodeRunnable implements Runnable {
        byte[] m_nv21Data = new byte[CameraHelper.getInstance().getPreviewWidth()
                * CameraHelper.getInstance().getPreviewHeight() * 3 / 2];
        @Override
        public void run() {
            LiveLog.d(this,"编码线程开始");
            while (isRecord) {
                try {
                    Thread.sleep(1, 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!mQueue.isEmpty()){
                    byte[] chunkPCM = mQueue.poll();
                    if (chunkPCM != null && mFrameCallBack!=null) {
                        //System.arraycopy(chunkPCM,0,m_nv21Data,0,chunkPCM.length);
                        mFrameCallBack.onVideoFrame(chunkPCM, chunkPCM.length);
                    }
                }
            }
            release();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if(mCallBack!=null){
            mCallBack.onVideoStopSuccess();
        }
        LiveLog.d(this,"release");
    }

    public interface VideoStackCallBack {

        void onVideoInitSuccess();

        void onVideoStartSuccess();

        void onVideoStopSuccess();

    }

    public interface VideoFrameCallBack {
        void onVideoFrame( byte[] chunkPCM,int length);

    }
}
