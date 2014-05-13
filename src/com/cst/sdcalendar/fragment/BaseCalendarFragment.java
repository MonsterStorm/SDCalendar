package com.cst.sdcalendar.fragment;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.cst.sdcalendar.R;
import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.adapter.ColumnTitleAdapter;
import com.cst.sdcalendar.calendar.OnCalendarChangeListener;
import com.cst.sdcalendar.util.CalendarHelper;
import com.cst.sdcalendar.viewpager.ContentPagerAdapter;
import com.cst.sdcalendar.viewpager.InfinitePagerAdapter;
import com.cst.sdcalendar.viewpager.InfiniteViewPager;

/**
 * Calendar基类
 */

@SuppressLint("DefaultLocale")
public abstract class BaseCalendarFragment extends DialogFragment {
	public String TAG = "BaseCalendarFragment";

	// default date format
	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";

	/**
	 * 周
	 */
	public static int SUNDAY = 1;
	public static int MONDAY = 2;
	public static int TUESDAY = 3;
	public static int WEDNESDAY = 4;
	public static int THURSDAY = 5;
	public static int FRIDAY = 6;
	public static int SATURDAY = 7;

	/**
	 * 定制选中日期的背景和字体颜色
	 */
	public static int selectedBackgroundDrawable = -1;
	public static int selectedTextColor = Color.BLACK;

	/**
	 * 定制不可用时间的背景和字体颜色
	 */
	public static int disabledBackgroundDrawable = -1;
	public static int disabledTextColor = Color.GRAY;

	/**
	 * 页数
	 */
	public final static int NUMBER_OF_PAGES = 4;

	/**
	 * View组件
	 */
	protected Button btnArrowLeft;// 左边按钮
	protected Button btnArrowRight;// 右边按钮
	protected TextView tvTitle;// 标题
	protected GridView gvContentTitle;// 内容标题
	protected InfiniteViewPager vpContent;// 主体部分
	protected DatePageChangeListener pageChangeListener;
	protected ArrayList<DateGridFragment> fragments;

	/**
	 * 初始化参数
	 */
	public final static String DIALOG_TITLE = "dialogTitle";// 标题
	public final static String DAY = "day";
	public final static String MONTH = "month";
	public final static String YEAR = "year";
	public final static String DISABLE_DATES = "disableDates";// 失效日期
	public final static String SELECTED_DATES = "selectedDates";// 选中日期
	public final static String MIN_DATE = "minDate";// 最小日期
	public final static String MAX_DATE = "maxDate";// 最大日期
	// 效果参数
	public final static String SHOW_NAVIGATION_ARROWS = "showNavigationArrows";// 是否显示箭头
	public final static String ENABLE_SWIPE = "enableSwipe";// 是否允许滑动
	public final static String ENABLE_CLICK_ON_DISABLED_DATES = "enableClickOnDisabledDates";

	/**
	 * 初始化数据
	 */
	protected String dialogTitle;// 对话框标题
	protected int day = -1;
	protected int month = -1;
	protected int year = -1;
	protected ArrayList<DateTime> disableDates = new ArrayList<DateTime>();// 失效时间
	protected ArrayList<DateTime> selectedDates = new ArrayList<DateTime>();// 选中时间
	protected DateTime minDateTime;// 最小时间
	protected DateTime maxDateTime;// 最大时间
	protected ArrayList<DateTime> datetimeList;// 时间

	/**
	 * 内部使用
	 */
	public final static String _MIN_DATE_TIME = "_minDateTime";
	public final static String _MAX_DATE_TIME = "_maxDateTime";
	public final static String _BACKGROUND_FOR_DATETIME_MAP = "_backgroundForDateTimeMap";
	public final static String _TEXT_COLOR_FOR_DATETIME_MAP = "_textColorForDateTimeMap";

	// 内部数据
	protected HashMap<String, Object> caldroidData = new HashMap<String, Object>();
	// 用户数据
	protected HashMap<String, Object> extraData = new HashMap<String, Object>();
	// 某一时间背景
	protected HashMap<DateTime, Integer> backgroundForDateTimeMap = new HashMap<DateTime, Integer>();
	// 某一时间文字颜色
	protected HashMap<DateTime, Integer> textColorForDateTimeMap = new HashMap<DateTime, Integer>();;
	// 包含4页，用于重用
	protected ArrayList<BaseCalendarGridAdapter> datePagerAdapters = new ArrayList<BaseCalendarGridAdapter>();

	// 导航特效
	protected boolean enableSwipe = true;// 是否允许滑动切换
	protected boolean showNavigationArrows = true;// 是否显示导航箭头
	protected boolean enableClickOnDisabledDates = false;// 是否允许无效时间的点击效果

	// -------------------事件处理变量----------------------
	// 日期的点击事件
	private OnItemClickListener dateItemClickListener;

	// 日期的长按事件
	private OnItemLongClickListener dateItemLongClickListener;

	// 自定义事件处理
	private OnCalendarChangeListener caldroidListener;

	/**
	 * 用于子类定制显示，创建一个adapter
	 */
	public abstract BaseCalendarGridAdapter getNewDatesGridAdapter(DateTime dateTime);

	// ----------------------------------控件状态控制------------------------------------
	/**
	 * 将当前控件状态保存到states
	 * 
	 * @param outState
	 * @param key
	 */
	public void saveStatesToKey(Bundle outState, String key) {
		outState.putBundle(key, getSavedStates());
	}

	/**
	 * 得到当前保存的状态，用于处理旋转
	 */
	public Bundle getSavedStates() {
		Bundle bundle = new Bundle();
		bundle.putInt(DAY, day);
		bundle.putInt(MONTH, month);
		bundle.putInt(YEAR, year);

		if (dialogTitle != null) {
			bundle.putString(DIALOG_TITLE, dialogTitle);
		}

		if (selectedDates != null && selectedDates.size() > 0) {
			bundle.putStringArrayList(SELECTED_DATES, CalendarHelper.convertToStringList(selectedDates));
		}

		if (disableDates != null && disableDates.size() > 0) {
			bundle.putStringArrayList(DISABLE_DATES, CalendarHelper.convertToStringList(disableDates));
		}

		if (minDateTime != null) {
			bundle.putString(MIN_DATE, minDateTime.format(DATE_FORMAT_DEFAULT));
		}

		if (maxDateTime != null) {
			bundle.putString(MAX_DATE, maxDateTime.format(DATE_FORMAT_DEFAULT));
		}

		bundle.putBoolean(SHOW_NAVIGATION_ARROWS, showNavigationArrows);
		bundle.putBoolean(ENABLE_SWIPE, enableSwipe);

		getChildSavedStates(bundle);

		return bundle;
	}

	/**
	 * 子类实现，添加子类需要用到的状态
	 * 
	 * @param bundle
	 */
	public void getChildSavedStates(Bundle bundle) {
	}

	/**
	 * 用于对话框的状态恢复
	 * 
	 * @param savedInstanceState
	 * @param key
	 * @param dialogTag
	 */
	public void restoreDialogStatesFromKey(FragmentManager manager, Bundle savedInstanceState, String key, String dialogTag) {
		restoreStatesFromKey(savedInstanceState, key);

		BaseCalendarFragment existingDialog = (BaseCalendarFragment) manager.findFragmentByTag(dialogTag);
		if (existingDialog != null) {
			existingDialog.dismiss();
			show(manager, dialogTag);
		}
	}

	/**
	 * 从state中指定的key恢复
	 * 
	 * @param savedInstanceState
	 * @param key
	 */
	public void restoreStatesFromKey(Bundle savedInstanceState, String key) {
		if (savedInstanceState != null && savedInstanceState.containsKey(key)) {
			Bundle caldroidSavedState = savedInstanceState.getBundle(key);
			setArguments(caldroidSavedState);
		}
	}

	/**
	 * Below code fixed the issue viewpager disappears in dialog mode on
	 * orientation change
	 * 
	 * Code taken from Andy Dennie and Zsombor Erdody-Nagy
	 * http://stackoverflow.com/questions/8235080/fragments-dialogfragment
	 * -and-screen-rotation
	 */
	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	/**
	 * 初始化view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		retrieveInitialArgs();

		// 支持保存状态到dialog中
		if (getDialog() != null) {
			setRetainInstance(true);
		}

		// 初始化布局
		View view = inflater.inflate(R.layout.calendar_view, container, false);

		// 标题
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);

		// 导航按钮
		btnArrowLeft = (Button) view.findViewById(R.id.btnLeftArrow);
		btnArrowRight = (Button) view.findViewById(R.id.btnRightArrow);

		// 导航条左按钮
		btnArrowLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prev();
			}
		});

		// 导航条右按钮
		btnArrowRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				next();
			}
		});

		// 是否显示箭头
		setShowNavigationArrows(showNavigationArrows);

		// 标题
		gvContentTitle = (GridView) view.findViewById(R.id.gvColumnTitle);
		ColumnTitleAdapter columnTitleAdapter = getColumnTitleAdapter(gvContentTitle);
		if (columnTitleAdapter != null) {
			gvContentTitle.setAdapter(columnTitleAdapter);
		} else {
			gvContentTitle.setVisibility(View.GONE);
		}

		// 设置gridview中的所有View，这些View可以被回收
		setupDateGridPages(view);

		// 刷新显示
		refreshView();

		// 通知客户端view创建成功，客户端需要在这里定制按钮和TextView
		if (caldroidListener != null) {
			caldroidListener.onCaldroidViewCreated();
		}

		return view;
	}

	/**
	 * 得到列标题
	 * 如果返回null，则不显示标题
	 * @return
	 */
	public abstract ColumnTitleAdapter getColumnTitleAdapter(GridView gvContentTitle);
	
	/**
	 * 得到初始参数，通过setArguments方式得到的数据
	 */
	protected void retrieveInitialArgs() {
		Bundle args = getArguments();
		if (args != null) {
			day = args.getInt(DAY, -1);
			month = args.getInt(MONTH, -1);
			year = args.getInt(YEAR, -1);
			// title of dialog
			dialogTitle = args.getString(DIALOG_TITLE);

			Dialog dialog = getDialog();
			if (dialog != null) {
				if (dialogTitle != null) {
					dialog.setTitle(dialogTitle);
				} else {// 如果用户要求不显示title
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				}
			}

			// 是否显示箭头
			showNavigationArrows = args.getBoolean(SHOW_NAVIGATION_ARROWS, true);

			// 是否允许滑动切换
			enableSwipe = args.getBoolean(ENABLE_SWIPE, true);

			// 是否允许点击无效日期
			enableClickOnDisabledDates = args.getBoolean(ENABLE_CLICK_ON_DISABLED_DATES, false);

			// 得到无效日期
			ArrayList<String> disableDateStrings = args.getStringArrayList(DISABLE_DATES);
			if (disableDateStrings != null && disableDateStrings.size() > 0) {
				disableDates.clear();
				for (String dateString : disableDateStrings) {
					DateTime dt = CalendarHelper.getDateTimeFromString(dateString, DATE_FORMAT_DEFAULT);
					disableDates.add(dt);
				}
			}

			// 得到选中日期
			ArrayList<String> selectedDateStrings = args.getStringArrayList(SELECTED_DATES);
			if (selectedDateStrings != null && selectedDateStrings.size() > 0) {
				selectedDates.clear();
				for (String dateString : selectedDateStrings) {
					DateTime dt = CalendarHelper.getDateTimeFromString(dateString, DATE_FORMAT_DEFAULT);
					selectedDates.add(dt);
				}
			}

			// 得到最小日期
			String minDateTimeString = args.getString(MIN_DATE);
			if (minDateTimeString != null) {
				minDateTime = CalendarHelper.getDateTimeFromString(minDateTimeString, null);
			}

			// 得到最大日期
			String maxDateTimeString = args.getString(MAX_DATE);
			if (maxDateTimeString != null) {
				maxDateTime = CalendarHelper.getDateTimeFromString(maxDateTimeString, null);
			}

			// 处理子类的数据
			retrieveChildInitialArgs(args);
		}
		if (month == -1 || year == -1 || day == -1) {
			DateTime dateTime = DateTime.today(TimeZone.getDefault());
			year = dateTime.getYear();
			month = dateTime.getMonth();
			day = dateTime.getDay();
		}
	}

	/**
	 * 子类覆盖的方法，获取子类的参数
	 * 
	 * @param bundle
	 */
	public void retrieveChildInitialArgs(Bundle bundle) {
	}

	/**
	 * 设置4页包含日期的gridview，这些页面可以被回收
	 * 
	 * @param view
	 */
	private void setupDateGridPages(View view) {
		// 得到当前日期
		DateTime currentDateTime = buildCurrentDateTime();

		// 设置pageChangeListener
		pageChangeListener = new DatePageChangeListener();
		pageChangeListener.setCurrentDateTime(currentDateTime);

		// 当前日期
		BaseCalendarGridAdapter adapter0 = getNewDatesGridAdapter(currentDateTime);

		// 时间
		datetimeList = adapter0.getDatetimeList();

		// 下一个日期
		DateTime nextDateTime = buildDateTime(currentDateTime, 1);
		BaseCalendarGridAdapter adapter1 = getNewDatesGridAdapter(nextDateTime);

		// 下两个日期
		DateTime next2DateTime = buildDateTime(nextDateTime, 1);
		BaseCalendarGridAdapter adapter2 = getNewDatesGridAdapter(next2DateTime);

		// 前一个日期
		DateTime prevDateTime = buildDateTime(currentDateTime, -1);
		BaseCalendarGridAdapter adapter3 = getNewDatesGridAdapter(prevDateTime);

		// 添加所有adapter
		datePagerAdapters.add(adapter0);
		datePagerAdapters.add(adapter1);
		datePagerAdapters.add(adapter2);
		datePagerAdapters.add(adapter3);

		// 设置adapter到Listener，当页面改变的时候刷新adapter
		pageChangeListener.setCaldroidGridAdapters(datePagerAdapters);

		// 设置无限滑动的viewpager和pageradapter，用于重用fragemnts
		vpContent = (InfiniteViewPager) view.findViewById(R.id.vpContent);

		// 是否允许滑动
		vpContent.setEnabled(enableSwipe);

		// set dates in list, useful for calculate height of content
		vpContent.setDatetimeList(datetimeList);

		setupChildDateGridPages(vpContent);

		// 内容
		final ContentPagerAdapter pagerAdapter = new ContentPagerAdapter(getActivity().getSupportFragmentManager());

		// 在view绘制之前，给fragment设置初始数据
		fragments = pagerAdapter.getFragments();
		for (int i = 0; i < NUMBER_OF_PAGES; i++) {
			DateGridFragment dateGridFragment = fragments.get(i);
			BaseCalendarGridAdapter adapter = datePagerAdapters.get(i);
			dateGridFragment.setGridAdapter(adapter);
			dateGridFragment.setOnItemClickListener(getDateItemClickListener());
			dateGridFragment.setOnItemLongClickListener(getDateItemLongClickListener());
			// 子类的dateGridAdapter
			setupChildDateGridFragment(dateGridFragment);
		}

		// 设置InfinitePagerAdapter围绕BaseGridPagerAdapter滑动
		InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(pagerAdapter);

		// 使用infinitePagerAdapter来为dateViewPager提供数据
		vpContent.setAdapter(infinitePagerAdapter);

		// 设置 pageChangeListener
		vpContent.setOnPageChangeListener(pageChangeListener);
	}

	/**
	 * 得到当前日期
	 * 
	 * @return
	 */
	public abstract DateTime buildCurrentDateTime();

	/**
	 * 得到一个新日期，根据当前定义的unit，unit>0增加日期，否则减小日期
	 * 
	 * @param dateTime
	 * @return
	 */
	public abstract DateTime buildDateTime(DateTime dateTime, int unit);

	/**
	 * 设置子类的page数据
	 */
	public void setupChildDateGridPages(InfiniteViewPager vpContent) {
	}

	/**
	 * 设置子类的date grid fragment
	 */
	public void setupChildDateGridFragment(DateGridFragment dateGridFragment) {
	}

	@Override
	public void onDetach() {
		super.onDetach();

		setCaldroidListener(null);

		if (fragments != null) {// 清空所有fragments，否则会出现fragment重复的bug
			try{
				for (Fragment frag : fragments) {
					getActivity().getSupportFragmentManager().beginTransaction().remove(frag).commit();
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		// 可以用下列方法清空，但是在新版本中不可用
		// try {
		// Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
		// childFragmentManager.setAccessible(true);
		// childFragmentManager.set(this, null);
		// } catch (NoSuchFieldException e) {
		// throw new RuntimeException(e);
		// } catch (IllegalAccessException e) {
		// throw new RuntimeException(e);
		// }
	}

	// ------------------------封装方法----------------------------
	/**
	 * 当参数变化的时候刷新视图，调用的时候需要线改变参数，后调用
	 */
	public void refreshView() {

		refreshTitle();

		// 刷新DateGrid
		for (BaseCalendarGridAdapter adapter : datePagerAdapters) {
			// 重置内部数据
			adapter.setCaldroidData(getCaldroidData());

			// 重置额外数据
			adapter.setExtraData(extraData);

			// 刷新View
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 当用户滑动的时候改变Title
	 */
	protected void refreshTitle() {
	}

	/**
	 * 设置日历的日期
	 * 
	 * @param date
	 */
	public void setCalendarDate(Date date) {
		setCalendarDateTime(CalendarHelper.convertDateToDateTime(date));
	}

	/**
	 * 设置日历的时间
	 * 
	 * @param dateTime
	 */
	public void setCalendarDateTime(DateTime dateTime) {
		year = dateTime.getYear();
		month = dateTime.getMonth();
		day = dateTime.getDay();

		// Notify listener
		if (caldroidListener != null) {
			caldroidListener.onChangeDateTime(year, month, day);
		}

		refreshView();
	}

	/**
	 * 滑动到指定的日期
	 * 
	 * @param date
	 */
	public void moveToDate(Date date) {
		moveToDateTime(CalendarHelper.convertDateToDateTime(date));
	}

	/**
	 * 得到该单位下的第一个时间
	 * 
	 * @return
	 */
	protected abstract DateTime getFirstDateTime(DateTime datetime);

	/**
	 * 得到该单位下的最后一个时间
	 * 
	 * @return
	 */
	protected abstract DateTime getLastDateTime(DateTime datetime);

	/**
	 * 移动到指定日期，带动画，格式
	 * 
	 * @param dateTime
	 */
	public void moveToDateTime(DateTime datetime) {
		DateTime currentDateTime = buildCurrentDateTime();

		if (datetime.getYear() == year && datetime.getMonth() == month && datetime.getDay() == day) {
			return;
		}

		DateTime fistDateTime = getFirstDateTime(currentDateTime);
		DateTime lastDateTime = getLastDateTime(currentDateTime);

		// 当目标时间小于第一个时间，向左滑动
		if (datetime.lt(fistDateTime)) {
			DateTime newFirstDateTime = buildDateTime(datetime, 1);
			// 刷新adapters
			pageChangeListener.setCurrentDateTime(newFirstDateTime);
			int currentItem = vpContent.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// 向左滑动
			vpContent.setCurrentItem(currentItem - 1);
		} else if (datetime.gt(lastDateTime)) {// 目标时间大于最后一个时间，向右滑动，时间增加
			DateTime newLastDateTime = buildDateTime(datetime, -1);
			// 刷新adapters
			pageChangeListener.setCurrentDateTime(newLastDateTime);
			int currentItem = vpContent.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// 向右滑动
			vpContent.setCurrentItem(currentItem + 1);
		}

	}

	// -----------------------------listeners---------------------------------
	/**
	 * DatePageChangeListener 当用户滑动的时候刷新数据
	 */
	public class DatePageChangeListener implements OnPageChangeListener {
		private int currentPage = InfiniteViewPager.OFFSET;
		private DateTime currentDateTime;
		private ArrayList<BaseCalendarGridAdapter> caldroidGridAdapters;

		/**
		 * 得到dateViewPager的当前pager
		 * 
		 * @return
		 */
		public int getCurrentPage() {
			return currentPage;
		}

		/**
		 * 设置当前的page
		 * 
		 * @param currentPage
		 */
		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}

		/**
		 * 得到当前选中的时间
		 * 
		 * @return
		 */
		public DateTime getCurrentDateTime() {
			return currentDateTime;
		}

		/**
		 * 设置当前时间
		 * 
		 * @param dateTime
		 */
		public void setCurrentDateTime(DateTime dateTime) {
			this.currentDateTime = dateTime;
			setCalendarDateTime(currentDateTime);
		}

		/**
		 * 返回4个Adapter
		 */
		public ArrayList<BaseCalendarGridAdapter> getCaldroidGridAdapters() {
			return caldroidGridAdapters;
		}

		/**
		 * 设置adapter
		 * 
		 * @param caldroidGridAdapters
		 */
		public void setCaldroidGridAdapters(ArrayList<BaseCalendarGridAdapter> caldroidGridAdapters) {
			this.caldroidGridAdapters = caldroidGridAdapters;
		}

		/**
		 * 得到显示的下一个位置
		 * 
		 * @param position
		 * @return
		 */
		private int getNext(int position) {
			return (position + 1) % BaseCalendarFragment.NUMBER_OF_PAGES;
		}

		/**
		 * 得到显示上的前一个位置
		 * 
		 * @param position
		 * @return
		 */
		private int getPrevious(int position) {
			return (position + 3) % BaseCalendarFragment.NUMBER_OF_PAGES;
		}

		/**
		 * 得到显示上的当前位置
		 * 
		 * @param position
		 * @return
		 */
		public int getCurrent(int position) {
			return position % BaseCalendarFragment.NUMBER_OF_PAGES;
		}

		@Override
		public void onPageScrollStateChanged(int position) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void refreshAdapters(int position) {
			// 得到所有要刷新的adapter
			BaseCalendarGridAdapter currentAdapter = caldroidGridAdapters.get(getCurrent(position));
			BaseCalendarGridAdapter prevAdapter = caldroidGridAdapters.get(getPrevious(position));
			BaseCalendarGridAdapter nextAdapter = caldroidGridAdapters.get(getNext(position));

			if (position == currentPage) {
				// 刷新当前Adapter

				currentAdapter.setAdapterDateTime(currentDateTime);
				currentAdapter.notifyDataSetChanged();

				// 刷新前一个Adapter
				prevAdapter.setAdapterDateTime(buildDateTime(currentDateTime, -1));
				prevAdapter.notifyDataSetChanged();

				// 刷新下一个Adapter
				nextAdapter.setAdapterDateTime(buildDateTime(currentDateTime, 1));
				nextAdapter.notifyDataSetChanged();
			} else if (position > currentPage) {// 向右滑动
				// Update current date time to next month
				currentDateTime = buildDateTime(currentDateTime, 1);

				// Refresh the adapter of next gridview
				nextAdapter.setAdapterDateTime(buildDateTime(currentDateTime, 1));
				nextAdapter.notifyDataSetChanged();

			} else {// 向左滑动
				// Update current date time to previous month
				currentDateTime = buildDateTime(currentDateTime, -1);

				// Refresh the adapter of previous gridview
				prevAdapter.setAdapterDateTime(buildDateTime(currentDateTime, -1));
				prevAdapter.notifyDataSetChanged();
			}

			// Update current page
			currentPage = position;
		}

		/**
		 * 刷新fragments
		 */
		@Override
		public void onPageSelected(int position) {
			refreshAdapters(position);

			// Update current date time of the selected page
			setCalendarDateTime(currentDateTime);

			// 刷新所有当前区域的时间
			BaseCalendarGridAdapter currentAdapter = caldroidGridAdapters.get(position % BaseCalendarFragment.NUMBER_OF_PAGES);

			// 刷新List中的时间
			datetimeList.clear();
			datetimeList.addAll(currentAdapter.getDatetimeList());
		}

	}

	/**
	 * 点击效果（在非无效状态，并且在最小/最大时间区域内
	 * 
	 * @return
	 */
	private OnItemClickListener getDateItemClickListener() {
		if (dateItemClickListener == null) {
			dateItemClickListener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					DateTime dateTime = datetimeList.get(position);

					if (caldroidListener != null) {
						if (!enableClickOnDisabledDates) {
							if ((minDateTime != null && dateTime.lt(minDateTime)) || (maxDateTime != null && dateTime.gt(maxDateTime)) || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {
								return;
							}
						}

						Date date = CalendarHelper.convertDateTimeToDate(dateTime);
						caldroidListener.onSelectDate(date, view);
					}
				}
			};
		}

		return dateItemClickListener;
	}

	/**
	 * 长按点击效果（在非无效状态，并且在最小/最大时间区域内
	 * 
	 * @return
	 */
	private OnItemLongClickListener getDateItemLongClickListener() {
		if (dateItemLongClickListener == null) {
			dateItemLongClickListener = new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

					DateTime dateTime = datetimeList.get(position);

					if (caldroidListener != null) {
						if (!enableClickOnDisabledDates) {
							if ((minDateTime != null && dateTime.lt(minDateTime)) || (maxDateTime != null && dateTime.gt(maxDateTime)) || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {
								return false;
							}
						}
						Date date = CalendarHelper.convertDateTimeToDate(dateTime);
						caldroidListener.onLongClickDate(date, view);
					}

					return true;
				}
			};
		}

		return dateItemLongClickListener;
	}

	// ----------------------------getters and setters------------------------
	/**
	 * 得到所有的 fragments
	 */
	public ArrayList<DateGridFragment> getFragments() {
		return fragments;
	}

	/**
	 * 得到左边按钮
	 */
	public Button getLeftArrowButton() {
		return btnArrowLeft;
	}

	/**
	 * 得到右边按钮
	 * 
	 * @return
	 */
	public Button getRightArrowButton() {
		return btnArrowRight;
	}

	/**
	 * 得到title文字
	 */
	public TextView getTitleTextView() {
		return tvTitle;
	}

	/**
	 * 设置title的文字
	 * 
	 * @param titleView
	 */
	public void setTitleTextView(TextView titleView) {
		this.tvTitle = titleView;
	}

	/**
	 * 内部用到的数据
	 * 
	 * @return
	 */
	public HashMap<String, Object> getCaldroidData() {
		caldroidData.clear();
		caldroidData.put(DISABLE_DATES, disableDates);
		caldroidData.put(SELECTED_DATES, selectedDates);
		caldroidData.put(_MIN_DATE_TIME, minDateTime);
		caldroidData.put(_MAX_DATE_TIME, maxDateTime);

		getChildCaldroidData();

		// For internal use
		caldroidData.put(_BACKGROUND_FOR_DATETIME_MAP, backgroundForDateTimeMap);
		caldroidData.put(_TEXT_COLOR_FOR_DATETIME_MAP, textColorForDateTimeMap);

		return caldroidData;
	}

	/**
	 * 得到子类的数据
	 */
	public void getChildCaldroidData() {
	}

	/**
	 * 得到grid view的adapter，用于设置自定义数据，以及刷新gridview的数据
	 * 
	 * @return
	 */
	public ArrayList<BaseCalendarGridAdapter> getDatePagerAdapters() {
		return datePagerAdapters;
	}

	/**
	 * Set backgroundForDateMap
	 */
	public void setBackgroundResourceForDates(HashMap<Date, Integer> backgroundForDateMap) {
		// Clear first
		backgroundForDateTimeMap.clear();

		if (backgroundForDateMap == null || backgroundForDateMap.size() == 0) {
			return;
		}

		for (Date date : backgroundForDateMap.keySet()) {
			Integer resource = backgroundForDateMap.get(date);
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
			backgroundForDateTimeMap.put(dateTime, resource);
		}
	}

	public void setBackgroundResourceForDateTimes(HashMap<DateTime, Integer> backgroundForDateTimeMap) {
		this.backgroundForDateTimeMap.putAll(backgroundForDateTimeMap);
	}

	public void setBackgroundResourceForDate(int backgroundRes, Date date) {
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
		backgroundForDateTimeMap.put(dateTime, Integer.valueOf(backgroundRes));
	}

	public void setBackgroundResourceForDateTime(int backgroundRes, DateTime dateTime) {
		backgroundForDateTimeMap.put(dateTime, Integer.valueOf(backgroundRes));
	}

	/**
	 * Set textColorForDateMap
	 * 
	 * @return
	 */
	public void setTextColorForDates(HashMap<Date, Integer> textColorForDateMap) {
		// Clear first
		textColorForDateTimeMap.clear();

		if (textColorForDateMap == null || textColorForDateMap.size() == 0) {
			return;
		}

		for (Date date : textColorForDateMap.keySet()) {
			Integer resource = textColorForDateMap.get(date);
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
			textColorForDateTimeMap.put(dateTime, resource);
		}
	}

	public void setTextColorForDateTimes(HashMap<DateTime, Integer> textColorForDateTimeMap) {
		this.textColorForDateTimeMap.putAll(textColorForDateTimeMap);
	}

	public void setTextColorForDate(int textColorRes, Date date) {
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
		textColorForDateTimeMap.put(dateTime, Integer.valueOf(textColorRes));
	}

	public void setTextColorForDateTime(int textColorRes, DateTime dateTime) {
		textColorForDateTimeMap.put(dateTime, Integer.valueOf(textColorRes));
	}

	// -------------------------工具方法-----------------------------------
	/**
	 * 前一个日期
	 */
	public void prev() {
		vpContent.setCurrentItem(pageChangeListener.getCurrentPage() - 1);
	}

	/**
	 * 下一个日期
	 */
	public void next() {
		vpContent.setCurrentItem(pageChangeListener.getCurrentPage() + 1);
	}

	// ---------失效状态
	/**
	 * 清空disable状态，注意该方法并不会自动刷新view，需要手动调用refreshView()
	 */
	public void clearDisableDates() {
		disableDates.clear();
	}

	/**
	 * 设置当前不可用的日期
	 * 
	 * @param disableDateList
	 */
	public void setDisableDates(ArrayList<Date> disableDateList) {
		disableDates.clear();
		if (disableDateList == null || disableDateList.size() == 0) {
			return;
		}

		for (Date date : disableDateList) {
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
			disableDates.add(dateTime);
		}
	}

	/**
	 * 以字符串格式设置不可用日期，默认日期格式 yyyy-MM-dd. 如：2013-12-24
	 * 
	 * @param disableDateStrings
	 */
	public void setDisableDatesFromString(ArrayList<String> disableDateStrings) {
		setDisableDatesFromString(disableDateStrings, null);
	}

	/**
	 * 用一串字符串数组设置失效时间，注意时间格式：例如 06-Jan-2013, 使用格式 dd-MMM-yyyy。
	 * 该方法会自动刷新显示，不需要手动调用refreshView
	 * 
	 * @param disableDateStrings
	 * @param dateFormat
	 */
	public void setDisableDatesFromString(ArrayList<String> disableDateStrings, String dateFormat) {
		disableDates.clear();
		if (disableDateStrings == null) {
			return;
		}

		for (String dateString : disableDateStrings) {
			DateTime dateTime = CalendarHelper.getDateTimeFromString(dateString, dateFormat);
			disableDates.add(dateTime);
		}
	}

	// -------------选中状态
	/**
	 * 选中从fromDate到toDate指定到时间，默认背景色为holo_blue_light，并且文字颜色为back，可以通过CaldroidFragment.selectedBackgroundDrawable和
	 * CaldroidFragment.selectedTextColor来设置背景色和文字颜色，该方法并不刷新视图，需要手工调用refreshView
	 * @param fromDate
	 * @param toDate
	 */
	public void setSelectedDates(Date fromDate, Date toDate) {
		// Ensure fromDate is before toDate
		if (fromDate == null || toDate == null || fromDate.after(toDate)) {
			return;
		}

		selectedDates.clear();

		DateTime fromDateTime = CalendarHelper.convertDateToDateTime(fromDate);
		DateTime toDateTime = CalendarHelper.convertDateToDateTime(toDate);

		DateTime dateTime = fromDateTime;
		while (dateTime.lt(toDateTime)) {
			selectedDates.add(dateTime);
			dateTime = dateTime.plusDays(1);
		}
		selectedDates.add(toDateTime);
	}

	/**
	 * 清空选中的时间，需要调用refreshView来刷新视图
	 */
	public void clearSelectedDates() {
		selectedDates.clear();
	}

	/**
	 * 得到当前显示的
	 */
	public int getCurrentVirtualPosition() {
		int currentPage = vpContent.getCurrentItem();
		return pageChangeListener.getCurrent(currentPage);
	}

	/**
	 * 选中时间，格式yyyy-MM-dd
	 * 
	 * @param fromDateString
	 * @param toDateString
	 * @param dateFormat
	 * @throws ParseException
	 */
	public void setSelectedDateStrings(String fromDateString, String toDateString, String dateFormat) throws ParseException {
		Date fromDate = CalendarHelper.getDateFromString(fromDateString, dateFormat);
		Date toDate = CalendarHelper.getDateFromString(toDateString, dateFormat);
		setSelectedDates(fromDate, toDate);
	}

	// -------------------------getters and setters-----------------------
	/**
	 * get content title
	 * @return
	 */
	public GridView getGvContentTitle() {
		return gvContentTitle;
	}
	
	
	public void setCaldroidListener(OnCalendarChangeListener caldroidListener) {
		this.caldroidListener = caldroidListener;
	}

	public OnCalendarChangeListener getCaldroidListener() {
		return caldroidListener;
	}

	/**
	 * 是否显示导航箭头
	 */
	public boolean isShowNavigationArrows() {
		return showNavigationArrows;
	}

	/**
	 * 设置是否显示导航箭头
	 * 
	 * @param showNavigationArrows
	 */
	public void setShowNavigationArrows(boolean showNavigationArrows) {
		this.showNavigationArrows = showNavigationArrows;
		if (showNavigationArrows) {
			btnArrowLeft.setVisibility(View.VISIBLE);
			btnArrowRight.setVisibility(View.VISIBLE);
		} else {
			btnArrowLeft.setVisibility(View.INVISIBLE);
			btnArrowRight.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 设置最小时间，不自动刷新view
	 * 
	 * @param minDate
	 */
	public void setMinDate(Date minDate) {
		if (minDate == null) {
			minDateTime = null;
		} else {
			minDateTime = CalendarHelper.convertDateToDateTime(minDate);
		}
	}

	/**
	 * 用字符串设置最小时间，格式 yyyy-MM-dd
	 * 
	 * @param minDateString
	 * @param dateFormat
	 */
	public void setMinDateFromString(String minDateString, String dateFormat) {
		if (minDateString == null) {
			setMinDate(null);
		} else {
			minDateTime = CalendarHelper.getDateTimeFromString(minDateString, dateFormat);
		}
	}

	/**
	 * 设置最大时间，该方法不自动刷新view
	 * 
	 * @param maxDate
	 */
	public void setMaxDate(Date maxDate) {
		if (maxDate == null) {
			maxDateTime = null;
		} else {
			maxDateTime = CalendarHelper.convertDateToDateTime(maxDate);
		}
	}

	/**
	 * 设置最大时间，格式 yyyy-MM-dd
	 * 
	 * @param maxDateString
	 * @param dateFormat
	 */
	public void setMaxDateFromString(String maxDateString, String dateFormat) {
		if (maxDateString == null) {
			setMaxDate(null);
		} else {
			maxDateTime = CalendarHelper.getDateTimeFromString(maxDateString, dateFormat);
		}
	}

	/**
	 * 是否允许滑动
	 * 
	 * @return
	 */
	public boolean isEnableSwipe() {
		return enableSwipe;
	}

	/**
	 * 设置是否允许滑动
	 * 
	 * @param enableSwipe
	 */
	public void setEnableSwipe(boolean enableSwipe) {
		this.enableSwipe = enableSwipe;
		vpContent.setEnabled(enableSwipe);
	}

	/**
	 * 客户端的额外数据
	 * 
	 * @return
	 */
	public HashMap<String, Object> getExtraData() {
		return extraData;
	}

	/**
	 * 客户端在此处可以设置额外数据
	 * 
	 * @param extraData
	 */
	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public InfiniteViewPager getVpContent() {
		return vpContent;
	}

}
