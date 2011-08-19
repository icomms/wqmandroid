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

import java.util.Vector;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;

/**
 * Models a measured value as specified in the database.
 */
public class MeasuredValue {
	public int id;
	public Sample sample;
	public Parameter parameter;
	public double value;
	public String valueKey;
	
	public Range range;

	/** 
	 * Constructor
	 * 
	 * @param _id Sample id
	 * @param _sample Details of where, when and who took the sample
	 * @param _parameter Details of test being done
	 * @param _value Result of the test
	 */
    public MeasuredValue (int _id, Sample _sample, Parameter _parameter, double _value) 
    {
    	// check for null values
    	if (_sample == null) 
    	{
    		throw new IllegalArgumentException("Null value passed to MeasuredValue constructor - [_sample]");
    	}
    	if (_parameter == null)
    	{
    		throw new IllegalArgumentException("Null value passed to MeasuredValue constructor - [_parameter]");
    	}
    	
    	// populate fields
    	id = _id;
    	sample = _sample;
    	parameter = _parameter;
    	value = _value;
  
    	valueKey = parameter.getKeyOfValue(_value);
    	range = parameter.getRangeOfValue(_value);
    }
    
    /**
     * Gets a vector of the object's data that can be understood by and displayed in a DataList
     * 
     * @param hiddenColumn index of a particular data item that shouldn't be added to the vector to be displayed
     * @return vector of DataIndicator items
     */
    public Vector <DataIndicator> getDataIndicators(int hiddenColumn) {
    	String colour;
    	
    	if (range != null) {
    		colour = range.colour;
    	} else {
    		colour = TestIndicators.NONE;
    	}
    	
    	DataIndicator nameDI = new DataIndicator(-1, TestIndicators.NONE, DataUtils.calendarToString(sample.date));
    	DataIndicator samplingPointDI = new DataIndicator (-1, colour, sample.samplingPoint.pointName);
    	DataIndicator townDI = new DataIndicator (-1, colour, sample.samplingPoint.townName);
    	DataIndicator parameterDI = new DataIndicator (-1, colour, parameter.testNameShort);
    	DataIndicator valueDI = new DataIndicator (value, colour, valueKey);
    	
    	Vector <DataIndicator> dIs = new Vector <DataIndicator>();
    	
    	dIs.add(nameDI);
    	dIs.add(samplingPointDI);
    	dIs.add(townDI);
    	dIs.add(parameterDI);
    	dIs.add(valueDI);
    	
    	if (hiddenColumn > -1) {
    		dIs.remove(hiddenColumn);
    	}
    	
    	return dIs;
    }
}
