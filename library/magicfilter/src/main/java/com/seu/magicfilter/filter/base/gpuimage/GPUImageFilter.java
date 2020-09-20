package com.seu.magicfilter.filter.base.gpuimage;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.seu.magicfilter.R;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

public class GPUImageFilter {

    /**
     * 片段着色器
     */
    public static final String FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;" +
            "varying vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES s_texture;\n" +
            "void main() {" +
            "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
            "}";
    /**
     * 顶点着色器
     */
    public static final String VERTEX_SHADER = "" +
            "attribute vec4 position;" +
            "attribute vec2 inputTextureCoordinate;" +
            "varying vec2 textureCoordinate;" +
            "void main()" +
            "{" +
            "gl_Position = position;" +
            "textureCoordinate = inputTextureCoordinate;" +
            "}";

    private final LinkedList<Runnable> mRunOnDraw;
    private final String mVertexShader;
    private final String mFragmentShader;

    protected int mGLProgramId;
    protected int mGLAttributePosition;
    protected int mGLUniformTexture;
    protected int mGLAttributeTextureCoordinate;

    /**
     * 预览尺寸
     */
    protected int mInputWidth;
    protected int mInputHeight;

    protected boolean mIsInitialized;
    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;

    /**
     * 屏幕尺寸
     */
    protected int mOutputWidth, mOutputHeight;

    protected float[] mTextureTransformMatrix;
    protected int mTextureTransformMatrixLocation;

    protected int[] mFrameBuffers = null;
    protected int[] mFrameBufferTextures = null;

    public GPUImageFilter() {
        this(R.raw.default_fragment);
    }

    public GPUImageFilter(int fragmentShaderId) {
        this(R.raw.default_vertex, fragmentShaderId);
    }

    public GPUImageFilter(int vertexShaderId, int fragmentShaderId) {
        this.mVertexShader = OpenGlUtils.readShaderFromRawResource(vertexShaderId);
        this.mFragmentShader =OpenGlUtils.readShaderFromRawResource(fragmentShaderId);

        mRunOnDraw = new LinkedList<>();

        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
    }

    public GPUImageFilter(String vertexShader, String fragmentShader) {
        this.mVertexShader =vertexShader;
        this.mFragmentShader =fragmentShader;

        mRunOnDraw = new LinkedList<>();

        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
    }

    public void init() {
        onInit();
        onInitialized();
    }

    protected void onInit() {
        mGLProgramId = OpenGlUtils.loadProgram(mVertexShader,
                mFragmentShader);
        mGLAttributePosition = GLES20.glGetAttribLocation(mGLProgramId, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgramId, "inputImageTexture");
        mGLAttributeTextureCoordinate = GLES20.glGetAttribLocation(mGLProgramId,
                "inputTextureCoordinate");
        mTextureTransformMatrixLocation = GLES20.glGetUniformLocation(mGLProgramId, "textureTransform");
        mIsInitialized = true;
    }

    protected void onInitialized() {

    }

    public void ifNeedInit(){
        if(!mIsInitialized) init();
    }

    public final void destroy() {
        mIsInitialized = false;
        GLES20.glDeleteProgram(mGLProgramId);
        onDestroy();
    }

    protected void onDestroy() {
        destroyFrameBuffers();
    }

    public void onInputSizeChanged(final int width, final int height) {
        mInputWidth = width;
        mInputHeight = height;
    }

    public void setTextureTransformMatrix(float[] mtx) {
        mTextureTransformMatrix = mtx;
    }

    public void initFrameBuffer(int width, int height) {
        if (mFrameBuffers != null && (mInputWidth != width || mInputHeight != height)) {
            destroyFrameBuffers();
        }
        if (mFrameBuffers != null) {
            return;
        }

        mFrameBuffers = new int[1];
        mFrameBufferTextures = new int[1];

        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void destroyFrameBuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    public int onDrawFrame(final int textureId, final FloatBuffer cubeBuffer,
                           final FloatBuffer textureBuffer) {
        if (!mIsInitialized) {
            return OpenGlUtils.NOT_INIT;
        }

        GLES20.glUseProgram(mGLProgramId);
        runPendingOnDrawTasks();
        //
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttributePosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttributePosition);
        //
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttributeTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttributeTextureCoordinate);

        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttributePosition);
        GLES20.glDisableVertexAttribArray(mGLAttributeTextureCoordinate);
        onDrawArraysAfter();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return OpenGlUtils.ON_DRAWN;
    }

    public int onDrawFrame(final int textureId) {
        if (!mIsInitialized) {
            return OpenGlUtils.NOT_INIT;
        }

        GLES20.glUseProgram(mGLProgramId);
        runPendingOnDrawTasks();

        mGLCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttributePosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttributePosition);
        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttributeTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttributeTextureCoordinate);

        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttributePosition);
        GLES20.glDisableVertexAttribArray(mGLAttributeTextureCoordinate);
        onDrawArraysAfter();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return OpenGlUtils.ON_DRAWN;
    }

    //绘制到fbo，注意GL_TEXTURE_EXTERNAL_OES，这里使用SurfaceTexture纹理
    public int onDrawToTexture(final int textureId) {
        if (mFrameBuffers == null) {
            return OpenGlUtils.NO_TEXTURE;
        }
        if (!isInitialized()) {
            return OpenGlUtils.NOT_INIT;
        }

        GLES20.glViewport(0, 0, mInputWidth, mInputHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glUseProgram(mGLProgramId);
        runPendingOnDrawTasks();

        mGLCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttributePosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttributePosition);
        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttributeTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttributeTextureCoordinate);

        GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0);

        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttributePosition);
        GLES20.glDisableVertexAttribArray(mGLAttributeTextureCoordinate);
        onDrawArraysAfter();

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        return mFrameBufferTextures[0];
    }

    protected void onDrawArraysPre() {
    }

    protected void onDrawArraysAfter() {
    }

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public int getInputWidth() {
        return mInputWidth;
    }

    public int getInputHeight() {
        return mInputHeight;
    }

    public int getProgram() {
        return mGLProgramId;
    }

    public int getAttributePosition() {
        return mGLAttributePosition;
    }

    public int getAttributeTextureCoordinate() {
        return mGLAttributeTextureCoordinate;
    }

    public int getUniformTexture() {
        return mGLUniformTexture;
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    public void onDisplaySizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }
}
