package com.suishi.utils;

//import com.dongba.droidcore.log.ALog;

import java.text.DecimalFormat;

/**
 * 类型转换工具类
 */
public final class TypeParseUtils {

    public static Long stringToLong(String s) {
        Long result = -1L;
        try {
            result = Long.parseLong(s);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 默认String-->int
     *
     * @param data
     * @return
     */
    public static int parseInt(String data) {
        int result = 0;
        try {
            result = Integer.parseInt(data);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 转double 再四舍五入
     *
     * @param data
     * @return
     */
    public static long parseDoubleRoundLong(String data) {
        long result = 0;
        try {
            result = Math.round(Double.parseDouble(data));
        } catch (Exception e) {

        }
        return result;
    }

    public static Integer parseInteger(String data) {
        Integer result = null;
        try {
            result = Integer.parseInt(data);
        } catch (Exception e) {
        }
        return result;
    }


    /**
     * 默认String-->int
     *
     * @param data
     * @param defaultValue 默认值
     * @return
     */
    public static int parseInt(String data, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(data);
        } catch (Exception e) {
        }
        return result;
    }


    public static float parseFloat(String data) {
        float result = 0f;
        try {
            result = Float.parseFloat(data);
        } catch (Exception e) {
        }
        return result;
    }

    public static Long parseLong(int data) {
        Long result = null;
        try {
            result = Long.parseLong(String.valueOf(data));
        } catch (Exception e) {
        }
        return result;
    }

    public static Long parseLong(String data) {
        Long result = null;
        try {
            result = Long.parseLong(data);
        } catch (Exception e) {
        }
        return result;
    }

    public static float parseFloat(String data, float defaultValue) {
        float result = defaultValue;
        try {
            result = Float.parseFloat(data);
        } catch (Exception e) {
        }
        return result;
    }

    public static double parseDouble(String data) {
        double result = 0f;
        try {
            result = Double.parseDouble(data);
        } catch (Exception e) {
        }
        return result;
    }

    public static boolean parseBoolean(String data) {
        boolean result = false;
        try {
            result = Boolean.parseBoolean(data);
        } catch (Exception e) {
        }
        return result;
    }

    public static double coverseDouble(String data, double defaultValue) {
        double result = defaultValue;
        try {
            result = Double.parseDouble(data);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * float类型去零转换
     *
     * @param data
     * @return
     */
    public static String converData(float data) {
        String tempData = data + "";
        if ((tempData).contains(".0")) {
            tempData = tempData.replace(".0", "");
        }
        return tempData;
    }

    //小于两位的数字前面加0（1-->01）
    public static String getDateString(int dayOrMonth) {
        String day = "";
        if (dayOrMonth < 10) {
            day = "0" + dayOrMonth;
        } else {
            day = dayOrMonth + "";
        }
        return day;
    }

    /**
     * 将数据保留两位小数
     */
    public static String getTwoDecimal(double num) {
        DecimalFormat dFormat = new DecimalFormat("#0.00");
        String string = dFormat.format(num);
        return string;
    }

    public static String getNullDecimal(double num) {
        DecimalFormat dFormat = new DecimalFormat("#0");
        String string = dFormat.format(num);
        return string;
    }

    public static String getHundredBit(double str) {
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(str);
    }
}
