package com.kanven.cloud.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date parse(String dt, String format) throws ParseException {
		if (dt == null) {
			return null;
		}
		if (StringUtils.isEmpty(format)) {
			format = DATE_TIME_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(dt);
	}

	public static Date parse(String dt) throws ParseException {
		return parse(dt, DATE_TIME_FORMAT);
	}

	/**
	 * 日期转化成指定日期格式的字符串
	 * 
	 * @param dt
	 * @param format
	 * @return
	 */
	public static String format(Date dt, String format) {
		if (dt == null) {
			return null;
		}
		if (StringUtils.isEmpty(format)) {
			format = DATE_TIME_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(dt);
	}

	/**
	 * 获取指定天数的日期
	 * 
	 * @param dt
	 *            指定时间
	 * @param days
	 *            指定天數
	 * @return
	 */
	public static Date addDays(Date dt, int days) {
		if (dt == null) {
			dt = new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}

	public static boolean before(Date dt) {
		return before(new Date(), dt);
	}

	public static boolean before(Date dt, Date target) {
		return dt.getTime() <= target.getTime() ? true : false;
	}

}
