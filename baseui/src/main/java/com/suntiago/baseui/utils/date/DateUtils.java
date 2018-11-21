package com.suntiago.baseui.utils.date;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期显示，格式化。
 * Created by LiGang on 2016/6/1.
 */
@SuppressWarnings("ALL")
@SuppressLint("SimpleDateFormat")
public class DateUtils {
    /**
     * 获取当前时间
     * 格式 ： "yyyy_MM_dd"
     *
     * @return
     */
    public static String currentDateTime() {
        return currentDateTime(DateStyle.YYYY_MM_DD);
    }

    /**
     * 获取当前时间
     * 指定样式
     *
     * @param dateStyle 格式
     * @return
     */
    public static String currentDateTime(String dateStyle) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateStyle);
        return formatter.format(new Date());
    }

    /**
     * 格式化时间
     *
     * @param date  时间
     * @param style 格式
     * @return 格式化结果
     */
    public static String format(Date date, String style) {
        SimpleDateFormat format = new SimpleDateFormat(style);
        return format.format(date);
    }

    /**
     * 今天零点的时间
     *
     * @param date
     * @return
     */
    public static Date startOfDate(Date date) {
        long time = date.getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();// 今天零点零分零秒的毫秒数
        return new Date(time);
    }

    /**
     * 第一个时间是不是在第二个时间之前
     *
     * @param date
     * @param now
     * @return
     */
    public static boolean before(Date date, Date now) {
        if (date.before(now)) {
            return true;
        } else {
            long between = date.getTime() - now.getTime();

            if (between > (24 * 3600000)) {
                return false;
            }
            return true;
        }
    }

    /**
     * 今天23：59：59的时间
     *
     * @param date
     * @return
     */
    public static Date endOfDate(Date date) {
        long time = date.getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset() + 24 * 60 * 60 * 1000 - 1;
        return new Date(time);
    }

    /**
     * 格式化timestamp格式的时间，以秒为单位
     *
     * @param timestamp 秒
     * @param dateStyle 格式
     * @return 格式化结果
     */
    public static String formatSecondTimestamp(long timestamp, String dateStyle) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp * 1000);
        return format(c.getTime(), dateStyle);
    }

    /**
     * 根据时间戳，格式化日期，当天只返回时间，其他返回日期
     *
     * @param timestamp 秒
     * @return 格式化的时间
     */
    public static String getCurrentTimeIFCurrentDayOrDate(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp * 1000);
        if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                && c.get(Calendar.DATE) == now.get(Calendar.DATE)) {
            return format(c.getTime(), DateStyle.HH_MM);
        }
        return format(c.getTime(), DateStyle.YYYY_MM_DD);
    }

    /**
     * 获取yyyy-MM-dd格式日期
     *
     * @param context
     * @param date
     * @return
     */
    public static String getDateFormatYYYYMMDD(Context context, Date date) {
        if (context == null) return null;
        SimpleDateFormat format = new SimpleDateFormat(DateStyle.YYYY_MM_DD);
        return format.format(date);
    }

    /**
     * 把一个日期格式的字符串转化为日期
     *
     * @param context
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parseStringToDate(Context context, String dateStr) throws ParseException {
        if (context == null || dateStr == null) return null;
        SimpleDateFormat format = new SimpleDateFormat(DateStyle.YYYY_MM_DD);
        return format.parse(dateStr);
    }

    /**
     * 将一个时间转换为提示性字符串
     *
     * @param timeStamp 秒值
     * @return
     */
    public static String convertTimeToFormat(long timeStamp) {
        int current = (int) (System.currentTimeMillis() / 1000 - timeStamp);
        Calendar calendar = Calendar.getInstance();
        String today = DateUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String currentYear = DateUtils.format(calendar.getTime(), "yyyy");
        calendar.setTimeInMillis(timeStamp * 1000);
        String publishDay = DateUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String publishYear = DateUtils.format(calendar.getTime(), "yyyy");
        String publishMonth = DateUtils.format(calendar.getTime(), "MM-dd HH:mm");
        if (current < 60) {
            return "刚刚";
        } else if (current < 3600) {
            if (today.equals(publishDay)) {
                return current / 60 + "分钟前";
            } else {
                return publishMonth;
            }
        } else if (current < 86400) {
            if (today.equals(publishDay)) {
                return current / 3600 + "小时前";
            } else {
                return publishMonth;
            }
        } else if (current < 31536000) {
            if (publishYear.equals(currentYear)) {
                return publishMonth;
            } else {
                return publishDay;
            }
        } else {
            return publishDay;
        }
    }

    /**
     * 将一个日期转换为提示性时间字符串
     *
     * @param date 日期
     * @return
     */
    public static String formatDateTime(Date date) {
        String text;
        long dateTime = date.getTime();
        if (isSameDay(dateTime)) {
            Calendar calendar = GregorianCalendar.getInstance();
            if (inOneMinute(dateTime, calendar.getTimeInMillis())) {
                return "刚刚";
            } else if (inOneHour(dateTime, calendar.getTimeInMillis())) {
                return String.format("%d分钟之前", Math.abs(dateTime - calendar.getTimeInMillis()) / 60000);
            } else {
                calendar.setTime(date);
                text = "HH:mm";
            }
        } else if (isYesterday(dateTime)) {
            text = "昨天 HH:mm";
        } else if (isSameYear(dateTime)) {
            text = "M-d HH:mm";
        } else {
            text = "yyyy-M-d HH:mm";
        }

        // 注意，如果使用android.text.format.DateFormat这个工具类，在API 17之前它只支持adEhkMmszy
        return new SimpleDateFormat(text, Locale.CHINA).format(date);
    }

    /**
     * 时间间隔是否在1min内
     *
     * @param time1 毫秒
     * @param time2 毫秒
     * @return
     */
    private static boolean inOneMinute(long time1, long time2) {
        return Math.abs(time1 - time2) < 60000;
    }

    /**
     * 时间间隔是否在1h内
     *
     * @param time1 毫秒
     * @param time2 毫秒
     * @return
     */
    private static boolean inOneHour(long time1, long time2) {
        return Math.abs(time1 - time2) < 3600000;
    }

    /**
     * 是否是同一天
     *
     * @param time 毫秒
     * @return
     */
    private static boolean isSameDay(long time) {
        long startTime = floorDay(Calendar.getInstance()).getTimeInMillis();
        long endTime = ceilDay(Calendar.getInstance()).getTimeInMillis();
        return time > startTime && time < endTime;
    }

    /**
     * 是否是昨天
     *
     * @param time 毫秒
     * @return
     */
    private static boolean isYesterday(long time) {
        Calendar startCal;
        startCal = floorDay(Calendar.getInstance());
        startCal.add(Calendar.DAY_OF_MONTH, -1);
        long startTime = startCal.getTimeInMillis();

        Calendar endCal;
        endCal = ceilDay(Calendar.getInstance());
        endCal.add(Calendar.DAY_OF_MONTH, -1);
        long endTime = endCal.getTimeInMillis();
        return time > startTime && time < endTime;
    }

    /**
     * 是否是同一年
     *
     * @param time 毫秒
     * @return
     */
    private static boolean isSameYear(long time) {
        Calendar startCal;
        startCal = floorDay(Calendar.getInstance());
        startCal.set(Calendar.MONTH, Calendar.JANUARY);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        return time >= startCal.getTimeInMillis();
    }

    private static Calendar floorDay(Calendar startCal) {
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        return startCal;
    }

    private static Calendar ceilDay(Calendar endCal) {
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        return endCal;
    }
}
