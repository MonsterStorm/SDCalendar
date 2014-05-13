package com.cst.sdcalendar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.cst.sdcalendar.Mode;
import com.cst.sdcalendar.R;
import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;

/**
 * DateGridFragment 包含一个gridView，通过init函数设置gridView的列数，可以指定任意数目的列（月7，周7，日2）
 * 在使用之前需要在Fragment添加到屏幕之前设置adapter，onItemClickListener，避免崩溃问题
 */
public class DateGridFragment extends Fragment {
	// 当前模式
	private Mode mode;
	private GridView gridView;
	private Integer stretchMode;
	private BaseCalendarGridAdapter gridAdapter;

	// 点击事件
	private OnItemClickListener onItemClickListener;
	private OnItemLongClickListener onItemLongClickListener;

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setStretchMode(Integer stretchMode) {
		this.stretchMode = stretchMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		gridView = (GridView) inflater.inflate(R.layout.date_grid_fragment, container, false);

		gridView.setNumColumns(mode.getColumn());

		if (gridAdapter != null) {
			gridView.setAdapter(gridAdapter);
		}

		if (stretchMode != null) {
			gridView.setStretchMode(stretchMode);
		}

		if (onItemClickListener != null) {
			gridView.setOnItemClickListener(onItemClickListener);
		}

		if (onItemLongClickListener != null) {
			gridView.setOnItemLongClickListener(onItemLongClickListener);
		}
		return gridView;
	}
	
	// -----------------------------getters and setters-----------------------------
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
