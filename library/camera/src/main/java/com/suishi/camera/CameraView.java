package com.suishi.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


import com.suishi.camera.camera.CameraController;
import com.suishi.camera.camera.drawer.CameraDrawer;
import com.suishi.camera.camera.gpufilter.SlideGpuFilterGroup;
import com.suishi.camera.player.MPlayer;
import com.suishi.camera.player.MinimalDisplay;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by weight68kg on 2018/5/8.
 */

public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private Context mContext;

    private CameraDrawer mCameraDrawer;
    private CameraController mCamera;

    private int dataWidth = 0, dataHeight = 0;

    private boolean isSetParm = false;

    /**
     * 1 前置 0 后置
     */
    private int cameraId = 1;

    public CameraView(Context context) {
        this(context, null);
        mContext = context;
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 初始化OpenGL的相关信息
     */
    private void init() {
        //设置版本
        setEGLContextClientVersion(2);
        //设置Renderer
        setRenderer(this);
        setRenderModeDirty();
        //保存Context当pause时
        setPreserveEGLContextOnPause(true);
        //相机距离
        setCameraDistance(100);
        //初始化Camera的绘制类*/
        mCameraDrawer = new CameraDrawer(getResources());
        //初始化相机的管理类*/
        mCamera = new CameraController();


    }

    /**
     * 设置为主动渲染.
     */
    public void setRenderModeAuto() {
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    /**
     * 设置为非主动渲染.
     */
    public void setRenderModeDirty() {
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    public void open(int cameraId) {
        mCamera.close();
        mCamera.open(cameraId);
        mCameraDrawer.setCameraId(cameraId);
        final Point previewSize = mCamera.getPreviewSize();
        dataWidth = previewSize.x;
        dataHeight = previewSize.y;
        SurfaceTexture texture = mCameraDrawer.getTexture();
        texture.setOnFrameAvailableListener(this);
        mCamera.setPreviewTexture(texture);
        mCamera.preview();
    }

    public void switchCamera() {
        cameraId = cameraId == 0 ? 1 : 0;
        open(cameraId);
    }

    public Camera getCamera() {
        return mCamera.getmCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer.onSurfaceCreated(gl, config);
        if (!isSetParm) {
            open(cameraId);
            stickerInit();
        }
        mCameraDrawer.setPreviewSize(dataWidth, dataHeight);


    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        super.surfaceRedrawNeeded(holder);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (isSetParm) {
            mCameraDrawer.onDrawFrame(gl);
        }


    }

    /**
     * 每次Activity onResume时被调用,第一次不会打开相机
     */
    @Override
    public void onResume() {
        super.onResume();
        if (isSetParm) {
            open(cameraId);
        }
    }

    public void onDestroy() {
        if (mCamera != null) {
            mCamera.close();
        }
    }

    /**
     * 摄像头聚焦
     */
    public void onFocus(Point point, Camera.AutoFocusCallback callback) {
        mCamera.onFocus(point, callback);
    }

    public int getCameraId() {
        return cameraId;
    }

    public int getBeautyLevel() {
        return mCameraDrawer.getBeautyLevel();
    }

    public void changeBeautyLevel(final int level) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.changeBeautyLevel(level);
            }
        });
    }

    public void startRecord() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.startRecord();
            }
        });
    }

    public void stopRecord() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.stopRecord();
            }
        });
    }

    public void setSavePath(String path) {
        mCameraDrawer.setSavePath(path);
    }

    public void resume(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onResume(auto);
            }
        });
    }

    public void pause(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onPause(auto);
            }
        });
    }

    public void takePicture(CameraController.TakePictureCallBack callBack) {
        mCamera.takePicture(callBack);
    }

    public void onTouch(final MotionEvent event) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onTouch(event);
            }
        });
    }

    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener) {
        mCameraDrawer.setOnFilterChangeListener(listener);
    }

    private void stickerInit() {
        if (!isSetParm && dataWidth > 0 && dataHeight > 0) {
            isSetParm = true;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.requestRender();
    }


    public void openLightOn() {
        mCamera.OpenLightOn();
    }

}