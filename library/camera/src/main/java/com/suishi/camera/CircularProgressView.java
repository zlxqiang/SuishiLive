package com.suishi.camera;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageButton;
import com.scwang.smartrefresh.layout.util.DensityUtil;


/**
 * Description:
 */
public class CircularProgressView extends AppCompatImageButton{

    private int mStroke = 8;
    private int mProcess = 0;
    /**
     * 最长录制10s
     */
    private int mTotal = 10000;
    /**
     * 外圈
     */
    private int mNormalColor = 0x80FFFFFF;
    /**
     * 进度条
     */
    private int mSecondColor = 0xFF7ed321;
    /**
     * 内圈
     */
    private int centerColor = 0xFFFFFFFF;
    /**
     *
     */
    private int progressShade = 0x50000000;
    /**
     *
     */
    private int mStartAngle = -90;
    private RectF mRectF;
    private Paint normalPaint;
    private Paint centerPaint;
    private Paint progressPaint;
    private Drawable mDrawable;
    /**
     * 阴影
     */
    BlurMaskFilter blu;
    /**
     * 按下时的时间
     */
    private long downTime;
    /**
     * 停留时的时间
     */
    long thisTime;
    /**
     * 是否是点击
     */
    boolean click = false;

    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        blu = new BlurMaskFilter(7, BlurMaskFilter.Blur.SOLID);
        mStroke = DensityUtil.dp2px(mStroke);
        normalPaint = new Paint();
        normalPaint.setColor(mNormalColor);
        normalPaint.setStrokeWidth(mStroke);
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setAntiAlias(true);
//        normalPaint.setShadowLayer(2,2,2,progressShade);
//        setLayerType(LAYER_TYPE_SOFTWARE, normalPaint);
        normalPaint.setMaskFilter(blu);

        mDrawable = new Progress();
        setImageDrawable(mDrawable);

        centerPaint = new Paint();
        centerPaint.setColor(centerColor);
        centerPaint.setMaskFilter(blu);
    }

    public void setTotal(int total) {
        this.mTotal = total;
        mDrawable.invalidateSelf();
    }

    public int getTotal() {
        return mTotal;
    }

    public void setProcess(int process) {
        this.mProcess = process;
        post(new Runnable() {
            @Override
            public void run() {
                mDrawable.invalidateSelf();
            }
        });
    }

    public int getProcess() {
        return mProcess;
    }

    public void setStroke(float dp) {
        this.mStroke = DensityUtil.dp2px(dp);
        normalPaint.setStrokeWidth(mStroke);
        mDrawable.invalidateSelf();
    }


    Handler mHandler = new Handler();
    Runnable r = new Runnable() {

        @Override
        public void run() {
            //每隔1s循环执行run方法
            mHandler.postDelayed(this, 50);
            drawCircle();
        }
    };


    /**
     * 是否运行
     */
    private boolean isRunning = false;

    private boolean longClick = false;

    @Override
    public boolean performClick() {
        return super.performClick();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (down) {
//                    downTime = System.currentTimeMillis();
//                    down = false;
//                    click = true;
//                    mHandler.postDelayed(r, 0);//延时100毫秒
//                }
//
//                break;
//            case MotionEvent.ACTION_MOVE:
//                thisTime = System.currentTimeMillis();
//                if (thisTime - downTime >= 500) {
//                    if (isStop) return super.onTouchEvent(event);
//                    longClick = true;
//                    click = false;
//                    if (listener != null) {
//                        listener.onLongClick();
//                        mHandler.postDelayed(r, 0);//延时100毫秒
//                    }
//                } else {
//                    click = true;
//
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                down = true;
//                mHandler.removeCallbacks(r);
//                if (listener != null) {
//                    if (click) {
//                        listener.onClick();
//                    } else {
//                        listener.onLongClickUp();
//                    }
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    private void drawCircle() {
        mProcess = (int) (System.currentTimeMillis() - downTime);
        mDrawable.invalidateSelf();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }

    public void startOrStop(){
        if(!isRunning) {
            isRunning = true;
            downTime = System.currentTimeMillis();
            mHandler.postDelayed(r, 0);//延时100毫秒
        }else{
            isRunning = false;
            mHandler.removeCallbacks(r);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     *
     */
    private class Progress extends Drawable {
        @Override
        public void draw(Canvas canvas) {
            int width = getWidth();
            int pd = mStroke / 2 + 1;
            if (mRectF == null) {
                mRectF = new RectF(pd, pd, width - pd, width - pd);
            }
            canvas.drawCircle(width / 2, width / 2, width / 2 - pd * 2 + 1, centerPaint);

            normalPaint.setColor(mNormalColor);
            normalPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(width / 2, width / 2, width / 2 - pd, normalPaint);

            normalPaint.setColor(mSecondColor);
            canvas.drawArc(mRectF, mStartAngle, mProcess * 360 / (float) mTotal, false, normalPaint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }
    }

    public interface OnLongDownListener {
        /**
         *
         */
        void onLongClick();

        /**
         * 单击
         */
        void onClick();

        /**
         *
         */
        void onLongClickUp();
    }

    private OnLongDownListener listener;

    public void setOnLongDownListener(OnLongDownListener listener) {
        this.listener = listener;
    }
}
