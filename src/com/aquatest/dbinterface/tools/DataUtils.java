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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Class of utility methods and global constants used in manipulating and
 * formating data.
 */
public class DataUtils {

	/**
	 * Class of constants used in comparing and displaying test results.
	 */
	public static class TestIndicators {

		public static final String GREEN = "green";
		public static final String YELLOW = "yellow";
		public static final String RED = "red";
		public static final String GREY = "grey";

		public static final int GREEN_COLOR = 0xdd00dd00;
		public static final int YELLOW_COLOR = 0xdddddd00;
		public static final int RED_COLOR = 0xffff0000;
		public static final int GREY_COLOR = 0x00000000;// 0xff888888;
		public static final int WHITE_COLOR = 0xffFFFFFF;

		public static final String PASS = GREEN;
		public static final String WARNING = YELLOW;
		public static final String FAIL = RED;
		public static final String NONE = GREY;

	}

	/**
	 * Class of constants used in determining what format a DataList object
	 * should display data in.
	 */
	public static class TableViews {
		public static final Uri CONTENT_URI = Uri
				.parse("content://com.aquatest.ui/tableviews");

		public static final int DAILY_MEASURED_VALUE = 0;
		public static final int RECENT_MEASURED_VALUE = 1;
		public static final int POINT_RECENT_MEASURED_VALUE = 2;
		public static final int POINT_RECENT_PARAMETERS = 3;
		public static final int RECENT_PARAMETERS = 4;
		public static final int RECENT_PARAMETERS_GROUPED_BY_DATE = 5;
		public static final int SINGLE_PARAMETER_MEASURED_VALUE = 6;

		public static Uri buildUri(int areaType, int areaId, int viewType,
				int dataId) {
			Uri uri = ContentUris.withAppendedId(ContentUris.withAppendedId(
					TableViews.CONTENT_URI, areaType), areaId);
			uri = ContentUris.withAppendedId(
					ContentUris.withAppendedId(uri, viewType), dataId);

			return uri;
		}
	}

	/**
	 * Get actual colour to display based on String colour code.
	 * 
	 * @param color
	 *            colour code to return actual colour for
	 * @return actual colour to display, or white if no colour provided
	 */
	public static int getColor(String color) {
		if (color != null) {
			
			if (color.equals(TestIndicators.GREEN)) {
				return TestIndicators.GREEN_COLOR;
			} else if (color.equals(TestIndicators.YELLOW)) {
				return TestIndicators.YELLOW_COLOR;
			} else if (color.equals(TestIndicators.RED)) {
				return TestIndicators.RED_COLOR;
			} else if (color.equals(TestIndicators.GREY)) {
				return TestIndicators.GREY_COLOR;
			}
		}

		// return white if no color provided
		return TestIndicators.WHITE_COLOR;
	}

	/**
	 * Method to subtract days from a date.
	 * 
	 * @param endDate
	 *        the date to subtract the days from
	 * @param days
	 *        number of days to subtract
	 * @return endDate less days
	 */
	public static Calendar calendarSubtractDays(Calendar endDate, int days)
	{
		Calendar startDate = (Calendar) endDate.clone();
		startDate.add(Calendar.DATE, -days);
		
		return startDate;
	}
	
	/**
	 * Convert Calendar object to String formatted as yyyy-MM-dd.
	 * 
	 * @param date
	 *            date to convert
	 * @return date converted to String
	 */
	public static String calendarToString(Calendar date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(date.getTime());
	}

	/**
	 * Convert Calendar object to String formatted as MM/dd.
	 * 
	 * @param date
	 *            date to convert
	 * @return date converted to String
	 */
	public static String calendarToStringShort(Calendar date) {

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

		return sdf.format(date.getTime());
	}

	public static class SamplingPointColumns {
		public static final String ID = "_id";
		public static final String POINTNAME = "pointname";
		public static final String POINTCODE = "pointcode";
		public static final String WATERUSETYPEID = "waterusetype";
		public static final String TOWNID = "wqmarea";
		public static final String TOWNNAME = "pointarea";
		public static final String MUNICIPALITYID = "wqmauthority";
		public static final String LONGITUDE = "x_coord";
		public static final String LATITUDE = "y_coord";
	}

	public static class SampleColumns {
		public static final String ID = "_id";
		public static final String SAMPLINGPOINTID = "samplingpoint";
		public static final String SAMPLERID = "takenby";
		public static final String NOTES = "notes";
		public static final String DATE = "datetaken";
	}

	public static class MeasuredValueColumns {
		public static final String ID = "_id";
		public static final String SAMPLEID = "sample";
		public static final String PARAMETERID = "parameter";
		public static final String VALUE = "value";
	}

	public static class ParameterColumns {
		public static final String ID = "_id";
		public static final String TESTNAME = "testname";
		public static final String UNITS = "units";
		public static final String LOOKUPHINT = "lookuphint";
		public static final String TESTNAMESHORT = "testnameshort";
	}

	public static class ValueRuleColumns {
		public static final String ID = "_id";
		public static final String DESCRIPTION = "description";
		public static final String PARAMETERID = "parameter";
	}

	public static class RangeColumns {
		public static final String ID = "_id";
		public static final String DESCRIPTION = "description";
		public static final String VALUERULEID = "valuerule";
		public static final String MINIMUM = "minimum";
		public static final String MAXIMUM = "maximum";
		public static final String COLOUR = "colour";
		public static final String WQMAUTHORITYID = "wqmauthority";
	}

	public static class UpdateIntervalOptions {
		public static final int OFF = 0;
		public static final int ONE_HOUR = 1;
	}
}
