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
 * Models a range as specified in the database. </p>
 * Each type of number has some ranges, with some colours. </p>
 */
public class Range {

	public int id;
	public String description;
	public int valueRuleId;
	public double minimum;
	public double maximum;
	public String colour;
	public int wqmAuthorityId;
	
	/**
	 * Constructor
	 * 
	 * @param _id
	 * @param _description
	 * @param _valueRuleId
	 * @param _minimum
	 * @param _maximum
	 * @param _colour
	 * @param _wqmAuthorityId
	 */
	public Range (int _id, String _description, int _valueRuleId, double _minimum, double _maximum, String _colour, int _wqmAuthorityId) {
		id = _id;
		description = _description;
		valueRuleId = _valueRuleId;
		minimum = _minimum;
		maximum = _maximum;
		colour = _colour;
		wqmAuthorityId = _wqmAuthorityId;
	}
}
