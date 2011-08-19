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
 * Models a town as specified in the database
 */
public class Town
{

	public int id;
	public String name;
	public double latitude;
	public double longitude;
	
	/**
	 * Creates a new {@link Town} instance.
	 * 
	 * @param _id
	 * @param _name
	 * @param _longitude
	 * @param _latitude
	 */
	public Town(int _id, String _name, double _longitude, double _latitude)
	{
		id = _id;
		name = _name;
		longitude = _longitude;
		latitude = _latitude;
	}
	
	// this method shows the town name in the debugger
	public String toString()
	{
		return name + " (" + latitude + "," + longitude + ")";
	}

	/**
	 * Compares if a given object is equal to this {@link Town}. Equality is
	 * defined by having equal <code>id</code> fields, and the same
	 * <code>name</code> ignoring case. </p>
	 * 
	 * This method ignores the values in latitude & longitude.
	 * 
	 * @param testObject
	 *        an object to test - this should preferably be a {@link Town}
	 *        instance.
	 * @return <code>true</code> if <code>testObject</code> is a {@link Town}
	 *         with the same <code>id</code> & (case insensitive)
	 *         <code>name</code> fields.
	 * @see #hashCode()
	 */
	public boolean equals(Object testObject)
	{
		// test for self comparison
		if (this == testObject) { return true; }

		// test if it is a Town
		if (!(testObject instanceof Town)) { return false; }

		/* so testObject is not null, and is a Town */

		Town testTown = (Town) testObject;

		// ensure all fields equal
		if (this.id != testTown.id) { return false; }

		if (!this.name.toLowerCase().equals(testTown.name.toLowerCase())) { return false; }

		// finally all tests pass
		return true;
	}

	/**
	 * Calculates a hashcode for this {@link Town} object. Hashcode is based on
	 * the <code>id</code> & <code>name</code> fields, ignoring case. </p>
	 * 
	 * This method ignores the values in latitude & longitude.
	 * 
	 * @return an int hashcode such that 2 {@link Town} objects with the same
	 *         hashcode can be considered equal.
	 * @see #equals(Object)
	 */
	public int hashCode()
	{
		String tmpHashString = this.id + this.name.toLowerCase();
		
		return tmpHashString.hashCode();
	}
}
