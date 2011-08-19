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

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Calendar Day View. </p>
 * View that represents a day on the calendar
 */
public class CalendarDayView extends LinearLayout{
	private Activity mActivity;
	private Context mContext;
	TextView day;
	TextView data;
	private GregorianCalendar date = null;
	
	public CalendarDayView(Activity a, Context context) {
		super(context);
		mActivity = a;
		mContext = context;
		init();
	}
	
	public CalendarDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		View v = LayoutInflater.from(mContext).inflate(R.layout.calendarday, null, true);
		this.addView(v, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		day = (TextView) v.findViewById(R.id.DayDateText);
		data = (TextView) v.findViewById(R.id.DayDataText);
	}
	
	/**
	 * set the date of the day
	 * @param date
	 */
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	
	/**
	 * getter of date
	 * @return date of day
	 */
	public GregorianCalendar getDate() {
		return date;
	}
}
