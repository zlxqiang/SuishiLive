package com.suishi.live.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.PreviewConfig;

import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.suishi.camera.CameraView;
import com.suishi.camera.CircularProgressView;
import com.suishi.camera.FocusImageView;
import com.suishi.camera.camera.CameraController;
import com.suishi.camera.camera.SensorControler;
import com.suishi.camera.camera.gpufilter.SlideGpuFilterGroup;
import com.suishi.camera.camera.utils.CameraUtils;
import com.suishi.live.app.R;
import com.suishi.live.app.widgets.SystemVideoView;
import com.suishi.utils.BitmapUtil;
import com.suishi.utils.DensityUtils;
import com.suishi.utils.ToastUtil;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, SensorControler.CameraFocusListener, SlideGpuFilterGroup.OnFilterChangeListener, SurfaceHolder.Callback {


    public static void startActivity(AppCompatActivity context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivityForResult(intent, 1);
    }


    //照相 摄像
    public static void startActivity(AppCompatActivity context, int code) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivityForResult(intent, code);
    }

    public static void startActivity(AppCompatActivity context, boolean openTakePicture) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(CameraActivity.CAMERA_CLOSE_TAKE_PICTURE, openTakePicture);
        context.startActivity(intent);
    }

    public static final String CAMERA_CLOSE_TAKE_PICTURE = "CAMERA_CLOSE_TAKE_PICTURE";

    /**
     *
     */
    CameraView mCameraView;

    /**
     *
     */
    ImageView mCameraChange;

    /**
     *
     */
    CircularProgressView mCapture;

    /**
     *
     */
    FocusImageView mFocus;

    /**
     *
     */
    RelativeLayout rlCameraBefore;

    /**
     *
     */
    RelativeLayout rlCameraLater;


    /**
     *
     */
    ImageView ivCameraConfirm;

    /**
     *
     */
    SystemVideoView mVideoView;

    /**
     *
     */
    ImageView imagePhoto;

    /**
     *
     */
    LinearLayout ll_function;

    /**
     * 美颜设置
     */
    CheckBox mBeautySwitch;

    private boolean pausing = false;

    /**
     * 是否正在录制
     */
    private boolean recordFlag = false;

    /**
     * 用于记录录制时间
     */
    long timeCount = 0;

    private boolean autoPausing = false;

    ExecutorService executorService;

    private SensorControler mSensorControler;

    /**
     * 是否开启音乐
     */
    private boolean isOpenMusic = false;

    /**
     * 是否开启闪光灯
     */
    private boolean isOpenFlash = false;

    /**
     * 是否开启美颜
     */
    private boolean isOpenBeauty = false;

    private boolean isOpenTakePicture = false;

    private String videoPath;


    private Bitmap bitmap;

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                //  CarUtils.dismissLoading();
                break;
        }
        return true;
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mBeautySwitch = findViewById(R.id.iv_beauty_switch);
        mBeautySwitch.setVisibility(View.GONE);
        mCapture = findViewById(R.id.mCapture);
        mCameraView = findViewById(R.id.camera_view);
        mCameraChange = findViewById(R.id.btn_camera_switch);
        mFocus = findViewById(R.id.focusImageView);
        rlCameraBefore = findViewById(R.id.rl_camera_before);
        rlCameraLater = findViewById(R.id.rl_camera_later);
        ivCameraConfirm = findViewById(R.id.iv_camera_confirm);
        mVideoView = findViewById(R.id.video);
        mVideoView.setDisplayAspectRatio(1);
        imagePhoto = findViewById(R.id.image_photo);
        ll_function = findViewById(R.id.ll_function);


        mCameraView.setOnTouchListener(this);
        mCameraView.setOnFilterChangeListener(this);
        mCameraChange.setOnClickListener(this);
        mVideoView.getHolder().addCallback(this);
        findViewById(R.id.iv_camera_back).setOnClickListener(this);
        ivCameraConfirm.setOnClickListener(this);
        mCameraChange.setOnClickListener(this);
        mBeautySwitch.setOnClickListener(this);
        findViewById(R.id.iv_flash_switch).setOnClickListener(this);

        addListener();
        if (getIntent().hasExtra(CAMERA_CLOSE_TAKE_PICTURE)) {
            isOpenTakePicture = getIntent().getBooleanExtra(CAMERA_CLOSE_TAKE_PICTURE, true);
        }
        executorService = Executors.newSingleThreadExecutor();
        mSensorControler = SensorControler.getInstance();
        mSensorControler.setCameraFocusListener(this);

    }

//    private void startCamera() {
//        // 1. preview
//        @SuppressLint("RestrictedApi") Preview preview = new Preview(Preview.DEFAULT_CONFIG.getConfig());
//        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
//            @Override
//            public void onUpdated(Preview.PreviewOutput output) {
//                ViewGroup parent = (ViewGroup) viewFinder.getParent();
//                parent.removeView(viewFinder);
//                parent.addView(viewFinder, 0);
//
//                viewFinder.setSurfaceTexture(output.getSurfaceTexture());
//                updateTransform();
//            }
//        });
//
//        CameraX.bindToLifecycle(this, preview);
//    }


    protected void addListener() {
        mCapture.setOnLongDownListener(new CircularProgressView.OnLongDownListener() {
            @Override
            public void onLongClick() {
                if (!recordFlag) {
                    Log.e("CameraActivity", isOpenMusic + "!recordFlag" + System.currentTimeMillis());
                    if (isOpenMusic) {
                        if (mp3Player != null) {
                            mp3Player.start();
                        }
                    }
                    if (mCapture.getProcess() >= mCapture.getTotal()) {
                        recordFlag = false;
                        mCameraView.stopRecord();
                    } else {
                        executorService.execute(recordRunnable);
                    }
                } else if (!pausing) {
                    Log.e("CameraActivity", "!pausing");
                    mCameraView.pause(false);
                    pausing = true;
                } else {
                    Log.e("CameraActivity", "else");
                    mCameraView.resume(false);
                    pausing = false;
                    if (mp3Player != null) {
                        mp3Player.stop();
                    }
                }
            }

            @Override
            public void onClick() {
                if (!isOpenTakePicture) {
                    ToastUtil.toast("wwwww");
                    return;
                }
                mCameraView.takePicture(new CameraController.TakePictureCallBack() {
                    @Override
                    public void onRawPictureTaken(byte[] data, Camera camera) {
                        Log.e("CameraActivity", "onRawPictureTaken");
                        rlCameraBefore.setVisibility(View.GONE);
                        rlCameraLater.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onJpegPictureTaken(byte[] data, Camera camera) {
                        Log.e("CameraActivity", "onJpegPictureTaken");
                        /* 取得相片 */
                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
                                data.length);
                        imagePhoto.setVisibility(View.VISIBLE);
                        mCameraView.setVisibility(View.GONE);
                        int cameraPhotoDegree = CameraUtils.getCameraPhotoDegree(CameraActivity.this, mCameraView.getCameraId());
                        bitmap = BitmapUtil.rotateBitmapByDegree(bm, cameraPhotoDegree);
                        imagePhoto.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onShutter() {
                        Log.e("CameraActivity", "onShutter");
                    }
                });
            }

            @Override
            public void onLongClickUp() {
                if (recordFlag) {
                    recordFlag = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_camera_back:
                //放弃录制的视频
                rlCameraBefore.setVisibility(View.VISIBLE);
                rlCameraLater.setVisibility(View.GONE);
                mCameraView.open(mCameraView.getCameraId());
                mCameraView.setVisibility(View.VISIBLE);
                imagePhoto.setVisibility(View.INVISIBLE);
                mVideoView.stopPlayback();
                mCapture.stopTouch(false);
                mCapture.setProcess(0);
                ll_function.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_camera_confirm:
                //
                Intent intent = getIntent();
                intent.putExtra("path", videoPath);
                setResult(2, intent);
                finish();
                break;
            case R.id.btn_camera_switch:
                //转换摄像头
                mCameraView.switchCamera();
                if (mCameraView.getCameraId() == 1) {
                    //前置摄像头 使用美颜
                    mCameraView.changeBeautyLevel(5);
                    aotuFocus();
                } else {
                    //后置摄像头不使用美颜
                    mCameraView.changeBeautyLevel(5);
                    aotuFocus();
                }
                break;
            case R.id.mCapture:
                //进度条

                break;
            case R.id.iv_beauty_switch:
                //美颜
                if (mCameraView.getCameraId() == 0) {
                    Toast.makeText(this, "后置摄像头 不使用美白磨皮功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("CameraActivity", mCameraView.getBeautyLevel() + "");
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(new String[]{"关闭", "1", "2", "3", "4", "5"}, mCameraView.getBeautyLevel(),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mCameraView.changeBeautyLevel(which);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消", null)
                        .show();
                if (isOpenBeauty) {
                    //关闭美颜
                    isOpenBeauty = false;
                    mCameraView.changeBeautyLevel(0);
                } else {
                    //打开美颜
                    isOpenBeauty = true;
                    mCameraView.changeBeautyLevel(3);
                }
                break;
            case R.id.iv_flash_switch:
                //闪光灯
                if (isOpenFlash) {
                    isOpenFlash = false;
                    mCameraView.openLightOn();
                } else {
                    isOpenFlash = true;
                    mCameraView.openLightOn();
                }

                break;
        }

    }

    private void aotuFocus() {
        float sRawX = DensityUtils.getScreenWidth() / 2;
        float sRawY = DensityUtils.getScreenHeight() / 2;
        float rawY = sRawY * DensityUtils.getScreenWidth() / DensityUtils.getScreenHeight();
        float temp = sRawX;
        float rawX = rawY;
        rawY = (DensityUtils.getScreenWidth() - temp) * DensityUtils.getScreenHeight() / DensityUtils.getScreenWidth();
        mFocus.startFocus(DensityUtils.getScreenWidth() / 2, DensityUtils.getScreenHeight() / 2);
        Point point = new Point((int) rawX, (int) rawY);
        mCameraView.onFocus(point, callback);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mCameraView.onTouch(event);
        if (mCameraView.getCameraId() == 1) {
//            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float sRawX = event.getRawX();
                float sRawY = event.getRawY();
                float rawY = sRawY * DensityUtils.getScreenWidth() / DensityUtils.getScreenHeight();
                float temp = sRawX;
                float rawX = rawY;
                rawY = (DensityUtils.getScreenWidth() - temp) * DensityUtils.getScreenHeight() / DensityUtils.getScreenWidth();

                Point point = new Point((int) rawX, (int) rawY);
                mCameraView.onFocus(point, callback);
                mFocus.startFocus(new Point((int) sRawX, (int) sRawY));
        }
        return true;
    }

    Camera.AutoFocusCallback callback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //聚焦之后根据结果修改图片
            Log.e("hero", "----onAutoFocus====" + success);
            if (success) {
                mFocus.onFocusSuccess();
            } else {
                //聚焦失败显示的图片
                mFocus.onFocusFailed();
            }
        }
    };

    @Override
    public void onFocus() {
        if (mCameraView.getCameraId() == 1) {
            return;
        }
        Point point = new Point(DensityUtils.getScreenWidth() / 2, DensityUtils.getScreenHeight() / 2);
        mCameraView.onFocus(point, callback);
    }

    @Override
    public void onBackPressed() {
        if (recordFlag) {
            recordFlag = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
        if (recordFlag && autoPausing) {
            mCameraView.resume(true);
            autoPausing = false;
        }
        if (imagePhoto.getVisibility() == View.INVISIBLE) {
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recordFlag && !pausing) {
            mCameraView.pause(true);
            autoPausing = true;
        }
        mCameraView.onPause();
        mVideoView.pause();
    }

    @Override
    public void onFilterChange(final MagicFilterType type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type == MagicFilterType.NONE) {
                    Toast.makeText(CameraActivity.this, "当前没有设置滤镜--" + type, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CameraActivity.this, "当前滤镜切换为--" + type, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            recordFlag = true;
            pausing = false;
            autoPausing = false;
            timeCount = 0;
            long time = System.currentTimeMillis();
            String savePath = getPath("video/", time + ".mp4");

            try {
                mCameraView.setSavePath(savePath);
                mCameraView.startRecord();
                while (recordFlag) {
                    if (mCapture.getProcess() >= mCapture.getTotal()) {
                        recordFlag = false;
                        mCapture.setCanDown(true);
                        mCapture.stop();
                    }
                    Thread.sleep(20);
                }
                Thread.sleep(1000);
                recordFlag = false;
                mCameraView.pause(true);
                mCameraView.stopRecord();
                recordComplete(savePath, mCapture.getProcess());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //获取VideoPath
    public static String getPath(String path, String fileName) {
        String p = getBaseFolder() + path;
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder() + fileName;
        }
        return p + fileName;
    }

    public static String getBaseFolder() {
        String baseFolder = Environment.getExternalStorageDirectory() + "/sslive/";
        File f = new File(baseFolder);
        if (!f.exists()) {
            f.mkdirs();
        }
        return baseFolder;
    }

    private void recordComplete(final String path, final long timeCount) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                /**
                 *要执行的操作
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rlCameraBefore.setVisibility(View.GONE);
                        rlCameraLater.setVisibility(View.VISIBLE);
                        if (mCapture.getProcess() < 2000) {
                            ivCameraConfirm.setVisibility(View.INVISIBLE);
                            Toast.makeText(CameraActivity.this, "时间太短", Toast.LENGTH_SHORT).show();
                        } else {
                            ivCameraConfirm.setVisibility(View.VISIBLE);
                        }
                        mCapture.stopTouch(true);
                        rlCameraBefore.setVisibility(View.GONE);
                        rlCameraLater.setVisibility(View.VISIBLE);
                        ll_function.setVisibility(View.GONE);

                        videoPath = path;
                        mCapture.setProcess(0);
                        mCameraView.setRenderModeDirty();
                        mCameraView.setVisibility(View.GONE);
                        mVideoView.setVideoPath(videoPath);
                        mVideoView.start();
                        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mVideoView.start();
                            }
                        });
                    }
                });

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000);//3秒后执行TimeTask的run方法


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    MediaPlayer mp3Player;

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (mp3Player != null) {
            mp3Player.release();
        }
        if (mCameraView != null) {
            mCameraView.onDestroy();
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
