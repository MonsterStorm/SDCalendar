package com.cst.sdcalendar;

import java.io.Serializable;

/**
 * 日历的模式
 * @author song
 *
 */
public enum Mode implements Serializable{
	DAY(8),
	WEEK(8),
	MONTH(7);
	
	int column;
	
	Mode(int column){
		this.column = column;
	}
	
	public int getColumn() {
		return column;
	}
	
	public void setColumn(int column){
		this.column = column;
	}
}
