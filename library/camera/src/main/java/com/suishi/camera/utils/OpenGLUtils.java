package com.suishi.camera.utils;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLUtils {

    private static final String TAG = "OpenGLUtils";

    public static int getExternalOESTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public static int loadShader(int type, String source) throws Exception {
        // 1. create shader
        int shader = GLES20.glCreateShader(type);
        if (shader == GLES20.GL_NONE) {
            throw new Exception("create shared failed! type: " + type);
        }
        // 2. load shader source
        GLES20.glShaderSource(shader, source);
        // 3. compile shared source
        GLES20.glCompileShader(shader);
        // 4. check compile status
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == GLES20.GL_FALSE) { // compile failed
            Log.e(TAG, "Error compiling shader. type: " + type + ":");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader); // delete shader
            throw new Exception("Error compiling shader. type: " + type + ":"+GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        try {
            // 1. load shader
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            // 2. create gl program
            int program = GLES20.glCreateProgram();
            if (program == GLES20.GL_NONE) {
                throw new Exception("create program failed! ");
            }
            // 3. attach shader
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            // we can delete shader after attach
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragmentShader);
            // 4. link program
            GLES20.glLinkProgram(program);
            // 5. check link status
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == GLES20.GL_FALSE) { // link failed
                GLES20.glDeleteProgram(program); // delete program
                throw new Exception("Error link program: "+GLES20.glGetProgramInfoLog(program));
            }
            return program;
        }catch (Exception e){
          throw new IllegalStateException(e);
        }

    }

    public static String loadFromAssets(String fileName, Resources resources) {
        String result = null;
        try {
            InputStream is = resources.getAssets().open(fileName);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            is.close();
            result = new String(data, "UTF-8");
            result = result.replace("\\r\\n", "\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
