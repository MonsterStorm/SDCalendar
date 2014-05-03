package test;

import hirondelle.date4j.DateTime;

import com.cst.sdcalendar.adapter.BaseCalendarGridAdapter;
import com.cst.sdcalendar.fragment.MonthFragment;

public class CalendarSampleCustomFragment extends MonthFragment {

	@Override
	public BaseCalendarGridAdapter getNewDatesGridAdapter(DateTime datetime) {
		return new CalendarSampleCustomAdapter(getActivity(), month, year,
				getCaldroidData(), extraData);	}
}
