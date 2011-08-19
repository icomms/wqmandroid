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

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.debug.DebugConstants;

/**
 * CalendarView Calendar Control for the main calendar view screen. Allows you
 * to pick a day to examine.
 */
public class CalendarView extends LinearLayout implements OnClickListener,
		OnItemClickListener {

	public static final String MONTH_OF_YEAR[] = { "January", "February",
			"March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };

	private Calendar calendar;
	private CalendarAdapter cAdapter;
	private int currentMonth;
	private int currentYear;

	private TextView monthText;
	private TextView yearText;
	private GridView gridview;
	private ImageButton previousButton;
	private ImageButton nextButton;

	private Activity mActivity;
	private Context mContext;

	private GregorianCalendar gregCal = new GregorianCalendar();
	private CalendarViewListener calListener;

	/**
	 * Creates a new CalendarView object to pick a day.
	 * 
	 * @param a
	 * @param context
	 */
	public CalendarView(Activity a, Context context) {
		super(context);
		mActivity = a;
		mContext = context;
		init();
	}

	/**
	 * Creates a new CalendarView object to pick a day.
	 * 
	 * @param context
	 * @param attrs
	 */
	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	/**
	 * Initialisation method. </p> Sets up the UI.
	 */
	private void init() {
		View v = LayoutInflater.from(mContext).inflate(R.layout.calendar, null,
				true);
		this.addView(v, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		calendar = Calendar.getInstance();
		gridview = (GridView) findViewById(R.id.CalendarGridView);
		monthText = (TextView) findViewById(R.id.CalendarMonth);
		yearText = (TextView) findViewById(R.id.CalendarYear);

		previousButton = (ImageButton) findViewById(R.id.CalendarPreviousMonthButton);
		nextButton = (ImageButton) findViewById(R.id.CalendarNextMonthCalendar);
		previousButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		gridview.setOnItemClickListener(this);
		cAdapter = new CalendarAdapter(mActivity, mContext, calendar);

		if (DebugConstants.CALENDAR_DEBUG)
		{
			setMonthYear(9, 2010);
		}
		else
		{
			setMonthYear(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
		}

	}

	/**
	 * Setter of CalendarViewListener
	 * 
	 * @param calListener
	 *            CalendarViewListener
	 */
	public void setCalendarViewListener(CalendarViewListener calListener) {
		this.calListener = calListener;
	}

	/**
	 * get viewed month
	 * 
	 * @return viewed month
	 */
	public int getMonth() {
		return currentMonth;
	}

	/**
	 * get viewed year
	 * 
	 * @return viewed year
	 */
	public int getYear() {
		return currentYear;
	}

	/**
	 * returns days in month
	 * 
	 * @param month
	 * @return days in month
	 */
	public int daysInMonth(int month) {
		int days = CalendarAdapter.NUM_DAYS_IN_MONTH[month];
		if (month == Calendar.FEBRUARY) {
			if (gregCal.isLeapYear(currentYear)) {
				days = 29;
			}
		}

		return days;
	}

	/**
	 * Set month and year
	 * 
	 * @param month
	 * @param year
	 */
	public void setMonthYear(int month, int year) {
		this.currentMonth = month;
		this.currentYear = year;
		this.monthText.setText(MONTH_OF_YEAR[currentMonth]);
		this.yearText.setText("" + currentYear);
		// cAdapter.setMonthYear(currentMonth,currentYear);
		// gridview.setAdapter(cAdapter);
	}

	/**
	 * set water sample data for month
	 * 
	 * @param waterSamples
	 *            water sample data
	 */
	public void setWaterSamples(HashMap<String, DataIndicator> waterSamples) {
		// Log.v("leo","setting water samples");
		cAdapter.setWaterSamples(waterSamples);
		cAdapter.setMonthYear(currentMonth, currentYear);
		gridview.setAdapter(cAdapter);
	}

	/**
	 * handler when user selects button to change month
	 */
	public void onClick(View v) {
		if (v == previousButton) {
			if (currentMonth == 0) {
				currentMonth = 11;
				currentYear--;
			} else {
				currentMonth--;
			}
		} else {
			if (currentMonth == 11) {
				currentMonth = 1;
				currentYear++;
			} else {
				currentMonth++;
			}
		}

		monthText.setText(MONTH_OF_YEAR[currentMonth]);
		yearText.setText("" + currentYear);
		calListener.calendarChanged();
		// cAdapter.setMonthYear(currentMonth,currentYear);
		// gridview.setAdapter(cAdapter);
	}

	/**
	 * handler when day pressed
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Log.v("CalendarView","item clicked!!");

		CalendarDayView cdv = (CalendarDayView) arg1;
		if ((cdv.getDate() != null) && (calListener != null)
				&& (cdv.data.getText().length() > 1)) {
			calListener.dateSelected(cdv.getDate());
		}
	}
}
