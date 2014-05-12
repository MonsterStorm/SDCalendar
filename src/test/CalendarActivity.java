package test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cst.sdcalendar.Mode;
import com.cst.sdcalendar.R;
import com.cst.sdcalendar.calendar.OnCalendarChangeListener;
import com.cst.sdcalendar.fragment.BaseCalendarFragment;
import com.cst.sdcalendar.fragment.DayFragment;
import com.cst.sdcalendar.fragment.MonthFragment;
import com.cst.sdcalendar.fragment.WeekFragment;

@SuppressLint("SimpleDateFormat")
public class CalendarActivity extends FragmentActivity {
	private BaseCalendarFragment caldroidFragment;

	View btnMonth;
	View btnWeek;
	View btnDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_all);
		btnMonth = this.findViewById(R.id.btnMonth);
		btnWeek = this.findViewById(R.id.btnWeek);
		btnDay = this.findViewById(R.id.btnDay);
		btnMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init(null, Mode.MONTH);
			}
		});
		btnWeek.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init(null, Mode.WEEK);
			}
		});
		btnDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init(null, Mode.DAY);
			}
		});

		init(savedInstanceState, Mode.MONTH);
	}

	private void init(Bundle savedInstanceState, Mode mode) {
		final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
		switch (mode) {
		case MONTH:
			caldroidFragment = new MonthFragment();
			break;
		case WEEK:
			caldroidFragment = new WeekFragment();
			break;
		case DAY:
			caldroidFragment = new DayFragment();
			break;
		}

		if (savedInstanceState != null) {
			caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
		} else {
			Bundle args = new Bundle();
			Calendar cal = Calendar.getInstance();
			args.putInt(BaseCalendarFragment.MONTH, cal.get(Calendar.MONTH) + 1);
			args.putInt(BaseCalendarFragment.YEAR, cal.get(Calendar.YEAR));
			args.putInt(BaseCalendarFragment.DAY, cal.get(Calendar.DAY_OF_MONTH));
			args.putBoolean(BaseCalendarFragment.ENABLE_SWIPE, true);
			args.putBoolean(MonthFragment.SIX_WEEKS_IN_CALENDAR, false);
			caldroidFragment.setArguments(args);
		}

		// Attach to the activity
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();

		// Setup Caldroid
		caldroidFragment.setCaldroidListener(new OnCalendarChangeListener() {
			@Override
			public void onSelectDate(Date date, View view) {
//				Toast.makeText(getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onChangeDateTime(int year, int month, int day) {
				String text = "month: " + month + " year: " + year + " day: " + day;
//				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLongClickDate(Date date, View view) {
//				Toast.makeText(getApplicationContext(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCaldroidViewCreated() {
				if (caldroidFragment.getLeftArrowButton() != null) {
//					Toast.makeText(getApplicationContext(), "Caldroid view is created", Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

	/**
	 * Save current states of the Caldroid here
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		if (caldroidFragment != null) {
			caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		}
	}

}
