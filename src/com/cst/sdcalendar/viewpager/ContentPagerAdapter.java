package com.cst.sdcalendar.viewpager;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cst.sdcalendar.fragment.BaseCalendarFragment;
import com.cst.sdcalendar.fragment.DateGridFragment;

/**
 * PagerAdapter holds 4 fragments, which provides fragment for current
 * , previous and next. The extra fragment helps for recycle
 * fragments.
 * 
 */
public class ContentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<DateGridFragment> fragments;
	
	public ContentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	// Lazily create the fragments
	public ArrayList<DateGridFragment> getFragments() {
		if (fragments == null) {
			fragments = new ArrayList<DateGridFragment>();
			for (int i = 0; i < getCount(); i++) {
				fragments.add(new DateGridFragment());
			}
		}
		return fragments;
	}

	public void setFragments(ArrayList<DateGridFragment> fragments) {
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		DateGridFragment fragment = getFragments().get(position);
		return fragment;
	}

	@Override
	public int getCount() {
		// We need 4 gridviews for previous month, current month and next month,
		// and 1 extra fragment for fragment recycle
		return BaseCalendarFragment.NUMBER_OF_PAGES;
	}

}
