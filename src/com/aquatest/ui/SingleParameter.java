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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.aquatest.dbinterface.objects.SamplingPoint;
import com.aquatest.dbinterface.tools.DataCache;
import com.aquatest.dbinterface.tools.DataUtils;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;

/**
 * Shows single paramter data
 */
public class SingleParameter extends Activity {
	
	// TODO this should be a global constant
	public static final int OVERVIEW_PERIOD_DAYS = 30;
	
	AquaTestApp application;
	
	//HashMap<Integer, SamplingPoint> samplingPoints;
	Vector<SamplingPoint> samplingPoints;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (AquaTestApp) this.getApplication();
        this.setTitle(application.getCurrentParameter().testName);
        setContentView(R.layout.single_parameter);
        //View v = findViewById(R.id.SingleParameterScreenLayout);
        
        //get time frame
        Calendar endDate = Calendar.getInstance();
		Calendar startDate = DataUtils.calendarSubtractDays(endDate,
			OVERVIEW_PERIOD_DAYS);

		// get data
		DataCache dataCache = new DataCache(application.dbAdapter,
			application.getCurrentTown().id, DatabaseAdaptor.TOWN,
			application.getCurrentParameter().id, startDate, endDate);
		samplingPoints = dataCache.getOrderedSamplingPoints();

        String [] colFrom = {SAMPLING_POINT_NAME,SAMPLING_POINT_COLOR};
		int [] colTo = {R.id.SamplingPointItemText,R.id.SamplingPointImage};
		SimpleAdapter sa = new SimpleAdapter(this, getSamplePoints(), R.layout.sampling_point_list_item, colFrom, colTo);
		
        ListView lv = (ListView) findViewById(R.id.SamplingPointList);
        lv.setAdapter(sa);
    }
    
	private static final String SAMPLING_POINT_NAME = "SAMPLING_POINT_NAME";
	private static final String SAMPLING_POINT_COLOR = "SAMPLING_POINT_COLOR";

	/**
	 * returns list of sample points
	 * @return list of sample points
	 */
	private List<Map<String, Object>> getSamplePoints()
	{
		List<Map<String, Object>> oRet = new ArrayList<Map<String, Object>>(samplingPoints.size());
		Map<String, Object> m;
		
		for(int i = 0; i < samplingPoints.size(); i++) {
			SamplingPoint sp = samplingPoints.elementAt(i);
			m = new HashMap<String, Object>();
			m.put(SAMPLING_POINT_NAME, sp.pointName);
			int color = DataUtils.getColor(sp.getColour());
			switch(color) {
			case TestIndicators.GREEN_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_green));
				break;
			case TestIndicators.YELLOW_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_yellow));
				break;
			case TestIndicators.RED_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_red));
				break;
			case TestIndicators.GREY_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_grey));
				break;
			default:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_grey));
				break;
			}
			oRet.add(m);
		}
		/*Iterator<Integer> i = samplingPoints.keySet().iterator();
    	
    	while (i.hasNext()) {
    		Integer key = (Integer) i.next();
    		SamplingPoint sp = samplingPoints.get(key);
    		m = new HashMap<String, Object>();
			m.put(SAMPLING_POINT_NAME, sp.pointName);
			int color = DataUtils.getColor(sp.getColour());
			switch(color) {
			case TestIndicators.GREEN_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_green));
				break;
			case TestIndicators.YELLOW_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_yellow));
				break;
			case TestIndicators.RED_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_red));
				break;
			case TestIndicators.GREY_COLOR:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_grey));
				break;
			default:
				m.put(SAMPLING_POINT_COLOR, new Integer(R.drawable.indicator_grey));
				break;
			}
			oRet.add(m);
    	}*/
		
		return oRet;
	}

    
    
}
