package com.seu.magicfilter.filter.advanced;

import android.opengl.GLES20;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

public class MagicSketchFilter extends GPUImageFilter {

    private int mSingleStepOffsetLocation;
    //0.0 - 1.0
    private int mStrengthLocation;

    public MagicSketchFilter() {
        super(OpenGlUtils.readShaderFromRawResource(R.raw.default_vertex),OpenGlUtils.readShaderFromRawResource(R.raw.sketch));
    }

    public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[]{1.0f / w, 1.0f / h});
    }

    public void onInitialized() {
        super.onInitialized();
        setFloat(mStrengthLocation, 0.5f);
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
