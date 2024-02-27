/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.activiti.z_six.util;

import com.alibaba.fastjson.util.TypeUtils;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 *
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss.SSS",
            "yyyy.MM.dd HH:mm", "yyyy.MM", "yyyyMMddHHmmss", "yyyyMMdd", "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss:SSSZZ", "yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ss.SSS Z", "yyyy-MM-dd HH"};

    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    public static String getDateMonth() {
        return getDate("yyyy-MM");
    }


    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    public static String formatDate(Date date) {
        if (date == null ) {
            return null;
        }
        return formatDate(date, "yyyy-MM-dd");
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date, String pattern) {
        return formatDate(date, pattern);
    }

    /**
     * 将传入的日期时间，转换格式 为pattern 格式的时间
     */
    public static Date formatDateToPattern(Date date, String pattern) {
        try {
            return parse(formatDate(date, pattern), pattern);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 得到周几 返回 1-7 （周一 到 周日）
     *
     * @return
     */
    public static int getWeekNum() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int num = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (num == 0) {
            num = 7;
        }
        return num;
    }

    /**
     * 日期型字符串转化为日期 格式
     * {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
     * "yyyyMMddHHmmss","yyyyMMdd","yyyy-MM-dd'T'HH:mm:ss.SSS","yyyy-MM-dd'T'HH:mm:ss:SSSZZ",
     * "yyyy-MM-dd'T'HH:mm:ss.SSSX","yyyy-MM-dd'T'HH:mm:ss.SSS Z"}
     */
    public static Date parseDate(Object str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取将来的小时
     *
     * @param date
     * @return
     */
    public static long futureHour(Date date) {
        long t = date.getTime() - System.currentTimeMillis();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }


    /****
     * 找出在某个范围之内的时间集合
     * @param start 开始
     * @param end 结束
     * @param list 源
     * @return teml 返回
     */
    public static List<Date> splitDate(Date start, Date end, List<Date> list) {
        List<Date> teml = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (Date d : list) {
                if (d.getTime() == start.getTime() || d.getTime() == end.getTime()) {
                    teml.add(d);
                }
                if (d.after(start) && d.before(end)) {
                    teml.add(d);
                }
            }
        }
        return teml;

    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parse(String strDate, String pattern) throws ParseException {
        return StringUtils.isBlank(strDate) ? null : new SimpleDateFormat(pattern).parse(strDate);
    }

    /**
     * 在日期上增加数个整月
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /***
     * 获取当前时间的后几天
     * @param addDay
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Date getDateAfter(int addDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, addDay);
        return calendar.getTime();
    }

    /***
     * 获取当前时间的后几天返回值字符串
     * @param addDay
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getDateStrAfter(int addDay) {
        return formatDate(getDateAfter(addDay));
    }

    /***
     * 获取当前时间后几个月日期 返回date
     * @param addMonth
     * @return
     */
    public static Date getDateAfterMonth(int addMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, addMonth);
        return calendar.getTime();
    }

    /***
     * 获取当前时间后几个月日期 返回Str
     * @param addMonth
     * @return
     */
    public static String getDateStrAfterMonth(int addMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, addMonth);
        return sdf.format(calendar.getTime());
    }

    /***
     * 获取时间后几个月日期 返回date
     * @param addMonth
     * @param dateStr
     * @return
     */
    public static Date getDateAfterMonthByDateStr(int addMonth, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Date now;
        try {
            now = sdf.parse(dateStr);
            calendar.setTime(now);
            System.out.println(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, addMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime();

    }

    /****
     * 获取时间后几个月日期 返回date
     * @param addMonth
     * @param date
     * @return
     */
    public static Date getDateAfterMonthByDate(int addMonth, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, addMonth);
        return calendar.getTime();
    }

    /****
     * 获取时间后几天 返回date
     * @param addDay
     * @param date
     * @return
     */
    public static Date getDateAfterDayByDate(int addDay, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, addDay);
        return calendar.getTime();
    }

    /****
     * 获取时间后几个月日期 返回Str
     * @param addMonth
     * @param dateStr
     * @return
     */
    public static String getDateAfterMonth(int addMonth, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Date now;
        try {
            now = sdf.parse(dateStr);
            calendar.setTime(now);
            calendar.add(Calendar.MONTH, addMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(calendar.getTime());
    }

    /****
     * 获取时间后几个月日期 返回str
     * @param addMonth
     * @param date
     * @return
     */
    public static String getDateAfterMonth(int addMonth, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, addMonth);
        return sdf.format(calendar.getTime());
    }

    /***
     * 判断两个时间是不是在同一天
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    /***
     * 判断两个时间是不是在同一个月
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

        return isSameMonth;
    }

    /**
     * utc格式返回本地时间
     *
     * @param utcTimeStr 例如：2016-04-24T16:00:00.000Z
     * @return
     * @throws ParseException
     */
    public static String utc2local(String utcTimeStr, String pattern) {
        try {
            utcTimeStr = utcTimeStr.replace("Z", " UTC");// 注意是空格+UTC
            Date d = parseDate(utcTimeStr, parsePatterns);
            return formatDate(d, pattern);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * utc 转date
     *
     * @param utcTimeStr 例如：2016-04-24T16:00:00.000Z
     * @return
     * @throws ParseException
     */
    public static Date utc2Date(String utcTimeStr) {
        if (org.apache.commons.lang3.StringUtils.isBlank(utcTimeStr)) {
            return new Date();
        }
        Instant instant = Instant.parse(utcTimeStr);
        try {
            return new Date(instant.toEpochMilli());
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    /**
     * 判断日期大小
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return
     * @throws Exception
     */
    public static Boolean compareDate(String beginTime, String endTime) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (sdf.parse(beginTime).getTime() <= sdf.parse(endTime).getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 得到某年某周的第一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        week = week - 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);

        Calendar cal = (Calendar) calendar.clone();
        cal.add(Calendar.DATE, week * 7);

        return getFirstDayOfWeek(cal.getTime());
    }

    /**
     * 得到某年某周的最后一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getLastDayOfWeek(int year, int week) {
        week = week - 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        Calendar cal = (Calendar) calendar.clone();
        cal.add(Calendar.DATE, week * 7);

        return getLastDayOfWeek(cal.getTime());
    }

    /**
     * String转为Date
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date strtodate = formatter.parse(strDate, new ParsePosition(0));
        return strtodate;
    }

    /**
     * 取得当前日期所在周的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Monday
        return calendar.getTime();
    }

    /**
     * 取得当前日期所在周的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6); // Sunday
        return calendar.getTime();
    }

    /**
     * 取得当前日期所在周的前一周最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfLastWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return getLastDayOfWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR) - 1);
    }

    /**
     * 取得当前日期所在周的前一周第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfLastWeek(Date date) {
        return getFirstDayOfWeek(getLastDayOfLastWeek(date));
    }

    /**
     * 返回指定日期的月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        return calendar.getTime();
    }

    /**
     * 返回指定年月的月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        calendar.set(year, month, 1);
        return calendar.getTime();
    }

    /**
     * 获取本月最后一天
     *
     * @param time yyyy-MM
     * @return
     */
    public static Date getLastDayMonth(String time) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, TypeUtils.castToInt(time.substring(0, 4)));
        cal.set(Calendar.MONTH, TypeUtils.castToInt(time.substring(5, 7)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获取当前天数的后几天
     *
     * @param str 日期
     * @param day 后几天
     * @return
     * @throws Exception
     */
    public static Date getSpecifiedDayAfter(String str, Integer day) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(str, "yyyy-MM-dd"));
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + day);
        return cal.getTime();
    }

    /**
     * 获取昨天日期
     *
     * @return yyyy-MM-dd
     * @author MingLi.Han
     * @date 2019年4月28日
     */
    public static String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        return sp.format(cal.getTime());
    }

    /**
     * 返回指定日期的月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 返回指定年月的下个月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        calendar.set(year, month, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 返回指定日期的上个月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 返回指定日期的季的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFirstDayOfQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date));
    }

    /**
     * 返回指定年季的季的第一天
     *
     * @param year
     * @param quarter
     * @return
     */
    public static Date getFirstDayOfQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 1 - 1;
        } else if (quarter == 2) {
            month = 4 - 1;
        } else if (quarter == 3) {
            month = 7 - 1;
        } else if (quarter == 4) {
            month = 10 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getFirstDayOfMonth(year, month);
    }

    /**
     * 返回指定日期的季的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getLastDayOfQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date));
    }

    /**
     * 返回指定年季的季的最后一天
     *
     * @param year
     * @param quarter
     * @return
     */
    public static Date getLastDayOfQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 3 - 1;
        } else if (quarter == 2) {
            month = 6 - 1;
        } else if (quarter == 3) {
            month = 9 - 1;
        } else if (quarter == 4) {
            month = 12 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getLastDayOfMonth(year, month);
    }

    /**
     * 返回指定日期的上一季的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfLastQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getLastDayOfLastQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date));
    }

    /**
     * 返回指定年季的上一季的最后一天
     *
     * @param year
     * @param quarter
     * @return
     */
    public static Date getLastDayOfLastQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 12 - 1;
        } else if (quarter == 2) {
            month = 3 - 1;
        } else if (quarter == 3) {
            month = 6 - 1;
        } else if (quarter == 4) {
            month = 9 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getLastDayOfMonth(year, month);
    }

    /**
     * 返回指定日期的季度
     *
     * @param date
     * @return
     */
    public static int getQuarterOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) / 3 + 1;
    }

    /**
     * 获取一天开始的时间
     *
     * @return
     * @throws Exception
     */
    public static Date getDayStart(Date date) throws Exception {
        String newDate = formatDate(date, "yyyy-MM-dd");
        newDate = newDate + " 00:00:00";
        return parse(newDate, "yyyy-MM-dd hh:mm:ss");
    }
    /**
     * 获取一天动态入参的时间
     *
     * @return
     * @throws Exception
     */
    public static Date getDayInputStart(Date date,String input) throws Exception {
        String newDate = formatDate(date, "yyyy-MM-dd");
        newDate = newDate +" " +input+":00:00";
        return parse(newDate, "yyyy-MM-dd hh:mm:ss");
    }

    /**
     * 将时间转化成yyyy-MM-dd 23:59:59格式
     *
     * @param packageEndDate
     * @return
     */
    public static Date forDateAddDay(Date packageEndDate) throws Exception {
        String newDate = formatDate(packageEndDate, "yyyy-MM-dd");
        newDate = newDate + " 23:59:59";
        return parse(newDate, "yyyy-MM-dd hh:mm:ss");
    }
    /**
     * 获取一天结束的时间
     *
     * @return
     * @throws Exception
     */
    public static Date getDayEnd(Date date) throws Exception {
        String newDate = formatDate(getDateAfterDayByDate(1, date), "yyyy-MM-dd");
        newDate = newDate + " 00:00:00";
        return parse(newDate, "yyyy-MM-dd hh:mm:ss");
    }

    /**
     * 获取一周开始的时间
     *
     * @return
     * @throws Exception
     */
    public static Date getWeekStart() throws Exception {
        LocalDate inputDate = LocalDate.now();
        TemporalAdjuster firstOfWeek = TemporalAdjusters.ofDateAdjuster(
                localDate -> localDate.minusDays(localDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()));
        String dateStr = inputDate.with(firstOfWeek).toString() + " 00:00:00";
        return getDayStart(DateUtils.parse(dateStr, "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取一周结束的时间
     *
     * @return
     * @throws Exception
     */
    public static Date getWeekEnd() throws Exception {
        LocalDate inputDate = LocalDate.now();
        TemporalAdjuster lastOfWeek = TemporalAdjusters.ofDateAdjuster(
                localDate -> localDate.plusDays(DayOfWeek.SUNDAY.getValue() - localDate.getDayOfWeek().getValue()));
        return getDayEnd(DateUtils.parse(inputDate.with(lastOfWeek).toString() + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取某年某月有多少天
     *
     * @param year  例 ： 2018
     * @param month 例 ：7
     * @return
     */
    public static int getDayNumOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); // 输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt, String... weekFormats) {
        if (weekFormats != null) {
            if (7 != weekFormats.length) {
                throw new ArrayIndexOutOfBoundsException("数组数量小于 7 或者 大于 7 ");
            }
        } else {
            weekFormats = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekFormats[w];
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dateStr
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(String dateStr, String... weekFormats) {
        Date date = DateUtils.parseDate(dateStr);
        return getWeekOfDate(date, weekFormats);
    }

    /**
     * 根据Date获取cron表达式
     *
     * @param date
     * @return
     */
    public static String getCron(Date date) {
        String dateFormat = "ss mm HH dd MM ?";
        return formatDate(date, dateFormat);
    }

    /**
     * 两个日期相差月份
     *
     * @param date1 <String>
     * @param date2 <String>
     * @return int
     * @throws ParseException
     */
    public static int getMonthSpace(String date1, String date2, boolean isAbs) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return getMonthSpace(sdf.parse(date1), sdf.parse(date2), isAbs);
    }

    /**
     * 两个日期相差月份
     *
     * @param date1 <String>
     * @param date2 <String>
     * @param isAbs 是否取绝对值
     * @return int
     * @throws ParseException
     */
    public static int getMonthSpace(Date date1, Date date2, boolean isAbs) throws ParseException {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        int result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        return isAbs ? Math.abs(result) : result;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 要判断的时间
     * @return dayForWeek 判断结果
     */
    public static int dayForWeek(Date pTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(pTime);
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * 判断传入日期是星期几
     *
     * @param pTime 要判断的时间
     * @return dayForWeek 判断结果
     */
    public static int dayForWeek(String pTime) {
        return dayForWeek(DateUtils.parseDate(pTime));
    }

    /**
     * 判断日期为第几周<br>
     * 注：日期为星期天时要注意(周天~周六为一周)
     *
     * @param time 时间
     * @return
     * @throws Exception
     */
    public static int getWhichWeek(String time) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(parse(time, "yyyy-MM-dd"));
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 将秒 转为时分秒格式
     *
     * @param str
     * @return
     */
    public static String formatSecond(String str) {
        Long seconds = Long.parseLong(str);
        Long temp = 0L;
        StringBuffer sb = new StringBuffer();
        temp = seconds / 3600;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 % 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    /**
     * date转string
     *
     * @param date
     * @return
     */
    public static String dateConversion(Date date, String format) {
        //转换成string格式
        SimpleDateFormat sformat = new SimpleDateFormat(format);
        String tiem = sformat.format(date);
        return tiem;
    }

    /**
     * 时间加减
     *
     * @param date   时间
     * @param number 天数
     * @param type   类型
     * @return
     * @throws Exception
     */
    public static String plusTime(Date date, int number, String type, String format) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //把日期往后增加一天,整数  往后推,负数往前移动
        switch (type) {
            //增加天数
            case "1":
                calendar.add(calendar.DATE, number);
                break;
            //小时
            case "2":
                calendar.add(calendar.HOUR, number);
                break;
            //分钟
            case "3":
                calendar.add(calendar.MINUTE, number);
                break;
            //秒
            case "4":
                calendar.add(calendar.SECOND, number);
                break;
            //年
            case "5":
                calendar.add(calendar.YEAR, number);
                break;
            //月
            case "6":
                calendar.add(calendar.MONTH, number);
                break;
            default:
                break;
        }
        //这个时间就是日期往后推一天的结果
        date = calendar.getTime();
        //转换成string格式
        SimpleDateFormat sformat = new SimpleDateFormat(format);
        String tiem = sformat.format(date);
        return tiem;

    }


    /**
     * 获取当前年第一天
     *
     * @return
     */
    public static Date getCurrYearFirst() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }


    /**
     * 获取当前年最后一天
     *
     * @return
     */
    public static Date getCurrYearLast() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取当前年第一天
     *
     * @param year
     * @return
     */
    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取当前年最后一天
     *
     * @param year
     * @return
     */
    public static Date getYearLast(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        return currYearLast;
    }

    /**
     * 当前时间
     *
     * @return
     */
    public static String currentTime() {
        return LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 几分钟前时间
     *
     * @return
     */
    public static String minutesAgoTime(long minute) {
        return LocalDateTime.now().withNano(0).minusMinutes(minute).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /***
     * 获取当前时间的后几秒
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Date getDateAfterSecond(int addSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, addSecond);
        return calendar.getTime();
    }

    /**
     * 格转为ISO8601格式
     *
     * @param date
     * @return1
     */
    public static String formatISO8601Date(Date date) {
        return ISO8601Utils.format(date, false, TimeZone.getDefault());
    }



    /**
     * isoDate
     *
     * @param date
     * @return
     */
    public static String getISO8601Timestamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
//        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    public static String getBeforeHourTime(int ihour) {
        String returnstr = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - ihour);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        returnstr = sdf.format(calendar.getTime());
        return returnstr;
    }

    public static String getPreHourStart()
    {
        String format="yyyy-MM-dd HH";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date d=new Date();
        d.setHours(d.getHours()-1);
        return sdf.format(d)+":00:00";
    }
    public static String getPreHourBanStart()
    {
        String format="yyyy-MM-dd HH";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date d=new Date();
        d.setHours(d.getHours()-1);
        return sdf.format(d)+":30:00";
    }

    public static String getNowHourStart()
    {
        String format="yyyy-MM-dd HH";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date d=new Date();
        d.setHours(d.getHours());
        return sdf.format(d)+":00:00";
    }
    public static String getPreMinuteStart()
    {
        String format="yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date d=new Date();
        d.setMinutes(d.getMinutes()-1);
        return sdf.format(d)+":00";
    }

    public static String getMinuteByParam(String paramHour)
    {
        String format="yyyy-MM-dd";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date d=new Date();
        return sdf.format(d)+" "+paramHour+":00";
    }


    public static String getPreHourEnd()
    {
        String format="yyyy-MM-dd HH";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date d=new Date();
        d.setHours(d.getHours()-1);
        return sdf.format(d)+":59:59";
    }
    public static String getPreMinuteEnd()
    {
        String format="yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date d=new Date();
        d.setMinutes(d.getMinutes()-1);
        return sdf.format(d)+":59";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(DateUtils.getDateMonth());
    }

    public static String getPreMonthStart(){
        Calendar c=Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        c.add(Calendar.MONTH,-1);
        return sdf.format(c.getTime());
    }
    public static String getPreMonth(){
        Calendar c=Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        c.add(Calendar.MONTH,-1);
        return sdf.format(c.getTime());
    }

    public static String getPreMonthEnd(){
        Calendar c=Calendar.getInstance();
        c.add(Calendar.MONTH,0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        return sdf.format(c.getTime());
    }

    public static String parseISO8601(String inputDate){
        LocalDateTime date = LocalDateTime.parse(inputDate,DateTimeFormatter.ISO_OFFSET_DATE_TIME);
         return  date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getPreSeconds(Date d) throws ParseException {
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str=format.format(d);
        Date d2=format.parse(str);
        int dayMis=1000*60*60*24;//一天的毫秒-1
        long curMillisecond=d2.getTime()+1000;//当天的毫秒
        DateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format2.format(curMillisecond);
    }

    public static String getPreDay(Date d) throws ParseException {
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        d.setHours(d.getHours()+24);
        return format.format(d);
    }

    public static String getMonthForInput(Date date) {
        return formatDate(date, "MM");
    }

}
