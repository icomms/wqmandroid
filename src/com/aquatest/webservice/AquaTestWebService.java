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
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.aquatest.configuration.ConfigFile;
import com.aquatest.ui.AquaTestApp;

/**
 * Class to handle web service calls for AquaTest app.
 */
public class AquaTestWebService
{
	/**
	 * Server host name:<br>
	 */
	static final String SERVER_HOST = "http://10.0.2.2/webservice/";

	// JSON end points
	/** web service end point to return list of table names */
	public static final String TABLES = "table_names";
	/** web service end point to return added records */
	public static final String ADDED_ROWS = "added_rows";
	/** web service end point to return updated records */
	public static final String UPDATED_ROWS = "updated_rows";
	/** web service end point to return records id's of deleted records */
	public static final String DELETED_ROWS = "deleted_rows";

	// parameters to the GET requests
	public static final String TABLE_PARAM = "table";
	public static final String TIME_PARAM = "time";
	public static final String OFFSET_PARAM = "offset";
	public static final String BATCH_SIZE_PARAM = "limit";
	public static final String ACCESS_KEY_PARAM = "key";
	public static final String DOMAIN_PARAM = "domain"; //aka municipality
	
	public static final String ACCESS_KEY_VALUE = ""; //key to access web service

	// TODO this should be parameterised, or passed in from another class
	/** how many records to request from the web service in each response */
	public static final String BATCH_SIZE_VALUE = "500";
	public static final String BATCH_SIZE_PARAM_STRING = "&" + BATCH_SIZE_PARAM
			+ "=" + BATCH_SIZE_VALUE;
	
	/**
	 * Global settings as defined in the configuration file
	 * /assets/config/config.txt. This is set by {@link AquaTestApp}.
	 */
	public static ConfigFile config;	

	/**
	 * Invokes the web service that will return the list of table names.
	 * 
	 * @return JSONObject containing the table names
	 * @throws IOException
	 * @throws JSONException
	 * @throws ClientProtocolException
	 */
	// method declared static for Android optimisation
	public static JSONObject retrieveTables() throws ClientProtocolException,
			JSONException, IOException
	{
		return invoke(SERVER_HOST + TABLES);
	}

	/**
	 * Invokes a web service to return a list of data that has been added,
	 * updated or deleted.
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
	// method declared static for Android optimisation
	public static JSONObject retrieveDataChanges(String method, String table,
			long lastTimeUpdated, int offset) throws ClientProtocolException,
			JSONException, IOException
	{				
		// TODO delete call can handle a much bigger batch size than the other
		// calls, so change the batch size setting for deletes
		String requestUrl = SERVER_HOST + method + "?" + TABLE_PARAM + "="
				+ table + "&" + TIME_PARAM + "="
				+ String.valueOf(lastTimeUpdated / 1000) + "&" + OFFSET_PARAM
				+ "=" + String.valueOf(offset) + BATCH_SIZE_PARAM_STRING
				+ "&" + ACCESS_KEY_PARAM + "=" + ACCESS_KEY_VALUE;
		
		if (!config.isInitialised()) {
			config.init();
		}
		
		if (config.isLimitedToOneMunicipality()) {
			requestUrl += "&" + DOMAIN_PARAM + "=" + String.valueOf(config.getAllowedMunicipalityId());
		} else {
			requestUrl += "&" + DOMAIN_PARAM + "=0" ;
		}
				
		//Log.v("URL", requestUrl);
		return invoke(requestUrl);
	}

	/**
	 * Method to invoke a JSON web service and return the response.
	 * 
	 * @param url
	 *            the url to send the request to
	 * @return JSONObject made up from the web service response
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	// method declared static for Android optimisation
	private static JSONObject invoke(String url) throws JSONException,
			ClientProtocolException, IOException
	{
		// send a GET request to the correct url
		HttpResponse response = sendGetRequest(url);

		// create a JSONObject from the response
		JSONObject json = getJsonFromResponse(response);

		return json;
	}

	/**
	 * Converts an HTTP response object into a JSON object.
	 * 
	 * @param response
	 *            an HTTP response in JSON string format
	 * @return JSONObject created from the response
	 */
	// method declared static for Android optimisation
	public static JSONObject getJsonFromResponse(HttpResponse response)
			throws JSONException, ClientProtocolException, IOException
	{

		// read the response stream
		InputStream inputStream = response.getEntity().getContent();
		byte[] data = new byte[256];
		int len = 0;

		StringBuffer buffer = new StringBuffer();
		while (-1 != (len = inputStream.read(data)))
		{
			buffer.append(new String(data, 0, len));

			if (Thread.interrupted()) { return null; }
		}
		inputStream.close();

		// turn response string into a JSON object
		// TODO create better error handling
		JSONObject o = null;
		try
		{
			o = new JSONObject(buffer.toString());
		}
		catch (JSONException e)
		{
			// Log.v("WEBSERVICE", "Error creating JSON object from response");
			throw e;
		}

		return o;
	}

	/**
	 * Sends an HTTP GET request to the url.
	 * 
	 * @param url
	 *            URL to send GET request to
	 * @return HttpResponse object
	 * @throws IOException
	 *             if there is an IO error during the request
	 * @throws ClientProtocolException
	 *             if there is a protocol error during the request
	 */
	// method declared static for Android optimisation
	public static HttpResponse sendGetRequest(String url)
			throws ClientProtocolException, IOException
	{
		// create http GET request object
		// Log.v("WEBSERVICE", "creating GET request to [" + url + "]");
		HttpGet get = new HttpGet(url);

		// send GET message to the url
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpResponse response = httpClient.execute(get, localContext);

		return response;
	}

}
