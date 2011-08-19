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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aquatest.dbinterface.objects.Town;

/**
 * Show list of towns in a municipality. </p>
 * 
 * Internally this class is very similar to {@link Municipalities}.
 * 
 * @see Municipalities
 */
public class Towns extends Activity implements OnItemClickListener
{
	AquaTestApp application;

	/** Ordered vector of {@link Town} objects in the current municipality */
	Vector<Town> towns;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// load the xml layout file
		setContentView(R.layout.towns);

		// create heading with the town name
		application = (AquaTestApp) this.getApplication();

		// this line is needed by the getTownNames() method
		towns = application.getCurrentMunicipality().getOrderedTowns();

		// get the town names from the towns data
		List<Map<String, Object>> townNames = this.getTownNames();

		// determine which towns to highlight
		int[] colorsForTowns = this.getColorsForTowns(towns, townNames);

		// get the data to put into the list
		String[] colFrom = { TOWN_NAME };
		int[] colTo = { R.id.TownItemText };
		ColoredRowsAdapter sa = new ColoredRowsAdapter(this, townNames,
			colorsForTowns, R.layout.town_list_item, colFrom, colTo);

		ListView lv = (ListView) findViewById(R.id.TownList);
		lv.setAdapter(sa);
		lv.setOnItemClickListener(this);
	}

	private static final String TOWN_NAME = "TOWN_NAME";

	/**
	 * get list of town names from the global towns Vector.
	 * 
	 * @return list of town names that can be used for screen output
	 */
	private List<Map<String, Object>> getTownNames()
	{
		List<Map<String, Object>> oRet = new ArrayList<Map<String, Object>>(
			towns.size());
		Map<String, Object> m;
		for (int i = 0; i < towns.size(); i++)
		{
			Town town = towns.elementAt(i);
			m = new HashMap<String, Object>();
			m.put(TOWN_NAME, town.name);
			oRet.add(m);
		}

		return oRet;
	}

	/**
	 * Get an array of colours to pass to the ColoredRowsAdapter to colour the
	 * towns. Towns with data will retain the black background colour. Towns
	 * with no data will be highlighted in a grey colour.
	 * 
	 * @param towns
	 *        Data to use to determine the colour array. This is assumed to be
	 *        in the same order as the town names List.
	 * @return Array of colours that can be used to highlight the towns.
	 */
	private int[] getColorsForTowns(Vector<Town> towns, List<Map<String, Object>> townNames)
	{
		// each town has a row with a colour
		int numberOfRows = towns.size();
		int[] colors = new int[numberOfRows];

		// colour of each row depends on the number of samples in the town
		HashMap<Town, Integer> samplesPerTown = application.dbAdapter
			.getSamplesPerTown();

		// loop over all towns, and assign colour based on samples
		Town curTown;
		Integer numSamples;
		
		Map<String, Object> curTownNameMap;
		String curName;
		
		for (int i = 0; i < colors.length; i++)
		{
			curTown = towns.elementAt(i);
			numSamples = samplesPerTown.get(curTown);
			
			// highlight row if no samples for this town
			if ((numSamples == null) || (numSamples.intValue() <= 0))
			{
				colors[i] = Color.DKGRAY;
			}
			else
			{
				colors[i] = Color.BLACK; 
			}
			

			// HACK to update the row text maybe?
			curTownNameMap = (HashMap<String, Object>) townNames.get(i);
			curName = (String) curTownNameMap.get(TOWN_NAME);
			
			curName = curName + " (" + numSamples + ")";
			curTownNameMap.put(TOWN_NAME, curName);
			
			townNames.set(i, curTownNameMap); 

		}

		return colors;
	}

	/**
	 * on town selected
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		application.setCurrentTown(towns.elementAt(arg2));
		Intent intent = new Intent(this, TownOptions.class);
		startActivity(intent);
	}
}
