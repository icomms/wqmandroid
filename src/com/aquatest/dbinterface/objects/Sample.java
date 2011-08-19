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
package com.aquatest.dbinterface.objects;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;

/**
 * Models a sample as specified in the database
 */
public class Sample {
	public int id;
	public SamplingPoint samplingPoint;
	public int samplerId;
	public String notes;
    public Calendar date;
    
    
    public HashMap <Integer, MeasuredValue> measuredValues;
	
	/**
	 * Constructor
	 * 
	 * @param _id
	 * @param _samplingPoint
	 * @param _samplerId
	 * @param _notes
	 * @param _date
	 */
	public Sample (int _id, SamplingPoint _samplingPoint, int _samplerId, String _notes, Calendar _date) {
		id = _id;
		samplingPoint = _samplingPoint;
		samplerId = _samplerId;
		notes = _notes;
        date = _date;
        
        measuredValues = new HashMap <Integer, MeasuredValue>();//Vector <MeasuredValue>();
	}
	
	/**
	 * Get the colour of this sample according to whether any of its measured values are failures, 
	 * warnings, or all passed
	 * 
	 * @return the colour as a String
	 */
	public String getColour() {
		
		String colour = TestIndicators.PASS;
		
        Iterator<Integer> i = measuredValues.keySet().iterator();
    	
        while (i.hasNext()) {
        	
        	MeasuredValue mV = measuredValues.get(i.next());
        	
        	if (mV.range == null) {
        		continue;
        	}
    		
    		if (mV.range.colour.compareTo(TestIndicators.FAIL) == 0)
    			return TestIndicators.FAIL;
    		else if (mV.range.colour.compareTo(TestIndicators.WARNING) == 0)
    			colour = TestIndicators.WARNING;
    	}
		return colour;
	}
	
	
	/**
	 * Get the compliance of this sample as a percentage
	 * 
	 * @return the compliance
	 */
	public double getCompliance() {
		
		double totalCount = 0;
		double passCount = 0;
		
        Iterator<Integer> i = measuredValues.keySet().iterator();
    	
        while (i.hasNext()) {
        	
        	MeasuredValue mV = measuredValues.get(i.next());
        	String colour;
        	
        	if (mV.range != null) {
        		colour = mV.range.colour;
        	} else {
        		colour = TestIndicators.PASS;//TODO mike
        	}
    		
    		if ((colour.compareTo(TestIndicators.PASS) == 0) || (colour.compareTo(TestIndicators.WARNING) == 0)) {
    			totalCount++;
    			passCount++;
    		}	
    		else if (colour.compareTo(TestIndicators.FAIL) == 0)
    			totalCount++;
    	}
		
		return passCount / totalCount;
	}
	
	
	
	
	/**
	 * 
	 * @param hiddenColumn
	 * @return
	 */
	public Vector <Vector<DataIndicator>> getMeasuredValueDataIndicators(int hiddenColumn) {
		
		Vector <Vector<DataIndicator>> dIs = new Vector <Vector<DataIndicator>> ();
		
        Iterator<Integer> i = measuredValues.keySet().iterator();
    	
        while (i.hasNext()) {
        	
        	MeasuredValue mV = measuredValues.get(i.next());
        	
        	dIs.add(mV.getDataIndicators(hiddenColumn));
        	
    	}
        
		return dIs;
	}
	
	
	/**
	 * Gets a vector of the object's data that can be understood by and displayed in a DataList.
	 * Samples returned are filtered according to a specified list of parameters.
	 * 
	 * @param _parameters parameter list to filter the returned samples by
	 * @param showPointName whether the sampling point's name should be returned as a value to be displayed
	 * @return vector of DataIndicator items
	 */
	public Vector <DataIndicator> getParametersDataIndicators(Vector <Parameter> _parameters, boolean showPointName) {
		
		Vector<DataIndicator> dIs = new Vector<DataIndicator> ();
		
		Iterator<Parameter> i = _parameters.iterator();
		
		String rowLabel = "";
		
		if (showPointName) {
			if (samplingPoint.pointName.length() > 20)
				rowLabel = samplingPoint.pointName.substring(0,20) + "...";
			else rowLabel = samplingPoint.pointName.substring(0,20);
			
			rowLabel = rowLabel + " " + DataUtils.calendarToStringShort(date);
		}
		else rowLabel = DataUtils.calendarToStringShort(date);
        
        dIs.add(new DataIndicator(-1, TestIndicators.NONE, rowLabel));
	
    	
        while (i.hasNext()) {
        	
        	Parameter p = i.next();
        	
        	if (measuredValues.containsKey(p.id)) {
        		
        		MeasuredValue mV = measuredValues.get(p.id);
        	
        		dIs.add(new DataIndicator(mV.value, mV.range.colour, p.getKeyOfValue(mV.value)));
        	}
        	else {
        		dIs.add(new DataIndicator(-1, TestIndicators.NONE, ""));
        	}
    	}
        
		return dIs;
	}
	
	
	/**
	 * Get the measured values in this sample filtered by the specified list of parameters.
	 * 
	 * @param _parameters list of parameters to filter the measured values by
	 * @return vector of the filtered measured values
	 */
	public Vector <MeasuredValue> getMeasuredValuesByParameters(Vector <Parameter> _parameters) {
		
		Vector<MeasuredValue> mVs = new Vector<MeasuredValue> ();
		
		Iterator<Parameter> i = _parameters.iterator();
               	
        while (i.hasNext()) {
        	
        	Parameter p = i.next();
        	
        	if (measuredValues.containsKey(p.id)) {
        	
        		mVs.add(measuredValues.get(p.id));
        	}
        	else {
        		mVs.add(null);
        	}
    	}
        
		return mVs;
	}
	
}
