package com.cst.sdcalendar.fragment;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.GridView;

import com.cst.sdcalendar.Mode;
import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.adapter.ColumnTitleAdapter;
import com.cst.sdcalendar.adapter.MonthCalendarGridAdapter;
import com.cst.sdcalendar.util.CalendarHelper;
import com.cst.sdcalendar.viewpager.InfiniteViewPager;

/**
 * 月视图
 * 
 * @author song
 * 
 */
public class MonthFragment extends BaseCalendarFragment {

	// 初始化参数
	public final static String START_DAY_OF_WEEK = "startDayOfWeek";
	public final static String SIX_WEEKS_IN_CALENDAR = "sixWeeksInCalendar";

	// 初始星期
	protected int startDayOfWeek = SUNDAY;
	// 是否显示6行固定高度（月视图）
	private boolean sixWeeksInCalendar = true;
	// 月首
	private Time firstMonthTime = new Time();
	// 可重用formatter，"MMMM yyyy" 格式
	private final StringBuilder monthYearStringBuilder = new StringBuilder(50);
	private Formatter monthYearFormatter = new Formatter(monthYearStringBuilder, Locale.getDefault());
	// 显示月份的flags
	private static final int MONTH_YEAR_FLAG = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;

	@Override
	public BaseCalendarGridAdapter getNewDatesGridAdapter(DateTime datetime) {
		return new MonthCalendarGridAdapter(getActivity(), datetime.getYear(), datetime.getMonth(), getCaldroidData(), extraData);
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
		// 得到一周的起始天，默认是 SUNDAY
		startDayOfWeek = args.getInt(START_DAY_OF_WEEK, 1);
		if (startDayOfWeek > 7) {
			startDayOfWeek = startDayOfWeek % 7;
		}

		// 是否是六行显示
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
			return datetime.minus(0, Math.abs(unit), 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
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
	
	@Override
	public ColumnTitleAdapter getColumnTitleAdapter(GridView gvContentTitle) {
		gvContentTitle.setNumColumns(Mode.MONTH.getColumn());
		return new ColumnTitleAdapter(getActivity(), android.R.layout.simple_list_item_1, getColumnTitles());
	}

	@Override
	public void setupChildDateGridPages(InfiniteViewPager vpContent) {
		super.setupChildDateGridPages(vpContent);
		// Set if viewpager wrap around particular month or all months (6 rows)
		vpContent.setSixWeeksInCalendar(sixWeeksInCalendar);
	}
	
	@Override
	public void setupChildDateGridFragment(DateGridFragment dateGridFragment) {
		dateGridFragment.setMode(Mode.MONTH);
	}

	// -----------------------------刷新----------------------------
	/**
	 * 刷新Title
	 */
	@Override
	protected void refreshTitle() {
		if(tvTitle == null){
			return;
		}
		
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
	
	// --------------------private methods----------------------------
	/**
	 * 得到子标题的数据
	 * 
	 * @return "SUN, MON, TUE, WED, THU, FRI, SAT"
	 */
	private ArrayList<String> getColumnTitles() {
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


	// -------------------getters and setters--------------------------
	public boolean isSixWeeksInCalendar() {
		return sixWeeksInCalendar;
	}

	public void setSixWeeksInCalendar(boolean sixWeeksInCalendar) {
		this.sixWeeksInCalendar = sixWeeksInCalendar;
		vpContent.setSixWeeksInCalendar(sixWeeksInCalendar);
	}

}
