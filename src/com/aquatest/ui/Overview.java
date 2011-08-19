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

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;

/**
 * View sample data represented by traffic light
 */
public class Overview extends Activity {
	
	// TODO this should be a global constant
	public static final int OVERVIEW_PERIOD_DAYS = 30;
	
	AquaTestApp application;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (AquaTestApp) this.getApplication();
        this.setTitle(application.getCurrentTown().name+" Overview");
        setContentView(R.layout.overview);
        //View v = findViewById(R.id.OverViewScreenLayout);
        
        //get time frame
        Calendar endDate = Calendar.getInstance();
		Calendar startDate = DataUtils.calendarSubtractDays(endDate,
			OVERVIEW_PERIOD_DAYS);
        
        // Log.v("endDate",startDate.get(Calendar.DAY_OF_MONTH)+" "+startDate.get(Calendar.MONTH)+" "+startDate.get(Calendar.YEAR));
        DataIndicator dI = application.dbAdapter.getAreaOverallQuality(application.getCurrentTown().id, DatabaseAdaptor.TOWN , startDate, endDate);
        String color = dI.colour;
        String percentage = dI.name;
        
       
        ImageView imgView = (ImageView)findViewById(R.id.TrafficLightImage);
        int complianceColor = DataUtils.getColor(color);
        switch(complianceColor) {
        case TestIndicators.GREEN_COLOR:
        	  imgView.setImageResource(R.drawable.traffic_light_green);
        	break;
        case TestIndicators.YELLOW_COLOR:
        	  imgView.setImageResource(R.drawable.traffic_light_amber);
        	break;
        case TestIndicators.RED_COLOR:
        	  imgView.setImageResource(R.drawable.traffic_light_red);
        	break;
        default:
        	  imgView.setImageResource(R.drawable.traffic_light_grey);
        	break;
        }
        TextView tv = (TextView) findViewById(R.id.ComplianceText);
        if ((percentage != null) && (percentage.length() > 0)) {
        	tv.setText(percentage);
        } else {
        	tv.setText("<No Data Available>");
        }
      
        //v.setBackgroundColor(complianceColor);
    }
    
    
}
