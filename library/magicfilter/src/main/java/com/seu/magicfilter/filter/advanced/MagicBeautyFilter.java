package com.seu.magicfilter.filter.advanced;

import android.opengl.GLES20;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

public class MagicBeautyFilter extends GPUImageFilter {
    private int mSingleStepOffsetLocation;

    public MagicBeautyFilter() {
        super(OpenGlUtils.readShaderFromRawResource(R.raw.default_vertex),OpenGlUtils.readShaderFromRawResource(R.raw.beauty_new));
    }

    public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        setFloatVec2(mSingleStepOffsetLocation, new float[]{2.0f / width, 2.0f / height});
    }


}
