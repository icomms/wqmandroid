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
import java.util.Vector;

/**
 *  Models a municipality as specified in the database. Includes all the towns in that municipality.
 */
public class Municipality {

	public int id;
	public HashMap <Integer, Town> towns;
	public String name;
	
	
	/**
	 * Constructor
	 * 
	 * @param _id
	 * @param _name
	 */
	public Municipality (int _id, String _name) {
		
		id = _id;
		name = _name;
		towns = new HashMap <Integer, Town>();
		
	}
	
	
	/**
	 * Gets the town in the municipality that is nearest to the specified location
	 * 
	 * @param longitude: longitude in decimal degrees
	 * @param latitude: latitude in decimal degrees
	 * @return: nearest town to specified location
	 */
	public Town getTownNearestToLocation(double longitude, double latitude) {
		
		Iterator<Integer> i = towns.keySet().iterator();
    	
		double distance = 90000;
		
		int townId = -1;
		
    	while (i.hasNext()) {
    		
    		int key = (Integer) i.next();
    		
    		Town town = towns.get(key);
    		
    		if (town.longitude == 0 || town.latitude == 0) {
    			continue;
    		} //if
    		
    		double currentDistance = Math.pow(town.longitude - longitude, 2) + Math.pow(town.latitude - latitude, 2);
    		
    		if (currentDistance < distance) {
    			distance = currentDistance;
    			townId = key;
    		}
    	}
		
    	if (townId > 0) {
    		return towns.get(townId);
    	} else {
    		return null;
    	} 
	}
	
	
	
	
	/**
	 * Gets a vector of towns in the municipality, ordered by name
	 * 
	 * @return vector of towns ordered by name
	 */
	public Vector <Town> getOrderedTowns() {
		
		
		Vector<Town> tVector = new Vector<Town> ();
		
		Iterator<Integer> i = towns.keySet().iterator();
    	
    	while (i.hasNext()) {
    		
    		int key = (Integer) i.next();
    		
    		Town town = towns.get(key);
    		
    		
    		if (tVector.size() == 0) {
    			tVector.add(town);
    		}
    		else {
    			Iterator<Town> i1 = tVector.iterator();
    			
    			int position = 0;
    			boolean inserted = false;
    			
    			while (i1.hasNext()) {
    				if (town.name.compareTo(i1.next().name) < 0) {
    					
    					
    					inserted = true;
    					break;
    				
    				}
    				position++;
    			}
    			
    			if (!inserted) {
    				tVector.add(town);
    			}
    			else {
    				tVector.insertElementAt(town, position);
    			}
    		}
    	}
		
		return tVector;
	}
	
}
