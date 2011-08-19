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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils.TableViews;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;

/**
 * Aqua Test Calendar
 * 
 * Populates Calendar Control with data
 */
public class AquaCalendar extends Activity implements CalendarViewListener {
	
	private AquaTestApp application;
	private CalendarView calendarView;
	private HashMap<String, DataIndicator> waterSamples;
	private boolean isMunicipality;
	
	private int areaType;
	private int areaId;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        application = (AquaTestApp) this.getApplication();
        isMunicipality = (application.getCurrentTown() == null);
        
        if (isMunicipality) {
        	this.setTitle(application.getCurrentMunicipality().name);
    		areaType = DatabaseAdaptor.MUNICIPALITY;
    		areaId = application.getCurrentMunicipality().id;
        } else {
        	this.setTitle(application.getCurrentTown().name);
    		areaType = DatabaseAdaptor.TOWN;
    		areaId = application.getCurrentTown().id;
        }
        
        setContentView(R.layout.calendar_layout);
        calendarView = (CalendarView) findViewById(R.id.CalendarView);
        waterSamples = getCalendarSamples(calendarView.getMonth(), calendarView.getYear());
        calendarView.setWaterSamples(waterSamples); //sets water sample data for viewed month in calendar view
        calendarView.setCalendarViewListener(this);
    }
    
    /**
     * Gets calendar samples for a specific time period
     * @param month: current month being viewed 
     * @param year: current year being viewed
     * @return
     */
    private HashMap<String, DataIndicator> getCalendarSamples(int month, int year) {
    	Calendar startDate = Calendar.getInstance();
    	startDate.set(year, month, 1);
    	// Log.v("getCalendarSamples","year = "+year+" month = "+month);
    	Calendar endDate = Calendar.getInstance();
    	endDate.set(year, month, calendarView.daysInMonth(month));
    	// Log.v("getCalendarSamples","year = "+year+" month = "+month+" end day"+calendarView.daysInMonth(month));
    	//if (isMunicipality) {

    		//return application.dbAdapter.getPeriodOverallQuality(areaType, areaId, startDate, endDate);
    	//} else {

    		return application.dbAdapter.getPeriodOverallQuality(areaId, areaType,  startDate, endDate);
    	//}
    }

    /**
     * CalendarViewListener method, called when date selected
     */
	public void dateSelected(GregorianCalendar date) {
		// set the current date being used
		application.setdayDate(date);
		
		Uri uri = TableViews.buildUri(areaType, areaId, TableViews.DAILY_MEASURED_VALUE, -1);

		// open a new activity
		Intent intent = new Intent(this, DataList.class);
		intent.setData(uri);
		
		startActivity(intent);
	}

	/**
	 * CalendarViewListener method, called when month changed
	 */
	public void calendarChanged() {
		waterSamples = getCalendarSamples(calendarView.getMonth(), calendarView.getYear());
        calendarView.setWaterSamples(waterSamples);
	}


}
