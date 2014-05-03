package com.cst.sdcalendar.adapter;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cst.sdcalendar.Mode;
import com.cst.sdcalendar.R;
import com.cst.sdcalendar.fragment.MonthFragment;
import com.cst.sdcalendar.util.CalendarHelper;

/**
 * 月视图adapter
 */
public class WeekCalendarGridAdapter extends BaseCalendarGridAdapter {
	public static final int TYPE_RULE = 0;
	public static final int TYPE_DATE = 1;
	// 当前月份
	protected int month;
	// 当前年
	protected int year;
	// 当前周
	protected int day;
	// 当前时间
	protected DateTime today;
	// 起始日为周的第几天
	protected int startDayOfWeek;

	public WeekCalendarGridAdapter(Context context, int year, int month, int week, HashMap<String, Object> caldroidData, HashMap<String, Object> extraData) {
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
		this.datetimeList = CalendarHelper.getFullDaysForWeekView(this.year, this.month, this.day);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (position % Mode.WEEK.getColumn() == 0) {
			return TYPE_RULE;
		} else {
			return TYPE_DATE;
		}
	}

	/**
	 * 定制Cell的背景和颜色，根据不同的状态
	 * 
	 * @param position
	 * @param cellView
	 */
	protected void customizeTextView(int position, TextView cellView) {
		int type = getItemViewType(position);

		if (type == TYPE_RULE) {//时间尺

		} else {//时间

			cellView.setTextColor(Color.BLACK);

			// Get dateTime of this cell
			DateTime dateInPos = this.datetimeList.get(position);

			// 取到日
			DateTime dateTime = new DateTime(dateInPos.getYear(), dateInPos.getMonth(), dateInPos.getDay(), 0, 0, 0, 0);

			// Set color of the dates in previous / next month
			if (dateTime.getMonth() != month) {
				cellView.setTextColor(resources.getColor(R.color.caldroid_darker_gray));
			}

			boolean shouldResetDiabledView = false;
			boolean shouldResetSelectedView = false;

			// Customize for disabled dates and date outside min/max dates
			if ((minDateTime != null && dateTime.lt(minDateTime)) || (maxDateTime != null && dateTime.gt(maxDateTime)) || (disableDates != null && disableDatesMap.containsKey(dateTime))) {

				cellView.setTextColor(MonthFragment.disabledTextColor);
				if (MonthFragment.disabledBackgroundDrawable == -1) {
					cellView.setBackgroundResource(R.drawable.disable_cell);
				} else {
					cellView.setBackgroundResource(MonthFragment.disabledBackgroundDrawable);
				}

				if (dateTime.equals(getToday())) {
					cellView.setBackgroundResource(R.drawable.red_border_gray_bg);
				}
			} else {
				shouldResetDiabledView = true;
			}

			// Customize for selected dates
			if (selectedDates != null && selectedDatesMap.containsKey(dateTime)) {
				if (MonthFragment.selectedBackgroundDrawable != -1) {
					cellView.setBackgroundResource(MonthFragment.selectedBackgroundDrawable);
				} else {
					cellView.setBackgroundColor(resources.getColor(R.color.caldroid_sky_blue));
				}

				cellView.setTextColor(MonthFragment.selectedTextColor);
			} else {
				shouldResetSelectedView = true;
			}

			if (shouldResetDiabledView && shouldResetSelectedView) {
				// Customize for today
				if (dateTime.equals(getToday())) {
					cellView.setBackgroundResource(R.drawable.red_border);
				} else {
					cellView.setBackgroundResource(R.drawable.cell_bg);
				}
			}

			cellView.setText("" + dateTime.getDay());

			// Set custom color if required
			setCustomResources(dateTime, cellView, cellView);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView cellView = (TextView) convertView;

		int type = getItemViewType(position);
		// For reuse
		if (convertView == null) {
			if(type == TYPE_RULE){
				cellView = (TextView) inflater.inflate(R.layout.date_timerule, null);
			} else {
				cellView = (TextView) inflater.inflate(R.layout.date_cell, null);
			}
		}

		customizeTextView(position, cellView);

		return cellView;
	}

	// --------------------getters and setters-----------------------
	public void setAdapterDateTime(DateTime dateTime) {
		this.month = dateTime.getMonth();
		this.year = dateTime.getYear();
		this.day = dateTime.getDay();
		this.datetimeList = CalendarHelper.getFullDaysForWeekView(this.year, this.month, this.day);
	}

	protected DateTime getToday() {
		if (today == null) {
			today = CalendarHelper.convertDateToDateTime(new Date());
		}
		return today;
	}

}
