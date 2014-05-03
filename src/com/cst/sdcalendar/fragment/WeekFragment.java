package com.cst.sdcalendar.fragment;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;

import com.cst.sdcalendar.Mode;
import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.adapter.ColumnTitleAdapter;
import com.cst.sdcalendar.adapter.WeekCalendarGridAdapter;
import com.cst.sdcalendar.util.CalendarHelper;

/**
 * 月视图
 * 
 * @author song
 * 
 */
public class WeekFragment extends BaseCalendarFragment {

	// 初始化参数
	public final static String START_DAY_OF_WEEK = "startDayOfWeek";

	// 初始星期
	protected int startDayOfWeek = SUNDAY;

	@Override
	public BaseCalendarGridAdapter getNewDatesGridAdapter(DateTime datetime) {
		return new WeekCalendarGridAdapter(getActivity(), datetime.getYear(), datetime.getMonth(), datetime.getWeekDay(), getCaldroidData(), extraData);
	}

	@Override
	public void getChildCaldroidData() {
		super.getChildCaldroidData();
		caldroidData.put(START_DAY_OF_WEEK, Integer.valueOf(startDayOfWeek));
	}

	@Override
	public void getChildSavedStates(Bundle bundle) {
		super.getChildSavedStates(bundle);
		bundle.putInt(START_DAY_OF_WEEK, startDayOfWeek);
	}

	@Override
	public void retrieveChildInitialArgs(Bundle args) {
		super.retrieveChildInitialArgs(args);
		// 得到一周的起始天，默认是 SUNDAY
		startDayOfWeek = args.getInt(START_DAY_OF_WEEK, 1);
		if (startDayOfWeek > 7) {
			startDayOfWeek = startDayOfWeek % 7;
		}
	}

	@Override
	public DateTime buildCurrentDateTime() {
		return new DateTime(year, month, 1, 0, 0, 0, 0);
	}

	@Override
	public DateTime buildDateTime(DateTime datetime, int unit) {
		if (unit > 0) {
			return datetime.plus(0, 0, unit * 7, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		} else {
			return datetime.minus(0, 0, Math.abs(unit) * 7, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		}
	}

	@Override
	protected DateTime getFirstDateTime(DateTime datetime) {
		int index = datetime.getWeekIndex();
		return datetime.minus(0, 0, index, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
	}

	@Override
	protected DateTime getLastDateTime(DateTime datetime) {
		int index = datetime.getWeekIndex();
		return datetime.minus(0, 0, (7 - index), 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
	}

	@Override
	public ColumnTitleAdapter getColumnTitleAdapter() {
		return new ColumnTitleAdapter(getActivity(), android.R.layout.simple_list_item_1, getColumnTitles());
	}

	@Override
	public void setupChildDateGridFragment(DateGridFragment dateGridFragment) {
		dateGridFragment.setMode(Mode.WEEK);
	}

	// -----------------------------刷新----------------------------
	/**
	 * 刷新Title
	 */
	@Override
	protected void refreshTitle() {
		String title = year + "-" + month + "-" + week;
		tvTitle.setText(title);
	}

	// --------------------private methods----------------------------
	/**
	 * 得到子标题的数据
	 * 
	 * @return "SUN, MON, TUE, WED, THU, FRI, SAT"
	 */
	private ArrayList<String> getColumnTitles() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");// 第一个空白

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

}
