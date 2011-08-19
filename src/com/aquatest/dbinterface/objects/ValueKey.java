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

/**
 * Sortof useful sometimes... for ranges...
 * Models a value key as specified in the database
 */
public class ValueKey {
	
	public int id;
	public String key;
	public double value;
	public int parameterId;
	
	/**
	 * Constructor
	 * 
	 * @param _id
	 * @param _key
	 * @param _value
	 * @param _parameterId
	 */
	public ValueKey (int _id, String _key, double _value, int _parameterId) {
		id = _id;
		key = _key;
		value = _value;
		parameterId = _parameterId;
	}

}
