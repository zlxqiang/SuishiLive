package com.suishi.utils;


import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Map工具类
 *
 * @author jqlin
 */
public class MapUtils<T> {

    /**
     * 将实体类转换成请求参数,以map<k,v>形式返回
     *
     * @return
     */
    public static Map<String, String> beanToMap(Object object) {
        Map<String, String> map = new HashMap<String, String>();
        // System.out.println(obj.getClass());
        // 获取f对象对应类中的所有属性域
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o = fields[i].get(object);
                if (o != null)
                    map.put(varName, o.toString());
                // System.out.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    public static Map<String, String> beanToMapWithFilterEmpty(Object object) {
        Map<String, String> map = new HashMap<String, String>();
        // System.out.println(obj.getClass());
        // 获取f对象对应类中的所有属性域
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o = fields[i].get(object);
                if (o != null) {
                    if (o instanceof String) {
                        String value = o.toString();
                        if (!TextUtils.isEmpty(value)) {
                            map.put(varName, o.toString());
                        }
                    } else if (o instanceof Integer) {
                        int value = TypeParseUtils.parseInt(o.toString());
                        if (value >= 0) {
                            map.put(varName, o.toString());
                        }
                    } else {
                        map.put(varName, o.toString());
                    }
                }

                // System.out.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    /**
     * javabean to json
     *
     * @param o
     * @return
     */
    public static String javabeanToJson(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }

    /**
     * list to json
     *
     * @param list
     * @return
     */
//    public  String listToJson(List<T> list) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(list);
//        return json;
//    }
//    public static String listToJson(List<String> list) {
//        Gson gson = new Gson();
//        String json = gson.toJson(list);
//        return json;
//    }
    public static String listToJson(List<Double> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    public static List<Double> jsonToList(String json) {
        List<Double> list = new ArrayList<>();
        if (json.length() <= 0) {
            return list;
        }
        json = json.substring(1, json.length() - 1);
        json = json.replace("\"", "");

        String[] split = json.split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(TypeParseUtils.parseDouble(split[i]));
        }
        return list;
    }

    /**
     * map to json
     *
     * @param map
     * @return
     */
    public static String mapToJson(Map<String, Object> map) {

        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }

}