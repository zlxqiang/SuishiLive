package com.suishi.sslive.mode.engine.audio;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.suishi.sslive.mode.stream.StreamManager;
import com.suishi.sslive.utils.LiveLog;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 赵增强 on 2018/3/24.
 */
public class AudioManager {

    private static final String TAG="AudioManager";

    private static AudioManager sHolder;

    private AudioConfig mAudioConfig;

    private Thread mRecordThread;
    private Thread mEncodeThread;

    private AudioRecord mAudioRecord;

    private boolean isRecord = false;
    private byte[] mAudioBuffer;

    /**
     * 回掉
     */
    private AudioStackCallBack mCallBack;
    private AudioFrameCallBack mFrameCallBack;

    private ArrayBlockingQueue<AudioData> mQueue;

    private byte[] audioBuffer;

    private AudioManager(){
        mAudioConfig=new AudioConfig();
    }


    public static synchronized AudioManager instance() {
        if (sHolder == null) {
            sHolder = new AudioManager();
        }
        return sHolder;
    }

    public void setCallBack(AudioStackCallBack callBack){
        this.mCallBack=callBack;
    }
    public void setFrameCallBack(AudioFrameCallBack callBack){
        this.mFrameCallBack=callBack;
    }
    /**
     * 初始化AudioRecord  这里测试就直接用44100采样率 双声道  16bit。于FFmpeg编码器重保持一致
     * 当然大家可以根据自己的情况来修改，这里只是抛砖引玉。
     */
    public boolean initAudioDevice() {
        int minsize = AudioRecord.getMinBufferSize(mAudioConfig.getmSampleRate(), mAudioConfig.getmChannelConfig(), mAudioConfig.getmAudioFormat());
       // if(minsize< mAudioConfig.getmBuffsize()){
            minsize=mAudioConfig.getmBuffsize();
      //  }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mAudioConfig.getmSampleRate(), mAudioConfig.getmChannelConfig(),
                mAudioConfig.getmAudioFormat(),minsize);
        if(Build.VERSION.SDK_INT >= 16) {
            int m_iSessionID = this.mAudioRecord.getAudioSessionId();
            //噪音
            NoiseSuppressor m_oNoiseSuppressor = NoiseSuppressor.create(m_iSessionID);
            if(m_oNoiseSuppressor != null) {
                m_oNoiseSuppressor.setEnabled(true);
            }
            //回声
            AcousticEchoCanceler mAcousticEchoCanceler = AcousticEchoCanceler.create(m_iSessionID);
            if(mAcousticEchoCanceler != null) {
                mAcousticEchoCanceler.setEnabled(true);
            }
        }
        if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED ) {
            mAudioBuffer = new byte[minsize];
            audioBuffer = new byte[minsize];
            mQueue = new ArrayBlockingQueue(200);
            LiveLog.d(this,"音频参数："+mAudioConfig.toString());
            if(mCallBack!=null){
                mCallBack.onAudioInitSuccess();
            }
            return true;
        }
        return false;
    }

    public AudioConfig getAudioConfig(){
        return  mAudioConfig;
    }

    //开启录音
    public void start(){

        isRecord = true;
        mRecordThread = new Thread(fetchAudioRunnable());
        mEncodeThread = new Thread(new EncodeRunnable());
        mAudioRecord.startRecording();
        mEncodeThread.start();
        mRecordThread.start();
    }

    /**
     * 音频停止
     */
    public void stop() {
        isRecord = false;
    }

    private Runnable fetchAudioRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                fetchPcmFromDevice();
            }
        };
    }
    /**
     * 采集音频数据
     */
    private void fetchPcmFromDevice() {
        Log.w("","录音线程开始");
        while (isRecord && mAudioRecord != null && !Thread.interrupted()) {
            int size = mAudioRecord.read(mAudioBuffer, 0, mAudioBuffer.length);
            if (size < 0) {
                LiveLog.d(this,"av.audio ignore ,no data to read");
                break;
            }
            if (isRecord) {
                AudioData data = new AudioData();
                data.mData=mAudioBuffer;
                data.mLength=size;
                mQueue.offer(data);

            }
        }
    }


    private class EncodeRunnable implements Runnable {
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
                    AudioData data = mQueue.poll();
                    if (data != null && mFrameCallBack!=null) {
                        System.arraycopy(data.mData, 0, audioBuffer, 0, data.mLength);
                        mFrameCallBack.onAudioFrame(audioBuffer, data.mLength);
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
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
        }
        if(mCallBack!=null){
            mCallBack.onAudioStopSuccess();
        }
        LiveLog.d(this,"release");
    }

    public interface AudioStackCallBack {

        void onAudioInitSuccess();

        void onAudioStartSuccess();

        void onAudioStopSuccess();

    }

    public interface AudioFrameCallBack {
        void onAudioFrame( byte[] chunkPCM,int length);

    }
}
