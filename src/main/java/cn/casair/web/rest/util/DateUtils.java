package cn.casair.web.rest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: liu
 * @Description:
 * @Date: Create in 下午5:56 18-11-7
 * @Modified:
 */
public class DateUtils {

    /**
     * 获取某一天的前一天
     * @param date
     * @return
     */
    public static Date getLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 日期转成yyyy-MM-dd的形式
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date);
        return dateString;
    }

    /**
     * yyyy-MM-dd转成日期
     * @param dateStr
     * @return
     */
    public static Date strToDate(String dateStr) throws ParseException {
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(dateStr);
    }



    /**
     * 字符串转日期
     * @param str
     * @param dateFormatExp
     * @return
     * @throws ParseException
     */
    public static Date strToDate(String str, String dateFormatExp) throws ParseException {
        SimpleDateFormat format =new SimpleDateFormat(dateFormatExp);
        return format.parse(str);
    }

    /**
     * 日期转字符串
     * @param date
     * @param dateFormatExp
     * @return
     */
    public static String dateToStr(Date date, String dateFormatExp) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatExp);
        return format.format(date);
    }







}
