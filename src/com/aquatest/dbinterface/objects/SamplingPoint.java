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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;


/**
 * Models a sampling point as specified in the database
 * 
 */
public class SamplingPoint
{

	public int id;
	public String pointName;
	public String pointCode;
	public int waterUseTypeId;
	public int townId;
	public String townName;
	public int municipalityId;
	public String municipalityName;
	
	/**
	 * Longitude in degrees. East is positive, West is negative (a value of 0
	 * means the point is undefined)
	 */
	public double longitude;
	/**
	 * Latitude in degrees. North is positive, South is negative (a value of 0
	 * means the point is undefined)
	 */
	public double latitude;

	public HashMap<Integer, Sample> samples;

	/**
	 * Constructor
	 * 
	 * @param _id
	 * @param _pointName
	 * @param _pointCode
	 * @param _waterUseTypeId
	 * @param _townId
	 * @param _townName
	 * @param _municipalityId
	 * @param _longitude
	 * @param _latitude
	 */
	public SamplingPoint(int _id, String _pointName, String _pointCode,
			int _waterUseTypeId, int _townId, String _townName,
			int _municipalityId, double _longitude, double _latitude)
	{
		id = _id;
		pointName = _pointName;
		pointCode = _pointCode;
		waterUseTypeId = _waterUseTypeId;
		townId = _townId;
		townName = _townName;
		municipalityId = _municipalityId;

		longitude = _longitude;
		latitude = _latitude;

		samples = new HashMap<Integer, Sample>();
	}


	/**
	 * Get the colour of this sampling point according to whether any of its
	 * measured values are failures, warnings, or all passed
	 * 
	 * @return the colour as a String
	 */
	public String getColour()
	{

		String colour = TestIndicators.NONE;

		Iterator<Integer> i = samples.keySet().iterator();

		while (i.hasNext())
		{

			Sample s = samples.get(i.next());
			String sampleColour = s.getColour();

			if ((sampleColour.compareTo(TestIndicators.PASS) == 0)
					&& (colour.compareTo(TestIndicators.NONE) == 0))
				colour = TestIndicators.PASS;

			else if (sampleColour.compareTo(TestIndicators.FAIL) == 0)
				return TestIndicators.FAIL;
			else if (sampleColour.compareTo(TestIndicators.YELLOW) == 0)
				colour = TestIndicators.YELLOW;
		}
		return colour;
	}

	/**
	 * Get the compliance of this sampling point as an array of two values. The
	 * first value is the number of measured values taken at the sampling that
	 * passed. The second value is the total number of measured values at the
	 * sampling point.
	 * 
	 * @return the compliance as an array of two values - measured values passed
	 *         and total measured values.
	 */
	public int[] getCompliance()
	{

		int totalCount = 0;
		int passCount = 0;

		Iterator<Integer> i = samples.keySet().iterator();

		while (i.hasNext())
		{

			Sample s = samples.get(i.next());
			double sampleCompliance = s.getCompliance();

			totalCount = totalCount + s.measuredValues.size();
			passCount = passCount
					+ (int) Math.round(sampleCompliance
							* s.measuredValues.size());

		}

		int[] compliance = { passCount, totalCount };
		return compliance;
	}


	/**
	 * Gets a vector of samples at this sampling point ordered by date taken.
	 * 
	 * @return a vector of the ordered samples
	 */
	public Vector<Sample> getSamplesOrderedByDate()
	{
		Vector<Sample> sVector = new Vector<Sample>();

		Iterator<Integer> i = samples.keySet().iterator();

		while (i.hasNext())
		{

			int key = (Integer) i.next();

			Sample s = samples.get(key);


			if (sVector.size() == 0)
			{
				sVector.add(s);
			}
			else
			{
				Iterator<Sample> i1 = sVector.iterator();

				int position = 0;
				boolean inserted = false;

				while (i1.hasNext())
				{
					if (s.date.compareTo(i1.next().date) < 0)
					{


						inserted = true;
						break;

					}
					position++;
				}

				if (!inserted)
				{
					sVector.add(s);
				}
				else
				{
					sVector.insertElementAt(s, position);
				}
			}
		}

		return sVector;
	}


	/**
	 * Gets a vector of all the measured values taken at this sampling point in
	 * a format that can be understood by and displayed in a DataList.
	 * 
	 * @param hiddenColumn
	 *            index of a particular data item within each measured value
	 *            that shouldn't be added to the vector to be displayed
	 * @return a vector for all measured values' data
	 * 
	 */
	public Vector<Vector<DataIndicator>> getMeasureValueDataIndicators(
			int hiddenColumn)
	{

		Vector<Vector<DataIndicator>> dIs = new Vector<Vector<DataIndicator>>();

		Vector<Sample> orderedSamples = getSamplesOrderedByDate();

		Iterator<Sample> i = orderedSamples.iterator();

		while (i.hasNext())
		{

			Sample s = i.next();

			dIs.addAll(s.getMeasuredValueDataIndicators(hiddenColumn));

		}

		return dIs;
	}

	/**
	 * Gets a vector of all samples taken at this point that can be understood
	 * by and displayed in a DataList. Data returned is filtered according to a
	 * specified list of parameters.
	 * 
	 * @param _parameters
	 *            parameter list to filter the returned data by
	 * @param showPointName
	 *            whether the sampling point's name should be returned as a
	 *            value to be displayed
	 * @return vector of samples' data
	 */
	public Vector<Vector<DataIndicator>> getParametersDataIndicators(
			Vector<Parameter> _parameters, boolean showPointName)
	{

		Vector<Vector<DataIndicator>> dIs = new Vector<Vector<DataIndicator>>();

		Vector<Sample> oS = getSamplesOrderedByDate();

		Iterator<Sample> i = oS.iterator();

		while (i.hasNext())
		{

			Sample s = i.next();

			dIs.add(s.getParametersDataIndicators(_parameters, showPointName));

		}

		return dIs;
	}


	/**
	 * gets a vector of this sampling point's data that can be displayed in a
	 * DataList. Data is filtered by and displayed according to a specified list
	 * of parameters.
	 * 
	 * @param _parameters
	 *            parameter list to filter the returned data by
	 * @return vector of sampling point's data
	 */
	public Vector<DataIndicator> getParametersDataIndicatorsGroupedByDate(
			Vector<Parameter> _parameters)
	{
		if (samples.size() == 0)
			return null;

		Vector<DataIndicator> dIs = new Vector<DataIndicator>();

		Iterator<Parameter> i = _parameters.iterator();

		dIs.add(new DataIndicator(-1, TestIndicators.NONE, pointName + " ("
				+ townName + ")"));

		while (i.hasNext())
		{

			double c = 0;
			double totalCount = 0;
			double passCount = 0;
			String colour = TestIndicators.NONE;

			Parameter p = i.next();

			Iterator<Sample> iS = samples.values().iterator();

			while (iS.hasNext())
			{

				Sample s = iS.next();

				if (s.measuredValues.containsKey(p.id))
				{

					MeasuredValue mV = s.measuredValues.get(p.id);

					totalCount++;

					if (mV.range.colour.compareTo(TestIndicators.PASS) == 0)
					{
						passCount++;
						if (colour == TestIndicators.NONE)
							colour = TestIndicators.PASS;

					}
					else if (mV.range.colour.compareTo(TestIndicators.WARNING) == 0)
					{
						passCount++;
						if (colour != TestIndicators.FAIL)
							colour = TestIndicators.WARNING;
					}
					else if (mV.range.colour.compareTo(TestIndicators.FAIL) == 0)
					{
						colour = TestIndicators.FAIL;
					}
				}
				// else {
				// dIs.add(new DataIndicator(-1, TestIndicators.NONE, ""));
				// }
			}

			DecimalFormat dF = new DecimalFormat("#.##%");

			if (totalCount > 0)
				c = passCount / totalCount;

			dIs.add(new DataIndicator(c, colour, dF.format(c)));
		}

		return dIs;
	}

	public String toString()
	{
		return "(" + latitude + "," + longitude + ")";
	}
}