package com.suishi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gaowei555 on 2017/9/9 16:31
 * E-mail fkfj555@163.com
 * Function
 */

public class PreferDefaultUtils {

    private static final String TAG = "PreferDefaultUtils";
    private static final String ACCOUNT_PRE = "c_app_info";

    /**
     * 是否打开过欢迎页面
     */
    private static final String ISWELCOME = "ISWELCOME";

    /**
     * 个推ID
     */
    private static final String GT_CID = "GT_CID";

    /**
     * 推送开关
     */
    private static final String ISPUSH = "ISPUSH";

    /**
     * 默认语言
     */
    private static final String AD_TIME = "languages";

    /**
     * 首页是否设置图片
     */
    private static final String HOMECHECKSTATE = "HOMECHECKSTATE";

    /**
     * 声音
     */
    private static final String VOICE = "VOICE";

    /**
     * 自动播放
     */
    private static final String AUTO_PLAY = "auto_paly";

    /**
     * 认证成功显示弹框
     */
    private static final String AUTH_STATE_SUCCESS = "auth_state_success";

    private volatile static PreferDefaultUtils instance;

    private Context mContext;

    public static void init(Context context) {
        instance = new PreferDefaultUtils(context);
    }


    public static PreferDefaultUtils getInstance() {
        if (instance == null) {
            throw new NullPointerException("请先初始化");
        }
        return instance;
    }

    /**
     * 读取信息到内存
     *
     * @param c
     */
    private PreferDefaultUtils(Context c) {
        this.mContext = c;
        PrefHelper.init(mContext);
    }

    /**
     * 清空数据
     */
    public void reset() {
        SharedPreferences sp = mContext.getSharedPreferences(ACCOUNT_PRE, MODE_PRIVATE);
        if (sp != null) {
            sp.edit().clear().commit();
        }
    }


    public PreferDefaultUtils setIsWelcome(boolean isWelcome) {
        PrefHelper.putBoolean(ACCOUNT_PRE, ISWELCOME, isWelcome);
        return this;
    }

    public boolean getIsWelcome() {
        return PrefHelper.getBoolean(ACCOUNT_PRE, ISWELCOME, true);
    }

    public PreferDefaultUtils setAutoPlay(boolean autoPlay) {
        PrefHelper.putBoolean(ACCOUNT_PRE, AUTO_PLAY, autoPlay);
        return this;
    }

    public boolean getAutoPlay() {
        return PrefHelper.getBoolean(ACCOUNT_PRE, AUTO_PLAY, true);
    }

    public String isShowAuthStateDialog() {
        return PrefHelper.getString(ACCOUNT_PRE, AUTH_STATE_SUCCESS, "-1");
    }

    public PreferDefaultUtils setShowStateDialog(String state) {
        PrefHelper.putString(ACCOUNT_PRE, AUTH_STATE_SUCCESS, state);
        return this;
    }


    public PreferDefaultUtils setGt_Cid(String cid) {
        PrefHelper.putString(ACCOUNT_PRE, GT_CID, cid);
        return this;
    }

    public String getGt_Cid() {
        return PrefHelper.getString(ACCOUNT_PRE, GT_CID, "");
    }

    public PreferDefaultUtils setIsPush(boolean isWelcome) {
        PrefHelper.putBoolean(ACCOUNT_PRE, ISPUSH, isWelcome);
        return this;
    }

    public boolean getIsPush() {
        return PrefHelper.getBoolean(ACCOUNT_PRE, ISPUSH, true);
    }

    public PreferDefaultUtils setVoice(boolean voice) {
        PrefHelper.putBoolean(ACCOUNT_PRE, VOICE, voice);
        return this;
    }

    public boolean getVoice() {
        return PrefHelper.getBoolean(ACCOUNT_PRE, VOICE, true);
    }

    public PreferDefaultUtils setLanguages(String ad_time) {
        PrefHelper.putString(ACCOUNT_PRE, AD_TIME, ad_time);
        return this;
    }

    public String getLanguages() {
        return PrefHelper.getString(ACCOUNT_PRE, AD_TIME, ConstantLanguages.ENGLISH);
    }

    public PreferDefaultUtils setHaveHomePicture(boolean is) {
        PrefHelper.putBoolean(ACCOUNT_PRE, HOMECHECKSTATE, is);
        return this;
    }

    public boolean getIsHaveHomePicture() {
        return PrefHelper.getBoolean(ACCOUNT_PRE, HOMECHECKSTATE, false);
    }

    public String getUuid() {
        return PrefHelper.getString(ACCOUNT_PRE, "sysCacheMap", null);
    }

    public void setUuid(String uuid) {
        PrefHelper.putString(ACCOUNT_PRE, "sysCacheMap", uuid);
    }
}
