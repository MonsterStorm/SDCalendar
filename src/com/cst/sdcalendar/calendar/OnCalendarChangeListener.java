package com.cst.sdcalendar.calendar;

import java.util.Date;

import android.view.View;

/**
 * CaldroidListener inform when user clicks on a valid date (not within disabled
 * dates, and valid between min/max dates)
 * 
 * The method onChangeMonth is optional, user can always override this to listen
 * to month change event
 * 
 * @author thomasdao
 * 
 */
public interface OnCalendarChangeListener {
	/**
	 * Inform client user has clicked on a date
	 * @param date
	 * @param view
	 */
	public void onSelectDate(Date date, View view);

	/**
	 * Inform client user has long clicked on a date
	 * @param date
	 * @param view
	 */
	public void onLongClickDate(Date date, View view);

	/**
	 * change datetime
	 * @param month
	 * @param year
	 */
	public void onChangeDateTime(int year, int month, int day);

	/**
	 * Inform client that CaldroidFragment view has been created and views are
	 * no longer null. Useful for customization of button and text views
	 */
	public void onCaldroidViewCreated();
}
