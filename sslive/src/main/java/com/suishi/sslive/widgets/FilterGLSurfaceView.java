package com.suishi.sslive.widgets;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.camera.utils.CameraInfo;
import com.seu.magicfilter.filter.advanced.MagicWhiteCatFilter;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.base.MagicRecordFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.helper.MagicFilterFactory;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;
import com.seu.magicfilter.widget.base.MagicBaseView;
import com.suishi.sslive.mode.engine.video.VideoConfig;
import com.suishi.sslive.mode.engine.video.VideoManager;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 特效层
 * Created by admin on 2018/3/8.
 */

public class FilterGLSurfaceView extends GLSurfaceViewWithCamera {
    /**
     * 所选择的滤镜，类型为MagicBaseGroupFilter
     * 1.mDefaultFilter将SurfaceTexture中YUV数据绘制到FrameBuffer
     * 2.filter将FrameBuffer中的纹理绘制到屏幕中
     */
    protected GPUImageFilter mFilter;


    private MagicRecordFilter mRecordFilter;//绘制到FBO
    /**
     * 默认渲染模式
     */
    private MagicCameraInputFilter mDefaultFilter;

    private final FloatBuffer mRecordCubeBuffer;//顶点坐标
    private final FloatBuffer mRecordTextureBuffer;//纹理坐标
    /**
     */
    public FilterGLSurfaceView(Context context) {
        this(context,null);
    }

    /**
     */
    public FilterGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleType = MagicBaseView.ScaleType.CENTER_CROP;

        mRecordCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mRecordTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

    }

    /**
     * 创建surface
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        if(mDefaultFilter == null)
            mDefaultFilter = new MagicCameraInputFilter();
        mDefaultFilter.init(this.getContext());

        if (mFilter == null) {
            mFilter = new GPUImageFilter();
            mFilter.init(getContext());
        }

        if (mRecordFilter == null) {
            mRecordFilter = new MagicRecordFilter();
            mRecordFilter.init(getContext());
            mRecordFilter.setRecordListener(VideoManager.instance());
        }
    }

    /**
     * 视图变化
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        onFilterChanged();
        CameraInfo info = CameraEngine.getCameraInfo();
        adjustSize(info.orientation, info.isFront, !info.isFront);
        //重新计算录制顶点、纹理坐标
        float[][] data = adjustSize(VideoConfig.width, VideoConfig.height, info.orientation,
                info.isFront, !info.isFront);
        mRecordCubeBuffer.clear();
        mRecordCubeBuffer.put(data[0]).position(0);
        mRecordTextureBuffer.clear();
        mRecordTextureBuffer.put(data[1]).position(0);
    }

    /**
     * gl绘制
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (mSurfaceTexture == null) return;
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);
        //先将纹理绘制到fbo同时过滤镜
        mFilter.setTextureTransformMatrix(mtx);
        int id = mFilter.onDrawToTexture(mSurfaceTextureId);

        //绘制到屏幕上
        mDefaultFilter.onDrawFrame(id,  gLCubeBuffer, gLTextureBuffer);

        //绘制到另一个fbo上，同时使用pbo获取数据
        mRecordFilter.onDrawToFbo(id, mRecordCubeBuffer, mRecordTextureBuffer,
                mSurfaceTexture.getTimestamp());
    }

    public void setFilter(final MagicFilterType type){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mFilter != null)
                    mFilter.destroy();
                mFilter = null;
                mFilter = MagicFilterFactory.initFilters(type);
                if (mFilter != null)
                    mFilter.init(FilterGLSurfaceView.this.getContext());
                onFilterChanged();
            }
        });
        requestRender();
    }

    protected void onFilterChanged(){
        if (mFilter != null) {
            mFilter.initFrameBuffer(mSurfaceWidth, mSurfaceHeight);
            mFilter.onInputSizeChanged(mSurfaceWidth, mSurfaceHeight);
            mFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
        }


        mDefaultFilter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
        mDefaultFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);

        //初始化fbo，pbo
        mRecordFilter.initFrameBuffer(VideoConfig.width, VideoConfig.height);
        mRecordFilter.initPixelBuffer(VideoConfig.width, VideoConfig.height);
        mRecordFilter.onInputSizeChanged(VideoConfig.width, VideoConfig.height);
        mRecordFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
    }
}
