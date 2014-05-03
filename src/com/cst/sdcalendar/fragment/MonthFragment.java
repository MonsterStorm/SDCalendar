package com.cst.sdcalendar.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import hirondelle.date4j.DateTime;

import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.adapter.MonthCalendarGridAdapter;
import com.cst.sdcalendar.util.CalendarHelper;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;

/**
 * ����ͼ
 * 
 * @author song
 * 
 */
public class MonthFragment extends BaseCalendarFragment {

	// ��ʼ������
	public final static String START_DAY_OF_WEEK = "startDayOfWeek";
	public final static String SIX_WEEKS_IN_CALENDAR = "sixWeeksInCalendar";

	// ��ʼ����
	protected int startDayOfWeek = SUNDAY;
	// �Ƿ���ʾ6�й̶��߶ȣ�����ͼ��
	private boolean sixWeeksInCalendar = true;
	// ����
	private Time firstMonthTime = new Time();
	// ������formatter��"MMMM yyyy" ��ʽ
	private final StringBuilder monthYearStringBuilder = new StringBuilder(50);
	private Formatter monthYearFormatter = new Formatter(monthYearStringBuilder, Locale.getDefault());
	// ��ʾ�·ݵ�flags
	private static final int MONTH_YEAR_FLAG = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;

	// title
	@Override
	public View getContentTitle() {
		return null;
	}

	@Override
	public BaseCalendarGridAdapter getNewDatesGridAdapter(DateTime datetime) {
		return new MonthCalendarGridAdapter(getActivity(), datetime.getMonth(), datetime.getYear(), getCaldroidData(), extraData);
	}

	@Override
	public void getChildCaldroidData() {
		super.getChildCaldroidData();
		caldroidData.put(START_DAY_OF_WEEK, Integer.valueOf(startDayOfWeek));
		caldroidData.put(SIX_WEEKS_IN_CALENDAR, Boolean.valueOf(sixWeeksInCalendar));
	}

	@Override
	public void getChildSavedStates(Bundle bundle) {
		super.getChildSavedStates(bundle);
		bundle.putInt(START_DAY_OF_WEEK, startDayOfWeek);
		bundle.putBoolean(SIX_WEEKS_IN_CALENDAR, sixWeeksInCalendar);
	}

	@Override
	public void retrieveChildInitialArgs(Bundle args) {
		super.retrieveChildInitialArgs(args);
		// �õ�һ�ܵ���ʼ�죬Ĭ���� SUNDAY
		startDayOfWeek = args.getInt(START_DAY_OF_WEEK, 1);
		if (startDayOfWeek > 7) {
			startDayOfWeek = startDayOfWeek % 7;
		}

		// �Ƿ���������ʾ
		sixWeeksInCalendar = args.getBoolean(SIX_WEEKS_IN_CALENDAR, true);
	}

	@Override
	public DateTime buildCurrentDateTime() {
		return new DateTime(year, month, 1, 0, 0, 0, 0);
	}

	@Override
	public DateTime buildDateTime(DateTime datetime, int unit) {
		if (unit > 0) {
			return datetime.plus(0, unit, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		} else {
			return datetime.minus(0, unit, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		}
	}

	@Override
	protected DateTime getFirstDateTime(DateTime datetime) {
		return datetime.getStartOfMonth();
	}

	@Override
	protected DateTime getLastDateTime(DateTime datetime) {
		return datetime.getEndOfMonth();
	}

	/**
	 * �õ��ӱ��������
	 * 
	 * @return "SUN, MON, TUE, WED, THU, FRI, SAT"
	 */
	public ArrayList<String> getContentTitles() {
		ArrayList<String> list = new ArrayList<String>();

		SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.getDefault());

		// 17 Feb 2013 is Sunday
		DateTime sunday = new DateTime(2013, 2, 17, 0, 0, 0, 0);
		DateTime nextDay = sunday.plusDays(startDayOfWeek - SUNDAY);

		for (int i = 0; i < 7; i++) {
			Date date = CalendarHelper.convertDateTimeToDate(nextDay);
			list.add(fmt.format(date).toUpperCase());
			nextDay = nextDay.plusDays(1);
		}

		return list;
	}

	@Override
	public void setupChildDateGridPages() {
		super.setupChildDateGridPages();
		// Set if viewpager wrap around particular month or all months (6 rows)
		vpContent.setSixWeeksInCalendar(sixWeeksInCalendar);
	}

	// -----------------------------ˢ��----------------------------
	/**
	 * ˢ��Title
	 */
	@Override
	protected void refreshTitle() {
		firstMonthTime.year = year;
		firstMonthTime.month = month - 1;
		firstMonthTime.monthDay = 1;
		long millis = firstMonthTime.toMillis(true);

		// This is the method used by the platform Calendar app to get a
		// correctly localized month name for display on a wall calendar
		monthYearStringBuilder.setLength(0);
		String monthTitle = DateUtils.formatDateRange(getActivity(), monthYearFormatter, millis, millis, MONTH_YEAR_FLAG).toString();

		tvTitle.setText(monthTitle);
	}

	// -------------------getters and setters--------------------------
	public boolean isSixWeeksInCalendar() {
		return sixWeeksInCalendar;
	}

	public void setSixWeeksInCalendar(boolean sixWeeksInCalendar) {
		this.sixWeeksInCalendar = sixWeeksInCalendar;
		vpContent.setSixWeeksInCalendar(sixWeeksInCalendar);
	}

}
