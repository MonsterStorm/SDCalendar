package com.cst.sdcalendar.adapter;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

import com.cst.sdcalendar.fragment.CalendarFragment;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Calendar GridAdapter����
 * 
 * @author song
 * 
 */
public abstract class BaseCalendarGridAdapter extends BaseAdapter {

	protected Context context;
	protected Resources resources;
	// ʱ���
	protected ArrayList<DateTime> datetimeList;
	protected ArrayList<DateTime> disableDates;
	protected ArrayList<DateTime> selectedDates;

	// ʧЧʱ�����ʾ
	protected HashMap<DateTime, Integer> disableDatesMap = new HashMap<DateTime, Integer>();
	// ѡ��ʱ�����ʾ
	protected HashMap<DateTime, Integer> selectedDatesMap = new HashMap<DateTime, Integer>();

	// ��Сʱ��
	protected DateTime minDateTime;
	// ���ʱ��
	protected DateTime maxDateTime;

	// �ڲ�����
	protected HashMap<String, Object> caldroidData;
	// �û�����
	protected HashMap<String, Object> extraData;
	
	public BaseCalendarGridAdapter(Context context, HashMap<String, Object> caldroidData, HashMap<String, Object> extraData){
		this.context = context;
		this.context = context;
		this.caldroidData = caldroidData;
		this.extraData = extraData;
		this.resources = context.getResources();
		populateFromCaldroidData();
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
	 * ��ȡ�ڲ���������caldroidData��ȡ��
	 */
	@SuppressWarnings("unchecked")
	private void populateFromCaldroidData() {
		disableDates = (ArrayList<DateTime>) caldroidData.get(CalendarFragment.DISABLE_DATES);
		if (disableDates != null) {
			disableDatesMap.clear();
			for (DateTime dateTime : disableDates) {
				disableDatesMap.put(dateTime, 1);
			}
		}

		selectedDates = (ArrayList<DateTime>) caldroidData.get(CalendarFragment.SELECTED_DATES);
		if (selectedDates != null) {
			selectedDatesMap.clear();
			for (DateTime dateTime : selectedDates) {
				selectedDatesMap.put(dateTime, 1);
			}
		}

		minDateTime = (DateTime) caldroidData.get(CalendarFragment._MIN_DATE_TIME);
		maxDateTime = (DateTime) caldroidData.get(CalendarFragment._MAX_DATE_TIME);
		
		populateChildFromCaldroidData();
	}
	
	/**
	 * ����ĳһ���ڵı�����������ɫ
	 * @param dateTime
	 * @param backgroundView
	 * @param textView
	 */
	@SuppressWarnings("unchecked")
	protected void setCustomResources(DateTime dateTime, View backgroundView, TextView textView) {
		//������ɫ
		HashMap<DateTime, Integer> backgroundForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData.get(CalendarFragment._BACKGROUND_FOR_DATETIME_MAP);
		if (backgroundForDateTimeMap != null) {
			Integer backgroundResource = backgroundForDateTimeMap.get(dateTime);
			if (backgroundResource != null) {
				backgroundView.setBackgroundResource(backgroundResource.intValue());
			}
		}

		//����������ɫ
		HashMap<DateTime, Integer> textColorForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData.get(CalendarFragment._TEXT_COLOR_FOR_DATETIME_MAP);
		if (textColorForDateTimeMap != null) {
			Integer textColorResource = textColorForDateTimeMap.get(dateTime);
			if (textColorResource != null) {
				textView.setTextColor(resources.getColor(textColorResource.intValue()));
			}
		}
	}
	
	/**
	 * ���ദ�������ʼ��
	 */
	public abstract void populateChildFromCaldroidData();

	// ---------------------getters and setters-----------------------
	public void setCaldroidData(HashMap<String, Object> caldroidData) {
		this.caldroidData = caldroidData;
		//���»�ȡ���в���
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
