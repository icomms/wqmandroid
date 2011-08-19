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
package com.aquatest.dbinterface.tools;

/**
 * A DataIndicator object is used to model a single test result so that it can be easily 
 * understood by UI objects and interpreted to graphically represent results.
 */
public class DataIndicator {

	public double value;
	public String colour;
	public String name;
	
	/**
	 * Constructor
	 * 
	 * @param _value the value to be displayed for this result
	 * @param _colour the colour to be displayed for this result
	 */
	public DataIndicator(double _value, String _colour) {
		value = _value;
		colour = _colour;
	}
	
	/**
	 * Constructor
	 * 
	 * @param _value the value to be displayed for this result
	 * @param _colour the colour to be displayed for this result
	 * @param _name a name to give the indicator for reference purposes, not displayed
	 */
	public DataIndicator(double _value, String _colour, String _name) {
		
		value = _value;
		colour = _colour;
		name = _name;
	}
	
	/**
	 * @return string representation of a DataIndicator, as: <br>
	 * 	<i>[name] = value (colour)</i>
	 */
	public String toString()
	{
		return "[" + name + "] = " + value + " (" + colour + ")";
	}
}
