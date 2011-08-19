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
import java.util.Vector;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.aquatest.dbinterface.tools.DataCache;
import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;
import com.aquatest.dbinterface.tools.DataUtils.TableViews;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;


/**
 * The core view used in displaying data. Displays data formated as a vector of 
 * DataIndicator objects in a table style format. </p>
 * DataList might typically be created when a user clicks on a date on a calendar, 
 * in order to show data for that date.
 */
public class DataList extends Activity {
	
	// TODO this should be a global constant
	public static final int OVERVIEW_PERIOD_DAYS = 30; 
	
	AquaTestApp application;
	boolean isMunicipality;
	DataCache dataCache;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Uri uri = this.getIntent().getData();
        
        application = (AquaTestApp) this.getApplication();

        setContentView(R.layout.data_list);
             
        Calendar endDate = Calendar.getInstance();
		Calendar startDate = DataUtils.calendarSubtractDays(endDate,
			OVERVIEW_PERIOD_DAYS);
        
        isMunicipality = (application.getCurrentTown() == null);
        
        String title = "";
        int areaType  = Integer.valueOf(uri.getPathSegments().get(1));
        int areaId = Integer.valueOf(uri.getPathSegments().get(2));
        int viewType = Integer.valueOf(uri.getPathSegments().get(3));
        int dataId = Integer.valueOf(uri.getPathSegments().get(4));
        
        if (areaType == DatabaseAdaptor.MUNICIPALITY) {
        	title = application.dbAdapter.municipalities.get(areaId).name;
        }
        else
        	title = application.dbAdapter.getTown(areaId).name;
        
        Vector <DataIndicator> headers = new Vector <DataIndicator>();
        Vector <Vector <DataIndicator>> rows = new  Vector <Vector <DataIndicator>>();
        
        DataIndicator complianceDI = null;
        
        if (viewType == TableViews.DAILY_MEASURED_VALUE) {
        	// CRASH!
        	dataCache = new DataCache(application.dbAdapter, areaId, areaType, application.getDate(), application.getDate());
        	
        	headers = dataCache.getMeasuredValueColumnHeaders(0);
        	rows = dataCache.getMeasuredValueDataIndicators(0);
        	title += ": " + DataUtils.calendarToString(application.getDate());
        	
        	//headers.remove(0);
            complianceDI = dataCache.getCompliance();
        }
        else if (viewType == TableViews.RECENT_MEASURED_VALUE) {
        	dataCache = new DataCache(application.dbAdapter, areaId, areaType, startDate, endDate);
        	headers = dataCache.getMeasuredValueColumnHeaders(-1);
        	rows = dataCache.getMeasuredValueDataIndicators(-1);
        	title += ": " + DataUtils.calendarToStringShort(startDate) + " - " + DataUtils.calendarToStringShort(endDate);
        	
            complianceDI = dataCache.getCompliance();
        }
        else if (viewType == TableViews.POINT_RECENT_MEASURED_VALUE) {
        	dataCache = new DataCache(application.dbAdapter, areaId, areaType, startDate, endDate);
        	headers = dataCache.getMeasuredValueColumnHeaders(-1);
        	rows = dataCache.samplingPoints.get(dataId).getMeasureValueDataIndicators(-1);
        	title += " > " + dataCache.samplingPoints.get(dataId).pointName + ": " 
        			+ DataUtils.calendarToStringShort(startDate) + " - " + DataUtils.calendarToStringShort(endDate);
        	
            complianceDI = dataCache.getSamplingPointCompliance(dataId);
        }
        else if (viewType == TableViews.RECENT_PARAMETERS) {
        	dataCache = new DataCache(application.dbAdapter, areaId, areaType, startDate, endDate);
        	headers = dataCache.getParametersColumnHeaders();
        	rows = dataCache.getParametersDataIndicators(true);
        	title += " > Points: " + DataUtils.calendarToStringShort(startDate) + " - " + DataUtils.calendarToStringShort(endDate);
        	
            complianceDI = dataCache.getCompliance();
        }
        else if (viewType == TableViews.POINT_RECENT_PARAMETERS) {
        	dataCache = new DataCache(application.dbAdapter, areaId, areaType, startDate, endDate);
        	headers = dataCache.getParametersColumnHeaders();
        	rows = dataCache.samplingPoints.get(dataId).getParametersDataIndicators(dataCache.getOrderedParameters1(),false);
        	title += " > " + dataCache.samplingPoints.get(dataId).pointName + ": " 
				+ DataUtils.calendarToStringShort(startDate) + " - " + DataUtils.calendarToStringShort(endDate);
        	
            complianceDI = dataCache.getSamplingPointCompliance(dataId);
        }
        else if (viewType == TableViews.RECENT_PARAMETERS_GROUPED_BY_DATE) {
        	dataCache = new DataCache(application.dbAdapter, areaId, areaType, startDate, endDate);
        	headers = dataCache.getParametersColumnHeaders();
        	rows = dataCache.getParametersDataIndicatorsGroupedByDate();
        	title += " > Points: " + DataUtils.calendarToStringShort(startDate) + " - " + DataUtils.calendarToStringShort(endDate);
        	
            complianceDI = dataCache.getCompliance();
        } 
        else if (viewType == TableViews.SINGLE_PARAMETER_MEASURED_VALUE) {
        	dataCache = new DataCache(application.dbAdapter, areaId, areaType, dataId, startDate, endDate);
        	headers = dataCache.getMeasuredValueColumnHeaders(2);
        	rows = dataCache.getMeasuredValueDataIndicators(2);
        	title += " > " + application.dbAdapter.parameters.get(dataId).testNameShort + ": "
        		+ DataUtils.calendarToStringShort(startDate) + " - " + DataUtils.calendarToStringShort(endDate);
        	
            complianceDI = dataCache.getCompliance();
        }
        

        this.setTitle(title);
        

        TextView tV = (TextView) findViewById(R.id.ComplianceDataText);
        tV.setTextColor(TestIndicators.WHITE_COLOR);
        tV.setText(" Compliance: " + complianceDI.name);
        tV.setBackgroundColor(DataUtils.getColor(complianceDI.colour));
        
        GridView gV = (GridView) findViewById(R.id.DataHeaderView);
        gV.setNumColumns(headers.size());
        
        ListView lV = (ListView) findViewById(R.id.DataItemList);
        lV.setAdapter(new DataListAdapter(this, rows));
        gV.setAdapter(new DataListItemAdapter(this, headers, true));
    }
}
