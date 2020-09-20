package com.seu.magicfilter.filter.advanced;

import android.opengl.GLES20;

import com.seu.magicfilter.filter.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

public class MagicAmaroFilter extends GPUImageFilter {

    public static final String Fragment=""+
    "#extension GL_OES_EGL_image_external : require\n"+
            "\n"+
     "precision mediump float;\n"+
            "\n"+
    "varying mediump vec2 textureCoordinate;\n"+
            "\n"+
     "uniform samplerExternalOES inputImageTexture;\n"+
     "uniform samplerExternalOES inputImageTexture2;\n"+
     "uniform samplerExternalOES inputImageTexture3;\n"+
     "uniform samplerExternalOES inputImageTexture4;\n"+
            "\n"+
            "uniform float strength;\n"+
            "\n"+
     "void main() {\n"+
        "vec4 originColor = texture2D(inputImageTexture, textureCoordinate);\n"+
        "vec4 texel = texture2D(inputImageTexture, textureCoordinate);\n"+
        "vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;\n"+
            "\n"+
        "texel.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;\n"+
        "texel.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;\n"+
        "texel.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;\n"+
            "\n"+
        "vec4 mapped;\n"+
        "mapped.r = texture2D(inputImageTexture4, vec2(texel.r, .16666)).r;\n"+
        "mapped.g = texture2D(inputImageTexture4, vec2(texel.g, .5)).g;\n"+
        "mapped.b = texture2D(inputImageTexture4, vec2(texel.b, .83333)).b;\n"+
        "mapped.a = 1.0;\n"+
            "\n"+
        "mapped.rgb = mix(originColor.rgb, mapped.rgb, strength);\n"+
         "\n"+
        "gl_FragColor = mapped;\n"+
    "}\n";


    private int[] inputTextureHandles = {-1, -1, -1};

    private int[] inputTextureUniformLocations = {-1, -1, -1};

    private int mGLStrengthLocation;

    public MagicAmaroFilter() {
        super(NO_FILTER_VERTEX_SHADER,Fragment);
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for (int i = 0; i < inputTextureHandles.length; i++)
            inputTextureHandles[i] = -1;
    }

    protected void onDrawArraysAfter() {
        for (int i = 0; i < inputTextureHandles.length
                && inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3));
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        }
    }

    protected void onDrawArraysPre() {
        for (int i = 0; i < inputTextureHandles.length
                && inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3));
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
            GLES20.glUniform1i(inputTextureUniformLocations[i], (i + 3));
        }
    }

    public void onInit() {
        super.onInit();
        for (int i = 0; i < inputTextureUniformLocations.length; i++)
            inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture" + (2 + i));
        mGLStrengthLocation = GLES20.glGetUniformLocation(mGLProgId,
                "strength");
    }

    public void onInitialized() {
        super.onInitialized();
        setFloat(mGLStrengthLocation, 1.0f);
        runOnDraw(new Runnable() {
            public void run() {
                inputTextureHandles[0] = OpenGlUtils.loadTexture("filter/brannan_blowout.png");
                inputTextureHandles[1] = OpenGlUtils.loadTexture("filter/overlaymap.png");
                inputTextureHandles[2] = OpenGlUtils.loadTexture("filter/inkwellmap.png");
            }
        });
    }
}
