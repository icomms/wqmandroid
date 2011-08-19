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


import java.util.HashMap;
import java.util.Iterator;

/**
 * Models a parameter as specified in the database
 */
public class Parameter {

	public int id;
	public String testName;
	public String units;
	public String lookupHint;
	public String testNameShort;
	public ValueRule valueRule;
	
	public HashMap <Double, String> valueKeys;
	public HashMap <Integer, Sample> samples;
	
	/**
	 * Constructor
	 * 
	 * @param _id
	 * @param _testName
	 * @param _units
	 * @param _lookupHint
	 * @param _testNameShort
	 * @param _valueRule
	 */
	public Parameter (int _id, String _testName, String _units, String _lookupHint, String _testNameShort, ValueRule _valueRule) {
		
		id = _id;
		testName = _testName;
		units = _units;
		lookupHint = _lookupHint;
		testNameShort = _testNameShort;
		valueRule = _valueRule;
		samples = new HashMap <Integer, Sample> ();
		valueKeys = new HashMap <Double, String> ();
	}
	
	/**
	 * Checks if a key exists for the specified value and current parameter and returns it
	 * as a String, else returns the value as a String
	 * 
	 * @param value the value to find a key for
	 * @return the key as a string
	 */
	public String getKeyOfValue (double value) {
		
		if (valueKeys.containsKey(value))
			return valueKeys.get(value);	
		else return String.valueOf(value);
	}
	
	
	/**
	 * Gets the range that the specified value falls within based on the current parameter
	 * @param value value to find range for
	 * @return Range that the value falls within
	 */
	public Range getRangeOfValue(double value) {
		if (valueRule == null) {
			return null;
		}
		
		Iterator<Integer> i = valueRule.ranges.keySet().iterator();
    	
    	while (i.hasNext()) {
    		
    		int key = (Integer) i.next();
    		
    		Range range = valueRule.ranges.get(key);
    		
    		if ((range.minimum <= value) && (range.maximum >= value)) {
    			return range;    			
    		}		    		
    	}
    	return null;
	}
}
