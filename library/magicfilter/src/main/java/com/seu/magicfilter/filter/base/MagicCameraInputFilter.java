package com.seu.magicfilter.filter.base;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;

/**
 * 绘制纹理到屏幕
 *
 * @author Created by jz on 2017/5/2 17:53
 */
public class MagicCameraInputFilter extends GPUImageFilter {

    /**
     * 片段着色器
     */
    String FRAGMENT_SHADER = "" +
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
    String VERTEX_SHADER = "" +
            "attribute vec4 vPosition;" +
            "attribute vec2 inputTextureCoordinate;" +
            "varying vec2 textureCoordinate;" +
            "void main()" +
            "{" +
            "gl_Position = vPosition;" +
            "textureCoordinate = inputTextureCoordinate;" +
            "}";



    //这里的顶点着色器没有矩阵参数
    public MagicCameraInputFilter() {
        super(R.raw.default_vertex, R.raw.default_fragment);
    }

}