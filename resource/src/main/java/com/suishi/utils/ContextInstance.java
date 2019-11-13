package com.suishi.utils;

import android.app.Application;

/**
 * @Author: zzq
 * @Description:
 * @CreateDate: 2019/6/14 18:29
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/6/14 18:29
 * @Version: 1.0
 */
public class ContextInstance {

    private static Application application;

    public static void init(Application context) {
        application = context;
    }

    public static Application getInstance() {
        if (application == null) {
            throw new NullPointerException("请先初始化");
        }
        return application;
    }

}
