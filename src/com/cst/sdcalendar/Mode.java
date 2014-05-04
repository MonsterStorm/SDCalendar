package com.cst.sdcalendar;

/**
 * 日历的模式
 * @author song
 *
 */
public enum Mode {
	DAY(2),
	WEEK(8),
	MONTH(7);
	
	int column;
	
	Mode(int column){
		this.column = column;
	}
	
	public int getColumn() {
		return column;
	}
}
