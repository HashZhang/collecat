package com.sf.sgs.kafka2hdfs.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

public class DateTimeUtils {

	public static final int MILLIS_OF_SECOND = 1000;
	public static final int SECOND_OF_MINUTE = 60;
	public static final int MINUTE_OF_HOUR = 60;
	public static final int HOUR_OF_DAY = 24;

	public static final long MILLIS_OF_MINUTE = MILLIS_OF_SECOND * SECOND_OF_MINUTE;
	public static final long MILLIS_OF_HOUR = MILLIS_OF_MINUTE * MINUTE_OF_HOUR;
	public static final long MILLIS_OF_DAY = MILLIS_OF_HOUR * HOUR_OF_DAY;

	public static final int SECOND_OF_HOUR = SECOND_OF_MINUTE * MINUTE_OF_HOUR;
	public static final int SECOND_OF_DAY = SECOND_OF_HOUR * HOUR_OF_DAY;

	public static final int MINUTE_OF_DAY = MINUTE_OF_HOUR * HOUR_OF_DAY;
	/**
	 * 时区偏移
	 */
	public static final long TIME_OFFSET = TimeZone.getDefault().getRawOffset();

	public static final int TIME_TYPE_YEAR = 1;
	public static final int TIME_TYPE_MONTH = 2;
	public static final int TIME_TYPE_DATE = 3;
	public static final int TIME_TYPE_HOUR = 4;
	public static final int TIME_TYPE_MINUTE = 5;
	public static final int TIME_TYPE_SECOND = 6;

	private static Map<Long, String> workDayMap = new ConcurrentHashMap<Long, String>();
	
	/**
	 * 缓存一天的分钟数
	 * "0000"->0
	 * "0100"->60
	 * .....
	 * "2359"->1439
	 */
	private static final Map<String,Integer> hhmm2MinutesMap=cacheAllMinutesOfDay();
	
	/**
	 * 基准时间，2014-01-01（现用于中转班次预测预警）
	 */
	public static final long BASETM20140101 = 1388505600000L;

	/**
	 * 增加指定类型时间长度，时间类型参见本类常量
	 *
	 * @since 2010-8-3
	 */
	public static Date addTimes(Date orgDate, int amount, int timeType) {
		if (orgDate == null) {
			return null;
		}

		switch (timeType) {
			case TIME_TYPE_YEAR:
			case TIME_TYPE_MONTH:
				return addMutableTimes(orgDate, amount, timeType);
			case TIME_TYPE_DATE:
				return new Date(orgDate.getTime() + MILLIS_OF_DAY * amount);
			case TIME_TYPE_HOUR:
				return new Date(orgDate.getTime() + MILLIS_OF_HOUR * amount);
			case TIME_TYPE_MINUTE:
				return new Date(orgDate.getTime() + MILLIS_OF_MINUTE * amount);
			case TIME_TYPE_SECOND:
				return new Date(orgDate.getTime() + (long) MILLIS_OF_SECOND * amount);
			default:
				throw new RuntimeException("invalid time type, please refer DateTimeUtils constants.");
		}
	}

	private static Date addMutableTimes(Date orgDate, int amount, int timeType) {
		DateTime orgDateTime = new DateTime(orgDate.getTime());
		int year = orgDateTime.getYear();
		int month = orgDateTime.getMonthOfYear();
		int date = orgDateTime.getDayOfMonth();
		int hourOfDay = orgDateTime.getHourOfDay();
		int minute = orgDateTime.getMinuteOfHour();
		int second = orgDateTime.getSecondOfMinute();

		MutableDateTime dateTime = new MutableDateTime(year, month, date, hourOfDay, minute, second, 0);
		switch (timeType) {
			case TIME_TYPE_YEAR:
				dateTime.addYears(amount);
				break;
			case TIME_TYPE_MONTH:
				dateTime.addMonths(amount);
				break;
			default:
				throw new RuntimeException("invalid time type, please refer DateTimeUtils constants.");
		}
		return new Date(dateTime.getMillis());
	}

	/**
	 * 判断是否在同一天
	 */
	public static boolean inSameDay(Date date1, Date date2) {
		DateTime orgDateTime = new DateTime(date1.getTime());
		int year = orgDateTime.getYear();
		int day = orgDateTime.getDayOfYear();

		DateTime orgDateTime2 = new DateTime(date2.getTime());
		int year2 = orgDateTime2.getYear();
		int day2 = orgDateTime2.getDayOfYear();

		return year == year2 && day == day2;
	}

	public static int getMinuteOfDay(Date date) {
		return new DateTime(date.getTime()).getMinuteOfDay();
	}

    public static long getMidnight(long timeMillis) {
        return timeMillis - timePastMidnight(timeMillis);
    }
    
    public static long getHourTime(long timeMillis) {
        return timeMillis - timePastHour(timeMillis);
    }

    public static long timePastHour(long timeMillis) {
        return (TIME_OFFSET + timeMillis) % MILLIS_OF_HOUR;
    }
    
    public static long timePastMidnight(long timeMillis) {
        return (TIME_OFFSET + timeMillis) % MILLIS_OF_DAY;
    }
	/**
	 * 合成新日期， 15点 ，传入的time为 1500
	 * 注意与calcDateByMinutes的区别
	 */
	public static Date calcDate(Date baseDateTime, int time, int crossDay) {
		int minutesOfTime = hhmm2Min(time);
		return calcDateByMinutes(baseDateTime, minutesOfTime, crossDay);
	}

	public static Date calcDate(Date baseDateTime, String time) {
		return calcDateByMinutes(baseDateTime, hhmm2Min(time), 0);
	}

	/**
	 * 按分钟数合成新日期，15点 ，传入的time为 900
	 */
	public static Date calcDateByMinutes(Date baseDate, int minutesOfTime, int crossDay) {
		DateTime baseDateTime = new DateTime(baseDate.getTime());
		return new DateTime(baseDateTime.getYear(), baseDateTime.getMonthOfYear(), baseDateTime.getDayOfMonth(), 0, 0, 0, crossDay).plusMinutes(minutesOfTime).plusDays(
				crossDay).toDate();
	}

	/**
	 * Date 类型转换 XMLGregorianCalendar
	 * @param date
	 * @return
	 * @author 370453
	 */
	public static Date xMLGregorianCalendarToDate(XMLGregorianCalendar xmlDate) {
		if(xmlDate == null){
			return null;
		}
		Date date = xmlDate.toGregorianCalendar().getTime();
		return date;
	}

	/**
	 * Date 类型转换 XMLGregorianCalendar
	 *
	 * @param date
	 * @return
	 */
	public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		XMLGregorianCalendar xmlGregorianCalendar = null;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
					calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
					calendar.get(Calendar.MILLISECOND), 480);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("dateToXMLGregorianCalendar!");
		}
		return xmlGregorianCalendar;
	}

	public static int hhmm2Min(int hhmmInt) {
		return (hhmmInt / 100) * 60 + (hhmmInt % 100);
	}

	public static int hhmm2Min(String hhmm) {
		return hhmm2MinutesMap.get(hhmm);
	}
	
	/**
	 * @describe 计算两个日期间隔分钟数（secondDate-firstDate）
	 * @param firstDate
	 *            时间
	 * @param secondDate
	 *            时间
	 * @return 间隔分钟数（secondDate-firstDate）
	 * @author zhangYao 568677
	 * @date 2014-8-9
	 */
	public static int getDistanceMM(Date firstDate, Date secondDate) {
		long firstMs = firstDate.getTime();
		long secondMs = secondDate.getTime();
		return Math.abs((int) ((secondMs - firstMs) / MILLIS_OF_MINUTE));
	}

	/**
	 * 计算两个日期间隔天数，比较日期，不根据时间计算
	 */
	public static int getDistanceDay(Date firstDate, Date secondDate) {
		long firstMs = trunc(firstDate);
		long secondMs = trunc(secondDate);
		return Math.abs((int) ((secondMs - firstMs) / MILLIS_OF_DAY));
	}

	public static Date truncDate(Date now) {
		return new Date(trunc(now));
	}

	public static long trunc(Date now) {
		return (now.getTime() + TIME_OFFSET) / MILLIS_OF_DAY * MILLIS_OF_DAY;
	}

	/**
	 * 获得工作日
	 */
	public static String getWorkday(Date orgDate) {
		if (orgDate == null) {
			return null;
		}

		long dateSign = trunc(orgDate);
		String workDayString = workDayMap.get(dateSign);
		if (workDayString != null) {
			return workDayString;
		}

		DateTime orgDateTime = new DateTime(orgDate.getTime());
		workDayMap.put(dateSign, workDayString = orgDateTime.getDayOfWeek() + "");

		return workDayString;
	}
	
	/**
	 * 计算hhmm格式时间的分钟数
	 * 0100->60
	 * 0200->120
	 * @param hhmm hhmm格式的时间
	 * @return
	 */
	public static int calcMinutesOfHHmm(String hhmm){
		int hhmmInt=Integer.parseInt(hhmm);
		return hhmm2Min(hhmmInt);
	}
	
	/**
	 * 获取一天的所有时间字符串(0000->2359)
	 * @return
	 */
	public static String[] getAllTimesOfDay() {
		String[] result = new String[HOUR_OF_DAY * MINUTE_OF_HOUR];
		
		int i=0;
		for (String hh : getHoursOfDay()) {
			for (String mm : getMinutesOfHour()) {
				result[i++]=hh+mm;
			}
		}
		return result;
	}
	/**
	 * 获取一天的所有小时字符串
	 * @return (00->23)
	 */
	private static String[] getHoursOfDay() {
		String[] result=new String[HOUR_OF_DAY];
		for(int i=0;i<HOUR_OF_DAY;i++){
			result[i]=StringUtils.leftPad(String.valueOf(i), 2,'0');
		}
		return result;
	}
	
	/**
	 * 获取所有分钟字符串
	 * @return (00->59)
	 */
	private static String[] getMinutesOfHour() {
		String[] result=new String[MINUTE_OF_HOUR];
		for(int i=0;i<MINUTE_OF_HOUR;i++){
			result[i]=StringUtils.leftPad(String.valueOf(i), 2,'0');
		}
		return result;
	}
	
	private static Map<String,Integer> cacheAllMinutesOfDay() {
		Map<String,Integer> hhmm2MinutesTempMap=new HashMap<String, Integer>();
		String[] allTimesOfDay=getAllTimesOfDay();
		for (String hhmm : allTimesOfDay) {
			hhmm2MinutesTempMap.put(hhmm, calcMinutesOfHHmm(hhmm));
		}
		return Collections.unmodifiableMap(hhmm2MinutesTempMap);
	}
	
	/**
     * 比较两个日期是否跨分钟
     * @param maxDate
     * @param minDate
     * @return maxDate>=minDate:true; maxDate<minDate:flase
     */
    public static boolean compareMinutesOfDate(Date maxDate, Date minDate) {
        if (maxDate == null || minDate == null) {
            return false;
        }
        return compareMinutesOfDate(maxDate.getTime(), minDate.getTime());

    }
    
    /**
     * 比较两个日期是否跨分钟
     * @param maxDateTime
     * @param minDateTime
     * @return maxDate>=minDate:true; maxDate<minDate:flase
     */
    public static boolean compareMinutesOfDate(long maxDateTime, long minDateTime) {
        return maxDateTime / DateTimeUtils.MILLIS_OF_MINUTE > minDateTime / DateTimeUtils.MILLIS_OF_MINUTE;

    }
    
    /**
     * 比较两个日期差是否大于指定分钟数
     * @param maxDate
     * @param minDate
     * @return maxDate>=minDate:true; maxDate<minDate:flase
     */
    public static boolean compareMinutesOfDate(Date maxDate, Date minDate, int minutes){
        if (maxDate == null || minDate == null) {
            return false;
        }
        return maxDate.getTime() / DateTimeUtils.MILLIS_OF_MINUTE - minDate.getTime() / DateTimeUtils.MILLIS_OF_MINUTE > minutes;
    }
    
    /**
	 * 格式化长时间，格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 * @author Jack.Qiu
	 * @since 2010-8-3
	 */
	public static String formatDateTime(Date orgDate) {
		if (orgDate == null) {
			return null;
		}

		DateTime orgDateTime = new DateTime(orgDate.getTime());
		int year = orgDateTime.getYear();
		int month = orgDateTime.getMonthOfYear();
		int date = orgDateTime.getDayOfMonth();
		int hourOfDay = orgDateTime.getHourOfDay();
		int minute = orgDateTime.getMinuteOfHour();
		int second = orgDateTime.getSecondOfMinute();

		StringBuffer sb = new StringBuffer();
		sb.append(year).append("-");
		twoLength(sb, month);
		sb.append("-");
		twoLength(sb, date);
		sb.append(" ");
		twoLength(sb, hourOfDay);
		sb.append(":");
		twoLength(sb, minute);
		sb.append(":");
		twoLength(sb, second);
		return sb.toString();
	}
	
	/**
	 * 不足两位则补零
	 * 
	 * @param amount
	 * @return
	 * @author Jack.Qiu
	 * @since 2010-8-3
	 */
	private static void twoLength(StringBuffer sb, int amount) {
		if (amount < 10) {
			sb.append("0");
		}

		sb.append(amount);
	}
	
	/**
	 * 时间增加指定小时数,可以为小数,如1.5
	 * @param orgDate
	 * @param hours
	 * @return
	 */
	public static Date addHours(Date orgDate, double hours) {
		return new Date(orgDate.getTime() + (long)(MILLIS_OF_HOUR * hours));
	}
	
	/**
	 * 简化创建日期
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param mimute
	 * @return
	 */
	public static Date date(int year, int month, int day, int hour, int mimute) {
		return new DateTime(year, month, day, hour, mimute, 0, 0).toDate();
	}

	/**
	 * 获取日期唯一标识
	 * @param date
	 * @return
	 */
	public static String getUUID(Date date) {
		return date == null ? "" : String.valueOf(date.getTime());
	}
	
	public static String getDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	
}