package com.suishi.live.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.suishi.live.app.R;
import com.suishi.live.app.widgets.MultiToggleImageButton;
import com.suishi.sslive.ui.LiveManager;
import com.suishi.sslive.widgets.CameraGlSurfaceView;

public class PortraitActivity extends Activity {

    CameraGlSurfaceView filter;

    private LiveManager mLiveManager;

    private MultiToggleImageButton mMicBtn;

    private MultiToggleImageButton mFlashBtn;

    private MultiToggleImageButton mFaceBtn;

    private MultiToggleImageButton mBeautyBtn;

    private MultiToggleImageButton mFocusBtn;

    private ImageButton mRecordBtn;

    private boolean isGray;

    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        filter = findViewById(R.id.surface);
        mLiveManager = new LiveManager(this, filter);
        initViews();
        initListeners();
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mLiveManager.onStopStream();
    }

    private void initViews() {
        //  mLFLiveView = (LivingView) findViewById(R.id.liveView);
        mMicBtn = findViewById(R.id.record_mic_button);
        mFlashBtn = findViewById(R.id.camera_flash_button);
        mFaceBtn = findViewById(R.id.camera_switch_button);
        mBeautyBtn = findViewById(R.id.camera_render_button);
        mFocusBtn = findViewById(R.id.camera_focus_button);
        mRecordBtn = findViewById(R.id.btnRecord);
    }

    private void initListeners() {
        mMicBtn.setOnStateChangeListener((view, state) -> {
            //   mLFLiveView.mute(true);
        });
        mFlashBtn.setOnStateChangeListener((view, state) -> {
            //  mLFLiveView.switchTorch();
        });
        mFaceBtn.setOnStateChangeListener((view, state) -> {
            // 切换摄像头
            mLiveManager.switchCamera();
        });

        mBeautyBtn.setOnStateChangeListener((view, state) -> isGray = !isGray);

        mFocusBtn.setOnStateChangeListener((view, state) -> {
            // mLFLiveView.switchFocusMode();
        });

        mRecordBtn.setOnClickListener(v -> {
            if (isRecording) {
                //停止推流
                mRecordBtn.setBackgroundResource(R.mipmap.ic_record_start);
                mLiveManager.onStopStream();
                isRecording = false;
            } else {
                //开始推流
                mRecordBtn.setBackgroundResource(R.mipmap.ic_record_stop);
                mLiveManager.onStartStream();
                isRecording = true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
