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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.aquatest.configuration.ConfigFile;
import com.aquatest.dbinterface.objects.Municipality;
import com.aquatest.dbinterface.objects.Parameter;
import com.aquatest.dbinterface.objects.Range;
import com.aquatest.dbinterface.objects.Town;
import com.aquatest.dbinterface.objects.ValueKey;
import com.aquatest.dbinterface.objects.ValueRule;
import com.aquatest.dbinterface.tools.DataUtils.ParameterColumns;
import com.aquatest.dbinterface.tools.DataUtils.RangeColumns;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;
import com.aquatest.dbinterface.tools.DataUtils.ValueRuleColumns;
import com.aquatest.ui.AquaTestApp;

/**
 * Class to manage all interaction with the device database
 */
public class DatabaseAdaptor
{

	private DatabaseProvider provider;
	public SQLiteDatabase database;

	public HashMap<Integer, Parameter> parameters;
	private HashMap<Integer, Range> ranges;
	private HashMap<Integer, ValueRule> valueRules;
	private HashMap<Integer, ValueKey> valueKeys;

	public HashMap<Integer, Municipality> municipalities;

	// these identify different area types
	/** Municipality area type id */
	public static final int MUNICIPALITY = 0;
	/** Town area type id */
	public static final int TOWN = 1;
	/** Sampling Point area type id */
	public static final int SAMPLINGPOINT = 2;

	// TODO this should be a global constant
	public static final int OVERVIEW_PERIOD_DAYS = 30;

	// TODO this flag may need to be removed
	public static final boolean HIDE_EMPTY_DATA = true;

	public static final String[] tables = { 
		"abnormalrange", "authorisedsampler", "domainlookup", "measuredvalue",
		"normalrange", "parameter", "sample", "samplingpoint",
		"smsnotifications", "standard", "valuerule", "waterusetype", "wqmarea",
		"wqmauthority"
	};

	/**
	 * Global settings as defined in the configuration file
	 * /assets/config/config.txt. This is set by {@link AquaTestApp}.
	 */
	public static ConfigFile config;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public DatabaseAdaptor(Context context)
	{

		provider = new DatabaseProvider(context);
		database = null;

		try
		{
			provider.createDataBase();
		}
		catch (IOException ioe)
		{

			throw new Error("Unable to create database");
		}

		try
		{

			database = provider.openDataBase();

		}
		catch (SQLException sqle)
		{

			throw sqle;
		}

		// reset base data from the database
		reset();
	}

	/**
	 * Re-retrieve all base data from database.
	 */
	public void reset()
	{

		ranges = getRanges();
		valueRules = getValueRules();
		valueKeys = getValueKeys();
		parameters = getParameters();
		municipalities = getMunicipalities();

	}

	/**
	 * Close database connection.
	 */
	public void close()
	{
		provider.close();
	}

	/**
	 * Get ranges as a map filtered by a value rule
	 * 
	 * @param valueRuleId
	 *        id of value rule
	 * @return map of ranges, mapped as range id to range
	 */
	private HashMap<Integer, Range> getRangesByValueRule(int valueRuleId)
	{

		HashMap<Integer, Range> rangesByValueRule = new HashMap<Integer, Range>();

		Iterator<Integer> i = ranges.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			if (ranges.get(key).valueRuleId == valueRuleId)
			{
				rangesByValueRule.put(key, ranges.get(key));
			}
		}

		return rangesByValueRule;
	}

	/**
	 * Get value rule of a specified parameter.
	 * 
	 * @param parameterId
	 *        id of parameter
	 * @return value rule
	 */
	private ValueRule getValueRuleByParameter(int parameterId)
	{

		ValueRule valueRule = null;

		Iterator<Integer> i = valueRules.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			if (valueRules.get(key).parameterId == parameterId)
			{
				valueRule = valueRules.get(key);

				break;
			}
		}

		return valueRule;
	}

	/**
	 * Get all parameters as a map of id to ranges.
	 * 
	 * @return map of ranges.
	 */
	private HashMap<Integer, Range> getRanges()
	{

		HashMap<Integer, Range> rs = new HashMap<Integer, Range>();

		Cursor cursor = database.query("abnormalrange", null, null, null, null,
			null, null);

		if (cursor.moveToFirst())
		{

			do
			{

				int id = cursor.getInt(cursor.getColumnIndex(RangeColumns.ID));
				String description = cursor.getString(cursor
					.getColumnIndex(RangeColumns.DESCRIPTION));
				int valueRuleId = cursor.getInt(cursor
					.getColumnIndex(RangeColumns.VALUERULEID));
				double minimum = cursor.getDouble(cursor
					.getColumnIndex(RangeColumns.MINIMUM));
				double maximum = cursor.getDouble(cursor
					.getColumnIndex(RangeColumns.MAXIMUM));
				String colour = cursor.getString(cursor
					.getColumnIndex(RangeColumns.COLOUR));
				int wqmAuthorityId = cursor.getInt(cursor
					.getColumnIndex(RangeColumns.WQMAUTHORITYID));
				
				Range range = new Range(id, description, valueRuleId, minimum,
					maximum, colour, wqmAuthorityId);

				rs.put(id, range);

			}
			while (cursor.moveToNext());
		}

		cursor.close();

		Cursor cursor2 = database.query("normalrange", null, null, null, null,
			null, null);

		if (cursor2.moveToFirst())
		{

			do
			{

				int id = cursor2
					.getInt(cursor2.getColumnIndex(RangeColumns.ID));
				String description = cursor2.getString(cursor2
					.getColumnIndex(RangeColumns.DESCRIPTION));
				int valueRuleId = cursor2.getInt(cursor2
					.getColumnIndex(RangeColumns.VALUERULEID));
				double minimum = cursor2.getDouble(cursor2
					.getColumnIndex(RangeColumns.MINIMUM));
				double maximum = cursor2.getDouble(cursor2
					.getColumnIndex(RangeColumns.MAXIMUM));
				String colour = "green";
				int wqmAuthorityId = cursor2.getInt(cursor2
						.getColumnIndex(RangeColumns.WQMAUTHORITYID));				

				Range range = new Range(id, description, valueRuleId, minimum,
					maximum, colour, wqmAuthorityId);

				rs.put(id, range);

			}
			while (cursor2.moveToNext());
		}

		cursor2.close();

		return rs;
	}

	/**
	 * Get all parameters as a map of id to value rules.
	 * 
	 * @return map of value rules.
	 */
	private HashMap<Integer, ValueRule> getValueRules()
	{

		if (ranges == null)
			getRanges();

		HashMap<Integer, ValueRule> vRs = new HashMap<Integer, ValueRule>();

		Cursor cursor = database.query("valuerule", null, null, null, null,
			null, null);

		if (cursor.moveToFirst())
		{

			do
			{

				int id = cursor.getInt(cursor
					.getColumnIndex(ValueRuleColumns.ID));
				int parameterId = cursor.getInt(cursor
					.getColumnIndex(ValueRuleColumns.PARAMETERID));
				String description = cursor.getString(cursor
					.getColumnIndex(ValueRuleColumns.DESCRIPTION));

				ValueRule valueRule = new ValueRule(id, description,
					parameterId, getRangesByValueRule(id));//mike

				vRs.put(id, valueRule);

			}
			while (cursor.moveToNext());
		}

		cursor.close();

		return vRs;
	}

	/**
	 * Get all value keys as a map of id to value key.
	 * 
	 * @return map of value keys.
	 */
	private HashMap<Integer, ValueKey> getValueKeys()
	{

		HashMap<Integer, ValueKey> vKs = new HashMap<Integer, ValueKey>();

		Cursor cursor = database.query("domainlookup", null, null, null, null,
			null, null);

		if (cursor.moveToFirst())
		{

			do
			{
				int id = cursor.getInt(0);
				int parameterId = cursor.getInt(3);
				String key = cursor.getString(1);
				double value = cursor.getDouble(2);

				ValueKey valueKey = new ValueKey(id, key, value, parameterId);

				vKs.put(id, valueKey);

			}
			while (cursor.moveToNext());
		}

		cursor.close();

		return vKs;

	}

	/**
	 * Get all Parameter objects as a map of id to parameter. </p> Parameter
	 * objects are returned by querying the database. </p> This can typically be
	 * called once, when the app starts up.
	 * 
	 * @return map of parameters.
	 */
	private HashMap<Integer, Parameter> getParameters()
	{

		if (valueRules == null)
			getValueRules();
		if (valueKeys == null)
			getValueKeys();

		HashMap<Integer, Parameter> ps = new HashMap<Integer, Parameter>();

		Cursor cursor;

		// retrieve all required parameters
		cursor = database.rawQuery("SELECT * FROM parameter", null);

		if (cursor.moveToFirst())
		{

			do
			{
				int id = cursor.getInt(cursor
					.getColumnIndex(ParameterColumns.ID));
				String testname = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.TESTNAME));
				String units = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.UNITS));
				String lookuphint = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.LOOKUPHINT));
				String testnameshort = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.TESTNAMESHORT));

				Parameter parameter = new Parameter(id, testname, units,
					lookuphint, testnameshort, getValueRuleByParameter(id));

				// Load domain lookups
				Iterator<Integer> i = valueKeys.keySet().iterator();
				while (i.hasNext())
				{

					Integer key = i.next();
					ValueKey vK = valueKeys.get(key);
					if (vK.parameterId == id)
						parameter.valueKeys.put(vK.value, vK.key);
				}

				ps.put(id, parameter);

			}
			while (cursor.moveToNext());
		}

		cursor.close();

		return ps;
	}

	/**
	 * Get all municipalities as a map of id to municipality. </p>
	 * 
	 * If the {@link ConfigFile} settings have limited the user to one
	 * municipality, then this method will only return that one allowed
	 * municipality.
	 * 
	 * @return map of municipalities.
	 */
	private HashMap<Integer, Municipality> getMunicipalities()
	{

		HashMap<Integer, Municipality> municipalityMap = new HashMap<Integer, Municipality>();

		// query the wqmauthority table for all municipality names & id's
		String[] columns = { "_id", "name" };
		Cursor municipalityCursor = database.query("wqmauthority", columns,
			null, null, null, null, null);

		// initialise the config file because this is the first use of it
		// TODO make ConfigFile more robust - maybe lazy-loaded singleton
		if (!config.isInitialised())
		{
			config.init();
		}

		// save returned results to the HashMap if they are allowed by
		// ConfigFile
		if (municipalityCursor.moveToFirst())
		{
			int municipalityId;
			do
			{
				municipalityId = municipalityCursor.getInt(0);

				// create & add municipality if allowed
				if ((!config.isLimitedToOneMunicipality())
					|| (config.getAllowedMunicipalityId() == municipalityId))
				{
					Municipality municipality = new Municipality(
						municipalityId, municipalityCursor.getString(1));
					municipalityMap.put(municipalityCursor.getInt(0),
						municipality);
				}
			}
			while (municipalityCursor.moveToNext());
		}
		municipalityCursor.close();

		/*
		 * Municipality map has now been populated, and we need to populate each
		 * municipality with its towns.
		 */

		// query wqmarea & samplingpoint tables for all towns & the sample
		// coverage within each town
		Cursor townCursor = database
			.rawQuery(
				"SELECT wqmarea._id, wqmarea.name, wqmarea.wqmauthority, "
					+ "max(x_coord), min(x_coord), max(y_coord), min(y_coord) "
					+ "FROM wqmarea inner join samplingpoint on wqmarea._id = samplingpoint.wqmarea "
					+ "WHERE x_coord IS NOT NULL AND x_coord != 0 AND y_coord IS NOT NULL AND y_coord != 0 "
					+ "GROUP BY wqmarea._id, wqmarea.name, wqmarea.wqmauthority",
				null);

		if (townCursor.moveToFirst())
		{

			do
			{
				// get results
				int id = townCursor.getInt(0);
				String name = townCursor.getString(1);
				int municipalityId = townCursor.getInt(2);
				double centreX = 0;
				double centreY = 0;

				// calculate town centre point (used in AquaMap class)
				if (townCursor.getDouble(3) > 0)
				{
					double westX = townCursor.getDouble(4);
					double eastX = townCursor.getDouble(3);
					double southY = townCursor.getDouble(6);
					double northY = townCursor.getDouble(5);

					centreX = westX + (Math.abs(eastX - westX) / 2);
					centreY = southY + (Math.abs(northY - southY) / 2);
				}

				// add town to municipality if it is in the map already
				if (municipalityMap.containsKey(municipalityId))
				{
					municipalityMap.get(municipalityId).towns.put(id, new Town(
						id, name, centreX, centreY));
				}

			}
			while (townCursor.moveToNext());
		}
		townCursor.close();

		return municipalityMap;
	}

	/**
	 * Get town by id. This method searches all the towns in each municipality
	 * until it finds the match.
	 * 
	 * @param townId
	 *        id of town
	 * 
	 * @return town with the given id; if no matching town is found, returns
	 *         <code>null</code>
	 */
	public Town getTown(int townId)
	{

		Iterator<Integer> i = municipalities.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			Municipality m = municipalities.get(key);

			if (m.towns.containsKey(townId))
				return m.towns.get(townId);
		}

		return null;
	}

	/**
	 * Get municipality by id. This method searches the
	 * <code>municipalities</code> HashMap for a match.
	 * 
	 * @param municipalityId
	 *        id of municipality
	 * @return municipality with the given id; if no matching municipality is
	 *         found, returns <code>null</code>
	 */
	public Municipality getMunicipality(int municipalityId)
	{
		return this.municipalities.get(municipalityId);
	}

	/**
	 * Get list of municipalities ordered by name.
	 * 
	 * @return vector of ordered municipalities
	 */
	public Vector<Municipality> getOrderedMunicipalities()
	{

		Vector<Municipality> mVector = new Vector<Municipality>();

		Iterator<Integer> i = municipalities.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			Municipality municipality = municipalities.get(key);
			if ((!HIDE_EMPTY_DATA) || (municipality.towns.size() > 0))
			{

				if (mVector.size() == 0)
				{
					mVector.add(municipality);
				}
				else
				{
					Iterator<Municipality> i1 = mVector.iterator();

					int position = 0;
					boolean inserted = false;

					while (i1.hasNext())
					{
						if (municipality.name.compareTo(i1.next().name) < 0)
						{

							inserted = true;
							break;
						}
						position++;
					}

					if (inserted)
					{
						mVector.insertElementAt(municipality, position);
					}
					else
					{
						mVector.add(municipality);
					}
				}
			}
		}

		return mVector;
	}

	/**
	 * Get list of all parameters ordered by name.
	 * 
	 * @return ordered vector of parameters
	 */
	public Vector<Parameter> getOrderedParameters()
	{

		Vector<Parameter> mVector = new Vector<Parameter>();

		Iterator<Integer> i = parameters.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			Parameter parameter = parameters.get(key);

			if (mVector.size() == 0)
			{
				mVector.add(parameter);
			}
			else
			{
				Iterator<Parameter> i1 = mVector.iterator();

				int position = 0;
				boolean inserted = false;

				while (i1.hasNext())
				{
					if (parameter.testName.compareTo(i1.next().testName) < 0)
					{

						inserted = true;
						break;
					}
					position++;
				}

				if (inserted)
				{
					mVector.insertElementAt(parameter, position);
				}
				else
				{
					mVector.add(parameter);
				}
			}
		}

		return mVector;
	}

	/**
	 * Get parameters filtered by a town. Only parameters that have measured
	 * values at that town are returned. This method only searches for
	 * parameters that have been sampled in the last month from the current
	 * date.
	 * 
	 * @param townId
	 *        id of the town
	 * @return vector of parameters
	 */
	public Vector<Parameter> getOrderedActiveParametersByTown(int townId)
	{

		if (valueRules == null)
			getValueRules();
		if (valueKeys == null)
			getValueKeys();

		Vector<Parameter> ps = new Vector<Parameter>();

		Cursor cursor;
		String startDate = DataUtils
			.calendarToString(DataUtils.calendarSubtractDays(
				Calendar.getInstance(), OVERVIEW_PERIOD_DAYS));

		cursor = database
			.rawQuery(
				"SELECT * FROM parameter WHERE _id in "
					+ "(SELECT parameter FROM sample INNER JOIN measuredvalue on sample._id = sample "
					+ "WHERE datetaken >= '"
					+ startDate
					+ "' "
					+ "AND samplingpoint in (SELECT _id FROM samplingpoint where wqmarea = "
					+ townId + ")) " + "ORDER BY testname", null);

		if (cursor.moveToFirst())
		{

			do
			{

				int id = cursor.getInt(cursor
					.getColumnIndex(ParameterColumns.ID));
				String testname = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.TESTNAME));
				String units = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.UNITS));
				String lookuphint = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.LOOKUPHINT));
				String testnameshort = cursor.getString(cursor
					.getColumnIndex(ParameterColumns.TESTNAMESHORT));

				Parameter parameter = new Parameter(id, testname, units,
					lookuphint, testnameshort, getValueRuleByParameter(id));

				// Load domain lookups
				Iterator<Integer> i = valueKeys.keySet().iterator();
				while (i.hasNext())
				{

					Integer key = i.next();
					ValueKey vK = valueKeys.get(key);
					if (vK.parameterId == id)
						parameter.valueKeys.put(vK.value, vK.key);
				}

				ps.addElement(parameter);

			}
			while (cursor.moveToNext());
		}

		cursor.close();

		return ps;
	}

	/**
	 * Get the overall quality of a municipality or town for a specified period.
	 * 
	 * @param areaId
	 *        id of the town or municipality
	 * @param areaType
	 *        town or municipality
	 * @param startDate
	 *        start date of period
	 * @param endDate
	 *        end date of period
	 * @return the quality as a DataIndicator object.
	 */
	// TODO this method clashes with the attempt to get the colour of points in
	// AquaMap class
	public DataIndicator getAreaOverallQuality(int areaId, int areaType,
		Calendar startDate, Calendar endDate)
	{

		String colour = TestIndicators.NONE;
		double totalCount = 0;
		double passCount = 0;

		String startDateString = null;
		String endDateString = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		startDateString = sdf.format(startDate.getTime());
		endDateString = sdf.format(endDate.getTime());

		String queryString = null;

		if (areaType == MUNICIPALITY)
		{
			queryString = "SELECT datetaken, parameter, value from sample "
				+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
				+ "WHERE sample.datetaken <= '"
				+ endDateString
				+ "' AND sample.datetaken >= '"
				+ startDateString
				+ "' "
				+ "AND samplingpoint IN (SELECT samplingpoint._id FROM samplingpoint inner join wqmarea on wqmarea._id = samplingpoint.wqmarea WHERE wqmarea.wqmauthority = "
				+ areaId + ")" + " ORDER BY datetaken";
		}
		else
		{
			queryString = "SELECT datetaken, parameter, value from sample "
				+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
				+ "WHERE sample.datetaken <= '"
				+ endDateString
				+ "' AND sample.datetaken >= '"
				+ startDateString
				+ "' "
				+ "AND samplingpoint IN (SELECT _id FROM samplingpoint WHERE wqmarea = "
				+ areaId + ")" + " ORDER BY datetaken";
		}

		Cursor cursor = database.rawQuery(queryString, null);

		if (cursor.moveToFirst())
		{

			do
			{
				Parameter parameter = parameters.get(cursor.getInt(1));
				double value = cursor.getDouble(2);
				Range tempRange = parameter.getRangeOfValue(value);
				
				// TODO mike
				String tempColour = TestIndicators.PASS;
				
				if (tempRange != null) {
					tempColour = tempRange.colour;
				} 

				if (tempColour.compareTo(TestIndicators.FAIL) == 0)
				{
					colour = tempColour;
					totalCount++;
					// return tempColour;
				}
				else if (tempColour.compareTo(TestIndicators.WARNING) == 0)
				{
					if (colour.compareTo(TestIndicators.FAIL) != 0)
						colour = tempColour;

					totalCount++;
					passCount++;
				}
				else if (tempColour.compareTo(TestIndicators.PASS) == 0)
				{
					if (colour.compareTo(TestIndicators.NONE) == 0)
						colour = tempColour;

					totalCount++;
					passCount++;
				}

			}
			while (cursor.moveToNext());
		}
		else
		{
			cursor.close();
			return new DataIndicator(0, colour, "");
		}

		cursor.close();

		DecimalFormat dF = new DecimalFormat("#.#%");

		return new DataIndicator(passCount / totalCount, colour,
			dF.format(passCount / totalCount));
	}

	/**
	 * Get the quality of a municipality or town for a particular period as a
	 * map of dates to results represented as a DataIndicator object.
	 * 
	 * @param areaId
	 *        id of the town or municipality
	 * @param areaType
	 *        town or municipality
	 * @param startDate
	 *        start date of period
	 * @param endDate
	 *        end date of period
	 * @return map of dates as String to results as DataIndicator
	 */
	public HashMap<String, DataIndicator> getPeriodOverallQuality(int areaId,
		int areaType, Calendar startDate, Calendar endDate)
	{

		HashMap<String, DataIndicator> dateSeries = new HashMap<String, DataIndicator>();

		String startDateString = null;
		String endDateString = null;

		startDateString = DataUtils.calendarToString(startDate);
		endDateString = DataUtils.calendarToString(endDate);

		String queryString = null;

		if (areaType == MUNICIPALITY)
		{
			queryString = "SELECT datetaken, parameter, value from sample "
				+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
				+ "WHERE sample.datetaken <= '"
				+ endDateString
				+ "' AND sample.datetaken >= '"
				+ startDateString
				+ "' "
				+ "AND samplingpoint IN (SELECT samplingpoint._id FROM samplingpoint inner join wqmarea on wqmarea._id = samplingpoint.wqmarea WHERE wqmarea.wqmauthority = "
				+ areaId + ")" + "ORDER BY datetaken";
		}
		else
		{
			queryString = "SELECT datetaken, parameter, value from sample "
				+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
				+ "WHERE sample.datetaken <= '"
				+ endDateString
				+ "' AND sample.datetaken >= '"
				+ startDateString
				+ "' "
				+ "AND samplingpoint IN (SELECT _id FROM samplingpoint WHERE wqmarea = "
				+ areaId + ")" + " ORDER BY datetaken";
		}

		Cursor cursor = database.rawQuery(queryString, null);

		if (cursor.moveToFirst())
		{
			String currentDateString = cursor.getString(0);
			double totalCount = 0;
			double passCount = 0;
			String currentDateColour = TestIndicators.NONE;

			do
			{
				Parameter recordParameter = parameters.get(cursor.getInt(1));
				double recordValue = cursor.getDouble(2);
				String recordDateString = cursor.getString(0);
				Range tempRange;

				String recordColour = TestIndicators.NONE;
				if (recordParameter != null)
				{
					tempRange = recordParameter.getRangeOfValue(recordValue);
					
					if (tempRange != null) {
						recordColour = tempRange.colour; //TODO mike
					}
				}

				if (recordDateString.compareTo(currentDateString) != 0)
				{
					DecimalFormat dF = new DecimalFormat("#.#%");
					DataIndicator dI = new DataIndicator(
						passCount / totalCount, currentDateColour,
						dF.format(passCount / totalCount));
					dateSeries.put(currentDateString, dI);
					// Log.v(TAG, recordDateString + " " +
					// dF.format(passCount / totalCount));

					currentDateString = recordDateString;

					totalCount = 0;
					currentDateColour = TestIndicators.NONE;
					passCount = 0;
				}

				totalCount++;
				if (recordColour.compareTo(TestIndicators.FAIL) == 0)
				{
					currentDateColour = TestIndicators.FAIL;
				}
				else if (recordColour.compareTo(TestIndicators.PASS) == 0)
				{
					passCount++;
					if (currentDateColour.compareTo(TestIndicators.NONE) == 0)
					{
						currentDateColour = TestIndicators.PASS;
					}
				}
				else if (recordColour.compareTo(TestIndicators.WARNING) == 0)
				{
					passCount++;
					if (currentDateColour.compareTo(TestIndicators.FAIL) != 0)
					{
						currentDateColour = TestIndicators.WARNING;
					}
				}
			}
			while (cursor.moveToNext());
		}

		cursor.close();
		return dateSeries;
	}


	/**
	 * Searches the towns for samples. Returns the number of samples taken for
	 * that town, if any.
	 * 
	 * @return Map of {@link Town} instances to the count of samples for each
	 *         one.
	 */
	public HashMap<Town, Integer> getSamplesPerTown()
	{
		/*
		 * SELECT COUNT(s._id) "Samples Taken", w._id "Town ID", w.name
		 * "Town Name" FROM wqmarea w LEFT JOIN samplingpoint sp ON sp.wqmarea =
		 * w._id LEFT JOIN sample s ON s.samplingpoint = sp._id GROUP BY w._id
		 */

		HashMap<Town, Integer> samplesPerTown = new HashMap<Town, Integer>();

		// query the count of samples per town
		Cursor townCursor = database
			.rawQuery(
				"SELECT  COUNT(s._id), w._id, w.name "
					+ "FROM wqmarea w LEFT JOIN samplingpoint sp ON sp.wqmarea = w._id "
					+ "LEFT JOIN sample s ON s.samplingpoint = sp._id "
					+ "GROUP BY w._id", null);

		// process the results
		if (townCursor.moveToFirst())
		{
			int sampleCount;
			int townId;
			String townName;

			do
			{
				// retrieve data
				sampleCount = townCursor.getInt(0);
				townId = townCursor.getInt(1);
				townName = townCursor.getString(2);

				// save the data in the hashmap
				samplesPerTown.put(new Town(townId, townName, 0, 0),
					sampleCount);

			}
			while (townCursor.moveToNext());
		}

		return samplesPerTown;
	}

	/**
	 * Delete all data in the database
	 */
	public void clearDatabase()
	{
		for (int i = 0; i < tables.length; i++)
		{
			database.execSQL("DELETE FROM " + tables[i]);
		}
	}

}
