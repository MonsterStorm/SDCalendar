package com.cst.sdcalendar.fragment;

import hirondelle.date4j.DateTime;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.cst.sdcalendar.adapter.ContentPagerAdapter;
import com.cst.sdcalendar.calendar.CalendarListener;
import com.cst.sdcalendar.util.CalendarHelper;
import com.cst.sdcalendar.viewpager.InfinitePagerAdapter;
import com.cst.sdcalendar.viewpager.InfiniteViewPager;

/**
 * Calendar����
 */

@SuppressLint("DefaultLocale")
public abstract class BaseCalendarFragment extends DialogFragment {
	public String TAG = "BaseCalendarFragment";

	/**
	 * ��
	 */
	public static int SUNDAY = 1;
	public static int MONDAY = 2;
	public static int TUESDAY = 3;
	public static int WEDNESDAY = 4;
	public static int THURSDAY = 5;
	public static int FRIDAY = 6;
	public static int SATURDAY = 7;

	/**
	 * ����ѡ�����ڵı�����������ɫ
	 */
	public static int selectedBackgroundDrawable = -1;
	public static int selectedTextColor = Color.BLACK;

	/**
	 * ���Ʋ�����ʱ��ı�����������ɫ
	 */
	public static int disabledBackgroundDrawable = -1;
	public static int disabledTextColor = Color.GRAY;

	/**
	 * ҳ��
	 */
	public final static int NUMBER_OF_PAGES = 4;

	/**
	 * View���
	 */
	protected Button btnArrowLeft;// ��߰�ť
	protected Button btnArrowRight;// �ұ߰�ť
	protected TextView tvTitle;// ����
	protected GridView gvContentTitle;// ���ݱ���
	protected InfiniteViewPager vpContent;// ���岿��
	protected DatePageChangeListener pageChangeListener;
	protected ArrayList<DateGridFragment> fragments;

	/**
	 * ��ʼ������
	 */
	public final static String DIALOG_TITLE = "dialogTitle";// ����
	public final static String DAY = "day";
	public final static String WEEK = "week";
	public final static String MONTH = "month";
	public final static String YEAR = "year";
	public final static String DISABLE_DATES = "disableDates";// ʧЧ����
	public final static String SELECTED_DATES = "selectedDates";// ѡ������
	public final static String MIN_DATE = "minDate";// ��С����
	public final static String MAX_DATE = "maxDate";// �������
	// Ч������
	public final static String SHOW_NAVIGATION_ARROWS = "showNavigationArrows";// �Ƿ���ʾ��ͷ
	public final static String ENABLE_SWIPE = "enableSwipe";// �Ƿ�������
	public final static String ENABLE_CLICK_ON_DISABLED_DATES = "enableClickOnDisabledDates";

	/**
	 * ��ʼ������
	 */
	protected String dialogTitle;// �Ի������
	protected int day = -1;
	protected int week = -1;
	protected int month = -1;
	protected int year = -1;
	protected ArrayList<DateTime> disableDates = new ArrayList<DateTime>();// ʧЧʱ��
	protected ArrayList<DateTime> selectedDates = new ArrayList<DateTime>();// ѡ��ʱ��
	protected DateTime minDateTime;// ��Сʱ��
	protected DateTime maxDateTime;// ���ʱ��
	protected ArrayList<DateTime> datetimeList;// ʱ��

	/**
	 * �ڲ�ʹ��
	 */
	public final static String _MIN_DATE_TIME = "_minDateTime";
	public final static String _MAX_DATE_TIME = "_maxDateTime";
	public final static String _BACKGROUND_FOR_DATETIME_MAP = "_backgroundForDateTimeMap";
	public final static String _TEXT_COLOR_FOR_DATETIME_MAP = "_textColorForDateTimeMap";

	// �ڲ�����
	protected HashMap<String, Object> caldroidData = new HashMap<String, Object>();
	// �û�����
	protected HashMap<String, Object> extraData = new HashMap<String, Object>();
	// ĳһʱ�䱳��
	protected HashMap<DateTime, Integer> backgroundForDateTimeMap = new HashMap<DateTime, Integer>();
	// ĳһʱ��������ɫ
	protected HashMap<DateTime, Integer> textColorForDateTimeMap = new HashMap<DateTime, Integer>();;
	// ����4ҳ����������
	protected ArrayList<BaseCalendarGridAdapter> datePagerAdapters = new ArrayList<BaseCalendarGridAdapter>();

	// ������Ч
	protected boolean enableSwipe = true;// �Ƿ��������л�
	protected boolean showNavigationArrows = true;// �Ƿ���ʾ������ͷ
	protected boolean enableClickOnDisabledDates = false;// �Ƿ�������Чʱ��ĵ��Ч��

	// -------------------�¼��������----------------------
	// ���ڵĵ���¼�
	private OnItemClickListener dateItemClickListener;

	// ���ڵĳ����¼�
	private OnItemLongClickListener dateItemLongClickListener;

	// �Զ����¼�����
	private CalendarListener caldroidListener;

	/**
	 * �����û�����title
	 */
	public abstract View getContentTitle();

	/**
	 * �������ඨ����ʾ������һ��adapter
	 */
	public abstract BaseCalendarGridAdapter getNewDatesGridAdapter(DateTime dateTime);

	// ----------------------------------�ؼ�״̬����------------------------------------
	/**
	 * �õ���ǰ�����״̬�����ڴ�����ת
	 */
	public Bundle getSavedStates() {
		Bundle bundle = new Bundle();
		bundle.putInt(DAY, day);
		bundle.putInt(WEEK, week);
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
			bundle.putString(MIN_DATE, minDateTime.format("YYYY-MM-DD"));
		}

		if (maxDateTime != null) {
			bundle.putString(MAX_DATE, maxDateTime.format("YYYY-MM-DD"));
		}

		bundle.putBoolean(SHOW_NAVIGATION_ARROWS, showNavigationArrows);
		bundle.putBoolean(ENABLE_SWIPE, enableSwipe);

		getChildSavedStates(bundle);

		return bundle;
	}

	/**
	 * ����ʵ�֣����������Ҫ�õ���״̬
	 * 
	 * @param bundle
	 */
	public void getChildSavedStates(Bundle bundle) {
	}

	/**
	 * ����ǰ�ؼ�״̬���浽states
	 * 
	 * @param outState
	 * @param key
	 */
	public void saveStatesToKey(Bundle outState, String key) {
		outState.putBundle(key, getSavedStates());
	}

	/**
	 * �ӵ�ǰ״̬�лָ�
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
	 * ���ڶԻ����״̬�ָ�
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
	 * �õ���ǰ��ʾ��
	 */
	public int getCurrentVirtualPosition() {
		int currentPage = vpContent.getCurrentItem();
		return pageChangeListener.getCurrent(currentPage);
	}

	/**
	 * To clear selectedDates. This method does not refresh view, need to
	 * explicitly call refreshView()
	 */
	public void clearSelectedDates() {
		selectedDates.clear();
	}

	/**
	 * Select the dates from fromDate to toDate. By default the background color
	 * is holo_blue_light, and the text color is black. You can customize the
	 * background by changing CaldroidFragment.selectedBackgroundDrawable, and
	 * change the text color CaldroidFragment.selectedTextColor before call this
	 * method. This method does not refresh view, need to call refreshView()
	 * 
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
	 * Convenient method to select dates from String
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

	/**
	 * Set caldroid listener when user click on a date
	 * 
	 * @param caldroidListener
	 */
	public void setCaldroidListener(CalendarListener caldroidListener) {
		this.caldroidListener = caldroidListener;
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
	 * ��ʼ��view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		retrieveInitialArgs();

		// ֧�ֱ���״̬��dialog��
		if (getDialog() != null) {
			setRetainInstance(true);
		}

		// ��ʼ������
		View view = inflater.inflate(R.layout.calendar_view, container, false);

		// ����
		tvTitle = (TextView) view.findViewById(R.id.calendar_month_year_textview);

		// ������ť
		btnArrowLeft = (Button) view.findViewById(R.id.calendar_left_arrow);
		btnArrowRight = (Button) view.findViewById(R.id.calendar_right_arrow);

		// ��������ť
		btnArrowLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prev();
			}
		});

		// �������Ұ�ť
		btnArrowRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				next();
			}
		});

		// �Ƿ���ʾ��ͷ
		setShowNavigationArrows(showNavigationArrows);

		// ����
		gvContentTitle = (GridView) view.findViewById(R.id.gvContentTitle);
		ColumnTitleAdapter weekdaysAdapter = new ColumnTitleAdapter(getActivity(), android.R.layout.simple_list_item_1, getContentTitles());
		gvContentTitle.setAdapter(weekdaysAdapter);

		// ����gridview�е�����View����ЩView���Ա�����
		setupDateGridPages(view);

		// ˢ����ʾ
		refreshView();

		// ֪ͨ�ͻ���view�����ɹ����ͻ�����Ҫ�����ﶨ�ư�ť��TextView
		if (caldroidListener != null) {
			caldroidListener.onCaldroidViewCreated();
		}

		return view;
	}
	
	public List<String> getContentTitles(){return null;};

	/**
	 * �õ���ʼ������ͨ��setArguments��ʽ�õ�������
	 */
	protected void retrieveInitialArgs() {
		Bundle args = getArguments();
		if (args != null) {
			day = args.getInt(DAY, -1);
			week = args.getInt(WEEK, -1);
			month = args.getInt(MONTH, -1);
			year = args.getInt(YEAR, -1);
			dialogTitle = args.getString(DIALOG_TITLE);
			Dialog dialog = getDialog();
			if (dialog != null) {
				if (dialogTitle != null) {
					dialog.setTitle(dialogTitle);
				} else {// ����û�Ҫ����ʾtitle
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				}
			}

			// �Ƿ���ʾ��ͷ
			showNavigationArrows = args.getBoolean(SHOW_NAVIGATION_ARROWS, true);

			// �Ƿ��������л�
			enableSwipe = args.getBoolean(ENABLE_SWIPE, true);

			// �Ƿ���������Ч����
			enableClickOnDisabledDates = args.getBoolean(ENABLE_CLICK_ON_DISABLED_DATES, false);

			// �õ���Ч����
			ArrayList<String> disableDateStrings = args.getStringArrayList(DISABLE_DATES);
			if (disableDateStrings != null && disableDateStrings.size() > 0) {
				disableDates.clear();
				for (String dateString : disableDateStrings) {
					DateTime dt = CalendarHelper.getDateTimeFromString(dateString, "yyyy-MM-dd");
					disableDates.add(dt);
				}
			}

			// �õ�ѡ������
			ArrayList<String> selectedDateStrings = args.getStringArrayList(SELECTED_DATES);
			if (selectedDateStrings != null && selectedDateStrings.size() > 0) {
				selectedDates.clear();
				for (String dateString : selectedDateStrings) {
					DateTime dt = CalendarHelper.getDateTimeFromString(dateString, "yyyy-MM-dd");
					selectedDates.add(dt);
				}
			}

			// �õ���С����
			String minDateTimeString = args.getString(MIN_DATE);
			if (minDateTimeString != null) {
				minDateTime = CalendarHelper.getDateTimeFromString(minDateTimeString, null);
			}

			// �õ��������
			String maxDateTimeString = args.getString(MAX_DATE);
			if (maxDateTimeString != null) {
				maxDateTime = CalendarHelper.getDateTimeFromString(maxDateTimeString, null);
			}

			// �������������
			retrieveChildInitialArgs(args);
		}
		if (month == -1 || year == -1) {
			DateTime dateTime = DateTime.today(TimeZone.getDefault());
			month = dateTime.getMonth();
			year = dateTime.getYear();
		}
	}

	/**
	 * ���า�ǵķ�������ȡ����Ĳ���
	 * 
	 * @param bundle
	 */
	public void retrieveChildInitialArgs(Bundle bundle) {
	}

	/**
	 * �õ���ǰ����
	 * 
	 * @return
	 */
	public abstract DateTime buildCurrentDateTime();

	/**
	 * �õ�һ�������ڣ����ݵ�ǰ�����unit��unit>0�������ڣ������С����
	 * 
	 * @param dateTime
	 * @return
	 */
	public abstract DateTime buildDateTime(DateTime dateTime, int unit);

	/**
	 * ����4ҳ�������ڵ�gridview����Щҳ����Ա�����
	 * 
	 * @param view
	 */
	private void setupDateGridPages(View view) {
		// �õ���ǰ����
		DateTime currentDateTime = buildCurrentDateTime();

		// ����pageChangeListener
		pageChangeListener = new DatePageChangeListener();
		pageChangeListener.setCurrentDateTime(currentDateTime);

		// ��ǰ����
		BaseCalendarGridAdapter adapter0 = getNewDatesGridAdapter(currentDateTime);

		// ����ʱ��
		datetimeList = adapter0.getDatetimeList();

		// ��һ������
		DateTime nextDateTime = buildDateTime(currentDateTime, 1);
		BaseCalendarGridAdapter adapter1 = getNewDatesGridAdapter(nextDateTime);

		// ����������
		DateTime next2DateTime = buildDateTime(nextDateTime, 1);
		BaseCalendarGridAdapter adapter2 = getNewDatesGridAdapter(next2DateTime);

		// ǰһ������
		DateTime prevDateTime = buildDateTime(currentDateTime, -1);
		BaseCalendarGridAdapter adapter3 = getNewDatesGridAdapter(prevDateTime);

		// �������adapter
		datePagerAdapters.add(adapter0);
		datePagerAdapters.add(adapter1);
		datePagerAdapters.add(adapter2);
		datePagerAdapters.add(adapter3);

		// ����adapter��Listener����ҳ��ı��ʱ��ˢ��adapter
		pageChangeListener.setCaldroidGridAdapters(datePagerAdapters);

		// �������޻�����viewpager��pageradapter����������fragemnts
		vpContent = (InfiniteViewPager) view.findViewById(R.id.vpContent);

		// �Ƿ�������
		vpContent.setEnabled(enableSwipe);

		// Set the numberOfDaysInMonth to dateViewPager so it can calculate the
		// height correctly
		vpContent.setDatesInMonth(datetimeList);

		setupChildDateGridPages();

		// MonthPagerAdapter actually provides 4 real fragments. The
		// InfinitePagerAdapter only recycles fragment provided by this
		// MonthPagerAdapter
		final ContentPagerAdapter pagerAdapter = new ContentPagerAdapter(getChildFragmentManager());

		// ��view����֮ǰ����fragment���ó�ʼ����
		fragments = pagerAdapter.getFragments();
		for (int i = 0; i < NUMBER_OF_PAGES; i++) {
			DateGridFragment dateGridFragment = fragments.get(i);
			BaseCalendarGridAdapter adapter = datePagerAdapters.get(i);
			dateGridFragment.setGridAdapter(adapter);
			dateGridFragment.setOnItemClickListener(getDateItemClickListener());
			dateGridFragment.setOnItemLongClickListener(getDateItemLongClickListener());
		}

		// ����InfinitePagerAdapterΧ��BaseGridPagerAdapter����
		InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(pagerAdapter);

		// ʹ��infinitePagerAdapter��ΪdateViewPager�ṩ����
		vpContent.setAdapter(infinitePagerAdapter);

		// ���� pageChangeListener
		vpContent.setOnPageChangeListener(pageChangeListener);
	}

	/**
	 * ���������page����
	 */
	public void setupChildDateGridPages() {
	}

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	// ------------------------��װ����----------------------------
	/**
	 * �������仯��ʱ��ˢ����ͼ�����õ�ʱ����Ҫ�߸ı�����������
	 */
	public void refreshView() {
		if (month == -1 || year == -1) {
			return;
		}

		refreshTitle();

		// ˢ��DateGrid
		for (BaseCalendarGridAdapter adapter : datePagerAdapters) {
			// �����ڲ�����
			adapter.setCaldroidData(getCaldroidData());

			// ���ö�������
			adapter.setExtraData(extraData);

			// ˢ��View
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * ���û�������ʱ��ı�Title
	 */
	protected void refreshTitle() {
	}

	/**
	 * ��������������
	 * 
	 * @param date
	 */
	public void setCalendarDate(Date date) {
		setCalendarDateTime(CalendarHelper.convertDateToDateTime(date));
	}

	/**
	 * ����������ʱ��
	 * 
	 * @param dateTime
	 */
	public void setCalendarDateTime(DateTime dateTime) {
		month = dateTime.getMonth();
		year = dateTime.getYear();

		// Notify listener
		if (caldroidListener != null) {
			caldroidListener.onChangeMonth(month, year);
		}

		refreshView();
	}

	/**
	 * ������ָ��������
	 * 
	 * @param date
	 */
	public void moveToDate(Date date) {
		moveToDateTime(CalendarHelper.convertDateToDateTime(date));
	}

	/**
	 * �õ��õ�λ�µĵ�һ��ʱ��
	 * 
	 * @return
	 */
	protected abstract DateTime getFirstDateTime(DateTime datetime);

	/**
	 * �õ��õ�λ�µ����һ��ʱ��
	 * 
	 * @return
	 */
	protected abstract DateTime getLastDateTime(DateTime datetime);

	/**
	 * �ƶ���ָ�����ڣ�����������ʽ
	 * 
	 * @param dateTime
	 */
	public void moveToDateTime(DateTime datetime) {
		DateTime currentDateTime = buildCurrentDateTime();
		DateTime fistDateTime = getFirstDateTime(currentDateTime);
		DateTime lastDateTime = getLastDateTime(currentDateTime);

		DateTime newFirstDateTime = getFirstDateTime(datetime);
		DateTime newLastDateTime = getLastDateTime(datetime);

		// ��Ŀ��ʱ��С�ڵ�һ��ʱ�䣬���󻬶�
		if (datetime.lt(fistDateTime)) {

			// ˢ��adapters
			pageChangeListener.setCurrentDateTime(newFirstDateTime);
			int currentItem = vpContent.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// ���󻬶�
			vpContent.setCurrentItem(currentItem - 1);
		} else if (datetime.gt(lastDateTime)) {// Ŀ��ʱ��������һ��ʱ�䣬���һ�����ʱ������
			// ˢ��adapters
			pageChangeListener.setCurrentDateTime(newLastDateTime);
			int currentItem = vpContent.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// ���һ���
			vpContent.setCurrentItem(currentItem + 1);
		}

	}

	// -----------------------------listeners---------------------------------
	/**
	 * DatePageChangeListener ���û�������ʱ��ˢ������
	 */
	public class DatePageChangeListener implements OnPageChangeListener {
		private int currentPage = InfiniteViewPager.OFFSET;
		private DateTime currentDateTime;
		private ArrayList<BaseCalendarGridAdapter> caldroidGridAdapters;

		/**
		 * �õ�dateViewPager�ĵ�ǰpager
		 * 
		 * @return
		 */
		public int getCurrentPage() {
			return currentPage;
		}

		/**
		 * ���õ�ǰ��page
		 * 
		 * @param currentPage
		 */
		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}

		/**
		 * �õ���ǰѡ�е�ʱ��
		 * 
		 * @return
		 */
		public DateTime getCurrentDateTime() {
			return currentDateTime;
		}

		/**
		 * ���õ�ǰʱ��
		 * 
		 * @param dateTime
		 */
		public void setCurrentDateTime(DateTime dateTime) {
			this.currentDateTime = dateTime;
			setCalendarDateTime(currentDateTime);
		}

		/**
		 * ����4��Adapter
		 */
		public ArrayList<BaseCalendarGridAdapter> getCaldroidGridAdapters() {
			return caldroidGridAdapters;
		}

		/**
		 * ����adapter
		 * 
		 * @param caldroidGridAdapters
		 */
		public void setCaldroidGridAdapters(ArrayList<BaseCalendarGridAdapter> caldroidGridAdapters) {
			this.caldroidGridAdapters = caldroidGridAdapters;
		}

		/**
		 * �õ���ʾ����һ��λ��
		 * 
		 * @param position
		 * @return
		 */
		private int getNext(int position) {
			return (position + 1) % BaseCalendarFragment.NUMBER_OF_PAGES;
		}

		/**
		 * �õ���ʾ�ϵ�ǰһ��λ��
		 * 
		 * @param position
		 * @return
		 */
		private int getPrevious(int position) {
			return (position + 3) % BaseCalendarFragment.NUMBER_OF_PAGES;
		}

		/**
		 * �õ���ʾ�ϵĵ�ǰλ��
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
			// �õ�����Ҫˢ�µ�adapter
			BaseCalendarGridAdapter currentAdapter = caldroidGridAdapters.get(getCurrent(position));
			BaseCalendarGridAdapter prevAdapter = caldroidGridAdapters.get(getPrevious(position));
			BaseCalendarGridAdapter nextAdapter = caldroidGridAdapters.get(getNext(position));

			if (position == currentPage) {
				// ˢ�µ�ǰAdapter

				currentAdapter.setAdapterDateTime(currentDateTime);
				currentAdapter.notifyDataSetChanged();

				// ˢ��ǰһ��Adapter
				prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				prevAdapter.notifyDataSetChanged();

				// ˢ����һ��Adapter
				nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				nextAdapter.notifyDataSetChanged();
			} else if (position > currentPage) {// ���һ���
				// Update current date time to next month
				currentDateTime = currentDateTime.plus(0, 1, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);

				// Refresh the adapter of next gridview
				nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				nextAdapter.notifyDataSetChanged();

			} else {// ���󻬶�
				// Update current date time to previous month
				currentDateTime = currentDateTime.minus(0, 1, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);

				// Refresh the adapter of previous gridview
				prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				prevAdapter.notifyDataSetChanged();
			}

			// Update current page
			currentPage = position;
		}

		/**
		 * ˢ��fragments
		 */
		@Override
		public void onPageSelected(int position) {
			refreshAdapters(position);

			// Update current date time of the selected page
			setCalendarDateTime(currentDateTime);

			// ˢ�����е�ǰ�����ʱ��
			BaseCalendarGridAdapter currentAdapter = caldroidGridAdapters.get(position % BaseCalendarFragment.NUMBER_OF_PAGES);

			// ˢ��List�е�ʱ��
			datetimeList.clear();
			datetimeList.addAll(currentAdapter.getDatetimeList());
		}

	}

	/**
	 * ���Ч�����ڷ���Ч״̬����������С/���ʱ��������
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
	 * �������Ч�����ڷ���Ч״̬����������С/���ʱ��������
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
	 * �õ����е� fragments
	 */
	public ArrayList<DateGridFragment> getFragments() {
		return fragments;
	}

	/**
	 * �õ���߰�ť
	 */
	public Button getLeftArrowButton() {
		return btnArrowLeft;
	}

	/**
	 * �õ��ұ߰�ť
	 * 
	 * @return
	 */
	public Button getRightArrowButton() {
		return btnArrowRight;
	}

	/**
	 * �õ�title����
	 */
	public TextView getTitleTextView() {
		return tvTitle;
	}

	/**
	 * ����title������
	 * 
	 * @param titleView
	 */
	public void setTitleTextView(TextView titleView) {
		this.tvTitle = titleView;
	}

	/**
	 * �ڲ��õ�������
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
	 * �õ����������
	 */
	public void getChildCaldroidData() {
	}

	/**
	 * �õ�grid view��adapter�����������Զ������ݣ��Լ�ˢ��gridview������
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

	// -------------------------���߷���-----------------------------------
	/**
	 * ǰһ������
	 */
	public void prev() {
		vpContent.setCurrentItem(pageChangeListener.getCurrentPage() - 1);
	}

	/**
	 * ��һ������
	 */
	public void next() {
		vpContent.setCurrentItem(pageChangeListener.getCurrentPage() + 1);
	}

	// ---------ʧЧ״̬
	/**
	 * ���disable״̬��ע��÷����������Զ�ˢ��view����Ҫ�ֶ�����refreshView()
	 */
	public void clearDisableDates() {
		disableDates.clear();
	}

	/**
	 * ���õ�ǰ�����õ�����
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
	 * ���ַ�����ʽ���ò��������ڣ�Ĭ�����ڸ�ʽ yyyy-MM-dd. �磺2013-12-24
	 * 
	 * @param disableDateStrings
	 */
	public void setDisableDatesFromString(ArrayList<String> disableDateStrings) {
		setDisableDatesFromString(disableDateStrings, null);
	}

	/**
	 * ��һ���ַ�����������ʧЧʱ�䣬ע��ʱ���ʽ������ 06-Jan-2013, ʹ�ø�ʽ dd-MMM-yyyy��
	 * �÷������Զ�ˢ����ʾ������Ҫ�ֶ�����refreshView
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

	// -------------ѡ��״̬

	// -------------------------getters and setters-----------------------
	public CalendarListener getCaldroidListener() {
		return caldroidListener;
	}

	/**
	 * �Ƿ���ʾ������ͷ
	 */
	public boolean isShowNavigationArrows() {
		return showNavigationArrows;
	}

	/**
	 * �����Ƿ���ʾ������ͷ
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
	 * ������Сʱ�䣬���Զ�ˢ��view
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
	 * ���ַ���������Сʱ�䣬��ʽ yyyy-MM-dd
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
	 * �������ʱ�䣬�÷������Զ�ˢ��view
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
	 * �������ʱ�䣬��ʽ yyyy-MM-dd
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
	 * �Ƿ�������
	 * 
	 * @return
	 */
	public boolean isEnableSwipe() {
		return enableSwipe;
	}

	/**
	 * �����Ƿ�������
	 * 
	 * @param enableSwipe
	 */
	public void setEnableSwipe(boolean enableSwipe) {
		this.enableSwipe = enableSwipe;
		vpContent.setEnabled(enableSwipe);
	}

	/**
	 * �ͻ��˵Ķ�������
	 * 
	 * @return
	 */
	public HashMap<String, Object> getExtraData() {
		return extraData;
	}

	/**
	 * �ͻ����ڴ˴��������ö�������
	 * 
	 * @param extraData
	 */
	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}
}
