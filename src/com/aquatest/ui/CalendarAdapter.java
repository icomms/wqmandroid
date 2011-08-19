/**
 * Water Quality Manager for Android
 * Copyright (C) 2011 iCOMMS (University of Cape Town)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aquatest.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils;


/**
 * CalendarAdapter </p> Renders Calendar </p>
 */
public class CalendarAdapter extends BaseAdapter
{
	public static final int NUM_DAYS_IN_MONTH[] = { 31, 28, 31, 30, 31, 30, 31,
			31, 30, 31, 30, 31 };
	public static final int TODAY_TEXT_COLOR = 0xFF0000FF;
	public static final int TODAY_BACKGROUND_COLOR = 0xFFEEEEEE;
	public static final int CALENDAR_BACKGROUND_COLOR = 0xFFFFFFFF;
	public static final int CALENDAR_TEXT_COLOR = 0xFF999999;
	public static final int CALENDAR_COLORED_BLOCK_TEXT_COLOR = 0xFFFFFFFF;

	private Context mContext;
	private Activity mActivity;

	private int month;
	private int year;
	private int days_in_month;
	private int first_day_of_month;

	private Calendar currentDate;
	private GregorianCalendar cal;

	private HashMap<String, DataIndicator> waterSamples;

	Random random;

	public CalendarAdapter(Activity activity, Context c, Calendar date)
	{
		mContext = c;
		mActivity = activity;
		cal = new GregorianCalendar();
		this.currentDate = date;
		random = new Random();

	}

	/**
	 * Sets the current Month and Year to render
	 * 
	 * @param month
	 * @param year
	 */
	public void setMonthYear(int month, int year)
	{
		this.month = month;
		cal.set(Calendar.MONTH, month);

		this.year = year;
		cal.set(Calendar.YEAR, year);

		cal.set(Calendar.DAY_OF_MONTH, 1);
		first_day_of_month = cal.get(Calendar.DAY_OF_WEEK);
		days_in_month = NUM_DAYS_IN_MONTH[month];

		// correct for leap years
		if (month == Calendar.FEBRUARY && cal.isLeapYear(year))
		{
			days_in_month = 29;
		}
	}

	/**
	 * BaseAdapter override method.
	 * 
	 * @return <code>null</code>
	 */
	public Object getItem(int position)
	{
		return null;
	}

	/**
	 * BaseAdapter override method.
	 * 
	 * @return id of the item in this position - currently id = position
	 */
	public long getItemId(int position)
	{
		return position;
	}

	/**
	 * BaseAdapter override method </p> number of blocks to show in calendar, 6
	 * weeks
	 * 
	 * @return 42, representing 6 weeks
	 */
	public int getCount()
	{
		return 42;
	}

	/**
	 * setter for waterSamples
	 * 
	 * @param waterSamples
	 *            HashMap of water sample DataIndicator objects which are used
	 *            to render the different colours in the calendar.
	 */
	public void setWaterSamples(HashMap<String, DataIndicator> waterSamples)
	{
		this.waterSamples = waterSamples;
	}

	/**
	 * gets the water sample DataIndicator for a particular day
	 * 
	 * @param day
	 *            day for which to return the sample
	 * @return water sample DataIndicator object for the given day; a return
	 *         value of <code>null</code> implies that nothing should be shown
	 *         for that day
	 */
	private DataIndicator getSample(int day)
	{
		// create calendar object to represent the day
		Calendar date = Calendar.getInstance();
		date.set(year, month, day);

		// format the date as yyyy-mm-dd
		String keyDate = DataUtils.calendarToString(date);

		// retrieve the sample for the day, if any
		DataIndicator daySample = null;

		if (waterSamples != null)
		{
			daySample = waterSamples.get(keyDate);
		}

		// Log.v("CalendarAdapter", "retrieving water sample for day " + day +
		// ": " + (daySample == null ? "<no sample>" : daySample.toString()));
		return daySample;
	}

	/**
	 * create a new ImageView for each item (day) referenced by the Adapter
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		CalendarDayView calDayView = new CalendarDayView(mActivity, mContext);
		int calDay = ((position + 1) - (first_day_of_month - 1));
		View dayView = calDayView.findViewById(R.id.DayLayout);

		// if day is today, show as different colour
		if ((this.month == currentDate.get(Calendar.MONTH))
				&& (this.year == currentDate.get(Calendar.YEAR))
				&& (calDay == currentDate.get(Calendar.DAY_OF_MONTH)))
		{
			calDayView.day.setTextColor(TODAY_TEXT_COLOR);
			dayView.setBackgroundColor(TODAY_BACKGROUND_COLOR);
		}
		else
		{
			calDayView.day.setTextColor(CALENDAR_TEXT_COLOR);
			dayView.setBackgroundColor(CALENDAR_BACKGROUND_COLOR);
		}

		// set the text for the day
		if ((calDay > 0) && (calDay <= days_in_month))
		{
			calDayView.day.setText("" + calDay); // set the calendar day text
			calDayView.setDate(new GregorianCalendar(year, month, calDay)); // set
																			// the
																			// date
																			// of
																			// the
																			// day

			// set the sample for this day
			DataIndicator sample = getSample(calDay);
			if (sample != null)
			{
				// set percentage value
				calDayView.data.setText(sample.name);
				dayView.setBackgroundColor(DataUtils.getColor(sample.colour));
				
				// change the font colour of the day number to white
				calDayView.day.setTextColor(CALENDAR_COLORED_BLOCK_TEXT_COLOR);
			}
		}
		else
		{
			calDayView.day.setText("");
			calDayView.data.setText(" ");
		}

		return calDayView;
	}


}
