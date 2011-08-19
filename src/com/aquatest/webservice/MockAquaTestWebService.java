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

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Mock class to fake web service calls, and instead return static JSON
 * responses. This can be used for testing and profiling the app in isolation,
 * without the uncertainty introduced by going online. </p> This class was
 * created with the original intention of of benchmarking changes to the way the
 * local database is updated based on results returned while synching to the
 * main AquaTest host.
 */
public class MockAquaTestWebService extends AquaTestWebService {

	/**
	 * Fakes the web service that will return the list of table names.
	 * 
	 * @return JSONObject containing the table names
	 * @throws IOException
	 * @throws JSONException
	 * @throws ClientProtocolException
	 */
	public static JSONObject retrieveTables() throws ClientProtocolException, JSONException, IOException 
	{
		JSONObject mockResponse = new JSONObject(MockJsonResponsesFull.TABLE_LIST);
		
		return mockResponse;
	}

	/**
	 * Fakes a web service to return a list of data that has been added, updated
	 * or deleted.
	 * 
	 * @param method
	 *            the specific web service to invoke
	 * @param table
	 *            table to invoke the web service on
	 * @param lastTimeUpdates
	 *            time of last update (in milliseconds)
	 * @param offset
	 *            offset of last update
	 * @return JSOBObject made up from the web service response, containing list
	 *         of relevant changes to the database
	 * @throws IOException
	 * @throws JSONException
	 * @throws ClientProtocolException
	 */
	public static JSONObject retrieveDataChanges(String method, String table, long lastTimeUpdated, int offset) throws ClientProtocolException, JSONException, IOException
	{
		JSONObject dataChanges = null;

		if (AquaTestWebService.ADDED_ROWS.equals(method)) {
			dataChanges = mockAddedRecords(table);
		} else if (AquaTestWebService.UPDATED_ROWS.equals(method)) {
			dataChanges = mockUpdatedRecords(table);
		} else if (AquaTestWebService.DELETED_ROWS.equals(method)) {
			dataChanges = mockDeletedRecords(table);
		}

		return dataChanges;
	}

	/**
	 * Fakes a web service call to the ADDED_ROWS method.
	 * 
	 * @param table
	 *            table to fake added records for
	 * @return JSONObject faking a list of added database records
	 */
	private static JSONObject mockAddedRecords(String table) {
		JSONObject mockResponse = null;

		try {

			if ("abnormalrange".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_ABNORMAL_RANGE);
			} else if ("authorisedsampler".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_AUTHORISED_SAMPLER);
			} else if ("authoritymanager".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_AUTHORITY_MANAGER);
			} else if ("domainlookup".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_DOMAIN_LOOKUP);
			} else if ("measuredvalue".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_MEASURED_VALUE);
			} else if ("normalrange".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_NORMAL_RANGE);
			} else if ("parameter".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_PARAMETER);
			} else if ("sample".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_SAMPLE);
			} else if ("samplingpoint".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_SAMPLING_POINT);
			} else if ("smsnotifications".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_SMS_NOTIFICATIONS);
			} else if ("standard".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_STANDARD);
			} else if ("valuerule".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_VALUE_RULE);
			} else if ("waterusetype".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_WATER_USE_TYPE);
			} else if ("wqmarea".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_WQM_AREA);
			} else if ("wqmauthority".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.ADD_WQM_AUTHORITY);
			}

		} catch (JSONException e) {
			Log.e("JSON",
					"Invalid JSON response for added records in table ["
							+ table + "]. Error is [" + e.getLocalizedMessage() + "].");
		}

		return mockResponse;
	}

	/**
	 * Fakes a web service call to the UPDATED_ROWD method.
	 * 
	 * @param table
	 *            table to fake updated records for
	 * @return JSONObject faking a list of updated database records
	 */
	private static JSONObject mockUpdatedRecords(String table) {
		JSONObject mockResponse = null;

		try {

			if ("abnormalrange".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_ABNORMAL_RANGE);
			} else if ("authorisedsampler".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_AUTHORISED_SAMPLER);
			} else if ("authoritymanager".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_AUTHORITY_MANAGER);
			} else if ("domainlookup".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_DOMAIN_LOOKUP);
			} else if ("measuredvalue".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_MEASURED_VALUE);
			} else if ("normalrange".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_NORMAL_RANGE);
			} else if ("parameter".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_PARAMETER);
			} else if ("sample".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_SAMPLE);
			} else if ("samplingpoint".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_SAMPLING_POINT);
			} else if ("smsnotifications".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_SMS_NOTIFICATIONS);
			} else if ("standard".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_STANDARD);
			} else if ("valuerule".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_VALUE_RULE);
			} else if ("waterusetype".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_WATER_USE_TYPE);
			} else if ("wqmarea".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_WQM_AREA);
			} else if ("wqmauthority".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.UPDATE_WQM_AUTHORITY);
			}

		} catch (JSONException e) {
			Log.e("JSON",
					"Invalid JSON response for updated records in table ["
							+ table + "]. Error is [" + e.getLocalizedMessage() + "].");
		}

		return mockResponse;
	}

	/**
	 * Fakes a web service call to the DELETED_ROWS method.
	 * 
	 * @param table
	 *            table to fake deleted records for
	 * @return JSONObject faking a list of deleted database records
	 */
	private static JSONObject mockDeletedRecords(String table) {
		JSONObject mockResponse = null;

		try {

			if ("abnormalrange".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_ABNORMAL_RANGE);
			} else if ("authorisedsampler".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_AUTHORISED_SAMPLER);
			} else if ("authoritymanager".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_AUTHORITY_MANAGER);
			} else if ("domainlookup".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_DOMAIN_LOOKUP);
			} else if ("measuredvalue".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_MEASURED_VALUE);
			} else if ("normalrange".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_NORMAL_RANGE);
			} else if ("parameter".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_PARAMETER);
			} else if ("sample".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_SAMPLE);
			} else if ("samplingpoint".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_SAMPLING_POINT);
			} else if ("smsnotifications".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_SMS_NOTIFICATIONS);
			} else if ("standard".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_STANDARD);
			} else if ("valuerule".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_VALUE_RULE);
			} else if ("waterusetype".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_WATER_USE_TYPE);
			} else if ("wqmarea".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_WQM_AREA);
			} else if ("wqmauthority".equals(table)) {
				mockResponse = new JSONObject(
						MockJsonResponsesFull.DELETE_WQM_AUTHORITY);
			}

		} catch (JSONException e) {
			Log.e("JSON",
					"Invalid JSON response for deleted records in table ["
							+ table + "]. Error is [" + e.getLocalizedMessage() + "].");
		}

		return mockResponse;
	}

}
