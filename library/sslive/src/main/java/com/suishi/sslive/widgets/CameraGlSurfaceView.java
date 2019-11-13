package com.suishi.sslive.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.suishi.sslive.mode.engine.camera.interfaces.OnFocusListener;
import com.suishi.sslive.mode.engine.camera.interfaces.OnRecordListener;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.base.MagicRecordFilter;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.TextureRotationUtil;
import com.suishi.sslive.mode.engine.camera.CameraHelper;
import com.suishi.sslive.mode.engine.video.VideoManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * CameraGlSurfaceView
 *
 * @author Created by jz on 2017/5/2 11:21
 */
public class CameraGlSurfaceView extends BaseGlSurfaceView implements GLSurfaceView.Renderer,
        Camera.AutoFocusCallback {

    public static final int RECORD_WIDTH = 480, RECORD_HEIGHT = 640;
    //顶点坐标
    private final FloatBuffer mRecordCubeBuffer;
    //纹理坐标
    private final FloatBuffer mRecordTextureBuffer;
    //绘制到屏幕上
    private MagicCameraInputFilter mCameraInputFilter;
    //绘制到FBO
    private MagicRecordFilter mRecordFilter;
    //surface纹理
    private SurfaceTexture mSurfaceTexture;

    private CameraHelper mCameraHelper;

   // private ThreadHelper mThreadHelper;

    private OnFocusListener mOnFocusListener;

    public CameraGlSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCameraHelper =CameraHelper.getInstance();
     //   mThreadHelper = new ThreadHelper();

        mScaleType = CENTER_CROP;

        mRecordCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mRecordTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        if (mCameraInputFilter == null) {
            mCameraInputFilter = new MagicCameraInputFilter();
            mCameraInputFilter.init();
        }
        if (mRecordFilter == null) {
            mRecordFilter = new MagicRecordFilter();
            mRecordFilter.init();
            mRecordFilter.setRecordListener(VideoManager.instance());
        }

        if (mTextureId == OpenGlUtils.NO_TEXTURE) {
            mTextureId = OpenGlUtils.getExternalOESTextureID();
            if (mTextureId != OpenGlUtils.NO_TEXTURE) {
                mSurfaceTexture = new SurfaceTexture(mTextureId);
                mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        requestRender();
                    }
                });
            }
        }
        mCameraHelper.startPreview(mSurfaceTexture);
        boolean rel = mCameraHelper.openCamera();
        if (rel) {
            review();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

        CameraHelper.CameraItem info = mCameraHelper.getCameraAngleInfo();
        adjustSize(info.orientation, info.isFront, !info.isFront);

        //重新计算录制顶点、纹理坐标
        float[][] data = adjustSize(mRecordWidth, mRecordHeight, info.orientation,
                info.isFront, !info.isFront);
        mRecordCubeBuffer.clear();
        mRecordCubeBuffer.put(data[0]).position(0);
        mRecordTextureBuffer.clear();
        mRecordTextureBuffer.put(data[1]).position(0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (mSurfaceTexture == null)
            return;
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);

        //先将纹理绘制到fbo同时过滤镜
        mFilter.setTextureTransformMatrix(mtx);
        int id = mFilter.onDrawToTexture(mTextureId);

        //绘制到屏幕上
        mCameraInputFilter.onDrawFrame(id, mGLCubeBuffer, mGLTextureBuffer);

        //绘制到另一个fbo上，同时使用pbo获取数据
        mRecordFilter.onDrawToFbo(id, mRecordCubeBuffer, mRecordTextureBuffer,
               mSurfaceTexture.getTimestamp());
    }

    @Override
    protected void onFilterChanged() {
        super.onFilterChanged();

        mCameraInputFilter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
        mCameraInputFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);

        //初始化fbo，pbo
        mRecordFilter.initFrameBuffer(mRecordWidth, mRecordHeight);
        mRecordFilter.initPixelBuffer(mRecordWidth, mRecordHeight);
        mRecordFilter.onInputSizeChanged(mRecordWidth, mRecordHeight);
        mRecordFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (mOnFocusListener != null)
            mOnFocusListener.onFocusEnd();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && mSurfaceWidth > 0 && mSurfaceHeight > 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (mOnFocusListener != null)
                mOnFocusListener.onFocusStart(x, y);
            int centerX = (x - mSurfaceWidth / 2) * 1000 / (mSurfaceWidth / 2);
            int centerY = (y - mSurfaceHeight / 2) * 1000 / (mSurfaceHeight / 2);
            mCameraHelper.selectCameraFocus(new Rect(centerX - 100, centerY - 100, centerX + 100, centerY + 100), this);
        }
        return true;
    }

    //调整view大小
    private void review() {
        mPreviewWidth = mCameraHelper.getPreviewWidth();
        mPreviewHeight = mCameraHelper.getPreviewHeight();
        mRecordWidth = mCameraHelper.getRecordWidth();
        mRecordHeight = mCameraHelper.getRecordHeight();
    }

    /**
     * 恢复摄像头，对应Activity生命周期
     */
    public boolean resume() {
        boolean rel = mCameraHelper.openCamera();
        if (rel) {
            review();
            if (mSurfaceTexture != null)
                mCameraHelper.startPreview();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 暂停摄像头，对应Activity生命周期
     */
    public void pause() {
        mCameraHelper.stopCamera();
    }

    /**
     * 停止摄像头，对应Activity的onDestroy
     */
    public void stop() {
        mCameraHelper.stopCamera();

        mFilter.destroy();
        mCameraInputFilter.destroy();
        mRecordFilter.destroy();
    }

    /**
     * 获得摄像头
     */
    public CameraHelper getCamera() {
        return mCameraHelper;
    }

    /**
     * 获得摄像头数量
     */
    public int getCameraCount() {
        return Camera.getNumberOfCameras();
    }

    /**
     * 当前是否前置摄像头
     */
    public boolean isFrontCamera() {
        return mCameraHelper.isFrontCamera();
    }

    /**
     * 返回录制宽度
     */
    public int getRecordWidth() {
        return mRecordWidth;
    }

    /**
     * 返回录制高度
     */
    public int getRecordHeight() {
        return mRecordHeight;
    }


    /**
     * 设置摄像头焦点回调
     *
     * @param l 回调
     */
    public void setOnFocusListener(OnFocusListener l) {
        this.mOnFocusListener = l;
    }


}
