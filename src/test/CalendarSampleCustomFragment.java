package test;

import com.cst.sdcalendar.adapter.MonthCalendarGridAdapter;
import com.cst.sdcalendar.fragment.CalendarFragment;

public class CalendarSampleCustomFragment extends CalendarFragment {

	@Override
	public MonthCalendarGridAdapter getNewDatesGridAdapter(int month, int year) {
		// TODO Auto-generated method stub
		return new CalendarSampleCustomAdapter(getActivity(), month, year,
				getCaldroidData(), extraData);
	}

}
