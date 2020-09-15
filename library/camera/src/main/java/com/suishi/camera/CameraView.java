package com.suishi.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


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

public class CameraView extends SurfaceView  {

    private CameraDrawer mCameraDrawer;

    private boolean isSetParm = false;

    /**
     * 1 前置 0 后置
     */
    private int cameraId = 1;

    public CameraView(Context context) {
        this(context, null);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化OpenGL的相关信息
     */
    private void init() {
//        //设置版本
//        setEGLContextClientVersion(2);
//        //设置Renderer
//        setRenderer(this);
//        setRenderModeDirty();
//        //保存Context当pause时
//        setPreserveEGLContextOnPause(true);
//        //相机距离
//        setCameraDistance(100);
//        //初始化Camera的绘制类*/
//        mCameraDrawer = new CameraDrawer(getResources());

    }

    /**
     * 设置为主动渲染.
     */
    public void setRenderModeAuto() {
       // setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    /**
     * 设置为非主动渲染.
     */
    public void setRenderModeDirty() {
      //  setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


//    public void open(int cameraId) {
//        mCameraDrawer.setCameraId(cameraId);
//        SurfaceTexture texture = mCameraDrawer.getTexture();
//        texture.setOnFrameAvailableListener(this);
//    }



    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener) {
        mCameraDrawer.setOnFilterChangeListener(listener);
    }


}