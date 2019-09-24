package com.suishi.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * @Author: zzq
 * @Description:
 * @CreateDate: 2019/7/5 14:23
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/7/5 14:23
 * @Version: 1.0
 */
public class DoubleClickExit {

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 9) {
                isExit = false;
            }
        }
    };

    private static Boolean isExit = false;


    public static void exit(Activity activity) {
        if (!isExit) {
            isExit = true;
            ToastUtil.shortToast("再按一次退出程序");
            mHandler.sendEmptyMessageDelayed(9, 2000);
        } else {
            activity.finish();
            Looper.getMainLooper().quitSafely();
        }
    }

}
