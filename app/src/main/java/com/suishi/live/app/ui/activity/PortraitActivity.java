package com.suishi.live.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageButton;

import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.suishi.live.apps.R;
import com.suishi.live.app.widgets.MultiToggleImageButton;
import com.suishi.sslive.ui.LiveManager;
import com.suishi.sslive.widgets.CameraGlSurfaceView;
import com.suishi.sslive.widgets.FilterGLSurfaceView;

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
        filter= findViewById(R.id.surface);
        mLiveManager=new LiveManager(this,filter);
        initViews();
        initListeners();
    }

    /**
     */
    @Override
    protected void onResume() {
        super.onResume();
      //  filter.resume();

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
        mMicBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
             //   mLFLiveView.mute(true);
            }
        });
        mFlashBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
              //  mLFLiveView.switchTorch();
            }
        });
        mFaceBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
              //  mLFLiveView.switchCamera();
                mLiveManager.switchCamera();
            }
        });
        mBeautyBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
                isGray = !isGray;
            }
        });
        mFocusBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
               // mLFLiveView.switchFocusMode();
            }
        });
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording) {
                    mRecordBtn.setBackgroundResource(R.mipmap.ic_record_start);
                    mLiveManager.onStopStream();
                    isRecording = false;
                } else {
                    mRecordBtn.setBackgroundResource(R.mipmap.ic_record_stop);
                    mLiveManager.onStartStream();
                    isRecording = true;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
