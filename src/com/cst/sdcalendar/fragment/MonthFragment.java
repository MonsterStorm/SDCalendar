package com.cst.sdcalendar.fragment;

import hirondelle.date4j.DateTime;

import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.adapter.MonthCalendarGridAdapter;

import android.os.Bundle;
import android.view.View;

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
	public DateTime buildDateTime(DateTime datetime, int unit){
		if(unit > 0){
			return datetime.plus(0, unit, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		} else {
			return datetime.minus(0, unit, 0, 0, 0, 0,  0, DateTime.DayOverflow.LastDay);
		}
	}
	
	@Override
	protected DateTime getFirstDateTime(DateTime datetime) {
		return datetime.getStartOfMonth();
	}
	
	@Override
	protected DateTime getLastDateTime(DateTime datetime){
		return datetime.getEndOfMonth();
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
