package com.suishi.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Size;
import android.view.SurfaceHolder;

import com.seu.magicfilter.filter.base.gpuimage.GPUImageColorBalanceFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.suishi.camera.camera.drawer.VideoDrawer;



/**
 */
public class CameraView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener  {

    private VideoDrawer mCameraDrawer;

    private Size mSize;
    /**
     *
     */
    private SurfaceHolder.Callback mCallback=null;

    private GPUImageFilter filter=new GPUImageColorBalanceFilter();

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
        //设置版本
        setEGLContextClientVersion(2);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        //保存Context当pause时
        setPreserveEGLContextOnPause(true);
        //设置Renderer
        mCameraDrawer = new VideoDrawer(filter);
        setRenderer( mCameraDrawer);
        setRenderModeDirty();
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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

    /**
     * 每次Activity onResume时被调用,第一次不会打开相机
     */
    @Override
    public void onResume() {
        super.onResume();
    }


    public void addCallBack2(SurfaceHolder.Callback callback){
        this.mCallback=callback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mCameraDrawer.createSurfaceTexture(this);
        if(mCallback!=null) {
            this.mCallback.surfaceCreated(holder);
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        if(mCallback!=null) {
            this.mCallback.surfaceChanged(holder,format,w,h);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        if(mCallback!=null) {
            this.mCallback.surfaceDestroyed(holder);
        }
    }


    public void setSize(Size size){
        this.mSize=size;
        mCameraDrawer.setPreviewSize(size);
    }

    public VideoDrawer getRender(){
        return mCameraDrawer;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }
}