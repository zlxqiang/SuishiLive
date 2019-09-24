package com.suishi.utils;

/**
 * Created by dongtaizhao on 2017/11/15.
 */

public class UrlParaUtils {
    public static String appendUrlPara(String url, String paraName, String paraValue) {
        url = removeParas(url, paraName + "=");
        url = appendParas(url, paraName + "=", paraValue);
        return url;
    }

    private static String appendParas(String url, String para, String value) {
        if (!url.toLowerCase().contains(para)) {
            if (url.contains("?")) {
                url = url + "&" + para + value;
            } else {
                url = url + "?" + para + value;
            }
        }
        return url;
    }

    public static String removeParas(String url, String para) {
        if (url.toLowerCase().contains(para)) {
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf(para));
            }
        }
        return url;
    }
}
