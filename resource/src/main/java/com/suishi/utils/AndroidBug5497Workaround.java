package com.suishi.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * @Author: zzq
 * @Description:
 * @CreateDate: 2019/6/19 16:03
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/6/19 16:03
 * @Version: 1.0
 */
public class AndroidBug5497Workaround {

    public static void assistActivity(Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        /** if (usableHeightNow != usableHeightPrevious) {
         int usableHeightSansKeyboard = mChildOfContent.getHeight();
         int heightDifference = usableHeightSansKeyboard - usableHeightNow;
         if (heightDifference > (usableHeightSansKeyboard / 4)) {            //
         //keyboard probably just became visible
         frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
         } else {            // keyboard probably just became hidden
         frameLayoutParams.height = usableHeightSansKeyboard;
         }
         mChildOfContent.requestLayout();
         usableHeightPrevious = usableHeightNow;
         */
        if (usableHeightNow != usableHeightPrevious) {
            frameLayoutParams.height = usableHeightNow;
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom);// 全屏模式下： return r.bottom}
    }
}
