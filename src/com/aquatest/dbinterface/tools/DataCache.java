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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.database.Cursor;
import android.util.Log;

import com.aquatest.dbinterface.objects.MeasuredValue;
import com.aquatest.dbinterface.objects.Parameter;
import com.aquatest.dbinterface.objects.Sample;
import com.aquatest.dbinterface.objects.SamplingPoint;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;

/**
 * Class used to store data for all areas of the application.
 * 
 */
public class DataCache
{

	DatabaseAdaptor parent;

	public HashMap<Integer, SamplingPoint> samplingPoints;
	public HashMap<Integer, Vector<Integer[]>> parameterToSampleMap;

	public int areaId;
	public int areaType;
	public Calendar startDate;
	public Calendar endDate;


	/**
	 * Constructor
	 * 
	 * @param _parent
	 *            DatabaseAdaptor used to interact with database
	 * @param _areaId
	 *            id of area that we are retrieving data for
	 * @param _areaType
	 *            municipality or town
	 * @param _startDate
	 *            start date to retrieve data from
	 * @param _endDate
	 *            end date to retrieve data to
	 */
	public DataCache(DatabaseAdaptor _parent, int _areaId, int _areaType,
			Calendar _startDate, Calendar _endDate)
	{

		parent = _parent;
		startDate = _startDate;
		endDate = _endDate;
		areaId = _areaId;
		areaType = _areaType;
		startDate = _startDate;
		endDate = _endDate;

		// parameterToSampleMap = parent.parameters;
		// dateToSampleMap = new HashMap<Calendar, Vector<Integer[]>>();
		parameterToSampleMap = new HashMap<Integer, Vector<Integer[]>>();

		String startDateString = null;
		String endDateString = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		startDateString = sdf.format(startDate.getTime());
		endDateString = sdf.format(endDate.getTime());

		String samplePointsQueryString = null;
		String dataQueryString = null;

		if (_areaType == DatabaseAdaptor.TOWN)
		{

			samplePointsQueryString = "SELECT _id, pointname, x_coord, y_coord, pointarea, wqmarea FROM samplingpoint WHERE wqmarea = "
					+ areaId + " ORDER BY pointarea";

			dataQueryString = "SELECT sample._id, samplingpoint, parameter, value, measuredvalue._id, datetaken from sample "
					+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
					+ "WHERE sample.datetaken <= '"
					+ endDateString
					+ "' AND sample.datetaken >= '"
					+ startDateString
					+ "' "
					+ "AND samplingpoint IN (SELECT _id FROM samplingpoint WHERE wqmarea = "
					+ _areaId + ") ORDER BY datetaken, samplingpoint";
		}
		else
		{

			samplePointsQueryString = "SELECT samplingpoint._id, pointname, x_coord, y_coord, pointarea, wqmarea FROM samplingpoint inner join wqmarea "
					+ "on wqmarea._id = samplingpoint.wqmarea WHERE wqmarea.wqmauthority = "
					+ areaId + " ORDER BY pointarea";

			dataQueryString = "SELECT sample._id, samplingpoint, parameter, value, measuredvalue._id, datetaken from sample "
					+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
					+ "WHERE sample.datetaken <= '"
					+ endDateString
					+ "' AND sample.datetaken >= '"
					+ startDateString
					+ "' "
					+ "AND samplingpoint IN (SELECT samplingpoint._id FROM samplingpoint inner join wqmarea on wqmarea._id = samplingpoint.wqmarea WHERE wqmarea.wqmauthority = "
					+ areaId + ")";
		}

		samplingPoints = loadSamplingPointsByAreaAndDate(
				samplePointsQueryString, dataQueryString);
	}


	/**
	 * Constructor
	 * 
	 * @param _parent
	 *            DatabaseAdaptor used to interact with database
	 * @param _areaId
	 *            id of area that we are retrieving data for
	 * @param _areaType
	 *            municipality or town
	 * @param parameterId
	 *            id of parameter to filter data by
	 * @param _startDate
	 *            start date to retrieve data from
	 * @param _endDate
	 *            end date to retrieve data to
	 */
	public DataCache(DatabaseAdaptor _parent, int _areaId, int _areaType,
			int parameterId, Calendar _startDate, Calendar _endDate)
	{

		parent = _parent;
		startDate = _startDate;
		endDate = _endDate;
		areaId = _areaId;
		areaType = _areaType;
		startDate = _startDate;
		endDate = _endDate;

		// dateToSampleMap = new HashMap<Calendar, Vector<Integer[]>>();
		parameterToSampleMap = new HashMap<Integer, Vector<Integer[]>>();

		String startDateString = null;
		String endDateString = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		startDateString = sdf.format(startDate.getTime());
		endDateString = sdf.format(endDate.getTime());

		String samplePointsQueryString = null;
		String dataQueryString = null;

		if (_areaType == DatabaseAdaptor.TOWN)
		{

			samplePointsQueryString = "SELECT _id, pointname, x_coord, y_coord, pointarea, wqmarea FROM samplingpoint WHERE wqmarea = "
					+ areaId;

			dataQueryString = "SELECT sample._id, samplingpoint, parameter, value, measuredvalue._id, datetaken from sample "
					+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
					+ "WHERE sample.datetaken <= '"
					+ endDateString
					+ "' AND sample.datetaken >= '"
					+ startDateString
					+ "' "
					+ "AND parameter = "
					+ parameterId
					+ " "
					+ "AND samplingpoint IN (SELECT _id FROM samplingpoint WHERE wqmarea = "
					+ _areaId + ")" + " ORDER BY  datetaken, samplingpoint";
		}
		else
		{

			samplePointsQueryString = "SELECT samplingpoint._id, pointname, x_coord, y_coord, pointarea, wqmarea FROM samplingpoint "
					+ "inner join wqmarea on wqmarea._id = samplingpoint.wqmarea WHERE wqmarea.wqmauthority = "
					+ areaId;

			dataQueryString = "SELECT sample._id, samplingpoint, parameter, value, measuredvalue._id, datetaken from sample "
					+ "INNER JOIN measuredvalue ON sample._id = measuredvalue.sample "
					+ "WHERE sample.datetaken <= '"
					+ endDateString
					+ "' AND sample.datetaken >= '"
					+ startDateString
					+ "' "
					+ "AND parameter = "
					+ parameterId
					+ " "
					+ "AND samplingpoint IN (SELECT samplingpoint._id FROM samplingpoint inner join wqmarea on wqmarea._id = samplingpoint.wqmarea WHERE wqmarea.wqmauthority = "
					+ areaId + ")" + "ORDER BY datetaken, samplingpoint";
		}

		samplingPoints = loadSamplingPointsByAreaAndDate(
				samplePointsQueryString, dataQueryString);
	}


	/**
	 * Get sampling points and data from the database based on the constructor
	 * used to create the DataCache.
	 */
	private HashMap<Integer, SamplingPoint> loadSamplingPointsByAreaAndDate(
			String _samplePointsQueryString, String _dataQueryString)
	{

		HashMap<Integer, SamplingPoint> sP = new HashMap<Integer, SamplingPoint>();

		/*
		 * 1st query gets all the sampling points, with no sample data
		 */
		
		// query the sample points
		Cursor cursor = parent.database
				.rawQuery(_samplePointsQueryString, null);

		// save the results
		if (cursor.moveToFirst())
		{

			do
			{
				// extract returned data
				int id = cursor.getInt(0);
				String pointname = cursor.getString(1);
				
				// get x & y coords from the database
				double x_coord = cursor.getDouble(2);
				double y_coord = cursor.getDouble(3);
				
				// String townname = cursor.getString(4);
				int townId = cursor.getInt(5);

				// create a SamplingPoint object
				SamplingPoint samplingPoint = new SamplingPoint(id, pointname,
						null, 0, townId, parent.getTown(townId).name, 0,
						x_coord, y_coord);

				// save it in the HashTable
				sP.put(id, samplingPoint);

			}
			while (cursor.moveToNext());
		}

		cursor.close();
		
		/*
		 * 2nd query populates each point with sample data
		 */

		// query the data
		Cursor cursor1 = parent.database.rawQuery(_dataQueryString, null);

		if (cursor1.moveToFirst())
		{

			do
			{
				int id = cursor1.getInt(0);
				int samplingPointId = cursor1.getInt(1);
				int parameterId = cursor1.getInt(2);
				double value = cursor1.getDouble(3);
				int measuredvalueid = cursor1.getInt(4);
				String dateString = cursor1.getString(5);

				Calendar date = Calendar.getInstance();
				date.set(Integer.parseInt(dateString.substring(0, 4)),
						Integer.parseInt(dateString.substring(5, 7)) - 1,
						Integer.parseInt(dateString.substring(8, 10)));


				if (sP.containsKey(samplingPointId))
				{

					SamplingPoint samplingPoint = sP.get(samplingPointId);


					// CRASH if the parent DatabaseAdaptor does not have this
					// parameter stored

					// get the parameter information
					Parameter paramObject = null;

					if (parent.parameters.containsKey(parameterId))
						paramObject = parent.parameters.get(parameterId);


					if (!samplingPoint.samples.containsKey(id))
					{

						Sample sample = new Sample(id, samplingPoint, 0, null,
								date);

						// try..catch block prevents Force Close when
						// parameter==null
						// TODO handle null value in manner that makes sense in
						// this app
						try
						{
							MeasuredValue tempValue = new MeasuredValue(
									measuredvalueid, sample, paramObject, value);
							sample.measuredValues
									.put(paramObject.id, tempValue);
						}
						catch (IllegalArgumentException e)
						{
							Log.e("DataCache.loadSamplingPointsByAreaAndDate(String, String)",
									"Illegal argument to MeasuredValue constructor",
									e);
						}

						samplingPoint.samples.put(id, sample);
					}
					else
						samplingPoint.samples.get(id).measuredValues.put(
								paramObject.id,
								new MeasuredValue(measuredvalueid,
										samplingPoint.samples.get(id),
										paramObject, value));


					Integer[] keyPair = { id, samplingPointId };
					if (!parameterToSampleMap.containsKey(parameterId))
					{

						Vector<Integer[]> keyPairs = new Vector<Integer[]>();
						keyPairs.add(keyPair);
						parameterToSampleMap.put(parameterId, keyPairs);
					}
					else
					{
						if (!parameterToSampleMap.get(parameterId).contains(
								keyPair))
							parameterToSampleMap.get(parameterId).add(keyPair);
					}
				}

			}
			while (cursor1.moveToNext());
		}

		cursor1.close();

		return sP;
	}


	/**
	 * Get parameters ordered by name
	 * 
	 * @return vector of ordered parameters
	 */
	public Vector<Parameter> getOrderedParameters()
	{

		return parent.getOrderedParameters();
	}


	/**
	 * Get sampling points ordered by name
	 * 
	 * @return vector of ordered sampling points
	 */
	public Vector<SamplingPoint> getOrderedSamplingPoints()
	{

		Vector<SamplingPoint> tVector = new Vector<SamplingPoint>();

		Iterator<Integer> i = samplingPoints.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			SamplingPoint samplingPoint = samplingPoints.get(key);


			if (tVector.size() == 0)
			{
				tVector.add(samplingPoint);
			}
			else
			{
				Iterator<SamplingPoint> i1 = tVector.iterator();

				int position = 0;
				boolean inserted = false;

				while (i1.hasNext())
				{
					if (samplingPoint.pointName.compareTo(i1.next().pointName) < 0)
					{


						inserted = true;
						break;

					}
					position++;
				}

				if (!inserted)
				{
					tVector.add(samplingPoint);
				}
				else
				{
					tVector.insertElementAt(samplingPoint, position);
				}
			}
		}

		return tVector;
	}


	/**
	 * Get parameter data in a format that can be displayed in a DataList
	 * 
	 * @return parameter data
	 */
	public Vector<Vector<DataIndicator>> getParametersDataIndicatorsGroupedByDate()
	{
		Vector<Vector<DataIndicator>> dIs = new Vector<Vector<DataIndicator>>();

		Vector<SamplingPoint> orderedSPs = getOrderedSamplingPoints();

		Iterator<SamplingPoint> i = orderedSPs.iterator();

		while (i.hasNext())
		{

			SamplingPoint sP = i.next();

			Vector<DataIndicator> dI = sP
					.getParametersDataIndicatorsGroupedByDate(getOrderedParameters1());

			if (dI != null)
				dIs.add(dI);
		}

		return dIs;
	}


	/**
	 * Get parameter data in a format that can be displayed in a DataList
	 * 
	 * @param showPointNames
	 *            whether to hide or show sampling point names
	 * @return parameter data
	 */
	public Vector<Vector<DataIndicator>> getParametersDataIndicators(
			boolean showPointNames)
	{

		Vector<Vector<DataIndicator>> dIs = new Vector<Vector<DataIndicator>>();

		Vector<SamplingPoint> orderedSPs = getOrderedSamplingPoints();

		Iterator<SamplingPoint> i = orderedSPs.iterator();

		while (i.hasNext())
		{

			SamplingPoint sP = i.next();

			dIs.addAll(sP.getParametersDataIndicators(getOrderedParameters1(),
					showPointNames));
		}

		return dIs;
	}


	/**
	 * Get column headers for displaying parameter data.
	 * 
	 * @return column headers
	 */
	public Vector<DataIndicator> getParametersColumnHeaders()
	{

		Vector<DataIndicator> dIs = new Vector<DataIndicator>();

		dIs.add(new DataIndicator(-1, TestIndicators.NONE, ""));

		Vector<Parameter> orderedPs = getOrderedParameters1();

		Iterator<Parameter> i = orderedPs.iterator();

		while (i.hasNext())
		{

			Parameter p = i.next();

			dIs.add(new DataIndicator(-1, TestIndicators.NONE, p.testNameShort));
		}

		return dIs;
	}


	/**
	 * Get measured values data in a format that can be displayed in a DataList.
	 * 
	 * @param hiddenColumn
	 *            index of a column to hide
	 * @return data to be displayed
	 */
	public Vector<Vector<DataIndicator>> getMeasuredValueDataIndicators(
			int hiddenColumn)
	{

		Vector<Vector<DataIndicator>> dIs = new Vector<Vector<DataIndicator>>();
		Vector<SamplingPoint> orderedSPs = getOrderedSamplingPoints();

		Iterator<SamplingPoint> i = orderedSPs.iterator();

		while (i.hasNext())
		{

			SamplingPoint sP = i.next();

			dIs.addAll(sP.getMeasureValueDataIndicators(hiddenColumn));
		}

		return dIs;
	}


	/**
	 * Get column headers to be used when displaying measured values.
	 * 
	 * @param hiddenColumn
	 *            index of a column to hide
	 * @return vector of the column headers
	 */
	public Vector<DataIndicator> getMeasuredValueColumnHeaders(int hiddenColumn)
	{

		DataIndicator dI1 = new DataIndicator(-1, TestIndicators.NONE, "");
		DataIndicator dI2 = new DataIndicator(-1, TestIndicators.NONE,
				"Sampling Point");
		DataIndicator dI3 = new DataIndicator(-1, TestIndicators.NONE, "Town");
		DataIndicator dI4 = new DataIndicator(-1, TestIndicators.NONE,
				"Parameter");
		DataIndicator dI5 = new DataIndicator(-1, TestIndicators.NONE, "Value");

		Vector<DataIndicator> dIs = new Vector<DataIndicator>();
		dIs.add(dI1);
		dIs.add(dI2);
		dIs.add(dI3);
		dIs.add(dI4);
		dIs.add(dI5);

		if (hiddenColumn >= 0)
			dIs.remove(hiddenColumn);

		return dIs;
	}


	/**
	 * Get list of parameters within the current DataCache object ordered by
	 * name.
	 * 
	 * @return vector of ordered parameters
	 */
	public Vector<Parameter> getOrderedParameters1()
	{

		Vector<Parameter> mVector = new Vector<Parameter>();

		Iterator<Integer> i = parameterToSampleMap.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			Parameter parameter = parent.parameters.get(key);


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
	 * Get compliance for a specific sampling point.
	 * 
	 * @param dataId
	 *            id of the sampling point
	 * @return compliance
	 */
	public DataIndicator getSamplingPointCompliance(int dataId)
	{

		DecimalFormat dF = new DecimalFormat("#.##%");

		int[] counts = samplingPoints.get(dataId).getCompliance();
		double passCount = counts[0];
		double totalCount = counts[1];

		DataIndicator dI = new DataIndicator(passCount / totalCount,
				samplingPoints.get(dataId).getColour(), dF.format(passCount
						/ totalCount));

		return dI;
	}


	/**
	 * Get overall compliance for all the data specified within the DataCache
	 * object.
	 * 
	 * @return compliance
	 */
	public DataIndicator getCompliance()
	{
		double totalCount = 0;
		double passCount = 0;
		String colour = TestIndicators.NONE;

		Iterator<Integer> i = samplingPoints.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			SamplingPoint samplingPoint = samplingPoints.get(key);

			int[] compliance = samplingPoint.getCompliance();

			// Log.v("Steve", "SP: " + compliance[0] + "," + compliance[1]);


			totalCount += compliance[1];
			passCount += compliance[0];

			String samplingPointColour = samplingPoint.getColour();

			if ((samplingPointColour.compareTo(TestIndicators.PASS) == 0)
					&& (colour.compareTo(TestIndicators.NONE) == 0))
				colour = TestIndicators.PASS;
			else if (samplingPointColour.compareTo(TestIndicators.FAIL) == 0)
				colour = TestIndicators.FAIL;
			else if ((samplingPointColour.compareTo(TestIndicators.YELLOW) == 0)
					&& (colour.compareTo(TestIndicators.FAIL) != 0))
				colour = TestIndicators.YELLOW;
		}

		// Log.v("Steve", "SP: " + passCount + "," + totalCount);

		DecimalFormat dF = new DecimalFormat("#.##%");
		DataIndicator dI = new DataIndicator(passCount / totalCount, colour,
				dF.format(passCount / totalCount));

		return dI;
	}


	/**
	 * Check for any sampling points that don't have coordinates set.
	 * 
	 * @return false if sampling point found without coordinates set, else true.
	 */
	public boolean checkForCoordinates()
	{
		Iterator<Integer> i = samplingPoints.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			SamplingPoint samplingPoint = samplingPoints.get(key);

			if ((samplingPoint.latitude == 0) && (samplingPoint.longitude == 0)) { return false; }
		}

		return true;
	}
}
