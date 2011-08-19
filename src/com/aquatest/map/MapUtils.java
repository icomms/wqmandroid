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
package com.aquatest.map;

import com.google.android.maps.GeoPoint;

/**
 * Utility class providing helper methods for working with maps.
 */
public class MapUtils
{

	/**
	 * Calculates the centre point between 2 points as defined by the
	 * parameters. The coordinates of the points must be provided in degrees. If
	 * the coordinates are already calculated in microdegrees, they need to be
	 * converted to use this method. Otherwise use method
	 * {@link #calculateCentrePoint(GeoPoint, GeoPoint)}.
	 * 
	 * @param westDegrees
	 *            The western most latitude value.
	 * @param eastDegrees
	 *            The eastern most latitude value.
	 * @param northDegrees
	 *            The northern most longitude value.
	 * @param southDegrees
	 *            The southern most longitude value.
	 * @return A new {@link GeoPoint} that is in the centre of the rectangle
	 *         defined by the parameters.
	 * 
	 * @see #calculateCentrePoint(GeoPoint, GeoPoint)
	 */
	public static GeoPoint calculateCentrePointFromDegrees(double westDegrees,
			double eastDegrees, double northDegrees, double southDegrees)
	{
		// calculate centre point in microdegrees
		int midLatitudeE6 = (int) ((northDegrees + southDegrees) / 2 * 1e6);
		int midLongitudeE6 = (int) ((westDegrees + eastDegrees) / 2 * 1e6);

		return new GeoPoint(midLatitudeE6, midLongitudeE6);
	}

	/**
	 * Calculates the centre point between 2 points.
	 * 
	 * @param point1
	 *            The first {@link GeoPoint}
	 * @param point2
	 *            The second {@link GeoPoint}
	 * @return A new {@link GeoPoint} that is in the centre of the rectangle
	 *         defined by the parameters.
	 * 
	 * @see #calculateCentrePointFromDegrees(int, int, int, int)
	 */
	public static GeoPoint calculateCentrePoint(GeoPoint point1, GeoPoint point2)
	{
		// calculate centre point in microdegrees
		int midLatitudeE6 = (point1.getLatitudeE6() + point2.getLatitudeE6()) / 2;
		int midLongitudeE6 = (point1.getLongitudeE6() + point2.getLatitudeE6()) / 2;

		return new GeoPoint(midLatitudeE6, midLongitudeE6);
	}

}
