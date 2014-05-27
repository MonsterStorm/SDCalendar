package com.cst.sdcalendar.adapter;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cst.sdcalendar.fragment.BaseCalendarFragment;
import com.cst.sdcalendar.util.CalendarHelper;

/**
 * Calendar GridAdapter基类
 * 
 * @author song
 * 
 */
public abstract class BaseCalendarGridAdapter extends BaseAdapter {

	public Context context;
	public Resources resources;
	public LayoutInflater mInflater;
	// 时间表
	protected ArrayList<DateTime> datetimeList;
	protected ArrayList<DateTime> disableDates;
	protected ArrayList<DateTime> selectedDates;
	// 当前时间，只精确到日
	protected DateTime today;

	// 失效时间的显示
	protected HashMap<DateTime, Integer> disableDatesMap = new HashMap<DateTime, Integer>();
	// 选中时间的显示
	protected HashMap<DateTime, Integer> selectedDatesMap = new HashMap<DateTime, Integer>();

	// 最小时间
	protected DateTime minDateTime;
	// 最大时间
	protected DateTime maxDateTime;

	// 内部数据
	protected HashMap<String, Object> caldroidData;
	// 用户数据
	protected HashMap<String, Object> extraData;

	public BaseCalendarGridAdapter(Context context, HashMap<String, Object> caldroidData, HashMap<String, Object> extraData) {
		this.context = context;
		this.caldroidData = caldroidData;
		this.extraData = extraData;
		this.resources = context.getResources();
		this.mInflater = LayoutInflater.from(context);
	}

	/**
	 * 子类必须在构造方法后调用该方法
	 */
	public void init() {
		populateChildFromCaldroidData();
	}

	@Override
	public int getCount() {
		if (this.datetimeList != null) {
			return this.datetimeList.size();
		} else {
			return 0;
		}
	}

	@Override
	public DateTime getItem(int pos) {
		if (this.datetimeList != null) {
			return this.datetimeList.get(pos);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	/**
	 * 获取内部参数，从caldroidData中取得
	 */
	@SuppressWarnings("unchecked")
	private void populateFromCaldroidData() {
		disableDates = (ArrayList<DateTime>) caldroidData.get(BaseCalendarFragment.DISABLE_DATES);
		if (disableDates != null) {
			disableDatesMap.clear();
			for (DateTime dateTime : disableDates) {
				disableDatesMap.put(dateTime, 1);
			}
		}

		selectedDates = (ArrayList<DateTime>) caldroidData.get(BaseCalendarFragment.SELECTED_DATES);
		if (selectedDates != null) {
			selectedDatesMap.clear();
			for (DateTime dateTime : selectedDates) {
				selectedDatesMap.put(dateTime, 1);
			}
		}

		minDateTime = (DateTime) caldroidData.get(BaseCalendarFragment._MIN_DATE_TIME);
		maxDateTime = (DateTime) caldroidData.get(BaseCalendarFragment._MAX_DATE_TIME);

		populateChildFromCaldroidData();
	}

	/**
	 * 处理某一日期的背景和字体颜色
	 * @param dateTime
	 * @param backgroundView
	 * @param textView
	 */
	@SuppressWarnings("unchecked")
	protected void setCustomResources(DateTime dateTime, View backgroundView, TextView textView) {
		// 背景颜色
		HashMap<DateTime, Integer> backgroundForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData.get(BaseCalendarFragment._BACKGROUND_FOR_DATETIME_MAP);
		if (backgroundForDateTimeMap != null) {
			Integer backgroundResource = backgroundForDateTimeMap.get(dateTime);
			if (backgroundResource != null) {
				backgroundView.setBackgroundResource(backgroundResource.intValue());
			}
		}

		// 设置文字颜色
		HashMap<DateTime, Integer> textColorForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData.get(BaseCalendarFragment._TEXT_COLOR_FOR_DATETIME_MAP);
		if (textColorForDateTimeMap != null) {
			Integer textColorResource = textColorForDateTimeMap.get(dateTime);
			if (textColorResource != null) {
				textView.setTextColor(resources.getColor(textColorResource.intValue()));
			}
		}
	}

	/**
	 * 子类处理参数初始化
	 */
	public abstract void populateChildFromCaldroidData();

	/**
	 * 设置adapter的时间
	 */
	public abstract void setAdapterDateTime(DateTime datetime);

	/**
	 * get today
	 * @return
	 */
	public DateTime getToday() {
		if (today == null) {
			DateTime dateTime = CalendarHelper.convertDateToDateTime(new Date());
			// 取到日
			today = CalendarHelper.precisionToDay(dateTime);
		}
		return today;
	}

	// ---------------------getters and setters-----------------------
	public void setCaldroidData(HashMap<String, Object> caldroidData) {
		this.caldroidData = caldroidData;
		// 重新获取所有参数
		populateFromCaldroidData();
	}

	public HashMap<String, Object> getExtraData() {
		return extraData;
	}

	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}

	public HashMap<String, Object> getCaldroidData() {
		return caldroidData;
	}

	public ArrayList<DateTime> getDatetimeList() {
		return datetimeList;
	}

	public DateTime getMinDateTime() {
		return minDateTime;
	}

	public void setMinDateTime(DateTime minDateTime) {
		this.minDateTime = minDateTime;
	}

	public DateTime getMaxDateTime() {
		return maxDateTime;
	}

	public void setMaxDateTime(DateTime maxDateTime) {
		this.maxDateTime = maxDateTime;
	}

	public ArrayList<DateTime> getDisableDates() {
		return disableDates;
	}

	public void setDisableDates(ArrayList<DateTime> disableDates) {
		this.disableDates = disableDates;
	}

	public ArrayList<DateTime> getSelectedDates() {
		return selectedDates;
	}

	public void setSelectedDates(ArrayList<DateTime> selectedDates) {
		this.selectedDates = selectedDates;
	}
}
