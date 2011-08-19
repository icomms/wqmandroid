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

import java.io.IOException;
import java.util.Vector;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aquatest.debug.DebugConstants;
import com.aquatest.webservice.AquaTestWebService;
import com.aquatest.webservice.MockAquaTestWebService;

/**
 * Class to manage updating of the database. </p> DatabaseUpdater accesses the
 * remote database on the main server, and will attempt to synchronise the data
 * on the device with the server.
 */
public class DatabaseUpdater extends Thread
{

	public static final String STATUS_KEY = "status";
	public static final String COUNT_KEY = "count";
	public static final String TOTAL_COUNT_KEY = "total_count";
	public static final String OFFSET_KEY = "offset";
	public static final String DATA_KEY = "data";

	public static final String STATUS_SUCCESS = "success";
	public static final String ERROR_CONNECTION = "Connection error.";
	public static final String CANCEL = "Cancel_Thread";

	// determines what type of changes are being dealt with in an operation
	/** indicates operation refers to added records */
	public static final int TYPE_ADDED = 0;
	/** indicates operation refers to updated records */
	public static final int TYPE_UPDATED = 1;
	/** indicates operation refers to deleted records */
	public static final int TYPE_DELETED = 2;

	// Handler message codes
	public static final int ERROR = 0;
	public static final int COMPLETE = 1;
	public static final int ITEM_COMPLETE = 2;
	public static final int ROW_COMPLETE = 3;
	public static final int CANCELLED = 4;
	public static final int QUERY_PROCESSING = 5;

	public static final long UPDATE_CHECK_PERIOD = 24 * 60 * 60 * 1000; // 1 day in milliseconds

	public static final long DEFAULT_LAST_UPDATE = 0;
	// TODO optimised out - don't understand why there would be a method call
	// when a final constant would work
	// public static long DefaultLastUpdate () {
	// long lastUpdate = 0;
	// return lastUpdate;
	// }

	public long lastUpdateTime; // in milliseconds
	private DatabaseAdaptor dA;
	private Handler h;

	/**
	 * Constructor
	 * 
	 * @param _time
	 *            time of last update
	 * @param _dA
	 *            databaseAdaptor to handle interaction with the database
	 */
	public DatabaseUpdater(long _time, DatabaseAdaptor _dA)
	{
		dA = _dA;
		lastUpdateTime = _time;
	}

	/**
	 * Set handler for receiving messages from this thread
	 * 
	 * @param _h
	 *            handler
	 */
	public void setHandler(Handler _h)
	{
		h = _h;
	}

	/**
	 * Send message from this thread using preset handler
	 * 
	 * @param s
	 *            message to be sent
	 * @param _code
	 *            code of the message for handler to be able to determine the
	 *            type of message
	 */
	public void sendMessage(String s, int code)
	{
		Bundle b = new Bundle();
		b.putString("msg", s);

		if (h != null)
		{
			Message msg = new Message();
			msg.setData(b);
			msg.what = code;
			h.sendMessage(msg);
		}
	}

	/**
	 * Send message from this thread using preset handler
	 * 
	 * @param b
	 *            message data to be sent
	 * @param _code
	 *            code of the message for handler to be able to determine the
	 *            type of message
	 */
	public void sendMessage(Bundle b, int code)
	{
		if (h != null)
		{
			Message msg = new Message();
			msg.setData(b);
			msg.what = code;
			h.sendMessage(msg);
		}
	}

	/**
	 * Run the updater to synchronise the device with the server. </p> Method
	 * cycles through all the database tables, and for each one, it retrieves
	 * records that have been added, changed or deleted.
	 */
	public void run()
	{
		try
		{
			long updateTime = System.currentTimeMillis();
			boolean result;

			Vector<String> tables = getTables();
			sendMessage("Table list downloaded: " + tables.size()
					+ " tables found.", ITEM_COMPLETE);

			// begin database transaction
			dA.database.beginTransaction();

			try
			{

				// loop through all the tables
				int tableCount = tables.size();
				for (int i = 0; i < tableCount; i++)
				{
					String tableName = tables.get(i);
					int k = i + 1;

					// ignore authoritymanager table
					if (tableName.compareTo("authoritymanager") == 0)
					{
						continue;
					} // if

					// retrieve ADDED rows
					sendMessage(tableName + " (table " + k + "/" + tableCount
							+ "): retrieving new records...", ITEM_COMPLETE);
					result = fetchAndExecuteQueries(TYPE_ADDED, tableName);

					if (!result)
					{
						// Log.v("THREAD", "KILLING");
						sendMessage("Update cancelled!", CANCELLED);
						return;
					} // if

					// retrieve UPDATED rows
					sendMessage(tableName + " (table " + k + "/" + tableCount
							+ "): retrieving updated records...", ITEM_COMPLETE);
					result = fetchAndExecuteQueries(TYPE_UPDATED, tableName);

					if (!result)
					{
						// Log.v("THREAD", "KILLING");
						sendMessage("Update cancelled!", CANCELLED);
						return;
					} // if

					// retrieve DELETED rows
					sendMessage(tableName + " (table " + k + "/" + tableCount
							+ "): retrieving deleted rows...", ITEM_COMPLETE);
					result = fetchAndExecuteQueries(TYPE_DELETED, tableName);

					if (!result)
					{
						// Log.v("THREAD", "KILLING");
						sendMessage("Update cancelled!", CANCELLED);
						return;
					} // if
				} // for

				// signal transaction can be committed
				dA.database.setTransactionSuccessful();
			}
			finally
			{
				// commit or rollback transaction
				dA.database.endTransaction();

			}

			// return success in a Bundle
			Bundle b = new Bundle();
			b.putString("msg", "Update complete!");
			b.putLong("time", updateTime);
			sendMessage(b, COMPLETE);

		}
		catch (JSONException jE)
		{
			sendMessage(jE.getMessage(), ERROR);
			return;
		}
		catch (ClientProtocolException cE)
		{
			sendMessage(
					cE.getMessage()
							+ " This is possibly caused by a lack of connectivity. "
							+ "Restart the app and try again after ensuring you have a valid connection.",
					ERROR);
			return;
		}
		catch (IOException iE)
		{
			sendMessage(
					iE.getMessage()
							+ " This is possibly caused by a lack of connectivity. "
							+ "Restart the app and try again after ensuring you have a valid connection.",
					ERROR);
			return;
		}
		catch (SQLiteException sE)
		{
			sendMessage("A SQLite exception occured: " + sE.getMessage(), ERROR);
			return;
		} // catch
	} // run

	/**
	 * Invoke web service to retrieve data, and then build and execute queries
	 * to update local database
	 * 
	 * @param type
	 *            of rows to fetch (TYPE_ADDED, TYPE_UPDATED, TYPE_DELETED)
	 * @param table
	 *            table to request rows for
	 * @return <code>true</code> if the method succeeds, <code>false</code> if
	 *         the method fails
	 * @throws ClientProtocolException
	 * @throws JSONException
	 *             if the data returned is not what was expected
	 * @throws IOException
	 */
	private boolean fetchAndExecuteQueries(int type, String table)
			throws ClientProtocolException, JSONException, IOException
	{
		// Log.v("START", "fetchAndExecuteQueries(" + type + ", " + table +
		// ")");
		try
		{

			int count = 0;
			int totalCount = 1;
			int offset = 0;
			String status = "";
			JSONArray dataArray = null;
			boolean run = true;

			// determine JSON method to contact with our web service call
			String wsMethodName = "";
			switch (type)
			{
				case TYPE_ADDED:
					wsMethodName = AquaTestWebService.ADDED_ROWS;
					break;

				case TYPE_UPDATED:
					wsMethodName = AquaTestWebService.UPDATED_ROWS;
					break;

				case TYPE_DELETED:
					wsMethodName = AquaTestWebService.DELETED_ROWS;
					break;

				// TODO a default case should be added with error handling
			} // switch

			// this loop allows for paging of the data from the web service -
			// server returns max 1000 records at a time
			// TODO return fewer records at a time to save memory on the device?
			// e.g sample return string is almost 800,000 characters long
			while (((count + offset) < totalCount) && run)
			{
				// fetch data from web service
				// Log.v("DatabaseUpdater", "invoking web service [" +
				// wsMethodName + "] on table [" + table + "]");

				// java compiler optimises this "if" statement away based on
				// value of MOCK_WEB_SERVICES i.e. similar to C compiler #ifdef
				// blocks
				JSONObject jsonResponse;
				if (DebugConstants.MOCK_WEB_SERVICES)
				{
					// mock web services
					jsonResponse = MockAquaTestWebService.retrieveDataChanges(
							wsMethodName, table, lastUpdateTime,
							(offset + count));
				}
				else
				{
					// use real production server
					jsonResponse = AquaTestWebService.retrieveDataChanges(
							wsMethodName, table, lastUpdateTime,
							(offset + count));
				}

				// // Log.v("JSON_REQUEST", wsMethodName + " : " + table);

				// cancel update if so result was returned
				if (jsonResponse == null)
					return false;

				// interpret the JSON results
				//try {
					status = jsonResponse.getString(STATUS_KEY);
				//} catch (JSONException e) {

				//}

				if (status.compareTo(STATUS_SUCCESS) == 0)
				{
					// these fields allow for paging of the responses
					count = jsonResponse.getInt(COUNT_KEY);
					totalCount = jsonResponse.getInt(TOTAL_COUNT_KEY);
					offset = jsonResponse.getInt(OFFSET_KEY);

					// process the returned data
					if (count > 0 && jsonResponse.has(DATA_KEY))
					{
						// get the data array
						dataArray = jsonResponse.getJSONArray(DATA_KEY);

						// create the prepared sql statement
						// FIXME this currently assumes that the first object
						// contains all the field names needed
						JSONArray dataFieldNames = dataArray.getJSONObject(0)
								.names();
						SQLiteStatement preparedStatement = generateQueryString(
								type, table, dataFieldNames);

						try
						{
							// do this to optimise the Android code
							int dataLength = dataArray.length();

							// loop over the returned data array
							for (int i = 0; i < dataLength; i++)
							{
								// check if thread has been cancelled
								if (Thread.interrupted())
									return false;

								// get the current data record
								JSONObject row = dataArray.getJSONObject(i);

								// add parameters to the prepared statement
								bindQueryParameters(preparedStatement, type,
										dataFieldNames, row);

								// Log.v("SQL", "executing statement for data: "
								// + row);

								// execute the prepared statement
								preparedStatement.execute();
							} // for
						}
						finally
						{
							// release resources
							preparedStatement.close();
						}
					}
					// else exit
					else
					{
						// Log.v("JSON", "empty or no data key: " + table + ", "
						// + wsMethodName + "(" + offset + "," + count + ")");
						run = false;
					} // else
				}
				// else the call failed, so do not continue
				else
				{
					// Log.v("DatabaseUpdater",
					// "web service call failed with status [" + status + "]");
					run = false;
				} // else

			} // while

			return true;
		}
		finally
		{
			// Log.v("END", "fetchAndExecuteQueries(" + type + ", " + table +
			// ")");
		}
	} // fetchAndExecuteQueries

	/**
	 * Generates a SQL query string depending on the type of operation
	 * requested.
	 * 
	 * @param type
	 *            Operation type. Can be one of <code>TYPE_ADDED</code>,
	 *            <code>TYPE_UPDATED</code> or <code>TYPE_DELETED</code>
	 * @param table
	 *            database table to build the SQL query on
	 * @param fieldNames
	 *            names of the columns needed in this query
	 * @return prepared sql statement for the fields in the table
	 * @throws JSONException
	 *             if fieldNames contains an object that is not a String
	 */
	private SQLiteStatement generateQueryString(int type, String table,
			JSONArray fieldNames) throws JSONException
	{
		switch (type)
		{
			case TYPE_ADDED:
				return generateAddQueryString(table, fieldNames);

			case TYPE_UPDATED:
				return generateUpdateQueryString(table, fieldNames);

			case TYPE_DELETED:
				// DELETE statements do not need the field names
				return generateDeleteQueryString(table);

			default:
				// TODO clean up this attempt at handling an unknown type
				return null;
		} // switch
	}


	/**
	 * Generates a SQL query string to INSERT new data into the database.
	 * 
	 * @param table
	 *            database table to build the SQL query on
	 * @param fieldNames
	 *            names of the columns needed in this query
	 * @return prepared sql statement for the fields in the table
	 * @throws JSONException
	 *             if fieldNames contains an object that is not a String
	 */
	private SQLiteStatement generateAddQueryString(String table,
			JSONArray fieldNames) throws JSONException
	{
		StringBuilder sql = new StringBuilder();
		StringBuilder values = new StringBuilder();

		// initialise the query with the table
		// TODO escape the table name
		sql.append("INSERT INTO ").append(table).append(" (");
		values.append(") VALUES (");

		// do this to optimise the Android code
		int fieldCount = fieldNames.length();

		// add items to sql by iterating over the array
		for (int i = 0; i < fieldCount; i++)
		{
			String fieldName = fieldNames.getString(i);

			// exclude some columns from the update
			if (includeFieldInUpdates(fieldName))
			{
				// add the field name to sql
				sql.append(" ");

				// id field must be renamed _id
				if ("id".equals(fieldName))
				{
					sql.append("_");
				}

				sql.append(fieldName).append(",");

				// add a parameter placeholder
				values.append(" ?,");
			}

		}

		// remove trailing commas
		sql.deleteCharAt(sql.length() - 1);
		values.deleteCharAt(values.length() - 1);

		// close off the query
		sql.append(values).append(")");

		// Log.v("SQL", "prepared sql statement: [" + sql.toString() + "]");

		// return the prepared query
		return dA.database.compileStatement(sql.toString());
	} // generateAddQueryString


	/**
	 * Generates a SQL query to UPDATE existing data in the database.
	 * 
	 * @param table
	 *            database table to build the SQL query on
	 * @param fieldNames
	 *            names of the columns needed in this query
	 * @return prepared sql statement for the fields in the table
	 * @throws JSONException
	 *             if fieldNames contains an object that is not a String
	 */
	private SQLiteStatement generateUpdateQueryString(String table,
			JSONArray fieldNames) throws JSONException
	{
		StringBuilder sql = new StringBuilder();

		// initialise the query with the table
		// TODO escape the table name
		sql.append("UPDATE ").append(table).append(" SET");

		// do this to optimise the Android code
		int fieldCount = fieldNames.length();

		// add items to sql by iterating over the array
		for (int i = 0; i < fieldCount; i++)
		{
			String fieldName = fieldNames.getString(i);

			// exclude some columns from the update
			if (includeFieldInUpdates(fieldName))
			{
				// add the field name to sql
				sql.append(" ");

				// id field must be renamed _id
				if ("id".equals(fieldName))
				{
					sql.append("_");
				}

				sql.append(fieldName).append(" = ?,");

			}

		}

		// remove trailing comma
		sql.deleteCharAt(sql.length() - 1);

		// close off the query
		sql.append(" WHERE (_id = ?)");

		// Log.v("SQL", "prepared sql statement: [" + sql.toString() + "]");

		// return the prepared query
		return dA.database.compileStatement(sql.toString());
	} // generateUpdateQueryString


	/**
	 * Generates a SQL query to DELETE old data from the database.
	 * 
	 * @param table
	 *            database table to build the SQL query on
	 * @return prepared sql statement for the fields in the table
	 */
	private SQLiteStatement generateDeleteQueryString(String table)
	{
		// initialise the query with the table
		// TODO escape the table name
		String sql = "DELETE FROM " + table + " WHERE (_id = ?)";

		// Log.v("SQL", "prepared sql statement: [" + sql.toString() + "]");

		// return the prepared query
		return dA.database.compileStatement(sql);
	} // generateDeleteQueryString


	/**
	 * Populates the "?" parameters in the prepared statement with values from
	 * the included JSON object. It is assumed that the prepared statement was
	 * created based on the same set of field names (in the same order) as have
	 * been passed to this method.
	 * 
	 * @param preparedStatement
	 *            previously prepared SQL statement, which includes "?"
	 *            parameter placeholders
	 * @param type
	 *            Operation type. Can be one of <code>TYPE_ADDED</code>,
	 *            <code>TYPE_UPDATED</code> or <code>TYPE_DELETED</code>
	 * @param fieldNames
	 *            array of field names relating to this query
	 * @param dataRow
	 *            JSON object containing data relevant to this prepared
	 *            statement
	 * @throws JSONException
	 *             if the JSON array or data row contain invalid JSON
	 */
	// method declared static for Android optimisation
	private static void bindQueryParameters(SQLiteStatement preparedStatement,
			int type, JSONArray fieldNames, JSONObject dataRow)
			throws JSONException
	{
		// initialise the query by clearing out any previous parameters
		preparedStatement.clearBindings();

		// index used to bind parameters to the prepared statement
		int paramIndex = 1;

		// DELETE statements only need the id field bound, other require all
		// fields
		if (type == TYPE_DELETED)
		{
			// assume the id field exists for a DELETE statement
			String value = dataRow.getString("deleted_id");
			preparedStatement.bindString(1, value);
		}
		else
		{
			// do this to optimise the Android code
			int fieldCount = fieldNames.length();

			// add items to prepared statement by iterating over the array
			for (int i = 0; i < fieldCount; i++)
			{
				String fieldName = fieldNames.getString(i);

				// exclude some columns from the update
				if (includeFieldInUpdates(fieldName))
				{
					// get data from JSON and add as parameter
					preparedStatement.bindString(paramIndex++,
							dataRow.getString(fieldName));
				}
			}
		}
	}

	/**
	 * Indicates whether this field should be updated by the web services. </p>
	 * Some fields should not be included.
	 * 
	 * @param field
	 *            Name of database field to check for inclusion.
	 * @return true if updates to this field must be made, false if this field
	 *         must not be updated
	 */
	// method declared static for Android optimisation
	private static boolean includeFieldInUpdates(String field)
	{
		// add a new line to exclude a certain column
		if ("created".equals(field))
			return false;

		if ("modified".equals(field))
			return false;

		if ("the_geom".equals(field))
			return false;

		if ("date_received".equals(field))
			return false;

		return true;
	} // includeFieldInUpdates

	/**
	 * Invoke web service to retrieve the tables to be updated
	 * 
	 * @return vector of table names
	 * @throws ClientProtocolException
	 * @throws JSONException
	 * @throws IOException
	 */
	// method declared static for Android optimisation
	private static Vector<String> getTables() throws JSONException,
			ClientProtocolException, IOException
	{
		// java compiler optimises this away if statement based on value of
		// MOCK_WEB_SERVICES i.e. similar to C compiler #ifdef blocks

		JSONObject tableResponse;
		if (DebugConstants.MOCK_WEB_SERVICES)
		{
			// mock web services
			tableResponse = MockAquaTestWebService.retrieveTables();
		}
		else
		{
			// real production server
			tableResponse = AquaTestWebService.retrieveTables();
			;
		}

		if (tableResponse == null)
			return new Vector<String>();

		Vector<String> tables = new Vector<String>();

		int count = 0;
		String status = "";
		JSONArray a = null;

		status = tableResponse.getString(STATUS_KEY);

		if (status.compareTo(STATUS_SUCCESS) == 0)
		{
			count = tableResponse.getInt(COUNT_KEY);

			if (count > 0)
			{
				a = tableResponse.getJSONArray(DATA_KEY);

				for (int i = 0; i < a.length(); i++)
				{
					tables.add((String) a.get(i));
				}
			}
		}

		return tables;
	}

}
