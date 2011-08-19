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
package com.aquatest.webservice;

import com.aquatest.debug.DebugConstants;

/**
 * Holds some JSON responses that can be used for testing the AquaTest app by
 * using mock web services instead of actually calling to the server each time.
 * </p>
 * 
 * The use of this class is governed by the value of
 * {@link DebugConstants#MOCK_WEB_SERVICES}. If <code>true</code> then this
 * class is used instead of web service calls, if <code>false</code> then the
 * real web services are used.
 */
public class MockJsonResponsesFull {

	// these strings represent a snapshot of the database
	// in some cases total_count has been edited to prevent further calls to web
	// service i.e. count = total_count always
	public static final String TABLE_LIST = "";

	// added records for each table
	public static String ADD_ABNORMAL_RANGE = "";
	public static String ADD_AUTHORISED_SAMPLER = "";
	public static String ADD_AUTHORITY_MANAGER = "";
	public static String ADD_DOMAIN_LOOKUP = "";

	public static String ADD_MEASURED_VALUE;
	static {
		if (DebugConstants.MOCK_WEB_SERVICE_LONG_RESPONSES) {
			ADD_MEASURED_VALUE = "";
		} else {
			ADD_MEASURED_VALUE = "";
		}
	}

	public static String ADD_NORMAL_RANGE = "";
	public static String ADD_PARAMETER = "";	
	public static String ADD_SAMPLE = "";
	public static String ADD_SAMPLING_POINT = "";
	public static String ADD_SMS_NOTIFICATIONS = "";
	public static String ADD_STANDARD = "";
	public static String ADD_VALUE_RULE = "";
	public static String ADD_WATER_USE_TYPE = "";
	public static String ADD_WQM_AREA = "";
	public static String ADD_WQM_AUTHORITY = "";

	// updated records for each table
	public static String UPDATE_ABNORMAL_RANGE = "";
	public static String UPDATE_AUTHORISED_SAMPLER = "";
	public static String UPDATE_AUTHORITY_MANAGER = "";
	public static String UPDATE_DOMAIN_LOOKUP = "";

	public static String UPDATE_MEASURED_VALUE;
	static {
		if (DebugConstants.MOCK_WEB_SERVICE_LONG_RESPONSES) {
			UPDATE_MEASURED_VALUE = "";
		} else {
			UPDATE_MEASURED_VALUE = "";
		}
	}

	public static String UPDATE_NORMAL_RANGE = "";
	public static String UPDATE_PARAMETER = "";
	public static String UPDATE_SAMPLE = "";
	public static String UPDATE_SAMPLING_POINT = "";
	public static String UPDATE_SMS_NOTIFICATIONS = "";
	public static String UPDATE_STANDARD = "";
	public static String UPDATE_VALUE_RULE = "";
	public static String UPDATE_WATER_USE_TYPE = "";
	public static String UPDATE_WQM_AREA = "";
	public static String UPDATE_WQM_AUTHORITY = "";

	// deleted records for each table
	public static String DELETE_ABNORMAL_RANGE = "";
	public static String DELETE_AUTHORISED_SAMPLER = "";
	public static String DELETE_AUTHORITY_MANAGER = "";
	public static String DELETE_DOMAIN_LOOKUP = "";
	public static String DELETE_MEASURED_VALUE = "";
	public static String DELETE_NORMAL_RANGE = "";
	public static String DELETE_PARAMETER = "";

	public static String DELETE_SAMPLE;
	static {
		if (DebugConstants.MOCK_WEB_SERVICE_LONG_RESPONSES) {
			DELETE_SAMPLE = "";
		} else {
			DELETE_SAMPLE = "";
		}
	}

	public static String DELETE_SAMPLING_POINT = "";
	public static String DELETE_SMS_NOTIFICATIONS = "";
	public static String DELETE_STANDARD = "";
	public static String DELETE_VALUE_RULE = "";
	public static String DELETE_WATER_USE_TYPE = "";
	public static String DELETE_WQM_AREA = "";
	public static String DELETE_WQM_AUTHORITY = "";

}
