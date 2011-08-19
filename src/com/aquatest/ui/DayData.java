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
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;

/**
 * DayData
 * Displays data for specific day
 */
public class DayData extends Activity {
	
	AquaTestApp application;
	
	Vector <SamplingPoint> samplingPoints;
	boolean isMunicipality;
	DataCache dataCache;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (AquaTestApp) this.getApplication();
        isMunicipality = (application.getCurrentTown() == null);
        setContentView(R.layout.daydata);
        //View v = findViewById(R.id.DayDataScreenLayout);
       
        if (isMunicipality) {
        	this.setTitle(application.getCurrentMunicipality().name+"("+DataUtils.calendarToString(application.getDate())+")");
        	dataCache = new DataCache(application.dbAdapter,application.getCurrentMunicipality().id, DatabaseAdaptor.MUNICIPALITY,application.getDate(), application.getDate());
        } else {
        	this.setTitle(application.getCurrentTown().name+"("+DataUtils.calendarToString(application.getDate())+")");
        	dataCache = new DataCache(application.dbAdapter,application.getCurrentTown().id, DatabaseAdaptor.TOWN,application.getDate(), application.getDate());
        }
        
        samplingPoints = dataCache.getOrderedSamplingPoints();

        String [] colFrom = {SAMPLING_POINT_NAME,SAMPLING_POINT_COLOR};
		int [] colTo = {R.id.DayDataItemText,R.id.DayDataImage};
		SimpleAdapter sa = new SimpleAdapter(this, getSamplePoints(), R.layout.daydata_list_item, colFrom, colTo);
		
        ListView lv = (ListView) findViewById(R.id.DayDataList);
        lv.setAdapter(sa);
    }
    
	private static final String SAMPLING_POINT_NAME= "SAMPLING_POINT_NAME";
	private static final String SAMPLING_POINT_COLOR = "SAMPLING_POINT_COLOR";

	private List<Map<String, Object>> getSamplePoints()
	{
		List<Map<String, Object>> oRet = new ArrayList<Map<String, Object>>(samplingPoints.size());
		Map<String, Object> m;
		// Log.v("daydata size = ",""+samplingPoints.size());
		for(int i = 0; i < samplingPoints.size(); i++) {
			SamplingPoint sp = samplingPoints.elementAt(i);
			m = new HashMap<String, Object>();
			m.put(SAMPLING_POINT_NAME, sp.pointName);
			// Log.v("sp.name",sp.pointName);
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
		
		return oRet;
	}

    
    
}
