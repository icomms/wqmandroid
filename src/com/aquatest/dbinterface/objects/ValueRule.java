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

/**
 * Determines if something passes a test or not.
 * Models a value rule as specified in the database
 */
public class ValueRule {

	public int id;
	public String description;
	public int parameterId;
	public HashMap <Integer, Range> ranges;
	
	/**
	 * Constructor
	 * 
	 * @param _id
	 * @param _description
	 * @param _parameterId
	 * @param _ranges
	 */
	public ValueRule (int _id, String _description, int _parameterId, HashMap <Integer, Range> _ranges) {
		
		id = _id;
		description = _description;
		parameterId = _parameterId;
		ranges = _ranges;
	}
}
