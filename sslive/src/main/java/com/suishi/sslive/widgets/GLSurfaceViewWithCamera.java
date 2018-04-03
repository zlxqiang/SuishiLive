package com.suishi.sslive.widgets;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;
import com.seu.magicfilter.widget.base.MagicBaseView;
import com.suishi.sslive.mode.engine.video.VideoManager;
import com.suishi.sslive.utils.LiveLog;
import com.suishi.sslive.utils.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 源数据层 GLSurfaceView 和 Camera组合
 * Created by admin on 2018/3/8.
 */

public abstract class GLSurfaceViewWithCamera extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{
    /**
     * GLSurfaceView的宽高
     */
    protected int mSurfaceWidth, mSurfaceHeight;
    /**
     * 纹理id
     */
    protected int mSurfaceTextureId = -1;

    /**
     * 纹理对象
     */
    protected SurfaceTexture mSurfaceTexture;

    /**
     * 主线程消息
     */
    private Handler mHandler = new Handler();

    protected int mPreviewWidth;//摄像头宽度
    protected int mPreviewHeight;//摄像头宽度



    protected MagicBaseView.ScaleType scaleType = MagicBaseView.ScaleType.FIT_XY;
    /**
     * 顶点坐标
     */
    protected final FloatBuffer gLCubeBuffer;

    /**
     * 纹理坐标
     */
    protected final FloatBuffer gLTextureBuffer;

    /**
     */
    public GLSurfaceViewWithCamera(Context context) {
        this(context, null);
    }

    /**
     */
    public GLSurfaceViewWithCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().addCallback(this);
        gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

    }

    /**
     * 创建surface
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mSurfaceTextureId = textures[0];
        mSurfaceTexture = new SurfaceTexture(mSurfaceTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        if(CameraEngine.getCamera() == null)
            CameraEngine.openCamera();
        VideoManager.instance().setSurface(mSurfaceTexture);

        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
    }

    /**
     * 视图变化
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }
    /**
     * gl绘制
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        LiveLog.d(getClass().getSimpleName(), "SurfaceView destroy");
        mHandler.removeCallbacksAndMessages(null);
        CameraEngine.releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        LiveLog.d(getClass().getSimpleName(), "SurfaceView created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
        LiveLog.d(getClass().getSimpleName(), "SurfaceView width:" + width + " height:" + height);
    }


    /**
     * 纹理绘制完成
     *
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    /**
     */
    @Override
    public void onPause() {
        CameraEngine.releaseCamera();
        super.onPause();
    }

    /**
     */
    @Override
    public void onResume() {

        super.onResume();
    }

    protected void adjustSize(int rotation, boolean flipHorizontal, boolean flipVertical) {
        float[][] data = adjustSize(mSurfaceWidth, mSurfaceHeight, rotation,
                flipHorizontal, flipVertical);

        gLCubeBuffer.clear();
        gLCubeBuffer.put(data[0]).position(0);
        gLTextureBuffer.clear();
        gLTextureBuffer.put(data[1]).position(0);
    }

    protected float[][] adjustSize(int width, int height, int rotation, boolean flipHorizontal, boolean flipVertical){
        float[] textureCords = TextureRotationUtil.getRotation(Rotation.fromInt(rotation),
                flipHorizontal, flipVertical);
        float[] cube = TextureRotationUtil.CUBE;
        float ratio1 = (float)width / mPreviewWidth;
        float ratio2 = (float)height / mPreviewHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mPreviewWidth * ratioMax);
        int imageHeightNew = Math.round(mPreviewHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float)width;
        float ratioHeight = imageHeightNew / (float)height;

        if(scaleType == MagicBaseView.ScaleType.CENTER_INSIDE){
            cube = new float[]{
                    TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                    TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                    TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                    TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
            };
        }else if(scaleType == MagicBaseView.ScaleType.FIT_XY){

        }else if(scaleType == MagicBaseView.ScaleType.CENTER_CROP){
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distVertical), addDistance(textureCords[1], distHorizontal),
                    addDistance(textureCords[2], distVertical), addDistance(textureCords[3], distHorizontal),
                    addDistance(textureCords[4], distVertical), addDistance(textureCords[5], distHorizontal),
                    addDistance(textureCords[6], distVertical), addDistance(textureCords[7], distHorizontal),
            };
        }
        return new float[][]{cube, textureCords};
    }


    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

}
