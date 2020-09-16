package com.suishi.camera.camera.drawer;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


import com.suishi.camera.R;
import com.suishi.camera.camera.filter.AFilter;
import com.suishi.camera.camera.filter.CameraFilter;
import com.suishi.camera.camera.filter.GroupFilter;
import com.suishi.camera.camera.filter.NoFilter;
import com.suishi.camera.camera.filter.ProcessFilter;
import com.suishi.camera.camera.filter.WaterMarkFilter;
import com.suishi.camera.camera.gpufilter.SlideGpuFilterGroup;
import com.suishi.camera.camera.gpufilter.filter.MagicBeautyFilter;
import com.suishi.camera.camera.record.video.TextureMovieEncoder;
import com.suishi.camera.camera.utils.EasyGlUtils;
import com.suishi.camera.camera.utils.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cj on 2017/8/2.
 * desc 管理图像绘制的类
 * 主要用于管理各种滤镜、画面旋转、视频编码录制等
 */
public class GLRender implements GLSurfaceView.Renderer {

    private float[] OM;
    /**
     * 显示画面的filter
     */
    private final AFilter showFilter;
    /**
     * 后台绘制的filter
     */
    private final AFilter drawFilter;
    /**
     * 绘制水印的filter组
     */
    private final GroupFilter mBeFilter;
    private final GroupFilter mAfFilter;
    /**
     * 用于绘制美白效果的filter
     */
    private AFilter mProcessFilter;
    /**
     * 美白的filter
     */
    private MagicBeautyFilter mBeautyFilter;
//    private MagicAntiqueFilter mBeautyFilter;
    /**
     * 多种滤镜切换
     */
    private SlideGpuFilterGroup mSlideFilterGroup;

    private SurfaceTexture mSurfaceTextrue;
    /**
     * 预览数据的宽高
     */
    private int mPreviewWidth = 0, mPreviewHeight = 0;
    /**
     * 控件的宽高
     */
    private int width = 0, height = 0;

    private int textureID;
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    private float[] SM = new float[16];     //用于显示的变换矩阵


    public GLRender(Resources resources) {
        //初始化一个滤镜 也可以叫控制器
        showFilter = new NoFilter(resources);
        drawFilter = new CameraFilter(resources);

        mProcessFilter = new ProcessFilter(resources);
        mBeFilter = new GroupFilter(resources);
        mAfFilter = new GroupFilter(resources);
        mBeautyFilter = new MagicBeautyFilter();
//        mBeautyFilter = new MagicAntiqueFilter();
        mSlideFilterGroup = new SlideGpuFilterGroup();


        //必须传入上下翻转的矩阵
        OM = MatrixUtils.getOriginalMatrix();
        MatrixUtils.flip(OM, false, true);//矩阵上下翻转
         //水印
        WaterMarkFilter waterMarkFilter = new WaterMarkFilter(resources);
        waterMarkFilter.setWaterMark(BitmapFactory.decodeResource(resources, R.mipmap.watermark));
        waterMarkFilter.setPosition(30, 50, 0, 0);
        addFilter(waterMarkFilter);
    }

    private void addFilter(AFilter filter) {
        /**抵消本身的颠倒操作*/
//        filter.setMatrix(OM);
        mBeFilter.addFilter(filter);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        textureID = createTextureID();
        mSurfaceTextrue = new SurfaceTexture(textureID);

        drawFilter.create();
        drawFilter.setTextureId(textureID);

        mProcessFilter.create();
        showFilter.create();
//        showFilter.setTextureId(textureID);
        mBeFilter.create();
        mAfFilter.create();
        mBeautyFilter.ifNeedInit();
        mSlideFilterGroup.init();
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        width = i;
        height = i1;
        //清除遗留的
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
        /**创建一个帧染缓冲区对象*/
        GLES20.glGenFramebuffers(1, fFrame, 0);
        /**根据纹理数量 返回的纹理索引*/
        GLES20.glGenTextures(1, fTexture, 0);
       /* GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                height);*/
        /**将生产的纹理名称和对应纹理进行绑定*/
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
        /**根据指定的参数 生产一个2D的纹理 调用该函数前  必须调用glBindTexture以指定要操作的纹理*/
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mPreviewWidth, mPreviewHeight,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        useTexParameter();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        mProcessFilter.setSize(mPreviewWidth, mPreviewHeight);
        mBeFilter.setSize(mPreviewWidth, mPreviewHeight);
        mAfFilter.setSize(mPreviewWidth, mPreviewHeight);
        drawFilter.setSize(mPreviewWidth, mPreviewHeight);
        mBeautyFilter.onDisplaySizeChanged(mPreviewWidth, mPreviewHeight);
        mBeautyFilter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
        mSlideFilterGroup.onSizeChanged(mPreviewWidth, mPreviewHeight);

        MatrixUtils.getShowMatrix(SM, mPreviewWidth, mPreviewHeight, width, height);
        showFilter.setMatrix(SM);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        /**更新界面中的数据*/
        mSurfaceTextrue.updateTexImage();

        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        GLES20.glViewport(0, 0, mPreviewWidth, mPreviewHeight);
        drawFilter.draw();
        EasyGlUtils.unBindFrameBuffer();

        mBeFilter.setTextureId(fTexture[0]);
        mBeFilter.draw();
        if (mBeautyFilter != null && mBeautyFilter.getBeautyLevel() != 0) {
            EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
            GLES20.glViewport(0, 0, mPreviewWidth, mPreviewHeight);
           // mBeautyFilter.onDrawFrame(mBeFilter.getOutputTexture());
            EasyGlUtils.unBindFrameBuffer();
            mProcessFilter.setTextureId(fTexture[0]);
        } else {
            mProcessFilter.setTextureId(mBeFilter.getOutputTexture());
        }
        mProcessFilter.draw();

        mSlideFilterGroup.onDrawFrame(mProcessFilter.getOutputTexture());

        mAfFilter.setTextureId(mSlideFilterGroup.getOutputTexture());
        mAfFilter.draw();

        /**绘制显示的filter*/
        GLES20.glViewport(0, 0, width, height);
        showFilter.setTextureId(mAfFilter.getOutputTexture());
        showFilter.draw();
    }

    /**
     * 触摸事件的传递
     */
    public void onTouch(MotionEvent event) {
        mSlideFilterGroup.onTouchEvent(event);
    }

    /**
     * 滤镜切换的事件监听
     */
    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener) {
        mSlideFilterGroup.setOnFilterChangeListener(listener);
    }

    /**
     * 设置预览效果的size
     */
    public void setPreviewSize(int width, int height) {
        if (mPreviewWidth != width || mPreviewHeight != height) {
            mPreviewWidth = width;
            mPreviewHeight = height;
        }
    }

    /**
     * 提供修改美白等级的接口
     */
    public void changeBeautyLevel(int level) {
        mBeautyFilter.setBeautyLevel(level);
    }

    public int getBeautyLevel() {
        return mBeautyFilter.getBeautyLevel();
    }

    /**
     * 根据摄像头设置纹理映射坐标
     */
    public void setCameraId(int id) {
        drawFilter.setFlag(id);
    }

    public SurfaceTexture getTexture() {
        return mSurfaceTextrue;
    }

    /**
     * 创建显示的texture
     */
    private int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public void useTexParameter() {
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }
}
