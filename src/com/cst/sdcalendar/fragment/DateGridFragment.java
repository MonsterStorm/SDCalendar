package com.cst.sdcalendar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.cst.sdcalendar.R;
import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.adapter.MonthCalendarGridAdapter;

/**
 * DateGridFragment ����һ��gridView��ͨ��init��������gridView������������ָ��������Ŀ���У���7����7����2��
 * ��ʹ��֮ǰ��Ҫ��Fragment��ӵ���Ļ֮ǰ����adapter��onItemClickListener�������������
 */
public class DateGridFragment extends Fragment {
	//Ԥ��������
	public static final int COLUMN_MONTH = 7;
	public static final int COLUMN_WEEK = 7;
	public static final int COLUMN_DAY = 2;
	
	private int column;//����Ŀ

	private GridView gridView;
	private BaseCalendarGridAdapter gridAdapter;
	
	//����¼�
	private OnItemClickListener onItemClickListener;
	private OnItemLongClickListener onItemLongClickListener;
	
	public void init(int column){
		this.column = column;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		gridView = (GridView) inflater.inflate(R.layout.date_grid_fragment,	container, false);
		gridView.setNumColumns(column);
		
		if (gridAdapter != null) {
			gridView.setAdapter(gridAdapter);
		}

		if (onItemClickListener != null) {
			gridView.setOnItemClickListener(onItemClickListener);
		}
		
		if(onItemLongClickListener != null) {
			gridView.setOnItemLongClickListener(onItemLongClickListener);
		}
		return gridView;
	}
	
	//-----------------------------getters and setters-----------------------------
	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public OnItemLongClickListener getOnItemLongClickListener() {
		return onItemLongClickListener;
	}
	
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	public BaseCalendarGridAdapter getGridAdapter() {
		return gridAdapter;
	}

	public void setGridAdapter(BaseCalendarGridAdapter gridAdapter) {
		this.gridAdapter = gridAdapter;
	}

	public GridView getGridView() {
		return gridView;
	}
}
