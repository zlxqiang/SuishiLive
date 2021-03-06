package com.suishi.camera;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.suishi.utils.DensityUtils;


/**
 *create by GH  相机对焦控件
 */
@SuppressLint("AppCompatCustomView")
public class FocusImageView extends ImageView {
    /**
     *
     */
    public final static String TAG = "FocusImageView";
    /**
     *
     */
    private static final int NO_ID = -1;
    /**
     *
     */
    private int mFocusImg = NO_ID;
    /**
     *
     */
    private int mFocusSucceedImg = NO_ID;
    /**
     *
     */
    private int mFocusFailedImg = NO_ID;
    /**
     *
     */
    private Animation mAnimation;
    /**
     *
     */
    private Handler mHandler;

    /**
     *
     * @param context
     */
    public FocusImageView(Context context) {
        super(context);
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.focusview_show);
        setVisibility(View.GONE);
        mHandler = new Handler();
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public FocusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.focusview_show);
        mHandler = new Handler();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FocusImageView);
        mFocusImg = a.getResourceId(R.styleable.FocusImageView_focus_focusing_id, NO_ID);
        mFocusSucceedImg = a.getResourceId(R.styleable.FocusImageView_focus_success_id, NO_ID);
        mFocusFailedImg = a.getResourceId(R.styleable.FocusImageView_focus_fail_id, NO_ID);
        a.recycle();

    }

    /**
     *
     * @param point
     */
    public void startFocus(Point point) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.topMargin = point.y - getHeight() / 2;
        params.leftMargin = point.x - getWidth() / 2;
        setLayoutParams(params);
        setVisibility(View.VISIBLE);
        setImageResource(mFocusImg);
        startAnimation(mAnimation);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        }, 3500);
    }

    /**
     *
     * @param width
     * @param height
     */
    public void startFocus(int width, int height){
        // int screenWidth=DensityUtils.getScreenWidth();
        float sRawX = width;
        float sRawY = height;
        //  float rawY = sRawY * screenWidth / DensityUtils.getScreenHeight();
        //  float temp = sRawX;
        //  float rawX = rawY;
        // rawY = (screenWidth- temp) * DensityUtils.getScreenHeight() /screenWidth;
        startFocus(new Point((int) sRawX, (int) sRawY));


    }

    /**
     *
     */
    public void onFocusSuccess() {
        setImageResource(mFocusSucceedImg);
        mHandler.removeCallbacks(null, null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        }, 1000);

    }

    /**
     *
     */
    public void onFocusFailed() {
        setImageResource(mFocusFailedImg);
        mHandler.removeCallbacks(null, null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     *
     * @param focus
     */
    public void setFocusImg(int focus) {
        this.mFocusImg = focus;
    }

    /**
     *
     * @param focusSucceed
     */
    public void setFocusSucceedImg(int focusSucceed) {
        this.mFocusSucceedImg = focusSucceed;
    }
}
