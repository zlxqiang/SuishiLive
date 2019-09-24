package com.suishi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设备相关及正则表达式相关工具类
 */
public final class CookiUtils {

    /**
     * Uri解析
     *
     * @param url
     * @return
     */
    public static Uri parseWWW(String url) {
        if (url == null)
            return null;
        if (url.contains("http")) {
            return Uri.parse(url);
        } else if (url.contains("file")) {
            return Uri.parse(url);
        } else if (url.contains("content")) {
            return Uri.parse(url);
        } else {
            return Uri.parse("http://" + url);
        }
    }

    /**
     * 拼接http
     *
     * @param url
     * @return
     */
    public static String containsHttp(String url) {
        if (url == null)
            return null;
        if (url.contains("http://")) {
            return url;
        } else {
            return "http://" + url;
        }
    }

    public static String replaceMobileHint(String mobile) {
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 检查手机密码  6~16位数字或字母
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNoPWD(String mobiles) {
        return mobiles.length() >= 6;
    }

    //验证码4位数字
    public static boolean isVerificaNumber(String verifica) {
        Pattern p = Pattern.compile("[0-9]{4}");
        Matcher m = p.matcher(verifica);
        return m.matches();
    }

    public static boolean checkSpritSeries(String string) {
        Pattern p = Pattern.compile("/+[a-zA-Z0-9]*");
        Matcher matcher = p.matcher(string);
        return matcher.matches();
    }

    /**
     * 检查手机号正确性
     *
     * @param phoneNum
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNum) {
        String num = phoneNum.replaceAll(" ", "");
        phoneNum = num.replace("+86", "");
        Pattern p = Pattern.compile("^[1][3456789][0-9]{9}$");
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }

    /**
     * 检查电话正确
     *
     * @param phone
     * @return
     */
    public static boolean isCorrentPhone(String phone) {
        String isMob = "^1[3-8]{1}[0-9]{9}$";
        String isTel = "^([0-9]{3,4}-)?[0-9]{7,8}$";
        String sw = "^400[0-9]{7}$";
        Pattern p1 = Pattern.compile(isMob);
        Pattern p2 = Pattern.compile(isTel);
        Pattern p3 = Pattern.compile(sw);
        Matcher m1 = p1.matcher(phone);
        Matcher m2 = p2.matcher(phone);
        Matcher m3 = p3.matcher(phone);
        return m1.matches() || m2.matches() || m3.matches();
    }

    /**
     * 检查邮箱正确性
     *
     * @param email
     * @return
     */
    public static boolean isCorrentEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断是否全是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 只使用字符数字下滑线
     *
     * @param str
     * @return
     */
    public static boolean isCorrentStrMun(String str) {
        Pattern p = Pattern.compile("^\\w+$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 取出当前版本号
     *
     * @param context
     * @return
     */
    public static int getVersion(Context context) {
        try {
            context = context.getApplicationContext();
            PackageManager manager = context.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 取出当前版本名字
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            context = context.getApplicationContext();
            PackageManager manager = context.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 截取倒数"/"字符串
     * <p>
     * 如 str="1/2/3/4/5/6/7" num=3   结果是"5/6/7"
     *
     * @param str 传入地址
     * @param num 第几个
     * @return 输入字符串
     */
    public static String getSubStr(String str, int num) {
        String result = "";
        int i = 0;
        while (i < num) {
            int lastFirst = str.lastIndexOf('/');
            result = str.substring(lastFirst) + result;
            str = str.substring(0, lastFirst);
            i++;
        }
        return result.substring(1);
    }

    public static String getSubStr(String str, String key, int num) {
        String result = "";
        int i = 0;
        while (i < num) {
            int lastFirst = str.lastIndexOf(key);
            result = str.substring(lastFirst) + result;
            str = str.substring(0, lastFirst);
            i++;
        }
        return result.substring(1);
    }

    /**
     * 从url获取文件类型name
     *
     * @param str
     * @return
     */
    public static String getFileNameType(String str) {
        String result = "";
        for (int i = str.length(); i > 0; i--) {
            String tmp = str.substring(i);
            if (tmp.contains(".")) {
                System.out.println(tmp);
                return tmp;
            }
        }
        return result;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getDeviceName() {
        return TextUtils.isEmpty(Build.MODEL) ? "" : Build.MODEL;
    }


    /**
     * 获取系统版本
     *
     * @return
     */
    public static String getOSVersion() {
        return "Android" + Build.VERSION.RELEASE;
    }


    /**
     * 判断是否开启GPS
     */
    public static boolean isGpsOpen(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检查设备是否有导航键盘
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }

        return hasNavigationBar;

    }

    /**
     * 计算虚拟按键高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context c) {
        TelephonyManager telephonyManager = (TelephonyManager) c
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    // 隐藏键盘
    public static void hideSoftInput(AppCompatActivity context) {
//        InputMethodManager imm = (InputMethodManager) context
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm.isActive()) {
//            View focusView = context.getCurrentFocus();
//            if (focusView != null) {
//                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
//            }
//        }
        hideSoftInputWithoutF(context, null);
    }

    // 隐藏键盘
    public static void hideSoftInputWithoutF(AppCompatActivity context, View parentView) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            // View focusView = context.getCurrentFocus();
            if (parentView != null) {
                imm.hideSoftInputFromWindow(parentView.getWindowToken(), 0);
            } else {
                View focusView = context.getCurrentFocus();
                if (focusView != null) {
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                }
            }
        }
    }

    //获取手机的唯一标识
    public static String getDeviceId(Context context) {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(context);
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID(context));
        }
        return deviceId.toString();
    }


    public static String getUUID(Context context) {
        String uuid = null;
        SharedPreferences mShare = context.getSharedPreferences("uuid", MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
    }

    /**
     * 将数据保留两位小数
     */
    public static String getTwoDecimal(double doublenum) {
        DecimalFormat dFormat = new DecimalFormat("#0.00");
        String yearString = dFormat.format(doublenum);
//        Double temp= Double.valueOf(yearString);
        return yearString;
    }

    public static String getBigDecimal(double bigDecimal) {
        NumberFormat nf = NumberFormat.getInstance();
        // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
        nf.setGroupingUsed(false);
        // 结果未做任何处理
        return nf.format(bigDecimal);
    }
}
