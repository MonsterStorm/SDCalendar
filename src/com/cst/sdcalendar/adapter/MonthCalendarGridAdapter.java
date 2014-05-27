package com.cst.sdcalendar.adapter;

import hirondelle.date4j.DateTime;

import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cst.sdcalendar.R;
import com.cst.sdcalendar.fragment.MonthFragment;
import com.cst.sdcalendar.util.CalendarHelper;

/**
 * 月视图adapter
 */
public class MonthCalendarGridAdapter extends BaseCalendarGridAdapter {
	// 当前月份
	protected int month;
	// 当前年
	protected int year;
	// 当前时间
	protected DateTime today;
	// 起始日为周的第几天
	protected int startDayOfWeek;
	// 是否显示6行视图
	protected boolean sixWeeksInCalendar;

	public MonthCalendarGridAdapter(Context context, int year, int month, HashMap<String, Object> caldroidData, HashMap<String, Object> extraData) {
		super(context, caldroidData, extraData);
		this.month = month;
		this.year = year;
		init();
	}

	/**
	 * 处理子类参数
	 */
	public void populateChildFromCaldroidData() {
		startDayOfWeek = (Integer) caldroidData.get(MonthFragment.START_DAY_OF_WEEK);
		sixWeeksInCalendar = (Boolean) caldroidData.get(MonthFragment.SIX_WEEKS_IN_CALENDAR);
		this.datetimeList = CalendarHelper.getFullWeeksForMonthView(this.month, this.year, startDayOfWeek, sixWeeksInCalendar);
	}

	/**
	 * 定制Cell的背景和颜色，根据不同的状态
	 * 
	 * @param position
	 * @param cellView
	 */
	protected void customizeTextView(int position, TextView cellView){
		cellView.setTextColor(Color.BLACK);

		// Get dateTime of this cell
		DateTime dateInPos = this.datetimeList.get(position);

		// 取到日
		DateTime dateTime = CalendarHelper.precisionToDay(dateInPos);

		boolean shouldResetDiabledView = false;
		boolean shouldResetSelectedView = false;

		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.lt(minDateTime)) || (maxDateTime != null && dateTime.gt(maxDateTime)) || (disableDates != null && disableDatesMap.containsKey(dateTime))) {

			cellView.setTextColor(MonthFragment.disabledTextColor);
			if (MonthFragment.disabledBackgroundDrawable == -1) {
				cellView.setBackgroundResource(R.drawable.cell_disable);
			} else {
				cellView.setBackgroundResource(MonthFragment.disabledBackgroundDrawable);
			}

			if (dateTime.equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.cell_today);
			}
		} else {
			shouldResetDiabledView = true;
		}

		// Customize for selected dates
		if (selectedDates != null && selectedDatesMap.containsKey(dateTime)) {
			if (MonthFragment.selectedBackgroundDrawable != -1) {
				cellView.setBackgroundResource(MonthFragment.selectedBackgroundDrawable);
			} else {
				cellView.setBackgroundResource(R.drawable.cell_selected);
			}

			cellView.setTextColor(MonthFragment.selectedTextColor);
		} else {
			shouldResetSelectedView = true;
		}

		if (shouldResetDiabledView && shouldResetSelectedView) {
			// Customize for today
			if (dateTime.equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.cell_today);
			} else {
				cellView.setBackgroundResource(R.drawable.cell_default);
			}
		}

		// Set color of the dates in previous / next month
		if (dateTime.getMonth() != month) {
			cellView.setBackgroundResource(R.drawable.cell_disable);
			cellView.setTextColor(resources.getColor(R.color.darker_gray));
		}

		cellView.setText("" + dateTime.getDay());

		// Set custom color if required
		setCustomResources(dateTime, cellView, cellView);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView cellView = (TextView) convertView;

		// For reuse
		if (convertView == null) {
			cellView = (TextView) inflater.inflate(R.layout.date_cell_month, null);
		}

		customizeTextView(position, cellView);

		return cellView;
	}

	// --------------------getters and setters-----------------------
	public void setAdapterDateTime(DateTime dateTime) {
		this.month = dateTime.getMonth();
		this.year = dateTime.getYear();
		this.datetimeList = CalendarHelper.getFullWeeksForMonthView(this.month, this.year, startDayOfWeek, sixWeeksInCalendar);
	}
}
