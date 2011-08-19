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

import com.aquatest.dbinterface.objects.Municipality;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;


/**
 * Shows list of municipality options:
 * <ul>
 * <li>Browse Towns</li>
 * <li>Calendar</li>
 * <li>Map View</li>
 * </ul>
 */
public class MunicipalityOptions extends Activity implements
		OnItemClickListener
{

	private static final String MENU_ITEM_ID = "MENU_ITEM_ID";

	AquaTestApp application;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// create heading with municipality name
		application = (AquaTestApp) this.getApplication();
		this.setTitle(application.getCurrentMunicipality().name);

		/*
		 * The following block of code references the municipality_list_item.xml
		 * file. This file is used to define the text format of the municipality
		 * list, but the same format is used for this options menu. It only
		 * defines the text format, and should not be confused with any
		 * municipality-related data.
		 */

		// get the data to put into the list (use same format as
		// Municipalities.java)
		String[] colFrom = { MENU_ITEM_ID };
		int[] colTo = { R.id.MunicipalityItemText };
		SimpleAdapter sa = new SimpleAdapter(this, getMenuItems(),
				R.layout.municipality_list_item, colFrom, colTo);

		// create the options menu from municipalityoptions.xml
		setContentView(R.layout.municipalityoptions);
		ListView lv = (ListView) findViewById(R.id.MunicipalityOptionsList);
		lv.setAdapter(sa);
		lv.setOnItemClickListener(this);
	}

	/**
	 * gets list of menu items
	 * 
	 * @return menu list
	 */
	private List<Map<String, Object>> getMenuItems()
	{
		List<Map<String, Object>> oRet = new ArrayList<Map<String, Object>>(3);

		// create 3 menu items
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put(MENU_ITEM_ID, "Browse Towns");
		oRet.add(m);
		m = new HashMap<String, Object>();
		m.put(MENU_ITEM_ID, "Calendar");
		oRet.add(m);
		m = new HashMap<String, Object>();
		m.put(MENU_ITEM_ID, "Map View");
		oRet.add(m);

		return oRet;
	}

	/**
	 * handle which option was selected
	 * <ul>
	 * <li>Towns: goto List of towns</li>
	 * <li>Calendar: show municipality data in calendar view</li> 
	 * <li>Map: show municipality data in map view</li>
	 * </ul>
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		application.setCurrentParameter(null);
		application.setCurrentTown(null);
		Intent intent;
		switch (arg2)
		{
			case 0: // Towns
				intent = new Intent(this, Towns.class);
				startActivity(intent);
				break;
			case 1: // Calendar
				intent = new Intent(this, AquaCalendar.class);
				startActivity(intent);
				break;
			case 2: // Map
				intent = new Intent(this, AquaMap.class);
				startActivity(intent);
				break;
		}
	}
}
