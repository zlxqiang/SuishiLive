package com.suishi.camera.tools;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import com.suishi.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by weight68kg on 2018/7/4.
 *
 *
 */

public class MakeVideoTool {

    public static final String TAG = "MakeVideoTool";

    private Map<Integer, String> mMediaPath = new HashMap<>();

    private String mTargetPath = FileUtils.getFileAbsolutePath(FileUtils.VIDEO_PATH);

    private String videoPath;
    private String audioPath;
    private String clipAudioPath;
    private long time;

    public MakeVideoTool(String videoPath, String audioPath, long time, ComposeCallBack callBack) {
        this.videoPath = videoPath;
        this.audioPath = audioPath;
        this.time = time;
        this.callBack = callBack;
        extractVideo(videoPath);

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showProgressLoading();
                    break;
                case 1:
                    if (callBack != null) {
                        callBack.success((String) msg.obj);
                    }
                    dismissProgress();
//                    String videoPath = (String) msg.obj;
//                    Intent intent = new Intent(MakeVideoActivity.this,MakeVideoActivity.class);
//                    intent.putExtra("path",videoPath);
//                    intent.putExtra("isPlayer",true);
//                    startActivity(intent);
//                    finish();
                    break;
                case 2:
                    dismissProgress();
                    break;
            }
        }
    };


    /**
     * 提取视频
     */
    public void extractVideo(String videoPath) {
        final String outVideo = mTargetPath + "/video.mp4";
//        String[] commands = FFmpegCommands.extractVideo(videoPath, outVideo);
//        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
//            @Override
//            public void onStart() {
//                Log.e(TAG, "extractVideo ffmpeg start...");
//            }
//
//            @Override
//            public void onEnd(int result) {
//                Log.e(TAG, "extractVideo ffmpeg end...");
//                mMediaPath.put(0, outVideo);
//                cutSelectMusic(audioPath, time);
//            }
//        });
    }


    /**
     * 提取音频
     */
    private void extractAudio(String filePath) {
        final String outVideo = mTargetPath + "/audio.aac";
//        String[] commands = FFmpegCommands.extractAudio(filePath, outVideo);
//        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
//            @Override
//            public void onStart() {
////                mAudioPlayer = new MediaPlayer();
//            }
//
//            @Override
//            public void onEnd(int result) {
//                Log.e(TAG, "extractAudio ffmpeg end...");
//                mMediaPath.put(1, outVideo);
//                String path = mMediaPath.get(0);
////                mVideoView.setVideoPath(path);
////                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////                    @Override
////                    public void onCompletion(MediaPlayer mediaPlayer) {
////                        mVideoView.start();
////                    }
////                });
////                mVideoView.start();
////                try {
////                    mAudioPlayer.setDataSource(mMediaPath.get(1));
////                    mAudioPlayer.setLooping(true);
////                    mAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                        @Override
////                        public void onPrepared(MediaPlayer mediaPlayer) {
////                            mAudioPlayer.setVolume(0.5f, 0.5f);
////                            mAudioPlayer.start();
////                        }
////                    });
////                    mAudioPlayer.prepare();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//            }
//        });
    }

    private void cutSelectMusic(String musicUrl, long time) {
        final String musicPath = mTargetPath + "/bgMusic.aac";
////        long time = getIntent().getIntExtra("time",0);
//        String[] commands = FFmpegCommands.cutIntoMusic(musicUrl, time, musicPath);
//        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
//            @Override
//            public void onStart() {
//                Log.e(TAG, "cutSelectMusic ffmpeg start...");
//            }
//
//            @Override
//            public void onEnd(int result) {
//                Log.e(TAG, "cutSelectMusic ffmpeg end...");
////                if(mMusicPlayer!=null){//移除上一个选择的音乐背景
////                    mMediaPath.remove(mMediaPath.size()-1);
////                }
//                mMediaPath.put(2, musicPath);
////                stopMediaPlayer();
////                mMusicPlayer = new MediaPlayer();
////                try {
////                    mMusicPlayer.setDataSource(musicPath);
////                    mMusicPlayer.setLooping(true);
////                    mMusicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                        @Override
////                        public void onPrepared(MediaPlayer mediaPlayer) {
////                            mediaPlayer.setVolume(0.5f, 0.5f);
////                            mediaPlayer.start();
////                            mMusicSeekBar.setProgress(50);
////                        }
////                    });
////                    mMusicPlayer.prepareAsync();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//                composeVideoMusic();
//            }
//        });
    }

    /**
     * 处理视频原声
     */
    public void composeVideoAudio() {
//        int mAudioVol = mAudioSeekBar.getProgress();
        int mAudioVol = 0;
        String audioUrl = mMediaPath.get(1);
        final String audioOutUrl = mTargetPath + "/tempAudio.aac";
//        String[] common = FFmpegCommands.changeAudioOrMusicVol(audioUrl, mAudioVol * 10, audioOutUrl);
//        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
//            @Override
//            public void onStart() {
//                Log.e(TAG, "changeAudioVol ffmpeg start...");
//                handler.sendEmptyMessage(0);
//            }
//
//            @Override
//            public void onEnd(int result) {
//                Log.e(TAG, "changeAudioVol ffmpeg end...");
//                if (mMediaPath.size() == 3) {
//                    composeVideoMusic();
//                } else {
//                    composeMusicAndAudio(audioOutUrl);
//                }
//            }
//        });
    }


    /**
     * 处理背景音乐
     */
    public void composeVideoMusic() {
//        final int mMusicVol = mMusicSeekBar.getProgress();
        final int mMusicVol = 100;
        String musicUrl;
//        if (audioUrl == null) {
//            musicUrl = mMediaPath.get(1);
//        } else {
        musicUrl = mMediaPath.get(2);
//        }
        final String musicOutUrl = mTargetPath + "/tempMusic.aac";
//        final String[] common = FFmpegCommands.changeAudioOrMusicVol(musicUrl, mMusicVol * 10, musicOutUrl);
//        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
//            @Override
//            public void onStart() {
//                Log.e(TAG, "changeMusicVol ffmpeg start...");
//                handler.sendEmptyMessage(0);
//            }
//
//            @Override
//            public void onEnd(int result) {
//                Log.e(TAG, "changeMusicVol ffmpeg end...");
////                composeAudioAndMusic(audioUrl, musicOutUrl);
//                composeMusicAndAudio(musicOutUrl);
//            }
//        });
    }

    /**
     * 视频和背景音乐合成
     *
     * @param bgMusicAndAudio
     */
    private void composeMusicAndAudio(String bgMusicAndAudio) {
        final String videoAudioPath = mTargetPath + "/videoMusicAudio.mp4";
        final String videoUrl = mMediaPath.get(0);
//        final int time = getIntent().getIntExtra("time", 0) - 1;

//        String[] common = FFmpegCommands.composeVideo(videoUrl, bgMusicAndAudio, videoAudioPath, time);
//        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
//            @Override
//            public void onStart() {
//                Log.e(TAG, "videoAndAudio ffmpeg start...");
//                handler.sendEmptyMessage(0);
//            }
//
//            @Override
//            public void onEnd(int result) {
//                Log.e(TAG, "videoAndAudio ffmpeg end...");
//                handleVideoNext(videoAudioPath);
//            }
//        });
    }

    /**
     * 适配处理完成，进入下一步
     */
    private void handleVideoNext(String videoUrl) {
        Message message = new Message();
        message.what = 1;
        message.obj = videoUrl;
        handler.sendMessage(message);
    }

    /**
     * 合成原声和背景音乐
     */
    public void composeAudioAndMusic(String audioUrl, String musicUrl) {
        if (audioUrl == null) {
            composeMusicAndAudio(musicUrl);
        } else {
            final String musicAudioPath = mTargetPath + "/audioMusic.aac";
//            String[] common = FFmpegCommands.composeAudio(audioUrl, musicUrl, musicAudioPath);
//            FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
//                @Override
//                public void onStart() {
//                    Log.e(TAG, "composeAudioAndMusic ffmpeg start...");
//                    handler.sendEmptyMessage(0);
//                }
//
//                @Override
//                public void onEnd(int result) {
//                    Log.e(TAG, "composeAudioAndMusic ffmpeg end...");
//                    composeMusicAndAudio(musicAudioPath);
//                }
//            });
        }
    }

    private void showProgressLoading() {

    }

    private void dismissProgress() {

    }


    public interface ComposeCallBack {
        void success(String videoPath);

    }

    ComposeCallBack callBack;
}
