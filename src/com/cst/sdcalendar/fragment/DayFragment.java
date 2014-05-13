package com.cst.sdcalendar.fragment;

import hirondelle.date4j.DateTime;
import android.annotation.SuppressLint;
import android.widget.GridView;

import com.cst.sdcalendar.Mode;
import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.adapter.ColumnTitleAdapter;
import com.cst.sdcalendar.adapter.DayCalendarGridAdapter;

/**
 * 月视图
 * 
 * @author song
 * 
 */
@SuppressLint("ValidFragment")
public class DayFragment extends BaseCalendarFragment {
	
	public DayFragment(){}
	
	/**
	 * update column
	 * @param column
	 */
	public DayFragment(int column){
		if(column >= 2){
			Mode.DAY.setColumn(column);
		}
	}

	@Override
	public BaseCalendarGridAdapter getNewDatesGridAdapter(DateTime datetime) {
		return new DayCalendarGridAdapter(getActivity(), datetime.getYear(), datetime.getMonth(), datetime.getDay(), getCaldroidData(), extraData);
	}

	@Override
	public DateTime buildCurrentDateTime() {
		return new DateTime(year, month, day, 0, 0, 0, 0);
	}

	@Override
	public DateTime buildDateTime(DateTime datetime, int unit) {
		if (unit > 0) {
			return datetime.plus(0, 0, unit, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		} else {
			return datetime.minus(0, 0, Math.abs(unit), 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
		}
	}

	@Override
	protected DateTime getFirstDateTime(DateTime datetime) {
		return datetime.getStartOfDay();
	}

	@Override
	protected DateTime getLastDateTime(DateTime datetime) {
		return datetime.getEndOfDay();
	}

	@Override
	public ColumnTitleAdapter getColumnTitleAdapter(GridView gvContentTitle) {
		gvContentTitle.setNumColumns(Mode.DAY.getColumn());
		return null;
	}

	@Override
	public void setupChildDateGridFragment(DateGridFragment dateGridFragment) {
		dateGridFragment.setMode(Mode.DAY);
	}

	// -----------------------------刷新----------------------------
	/**
	 * 刷新Title
	 */
	@Override
	protected void refreshTitle() {
		String monthTitle = year + "-" + month + "-" + day;
		tvTitle.setText(monthTitle);
	}

	// --------------------private methods----------------------------
}
