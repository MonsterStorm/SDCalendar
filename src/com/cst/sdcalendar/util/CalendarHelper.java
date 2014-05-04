package com.cst.sdcalendar.util;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Helper类，用于Date，JODA DateTime和String之间转化
 */
public class CalendarHelper {

	public static SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

	public static SimpleDateFormat MMMFormat = new SimpleDateFormat("MMM", Locale.getDefault());

	/**
	 * 给定日期，返回该日期排满月视图日期，包含前一个月，当前月，下一个月的日期
	 * 
	 * @param month
	 * @param year
	 * @param startDayOfWeek
	 *            : calendar can start from customized date instead of Sunday
	 * @return
	 */
	public static ArrayList<DateTime> getFullWeeksForMonthView(int month, int year, int startDayOfWeek, boolean sixWeeksInCalendar) {
		ArrayList<DateTime> datetimeList = new ArrayList<DateTime>();

		DateTime firstDateOfMonth = new DateTime(year, month, 1, 0, 0, 0, 0);
		DateTime lastDateOfMonth = firstDateOfMonth.plusDays(firstDateOfMonth.getNumDaysInMonth() - 1);

		// Add dates of first week from previous month
		int weekdayOfFirstDate = firstDateOfMonth.getWeekDay();

		// If weekdayOfFirstDate smaller than startDayOfWeek
		// For e.g: weekdayFirstDate is Monday, startDayOfWeek is Tuesday
		// increase the weekday of FirstDate because it's in the future
		if (weekdayOfFirstDate < startDayOfWeek) {
			weekdayOfFirstDate += 7;
		}

		while (weekdayOfFirstDate > 0) {
			DateTime dateTime = firstDateOfMonth.minusDays(weekdayOfFirstDate - startDayOfWeek);
			if (!dateTime.lt(firstDateOfMonth)) {
				break;
			}

			datetimeList.add(dateTime);
			weekdayOfFirstDate--;
		}

		// Add dates of current month
		for (int i = 0; i < lastDateOfMonth.getDay(); i++) {
			datetimeList.add(firstDateOfMonth.plusDays(i));
		}

		// Add dates of last week from next month
		int endDayOfWeek = startDayOfWeek - 1;

		if (endDayOfWeek == 0) {
			endDayOfWeek = 7;
		}

		if (lastDateOfMonth.getWeekDay() != endDayOfWeek) {
			int i = 1;
			while (true) {
				DateTime nextDay = lastDateOfMonth.plusDays(i);
				datetimeList.add(nextDay);
				i++;
				if (nextDay.getWeekDay() == endDayOfWeek) {
					break;
				}
			}
		}

		// Add more weeks to fill remaining rows
		if (sixWeeksInCalendar) {
			int size = datetimeList.size();
			int row = size / 7;
			int numOfDays = (6 - row) * 7;
			DateTime lastDateTime = datetimeList.get(size - 1);
			for (int i = 1; i <= numOfDays; i++) {
				DateTime nextDateTime = lastDateTime.plusDays(i);
				datetimeList.add(nextDateTime);
			}
		}

		return datetimeList;
	}

	/**
	 * 得到日视图的时间列表
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static ArrayList<DateTime> getFullTimeForDayView(int year, int month, int day) {
		ArrayList<DateTime> datetimeList = new ArrayList<DateTime>();
		DateTime firstTimeOfDay = new DateTime(year, month, day, 0, 0, 0, 0);
		DateTime lastTimeOfDay = firstTimeOfDay.plus(0, 0, 1, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		for (DateTime currentTimeOfDay = firstTimeOfDay; currentTimeOfDay.lt(lastTimeOfDay);) {
			datetimeList.add(currentTimeOfDay);// time rule
			datetimeList.add(currentTimeOfDay);// time
			currentTimeOfDay = currentTimeOfDay.plus(0, 0, 0, 1, 0, 0, 0, DateTime.DayOverflow.LastDay);
			;
		}
		return datetimeList;
	}

	/**
	 * 得到日视图的时间列表
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static ArrayList<DateTime> getFullDaysForWeekView(int year, int month, int day) {
		ArrayList<DateTime> datetimeList = new ArrayList<DateTime>();

		DateTime currentTime = new DateTime(year, month, day, 0, 0, 0, 0);

		int index = currentTime.getWeekDay();

		DateTime firstTimeOfWeek = currentTime.minus(0, 0, index, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		DateTime lastTimeOfWeek = currentTime.plus(0, 0, (7 - index), 0, 0, 0, 0, DateTime.DayOverflow.LastDay);

		for (int i = 0; i < 24; i++) {// 一天二十四小时
			DateTime currentTimeOfDay = firstTimeOfWeek;
			
			DateTime timeOfDay = currentTimeOfDay.plus(0, 0, 0, i, 0, 0, 0, DateTime.DayOverflow.LastDay);

			datetimeList.add(timeOfDay);// time rule
			
			for (;currentTimeOfDay.lt(lastTimeOfWeek);) {
				datetimeList.add(currentTimeOfDay);// time
				currentTimeOfDay = currentTimeOfDay.plus(0, 0, 1, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
			}
		}
		return datetimeList;
	}

	/**
	 * Date转DateTime，精确到天
	 * 
	 * @param date
	 * @return
	 */
	public static DateTime convertDateToDateTime(Date date) {
		// Get year, javaMonth, date
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date);

		int year = calendar.get(Calendar.YEAR);
		int javaMonth = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		// TODO hour not handle error ?
		// int hour = calendar.get(Calendar.HOUR_OF_DAY);

		// javaMonth start at 0. Need to plus 1 to get datetimeMonth
		return new DateTime(year, javaMonth + 1, day, 0, 0, 0, 0);
	}

	/**
	 * DateTime转Date
	 * 
	 * @param dateTime
	 * @return
	 */
	public static Date convertDateTimeToDate(DateTime dateTime) {
		int year = dateTime.getYear();
		int datetimeMonth = dateTime.getMonth();
		int day = dateTime.getDay();

		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		// datetimeMonth start at 1. Need to minus 1 to get javaMonth
		calendar.set(year, datetimeMonth - 1, day);

		return calendar.getTime();
	}

	/**
	 * String转Date，默认日期格式yyyy-MM-dd
	 * 
	 * @param dateString
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFromString(String dateString, String dateFormat) throws ParseException {
		SimpleDateFormat formatter;
		if (dateFormat == null) {
			formatter = yyyyMMddFormat;
		} else {
			formatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
		}

		return formatter.parse(dateString);
	}

	/**
	 * String转DateTime,默认格式 yyyy-MM-dd
	 * 
	 * @param dateString
	 * @param dateFormat
	 * @return
	 */
	public static DateTime getDateTimeFromString(String dateString, String dateFormat) {
		Date date;
		try {
			date = getDateFromString(dateString, dateFormat);
			return convertDateToDateTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * DateTime列表转字符串列表
	 * 
	 * @param dateTimes
	 * @return
	 */
	public static ArrayList<String> convertToStringList(ArrayList<DateTime> dateTimes) {
		ArrayList<String> list = new ArrayList<String>();
		for (DateTime dateTime : dateTimes) {
			list.add(dateTime.format("YYYY-MM-DD"));
		}
		return list;
	}

}
