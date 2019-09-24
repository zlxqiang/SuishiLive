package com.suishi.utils;


import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtils {

    //    public static String covertToDate(long duration) {
//        Date date = new Date(duration);
//        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
//        return format.format(date);
//    }
    public static String covertToDate(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = getDateFormat();
        return format.format(date);
    }

    public static String coverToDateMinute(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    public static String coverToDateDay(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String coverToDateDay2(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    public static String coverToHoureMinute(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    public static String coverToDateMoth(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    public static String coverToDateYear(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(date);
    }

    public static String getCurrentTime() {
        Date dt = new Date();
        Long time = dt.getTime();
        return time.toString();
    }


    public static Date getCurrentQuarterStartTime(int currentMonth) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    public static Date getCurrentQuarterEndTime(int currentMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime(currentMonth));
        cal.add(Calendar.MONTH, 3);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }


    /**
     * 调此方法输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳
     *
     * @param time
     * @return
     */
    public static String dataOne(String time) {
        SimpleDateFormat sdr = getDateFormat();
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();

    public static SimpleDateFormat getDateFormat() {
//        if (null == DateLocal.get()) {
        DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
//        }
        return DateLocal.get();
    }

    public static SimpleDateFormat getDateFormat(String pattern) {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat(pattern, Locale.getDefault()));
        }
        return DateLocal.get();
    }

    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault());
    }

    /**
     * 获取时间戳
     *
     * @return 获取时间戳
     */
    public static String getTimeString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    /**
     * @param time
     * @return
     */
    public static long dateToTimeStamp(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date date = null;
        String times = null;
        long timeStamp = 0;
        try {
            date = sdr.parse(time);
            timeStamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    /**
     * @param time
     * @return
     */
    public static long dateToTimeStamp2(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        long timeStamp = 0;
        try {
            Date date = sdr.parse(time);
            timeStamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static long dateToTimeStamp3(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        long timeStamp = 0;
        try {
            Date date = sdr.parse(time);
            timeStamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }


    /**
     * MS turn every minute
     *
     * @param duration Millisecond
     *                 *
     * @return Every minute
     */
    public static String timeParse(Long duration) {
        String time = "";
        if (duration > 1000) {
            time = timeParseMinute(duration);
        } else {
            long minute = duration / 60000;
            long seconds = duration % 60000;
            long second = Math.round(seconds / 1000);
            if (minute < 10) {
                time += "0";
            }
            time += minute + ":";
            if (second < 10) {
                time += "0";
            }
            time += second;
        }
        return time;
    }

    private static SimpleDateFormat msFormat = new SimpleDateFormat("mm:ss");

    /**
     * MS turn every minute
     *
     * @param duration Millisecond
     *                 *
     * @return Every minute
     */
    static String timeParseMinute(Long duration) {

        try {
            return msFormat.format(duration);
        } catch (Exception e) {
            e.printStackTrace();
            return "0:00";
        }

    }

    public static int getYear() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.YEAR); // 获取当前年份
    }

    public static int getMonth() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.MONTH); // 获取当前月
    }

    public static int getDay() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.DAY_OF_MONTH); // 获取当前天
    }

    public static int getHour() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.HOUR_OF_DAY); // 获取当前小时
    }

    public static int getMinute() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.MINUTE); // 获取当前分钟
    }

    public static int getAfterDate(int day) {
        Calendar calendar = Calendar.getInstance();
        long time = 24 * 3600 * 1000 * day;
        Date dt = new Date();
        Long currentTime = dt.getTime();
        long finalTime = currentTime + time;
        calendar.roll(Calendar.DATE, day);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得当月天数
     */
    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isToday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return diffDay == 0;
        }
        return false;
    }

    /**
     * 判断是否为今天
     *
     * @param time 毫秒
     * @return true今天 false不是
     */
    public static boolean isToday(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return diffDay == 0;
        }
        return false;
    }

    /**
     * 根据日期获取星期
     *
     * @param str
     * @return
     */
    public static String getWeekday(String str) throws ParseException {
        Date date = getDateFormat().parse(str);
        Calendar c = Calendar.getInstance();//
        c.setTime(date);
        if (date != null) {
            return weekdayFormat(c.get(Calendar.DAY_OF_WEEK)); // 0..6 0:sunday,
            // 6:saturday
        }

        return "";
    }

    public static String getWeekDay(long time) {
        Date date = new Date(time);
        Calendar c = Calendar.getInstance();//
        c.setTime(date);
        if (date != null) {
            return weekdayFormat(c.get(Calendar.DAY_OF_WEEK)); // 1..7 0:sunday,
            // 6:saturday
        }

        return "";
    }

    private static String weekdayFormat(int day) {
        switch (day) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }


    public static String getMonthDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日", Locale.getDefault());
        return format.format(time);
    }

    public static String getYearMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return format.format(time);
    }

    public static String getMonthDay(String time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return format.format(time);
    }

    public static String getMonthDayWeekDate(long time) {
        SimpleDateFormat format = getDateFormat("MM月dd日");
        String monthDay = format.format(time);
        Date date = new Date(time);
        Calendar c = Calendar.getInstance();//
        c.setTime(date);
        return monthDay + getWeekDay(time);
    }


    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }


    public static Date getThisWeekLastMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - 1);
        return cal.getTime();
    }


    /**
     * 用SimpleDateFormat计算时间差
     *
     * @throws ParseException
     */
    public static String dateString(String nowDate, String afterDate) throws ParseException {
        if (afterDate == null || nowDate == null) {
            return null;
        }
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        /*天数差*/
        Date fromDate1 = Calendar.getInstance().getTime();
        Date toDate1 = simpleFormat.parse(afterDate);
        long from1 = fromDate1.getTime();
        long to1 = toDate1.getTime();
        int days = (int) ((to1 - from1) / (1000 * 60 * 60 * 24));
        /*小时差*/
        int hours = (int) (((to1 - from1) % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        if (days <= 0 && hours <= 0) {
            return "去续费";
        }
        return "剩" + days + "天" + hours + "小时   去续费";
    }

}
