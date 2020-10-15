package com.suishi.live.app.widgets;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.suishi.live.app.R;


/**
 * Created by lifengwei on 2017/7/4.
 * 可左右滑动的选择控件
 */

public class SelectorView extends ViewGroup {

    private static final String TAG = "SelectorView";

    private int columnSpace;
    private int childWidth;

    private int touchSlop;

    //private CycleStruct<Integer> cycleStruct;

    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private ValueAnimator mValueAnimator;

    private int miniVelocity;
    private SeletcorAdapter adapter;
    private OnItemCheckListener mOnItemCheckListener;

    private int x_scroll = 0;
    private float touchDownX;
    private int x_down;
    private int lastFillingX;
    private boolean mScrolling;

    private int defaultSelectorItemIndex=0;

    private int defaultVisibleItem=5;

    private int initItemIndex=3;

    public SelectorView(Context context) {
        super(context);
    }

    public SelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.SelectorView);
        columnSpace = t.getDimensionPixelSize(R.styleable.SelectorView_space,0);
        t.recycle();

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        miniVelocity = viewConfiguration.getScaledMinimumFlingVelocity();

        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new Scroller(context);

        mValueAnimator = new ValueAnimator();
        mValueAnimator.setInterpolator(new AccelerateInterpolator());
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /**
     * 左侧滚动范围
     * @return
     */
    private int getLeftScrollRange(){
        if(adapter!=null){
            return (adapter.getItemCount()-defaultSelectorItemIndex-(initItemIndex-2)+6)*(childWidth+columnSpace);
        }
        return 0;
    }

    /**
     * 右侧滚动范围
     * @return
     */
    private int getRightScrollRange(){
        if(adapter!=null){
            return (defaultSelectorItemIndex+(initItemIndex-2))*(childWidth+columnSpace);
        }
        return 0;
    }


    private int getItemWidth(){
        return childWidth+columnSpace;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measureHeight = getChildAt(0).getMeasuredHeight();
        int measureWidth = getChildAt(0).getMeasuredWidth();
        childWidth = measureWidth;
        //edge check
        if(x_scroll>getRightScrollRange()){
            x_scroll = getRightScrollRange();
        }else if(x_scroll< -getLeftScrollRange()){
            x_scroll = -getLeftScrollRange();
        }

        int startIndex = x_scroll/getItemWidth();
        //绘制第一个view的位置
        int itemStartPos =getItemStartPosition();
        for (int index=0;index<defaultVisibleItem;index++){
         //   int i = cycleStruct.get();
            View child = getChildAt(index);
            int itemIndex = -startIndex+index+(initItemIndex-2);
            //scrolled by an item,order has changed,set value again
            if(adapter!=null&&itemIndex<adapter.getItemCount()){
                    adapter.setView(child, itemIndex);
                    child.setTag(itemIndex);
                    Checkable checkable = (Checkable) child;
                    checkable.setChecked(index==2);
                    if (mOnItemCheckListener != null) {
                        mOnItemCheckListener.onScrolled(itemIndex);
                    }
                defaultSelectorItemIndex=itemIndex;
            }
            child.layout(itemStartPos+columnSpace/2,0,itemStartPos+columnSpace/2+measureWidth,measureHeight);
            itemStartPos += getItemWidth();
        }
    }


    private int getItemStartPosition(){
        int leftWith=(getWidth()-getItemWidth())/2;
        return leftWith-getEmptySize()*getItemWidth();
    }

    /**
     * 获取空的条数
     * @return
     */
    private int getEmptySize(){
        int leftWith=(getWidth()-getItemWidth())/2;
        int itemSize=leftWith%getItemWidth();
        if(itemSize>0){
            itemSize=leftWith/getItemWidth()+1;
        }else{
            itemSize=leftWith/getItemWidth();
        }
        return itemSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        this.removeAllViews();
        for(int index=0;index<defaultVisibleItem;index++) {
            View item = adapter.createView();
            this.addView(item);
        }
        View child = getChildAt(0);
        LayoutParams layoutParam = child.getLayoutParams();
        int childWidth = (width - defaultVisibleItem* columnSpace)/defaultVisibleItem;
        int childHeightSpec;
        if(layoutParam == null||layoutParam.height==LayoutParams.WRAP_CONTENT){
            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.AT_MOST);
        }else if(layoutParam.height==LayoutParams.MATCH_PARENT){
            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);
        }else{//指定高度
            childHeightSpec = MeasureSpec.makeMeasureSpec(layoutParam.height,MeasureSpec.EXACTLY);
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
           // v.measure(MeasureSpec.makeMeasureSpec(childWidth,MeasureSpec.EXACTLY),childHeightSpec);
            v.measure(MeasureSpec.makeMeasureSpec(childWidth,MeasureSpec.EXACTLY),childHeightSpec);
        }
        int childHeight = child.getMeasuredHeight();
        //the view height equals it's child height
        setMeasuredDimension(width,childHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelAnimation();
                touchDownX = event.getX();
                x_down = x_scroll;
                mScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(touchDownX - event.getX()) >=touchSlop) {
                    mScrolling = true;
                } else {
                    mScrolling = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mScrolling = false;
                break;
        }
        return mScrolling;
    }

    int startFilingX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = event.getX()- touchDownX;
                if(mScrolling||Math.abs(offsetX)>touchSlop){
                    mScrolling= true;
                    x_scroll = (int) (x_down+offsetX);
                    requestLayout();
                }
                break;
            case MotionEvent.ACTION_UP:
                mScrolling = false;
                mVelocityTracker.computeCurrentVelocity(1000);
                if(Math.abs(mVelocityTracker.getXVelocity())>miniVelocity){
                    startFilingX = x_scroll;
                    mScroller.fling(0,0, (int) mVelocityTracker.getXVelocity(),0,Integer.MIN_VALUE,Integer.MAX_VALUE,0,0);
                    invalidate();
                    Log.i(TAG, "onTouchEvent: filing");
                }else{
                    Log.i(TAG, "onTouchEvent: not filing");
                    regressPos();
                }

                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()&&mScroller.getCurrX()!=lastFillingX&&mScroller.getCurrX()!=0){
            x_scroll = startFilingX+mScroller.getCurrX();
            lastFillingX = mScroller.getCurrX();
            Log.i(TAG, "computeScroll: filling:"+mScroller.getCurrX());
            requestLayout();
        }else if((!mScrolling)&&(!mValueAnimator.isStarted())&&(x_scroll%getItemWidth()!=0)){
            regressPos();
            Log.i(TAG, "computeScroll: regress");
        }else{
            Log.i(TAG, "computeScroll: none");
        }

    }

    private void cancelAnimation(){
        mValueAnimator.cancel();
        mScroller.abortAnimation();
    }

    /**
     * 弹回到只显示三个的位置
     */
    private void regressPos(){
        int residue = x_scroll%getItemWidth();
        int animStep;
        if(residue>getItemWidth()/2){
            animStep = getItemWidth()-residue;
        }else{
            animStep = -residue;
        }
        if(mValueAnimator.isRunning()){
            mValueAnimator.cancel();
        }
        Log.i(TAG, "regressPos: x_scroll:"+x_scroll+",to:"+(x_scroll+animStep));
        mValueAnimator.setIntValues(x_scroll,x_scroll+animStep);
        mValueAnimator.setDuration(Math.abs(animStep)/3);
        mValueAnimator.start();
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener(){
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            x_scroll = (int) animation.getAnimatedValue();
            Log.i(TAG, "onAnimationUpdate: x_scroll:"+x_scroll);
            requestLayout();
        }
    };

    public void setAdapter(SeletcorAdapter adapter){
        this.adapter = adapter;
        requestLayout();
    }

    public void setOnItemCheckListener(OnItemCheckListener l){
        mOnItemCheckListener = l;
    }

    public abstract static class SeletcorAdapter{

        private Context mContex;

        public SeletcorAdapter(Context context) {
            this.mContex=context;
        }

        public View createView(){
           return LayoutInflater.from(mContex).inflate(R.layout.adapter_item,null);
        }

        public abstract int getItemCount();

        public abstract void setView(View view,int position);

    }

    public interface OnItemCheckListener{
        void onItemChecked(int position);
        void onScrolled(int position);
    }

}