package com.seu.magicfilter.filter.advanced;

import android.opengl.GLES20;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

public class MagicFreudFilter extends GPUImageFilter {
	private int mTexelHeightUniformLocation;
    private int mTexelWidthUniformLocation;
	private int[] inputTextureHandles = {-1};
	private int[] inputTextureUniformLocations = {-1};
	private int mGLStrengthLocation;

	public MagicFreudFilter(){
		super(OpenGlUtils.readShaderFromRawResource(R.raw.default_vertex),OpenGlUtils.readShaderFromRawResource(R.raw.freud));
	}
	
	public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
        	inputTextureHandles[i] = -1;
    }
	
	protected void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	protected void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
		}
	}
	
	public void onInit(){
		super.onInit();
		inputTextureUniformLocations[0] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
		
		mTexelWidthUniformLocation = GLES20.glGetUniformLocation(getProgram(), "inputImageTextureWidth");
        mTexelHeightUniformLocation = GLES20.glGetUniformLocation(getProgram(), "inputImageTextureHeight");

		mGLStrengthLocation = GLES20.glGetUniformLocation(mGLProgId,
				"strength");
	}
	
	public void onInitialized(){
		super.onInitialized();
		setFloat(mGLStrengthLocation, 1.0f);
	    runOnDraw(new Runnable(){
		    public void run(){
                inputTextureHandles[0] = OpenGlUtils.loadTexture("filter/freud_rand.png");
		    }
	    });
	}
	
	public void onInputSizeChanged(int width, int height) {
        super.onInputSizeChanged(width, height);
        GLES20.glUniform1f(mTexelWidthUniformLocation, (float)width);
        GLES20.glUniform1f(mTexelHeightUniformLocation, (float)height);
    }
}