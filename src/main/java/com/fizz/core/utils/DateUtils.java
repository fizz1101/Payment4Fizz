package com.fizz.core.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间工具类
 *
 * Created by fuzhongyu on 2017/7/24.
 */
public class DateUtils {

    /**
     * 获取当天天的起始时间
     * @param time 时间戳
     * @return
     */
    public static Date getStartDayTime(Long time){
        return getAwayDayStartTime(time,0);
    }


    /**
     * 获取当天的结束时间
     * @param time 时间戳
     * @return
     */
    public static Date getEndDayTime(Long time){
       return getAwayDayEndTime(time,0);
    }

    /**
     * 获取距离 awayDay 天的起始时间
     * @param time 时间
     * @param awayDay 距离时间  正：time 之后  负数：time之前
     * @return
     */
    public static Date getAwayDayStartTime(Long time,Integer awayDay){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_MONTH,awayDay);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }


    /**
     * 获取距离 awayDay 天的结束时间
     * @param time 时间
     * @param awayDay 距离时间  正：time 之后  负数：time之前
     * @return
     */
    public static Date getAwayDayEndTime(Long time,Integer awayDay){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_MONTH,awayDay);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime();
    }

    /**
     * 格式化时间(Date->String)
     * @date 2017年7月25日
     * @author 张纯真
     * @param date
     * @param formatStr
     * @return
     */
    public static String getFormatDate(Date date, String formatStr) {
    	SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
    	return sdf.format(date);
    }

    /**
     * 格式化时间(long->String)
     * @date 2017年7月26日
     * @author 张纯真
     * @param datetime
     * @param formatStr
     * @return
     */
    public static String getFormatDate(long datetime, String formatStr) {
    	SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
    	return sdf.format(new Date(datetime));
    }
    
    /**
     * 格式化时间(String->Date)
     * @date 2017年9月18日
     * @author 张纯真
     * @param dateStr
     * @param formatStr
     * @return
     */
    public static Date parseDate(String dateStr, String formatStr) {
    	SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
    	try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
    }

    /**
     * 时间操作工具类，加（减）天，小时，分钟   （负数为减）
     * @param time
     * @param addDay
     * @param addHour
     * @param addMinute
     * @return
     */
    public static Date addTime(Long time,Integer addDay,Integer addHour,Integer addMinute){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_MONTH,addDay);
        calendar.add(Calendar.HOUR_OF_DAY,addHour);
        calendar.add(Calendar.MINUTE,addMinute);
        return calendar.getTime();
    }


    /**
     * 获取起始时间和结束时间中间的时间段，如果不是同一天，显示天的粒度，如果是同一天显示小时粒度
     * @param starteTime 起始时间
     * @param endTime 结束时间
     * @return
     */
    public static List<Date> getBetweenTime(Long starteTime,Long endTime){
        List<Date> timeList=new ArrayList<>();
        //如果是同一天返回24小时
        if(DateUtils.getStartDayTime(starteTime).equals(DateUtils.getStartDayTime(endTime))){
           timeList=getHourList(starteTime,endTime);
        //如果是不同日期，则返回中间的日期（包含起始和结束当天，格式：月.日）
        }else {
            timeList=getDayList(starteTime,endTime);
        }
        return timeList;
    }


    /**
     * 根据起始时间和结束时间获取天（月.日）格式
     * @param startDate 开始是时间
     * @param endDate  结束时间
     * @return
     */
    public static List<Date> getHourList(Long startDate,Long endDate){
        List<Date> returnList=new ArrayList<>();

        Long oneHour = 1000 * 60 * 60l;

        Long stDate = getStartDayTime(startDate).getTime();
        Long enDate= getEndDayTime(endDate).getTime();
        Long time=stDate;
        while (time <= enDate) {
            Date date = new Date(time);
            returnList.add(date);
            time += oneHour;
        }

        return returnList;
    }

    /**
     * 根据起始时间和结束时间获取天（月.日）格式
     * @param startDate 开始是时间
     * @param endDate  结束时间
     * @return
     */
    public static List<Date> getDayList(Long startDate,Long endDate){
        List<Date> returnList=new ArrayList<>();

        Long oneDay = 1000 * 60 * 60 * 24l;

        Long stDate = getStartDayTime(startDate).getTime();
        Long enDate= getEndDayTime(endDate).getTime();
        Long time=stDate;
        while (time <= enDate) {
            Date date = new Date(time);
            returnList.add(date);
            time += oneDay;
        }

        return returnList;
    }
    
    /**
     * 获取一定间隔的时间段(包含头尾)
     * @date 2017年9月30日
     * @author 张纯真
     * @param startTime
     * @param endTime
     * @param interval	间隔时间(ms)
     * @param dateformat 返回时间格式
     * @return
     */
    public static List<String> getTimeQuantum(Long startTime, Long endTime, Long interval, String dateformat) {
    	List<String> returnList=new ArrayList<>();
    	while (startTime < endTime) {
    		startTime += interval;
    		String dataStr = getFormatDate(startTime, dateformat);
    		returnList.add(dataStr);
    	}
    	return returnList;
    }
    
    /**
     * 计算时间是当天的第几个5分钟
     * @date 2017年7月28日
     * @author 张纯真
     * @param datetime
     * @return
     */
    public static int get5IndexOfDay(long datetime) {
    	long today_start = getStartDayTime(datetime).getTime();
    	int index = (int) ((datetime-today_start)/1000/60/5);
    	return index;
    }
    
    /**
     * 获取当前时间段(5分钟粒度)
     * @date 2017年7月28日
     * @author 张纯真
     * @param datetime
     * @return
     */
    public static Object[] getTodayDateFor5Minute(long datetime) {
    	long today_start = getStartDayTime(new Date().getTime()).getTime();
    	int index = get5IndexOfDay(datetime);
    	Object[] arr = new Object[index+1];
    	for (int i=0; i<=index; i++) {
    		long cur_datetime = today_start + i*5*60*1000;
    		arr[i] = getFormatDate(cur_datetime, "HH:mm");
    	}
    	return arr;
    }
    
    /**
     * 获取初始化的5分钟数据(当天0点->传入时间)
     * @date 2017年7月28日
     * @author Administrator
     * @param datetime
     * @return
     */
    public static Object[] getInit5MinuteArr(long datetime) {
    	int index = get5IndexOfDay(datetime);
    	Object[] arr = new Object[index+1];
    	for (int i=0; i<arr.length; i++) {
    		arr[i] = 0;
    	}
    	return arr;
    }

}
