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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Custom overlay item for the application, used to represent towns and sampling points
 */
public class AquaOverlayItem extends OverlayItem {
	
	/** id of the item the point represents */
	int itemId;

	/** whether it is a sampling point or town */
	int itemType;

	/**
	 * whether the point has any measured values or not and thus shouldn't
	 * invoke a screen change when clicked
	 */
	boolean hasData;

	/**
	 * Constructor
	 * 
	 * @param point coordinates of the overlay point
	 * @param title title of the point
	 * @param snippet description to be displayed in label
	 * @param _itemId id of the item the point represents
	 * @param _itemType whether it is a sampling point or town
	 * @param _hasData whether the point has any measured values or not and thus shouldn't invoke a screen change when clicked
	 */
	public AquaOverlayItem(GeoPoint point, String title, String snippet, int _itemId, int _itemType, boolean _hasData) {
		super(point, title, snippet);

		itemId = _itemId;
		itemType = _itemType;
		hasData = _hasData;
	}
}
